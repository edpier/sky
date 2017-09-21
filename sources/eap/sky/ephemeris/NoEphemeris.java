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

package eap.sky.ephemeris;

import eap.sky.time.*;
import eap.sky.time.barycenter.*;
import eap.sky.util.*;
import eap.sky.earth.*;

/***************************************************************************
* A stand-in for an Ephemeris which contains no information about any
* celestial bodies. All of its position and velocity methods throw
* UnsupportedOperationException and the aberration and deflection are the
* identity transform. 
***************************************************************************/
public class NoEphemeris extends Ephemeris {

/***************************************************************************
*
***************************************************************************/
public NoEphemeris() {

    super(null);

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public ThreeVector barycentricPosition(int body, PreciseDate tdb) {

    throw new UnsupportedOperationException("No ephemeris data available");

} // end of barycentricPosition method

/***************************************************************************
*
***************************************************************************/
public ThreeVector barycentricVelocity(int body, PreciseDate tdb) {

    throw new UnsupportedOperationException("No ephemeris data available");

} // end of barycentricVelocity method

/***************************************************************************
*
***************************************************************************/
public Transform aberration(PreciseDate tdb, EOP eop, Observatory obs) {

    return Rotation.IDENTITY;

} // end of aberration method

/***************************************************************************
*
***************************************************************************/
public Transform deflection(PreciseDate tdb, EOP eop, Observatory obs) {

    return Rotation.IDENTITY;

} // end of deflection method

} // end of NoEphemeris class
