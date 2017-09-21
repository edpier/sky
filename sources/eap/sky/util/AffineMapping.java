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

import java.awt.*;
import java.awt.geom.*;

/***************************************************************************
* A wrapper around an AffineTransform.
***************************************************************************/
public class AffineMapping extends Mapping {

AffineTransform trans;

/***************************************************************************
* Create a new mapping which will use a copy of the given AffineTransform
***************************************************************************/
public AffineMapping(AffineTransform trans) {

    this.trans = (AffineTransform)trans.clone();

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public Point2D map(Point2D point) {

//     System.out.println("Affine mapping "+point);
//     System.out.println("            to "+trans.transform(point, null));

    return trans.transform(point, null);

} // end of map method

/***************************************************************************
*
***************************************************************************/
public Shape map(Shape shape, double accuracy) {

    return trans.createTransformedShape(shape);

} // end of map shape method

/***************************************************************************
*
***************************************************************************/
protected Mapping createInverse() {


    try { return new AffineMapping(trans.createInverse()); }
    catch(NoninvertibleTransformException e) {}

    return null;


} // end of createInverse method

/***************************************************************************
*
***************************************************************************/
public boolean equals(Object o) {

    if(o instanceof IdentityMapping) return trans.isIdentity();
    if(o instanceof AffineMapping) {
        AffineMapping map = (AffineMapping)o;
        return trans.equals(map.trans);
    } else {
        return false;
    }
} // end of equals method

} // end of Mapping method
