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
import java.awt.*;
import java.awt.geom.*;

/****************************************************************************
* Represents a general projection from a sphere to a plane.
* Subclasses must all use the pole as the aspect point. Furthermore, the
* scale at the aspect point must be one radian per pixel.
* This is an abstract superclass. Subclass implement individual types of
* projections. This class has instances of some of these subclasses as
* static variables.
****************************************************************************/
public abstract class Projection {

/** An instance of {@link Aitoff} **/
public static final Projection AITOFF   = new Aitoff();

/** An instance of {@link PolarProjection} **/
public static final Projection POLAR    = new PolarProjection();

/** An instance of {@link TangentPlane} **/
public static final Projection TANGENT  = new TangentPlane();

/** An instance of {@link HammerProjection} **/
public static final Projection HAMMER  = new HammerProjection();

Shape border;
java.util.List<Arc> cuts;
java.util.List<Arc> read_only_cuts;


/***************************************************************************
* Create a new projection.
***************************************************************************/
protected Projection() {

    cuts = new ArrayList<Arc>();
    read_only_cuts = Collections.unmodifiableList(cuts);

} // end of constructor

/****************************************************************************
* Return the shape in the plane corresponding to the cuts made in the sphere
* in order to transform it to a plane.
* @return the edge of the region in the plane corresponding to points on the
* sphere.
****************************************************************************/
public Shape getBorder() { return border; }

/****************************************************************************
*
****************************************************************************/
//public abstract Point2D getBorderIntersection(Line2D line);

/*****************************************************************************
* Returns a list of the cuts made in the sphere required to stretch it
* onto a plane. Each cut corresponds to part of the border of the transform.
* A cut is represented by an {@link Arc} object. In general a point on
* a cut corresponds to more than one point in the plane.
*****************************************************************************/
public java.util.List<Arc> getCuts() { return read_only_cuts; }


/****************************************************************************
* Calculate the point on the projected plane corresponding to given
* sperical coordinates.
* @param dir a direction toward a point on a sphere.
* @return the corresponding point on the plane.
***************************************************************************/
public abstract Point2D project(Direction dir);


/***************************************************************************
* Calculate the sperical coordinates corresponding to a point on the
* projected plane.
* @param point A point in the projected plane.
* @return the corresponding spherical coordinates or null, if there is no
* corresponding direction.
***************************************************************************/
public abstract Direction unproject(Point2D point);

/***************************************************************************
* Project a point on an arc. This method is used for transforming
* {@link Arc}s which cross a cut. If the point lies on a cut, then this method
* gives extra information which can be used to determine which of the multiple
* corresponding projected points to return.
* This method ignores the additional information, but subclasses should
* override this behavior.
* @param dir a point on the sphere
* @param arc an arc containing the point.
* @param place one of {@link Arc#BEGINNING}, {@link Arc#MIDDLE}, or
* {@link Arc#END}, indicating where onthe arc the point falls.
***************************************************************************/
public Point2D project(Direction dir, Arc arc, int place) {

    return project(dir);

} // end of projectEndpoint method

/***************************************************************************
* Unprojects a shape back onto the sphere.
* @return the polygon on the sphere corresponding to an arbitrary plane shape.
***************************************************************************/
public ArcPath unproject(Shape shape) {

    /*****************************************
    * we need to remember the initial point
    * so that we can close to it
    *****************************************/
    Point2D start_point = null;
    Direction start_dir = null;
    Arc start_arc = null;

    Point2D point0 = null;
    Direction dir0 = null;
    Arc arc0 = null;
    for(PathIterator it = shape.getPathIterator(null);
        !it.isDone(); it.next()) {

        /*******************************
        * get the current path segment *
        *******************************/
        double[] coord = new double[6];
        int type = it.currentSegment(coord);

        Point2D point1;
        Direction dir1;

        if(type == PathIterator.SEG_MOVETO) {
            /**********
            * move to *
            **********/
            point0 = new Point2D.Double(coord[0], coord[1]);
            dir0 = unproject(point0);
        //  System.out.println("move to point0="+point0);

            if(start_point == null) {
                start_point = point0;
                start_dir   = dir0;
            }

        } else if(type ==  PathIterator.SEG_LINETO ||
                  type == PathIterator.SEG_CLOSE      ) {
             if(type ==  PathIterator.SEG_LINETO) {
                /**********
                * line to *
                **********/
                point1 = new Point2D.Double(coord[0], coord[1]);
   // System.out.println("line to point1="+point1);
                dir1 = unproject(point1);
             } else {
                 /********
                 * close *
                 ********/
                 point1 = start_point;
                 dir1 = start_dir;
             }

            /***********
            * midpoint *
            ***********/
            double mid_x = 0.5*(point0.getX()+point1.getX());
            double mid_y = 0.5*(point0.getY()+point1.getY());
            Point2D mid_point = new Point2D.Double(mid_x, mid_y);
            Direction mid_dir = unproject(mid_point);

            /*****************
            * create the arc *
            *****************/
            Arc arc = new Arc(dir0, dir1,
                              Arc.findNormal(dir0, mid_dir, dir1));

            if(start_arc == null) start_arc = arc;
            if(arc0 != null) arc0.connectBefore(arc);
            arc0 = arc;

            if(type == PathIterator.SEG_CLOSE) {
                arc.connectBefore(start_arc);
            }

            /**************
            * increment *
            ************/
            point0 = point1;
            dir0 = dir1;

        } else {
            System.out.println("unsupported segment type "+type);
        }

    } // end of loop over segments

   // return new ArcPath(start_arc);

    return new ArcPath(start_arc);

} // end of unproject shape method

/*****************************************************************************
*
*****************************************************************************/
protected Mapping createOptimizedReprojection(Transform trans,
                                              Projection reproject) {

    return null;

} // end of createOptimizedReprojection method

/*****************************************************************************
*
*****************************************************************************/
public final Mapping createReprojection(Transform trans, Projection reproject) {

    Mapping map = createOptimizedReprojection(trans, reproject);
    if(map == null) map = new Reprojection(this, trans, reproject);

    return map;
    
} // end of createReprojection method

} // end of Projection class
