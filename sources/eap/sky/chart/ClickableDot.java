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

import java.awt.*;
import java.awt.event.*;
//import javax.swing.event.*;

/**********************************************************************
*
**********************************************************************/
public class ClickableDot extends Dot implements Clickable {

boolean selected;
int click_radius2;

Object datum;


/***********************************************************************
*
***********************************************************************/
public ClickableDot(Direction dir, double radius, Coordinates coord,
                     Object datum) {

    super(dir, radius, coord);

    this.datum = datum;

    click_radius2 = this.radius;
    if(click_radius2 < 5) click_radius2 = 5;
    click_radius2 = click_radius2 * click_radius2;

    selected = false;

} // end of constructor

/*************************************************************************
*
*************************************************************************/
public Object getObject() { return datum; }

/*************************************************************************
*
*************************************************************************/
public boolean respondToClick(MouseEvent e) {

    if(point.distanceSq(e.getPoint()) > click_radius2 ) return false;

    return true;

} // end of respondTo method



} // end of SelectableDot class