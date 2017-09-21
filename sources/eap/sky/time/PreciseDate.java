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
* An extension to the java Date class which handles different timekeeping
* systems and sub-millisecond precision.
* <p>
* This class adds the following two properties to the standard Date class:
* <ul>
* <li> a "nanosec" count which specifies the number of
*      nanoseconds since the last integral millisecond.
* <li> a time system designation, indicating the definition of how the
*      time is measured (e.g. TAI, UTC, etc).
* </ul>
*
* It inherits the millisecond count
* from {@link java.util.Date}, but this is defined more precisely to mean
* "the number of milliseconds since the instant 1970-01-01T00:00:00.0
* in the time system designated by
* {@link #getTimeSystem()}, assuming all minutes have 60 seconds".
* This definition preserves backward compatibility with
* {@link java.util.GregorianCalendar}.
*
*****************************************************************************/
public class PreciseDate extends Date {

/** The time system used to represent an instant of time **/
protected TimeSystem system;

/** Number of nanoseconds since the last integral millisecond **/
protected int nanosec;

/****************************************************************************
* Creates a new date to be represented by the given time system.
* The millisecond and nanosecond counts will be set to zero.
* Note that not all time systems use a PreciseDate to accurately represent
* an instant in time (notably UTC). So it is better to use
* {@link TimeSystem#createDate()}, which will always return the proper
* subclass. This constructor does not check whether the given time system
* is appropriate for a PreciseDate.
* @param system The time system this date will use.
***************************************************************************/
public PreciseDate(TimeSystem system) {

    super(0l);
    this.nanosec = 0;

    this.system = system;

} // end of constructor


/****************************************************************************
* Returns the time system used by this date.
* @return This date's time system.
****************************************************************************/
public TimeSystem getTimeSystem() { return system; }

/***************************************************************************
* Return a copy of this date.
***************************************************************************/
public PreciseDate copy() {

    PreciseDate date = system.createDate();
    date.copyContentsFrom(this);

    return date;

} // end of copy method

/******************************************************************************
* Compare two dates assuming they use the same time system.
* This is called by the {@link #compareTo(Object)} method.
* This method first compares the millisecond value inherited from
* Date. If those are equal, it compares the nanosecond values.
* Subclasses
* should override this method if they use additional properties to specify
* an instant.
* @param date The date to compare to.
* @return -1 if this date comes before the given one, 1 if it comes after
*          and 0 if the two dates refer to the same instant.
******************************************************************************/
protected int compareTimes(PreciseDate date) {

    long millisec1 = this.getMilliseconds();
    long millisec2 = date.getMilliseconds();

    /***********************
    * compare milliseconds *
    ***********************/
    if(     millisec1 < millisec2) return -1;
    else if(millisec1 > millisec2) return  1;
    else {
        /*********************
        * compare nanoseconds *
        **********************/
        if(     nanosec < date.nanosec) return -1;
        else if(nanosec > date.nanosec) return  1;
        else                            return  0;
    }


} // end of compareTimes

/***************************************************************************
* Compares the ordering of two instances in time.
* Warning: if the argument is not a PreciseDate, then this object will be
* treated as a generic Java {@link Date}, ignoring all time system information.
* This will probably give invalid results. However this behavior is required
* so that {@link PreciseDate#compareTo(Date)} ==
*     - {@link Date#compareTo(Date)}, where the second argument is a PreciseDate.
* <p>
* If the given date is an PreciseDate, then this method will attempt to
* convert the given date to the same time system as this one before comparing.
* @return -1 if this date comes before the given one, 1 if it occurs after
*         and 0 if the two dates refer to the same instant.
****************************************************************************/
public final int compareTo(Date date) {

    /*********************************************************
    * first a quick check to see if they are the same object *
    *********************************************************/
    if(date == this) return 0;

    /************************************************
    * if the object is a plain Java Date, then
    * just compare milliseconds. We have to do this
    * for the sake of symmetry
    *************************************************/
    if(! (date instanceof PreciseDate)) {

        return -date.compareTo(this);
    }

    /*******************************************
    * now, recast the date as a precise date *
    *******************************************/
    PreciseDate precise = (PreciseDate)date;

    /**********************************************
    * if the time systems are the same we can compare
    * directly, otherwise we have to try converting
    * one of thedates to the other's time system
    **********************************************/
    if(system.equals(precise.getTimeSystem()) ||
       (system                  instanceof CachedTimeSystem &&
        precise.getTimeSystem() instanceof CachedTimeSystem    )) {
        /************************************
        * same time system, so just compare *
        ************************************/
        return compareTimes(precise);

    } else {
        /***********************************************
        * try converting that date to this time system *
        ***********************************************/
        try {
            PreciseDate converted = system.createDate();
            converted.setTime(precise);
            return compareTimes(converted);
        } catch(NoSuchConversionException e) {
            /*******************************
            * try converting the other way *
            *******************************/
            PreciseDate converted = precise.getTimeSystem().createDate();
            converted.setTime(this);
            return converted.compareTimes(precise);
        }

    } // end if the dates are in different systems

} // end of compareTo method

/****************************************************************************
* Returns the number of milliseconds since January 1, 1970, 00:00:00
* in the time system for this date, assuming each minute has 60 seconds.
* This is the same as the
* value returned by the getTime() method inherited from the Date class.
****************************************************************************/
public long getMilliseconds() { return getTime(); }

/****************************************************************************
* Returns the number of nanoseconds since the last intergal number of
* milliseconds
****************************************************************************/
public int getNanoseconds() { return nanosec; }


/***************************************************************************
* Set the nanosecond value.
* @throws IllegalArgumentException if the nanosec valus is outside the
*         range 0 - 999,999
***************************************************************************/
private void setNanoseconds(int nanosec) throws IllegalArgumentException {

    if(nanosec < 0 || nanosec > 999999 ) {
        throw new IllegalArgumentException("Illegal nanoseconds value "+
                                           nanosec);
    }

    this.nanosec = nanosec;

} // end of setNanosec method

/****************************************************************************
* Sets the number of milliseconds, with the number of nanoseconds assumed to
* be zero. This is the same as calling setTime(millisec,0).
* @param millisec The millisecond count.
* @see #getMilliseconds()
* @see #setTime(long, int)
****************************************************************************/
public void setTime(long millisec) {

    setTime(millisec,0);
}

/*****************************************************************************
* Sets the number of milliseconds and nanoseconds since
* January 1, 1970, 00:00:00 In the time system for this date, assuming each
* minute has 60 seconds.
* @param millisec The millisecond count
* @param nanosec The nanosecond count since the last integral milisecond
* @see #getMilliseconds()
* @see #getNanoseconds()
* @throws IllegalArgumentException if nanosec is not in the
*         range 0 - 999,999
****************************************************************************/
public void setTime(long millisec, int nanosec) {

    super.setTime(millisec);
    setNanoseconds(nanosec);
}

/****************************************************************************
* Calculates the difference in time between two dates. The argument is converted
* to the same time system as this date (if necessary) before comparing the
* two dates. Note that different time systems do not always use the same
* length of a second. Note also that the double return value may not be
* able to hold the full nanosecond precision of the difference.
* @param date an instant in time
* @return the number of seconds in this date's time system which need to
*         ellapse after the given instant until this
*         date occurs.
*
****************************************************************************/
public double secondsAfter(PreciseDate date) {

    try {
        date = system.convertDate(date);

        return (getMilliseconds() - date.getMilliseconds()) * 1e-3 +
               (getNanoseconds()  - date.getNanoseconds() ) * 1e-9;

    } catch(NoSuchConversionException e) {
        /********************************
        * try converting the other way *
        *******************************/
        PreciseDate converted = date.getTimeSystem().convertDate(this);

        return (converted.getMilliseconds() - date.getMilliseconds()) * 1e-3 +
               (converted.getNanoseconds()  - date.getNanoseconds() ) * 1e-9;
    }

} // end of secondsAfter method

/****************************************************************************
* Moves the time referred to by this date a given number of seconds into the
* future (or the past if the argument is negative).
* @param seconds a number of seconds in this date's time system.
****************************************************************************/
public void increment(double seconds) {

    /*****************************************************
    * split off the integer part of the seconds
    * so that we don't overflow a long with nanoseconds
    *****************************************************/
    long whole_seconds = (long)Math.floor(seconds);
    seconds -= whole_seconds;

    /*****************************************************************
    * express the remainder in nanoseconds
    ****************************************************************/
    long nanosec = (long)Math.round(seconds * 1e9);

    /*****************************************************************
    * add the nanosecond offset in this date
    * this gives us the nanosecond offset since the last integral
    * millisecond of time
    ****************************************************************/
    nanosec += getNanoseconds();

    /*********************************************************
    * split the nanoseconds into milliseconds and nanoseconds.
    * Note that if DUT1 is negative, we may need to
    * "borrow one" from the milliseconds.
    **********************************************************/
    long millisec = nanosec/1000000l;
    nanosec  -= millisec*1000000;

    if(nanosec < 0) {
        nanosec += 1000000;
        millisec -= 1;
    }

    /*********************************************
    * add the seconds back into the milliseconds *
    *********************************************/
    millisec += whole_seconds*1000l;

    /*****************
    * reset the time *
    *****************/
    setTime(getMilliseconds() + millisec, (int)nanosec);

} // end of increment method

/****************************************************************************
* Sets the time of this object to the same time as the given date,
* assuming it uses the same time system as this one. This method does
* not explicitly check that the dates use the same system, so the caller
* should be careful to ensure that this is the case.
* Subclasses may need to override this method if they add additional
* properties to PreciseDate.
* @param date A date in the same time system as this one.
****************************************************************************/
protected void copyContentsFrom(PreciseDate date) {

    setTime(date.getMilliseconds(), date.getNanoseconds());

} // end of copyContentsFrom method


/***************************************************************************
* Sets the time in this object to the same instant designated by the
* given date. If the given date uses a different time system, it will
* be converted to this time system.
* @param date The date to which we should set this date.
***************************************************************************/
public final void setTime(PreciseDate date) {

    if(!system.equals(date.getTimeSystem())) {
        /********************************************
        * we have to convert the date to our system *
        ********************************************/
        Conversion conv = date.getTimeSystem().getConversionTo(system);

        PreciseDate converted = system.createDate();
        conv.convert(date, converted);

        date = converted;

    }

    copyContentsFrom(date);

} // end of setTime method

/****************************************************************************
* Sets the time in this object to the same instant designated by the
* given date. If the argument is actually a PreciseDate, this method
* calls {@link #setTime(PreciseDate)}. Generic Java {@link Date} objects
* are treated as UTC
* dates formed by calling {@link UTCSystem#convertDate(Date)} using
* {UTCSystem#getInstance()}.
* @param date The date to which we should set this date.
****************************************************************************/
public final void setTime(Date date) {

    if(date instanceof PreciseDate) {
        /*************************************
        * setting from another accurate date *
        *************************************/
        setTime((PreciseDate)date);

    } else {
        /***********************************************
        * setting from a generic Java Date. We first
        * need to convert this to a UTCDate and then
        * set from that
        ***********************************************/
        UTCDate utc = UTCSystem.getInstance().convertDate(date);
        setTime(utc);
    }

} // end of setTime method

/****************************************************************************
* Returns a string representation of this date.
* Unlike {@link java.util.Date}, this method does not format the date into
* a calendar date.
****************************************************************************/
public String toString() {

    return system.createFormat().format(this);

} // end of toString method

/****************************************************************************
* Returns true if this date occurs after the given one. This is the
* same as compareTo(date) > 0
* @param date The date to compare to this one
****************************************************************************/
public boolean after(Date date) { return compareTo(date) > 0; }

/****************************************************************************
* Returns true if this date occurs before the given one. This is the
* same as compareTo(date) < 0
* @param date The date to compare to this one
****************************************************************************/
public boolean before(Date date) { return compareTo(date) < 0; }

/****************************************************************************
* Returns true if this date refers to the same instant as the given one.
* This is the same as compareTo(date) == 0
* @param date The date to compare to this one
****************************************************************************/
public boolean equals(Date date) { return compareTo(date) == 0; }

/****************************************************************************
* Returns true if this date refers to the same instant as the given one.
* This is the same as compareTo(date) == 0
* @param o The date to compare to this one
****************************************************************************/
public boolean equals(Object o) { return equals((Date)o); }

} // end of PreciseDate class

