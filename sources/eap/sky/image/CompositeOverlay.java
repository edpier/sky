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
import java.util.*;
import javax.swing.event.*;

/*************************************************************************
*
*************************************************************************/
public class CompositeOverlay extends AbstractOverlay {

java.util.List<Overlay> overlays;

EventForwarder event_forwarder;

/***********************************************************************
*
***********************************************************************/
public CompositeOverlay() {

    overlays = new ArrayList<Overlay>();
    event_forwarder = new EventForwarder();

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public void clear(boolean fire) { 
    
    overlays.clear(); 
    if(fire) fireChangeEvent();
    
} // end of clear method

/***********************************************************************
*
***********************************************************************/
public void add(Overlay overlay, boolean fire) { 
    
    overlay.addChangeListener(event_forwarder);
    overlays.add(overlay); 
    if(fire) fireChangeEvent();
        
} // end of add method

/***********************************************************************
*
***********************************************************************/
public void draw(Graphics2D g2) {

    for(Overlay overlay : overlays) {
        overlay.draw(g2);
    }

} // end of draw method


/***********************************************************************
*
***********************************************************************/
private class EventForwarder implements ChangeListener {
  
/***********************************************************************
*
***********************************************************************/
public void stateChanged(ChangeEvent e) {
    
    fireChangeEvent(e);
    
} // end of stateChanged method

} // end of EventForwarder inner class
    

} // end of Overlay interface