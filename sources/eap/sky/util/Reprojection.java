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

import java.awt.geom.*;

/****************************************************************************
*
****************************************************************************/
public class Reprojection extends Mapping {

Projection unproject;
Transform  transform;
Projection reproject;

/****************************************************************************
*
****************************************************************************/
public Reprojection(Projection unproject, Transform transform,
                           Projection reproject) {

    this.unproject = unproject;
    this.transform = transform;
    this.reproject = reproject;

} // end of constructor

/****************************************************************************
*
****************************************************************************/
public Point2D map(Point2D point) {


    Direction dir = unproject.unproject(point);
    dir = transform.transform(dir);
    return reproject.project(dir);

} // end of map method

/****************************************************************************
*
****************************************************************************/
protected Mapping createInverse() {

    return new Reprojection(reproject, transform.invert(), unproject);

} // end of invert method

} // end of PlaneToPlaneMapping class
