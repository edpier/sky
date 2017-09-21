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

package eap.sky.time.cycles;

import eap.sky.time.*;
import eap.sky.util.*;

import java.util.*;
import java.text.*;

/**************************************************************************
*
**************************************************************************/
public class Night implements Comparable<Night> {

private static EphemerisRiseSet default_calc;

private static final DateFormat format = initFormat();

EphemerisRiseSet calc;
LocalTimeSystem local;


int mjd;
CachedDate midnight;
String date_string;

CachedDate noon;
CachedDate sunset;
CachedDate evening_twilight;
CachedDate morning_twilight;
CachedDate sunrise;
CachedDate previous_sunrise;

Map<Twilight, CachedDate> evening;
Map<Twilight, CachedDate> morning;

/**************************************************************************
*
**************************************************************************/
private static DateFormat initFormat() {

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    format.setTimeZone(TimeZone.getTimeZone("GMT"));

    return format;

} // end of initFormat method

/**************************************************************************
*
**************************************************************************/
public static void setDefaultRiseSet(EphemerisRiseSet calc) {

    default_calc = calc;

} // end of setDefaultRiseSet static method

/**************************************************************************
*
**************************************************************************/
public static EphemerisRiseSet getDefaultRiseSet() {

    return default_calc;

} // end of setDefaultRiseSet static method

/**************************************************************************
*
**************************************************************************/
public Night(int mjd) {

    this(mjd, default_calc);

}


/**************************************************************************
* @throws IllegalArgumentException if the RiseSet is not for the Sun.
**************************************************************************/
public Night(int mjd, EphemerisRiseSet calc) {


    if(!calc.isSun()) {
        throw new IllegalArgumentException("Not a Sun RiseSet");
    }

    this.mjd = mjd;
    this.calc = calc;

    morning = new HashMap<Twilight, CachedDate>();
    evening = new HashMap<Twilight, CachedDate>();

    local = calc.getAzAltCoordinates()
                .getObservatory()
                .getLocalTimeSystem();

} // end of constructor

/**************************************************************************
*
**************************************************************************/
public Night(String date_string, EphemerisRiseSet calc) throws ParseException {

    this.calc = calc;

    morning = new HashMap<Twilight, CachedDate>();
    evening = new HashMap<Twilight, CachedDate>();

    local = calc.getAzAltCoordinates()
                .getObservatory()
                .getLocalTimeSystem();

    long millis = format.parse(date_string).getTime();

    UTCDate utc = (UTCDate)local.getUTCSystem().createDate();
    utc.setTime(millis, 0, false);
    JulianDate jd = new JulianDate(utc);

    mjd = (int)Math.floor(jd.getModifiedJulianDate())+1;

} // end string constructor

/**************************************************************************
*
**************************************************************************/
public Night(String date) throws ParseException {

    this(date, default_calc);

} // end of default string constructor method

/**************************************************************************
*
**************************************************************************/
public Night(PreciseDate time, EphemerisRiseSet calc) {

    this.calc = calc;

    morning = new HashMap<Twilight, CachedDate>();
    evening = new HashMap<Twilight, CachedDate>();

    local = calc.getAzAltCoordinates()
                .getObservatory()
                .getLocalTimeSystem();

    /*********************************************************
    * find the integer MJD which comes before the given time *
    *********************************************************/
    JulianDate jd = new JulianDate(local.getUTCSystem().convertDate(time));
    int mjd = (int)Math.floor(jd.getModifiedJulianDate());

    /***********************************
    * create a test night for that MJD *
    ***********************************/
    Night last = new Night(mjd, calc);

    /*************************************************
    * if we are before sunrise of that night,
    * then that's the night we're on,
    * otherwise were in the night after
    *************************************************/
    if(time.compareTo(last.getSunrise()) < 0) {
        /**********************************
        * it's the previous night we want *
        **********************************/
        this.mjd = last.mjd;
        this.midnight = last.midnight;
        this.sunrise = last.sunrise;
    } else {
        this.mjd = mjd+1;
    }

} // end of constructor from a time

/**************************************************************************
*
**************************************************************************/
public Night(PreciseDate time) {

    this(time, default_calc);

} // end of constructor from a time

/**************************************************************************
*
**************************************************************************/
public EphemerisRiseSet getRiseSet() { return calc; }

/**************************************************************************
*
**************************************************************************/
public int getMJDAtStart() { return mjd; }

/**************************************************************************
*
**************************************************************************/
public String getDateString() {

    if(date_string == null) {

        JulianDate jd = new JulianDate(local.getUTCSystem());
        jd.setModifiedJulianDate(mjd-1);

        date_string =  format.format(jd.toDate());

    } // end of we need to format the string

    return date_string;

} // end of getDateString method

/**************************************************************************
*
**************************************************************************/
public int getYear() {

    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("GMT"));

    JulianDate jd = new JulianDate(local.getUTCSystem());
    jd.setModifiedJulianDate(mjd-1);

    cal.setTime(jd.toDate());
    return cal.get(cal.YEAR);

} // end of getYear method

/**************************************************************************
*
**************************************************************************/
public int getNightOfYear() {

    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("GMT"));

    JulianDate jd = new JulianDate(local.getUTCSystem());
    jd.setModifiedJulianDate(mjd-1);

    cal.setTime(jd.toDate());
    return cal.get(cal.DAY_OF_YEAR);

} // end of getYear method

/**************************************************************************
*
**************************************************************************/
public PreciseDate getStartTime() { return getPreviousSunrise(); }

/**************************************************************************
*
**************************************************************************/
public PreciseDate getEndTime() { return getSunrise(); }

/**************************************************************************
*
**************************************************************************/
public CachedDate getNoonBefore() {

    if(noon == null) {

        UTCDate utc = (UTCDate)local.getUTCSystem().convertDate(getMidnight());
        boolean leap = utc.isLeapSecond();
        utc.increment(-12*3600);
        if(leap) utc.increment(-1);

        noon = new CachedDate(utc);

    } // end if we need to calculate it

    return noon;

} // end of getNoonBefore method


/**************************************************************************
*
**************************************************************************/
public CachedDate getMidnight() {

    if(midnight == null) {

        JulianDate jd = new JulianDate(local.getUTCSystem());
        jd.setModifiedJulianDate(mjd);
        LocalDate time = (LocalDate)local.convertDate(jd.toDate());
        time.advanceToMidnight();

        midnight = new CachedDate(time);

    } // end if we need to find midnight

    return midnight;

} // end of findMidnight method

/**************************************************************************
*
**************************************************************************/
public CachedDate getSunset() {

    if(sunset == null) {

        PreciseDate midnight = getMidnight();
        PreciseDate crossing = calc.findClosestCrossing(midnight, false, 0.0);

        sunset = new CachedDate(crossing);

    } // end if we have to find it

    return sunset;

} // end of getSunset method

/**************************************************************************
*
**************************************************************************/
public CachedDate getSunrise() {

    if(sunrise == null) {

        PreciseDate midnight = getMidnight();
        PreciseDate crossing = calc.findClosestCrossing(midnight, true, 0.0);
        sunrise = new CachedDate(crossing);

    } // end if we have to find it

    return sunrise;

} // end of getSunrise method

/**************************************************************************
*
**************************************************************************/
public CachedDate getPreviousSunrise() {

    if(previous_sunrise == null) {

        previous_sunrise = lastNight().getSunrise();

    } // end if we have to find it

    return previous_sunrise;

} // end of getPreviousSunrise method



/**************************************************************************
*
**************************************************************************/
public CachedDate getMorningTime(Twilight twilight) {

    CachedDate time = (CachedDate)morning.get(twilight);
    if(time == null) {

        PreciseDate midnight = getMidnight();
        PreciseDate crossing = calc.findClosestCrossing(midnight, true,
                                                        twilight.getAltOffset());
        time = new CachedDate(crossing);
        morning.put(twilight, time);
    }

    return time;

} // end of getMorning method

/**************************************************************************
*
**************************************************************************/
public CachedDate getEveningTime(Twilight twilight) {

    CachedDate time = (CachedDate)evening.get(twilight);
    if(time == null) {

        PreciseDate midnight = getMidnight();

        PreciseDate crossing = calc.findClosestCrossing(midnight, false,
                                                        twilight.getAltOffset());
        time = new CachedDate(crossing);
        evening.put(twilight, time);
    }

    return time;

} // end of getMorning method

/**************************************************************************
*
**************************************************************************/
public Direction getOpposition() {

    return getRiseSet().direction(new CachedDate(getMidnight()))
                       .oppositeDirection();

} // end of getOpposition method

/**************************************************************************
*
**************************************************************************/
public int daysAfter(Night night) {

    return mjd - night.getMJDAtStart();

} // end of daysAfter

/**************************************************************************
*
**************************************************************************/
public Night plus(int days) {

    return new Night(mjd + days, calc);

} // end of plus method

/**************************************************************************
*
**************************************************************************/
public Night lastNight() { return plus(-1); }


/**************************************************************************
*
**************************************************************************/
public Night nextNight() { return plus(1); }

/**************************************************************************
*
**************************************************************************/
public boolean contains(PreciseDate time) {

    return time.compareTo(getStartTime())  > 0 &&
           time.compareTo(  getEndTime())  <=0;

} // end of contains method

/**************************************************************************
*
**************************************************************************/
public boolean isAfter(PreciseDate time) {

    return time.compareTo(getStartTime()) <= 0;

} // end of isAfter method


/**************************************************************************
*
**************************************************************************/
public boolean isBefore(PreciseDate time) {

    return time.compareTo(getEndTime()) > 0;

} // end of isBefore method

/**************************************************************************
*
**************************************************************************/
public boolean equals(Object o) {

    Night night = (Night)o;
    return mjd == night.getMJDAtStart();

} // end of equals method

/**************************************************************************
*
**************************************************************************/
public int hashCode() { return mjd; }

/**************************************************************************
*
**************************************************************************/
public int compareTo(Night night) {

    int mjd = night.getMJDAtStart();

    if(      this.mjd <  mjd) return -1;
    else if (this.mjd == mjd) return  0;
    else                      return  1;

} // end of compareTo method


/**************************************************************************
*
**************************************************************************/
public String toString() {

    return "Night of "+getDateString()+" MJD="+mjd;

} // end of toString method

} // end of Night class
