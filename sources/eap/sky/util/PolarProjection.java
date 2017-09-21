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
* The azimuthal equidistant projection.
* This projection preserves directions and distances from the aspect point.
* This projection is commonly used for displaying a hemisphere, such as the
* "dome of the sky". Note that the south pole maps to a circle in this
* projection.
****************************************************************************/
public class PolarProjection extends Projection {



/****************************************************************************
* Create a new projection. We only need one instance of this class, so use
* {@link Projection#POLAR} instead.
****************************************************************************/
protected PolarProjection() {

   double radius = Math.PI;

    border = new Ellipse2D.Double(-radius, -radius, 2.0*radius, 2.0*radius);

    Direction minus_z = Direction.Z_AXIS.oppositeDirection();
    Arc cut = new Arc(minus_z, minus_z, minus_z);
    cuts.add(cut);
}

/****************************************************************************
*
****************************************************************************/
public Point2D getBorderIntersection(Line2D line) {

    /******************************************************
    * find the intersection if the segment was horizontal *
    ******************************************************/
    double y2 = line.ptLineDistSq(0.0, 0.0);
    double x2 = Math.PI*Math.PI - y2;

    double x = Math.sqrt(x2);
    double y = Math.sqrt(y2);

    /************************************
    * find the direction of the segment *
    ************************************/
    double dx = line.getX2() - line.getX1();
    double dy = line.getY2() - line.getY1();

    double length = Math.sqrt(dx*dx + dy*dy);
    double sin = dy/length;
    double cos = dx/length;

    /*********
    * rotate *
    *********/
    double xp =  cos*x + sin*y;
    double yp = -sin*x + cos*y;

    return new Point2D.Double(xp, yp);


} // end of getBorderIntersection

/****************************************************************************
*
****************************************************************************/
public  Point2D project(Direction  dir) {


    double[] vec = dir.unitVector();

//    System.out.println();
//    System.out.println(rotation.rotate(coord));
   // System.out.println("vec: "+vec[0]+" "+vec[1]+" "+vec[2]);

    double r = Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1]);
    if(r==0.0) {
        if(vec[2]>0.0) return new Point2D.Double(0.0, 0.0);
        else           return null;
    }

    double factor = Math.toRadians(90.0 - dir.getLatitude())/r;
    
    Point2D point = new Point2D.Double(vec[0]*factor, vec[1]*factor);

   // System.out.println("Polar: "+coord+" -> "+point);


    return point;

} // end of project method


/*******************************************************************************
*
*******************************************************************************/
public Direction unproject(Point2D point) {

    double x = point.getX();
    double y = point.getY();

    double r = Math.sqrt(x*x+y*y);

    if(r > Math.PI) System.out.println("r="+r);
    if(r > Math.PI) return null;
    double z = Math.cos(r);

  //  System.out.println("r="+r+" z="+z);

    if(r==0) return new Direction(0, 0, 1);

    double factor = Math.sqrt(1-z*z)/r;

    x *= factor;
    y *= factor;

    return new Direction(x, y, z);


} // end of unproject

/*******************************************************************************
*
*******************************************************************************/
// public Rectangle2D getBounds() {
// 
//     return new Rectangle2D.Double(-Math.PI, -Math.PI, 2.0*Math.PI, 2.0*Math.PI);
// 
// }

} // end of PolarProjection class
