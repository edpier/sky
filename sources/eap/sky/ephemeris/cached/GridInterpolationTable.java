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
public class GridInterpolationTable extends InterpolationTable {

double width;
int max_size;

PreciseDate time0;

List<InterpolationInterval> intervals;


/***************************************************************************
*
***************************************************************************/
public GridInterpolationTable(Ephemeris ephemeris, int body, VectorType type,
                              InterpolationMethod method, double width,
                              int max_size) {

    super(ephemeris, body, type, method);
    this.width = width;
    this.max_size = max_size;

    if(max_size<=0) throw new IllegalArgumentException("max_size="+max_size);

    intervals = new ArrayList<InterpolationInterval>(max_size);

} // end of constructor

/***************************************************************************
*
***************************************************************************/
private PreciseDate getStartTime() {

    return time0;

} // end of getStartTime method

/***************************************************************************
* Finds the first interpolation point in a given interval.
* The interval need not actually exist in the table. If it does not,
* this method will create a new interpolation point
***************************************************************************/
private InterpolationPoint getInterpolationPoint(int index) {

    int size = intervals.size();
    if(index>=0 && index<size) {
        InterpolationInterval interval = intervals.get(index);
        if(interval != null) return interval.getInterpolationPoint1();

    } else if(index == size) {
        /**************************************************
        * we are one past the end of the array, so
        * we can take the end point of the last interval
        **************************************************/
        InterpolationInterval interval = intervals.get(size-1);
        if(interval != null) return interval.getInterpolationPoint2();

    }

    /***********************************************
    * if we get here, we have to compute the point *
    ***********************************************/
    PreciseDate time = getStartTime().copy();
    time.increment(width*index);

    return compute(time);

} // end of getInterpolationPoint method

/***************************************************************************
*
***************************************************************************/
public InterpolationInterval createInterpolationInterval(int index) {

    InterpolationPoint p1 = getInterpolationPoint(index  );
    InterpolationPoint p2 = getInterpolationPoint(index+1);

    InterpolationInterval interval = createInterpolationInterval(p1, p2);
    intervals.set(index, interval);

    return interval;

} // end of createInterpolationInterval method


/***************************************************************************
*
***************************************************************************/
public ThreeVector makeFirstInterval(PreciseDate time) {

    /**************************
    * compute the first point *
    **************************/
    InterpolationPoint p1 = compute(time);

    /*************************************************
    * find the second time and compute a point there *
    *************************************************/
    PreciseDate time2 = time.copy();
    time2.increment(width);
    InterpolationPoint p2 = compute(time2);

    /********************
    * save the interval *
    ********************/
    intervals.add(createInterpolationInterval(p1, p2));

    /*********************************************
    * now to make our bookkeeping simpler, pad
    * the array out to its max size with nulls
    *********************************************/
    for(int i=1; i<max_size; ++i) {
        intervals.add(null);
    }

    /**********************
    * save the start time *
    **********************/
    time0 = time.copy();

    return p1.getThreeVector();

} // end of makeFirstInterval method

/***************************************************************************
*
***************************************************************************/
private void shift(int places) {

    if(places == 0) return;

    int n = Math.abs(places);

    if(n>=max_size) {
        /***************************************
        * we are shifting everything away,
        * so just set all the elements to null
        ***************************************/
        for(int i=0; i<max_size; ++i) {
            intervals.set(i, null);
        }

    } else if(places>0) {
        /**********************************************
        * we are shifting to the right, so pad with
        * nulls on the left
        **********************************************/
        intervals.subList(max_size-n, max_size).clear();
        for(int i=0; i<n; ++i) {
            intervals.add(0, null);
        }

    } else {
        /***********************************************
        * we are shifting to the left so remove
        * elements from the left and pad on the right
        **********************************************/
        intervals.subList(0, n).clear();
        for(int i=0; i<n; ++i) {
            intervals.add(null);
        }
    }

    time0.increment(-width*places);

} // end of shift method

/***************************************************************************
*
***************************************************************************/
public ThreeVector interpolate(PreciseDate time) {

    PreciseDate time0 = getStartTime();
    if(time0 == null) return makeFirstInterval(time);

    /***************************************************************
    * find the index of the interval containing the requested time *
    ***************************************************************/
    int index = (int)Math.floor(time.secondsAfter(time0)/width);

    if(index<0) {
        /*************************************************
        * we are off the left end of the array, so
        * shift to the right so we will be at the first
        * position
        *************************************************/
        shift(-index);
        index = 0;

    } else if(index>=max_size) {
        /*************************************************
        * we are off the right end of the array, so
        * shift to the left so we will be at the left
        * position
        *************************************************/
        shift(max_size-index+1);
        index=max_size-1;
    }

    /******************************************************************
    * now get the interval or create it if we need to and interpolate *
    ******************************************************************/
    //System.out.println("index="+index);
    InterpolationInterval interval = intervals.get(index);
    if(interval == null) interval = createInterpolationInterval(index);
    return interval.interpolate(time);


} // end of interpolate method

} // end of InterpolationTable class