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

/**************************************************************************
*
**************************************************************************/
public class EclipticCoordinates extends Coordinates {

private static EclipticCoordinates INSTANCE;

private static final Aspect POLAR = Aspect.createPolarAspect(-90, -1,1);
private static final Aspect EQUATORIAL = Aspect.createEquatorialAspect(90, -1,1);

TDBSystem TDB;

/***************************************************************************
*
***************************************************************************/
public EclipticCoordinates(TDBSystem TDB) {

    this.TDB = TDB;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public EclipticCoordinates() {

    this(TDBSystem.getInstance());

} // end of constructor using default TDB


/***************************************************************************
*
***************************************************************************/
public static EclipticCoordinates getInstance() {

    if(INSTANCE == null) INSTANCE = new EclipticCoordinates();
    return INSTANCE;

} // end of getInstance method

/***************************************************************************
*
***************************************************************************/
public Aspect getAspect(int type) {

    if(type == Aspect.POLAR) return POLAR;
    else if(type == Aspect.EQUATORIAL) return EQUATORIAL;
    else throw new IllegalArgumentException("Unsupported aspect "+type);

} // end of getAspect method

/***************************************************************************
*
***************************************************************************/
public Transform toRADec(PreciseDate time) {

    JulianDate jd = new JulianDate(TDB.convertDate(time));
    double t = ((jd.getNumber() - 2451545) + jd.getFraction())/36525.0;

    /*************************************************
    * these values taken from page 114 of Seidelmann *
    *************************************************/
    double obliquity = t*(t*(t*4.75833333e-7
                       - 1.63888888888889e-07)
                       - 0.0130041666666)
                       + 23.49291111111111111111;

   return new Rotation(-obliquity, Direction.X_AXIS);


// 23.49291111111111111111 +
//                        - 0.0130041666666 * t;
//                        - 1.63888888888889e-07 * t*t
//                        + 4.75833333e-7 * t*t*t;

} // end of toRADec method

} // end of EclipticCoordinates class
