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

import java.awt.*;
import java.awt.geom.*;

/**************************************************************************
*
**************************************************************************/
public class Axes implements ChartItem {


Coordinates coord;
ProjectedPoint origin;

String xlabel;
String ylabel;

double length;

Color color;
Stroke stroke;

Point2D point1;
Point2D point2;

Line2D y_axis;
Line2D x_axis;


/**************************************************************************
*
**************************************************************************/
public Axes(Coordinates coord, Direction origin, Coordinates origin_coord,
            String xlabel, String ylabel) {

    this.coord = coord;
    this.origin = new ProjectedPoint(origin, origin_coord);
    this.xlabel = xlabel;
    this.ylabel = ylabel;


    this.color = Color.black;
    this.length = 100;

    stroke = new BasicStroke(2);

} // end of constructor

/**************************************************************************
*
**************************************************************************/
public void setColor(Color color) {

    this.color = color;

} // end of setColor method

/************************************************************************
*
************************************************************************/
public void update(ChartState state) {

    /***********************************
    * skip this if nothing has changed *
    ***********************************/
    if(!state.anyChanged()) return;

    /**************************************
    * update the projection of the origin *
    **************************************/
    origin.update(state);

    Direction dir0 = state.getTransformTo(coord)
                          .transform(origin.getChartCoordinatesDirection());

    /******************************************************
    * find the point offset toward the pole by one degree *
    ******************************************************/
    point1 = offset(dir0, Direction.Z_AXIS, state);

    /****************************************
    * adjust the distance to point1 to give
    * the desired length
    ****************************************/
    double dist = origin.distance(point1);
    double hat = length/dist;

    point1 = new Point2D.Double(origin.getX()*(1.0-hat) + point1.getX()*hat,
                                origin.getY()*(1.0-hat) + point1.getY()*hat);

    /***********************************
    * now find the end of the X axis by
    * rotating by 90 degrees
    ************************************/
    AffineTransform rot = AffineTransform.getRotateInstance(Math.PI*0.5,
                                                            origin.getX(),
                                                            origin.getY());

    point2 = rot.transform(point1, point2);

  //  point2 = offset(dir0, Direction.X_AXIS, state);
    x_axis = new Line2D.Double(origin, point2);
    y_axis = new Line2D.Double(origin, point1);

} // end of update method

/************************************************************************
*
************************************************************************/
private Point2D offset(Direction dir0, Direction toward, ChartState state) {

    Direction pole = toward.perpendicular(dir0);
    Rotation rot = new Rotation(1.0,pole);

    Direction dir1 = rot.transform(dir0);

    Point2D point1 = state.toDisplay(dir1, coord);

    return point1;

} // end of offset method


/************************************************************************
*
************************************************************************/
public void paint(Chart chart, Graphics2D g2) {


    Color  orig_color  = g2.getColor();
    Stroke orig_stroke = g2.getStroke();
    Font   orig_font   = g2.getFont();

    g2.setColor(color);
    g2.setStroke(stroke);

    g2.draw(x_axis);
    g2.draw(y_axis);

    Font font = orig_font.deriveFont(Font.BOLD, orig_font.getSize()*2.f);
    g2.setFont(font);

    g2.drawString(ylabel, (float)point1.getX(), (float)point1.getY());
    g2.drawString(xlabel, (float)point2.getX(), (float)point2.getY());

    g2.setColor( orig_color );
    g2.setStroke(orig_stroke);
    g2.setFont(  orig_font  );

} // end of paint method

/*******************************************************************************
*
*******************************************************************************/
public void itemAdded(Chart chart) {}

/*******************************************************************************
*
*******************************************************************************/
public void itemRemoved(Chart chart) {}

} // end of Axes class
