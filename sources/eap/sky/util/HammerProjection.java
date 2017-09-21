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

/***************************************************************************
*
***************************************************************************/
public  class HammerProjection extends Projection {

/***************************************************************************
*
***************************************************************************/
protected HammerProjection() {

    double root2 = Math.sqrt(2.0);

    border = new Ellipse2D.Double(-2.0*root2, -root2, 4.0*root2, 2.0*root2);

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public  Point2D project(Direction dir) {

    double x = dir.getX();
    double y = dir.getY();
    double z = dir.getZ();

    /****************************************
    * compress everything into a hemisphere *
    ****************************************/
    if(Math.abs(y) < 1.0) {
        double angle = 0.5*Math.atan2(x,z);

        double scale = Math.sqrt(1.-y*y);
        x = scale*Math.sin(angle);
        z = scale*Math.cos(angle);
    } else {
        /******************************
        * these should be zero anyway *
        ******************************/
        x = 0.0;
        z = 0.0;

        if(y<0.0) y = -1.0;
        else      y =  1.0;

    }

  //  System.out.println("pre scaling x="+x+" y="+y);


    double r = Math.sqrt(x*x+y*y);
    double factor = 1.0;
    if(r != 0.0) {
        double angle2 = Math.atan2(r, z);
        factor = 2.0*Math.sin(angle2*0.5)/r;
    }

    /********************************
    * strecth by a factor of 2 in X *
    ********************************/
    return new Point2D.Double(2.0*x*factor, y*factor);

} // end of project method

/***************************************************************************
*
***************************************************************************/
public Direction unproject(Point2D point) {

    /*********************************
    * compress by a factor of 2 in X *
    *********************************/
    double x = point.getX()*0.5;
    double y = point.getY();

    /******************************
    * unproject to the hemisphere *
    ******************************/
    double r = Math.sqrt(x*x+y*y);

    double factor = 1.0;
    if(r != 0.0) {
        double angle2 = 2.0*Math.asin(0.5*r);
        factor = Math.sin(angle2)/r;
    }

    x *= factor;
    y *= factor;

     //   System.out.println("after unscaling x="+x+" y="+y);

    double z = Math.sqrt(1.0 - x*x - y*y);

    /*****************************
    * stretch to the full sphere *
    *****************************/
    if(Math.abs(y) < 1.0) {
        double angle = 2.0*Math.atan2(x,z);
        double scale = Math.sqrt(1.0-y*y);

        x = scale*Math.sin(angle);
        z = scale*Math.cos(angle);

    } else {
        x = 0.0;
        z = 0.0;

        if(y<0.0) y = -1.0;
        else      y =  1.0;
    }

    return new Direction(x,y,z);



} // end of unproject method

} // end of HammerProjection class
