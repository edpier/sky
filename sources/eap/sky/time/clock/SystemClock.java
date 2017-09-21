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

package eap.sky.time.clock;

import eap.sky.time.*;

/****************************************************************************
* A clock which reports the the computer's interal clock time.
* Specifically this is based on {@link System#currentTimeMillis()}, so it
* will never report a leap second.
****************************************************************************/
public class SystemClock implements Clock {

UTCSystem UTC;

/****************************************************************************
* Create a new clock.
* @param UTC The UTC time system in which it will report time.
****************************************************************************/
public SystemClock(UTCSystem UTC) {
    this.UTC = UTC;
}

/****************************************************************************
* Create a new clock which used the default {@link UTCSystem}.
****************************************************************************/
public SystemClock() {

    this(UTCSystem.getInstance());
}

/****************************************************************************
* Retrieves the current time according to the system clock
* @return the current time in UTC
****************************************************************************/
public PreciseDate currentTime() {

    /***************************************************
    * create a UTC date and set it to the current time *
    ***************************************************/
    PreciseDate utc = UTC.createDate();
    utc.setTime(System.currentTimeMillis(), 0);

    return utc;

} // end of currentTIme method

} // end of SystemClock class
