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
/************************************************************************
*
************************************************************************/
public class Arrow implements ChartItem {

//Coordinates coord;

UpdatablePoint from;
UpdatablePoint to;

Line2D shaft;
Line2D head1;
Line2D head2;

double head_size;
Color color;
Stroke stroke;
Angle head_angle;

boolean point;

double magnification;

/************************************************************************
*
************************************************************************/
public Arrow(UpdatablePoint from, UpdatablePoint to) {

    this.from = from;
    this.to   = to;

    head_size = 10.0;
    color = Color.black;
    stroke = new BasicStroke();
    head_angle = new Angle(30.0);

    magnification = 1.0;

} // end of constructor from projected points

/************************************************************************
*
************************************************************************/
public Arrow(Direction from, Direction to, Coordinates coord) {

    this(new ProjectedPoint(from, coord),
         new ProjectedPoint(to  , coord) );

} // end of constructor



/************************************************************************
*
************************************************************************/
public Arrow(Direction from, Direction to) {

    this(from, to, Coordinates.RA_DEC);

} // end of constructor


/************************************************************************
*
************************************************************************/
public void setMagnification(double magnification) {

    this.magnification = magnification;

} // end of setMagnification method

/************************************************************************
*
************************************************************************/
public void setColor(Color color) { this.color = color; }

/************************************************************************
*
************************************************************************/
public void setStroke(Stroke stroke) { this.stroke = stroke; }

/************************************************************************
*
************************************************************************/
public void setHeadSize(double head_size) {

    this.head_size = head_size;

} // end of setHeadAngle method

/************************************************************************
*
************************************************************************/
public void setHeadAngle(double degrees) {

    this.head_angle = new Angle(degrees);

} // end of setHeadAngle method

/************************************************************************
*
************************************************************************/
public UpdatablePoint getFrom() { return from; }

/************************************************************************
*
************************************************************************/
public UpdatablePoint getTo() { return to; }

/************************************************************************
*
************************************************************************/
public void setFrom(UpdatablePoint point) { this.from = point; }

/************************************************************************
*
************************************************************************/
public void setTo(UpdatablePoint point) { this.to = point; }

/************************************************************************
*
************************************************************************/
public void update(ChartState state) {

    /************
    * transform *
    ************/
    from.update(state);
    to.update(state);

    double dx = to.getX() - from.getX();
    double dy = to.getY() - from.getY();

    Point2D to = this.to;
    if(magnification != 1.0) {
        dx *= magnification;
        dy *= magnification;

// I don't understand why this is minus here at all. -ED Pier 2009-04-22
        to = new Point2D.Double(from.getX()-dx, from.getY()-dy);

    } // end if we have a magnification


    /************
    * the shaft *
    ************/
    shaft = new Line2D.Double(from, to);

    /*****************************
    * get the angle of the shaft *
    *****************************/
    double length = Math.sqrt(dx*dx + dy*dy);

    /**********************************
    * if the arrow has no length we
    * won't draw anything
    *********************************/
    if(length ==0) {
        point = true;
        return;
    } else {
        point = false;
    }


    Angle angle0 = new Angle(dy/length, dx/length);

    /*************************
    * first half of the head *
    *************************/
    Angle angle;
    Point2D head;

    angle = angle0.minus(head_angle);
    head = new Point2D.Double(to.getX() + angle.getCos() * head_size,
                              to.getY() + angle.getSin() * head_size);

    head1 = new Line2D.Double(to, head);

    /*************************
    * second half of the head *
    *************************/
    angle = angle0.plus(head_angle);
    head = new Point2D.Double(to.getX() + angle.getCos() * head_size,
                              to.getY() + angle.getSin() * head_size);

    head2 = new Line2D.Double(to, head);

} // end of update method


/************************************************************************
*
************************************************************************/
public void paint(Chart chart, Graphics2D g2) {

//System.out.println("painting arrow color="+color);

    /***********************************************
    * we draw nothing if the arrow has zero length *
    ***********************************************/
    if(point) return;
    if(head1 == null || head2 == null || shaft == null) return;

    Color orig_color = g2.getColor();
    Stroke orig_stroke = g2.getStroke();

    g2.setColor(color);
    g2.setStroke(stroke);

    g2.draw(shaft);
    g2.draw(head1);
    g2.draw(head2);

    g2.setColor(orig_color);
    g2.setStroke(orig_stroke);



} // end of paint method

/*******************************************************************************
*
*******************************************************************************/
public void itemAdded(Chart chart) {}

/*******************************************************************************
*
*******************************************************************************/
public void itemRemoved(Chart chart) {}

} // end of Arrow class
