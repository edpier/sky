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

/******************************************************************************
* Represents an instant in the "International Atomic Time" (TAI) time
* system. This system is based on a collection of atomic clocks as kept by
* <a href="http://www.bipm.fr">Bureau International des Poids et Measures
* (BIPM)</a>.
* TAI is not tied to the rotation of the Earth.
******************************************************************************/
public final class TAISystem extends TimeSystem {

private static final TAISystem instance = new TAISystem();

private static final Conversion[] conversions = {};

/******************************************************************************
* Create a new TAISystem. You should use {@link #getInstance()} instead
* of this constructor.
******************************************************************************/
protected TAISystem() {

    super("International Atomic Time", "TAI");
}

/******************************************************************************
* Returns an instance of this class. This method always returns the same object.
* @return the one and only TAISystem object.
******************************************************************************/
public static TAISystem getInstance() { return instance; }

/******************************************************************************
*
******************************************************************************/
protected Object readResolve() { return instance; }

/******************************************************************************
* Returns nothing. This is because TAi is not defined in terms of any other
* time system. Note that many other time systems are defined in terms of TAI,
* and provide methods for converting to and from TAI.
* @return an empty array
******************************************************************************/
public Conversion[] getConversionsTo() { return conversions; }

/******************************************************************************
* Returns nothing.
* @return an empty array
* @see #getConversionsTo()
******************************************************************************/
public Conversion[] getConversionsFrom() { return conversions; }

} // end of TAISystem class
