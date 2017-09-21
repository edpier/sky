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

/***********************************************************************
* A generic model for precession and nutation. The combination of these
* describes the motion of the Celestial Intermediate Pole
(roughly the Earth's rotation axis) with respect to the Geocentric
* Celestial Reference System (roughly speaking the fixed stars).
* @see EOP
***********************************************************************/
public abstract class PrecessionModel implements Serializable {

/***************************************************************************
* Create a new model.
***************************************************************************/
protected PrecessionModel() {}

/***************************************************************************
* Calculate the precession/nutation X coordinate in radians.
* Subclasses must implement this for their particular model.
* @param args A set of fundamental arguments.
***************************************************************************/
public abstract double calculateX(TidalArguments args);

/***************************************************************************
* Calculate the precession/nutation Y coordinate in radians.
* Subclasses must implement this for their particular model.
* @param args A set of fundamental arguments.
***************************************************************************/
public abstract double calculateY(TidalArguments args);

/***************************************************************************
* Calculate the precession/nutation S coordinate in radians.
* Subclasses must implement this for their particular model.
* @param args A set of fundamental arguments.
* @param x The X value calculated by {@link #calculateX(TidalArguments)}
* @param y The Y value calculated by {@link #calculateY(TidalArguments)}
***************************************************************************/
public abstract double calculateS(TidalArguments args, double x, double y);

/***************************************************************************
* Calculate the rotation matrix corresponding to a set of precession
* values. Specifically this is the transform from the coordinates
* defined by the Celestial Intermediate Pole and the
* Celestial Ephemeris Origin.
* @param x The X value calculated by {@link #calculateX(TidalArguments)}.
* @param y The Y value calculated by {@link #calculateY(TidalArguments)}
* @param s The S value calculated by
         {@link #calculateS(TidalArguments, double, double)}
* @return The CIP/CEO to GCRS rotation transform.
***************************************************************************/
public Rotation calculateRotation(double x, double y, double s) {

   // System.out.println("x="+x+" y="+y+" s="+s);

    /********************************
    * create the XY rotation matrix *
    ********************************/
    double x2 = x*x;
    double y2 = y*y;
    double r2 = x2+y2;
    double r = Math.sqrt(r2);
    double z = Math.sqrt(1.0 - r2);
    double a = 1.0 /(1.0 + z);
    double axy = a*x*y;

    double[][] matrix ={{1.0-a*x2,     -axy, x     },
                        {    -axy, 1.0-a*y2, y     },
                        {      -x,       -y, 1.0-a*r2}};

    /***********************************************
    * combine the S and XY rotations and copy the
    * results into this rotation
    ***********************************************/
    return new Rotation(new Rotation(s, Direction.Z_AXIS),
                        new Rotation(matrix));

} // end of constructor

} // end of PrecessionModel class
