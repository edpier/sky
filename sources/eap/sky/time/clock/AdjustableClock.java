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

/***************************************************************************
* Represents a clock which is tied to the system clock, but with
* an adjustable rate and offset.
* @see ClockControler
***************************************************************************/
public class AdjustableClock implements Clock {

UTCSystem UTC;

PreciseDate scaled_t0;
long        system_t0;

double rate;



/***************************************************************************
* Create a new clock.
* @param UTC The UTC system used to represent time.
***************************************************************************/
public AdjustableClock(UTCSystem UTC) {

    this.UTC = UTC;
    resynch();

} // end of constructor

/***************************************************************************
* Create a new clock with the default {@link UTCSystem} instance.
***************************************************************************/
public AdjustableClock() {

    this(UTCSystem.getInstance());
}

/***************************************************************************
* Returns a time scaled and offset from the current system time.
***************************************************************************/
public PreciseDate currentTime() {

    PreciseDate utc = UTC.createDate();
    utc.setTime(scaled_t0);

    double offset = (System.currentTimeMillis() - system_t0) * rate * 1e-3;

    utc.increment(offset);

    return utc;

} // end of currentTime method

/***************************************************************************
* Sets the rate at which this clock runs. A rate of 1.0 means this clock
* will increment by one second for every second on the system clock.
* A negative rate means the clock runs backwards.
* @param rate the number of seconds on this clock which ellapse for every
* second on the system clock.
***************************************************************************/
public void setRate(double rate) {

    /****************************
    * update the reference time *
    ****************************/
    setTime(currentTime());

    this.rate = rate;


} // end of setRate method

/***************************************************************************
* Set the current time on the clock. This leaves the rate unchanged.
* @param time The new current time.
***************************************************************************/
public void setTime(PreciseDate time) {

    scaled_t0 = time;
    system_t0 = System.currentTimeMillis();

} // end of setTime method

/***************************************************************************
* Sets the clock time to the current system time, but leaves the rate unchanged
***************************************************************************/
public void now() {

    system_t0 = System.currentTimeMillis();

    scaled_t0 = UTC.createDate();
    scaled_t0.setTime(system_t0, 0);

} // end of now method

/***************************************************************************
* Resynchronizes this clock to the system clock. Sets the clock rate to 1.0,
* and resets the time to the current system time.
***************************************************************************/
public void resynch() {

    rate = 1.0;
    now();

} // end of resynch method




} // end of AdjustableClock class


