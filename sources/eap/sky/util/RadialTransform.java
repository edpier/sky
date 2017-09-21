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

/***************************************************************************
* Represents a radially symmetric transform. Such a transform only depends
* on the angle between the original direction and an arbitrary, fixed pole.
* Furthermore, the transform only changes the angle between the original
* direction and the pole. This is an abstract class. Subclasses must
* define how the radial angle is transformed. Examples of radial transforms
* are aberration (where the pole is the velocity vector), and refraction,
* where the pole is the zenith.
***************************************************************************/
public abstract class RadialTransform extends Transform {

/*********************************************************
* the direction specified in the constructor. Note that
* subclasses should not modify this field.
**********************************************************/
private Direction pole_direction;

private double[] pole;

/***************************************************************************
* Creates a new Radial transform.
* @param pole The reference direction with respect to which the radial
*        angles are measured.
***************************************************************************/
public RadialTransform(Direction pole) {

    this.pole_direction = pole;
    this.pole = pole.unitVector();


} // end of constructor

/****************************************************************************
* Return the pole of the transform.
* @return the direction of the pole of the transform.
****************************************************************************/
public Direction getPole() { return pole_direction; }

/***************************************************************************
* Performs the transform. Given the apparent position of a star in the rest
* frame, gives the apparent position in the observer's frame.
* @param dir the position of a distant object in the rest frame (generally
*        taken to be the Solar System barycenter).
* @return The position of a distant object in the observer's frame.
***************************************************************************/
public Direction transform(Direction dir) {

    /*********************************
    * get the original unit vector *
    *******************************/
    double[] r = dir.unitVector();

    /******************************************************
    * get the component of the vector parallel to the pole *
    ******************************************************/
    double mu = r[0]*pole[0] + r[1]*pole[1] + r[2]*pole[2];
    if(mu >  1.0) mu =  1.0;
    if(mu < -1.0) mu = -1.0;



   /*****************************
   * transform the radial angle *
   *****************************/
   double mu_p = transformAngle(Angle.createFromCos(mu)).getCos();

   /*******************************
   * special case of no transform *
   *******************************/
   if(mu_p == mu) return dir;

   /************************************************************
   * get the component of the vector perpendicular to the pole *
   ************************************************************/
   double[] r_perp = {r[0] - mu*pole[0],
                      r[1] - mu*pole[1],
                      r[2] - mu*pole[2]};

   double r_perp2 = r_perp[0] * r_perp[0] +
                    r_perp[1] * r_perp[1] +
                    r_perp[2] * r_perp[2];

   /*********************************************************************
   * above we caught the case of an identity transform.
   * If the original direction points toward the pole,
   * then the transformed vector also has to point toward
   * the pole, since the azimuth angle is not defined.
   * Numerically this would mean r_perp2 is zero
   * and a is infinity or NaN (see below). So we catch this case
   * here and throw an exception so we don't have to scratch
   * our heads wondering where the nessed up vaolues are coming from
   ********************************************************************/
   if(r_perp2 == 0) {
       throw new IllegalArgumentException("Transform of the pole "+
                                          "is not identity");
   }

   /*****************************************************************
   * the perpendicular component of the transformed direction
   * points the same way as the perpendicular part of the original
   * direction. Since we just got the parallel part of the transformed
   * direction, we can determine the perpendicular part by
   * forcing the transformed vector to be normalized.
   * Here we calculate a = r_perp_p/r_perp
   *****************************************************************/
   double a = Math.sqrt((1.0 - mu_p*mu_p)/r_perp2);

   /************************************
   * reassemble the transformed vector *
   ************************************/
   double[] r_p = {mu_p*pole[0] + a*r_perp[0],
                   mu_p*pole[1] + a*r_perp[1],
                   mu_p*pole[2] + a*r_perp[2]};


   return new Direction(r_p);


} // end of transform method

/***************************************************************************
* Transforms the radial angle.
* @param angle the angle between the original direction and
*        the pole.
* @return The cosine of the angle between the transformed direction and
*         the pole.
***************************************************************************/
public abstract Angle transformAngle(Angle angle);



} // end of RadialTransform method
