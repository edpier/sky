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
import javax.swing.event.*;

/*************************************************************************
* The transform corresponsing to an instrument rotator.
* This transform takes a {@link TransformParameter} specifying the
* position of the rotator.
*************************************************************************/
public class RotatorTransform extends PlaneTransform {

boolean inverse;
double offset;
Point2D orig_center;
Point2D trans_center;
String param_name;

/*************************************************************************
* Create a new transform.
* @param orig_center The center of the rotation in the original coordinates.
* @param trans_center The center of the rotation in the transformed coordinates.
* @param param_name The name of the rotator position parameter.
* @param inverse If true, then this is actually the inverse rotator transform.
*************************************************************************/
public RotatorTransform(Point2D orig_center, Point2D trans_center,
                        String param_name, boolean inverse, double offset) {

    this.orig_center = orig_center;
    this.trans_center = trans_center;
    this.param_name = param_name;
    this.inverse = inverse;
    this.offset = offset;

} // end of constructor

/**************************************************************************
*
**************************************************************************/
private RotatorCache getCache(ParameterSet params) {

    /********************
    * get our parameter *
    ********************/
    TransformParameter param = params.getParameter(param_name);
    if(param==null) {
        throw new IllegalArgumentException("no parameter "+param_name+
                                           " in param set");
    }

    /*******************************
    * get the calculated transform *
    *******************************/
    RotatorCache cache = (RotatorCache)param.getCache(this);
    if(cache==null) {
        cache = new RotatorCache(orig_center, trans_center, inverse, offset);
        param.setCache(this, cache);
    }

    return cache;

} // end of updateCache method

/*************************************************************************
* Transform a point.
* @param point The original point
* @param result The transformed point. This method sets this point to
* the transformed coordinates
* @param params The parameter set from which we will get the rotator position.
* @throws IllegalArgumentException if the parameter set does not contain
* the rotator position.
*************************************************************************/
public void transform(Point2D point, Point2D result, ParameterSet params) {

    /******************************************
    * the cache knows how to do the transform *
    ******************************************/
    getCache(params).transform(point, result);

} // end of transform method

/*************************************************************************
*
*************************************************************************/
public Mapping getMapping(ParameterSet params) {

    return getCache(params).getMapping();

} // end of getMapping method


/*************************************************************************
* Invert this transform.
* @return A new transform which is the inverse of this one.
*************************************************************************/
public PlaneTransform invert() {

    return new RotatorTransform(trans_center, orig_center, param_name,
                                !inverse, -offset);
} // end of invert method

} // end of RotatorTransform class
