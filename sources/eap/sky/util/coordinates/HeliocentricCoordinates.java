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

package eap.sky.util.coordinates;

import eap.sky.time.*;
import eap.sky.time.barycenter.*;
import eap.sky.util.*;
import eap.sky.earth.*;
import eap.sky.ephemeris.*;

/**************************************************************************
*
**************************************************************************/
public class HeliocentricCoordinates extends EclipticCoordinates {

UT1System UT1;
Ephemeris ephemeris;
Observatory obs;

/**************************************************************************
*
**************************************************************************/
public HeliocentricCoordinates(TDBSystem TDB, UT1System UT1, Ephemeris ephemeris,
                               Observatory obs) {

    super(TDB);

    this.UT1 = UT1;
    this.ephemeris = ephemeris;
    this.obs = obs;



} // end of constructor

/**************************************************************************
*
**************************************************************************/
public HeliocentricCoordinates(Ephemeris ephemeris, Observatory obs) {

    this(TDBSystem.getInstance(), UT1System.getInstance(), ephemeris, obs);

} // end of default time system constructor

/***************************************************************************
*
***************************************************************************/
public Transform toRADec(PreciseDate time) {

    PreciseDate tdb = TDB.convertDate(time);
    EOP eop = (EOP)UT1.convertDate(time);

    ThreeVector pos = ephemeris.position(Ephemeris.SUN, tdb, eop, obs);
    Direction dir = pos.getDirection();

    Transform to_ecliptic = super.toRADec(time).invert();

    dir = to_ecliptic.transform(dir);

    return to_ecliptic.combineWith(
           new Rotation(dir.getLongitude(), Direction.Z_AXIS))
           .invert();

} // end of toRADec method



} // end of HeliocentricCoordinates class