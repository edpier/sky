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

/***********************************************************************
*
***********************************************************************/
public class Dot implements ChartItem {

ProjectedPoint point;

int radius;
int diameter;
Shape circle;

Color color;
boolean filled;

boolean radius_changed;

/***********************************************************************
*
***********************************************************************/
public Dot(Direction dir, double radius, Coordinates coord) {

    this(dir, radius, coord, true);

} // end of filled constructor

/***********************************************************************
*
***********************************************************************/
public Dot(Direction dir, double radius, Coordinates coord, boolean filled) {

    point = new ProjectedPoint(dir, coord);
    color = Color.black;
    this.filled = filled;
    setRadius(radius);

} // end of constructor

/*************************************************************************
*
*************************************************************************/
public Direction getDirection() { return point.getDirection(); }

/***********************************************************************
*
***********************************************************************/
public void setColor(Color color) { this.color = color; }

/*************************************************************************
* Set the radius of the dot in pixels. Note this requires an update
* before it goes into effect.
*************************************************************************/
public void setRadius(double radius) {

    int rounded = (int)Math.round(radius);
    if(rounded != this.radius) {
        this.radius = rounded;
        diameter = 2*this.radius;
        radius_changed = true;
    }

} // end of setRadius method

/***************************************************************************
*
***************************************************************************/
public void update(ChartState state) {

    if(point.update(state) || radius_changed) {
        int x = (int)Math.round(point.getX());
        int y = (int)Math.round(point.getY());
        circle = new Ellipse2D.Double(x-radius, y-radius, diameter, diameter);
        radius_changed = false;
    }

} // end of update method

/***************************************************************************
*
***************************************************************************/
public void paint(Chart chart, Graphics2D g2) {

    Color orig_color = g2.getColor();
    g2.setColor(color);

    if(filled) g2.fill(circle);
    else       g2.draw(circle);

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

} // end of StarRenderer
