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

import eap.sky.time.*;
import eap.sky.util.*;
import eap.sky.util.coordinates.*;

import java.awt.geom.*;

/******************************************************************************
* Represents a plane projection of some spherical coordinates system
******************************************************************************/
public class Plane {

Coordinates coord;
Projection projection;
Mapping to_pixels;

/***************************************************************************
*
***************************************************************************/
public Plane(Coordinates coord, Projection projection,
             Mapping to_pixels) {

    this.coord = coord;
    this.projection = projection;
    this.to_pixels = to_pixels;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public Direction toDirection(Point2D point) {

    return projection.unproject(to_pixels.invert().map(point));

} // end of unproject

/***************************************************************************
*
***************************************************************************/
public Point2D toPixels(Direction dir) {

    Point2D point = projection.project(dir);
    if(point == null) return null;
    return to_pixels.map(point);

} // end of project method

/***************************************************************************
*
***************************************************************************/
public Mapping getMappingTo(Plane plane, PreciseDate time) {

    Transform trans = coord.getTransformTo(plane.coord, time);

    Mapping reprojection = projection.createReprojection(trans, plane.projection);


    return to_pixels.invert().combineWith(reprojection)
                             .combineWith(plane.to_pixels);


} // end of getMappingTo method

/***************************************************************************
*
***************************************************************************/
public Mapping getMappingToPixels() { return to_pixels; }


/***************************************************************************
*
***************************************************************************/
public Projection getProjection() { return projection; }

/***************************************************************************
*
***************************************************************************/
public Coordinates getCoordinates() { return coord; }


} // end of Plane class
