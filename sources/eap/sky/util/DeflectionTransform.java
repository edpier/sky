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

package eap.sky.util;


/****************************************************************************
* Represents a radial transform for which the transformed angle is equal
* to the original angle plus some offset. Furthermore, the offset may
* only be calculated in one direction, so that the inverse transform requires
* iteration. Examples of deflection transforms, are velocity aberration,
* atmospheric refraction, and general relativistic  deflection of light rays.
* The actual formula for calculating the deflection is specified using
* an object implementing the {@link Deflection} interface.
****************************************************************************/
public class DeflectionTransform extends RadialTransform {

Deflection deflection;
private boolean inverse;

/**************************************************************************
* Create a new transform.
* @param deflection The formula for calculating the deflection angle.
* @param pole The pole of the radial transform.
* @param inverse If this is true, the constructed object will represent the
* inverse transform.
**************************************************************************/
public DeflectionTransform(Deflection deflection, Direction pole,
                           boolean inverse) {

    super(pole);

    this.deflection = deflection;
    this.inverse = inverse;

} // end of constructor

/**************************************************************************
* Create a new forward deflection transform.
* @param deflection The formula for calculating the deflection angle.
* @param pole The pole of the radial transform.
**************************************************************************/
public DeflectionTransform(Deflection deflection, Direction pole) {

    this(deflection, pole, false);
}

/**************************************************************************
* Calculates the transformed angle from the pole, given the original one.
* if this is an inverse transform, this method iterates to find the solution.
**************************************************************************/
public final Angle transformAngle(Angle angle0) {

    if(!inverse) {
        /********************************************
        * we can just apply the deflection directly *
        ********************************************/
        return angle0.plus(deflection.calculateDeflection(angle0));

    } else {
        /*******************************************************
        * we need to numerically invert the transform, since
        * we only know how to calculate the deflection in
        * terms of the answer.
        * Note this is not very fast or robust. We need a better
        * method.
        *******************************************************/
        Angle angle1 = angle0;
        for(int iteration=0; iteration < 1000; ++iteration) {

            Angle last_angle1 = angle1.copy();
            Angle delta = deflection.calculateDeflection(angle1);

            angle1 = angle0.minus(delta);
            if(angle1.equals(last_angle1)) break;


            if(iteration == 999) {
             //   System.out.println("deflection iteration did not converge");
              //  System.out.println(deflection);
               // System.exit(0);
            }

        } // end of iterations

        return angle1;

    } // end if we had to invert

} // end of transformAngle method


/**************************************************************************
* Return a new transform which is the inverse of this one.
* @return the inverse of this transform.
**************************************************************************/
public Transform invert() {

    return new DeflectionTransform(deflection, getPole(), !inverse);

} // end of invert method



} // end of Refraction class
