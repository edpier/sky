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
import java.awt.font.*;
import java.awt.geom.*;

/****************************************************************************
*
****************************************************************************/
public abstract class LabelItem implements ChartItem {

FontRenderContext context;

Point2D point;

Color color;
Color background;
String string;
Font font;

double anchor_x;
double anchor_y;

double margin;

Rectangle2D bounds;

double offset_x;
double offset_y;

/***************************************************************************
*
***************************************************************************/
public LabelItem(String string) {

    this.string = string;

    color = Color.black;
    font = new Font("sans-serif", Font.PLAIN, 12);

    anchor_x = 0.5;
    anchor_y = 0.0;

    margin = 2;

    calculateCenteringOffset();


} // end of constructor

/***************************************************************************
*
***************************************************************************/
protected void calculateCenteringOffset() {

    FontRenderContext context = new FontRenderContext(new AffineTransform(),
                                                      false, false);

    bounds = font.createGlyphVector(context, string).getVisualBounds();

//     offset_x =-bounds.getCenterX();
//     offset_y = 0.0;

    offset_x = -(bounds.getX() + anchor_x * bounds.getWidth());
    offset_y =  -bounds.getY() * anchor_y;

} // end of calculateCenteringOffset method

/***************************************************************************
*
***************************************************************************/
public void setText(String text) {

    this.string = text;
    calculateCenteringOffset();

} // end of setText method

/***************************************************************************
*
***************************************************************************/
public void setFont(Font font) {

    this.font = font;
    calculateCenteringOffset();

} // end of setFont method

/***************************************************************************
*
***************************************************************************/
public void setAnchor(double anchor_x, double anchor_y) {

    this.anchor_x = anchor_x;
    this.anchor_y = anchor_y;

    calculateCenteringOffset();

} // end of setAnchor

/***************************************************************************
*
***************************************************************************/
public void setColor(Color color) {

    this.color = color;

} // end of setColor method

/***************************************************************************
*
***************************************************************************/
public void setBackground(Color background) {

    this.background = background;

} // end of setColor method

/***************************************************************************
*
***************************************************************************/
public Rectangle2D getBounds() { return bounds; }

/***************************************************************************
*
***************************************************************************/
public void paint(Chart chart, Graphics2D g2) {

    if(string == null && point == null) return;

    /**********************
    * save graphics state *
    **********************/
    Color orig_color = g2.getColor();
    Font  orig_font = g2.getFont();

    /*****************
    * label position *
    *****************/
    float x = (float)(point.getX()+offset_x);
    float y = (float)(point.getY()+offset_y);

    /*************
    * background *
    *************/
    if(background != null) {
        g2.setColor(background);
        g2.fill(new Rectangle2D.Double(x + bounds.getX()-margin,
                                       y + bounds.getY()-margin,
                                       bounds.getWidth() +2*margin,
                                       bounds.getHeight()+2*margin));

    } // end of we are painting the background

    /********
    * label *
    ********/
    g2.setColor(color);
    g2.setFont(font);
    g2.drawString(string, x, y);

    /*************************
    * restore graphics state *
    *************************/
    g2.setColor(orig_color);
    g2.setFont(orig_font);

} // end of paint method

/****************************************************************************
*
****************************************************************************/
public void itemAdded(Chart chart) {}

/****************************************************************************
*
****************************************************************************/
public void itemRemoved(Chart chart) {}

/****************************************************************************
*
****************************************************************************/
public boolean isUnder(Point2D point) {

    if(this.point == null) return false;

    point = new Point2D.Double(point.getX() - this.point.getX()-offset_x,
                               point.getY() - this.point.getY()-offset_y);

    return bounds.contains(point);

} // end of isUnder method

} // end of LabelItem class
