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

/*****************************************************************************
* Holds a set of Z-Y-Z Euler angles. Euler angles are a set of three rotation
* angles about sucessive axes, which can specify any arbitrary rotation.
* There are several different kinds of Euler angles depending on the
* order of the axies. We arbitrarily restrict ourselves to the Z, Y, and Z
* axes, since these are close to the intuituve concepts of latitude, longitude,
* and roll angle. Note that this class is just a simple container for three
* numbers. The functionality for applying a rotational transform is handled
* by the {@link Rotation} class.
*****************************************************************************/
public class Euler {

double phi;
double theta;
double psi;

/******************************************************************************
* Create a new set of Euler angles.
* @param phi   The first  rotation angle about the Z axis in degress.
* @param theta The second rotation angle about the Y axis in degrees.
* @param psi   The third  rotation angle about the Z axis in degrees.
******************************************************************************/
public Euler(double phi, double theta, double psi) {

    this.phi   = phi;
    this.theta = theta;
    this.psi   = psi;

} // end of constructor

/******************************************************************************
* Creates a set of Euler angles by specifying the position of the transformed
* pole in the original coordinates
* and the angle from the old to new X axis.
* @param pole the direction of the original pole in the rotated coordinates.
* @param roll The angle between the original X axis and the rotated X axis
*        in degrees.
******************************************************************************/
public Euler(Direction pole, double roll) {

  //  this(pole.getLongitude(), 90.0-pole.getLatitude(), roll+90.0);
    this(pole.getLongitude(), 90.0-pole.getLatitude(), roll-pole.getLongitude());
} // end of constructor from longitude, latitude and roll angle

/**************************************************************************
*
**************************************************************************/
public static Euler fromDirAndRoll(Direction pole, double roll) {

    return new Euler(pole.getLongitude(), 90.0-pole.getLatitude(), roll+90.0);

} // end of fromDirAndRoll method

/******************************************************************************
* Returns the first Euler angle in degrees
* @return The first Euler angle in degrees.
******************************************************************************/
public double getPhi() { return phi; }

/******************************************************************************
* Returns the second Euler angle in degrees.
* @return The second Euler angle in degrees.
******************************************************************************/
public double getTheta() { return theta; }

/******************************************************************************
* Returns the third Euler angle in degrees.
* @return The third Euler angle in degrees.
******************************************************************************/
public double getPsi() { return psi; }

/******************************************************************************
* Returns the angle along the new equator of the original Z axis
* in degrees. Typically, this is Right Ascension.
* @return the corresponding longitude in degrees.
******************************************************************************/
public double getLongitude() { return phi; }

/******************************************************************************
* Returns the angle above the new equator of the original Z axis
* in degrees. Typically, this is Declination.
* @return The corresponding latitude in degrees.
******************************************************************************/
public double getLatitude() { return 90.0-theta; }

/******************************************************************************
* Returns the angle between the original X axis and the rotated X axis.
* Typically this is a roll angle, measured counterclockwide from the
* negative R.A. axis.
* @return the corresponding roll angle in degrees.
******************************************************************************/
public double getRoll() { return psi - 90.0; }

/******************************************************************************
* Convert to a string representation.
* @return a string representation
******************************************************************************/
public String toString() { return "Euler: phi="+phi+" theta="+theta+" psi="+psi; }

} // end of Euler class
