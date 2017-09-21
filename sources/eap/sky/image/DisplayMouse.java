package eap.sky.image;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

/********************************************************************
*
********************************************************************/
public class DisplayMouse extends MouseAdapter {

ImageDisplay display;

Point start;
double scale0;
double offset_x0;
double offset_y0;
boolean stretch;

/********************************************************************
*
********************************************************************/
public DisplayMouse(ImageDisplay display) {

    this.display = display;
    display.addMouseListener(this);
    display.addMouseMotionListener(this);

} // end of constructor

/********************************************************************
*
********************************************************************/
private boolean isStretch(MouseEvent e) {

    return e.getButton() != MouseEvent.BUTTON1 ||
           e.isControlDown();

} // end of isStretch method

/********************************************************************
*
********************************************************************/
public void mouseClicked(MouseEvent e) {

    if(e.getClickCount() == 2) display.defaultScale();

} // end of mouseClicked method

/********************************************************************
*
********************************************************************/
public void mousePressed(MouseEvent e) {

    start = e.getPoint();
    offset_x0 = display.getOffsetX();
    offset_y0 = display.getOffsetY();
    scale0    = display.getScale();

    stretch = isStretch(e);

} // end of mousePressed method

/********************************************************************
*
********************************************************************/
public void mouseDragged(MouseEvent e) {

    Point point = e.getPoint();

    if(stretch) {
        /*******************
        * change the scale *
        *******************/
        double height = display.getHeight();

        double factor = Math.sqrt(point.distanceSq(0.0, height)/
                                  start.distanceSq(0.0, height));

       // System.out.println("stretching factor="+factor);
        display.setScale(scale0*factor, factor*offset_x0, factor*offset_y0);

    } else {
        /********************
        * change the offset *
        ********************/
        double offset_x = offset_x0 + point.getX() - start.getX();
        double offset_y = offset_y0 + start.getY() - point.getY();

        display.setScale(scale0, offset_x, offset_y);
    }

} // end of mouseDragged method

} // end of MouseAdapter class