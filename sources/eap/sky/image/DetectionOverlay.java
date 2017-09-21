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
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.event.*;

/*********************************************************************
*
*********************************************************************/
public class DetectionOverlay extends CompositeOverlay {

DetectionList detections;

double radius;
NumberFormat mag_format;
Color color;
double label_x;
double label_y;

/*********************************************************************
*
*********************************************************************/
public DetectionOverlay(DetectionList detections) {

    this.detections = detections;

    radius = 10;
    mag_format = new DecimalFormat("0.0");

    color = Color.white;
    setLabelAlignment(0.0, 0.5);

    update();
    
    detections.addChangeListener(new Updater());

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public DetectionList getDetectionList() { return detections; }

/*********************************************************************
*
*********************************************************************/
public void setStarRadius(double radius) {

    this.radius = radius;
    update();

} // end of setStarRadius method

/*********************************************************************
*
*********************************************************************/
public void setColor(Color color) { 
    
    this.color = color; 
    update();
    
} // end of setColor method

/*********************************************************************
*
*********************************************************************/
public void setLabelAlignment(double x, double y) {

    this.label_x = x;
    this.label_y = y;
    update();

} // end of setLabelAlignment method

/*********************************************************************
*
*********************************************************************/
public void setMagnitudeFormat(NumberFormat format) {

    this.mag_format = format;

} // end of setMagnitudeFormat method

/*********************************************************************
*
*********************************************************************/
private void update() {

    double offset = radius*1.2;
    if(label_x>0.5) offset = -offset;

    clear(false);
    for(Detection det : detections.getDetections()) {

        String name = det.getName();
        double mag  = det.getMagnitude();
        double x    = det.getX();
        double y    = det.getY();

        /**************
        * star circle *
        **************/
        ShapeOverlay circle = ShapeOverlay.createCircle(x, y, radius);
        circle.setColor(color);
        add(circle, false);

        /********
        * label *
        ********/
        String text = name+" "+mag_format.format(mag);

        ImageLabel label = new ImageLabel(text, x+offset, y);
        label.setColor(color);
        label.setAlignment(label_x, label_y);
        add(label, false);

    } // end of loop over detections
    
    fireChangeEvent();

} // end of update method


/*********************************************************************
*
*********************************************************************/
private class Updater implements ChangeListener {

/*********************************************************************
*
*********************************************************************/ 
public void stateChanged(ChangeEvent e) {
    
    update();

} // end of stateChanged method

} // end of Updater inner class

} // end of DetectionOverlay class