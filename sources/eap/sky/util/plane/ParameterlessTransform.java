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

/***************************************************************************
*
***************************************************************************/
public abstract class ParameterlessTransform extends PlaneTransform {

/***************************************************************************
*
***************************************************************************/
public final void transform(Point2D point, Point2D result,
                               ParameterSet params) {

    transform(point, result);

} // end of transform method

/***************************************************************************
*
***************************************************************************/
public abstract void transform(Point2D point, Point2D result);

/***************************************************************************
*
***************************************************************************/
public final Mapping getMapping(ParameterSet params) { return getMapping(); }

/***************************************************************************
*
***************************************************************************/
public Mapping getMapping() {

    return new TransformMapping();

} // end of getMapping method

/***************************************************************************
*
***************************************************************************/
private class TransformMapping extends Mapping {

/***************************************************************************
*
***************************************************************************/
public Mapping createInverse() {


    ParameterlessTransform inverse = (ParameterlessTransform)
                              ParameterlessTransform.this.invert();
    return inverse.getMapping();


} // end of createInverse method

/***************************************************************************
*
***************************************************************************/
public Point2D map(Point2D point) {

    Point2D.Double result = new Point2D.Double(0,0);
    transform(point, result);

    return result;

} // end of map method


} // end of TransformMapping inner class

} // end of ParameterlessTransform class