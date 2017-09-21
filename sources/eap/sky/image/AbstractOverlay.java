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
public abstract class AbstractOverlay implements Overlay {
    
private Collection<ChangeListener> listeners;

/***********************************************************************
*
***********************************************************************/
public AbstractOverlay() {
    
    listeners = new HashSet<ChangeListener>();
    
} // end of constructor

/***********************************************************************
*
***********************************************************************/
public void addChangeListener(ChangeListener l) {
    
    listeners.add(l);
    
} // end of addChageListener method
    
/***********************************************************************
*
***********************************************************************/
public void removeChangeListener(ChangeListener l) {
    
    listeners.remove(l);
    
} // end of removeChageListener method
      
/***********************************************************************
*
***********************************************************************/
protected void fireChangeEvent(ChangeEvent e) {
    
    for(ChangeListener l : listeners) {
        l.stateChanged(e);
    }
    
} // end of fireChangeEvent method

/***********************************************************************
*
***********************************************************************/
protected void fireChangeEvent() {
    
    fireChangeEvent(new ChangeEvent(this));

} // end of fireChangeEvent method

} // end of Overlay interface