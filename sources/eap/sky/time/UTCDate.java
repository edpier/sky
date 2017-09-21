// Copyright 2012 Edward Alan Pier
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

package eap.sky.time;

import java.util.*;

/****************************************************************************
* Represents an instant specified in UTC (see {@link UTCSystem}).
* <p>
* This class handles leap seconds in the following way.
* For a negative leap
* second, the millisecond count is incremented by 1000 to skip over the last
* second of the month.
* At the beginning of a positive leap second, the millisecond count is
* decremented by 1000,
* so that the last second of the month is counted twice.
* Internally, this class maintains a flag to resolve the ambiguity
* in the millisecond count during a leap second.
* Note that this means a {@link java.util.Calendar} will express a positive
* leap second as 23:59:59, the same as the second before it.
* <p>
* Some references suggest that UTC allows a "double leap second",
* where 23:59:60 is followed by 23:59:61, however, this is not actually
* allowed by the defining document for UTC, and this class does not
* support it.
* <p>
* This class does not automatically
* check the validity of its time values. It is possible to specify a time
* which is incorrectly marked as a leap second, or a time which is a negative
* leap second, and therefore never occurs.
* However, you may use {@link #isValid()} to check if a given
* UTC date is consistent with the leap second table used by its time system.
* The results of operations on invalid dates are unspecified.
* <p>
* Note that UTC timekeeping started at 1972-01-01T00:00:00. Although this
* class can represent dates before this, they are not meaningful. However,
* in order to maintain a millisecond count from 1970-01-01T00:00:00.0, we
* arbitrarily extend UTC to that date and assume no leap seconds between
* 1970 and 1972.
* @see LeapTable
* @see UTCSystem
****************************************************************************/
public class UTCDate extends PreciseDate {

/** true during a leap second **/
boolean leapsec;

/*****************************************************************************
* Create a new UTCDate with the millisecond and nanosecond counts set to zero
* and the leap second flag set to false.
*****************************************************************************/
public UTCDate(UTCSystem system) {

    super(system);
}

/***************************************************************************
*
***************************************************************************/
protected UTCDate(TimeSystem system) {

    super(system);

} // end of protecte constructor


/***************************************************************************
* Same as calling setTime(millisec, 0).
* @param millisec The millisecond count.
* @see #setTime(long, int)
***************************************************************************/
public void setTime(long millisec) {

    setTime(millisec, 0);
}

/***************************************************************************
* Same as calling setTime(millisec, nanosec, false).
* @param millisec The millisecond count.
* @param nanosec The nanosecond count.
* @see #setTime(long, int, boolean)
***************************************************************************/
public void setTime(long millisec, int nanosec) {

    setTime(millisec, nanosec, false);
}


/***************************************************************************
* Sets the millisecond and nanosecond counts and the leap
* second flag.
* The precise meaning of these are defined in the class comments for
* {@link PreciseDate} and {@link UTCDate}.
* @param millisec The millisecond count.
* @param nanosec The nanosecond count.
* @param leapsec True if the instant occurs during a leap second.
***************************************************************************/
public void setTime(long millisec, int nanosec, boolean leapsec) {

    super.setTime(millisec, nanosec);
    this.leapsec = leapsec;

} // end of setTIme method


/******************************************************************************
* Method called internally by the {@link #compareTo(Object)} method.
* It assumes the given date is a UTC date and compares its time with this
* one, taking leap seconds into account.
* @param date the UTCDate to compare with this one.
* @return -1 if this date comes before the given one, 1 if it comes after
*          and 0 if the two dates refer to the same instant.
* @throws ClassCastException if the argument is not a UTCDate
******************************************************************************/
protected int compareTimes(PreciseDate date) {

    /**************************************************************
    * recast as a UTCDate since we will need to
    * look at the leapsecond counts. We could do this
    * later, but it's good to do it right away
    * to throw an exception if the time systems are not the same
    **************************************************************/
    UTCDate utc = (UTCDate)date;

    long millisec1 = this.getMilliseconds();
    long millisec2 = date.getMilliseconds();

    long sec1 = millisec1/1000;
    long sec2 = millisec2/1000;

    /******************
    * compare seconds *
    ******************/
    if(     sec1 < sec2) return -1;
    else if(sec1 > sec2) return 1;
    else {
        /******************************************
        * the second count is the same, so check
        * the leapsecond counts
        ******************************************/
        if(    !leapsec &&  utc.leapsec ) return -1;
        else if(leapsec && !utc.leapsec ) return  1;
        else {
            /****************************************
            * we are in the same second, so compare
            * milliseconds
            ****************************************/
            if(     millisec1 < millisec2) return -1;
            else if(millisec1 > millisec2) return  1;
            else {
                /**************************
                * we're down to nanoseconds *
                ****************************/
                if(     nanosec < date.nanosec) return -1;
                else if(nanosec > date.nanosec) return  1;
                else                            return  0;
            }
        }
    }

} // end of compareTimes method

/****************************************************************************
*
****************************************************************************/
protected void copyContentsFrom(PreciseDate date) {

    UTCDate utc = (UTCDate)date;

    setTime(utc.getMilliseconds(), utc.getNanoseconds(), utc.isLeapSecond());


} // end of copyContentsFrom method

/***************************************************************************
* Calculates the difference in time between two dates. In order to
* properly account for leap seconds, this method first converts both
* dates to TAI, and then computes the difference.
* @param date an instant in time
* @return the number of TAI seconds which need to
*         ellapse after the given instant until this
*         date occurs.
* @see PreciseDate#secondsAfter(PreciseDate)
****************************************************************************/
public double secondsAfter(PreciseDate date) {

    TAISystem TAI = TAISystem.getInstance();
    return TAI.convertDate(this).secondsAfter(TAI.convertDate(date));

} // end of secondsUntil method

/****************************************************************************
* Moves the time referred to by this date a given number of seconds into the
* future (or the past if the argument is negative). In order to account for
* leap seconds this method first converts to TAI, then applies the offset and
* finally converts back to UTC.
* @param seconds a number of TAI seconds
****************************************************************************/
public void increment(double seconds) {

    PreciseDate tai = TAISystem.getInstance().convertDate(this);
    tai.increment(seconds);
    setTime(tai);
}

/****************************************************************************
* Returns true during a leap second. The value returned is the same as
* the boolean flag in {@link #setTime(long, int, boolean)}.
* @return true if the instant represented by this object occurs during
*         a leap second.
****************************************************************************/
public boolean isLeapSecond() { return leapsec; }

/****************************************************************************
* Checks if this object is consistent with its time system's leap
* second table.
* @return true if this instant in time can actually occur.
****************************************************************************/
public boolean isValid() {

    return ((UTCSystem)system).getLeapTable().isValid(this);

}

/*****************************************************************************
* Gives a string representation of the date. This appends the leap second
* flag to {@link PreciseDate#toString()}.
*****************************************************************************/
//public String toString() { return super.toString() +" leapsec="+leapsec; }

} // end of UTCDate class
