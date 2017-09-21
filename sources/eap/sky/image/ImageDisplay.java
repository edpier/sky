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

import eap.sky.util.plane.*;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;

/*************************************************************************
*
*************************************************************************/
public class ImageDisplay extends JPanel {


BufferedImage image;
java.util.List<Overlay> overlays;
double scale;
double offset_x;
double offset_y;
AffineTransform trans;

boolean flip_y;

Repainter repainter;

/*************************************************************************
*
*************************************************************************/
public ImageDisplay() {

    overlays = new java.util.ArrayList<Overlay>();

    setScale(1.0, 0.0, 0.0);
    flip_y = true;

    setPreferredSize(new Dimension(800,600));

    new DisplayMouse(this);
    repainter = new Repainter();

} // end of constructor

/*************************************************************************
*
*************************************************************************/
public void addOverlay(Overlay overlay) {

    overlays.add(overlay);
    overlay.addChangeListener(repainter);
    repaint();

} // end of addOverlay method

/*************************************************************************
*
*************************************************************************/
public void setBufferedImage(BufferedImage image) {

    this.image = image;
    int width  = (int)Math.ceil(scale*image.getWidth());
    int height = (int)Math.ceil(scale*image.getHeight());
    setPreferredSize(new Dimension(width, height));
    repaint();

} // end of setBufferedImage method

/********************************************************************
*
********************************************************************/
public void defaultScale() {

    if(image == null) return;

    double scalex = (double)getWidth() /(double)image.getWidth();
    double scaley = (double)getHeight()/(double)image.getHeight();

    if(scalex < scaley) setScale(scalex, 0.0, 0.0);
    else                setScale(scaley, 0.0, 0.0);

} // end of defaultScale method

/********************************************************************
*
********************************************************************/
public void setScale(double scale, double offset_x, double offset_y) {

    this.scale = scale;
    this.offset_x = offset_x;
    this.offset_y = offset_y;

    if(flip_y) {
        trans = new AffineTransform(scale, 0.0, 0.0, scale,
                                    offset_x, offset_y);
    } else {
        trans = new AffineTransform(scale, 0.0, 0.0, scale,
                                    offset_x, -offset_y);
    }
    repaint();

} // end of setScale method

/********************************************************************
*
********************************************************************/
public Point2D getPixelCoordinates(Point2D screen) {
    
    AffineTransform trans = new AffineTransform(1.0, 0.0, 0.0, -1.0,
                                                0.0, getHeight());
    
    trans.concatenate(this.trans);
    
    try {trans.invert(); }
    catch(NoninvertibleTransformException e) { return null; }
    
    return trans.transform(screen, null); 
    
} // end of getPixelCoordinates method

/********************************************************************
*
********************************************************************/
public double getOffsetX() { return offset_x; }

/********************************************************************
*
********************************************************************/
public double getOffsetY() { return offset_y; }

/********************************************************************
*
********************************************************************/
public double getScale() { return scale; }

/*************************************************************************
*
*************************************************************************/
public void paintComponent(Graphics g) {

    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;

    /*************************
    * save the graphics state *
    **************************/
    AffineTransform orig_trans = g2.getTransform();
    Color orig_color = g2.getColor();

    /**********************************
    * explicitly paint the background *
    **********************************/
    g2.setColor(getBackground());
    g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
    g2.setColor(getForeground());

    if(flip_y) {
        /*****************************************
        * flip vertically to display the image
        * according to the FITS convention
        *****************************************/
        g2.transform(new AffineTransform(1.0, 0.0, 0.0, -1.0,
                                         0.0, getHeight()));
    } // end if we are flipping in Y

    /******************
    * paint the image *
    ******************/
    if(image != null) g2.drawRenderedImage(image, trans);

    /********************
    * draw the overlays *
    ********************/
    g2.transform(trans);
    for(Overlay overlay : overlays) {

        overlay.draw(g2);

    } // end of loop over overlays

    /*****************************
    * restore the graphics state *
    *****************************/
    g2.setTransform(orig_trans);
    g2.setColor(orig_color);

} // end of paintComponent method

/*************************************************************************
*
*************************************************************************/
private class Repainter implements ChangeListener {
  
/*************************************************************************
*
*************************************************************************/
public void stateChanged(ChangeEvent e) { repaint(); }

} // end of Repainter inner class
    


} // end of ImageDisplay class