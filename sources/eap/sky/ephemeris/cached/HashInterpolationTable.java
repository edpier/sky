// Copyright 2014 Edward Alan Pier
//
// This file is part of eap.sky
//
// eap.sky is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// eap.sky is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with eap.sky.  If not, see <http://www.gnu.org/licenses/>.

package eap.sky.ephemeris.cached;

import eap.sky.ephemeris.*;
import eap.sky.time.*;
import eap.sky.util.*;

import java.util.*;

/***************************************************************************
*
***************************************************************************/
public class HashInterpolationTable extends InterpolationTable {

SortedMap<PreciseDate, InterpolationPoint> points;
SortedMap<PreciseDate, InterpolationInterval>  intervals;

double accuracy2;
double timescale;

/***************************************************************************
*
***************************************************************************/
public HashInterpolationTable(Ephemeris ephemeris, int body, VectorType type,
                              InterpolationMethod method, double accuracy) {

    super(ephemeris, body, type, method);

    points    = new TreeMap<PreciseDate, InterpolationPoint>();
    intervals = new TreeMap<PreciseDate, InterpolationInterval>();

    accuracy = accuracy*accuracy;
    timescale = -1;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
private double getTimescale() { return timescale; }

/***************************************************************************
*
***************************************************************************/
private void setTimescale(double timescale) { this.timescale = timescale; }

/***************************************************************************
*
***************************************************************************/
private void add(InterpolationPoint point) {

//System.out.println("adding point at "+point.getTime());

    points.put(point.getTime(), point);

} // end of add interpolation point method

/***************************************************************************
*
***************************************************************************/
private void add(InterpolationInterval interval) {

   // System.out.println("adding "+interval);

    intervals.put(interval.getStartTime(), interval);

} // end of add method

/***************************************************************************
*
***************************************************************************/
private InterpolationInterval getInterpolationInterval(PreciseDate time) {

    SortedMap<PreciseDate, InterpolationInterval> head = intervals.headMap(time);
    if(head.size()==0) return null;
    InterpolationInterval interval =  head.get(head.lastKey());
   // System.out.println("considering "+interval);
    if(interval.contains(time)) return interval;
    else                       return null;

} // end of getInterpolationInterval method

/***************************************************************************
*
***************************************************************************/
private InterpolationPoint getClosestInterpolationPoint(PreciseDate time) {

    if(points.size() == 0) return null;

    SortedMap<PreciseDate, InterpolationPoint> head = points.headMap(time);
    SortedMap<PreciseDate, InterpolationPoint> tail = points.tailMap(time);

    PreciseDate  time1 = null;
    if(head.size()>0)  time1 = head.lastKey();

    PreciseDate time2 = null;
    if(tail.size()>0) time2 = tail.firstKey();

    if(time1 == null) return tail.get(time2);
    if(time2 == null) return head.get(time1);

    double dt1 = Math.abs(time.secondsAfter(time1));
    double dt2 = Math.abs(time.secondsAfter(time2));

   // System.out.println("dt1="+dt1+" dt2="+dt2);

    if(dt1<dt2) return head.get(time1);
    else        return tail.get(time2);

} // end of getClosestInterpolationPoint method

/***************************************************************************
*
***************************************************************************/
public InterpolationPoint compute(PreciseDate time) {

    InterpolationPoint p = super.compute(time);
    add(p);

    return p;

} // end of compute method

/***************************************************************************
*
***************************************************************************/
public ThreeVector interpolate(PreciseDate tdb) {

    /***************************************************
    * see if we already have an interpolation interval *
    ***************************************************/
    InterpolationInterval interval = getInterpolationInterval(tdb);
    if(interval != null) {
        return interval.interpolate(tdb);
    }

    /****************************************
    * see if we already have another point
    * with which we can form an interval
    ****************************************/
    InterpolationPoint point = getClosestInterpolationPoint(tdb);
    if(point == null) {
        /************************************************
        * the table is empty, so just calculate a point *
        ************************************************/
        return compute(tdb).getThreeVector();
    }

    /**********************************************************************
    * there is some chance we are asking for exactly the same time again *
    *********************************************************************/
    if(point.getTime().equals(tdb)) {
        return point.getThreeVector();
    }

    /**********************************************************************
    * if we get here we do not yet have an interval,
    * but there is another point with which we may be able to interpolate
    **********************************************************************/
    double timescale = getTimescale();
    double delta_t = tdb.secondsAfter(point.getTime());

    if(timescale != -1 && Math.abs(delta_t) < timescale) {
        /***********************************************
        * We are closer in than the last interval size,
        * so use the last interval size
        ***********************************************/
        if(delta_t<0.0) delta_t = -timescale;
        else            delta_t =  timescale;

    } else {
        /*****************************
        * lets try a larger interval *
        *****************************/
        delta_t *= 2.0;

    } // end if the point is too far away

    /********************************************************
    * refine the interval size until it gives
    * us the desired accuracy
    ********************************************************/
    boolean right = delta_t>0.0;

    /***********************************************************
    * compute the other endpoint of the interpolation interval *
    ***********************************************************/
    PreciseDate new_time = point.getTime().copy();
    new_time.increment(delta_t);
    InterpolationPoint new_point = compute(new_time);

    if(right) interval = createInterpolationInterval(point, new_point);
    else      interval = createInterpolationInterval(new_point, point);

    while(true) {
        /**************************************************************
        * if the interval no longer contains our target point,
        * we just give up and compute the target point independently
        **************************************************************/
        if(!interval.contains(tdb)) {
            InterpolationPoint p = compute(tdb);
            add(p);
            return p.getThreeVector();
        }

        /****************************************
        * compute a point in the middle so we
        * can test the accuracy of the interval
        ****************************************/
        PreciseDate middle_time = interval.getMiddleTime();
        InterpolationPoint mid_point = compute(middle_time);

        ThreeVector interpolated = interval.interpolate(middle_time);
        double error2 = mid_point.getThreeVector().distanceSquared(interpolated);

        if(error2 < accuracy2) {
            /****************************************************
            * the interval passes the accuracy test, so keep it *
            ****************************************************/
            add(interval);

            /****************************************
            * if the accuracy was too good we can
            * try a longer interval next time
            ****************************************/
            if(error2<accuracy2*0.1) setTimescale(interval.getDuration()*2.0);
            else                     setTimescale(interval.getDuration());

            return interval.interpolate(tdb);

        } // end if the interval was accurate enough

        /*********************************************************
        * if we get here, the interval was not accurate enough,
        * so we need to shrink it by half
        *********************************************************/
        if(right) interval = createInterpolationInterval(point, mid_point);
        else      interval = createInterpolationInterval(mid_point, point);

    } // end of loop over refinements

} // end of barycentricPosition method


} // end of InterpolationTable class

