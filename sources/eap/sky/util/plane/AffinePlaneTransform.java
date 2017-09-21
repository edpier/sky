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

import java.awt.Shape;
import java.awt.geom.*;

/*************************************************************************
*
*************************************************************************/
public class AffinePlaneTransform extends PlaneTransform {

AffineTransform trans;


/*************************************************************************
*
*************************************************************************/
public AffinePlaneTransform(AffineTransform trans) {

    this.trans = new AffineTransform(trans);


} // end of constructor

/*************************************************************************
*
*************************************************************************/
public void transform(Point2D point, Point2D result, ParameterSet params) {

    trans.transform(point, result);

} // end of transform method

/*************************************************************************
*
*************************************************************************/
public Mapping getMapping(ParameterSet params) {

    return new AffineMapping(trans);

} // end of getMapping method

/*************************************************************************
* Invert this transform.
* @return a transform which is the inverse of this one.
*************************************************************************/
public PlaneTransform invert() {

    try { return new AffinePlaneTransform(trans.createInverse()); }
    catch(NoninvertibleTransformException e) { return null; }

} // end of invert method

/*************************************************************************
*
*************************************************************************/
public String toString() { return trans.toString(); }

} // end of Translation class
