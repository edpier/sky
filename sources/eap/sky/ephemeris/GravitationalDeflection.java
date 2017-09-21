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

import eap.sky.util.*;

/***************************************************************************
* The deflection of a light ray due to gravity. The Sun produces the
* strongest such effect by far. This class uses the usual first order
* approximation.
***************************************************************************/
public class GravitationalDeflection implements Deflection {

/** The mass of the sun times the gravitational constant in mks units **/
public  static final double GM_SUN = 1.32712438e20;
private static final double SPEED_OF_LIGHT =299792458.0;

double magnitude;

double min_sin;

/**************************************************************************
* Create a new deflection.
* @param GM The mass of the deflecting body times the gravitational constant
* in mks units.
* @param r The distance from the observer to the sun in meters
**************************************************************************/
public GravitationalDeflection(double GM, double r) {

    magnitude = 2.*GM/(SPEED_OF_LIGHT*SPEED_OF_LIGHT*r);

    /********************************************
    * this is .4 degrees. This is an ugly hack
    * to fix some instability for angles close
    * to but not equal to zero.
    * this value  is good for the sun,
    * but not other bodies
    ********************************************/
    min_sin = 0.00872653534951907;

  //  System.out.println("magnitude="+Math.toDegrees(magnitude)*3600+" arcsec");

} // end of constructor


/***************************************************************************
* Calculate the deflection. This uses the
* first order formula accurate to half a milliarcsecond at the limb of
* the Sun.
* @param angle The angle between the direction of the sun and the direction of
* the light ray at infinity.
***************************************************************************/
public Angle calculateDeflection(Angle angle) {

    double deflection = 0.0;
    if(angle.getCos() <1.0) {

        deflection = magnitude * angle.getSin()/(1.0-angle.getCos());

        /********************************
        * to provide a little stability *
        ********************************/
        if(angle.getSin() < min_sin) {
            deflection *= angle.getSin()* angle.getSin();
        }

    }

//System.out.println("deflection="+deflection);

    return Angle.createFromRadians(deflection);

} // end of transformAngle method


} // end of GravitationalDeflection class
