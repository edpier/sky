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
import java.awt.font.*;
import java.awt.geom.*;

/********************************************************************
*
********************************************************************/
public class ImageLabel extends AbstractOverlay {

String text;
double x;
double y;
Point2D point;
Color color;
Font font;
Rectangle2D bounds;
float dx;
float dy;
Color background;

/********************************************************************
*
********************************************************************/
public ImageLabel(String text, double x, double y) {

    this.text = text;
    this.x = x;
    this.y = y;

    font = new Font("sans-serif", Font.PLAIN, 12);
    color = Color.white;

    computeBoundingBox();

} // end of constructor

/********************************************************************
*
********************************************************************/
private void computeBoundingBox() {

    FontRenderContext context = new FontRenderContext(new AffineTransform(),
                                                      false, false);

    bounds = font.createGlyphVector(context, text).getVisualBounds();

} // end of computeBoundingBox method

/********************************************************************
*
********************************************************************/
public void setText(String text) {

    this.text = text;
    computeBoundingBox();

} // end of setText method

/********************************************************************
*
********************************************************************/
public void setColor(Color color) { this.color = color; }

/********************************************************************
*
********************************************************************/
public void setBackground(Color background) {

    this.background = background;

} // end of setBackground method

/********************************************************************
*
********************************************************************/
public void setFont(Font font) {

    this.font = font;
    computeBoundingBox();

} // end of setFont method

/********************************************************************
*
********************************************************************/
public void resizeFont(double factor) {

    setFont(font.deriveFont((float)(font.getSize()*factor)));

} // end of resizeFont method

/********************************************************************
*
********************************************************************/
public void setAlignment(double dx, double dy) {

    this.dx = -(float)(dx * bounds.getWidth());
    this.dy =  (float)(dy * bounds.getHeight());

} // end of setAlignment method

/********************************************************************
*
********************************************************************/
public void draw(Graphics2D g2) {

    /**************************
    * save the graphics state *
    **************************/
    AffineTransform orig_trans = g2.getTransform();
    Color           orig_color = g2.getColor();
    Font            orig_font  = g2.getFont();


    g2.translate(x, y);

    if(orig_trans.getScaleX() <0.0) {
        /*******************
        * need to unflip X *
        *******************/
        g2.scale(-1.0, 1.0);
    }

    if(orig_trans.getScaleY() <0.0) {
        /*******************
        * need to unflip X *
        *******************/
        g2.scale(1.0, -1.0);
    }

    /***********************************************
    * paint the background if a color is specified *
    ***********************************************/
    if(background != null && text.length()>0) {
        g2.setColor(background);
        g2.fill(bounds);

        Stroke orig_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(4.f));
        g2.draw(bounds);
        g2.setStroke(orig_stroke);
    }

    /******************
    * draw the string *
    ******************/
    g2.setColor(color);
    g2.setFont(font);
    g2.drawString(text, dx, dy);

    /********************
    * restore the state *
    ********************/
    g2.setTransform(orig_trans);
    g2.setColor(    orig_color);
    g2.setFont(     orig_font);

} // end of draw method

} // end of ImageLabel class