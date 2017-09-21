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
public abstract class InterpolationInterval {

InterpolationPoint p1;
InterpolationPoint p2;

PreciseDate time1;
double duration;

PreciseDate middle;

/***************************************************************************
*
***************************************************************************/
public InterpolationInterval(InterpolationPoint p1, InterpolationPoint p2) {

    this.p1 = p1;
    this.p2 = p2;

    time1    = p1.getTime();
    duration = p2.getTime().secondsAfter(time1);

    if(duration<=0.0) throw new IllegalArgumentException("duration="+duration);

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public InterpolationPoint getInterpolationPoint1() { return p1; }

/***************************************************************************
*
***************************************************************************/
public InterpolationPoint getInterpolationPoint2() { return p2; }


/***************************************************************************
*
***************************************************************************/
public double getDuration() { return duration; }

/***************************************************************************
*
***************************************************************************/
public PreciseDate getStartTime() { return time1; }

/***************************************************************************
*
***************************************************************************/
public PreciseDate getEndTime() { return p2.getTime(); }


/***************************************************************************
*
***************************************************************************/
public PreciseDate getMiddleTime() {

    if(middle == null) {
        middle = time1.copy();
        middle.increment(0.5*duration);
    }

    return middle;

} // end of getMiddleTime method

/***************************************************************************
*
***************************************************************************/
public boolean contains(PreciseDate time) {

    double delta = time.secondsAfter(time1);
    return delta>=0.0 && delta <=duration;

} // end of contains method

/***************************************************************************
*
***************************************************************************/
public abstract ThreeVector interpolate(PreciseDate time);


/***************************************************************************
*
***************************************************************************/
public String toString() {

    return "Interval: "+time1+" + "+duration+" sec";

} // end of toString method

} // end of InterpolationInterval class