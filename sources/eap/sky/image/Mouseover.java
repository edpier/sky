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

import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/********************************************************************
*
********************************************************************/
public class Mouseover implements MouseMotionListener {

ImageDisplay display;
float[][] values;

MouseoverData data;

Collection<ChangeListener> listeners;
Firer firer;

/********************************************************************
*
********************************************************************/
public Mouseover(ImageDisplay display) {

    this.display = display;
    display.addMouseMotionListener(this);
    firer = new Firer();
    listeners = new HashSet<ChangeListener>();
    
    data = new MouseoverData();

} // end of constructor

/********************************************************************
*
********************************************************************/
public void addChangeListener(ChangeListener l ) {
    
    listeners.add(l);
    
} // end of addChangeListener method

/********************************************************************
*
********************************************************************/
public void removeChangeListener(ChangeListener l) {
    
    listeners.remove(l);
    
} // end of removeChangeListener method

/********************************************************************
*
********************************************************************/
public void setValues(float[][] values) {

    this.values = values;
    if(values == null) return;
    
    MouseoverData data = this.data;
    if(data.hasPoint()) {
        double x = data.getX();
        double y = data.getY();
        int i = (int)Math.round(x);
        int j = (int)Math.round(y);
        
        if(values.length>0 &&
           i>=0 && i<values[0].length &&
           j>=0 && j<values.length) {
            this.data = new MouseoverData(x, y, values[j][i]);
            
        } else {
            this.data = new MouseoverData(x, y);
        }
        
        fireChangeEvent();
    }
    
} // end of setValues method

/********************************************************************
*
********************************************************************/
public MouseoverData getMouseoverData() { return data; }

/********************************************************************
*
********************************************************************/
public void mouseDragged(MouseEvent e) {
} // end of mouseDragged method

/********************************************************************
*
********************************************************************/
public void mouseMoved(MouseEvent e) {
    
    Point2D pixel = display.getPixelCoordinates(e.getPoint());
    double x = pixel.getX();
    double y = pixel.getY();
   
    //System.out.println("values="+values);
    
    if(values == null) {
        this.data = new MouseoverData(x, y);
    } else {
        int i = (int)Math.round(x);
        int j = (int)Math.round(y);
        
       // System.out.println("i="+i+"j="+j);
        
        if(values.length>0 &&
           i>=0 && i<values[0].length &&
           j>=0 && j<values.length) {
            this.data = new MouseoverData(x, y, values[j][i]);
            
        } else {
            this.data = new MouseoverData(x, y);
        }
    }
    
    fireChangeEvent();
    
} // end of mouseMoved method

/********************************************************************
*
********************************************************************/
private void fireChangeEvent() {
    
   // System.out.println("firing");
    
    if(SwingUtilities.isEventDispatchThread()) {
        firer.run();
    } else {
        SwingUtilities.invokeLater(firer);
    }
    
} // end of fireChangeEvent method

/********************************************************************
*
********************************************************************/
private class Firer implements Runnable {
  
/********************************************************************
*
********************************************************************/
public void run() {
    
    ChangeEvent event = new ChangeEvent(Mouseover.this);
    
    for(ChangeListener l : listeners) {
        l.stateChanged(event);
    }
    
} // end of run method

} // end of Firer method

} // end of MouseAdapter class