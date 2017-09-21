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

package eap.sky.image;

import java.awt.*;
import java.awt.geom.*;

/*************************************************************************
*
*************************************************************************/
public class ShapeOverlay extends AbstractOverlay {

Shape shape;
Color color;
boolean fill;
AffineTransform trans;

/***********************************************************************
*
***********************************************************************/
public ShapeOverlay(Shape shape, AffineTransform trans) {

    this.shape = shape;
    this.trans = trans;
    color = Color.white;
    fill = false;

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public ShapeOverlay(Shape shape) {

    this(shape, null);

} // end of constructor

/********************************************************************
*
********************************************************************/
public static ShapeOverlay createCircle(double x, double y,
                                        double radius) {

    return new ShapeOverlay(
           new Ellipse2D.Double(x-radius, y-radius,
                                radius*2.0, radius*2.0));

} // end of createCircle static method


/********************************************************************
*
********************************************************************/
public static ShapeOverlay createEllipse(double x1, double y1,
                                         double x2, double y2,
                                         double radius) {

    double dx = x2-x1;
    double dy = y2-y1;
    double f = 0.5*Math.sqrt(dx*dx + dy*dy);
    double a = f+radius;
    double b = Math.sqrt(radius*(radius + 2.0*f));

   // System.out.println("a="+a+" b="+b);

    Shape ellipse = new Ellipse2D.Double(-a, -b, 2*a, 2*b);

    double x = 0.5*(x1+x2);
    double y = 0.5*(y1+y2);

   // System.out.println("x="+x+" y="+y);

    AffineTransform trans = AffineTransform.getTranslateInstance(x, y);
    trans.rotate(dx, dy);

//     AffineTransform trans = AffineTransform.getRotateInstance(dx, dy);
//     trans.translate(x, y);

  //  System.out.println(trans);

    return new ShapeOverlay(ellipse, trans);

} // end of createEllipse static method

/********************************************************************
*
********************************************************************/
public void setColor(Color color) {

    this.color = color;
    fireChangeEvent();

} // end of setColor method

/********************************************************************
*
********************************************************************/
public void setFill(boolean fill) {

    this.fill = fill;
    fireChangeEvent();

} // end of setFill method

/***********************************************************************
*
***********************************************************************/
public void draw(Graphics2D g2) {

    AffineTransform orig_trans = null;
    if(trans != null) {
        orig_trans = g2.getTransform();
        g2.transform(trans);
    }

    Color orig_color = g2.getColor();
    g2.setColor(color);

    g2.draw(shape);
    if(fill) g2.fill(shape);

    g2.setColor(orig_color);
    if(orig_trans != null) g2.setTransform(orig_trans);


} // end of draw method

} // end of ShapeOverlay class