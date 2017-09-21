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

package eap.sky.util.plane;

import eap.sky.util.*;

import java.awt.geom.*;

/*************************************************************************
* A parameter cache for a {@link RotatorTransform}.
* We use this to store the {@link AffineTransform} which describes the
* rotation for a given rotator position. This way we don't have to
* recalculate since and cosines every time we apply the transform with
* the same rotator position.
*************************************************************************/
public class RotatorCache implements ParamCache {

boolean inverse;
double offset;
Point2D orig_center;
Point2D trans_center;

AffineTransform trans;

/*************************************************************************
* Create a new cache.
* @param orig_center The center of the rotation in the original coordinates.
* @param trans_center The center of the rotation in the transformed coordinates.
* @param inverse If true, then this is actually the inverse rotator transform.
*************************************************************************/
public RotatorCache(Point2D orig_center, Point2D trans_center,
                    boolean inverse, double offset) {

    this.orig_center = orig_center;
    this.trans_center = trans_center;
    this.inverse = inverse;
    this.offset = offset;

} // end of cnstructor

/*************************************************************************
* Generate the {@link AffineTransform} corresponding to a given rotator
* position.
* @param angle The rotator position in degrees.
*************************************************************************/
public void update(double angle) {

    /*****************************************
    * move the original center to the origin *
    *****************************************/
    AffineTransform trans =
    AffineTransform.getTranslateInstance(-orig_center.getX(),
                                         -orig_center.getY() );


    /**************************
    * rotate about the origin *
    **************************/
    if(inverse) trans.rotate(-Math.toRadians(angle-offset));
    else        trans.rotate( Math.toRadians(angle+offset));

    /************************************
    * move the origin to the new center *
    ************************************/
    trans.translate(trans_center.getX(), trans_center.getY());

    this.trans = trans;

} // end of update method

/*************************************************************************
* Apply the cached transform.
* @param point The original point.
* @param result The transformed point. This coordinates of this point are
* changed to results.
*************************************************************************/
public void transform(Point2D point, Point2D result) {

    trans.transform(point, result);

} // end of transform method

/**************************************************************************
*
**************************************************************************/
public Mapping getMapping() {

    return new AffineMapping(trans);

} // end of getMapping method

} // end of RotatorCache method
