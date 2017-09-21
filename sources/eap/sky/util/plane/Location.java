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

import java.awt.geom.*;
import java.io.*;

/*************************************************************************
* Wraps together a {@link Point2D} and a {@link PlaneSegment}.
* We need this class because we don't always know the segment that a
* transformed point will lie in until we do the transform. So we need to
* be able to return both aty the same time.
*************************************************************************/
public class Location implements Serializable {

PlaneSegment seg;
Point2D point;

/*************************************************************************
* Create a new Location.
* @param seg The segment.
* @param point The point.
*************************************************************************/
public Location(PlaneSegment seg, Point2D point) {

    this.seg = seg;
    this.point = point;

} // end of Location method


/*************************************************************************
* Create a new Location.
* @param seg The segment.
*************************************************************************/
public Location(PlaneSegment seg, double x, double y) {

    this(seg, new Point2D.Double(x,y));

} // end of Location method

/*************************************************************************
* Returns the segment.
* @return The segment.
*************************************************************************/
public PlaneSegment getSegment() { return seg; }

/*************************************************************************
* Returns the point
* @return The point.
*************************************************************************/
public Point2D getPoint() { return point; }

/************************************************************************
*
************************************************************************/
public Location transformTo(PlaneCoordinates coord, ParameterSet params) {

    return seg.transformTo(coord, point, params);

} // end of transformTo method

/************************************************************************
*
************************************************************************/
public String toString() {

    return "Location: "+seg.getUniqueName()+
           " ("+point.getX()+", "+point.getY()+")";

} // end of toString method

} // end of Location class
