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

package eap.sky.ephemeris; // maybe a better place for this?

import eap.sky.util.*;

/*****************************************************************************
* Transforms a light ray as seen by a moving observer.
* Aberration is another name for relativistic beaming. In the classical limit
* where the observer's velocity is much less than light, the apparent position
* of a star is shifted away from the direction of the observer's motion by
* on the order of v/c radians. The largest component of a terrestrial
* observer's velocity is due to the Earth's orbit around the Sun, where
* v/c ~ 1e-4 = 20 arcsec. At this level, relativistic effects are on the
* order of a milliarcsecond. The rotation of the Earth adds a
* separate component
* on the order of 0.3 arc seconds. To accomodate different velocity
* components, it is generally better to sum the
* velocities and implement one aberration. This is because the velocity
* direction is specified in the untransformed coordinates, so the second
* aberration would need its velocity specified in the coordinates
* after the first aberration was applied.
* <p>
* This class does the full relativistic calculation. It gives the transform
* from the "actual" position of a star to its observed position. The
* {@link #transform(Direction)} method returns the inverse transform.
*****************************************************************************/
public class Aberration extends RadialTransform {

private static final double SPEED_OF_LIGHT = 299792458.0;

double v;

/***************************************************************************
* Creates a new aberration transform.
* @param velocity The velocity vector of the observer in meters per second.
***************************************************************************/
public Aberration(ThreeVector velocity) {

    this(velocity.getDirection(), velocity.getLength()/SPEED_OF_LIGHT);


} // end of constructor

/***************************************************************************
* Creates a new aberration transform.
* @param velocity The velocity vector of the observer in meters per second.
***************************************************************************/
private Aberration(Direction vhat, double v_over_c) {

    super(vhat);

    this.v = v_over_c;

   // System.out.println("velocity="+vhat+" v/c="+v);

} // end of constructor

/***************************************************************************
* Transforms the radial angle. This implements fully relativistic beaming.
* @param angle the angle between the original direction and
*        the pole.
* @return The angle between the transformed direction and
*         the pole.
***************************************************************************/
public Angle transformAngle(Angle angle) {

    /******************************
    * get the cosine of the angle *
    ******************************/
    double mu = angle.getCos();
    if(mu >  1.0) mu =  1.0;
    if(mu < -1.0) mu = -1.0;
   /*************************************************
   * now some relativistic beaming
   * This is Rybicki and Lightman eq (4.8b)
   * rearranged a bit to help with numerical accuracy
   **************************************************/
   double mu_p = mu + v * (mu*mu -1.0)/(1.0 - v*mu);

 //   System.out.println("mu="+mu +" mu_p="+mu_p);

   /*****************************************************
   * just in case roundoff error pushes mu_p outside
   * the range -1 to 1
   *****************************************************/
   if(mu_p >  1.0) mu_p =  1.0;
   if(mu_p < -1.0) mu_p = -1.0;

   return Angle.createFromCos(mu_p);

} // end of transformAngle method



/***************************************************************************
* Returns the inverse transform. This is the tranform where the observer's
* velocity is the negative of this transforms velocity.
***************************************************************************/
public Transform invert() {

    /**************************************************************
    * note that the inverse transform will share the vhat vector
    * with this one. This means that we should never modify vhat.
    * Currently this is true, I just wanted to make sure future
    * mods don't break this
    **************************************************************/
    return new Aberration(getPole(), -v);

} // end of invert method


} // end of Aberration class
