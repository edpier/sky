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

import java.util.*;
import java.awt.geom.*;

/***********************************************************************
* A combination of other transforms.
***********************************************************************/
public class CompositePlaneTransform extends PlaneTransform {

List<PlaneTransform> transforms;

/***********************************************************************
* Create a new composite transform consisting of no transforms.
* Note that this method is protected because a valid CompositeTransform
* Must have at least one component transform to be valid.
***********************************************************************/
protected CompositePlaneTransform() {

    transforms = new ArrayList<PlaneTransform>();

}

/***********************************************************************
* Create a composite transform from a collection of transforms.
* @param transforms A collection of transforms to combine. Note this
* collection must have some inherent ordfering (e.g. {@link List} or
* {@link SortedSet}) in order for the results to be meaningful.
***********************************************************************/
public CompositePlaneTransform(Collection<PlaneTransform> transforms) {

    this.transforms = new ArrayList<PlaneTransform>(transforms);

}

/***********************************************************************
* Apply the component transforms in sucession.
* @param point The original point.
* @param params A set of runtime parameters that the component transforms
* might need.
***********************************************************************/
public void transform(Point2D point, Point2D result, ParameterSet params) {

    result.setLocation(point.getX(), point.getY());

    for(Iterator it =transforms.iterator(); it.hasNext(); ) {
        PlaneTransform trans = (PlaneTransform)it.next();

        trans.transform(result, result, params);

    } // end of loop over transforms


} // end of transform method

/************************************************************************
*
************************************************************************/
public Mapping getMapping(ParameterSet params) {

    Mapping map = Mapping.IDENTITY;

    for(Iterator it =transforms.iterator(); it.hasNext(); ) {
        PlaneTransform trans = (PlaneTransform)it.next();

       // System.out.println("    "+trans.getClass().getName());

        map = map.combineWith(trans.getMapping(params));

    } // end of loop over transforms

    return map;


} // end of getMapping method

/***********************************************************************
* Invert the transform. This inverts each transform and reverses their
* order.
* @return A new transform which is the inverse of this one.
***********************************************************************/
public PlaneTransform invert() {

    CompositePlaneTransform composite = new CompositePlaneTransform();

    /**************************************
    * reverse the order of the transforms *
    **************************************/
    List<PlaneTransform> reverse = new ArrayList<PlaneTransform>(transforms);
    Collections.reverse(reverse);

    /************************
    * invert each transform *
    ************************/
    for(Iterator it =reverse.iterator(); it.hasNext(); ) {
        PlaneTransform trans = (PlaneTransform)it.next();

        PlaneTransform inverse = trans.invert();
        if(inverse == null) return null;

        composite.transforms.add(inverse);

    } // end of loop over sub-transforms

    return composite;

} // end of invert method

} // end of CompositePlaneTransform class
