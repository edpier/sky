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

package eap.sky.earth;

import eap.sky.util.*;

import java.io.*;

/************************************************************************
* Holds a set of polar motion parameters and their errors.
* Polar motion is the movement
* of the Celestial Intermediate Pole (CIP - roughly the Earth's rotation Axis)
* with respect to the International Terrestrial Reference System
* (tied to the Earth's crust). It is commonly specified by two parameters:
* <ul>
* <li> x is the position of the CIP measured along the Earth's zero degree
* (Greenwich) meridian.
* <li> y is the position of the CIP measured along the 90 degrees west meridian.
* </ul>
************************************************************************/
public class PolarMotionParameters implements Serializable {

double x;
double y;
double x_err;
double y_err;

/************************************************************************
* Create a new set of parameters.
* @param x The polar motion x in arc seconds.
* @param y The polar motion y in arc seconds.
* @param x_err The error in x in arc seconds.
* @param y_err The error in y in arc seconds.
************************************************************************/
public PolarMotionParameters(double x, double y, double x_err, double y_err) {

    this.x = x;
    this.y = y;
    this.x_err = x;
    this.y_err = y;

} // end of constructor

/************************************************************************
* Returns the polar motion x parameter.
* @return x in arc seconds.
************************************************************************/
public double getX() { return x; }

/************************************************************************
* Returns the polar motion y parameter.
* @return y in arc seconds.
************************************************************************/
public double getY() { return y; }

/************************************************************************
* Returns the error in the polar motion x parameter.
* @return The error in x in arc seconds.
************************************************************************/
public double getXError() { return x_err; }

/************************************************************************
* Returns the error in the polar motion y parameter.
* @return The error in y in arc seconds.
************************************************************************/
public double getYError() { return y_err; }

/************************************************************************
* Adds an offset to the x and y parameters. This object remains unchanged.
* @param delta_x The value to add to x in arc seconds
* @param delta_y The value to add to y in arc seconds.
************************************************************************/
public PolarMotionParameters offset(double delta_x, double delta_y) {

    return new PolarMotionParameters(x + delta_x, y+delta_y, x_err, y_err);
}

/****************************************************************************
* Returns the transform due to polar motion. Specifically this is the
* transform from International Terrestrial Reference System cordinates
* to the coordinates formed by the Celestial Intermediate Pole and the
* Terrestrial Ephemeris Origin.
* @return The polar motion transform from ITRS to CIP/TEO coordinates.
****************************************************************************/
public Rotation rotation(double julian_centuries) {

    /*****************************************************************
    * calculate s-prime, which describes the motion of Terrestrial
    * Ephemeris Origin. It moves very slowly. The time should
    * *probably be in TT, but we have TDB handy, so we'll use that
    * since the difference is trivial.
    ****************************************************************/
    double x = getX()/3600.0;
    double y = getY()/3600.0;
    double sprime =  -47e-6/3600.0 * julian_centuries;

    Rotation first  = new Rotation(y, Direction.X_AXIS);
    Rotation second = new Rotation(x, Direction.Y_AXIS);
    Rotation third  = new Rotation(-sprime   , Direction.Z_AXIS);

    return (Rotation) first.combineWith(second).combineWith(third);

} // end of rotation method

/************************************************************************
* Tests if two sets of parameters are equal.
* @return true if x, y, and their correspnding errors are identical.
************************************************************************/
public boolean equals(Object o) {

    PolarMotionParameters param = (PolarMotionParameters)o;

    return x     == param.x &&
           y     == param.y &&
           x_err == param.x_err &&
           y_err == param.y_err;

} // end of equals method

} // end of PolarMotionParameters class
