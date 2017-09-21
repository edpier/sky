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
import java.io.*;

/*************************************************************************
* A generic transform from one segment to another. This is an abstract
* class. Subclasses implement particular types of transforms.
*************************************************************************/
public abstract class PlaneTransform implements Serializable {

/*************************************************************************
* Transform a point.
* @param point The original point.
* @param params The parameters for this transform which can vary from
* observation to observation.
* @return The transformed point.
*************************************************************************/
public Point2D transform(Point2D point,  ParameterSet params) {

    Point2D result = new Point2D.Double();
    transform(point, result, params);

    return result;

} // end of transform method

/*************************************************************************
*
*************************************************************************/
// protected static Point2D result(Point2D result, double x, double y) {
// 
//     if(result == null) return new Point2D.Double(x,y);
//     else {
//         result.setLocation(x,y);
//         return result;
//     }
// 
// } // end of result method



/*************************************************************************
*
*************************************************************************/
public abstract void transform(Point2D point, Point2D result,
                               ParameterSet params);

/*************************************************************************
*
*************************************************************************/
public abstract Mapping getMapping(ParameterSet params);

/*************************************************************************
* Invert this transform.
* @return a transform which is the inverse of this one.
*************************************************************************/
public abstract PlaneTransform invert();



/*************************************************************************
* Transform a shape.
* @param shape The original shape
* @param params The parameters for this transform which can vary from
* observation to observation.
* @return The transformed shape.
*************************************************************************/
public Shape transform(Shape shape, ParameterSet params) {

    GeneralPath path = new GeneralPath();

    for(PathIterator it = shape.getPathIterator(null);
        !it.isDone(); it.next()) {

        double[] coord = new double[6];
        int type = it.currentSegment(coord);

        /****************************
        * transform the coordinates *
        ****************************/
        int npoints;
        if(type ==PathIterator. SEG_LINETO ||
           type == PathIterator.SEG_MOVETO   ) {
            /************
            * one point *
            ************/
            npoints = 1;
        } else if( type == PathIterator.SEG_CLOSE) {
            npoints = 0;
        } else {
            System.err.println("unsupported segment type");
            npoints = 0;
        }

        /*******************
        * loop over points *
        *******************/
        for(int i=0; i< npoints; ++i) {
            Point2D point = new Point2D.Double(coord[i*2], coord[i*2+1]);
            point = transform(point, params);
            coord[i*2  ] = point.getX();
            coord[i*2+1] = point.getY();
        }

        /******************************************
        * add the transformed segment to the path *
        ******************************************/
        if(type == PathIterator.SEG_MOVETO) {
            path.moveTo((float)coord[0], (float)coord[1]);
        } else if(type == PathIterator.SEG_LINETO) {
            path.lineTo((float)coord[0], (float)coord[1]);
        } else if(type == PathIterator.SEG_CLOSE) {
            path.closePath();
        }

    } // end of loop over segments

    return path;

} // end of transform shape method


} // end of PlaneTransform class
