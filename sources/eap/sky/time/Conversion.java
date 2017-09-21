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

/*****************************************************************************
* Represents a conversion from one time system to another.
* Subclasses of {@link TimeSystem}
* may create subclasses of this one to implement conversions
* to and from that time system.
*****************************************************************************/
public abstract class Conversion {

TimeSystem from_system;
TimeSystem to_system;

/*****************************************************************************
* Create a new conversion between two time systems.
* @param from_system the initial time system.
* @param   to_system the final time system.
*****************************************************************************/
public Conversion(TimeSystem from_system, TimeSystem to_system) {

    this.from_system = from_system;
    this.to_system   =   to_system;

} // end of public constructor

/*****************************************************************************
* Returns the initial time system.
*****************************************************************************/
public TimeSystem getFromSystem() { return from_system; }

/*****************************************************************************
* Returns the final time system.
*****************************************************************************/
public TimeSystem getToSystem() { return to_system; }

/*****************************************************************************
* This method does nothing. However, a composite conversion overrides this
* to save intermediate steps.
*****************************************************************************/
public void setCache(CachedTimeSystem cache) {}

/*****************************************************************************
* This method always returns 1. {@link CompositeConversion} overrides this
* to give the number of individual date conversions which must be done to
* reach the destination.
*****************************************************************************/
public int getSteps() { return 1; }

/*****************************************************************************
* Do the actual conversion.
*****************************************************************************/
public abstract void convert(PreciseDate from, PreciseDate to);

/*****************************************************************************
* Gives a string representation of the conversion.
*****************************************************************************/
public String toString() {

    return getFromSystem()+" to "+getToSystem();
}

} // end of Conversion class
