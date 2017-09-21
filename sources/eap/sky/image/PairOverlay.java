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
public class PairOverlay extends CompositeOverlay {

PairList pairs;

double radius;
Color color;


/*********************************************************************
*
*********************************************************************/
public PairOverlay(PairList pairs) {

    this.pairs = pairs;

    radius = 5;
    color = Color.yellow;

    update();

    pairs.addChangeListener(new Updater());

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public PairList getPairList() { return pairs; }

/*********************************************************************
*
*********************************************************************/
public void setRadius(double radius) {

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
private void update() {

    clear(false);
    for(DetectionPair pair : pairs.getPairs()) {
        Detection det1 = pair.getFirst();
        Detection det2 = pair.getSecond();

        double x1    = det1.getX();
        double y1    = det1.getY();
        double x2    = det2.getX();
        double y2    = det2.getY();

        /**************
        * star circle *
        **************/
        ShapeOverlay ellipse = ShapeOverlay.createEllipse(x1, y1,
                                                          x2, y2, radius);
        ellipse.setColor(color);
        add(ellipse, false);

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