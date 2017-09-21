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
import eap.sky.util.plane.*;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.event.*;

/*************************************************************************
*
*************************************************************************/
public class FOV implements ChartItem, Clickable, Dragable, Spinable {

ImageParams params;
CoordConfig config;

String name;

Color color;
boolean fill;
boolean stroke;

ProjectedPoint center;

PlaneCoordinates top;
PlaneCoordinates layer;

Collection<FOVSegment> segments;

Direction ra_dec;
Angle pa;
AzAlt az_alt;

ChartState state;

boolean moveable;
double drag_offset_x;
double drag_offset_y;

boolean is_dragging;
boolean is_spinning;

Angle orig_angle;
Angle orig_rotator;

Collection<ChangeListener> change_listeners;
Collection<ActionListener> action_listeners;



/*************************************************************************
*
*************************************************************************/
public FOV(CoordConfig config, boolean fill, boolean stroke) {

    this.config = config;
    this.fill = fill;
    this.stroke = stroke;
    this.name = "FOV";

    this.top = config.getTopCoordinates();

    params = new ImageParams(config);

    moveable = true;
    is_dragging = false;
    is_spinning = false;

    segments = new ArrayList<FOVSegment>();

    change_listeners = new ArrayList<ChangeListener>();
    action_listeners = new ArrayList<ActionListener>();

    /*******************************************
    * find the last layer with only one segment *
    *******************************************/
    layer = top;
    if(layer.getSegmentCount() == 1) {
        PlaneCoordinates child = null;
        while((child=layer.getChild()) != null &&
              child.getSegmentCount() == 1) {
            /********************
            * go down one layer *
            ********************/
            layer = child;
        }
    }

    /*****************************
    * collect the top segment(s) *
    *****************************/
    for(Iterator it = layer.getSegments().iterator(); it.hasNext(); ) {
        PlaneSegment seg = (PlaneSegment)it.next();

        segments.add(new FOVSegment(seg));
    }

    setColor(Color.black);

} // end of constructor

/*************************************************************************
*
*************************************************************************/
public CoordConfig getCoordConfig() { return config; }

/*************************************************************************
*
*************************************************************************/
public Color getColor() { return color; }

/*************************************************************************
*
*************************************************************************/
public void setColor(Color color) { this.color = color; }

/*************************************************************************
*
*************************************************************************/
public void setFilled(boolean fill) { this.fill = fill; }

/*************************************************************************
*
*************************************************************************/
public void setStroked(boolean stroke) { this.stroke = stroke; }

/*************************************************************************
*
*************************************************************************/
public void setMoveable(boolean moveable) { this.moveable = moveable; }

/*************************************************************************
*
*************************************************************************/
public boolean isDragging() { return is_dragging; }

/*************************************************************************
*
*************************************************************************/
public boolean isSpinning() { return is_spinning; }

/*************************************************************************
*
*************************************************************************/
public boolean isMoving() { return is_dragging || is_spinning; }


/*************************************************************************
*
*************************************************************************/
public void set(Direction ra_dec, Angle pa, AzAlt az_alt) {

    this.ra_dec = ra_dec;
    this.pa = pa;
    this.az_alt = az_alt;

} // end of set method

/*************************************************************************
*
*************************************************************************/
public void update(ChartState state) {

   // System.out.println("updating FOV");


    this.state = state;



  //  center.update(state);

    params.set(ra_dec, pa, state.getTime(), az_alt);
    if(!params.isSet()) return;

    /**************************
    * update all the segments *
    **************************/
    for(Iterator it = segments.iterator(); it.hasNext(); ) {
        FOVSegment seg = (FOVSegment)it.next();

        /*********
        * update *
        *********/
        seg.update(params, state, color, fill, stroke);

    } // end of loop over segments


} // end of update method

/*************************************************************************
*
*************************************************************************/
public void paint(Chart chart, Graphics2D g2) {

try {
//System.out.println("painting FOV "+name);
    for(Iterator it = segments.iterator(); it.hasNext(); ) {
        FOVSegment seg = (FOVSegment)it.next();

        seg.paint(chart, g2);
    }

//System.out.println("done painting FOV "+name);
} catch(OutOfMemoryError e) { e.printStackTrace(); }

} // end of paint method

/***************************************************************************
*
***************************************************************************/
private boolean isUnder(MouseEvent e) {

    return isUnder(e.getPoint());

} // end of isUnder mouse event method

/***************************************************************************
*
***************************************************************************/
public boolean isUnder(Point2D point) {

    /*******************************
    * loop over displayed segments *
    *******************************/
    for(Iterator it = segments.iterator(); it.hasNext(); ) {
        FOVSegment seg = (FOVSegment)it.next();

        if(seg.isUnder(point)) return true;

    } // end of loop over segments

    /*********************************************************
    * if we get here none of the segments is under the event *
    *********************************************************/
    return false;

} // end of isUnderMethod

/************************************************************************
*
************************************************************************/
public boolean respondToClick(MouseEvent e) {

   // System.out.println("responding to click");

    return isUnder(e);

} // end of respondToClick method

/***************************************************************************
*
***************************************************************************/
public boolean startDrag(MouseEvent e) {



//System.out.println("starting drag");

    if(!moveable) return false;
    if(!isUnder(e)) return false;

    is_dragging = true;

    /******************************************************
    * get the offset from the projected center of the FOV *
    ******************************************************/
    Point2D point = e.getPoint();
    if(center == null) {
        drag_offset_x = 0.0;
        drag_offset_y = 0.0;
    } else {
        drag_offset_x = center.getX() - point.getX();
        drag_offset_y = center.getY() - point.getY();
    }

    return true;

} // end of startDrag method

/***************************************************************************
*
***************************************************************************/
public void dragTo(MouseEvent e) {

    Point2D point = e.getPoint();

    point = new Point2D.Double(point.getX() + drag_offset_x,
                               point.getY() + drag_offset_y );

    Direction dir = state.toCoordinates(point, Coordinates.RA_DEC);
    if(dir==null) return;
//System.out.println("to dir="+dir);
    set(dir, pa, az_alt);


} // end of dragTo method

/***************************************************************************
*
***************************************************************************/
public void dragDone(MouseEvent e) {

    is_dragging = false;
    fireChangeEvent();

} // end of dragDone method

/***************************************************************************
*
***************************************************************************/
public boolean startSpin(MouseEvent e) {

    if(!moveable) return false;
    if(!isUnder(e)) return false;

    is_spinning = true;

    /*******************
    * calculate angles *
    *******************/
    Point2D point = e.getPoint();
    double x = point.getX() - center.getX();
    double y = point.getY() - center.getY();

    double norm = Math.sqrt(x*x+y*y);

    orig_angle = new Angle(y/norm, x/norm);

    return true;

} // end of startSpin method


/***************************************************************************
*
***************************************************************************/
public void spinTo(MouseEvent e) {

//System.out.println("spinning");

    Point2D point = e.getPoint();

    /************************
    * change the roll angle *
    ************************/
    double x = point.getX() - center.getX();
    double y = point.getY() - center.getY();

    double norm = Math.sqrt(x*x+y*y);

    Angle angle = new Angle(y/norm, x/norm);
    angle = angle.minus(orig_angle);


    set(ra_dec, pa.plus(angle), az_alt);


} // end of dragTo method

/***************************************************************************
*
***************************************************************************/
public void spinDone(MouseEvent e) {

    is_spinning = false;
    fireChangeEvent();

} // end of spinDone method

/***************************************************************************
*
***************************************************************************/
public void addChangeListener(ChangeListener l) {

    change_listeners.add(l);
}

/***************************************************************************
*
***************************************************************************/
public void removeChangeListener(ChangeListener l) {

    change_listeners.remove(l);
}

/***************************************************************************
*
***************************************************************************/
public void addActionListener(ActionListener l) {

    action_listeners.add(l);
}

/***************************************************************************
*
***************************************************************************/
public void removeActionListener(ActionListener l) {

    action_listeners.remove(l);
}
/***************************************************************************
*
***************************************************************************/
private void fireChangeEvent() {

//System.out.println("FOV firing change event");

    ChangeEvent e = new ChangeEvent(this);

    for(Iterator it = change_listeners.iterator(); it.hasNext(); ) {
        ChangeListener l = (ChangeListener)it.next();

        l.stateChanged(e);
    }

} // end of fireChangeEvent

/***************************************************************************
*
***************************************************************************/
private void fireActionEvent() {

    ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                    name);

    for(Iterator it = action_listeners.iterator(); it.hasNext(); ) {
        ActionListener l = (ActionListener)it.next();

        l.actionPerformed(e);
    }

} // end of fireActionEvent

/*******************************************************************************
*
*******************************************************************************/
public void itemAdded(Chart chart) {}

/*******************************************************************************
*
*******************************************************************************/
public void itemRemoved(Chart chart) {}

} // end of FOV class
