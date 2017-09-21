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
* Represents a mapping from one spherical coordinate system to another.
*****************************************************************************/
public abstract class Transform {

/***************************************************************************
* Performs a mapping from one direction to another.
* Subclasses need to implement this method to define an actual transform.
* @param dir The original coordinates
* @return The transformed coordinates
***************************************************************************/
public abstract Direction transform(Direction dir);

/**************************************************************************
* Returns a transform which is the inverse of this one, or null if the
* transform is uninvertable. This method always returns null. Subclasses
* should override it if they can implement an inverse.
* @return null
**************************************************************************/
public Transform invert() { return null; }

/***************************************************************************
* Returns the transform which is the same as first applying this transform and
* then the given one. This method returns a {@link CompositeTransform}.
* Subclasses should override this if they are able to truely merge a pair
* of transforms.
* @param trans The transform to apply after this one.
* @return A {@link CompositeTransform}
***************************************************************************/
public Transform combineWith(Transform trans) {

    return new CompositeTransform(this, trans);
}

/***************************************************************************
* Transform a direction in the plane of the celestial sphere. This is useful
* e.g. for calculating the paralactic angle, or the setting of an instrument
* rotator on an az/alt mount when sidereal tracking.
* In the current implementation, we find a point one degree from "dir" in the
* direction of "angle". We then transform this point and "dir", and
* calculate the new angle with respect to the pole of the new coordinate system.
* @param dir The point in the sky with respect to which the direction is
* measured.
* @param angle an angle measured about dir with zero being the direction
* toward the pole.
***************************************************************************/
public Angle transform(Direction dir, Angle angle) {

  // angle = angle.negative();

    /***************************************************
    * get the direction which is offset by one degree
    * toward the pole from the given direction
    ***************************************************/
    Direction offset;
    if(dir.equals(Direction.Z_AXIS)) {
        offset = null;
    } else {
        Rotation rot1 = new Rotation(angle, dir);

        Direction axis = dir.perpendicular(Direction.Z_AXIS);
        Rotation rot2 = new Rotation(new Angle(1.0), axis);

        Rotation rot = (Rotation)rot2.combineWith(rot1);
        offset = rot.transform(dir);
    }

//     System.out.println("pretrans");
//     System.out.println("pretrans="+angle+" "+dir.angleBetween(Direction.Z_AXIS, offset));

//     if(Math.abs(dir.angleBetween(Direction.Z_AXIS, offset).minus(angle).getDegrees(-180.0)) > 1e-12) {
//         throw new IllegalStateException("bad thing");
//     }

  //  System.out.println("pretrans="+angle+" "+dir.angleBetween(Direction.Z_AXIS, offset));
//System.out.println("posttrans--------------");
    /************************************************************
    * transform the original direction and the offset direction *
    ************************************************************/
    offset = transform(offset);
    dir    = transform(dir);

  //  System.out.println(offset);
  //  System.out.println(dir);

    return dir.angleBetween(Direction.Z_AXIS, offset);

//     Direction norm1 = dir.perpendicular(offset);
//     Direction norm2 = dir.perpendicular(Direction.Z_AXIS);
//
//     Angle angle1 = norm1.angleBetween(norm2);
//     Angle angle2 = norm1.perpendicular(norm2).angleBetween(dir);
//
//     if(angle2.getCos() < 0.0) {
//         angle1 = angle1.negative();
//     }
//
//     return angle1.negative();

} // end of transform angle method

} // end of Transform class
