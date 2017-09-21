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

package eap.sky.chart;

import eap.sky.util.*;
import eap.sky.util.coordinates.*;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;

/*************************************************************************
*
*************************************************************************/
public class CurveItem implements ChartItem {

SphereCurve curve;
Coordinates coord;

Color color;
Stroke stroke;

double t0;
double step;
Point2D point0;

double accuracy2;
double too_good2;

java.util.List<Point2D> list;
java.util.List<Point2D> new_list;

double value;
boolean longitude;

/*************************************************************************
*
*************************************************************************/
public CurveItem(SphereCurve curve, Coordinates coord) {

    this.curve = curve;
    this.coord = coord;

    color = Color.black;
    stroke = new BasicStroke();


    accuracy2 = 1.0;
    too_good2 = 0.001;


} // end of TestPath

/*************************************************************************
*
*************************************************************************/
public void setStroke(Stroke stroke) {

    this.stroke = stroke;

} // end of setStroke method

/*************************************************************************
*
*************************************************************************/
public void setColor(Color color) {

    this.color = color;

} // end of setColor method

/*************************************************************************
*
*************************************************************************/
private Point2D getPoint(double t, Transform trans, Plane plane) {

    return plane.toPixels(trans.transform(curve.direction(t)));
}

/*************************************************************************
*
*************************************************************************/
private void step(Transform trans, Plane plane, Shape border,
                  Rectangle2D bounds) {

    /***********************************
    * take a half step and a full step *
    ***********************************/
    double t2 = t0 + step;
    if(t2 > 1.0) t2 = 1.0;

    double t1 = 0.5*(t0+t2);

    Point2D point1 = getPoint(t1, trans, plane);
    Point2D point2 = getPoint(t2, trans, plane);

    /************************************************
    * we use this to test whether we are jumping over
    * a discontinuity
    ***************************************************/
    double ratio = point1.distanceSq(point0)/
                   point2.distanceSq(point1);


    /**********************
    * determine the error *
    **********************/
    Line2D line = new Line2D.Double(point0, point2);



    double error2 = line.ptSegDistSq(point1);

    /***********************************************
    * reduce the accuracy if we are off the screen *
    ***********************************************/
    double accuracy2 = this.accuracy2;
    if(!line.intersects(bounds)) {
        Point2D center = new Point2D.Double(bounds.getCenterX(),
                                            bounds.getCenterY() );

        double min2 = point0.distanceSq(center);

        double dist2 = point1.distanceSq(center);
        if(dist2 < min2) min2 = dist2;

        dist2 = point2.distanceSq(center);
        if(dist2 < min2) min2 = dist2;

        accuracy2 = min2 * 0.1;
        if(accuracy2 < this.accuracy2) accuracy2 = this.accuracy2;

    }




//     System.out.println("t2="+t2+" step="+step+" ratio="+ratio+" error2="+error2);

    if(error2 > accuracy2) {
        /*****************************************
        * that wasn't good enough
        * see if we are running out of bounds
        *****************************************/
        Point2D extrapolated =
                    new Point2D.Double(2.0*point1.getX() - point0.getX(),
                                       2.0*point1.getY() - point0.getY() );

       if(! border.contains(extrapolated)) {
           /***************************
           * running out of bounds *
           ************************/
         //  System.out.println("out of bounds");

//            Projection projection = plane.getProjection();
//            Mapping to_pixels = plane.getMappingToPixels();
//            Mapping from_pixels = to_pixels.invert();
//
//            Line2D seg = new Line2D.Double(from_pixels.map(point0),
//                                           from_pixels.map(point1) );
//
//            Point2D intersection = to_pixels.map(
//                                projection.getBorderIntersection(seg));
//
//
//           step = step * Math.sqrt(intersection.distanceSq(point1)/
//                                   point1.distanceSq(point0));



           step *= 0.5;

       } else {
           /***************************
           * just not accurate enough *
           ***************************/

            step *= 0.5;

       }


        /****************
        * redo the step *
        ****************/
        step(trans, plane, border, bounds);
        return;



    } else if(error2 < accuracy2 * 0.01) {
        step *= 2;

    }

    /**********
    * advance *
    **********/
    point0 = point2;
    t0 = t2;

    if(ratio >100.0 || ratio < .001) new_list.add(null);

    new_list.add(point2);
  //  System.out.println("    saving "+t2);



} // end of step method


/*************************************************************************
*
*************************************************************************/
public void update(ChartState state) {

    Transform trans = state.getTransformFrom(coord);
    Plane plane = state.getPlane();
    Projection projection = plane.getProjection();
    Shape border = projection.getBorder();
    border = plane.getMappingToPixels().map(border, 1);

    Rectangle2D bounds = state.getBounds();


    step = 0.5;
    t0 = 0.0;
    point0 = getPoint(t0, trans, plane);


    new_list = new ArrayList<Point2D>();
    new_list.add(point0);

  //  System.out.println();
    while(t0 < 1.0) step(trans, plane, border, bounds);

    list = new_list;

} // end of update method

/*************************************************************************
*
*************************************************************************/
public void paint(Chart chart, Graphics2D g2) {

    java.util.List list = this.list;
    if(list == null) return;

    /*******************************
    * set the graphics environment *
    *******************************/
    Stroke orig_stroke = g2.getStroke();
    Color  orig_color = g2.getColor();

    g2.setStroke(stroke);
    g2.setColor(color);

    int index = 0;
    Point2D last = null;
    for(Iterator it = list.iterator(); it.hasNext(); ++index) {
        Point2D point = (Point2D)it.next();

        if(point != null) {
            if(last != null) {

                g2.draw(new Line2D.Double(last, point));
            }

//             g2.setColor(Color.red);
//             g2.fill(new Rectangle2D.Double(point.getX()-1,
//                                            point.getY()-1, 3,3));
        }

        last = point;


    } // end of loop over points

    /***********************************
    * restore the graphics environment *
    ***********************************/
    g2.setStroke(orig_stroke);
    g2.setColor(orig_color);


} // end of paint method

/*******************************************************************************
*
*******************************************************************************/
public void itemAdded(Chart chart) {}

/*******************************************************************************
*
*******************************************************************************/
public void itemRemoved(Chart chart) {}

} // end of TestPath class
