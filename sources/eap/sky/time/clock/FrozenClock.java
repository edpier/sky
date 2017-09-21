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
* Represents a stopped clock. A frozen clock always returns the same time.
****************************************************************************/
public class FrozenClock implements Clock {

PreciseDate time;

/****************************************************************************
* Create a new clock.
* @param time the time which will be returned by {@link #currentTime()}.
*        Note the constructor copies this time, so it is safe to modify
*        it after calling the constructor.
****************************************************************************/
public FrozenClock(PreciseDate time) {

    /*******************************************************
    * we clone this time so nobody can mess with our copy
    * after calling the constructor
    *******************************************************/
    this.time = (PreciseDate)time.copy();
}

/****************************************************************************
* Returns a copy of the time specified in the constructor.
****************************************************************************/
public PreciseDate currentTime() {

    /**************************************************
    * note we return a copy of the current time,
    * so that this clock is not affected if someone
    * modified the returned time
    **************************************************/
    return (PreciseDate)time.copy();
}


} // end of FrozenClock class
