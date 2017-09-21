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

/************************************************************************
*
************************************************************************/
public class LocalDate extends UTCDate {

boolean daylight;

/************************************************************************
*
************************************************************************/
public LocalDate(LocalTimeSystem system) {

    super(system);

} // end of constructor

/************************************************************************
*
************************************************************************/
public boolean isDaylightSavingsTime() { return daylight; }

/*************************************************************************
*
*************************************************************************/
public void setTime(long millisec, int nanosec, boolean leapsec,
                    boolean daylight) {

    setTime(millisec, nanosec, leapsec);
    this.daylight = daylight;

} // end of setTime method

/*************************************************************************
* Sets this date to the previous or current midnight. Note that if the
* current time is already midnight the date will remain unchanged.
*************************************************************************/
public void clipToMidnight() {

    /*****************************************************
    * create a calendar. Note we set it to GMT, because
    * our values are already adjusted for timezone
    *****************************************************/
    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("GMT"));

    /************************************
    * set back to the previous midnight *
    ************************************/
    cal.setTimeInMillis(getMilliseconds());
    cal.set(cal.HOUR_OF_DAY, 0);
    cal.set(cal.MINUTE, 0);
    cal.set(cal.SECOND, 0);
    cal.set(cal.MILLISECOND, 0);

    setTime(cal.getTimeInMillis(), 0, false, daylight);

} // end of clipToMidnight method

/*************************************************************************
* Sets this date to the first midnight after the current date. Note that
* if the date is currently midnight, it will be advanced to the following
* midnight.
*************************************************************************/
public void advanceToMidnight() {

    /*****************************************************
    * create a calendar. Note we set it to GMT, because
    * our values are already adjusted for timezone
    *****************************************************/
    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("GMT"));

    /************************************
    * set back to the previous midnight *
    ************************************/
    cal.setTimeInMillis(getMilliseconds());
    cal.set(cal.HOUR_OF_DAY, 0);
    cal.set(cal.MINUTE, 0);
    cal.set(cal.SECOND, 0);
    cal.set(cal.MILLISECOND, 0);

    cal.add(cal.DAY_OF_YEAR, 1);

    setTime(cal.getTimeInMillis(), 0, false, daylight);

} // end of clipToMidnight method

} // end of LocalDate
