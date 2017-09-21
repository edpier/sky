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

import java.text.*;

/****************************************************************************
* Provides an alternative way to represent an instant in time. The JulianDate
* is the number of days and fractions of a day since noon, January 1, 4713 BCE
* in the Julian Calendar in a particular time system. This method of
* representing an instant in time was originally proposed by
* Joseph Justus Scaliger in 1583 in a work titled De Emendatione Temporum.
* It was later propsed for use in astronomy by John Herschel in his work
* <a href="http://visualiseur.bnf.fr/Visualiseur?Destination=Gallica&O=NUMM-94926">
* Outlines of Astronomy</a>, published in the mid 1800s.
* <p>
* To calculate the fractional part of a day, this class assumes every minute has
* 60 seconds. This means that The Julian Date during a UTC leap second
* is the same as the Julian Date one second earlier.
* This means that a JulianDate converted to a UTCDate can never represent
* a leap second.
* <p>
* The integer part of a Julian Date can be large, robbing precision from the
* fractional part if you use a floating point number to represent it.
* Even using a double, contemporary dates allow roughly 100 microsecond accuracy.
* This class splits the integer and fractional parts of the Julian date into
* separate numbers to ensure nanosecond precision for all dates.
****************************************************************************/
public class JulianDate {

private static final long MILLISECONDS_PER_DAY= 86400*1000;
private static final double JD_IN_1970 =  2440587.5;

private static final DecimalFormat format = new DecimalFormat(".0##############");

TimeSystem system;

int number;
double fraction;


/*****************************************************************************
* Creates a Julian Date for the given time system. The date is initialized to
* 0.0 or noon, January 1, 4713 BCE.
* @param system The time system in which thius Julian date is measured.
*****************************************************************************/
public JulianDate(TimeSystem system) {

    this(system, 0, 0.0);
}

/*****************************************************************************
* Creates a Julian Date for the given time system, with the date initialized
* to number + fraction.
* @param system The time system in which thius Julian date is measured.
* @param number The integer part of the Julian Date
* @param fraction The fractional part of the Julian Date.
*****************************************************************************/
public JulianDate(TimeSystem system, int number, double fraction) {

    this.system = system;
    set(number, fraction);
}

/*****************************************************************************
* Createas a Julian Date which refers to a given instant in time.
* @param date An instant in time.
*****************************************************************************/
public JulianDate(PreciseDate date) {

    date = CachedDate.uncache(date);

    this.system = date.getTimeSystem();

    long days = date.getMilliseconds()/MILLISECONDS_PER_DAY;
    long remainder = date.getMilliseconds() - days * MILLISECONDS_PER_DAY;

    days += 2440587;
    fraction = ((double)remainder + (double)date.getNanoseconds()/1000000.0)/
               (double)MILLISECONDS_PER_DAY + 0.5;

    if(fraction >=1.0) {
        fraction -=1.0;
        days += 1;
    }

    /*******************************************************
    * make sure we are not overflowing the integer storage *
    *******************************************************/
    if(days > Integer.MAX_VALUE) {
        throw new IllegalArgumentException(date+
                                    " is too far in the future to represent");
    }

    number = (int)days;

} // end of constructor from a date

/****************************************************************************
* Creates a new Julian Date specifying the integer and fraction parts
* separately for maximum precision
* @param number The julian day number
* @param fraction Fractional part of the Julian date.
* @throws IllegalArgumentException if the fraction is not in the range
*         0 <= fraction < 1.0
****************************************************************************/
public void set(int number, double fraction) {

    if(fraction < 0.0 || fraction >= 1.0 ) {
        throw new IllegalArgumentException("Invalid fraction "+fraction);
    }

    this.number = number;
    this.fraction = fraction;

} // end of constructor

/****************************************************************************
* Creates a new Julian date specified by a single double. Note that the
* specifying the Julian date this way is only accurate to the nearest
* millisecond, because of the limitations of a double.
****************************************************************************/
public void set(double jd) {


   this.number = (int)Math.floor(jd);
   this.fraction = jd - number;

 //  System.out.println("setting "+jd+" -> "+this);

} // end of constructor


/*****************************************************************************
* Returns the Julian day number. This is the integer part of this Julian date.
*****************************************************************************/
public int getNumber() { return number; }

/*****************************************************************************
* Returns the fractional part of the Julian date. This is equal to the Julian
* Date minus the Julian Day Number.
*****************************************************************************/
public double getFraction() { return fraction; }

/*****************************************************************************
* Returns the Julian Date as a single number. Note that this discards precision
* beyond the millisecond level.
*****************************************************************************/
public double getJulianDate() { return fraction + number; }

/*****************************************************************************
* Returns the Modified Julian date. This is the Julian date minus 2400000.5 .
* Note that the returned value is precise to the 10 microsecond level.
*****************************************************************************/
public double getModifiedJulianDate() {
    return (double)(number - 2400000 ) + (fraction - 0.5);

}

/*****************************************************************************
* Sets this Julian Date to the corresponding Modified Julian Day (MJD).
* MJD is the Julian date minus 2400000.5 .
* Note that MJD expressed as a double is precise to the 10 microsecond level.
*****************************************************************************/
public void setModifiedJulianDate(double mjd) {

//    int mjd_number = (int)Math.floor(mjd);
//    int mjd_fraction = mjd - mjd_number;

    set(mjd+0.5);
    number += 2400000;

} // end of setModifiedJulianDay method

/****************************************************************************
* Converts the Julian Day into a date.
* @return The instant in time refered to by this Julian Date.
****************************************************************************/
public PreciseDate toDate() {

    PreciseDate date = system.createDate();

    long millisec = (long)(number - 2440587) * MILLISECONDS_PER_DAY +
                    (long)Math.floor((fraction-.5) * MILLISECONDS_PER_DAY);

    long nanosec = Math.round((fraction) * 1000000*MILLISECONDS_PER_DAY);
    nanosec %= 1000000l;

    date.setTime(millisec, (int)nanosec);



    return date;

} // end of getDate method


/****************************************************************************
* Returns the difference between two Julian Days. Note that the precision of
* the result depends on how close together the two dates are.
****************************************************************************/
public double offsetFrom(JulianDate date0) {

    return (number-date0.number) + (fraction - date0.fraction);

}

/****************************************************************************
* Represents the Julian Date as a string.
****************************************************************************/
public String toString() {
    return "JD "+number+format.format(fraction)+" "+system;
} // end of toString method

} // end of JulianDate class
