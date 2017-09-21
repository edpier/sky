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
public class Scaling extends PlaneTransform {

double fx;
double fy;

double dx;
double dy;

/*************************************************************************
*
*************************************************************************/
public Scaling(double fx, double dx, double fy, double dy) {

//System.out.println("making translation");

    this.fx = fx;
    this.fy = fy;
    this.dx = dx;
    this.dy = dy;

} // end of constructor

/*************************************************************************
*
*************************************************************************/
public void transform(Point2D point, Point2D result, ParameterSet params) {

    result.setLocation(point.getX()*fx+dx, point.getY()*fy+dy);

} // end of transform method



/*************************************************************************
*
*************************************************************************/
public Mapping getMapping(ParameterSet params) {

    return new AffineMapping(new AffineTransform(fx, 0.0, 0.0, fy, dx, dy));
   // return new AffineMapping(AffineTransform.getScaleInstance(fx, fy));

} // end of getMapping method

/*************************************************************************
* Invert this transform.
* @return a transform which is the inverse of this one.
*************************************************************************/
public PlaneTransform invert() {

    return new Scaling(1.0/fx, -dx/fx, 1.0/fy, -dy/fy);

} // end of invert method

} // end of Translation class
