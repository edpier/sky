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

import eap.sky.util.*;
import eap.sky.time.*;

/**************************************************************************
* Represents the Right Ascension and Declination coordinates familiar
* to astronomers. In particular this is the
* <a href="http://aa.usno.navy.mil/faq/docs/ICRS_doc.html">
* International Celestial Reference System (ICRS)</a>, which for most
* purposes is functionally equivalent to "J2000". The difference is in
* the implementation for which the older J2000 FK5 uses optical observations
* of a set of stars to define the reference frame, while ICRS is implemented
* with a set of VLBI measurements of radio sources as the
* International Celestial Reference Frame (ICRF).
* <p>
* A {@link Direction} in these coordinates has longitude corresponding
* to Right Ascension, and latitude corresponding to Declination.
**************************************************************************/
public class RADec extends Coordinates {

private static final Aspect POLAR = Aspect.createPolarAspect(-90, -1,1);
private static final Aspect EQUATORIAL = Aspect.createEquatorialAspect(90, -1,1);

//private static final RADec instance = new RADec();

/*************************************************************************
* Create a new instance. This class is so simple we only need one instance,
* so this method is protected and you should use {@link Coordinates#RA_DEC}
* instead.
*************************************************************************/
protected RADec() {}

/***************************************************************************
*
***************************************************************************/
public Aspect getAspect(int type) {

    if(type == Aspect.POLAR) return POLAR;
    else if(type == Aspect.EQUATORIAL) return EQUATORIAL;
    else throw new IllegalArgumentException("Unsupported aspect "+type);

} // end of getAspect method

/*************************************************************************
* Returns the identity transform.
* @return {@link Rotation#IDENTITY}
*************************************************************************/
public Transform toRADec(PreciseDate time) { return Rotation.IDENTITY; }


} // end of RADec class
