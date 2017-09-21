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

import java.util.*;
import java.awt.Shape;
import java.awt.geom.*;

/*******************************************************************************
* A projection which maps from a sphere to a plane tangent to it at the
* aspect point. The mapping is done by continuing the direction from the center
* of the shpere until it intersects the plane. Note that points in the southern
* hemisphere do not correspond to any point in the plane.
*******************************************************************************/
public  class TangentPlane extends Projection {


/*******************************************************************************
* Create a new projection. We only need one instance of this clas,s so use
* {@link Projection#TANGENT} instead.
*******************************************************************************/
protected TangentPlane() {

    border = null;

}

/****************************************************************************
*
****************************************************************************/
public Point2D getBorderIntersection(Line2D line) { return  null; }

/*******************************************************************************
*
*******************************************************************************/
public  Point2D project(Direction  dir) {

    double[] vec = dir.getUnitVector();

    /*******************************************************************
    * return null if the coordinate is on the other side of the sphere *
    *******************************************************************/
    if(vec[2] < 0.0) return null;
    
    /**************************************************
    * return infinity if the point is 90 degrees away *
    **************************************************/
    if(vec[2] == 0.0 ) return null; //new InfinityPoint(-vec[0], vec[1]);


    double factor = 1./vec[2];
    
    if(factor> 100000) return null; //new InfinityPoint(-vec[0], vec[1]);

    return new Point2D.Double(vec[0]*factor, vec[1]*factor);

} // end of project method

/*******************************************************************************
*
*******************************************************************************/
public Direction unproject(Point2D point) {

    double[] vec = new double[3];
    vec[0] = point.getX();
    vec[1] = point.getY();
    vec[2] = 1.0;

    return new Direction(vec);



} // end of unproject method

/*****************************************************************************
*
*****************************************************************************/
// public ArcPath unproject(Shape shape) {
// 
// 
// 
// 
// } // end of unproject shape method



} // end of TangentPlane class

