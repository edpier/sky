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
* Represents a rotation by an angle of zero. This may be used as a generic
* identity transform. This class should produce identical results to
* a general rotation about an angle of zero. However, it is optimized
* to avoid unnecessary computation. This allows you to allow
* aritrary transforms in your application, without incurring the computational
* burden of a transform when one is not needed.
****************************************************************************/
public class IdentityRotation extends Rotation {


/*****************************************************************************
* Creates a new identity rotation. This method is protected, because the
* preferred way to get one of these is from {@link Rotation#IDENTITY}.
*****************************************************************************/
protected IdentityRotation() {

    /*******************************************************************
    * set the inherited quaternion elements, even though
    * for the most part we won't use them. This is necessary
    * in order for equals to work symmetrically with generic rotations
    *******************************************************************/
    q[0] = 0.0;
    q[1] = 0.0;
    q[2] = 0.0;
    q[3] = 1.0;

} // end of constructor

/*****************************************************************************
* Returns this rotation.
* @return this rotation.
*****************************************************************************/
public Transform invert() { return this; }

/*****************************************************************************
* Returns the original direction.
* @return the original direction.
*****************************************************************************/
public Direction transform(Direction dir) { return dir; }

/*****************************************************************************
* Returns the given rotation.
* @param rot the rotation to combine with
* @return rot.
*****************************************************************************/
public Rotation combineWith(Rotation rot) { return rot; }

/*********************************************************************
* Returns the given transform.
* @param trans the transform to combine with
* @return trans.
*****************************************************************************/
public Transform combineWith(Transform trans) { return trans; }


/*****************************************************************************
* Returns the identity matrix. This has diagonal elements 1, and off diagonal
* elements 0.
* @return {{1,0,0}, {0,1,0}, {0,0,1}}.
*****************************************************************************/
public double[][] getMatrix() {

    double[][] rot = new double[3][3];

    rot[0][0] = 1.0;
    rot[1][1] = 1.0;
    rot[2][2] = 1.0;

    return rot;

} // end of getMatrix method

/*****************************************************************************
* Returns three zero Euler angles.
* @return new Euler(0.0, 0.0, 0.0)
*****************************************************************************/
public Euler getEulerAngles() {

    return new Euler(0.0, 0.0, 0.0);
}

/***************************************************************************
* Returns the Z axis, arbitrarily. Note that the axis of a null rotation
* is undefined.
***************************************************************************/
public Direction getAxis() {

    return Direction.Z_AXIS;


} // end of getAxis method

/***************************************************************************
* Returns zero
* @return 0
***************************************************************************/
public double getAngle() {

    return 0.0;


} // end of getAngle method

/*****************************************************************************
* Converts this transform to a string.
* @return a string representation of this object.
*****************************************************************************/
public String toString() {
    return "Identity Rotation";
}



} //end of IdentityRotation class
