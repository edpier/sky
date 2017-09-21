// Copyright 2013 Edward Alan Pier
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

package eap.sky.time.gti;

import eap.sky.time.*;

/***********************************************************************
* An interval of time between two PreciseDates.
* GTI stands for "Good Time Interval". The term comes from the use of
* such intervals in time filtering of high energy astrophysics
* photon counting data.
***********************************************************************/
public class GTI implements Comparable {

PreciseDate start;
PreciseDate stop;

/***********************************************************************
* Creates a new Good Time Interval. The times are copied, so that they
* can later be modified without affecting the created object
***********************************************************************/
public GTI(PreciseDate start, PreciseDate stop)
                                           throws IllegalArgumentException {

    this.start = (PreciseDate)start.clone();
    this.stop = (PreciseDate)stop.clone();

    if(start.compareTo(stop) >= 0) {
        throw new IllegalArgumentException("start time is not before stop");
    }

} // end of constructor

/***********************************************************************
* create a new GTI which spans the two arguments.
***********************************************************************/
public GTI(GTI from, GTI to) {

    this.start = from.start;
    this.stop  = to.stop;

    if(start.compareTo(stop) >= 0) {
        throw new IllegalArgumentException("start time is not before stop");
    }

}  // end of constructor


/***********************************************************************
* Returns a copy of the start time of this GTI
***********************************************************************/
public PreciseDate getStart() { return (PreciseDate)start.clone(); }

/***********************************************************************
* Returns a copy of the stop time of this GTI
***********************************************************************/
public PreciseDate getStop() { return (PreciseDate)stop.clone(); }

/***********************************************************************
*
***********************************************************************/
public double duration() {

    return stop.secondsAfter(start);

}

/***********************************************************************
*
***********************************************************************/
public boolean contains(PreciseDate time) {

    return time.compareTo(start) >=0 &&
           time.compareTo(stop ) <=0;
}

/***********************************************************************
* returns true if two GTIs just touch each other
***********************************************************************/
public boolean abuts(GTI gti) {

    return start.equals(gti.stop ) ||
            stop.equals(gti.start);

}

/***********************************************************************
*
***********************************************************************/
public boolean overlaps(GTI gti) {

    if(this.contains( gti.start)) return true;
    if(this.contains( gti.stop )) return true;
    if( gti.contains(this.start)) return true;
    if( gti.contains(this.stop )) return true;

    return false;

} // end of overlaps method

/***********************************************************************
*
***********************************************************************/
public int compareTo(Object o) {

    return compareStartTo((GTI)o);

} // end of compareTo method

/***********************************************************************
*
***********************************************************************/
public int compareStartTo(GTI gti) {

    return start.compareTo(gti.start);

}

/***********************************************************************
*
***********************************************************************/
public int compareStopTo(GTI gti) {

    return stop.compareTo(gti.stop);

}

/***********************************************************************
*
***********************************************************************/
public boolean equals(Object o) {

    GTI gti = (GTI)o;

    return start.equals(gti.start) &&
            stop.equals(gti.stop );

} // end of equals method

/***********************************************************************
*
***********************************************************************/
public String toString() {

    return "GTI: "+start.getMilliseconds()+" - "+stop.getMilliseconds();

} // end of toString method

} // end of GTI class
