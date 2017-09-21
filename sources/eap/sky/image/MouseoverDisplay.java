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
import javax.swing.*;
import javax.swing.event.*;

/********************************************************************
*
********************************************************************/
public class MouseoverDisplay extends JPanel implements ChangeListener {
    
JLabel label;
   
/********************************************************************
*
********************************************************************/
public MouseoverDisplay(Mouseover mouseover) {
    
    label = new JLabel(" ");
    setLayout(new BorderLayout());
    add(label);
    
    mouseover.addChangeListener(this);
    
} // end of constructor
  
/********************************************************************
*
********************************************************************/
public void stateChanged(ChangeEvent e) {
    
    Mouseover mouseover = (Mouseover)e.getSource();
    label.setText(mouseover.getMouseoverData().toString());

} // end of stateChanged method
    
    
} // end of MouseoverDisplay class