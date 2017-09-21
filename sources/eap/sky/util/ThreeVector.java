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

import java.io.*;

/*************************************************************************
* Represents a three dimensional vector of arbitrary length.
* This class is implemented to hold either a direction/magnitude representation
* of the vector, or a set of cartesian coordinates or both,
* converting as needed.
*************************************************************************/
public class ThreeVector implements Serializable {

public static final ThreeVector ZERO = new ThreeVector(0.0, 0.0, 0.0);

double[] vec;
Direction dir;
double length;

/***************************************************************************
* Create a new vector by specifying its direction and length.
* @param dir the direction of the vector
* @param length The magnitude of the vector
***************************************************************************/
public ThreeVector(Direction dir, double length) {

    this.dir = dir;
    this.length = length;
}

/***************************************************************************
* Creates a unit vector pointing in the given direction
* @param dir the direction of the unit vector.
***************************************************************************/
public ThreeVector(Direction dir) {

    this(dir.unitVector());

    this.dir = dir;
    this.length = 1;



} // end of unit vector constructor

/***************************************************************************
* Create a new vector, specifying a set of cartesian coordinates.
* @param x the cartesian X coordinate.
* @param y the cartesian Y coordinate.
* @param z the cartesian Z coordinate.
***************************************************************************/
public ThreeVector(double x, double y, double z) {

    vec = new double[3];
    vec[0] = x;
    vec[1] = y;
    vec[2] = z;

}

/***************************************************************************
* Create a new vector from an array of cartesian coordinates
* @param v An Array of cartesian coordinates ith v[0] = x, v[1] = y,
* and v[2] = z.
* @throws IllegalArgumentException if the array does not have dimension 3.
***************************************************************************/
public ThreeVector(double[] v) {

    if(v.length != 3) throw new IllegalArgumentException("Not a 3 vector");

    vec = new double[3];
    vec[0] = v[0];
    vec[1] = v[1];
    vec[2] = v[2];

} // end of constructor from an array

/***************************************************************************
* Ensures that we have an internal representation of the vector in cartesian
* coordinates.
***************************************************************************/
private void forceCartesian() {

    if(vec == null) {
        vec = new double[3];
        vec[0] = dir.getX() * length;
        vec[1] = dir.getY() * length;
        vec[2] = dir.getZ() * length;
    }

} // end of vec method

/***************************************************************************
* Ensures that we have an internal representation of the vector in
* direction/magnitude form.
***************************************************************************/
private void forcePolar() {

    if(dir==null) {
        length = Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
        double norm = 1.0/length;
        dir = new Direction(vec[0]*norm, vec[1]*norm, vec[2]*norm);
    }

} // end of dir method

/***************************************************************************
* returns a new array containing the x, y, and z cartesian components of the
* vector.
* @return The array {x, y, z}.
***************************************************************************/
public double[] getComponents() {

    forceCartesian();

    double[] copy = {vec[0], vec[1], vec[2]};
    return copy;
}

/***************************************************************************
* Returns the cartesian X coordiate of the vector.
* @return The cartesian X coordiate of the vector.
***************************************************************************/
public double getX() {
    forceCartesian();
    return vec[0];
}

/***************************************************************************
* Returns the cartesian Y coordiate of the vector.
* @return The cartesian Y coordiate of the vector.
***************************************************************************/
public double getY() {
    forceCartesian();
    return vec[1];
}

/***************************************************************************
* Returns the cartesian Z coordiate of the vector.
* @return The cartesian Z coordiate of the vector.
***************************************************************************/
public double getZ() {
    forceCartesian();
    return vec[2];
}

/***************************************************************************
* Returns the direction in which the vector points.
* @return The direction in which the vector points.
***************************************************************************/
public Direction getDirection() {

    forcePolar();
    return dir;
}

/***************************************************************************
* Returns the length (magnitude) of the vector.
* @return The length (magnitude) of the vector.
***************************************************************************/
public double getLength() {

    forcePolar();
    return length;
}

/***************************************************************************
* Calculates the sum of this vector and another.
* @param v The vector to sum with this one.
* @return a new vector contsining the sum of this and another.
***************************************************************************/
public ThreeVector plus(ThreeVector v) {

    forceCartesian();
    v.forceCartesian();

    return new ThreeVector(vec[0] + v.vec[0],
                           vec[1] + v.vec[1],
                           vec[2] + v.vec[2]);

} // end of plus method

/***************************************************************************
* Calculates the difference of this vector and another.
* @param v The vector to subtract fro this one.
* @return A new vector representing this minus v.
***************************************************************************/
public ThreeVector minus(ThreeVector v) {

    forceCartesian();
    v.forceCartesian();

    return new ThreeVector(vec[0] - v.vec[0],
                           vec[1] - v.vec[1],
                           vec[2] - v.vec[2]);

} // end of plus method

/***************************************************************************
* Multiply this vector by a scalar
* @param scalar The factor by which to multiply this vector.
***************************************************************************/
public ThreeVector times(double scalar) {

    if(vec != null) {
        /************************
        * cartesian coordinates *
        ************************/
        return new ThreeVector(vec[0]*scalar, vec[1]*scalar, vec[2]*scalar);
    } else {
        /********************
        * polar coordinates *
        ********************/
        return new ThreeVector(dir, length * scalar);
    }


} // end of times method

/***************************************************************************
* Calculates the vector cross product.
* @param v The vector to be multipliesd by this one.
* @return A new vector representing this cross v.
***************************************************************************/
public ThreeVector cross(ThreeVector v) {

    /**********************************************
    * calculate the cross product to get the sign *
    **********************************************/
    forceCartesian();
    v.forceCartesian();
    double[] vec1 =   vec;
    double[] vec2 = v.vec;

    double[] cross = new double[3];

    cross[0] = vec1[1]*vec2[2] - vec1[2]*vec2[1];
    cross[1] = vec1[2]*vec2[0] - vec1[0]*vec2[2];
    cross[2] = vec1[0]*vec2[1] - vec1[1]*vec2[0];

    return new ThreeVector(cross[0], cross[1], cross[2]);

} // end of cross method

/***************************************************************************
* Calculates the vector dot product.
* @param v The vector to be dotted into this one.
* return A new vectors representing this dot v.
***************************************************************************/
public double dot(ThreeVector v) {

    /**********************************************
    * calculate the cross product to get the sign *
    **********************************************/
    forceCartesian();
    v.forceCartesian();

    double[] vec1 =   vec;
    double[] vec2 = v.vec;

    return vec1[0]*vec2[0] + vec1[1]*vec2[1] + vec1[2]*vec2[2];

} // end of cross method

/***************************************************************************
*
***************************************************************************/
public double distanceSquared(ThreeVector v) {

    forceCartesian();
    v.forceCartesian();

    double[] vec1 =   vec;
    double[] vec2 = v.vec;

    double dx = vec1[0] - vec2[0];
    double dy = vec1[1] - vec2[1];
    double dz = vec1[2] - vec2[2];

    return dx*dx + dy*dy + dz*dz;

} // end of distanceSquared method

/***************************************************************************
* Apply a rotation transform to this vector
* @param rotation The transform to apply.
* @return The vector in the rotated coordinates
***************************************************************************/
public ThreeVector rotate(Rotation rotation) {

    forcePolar();
    return new ThreeVector(rotation.transform(dir), length);
}

/***************************************************************************
* check if two vectors are equal.
* Note that if two equal vectors were specified using different conventions
* (cartesian vs. direction/magnitude), then roundoff error might cause this
* method to thing they are slightly different.
* @param o A ThreeVector object to be compar4ed with this one.
* @return true if the two vectors are equal.
* @throws ClassCastException if the arguyment is not a ThreeVector.
***************************************************************************/
public boolean equals(Object o) {

    ThreeVector v = (ThreeVector)o;

    return this.minus(v).getLength() == 0;

}

/***************************************************************************
* Returns a string representation of the vector.
* @return A string representation of the vector.
***************************************************************************/
public String toString() {

    forceCartesian();
    forcePolar();
    return "ThreeVector: x="+vec[0]+" y="+vec[1]+" z="+vec[2]+" length="+length;
}

} // end of Position class
