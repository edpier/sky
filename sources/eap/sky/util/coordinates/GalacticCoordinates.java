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
import eap.sky.util.*;

/*********************************************************************
*
*********************************************************************/
public class GalacticCoordinates extends Coordinates {

private static final Aspect POLAR = Aspect.createPolarAspect(-90, -1,1);
private static final Aspect EQUATORIAL = Aspect.createEquatorialAspect(90, -1,1);

// the following values are taken from Wikipedia. The rotation of
// -110.072442 was found by trial and error to get the value of
// galactic 0,0 right. it actually leaves the RA slightly off. and seems
//to bear no relation to the zero of the longitude value from wikipedia.
// but this is certainly good enough for now.

public static final double POLE_RA = 15.0*(12.0+51.0/60.0 + 26.282/3600.0);
public static final double POLE_DEC=       27.0 +7.0/60.0 + 42.01 /3600.0;
private static final Transform to_ra_dec = new Rotation(
                     new Euler(
                     new Direction(POLE_RA, POLE_DEC), -110.072442)).invert();
                 //    -90-122.932)).invert();

/*********************************************************************
*
*********************************************************************/
public Transform toRADec(PreciseDate time) {

    return to_ra_dec;

} // end of toRADec method

/***************************************************************************
*
***************************************************************************/
public Aspect getAspect(int type) {

    if(type == Aspect.POLAR) return POLAR;
    else if(type == Aspect.EQUATORIAL) return EQUATORIAL;
    else throw new IllegalArgumentException("Unsupported aspect "+type);

} // end of getAspect method

} // end of GalacticCoordinates class
