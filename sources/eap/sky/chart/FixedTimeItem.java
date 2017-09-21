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

import eap.sky.time.*;
import eap.sky.util.*;

import java.awt.*;
import java.awt.event.*;

/************************************************************************
*
************************************************************************/
public class FixedTimeItem implements ChartItem, Clickable, Dragable {

ChartItem item;
TransformCache time;
ChartState last_state;

/************************************************************************
*
************************************************************************/
public FixedTimeItem(ChartItem item, PreciseDate time) {

    this.item = item;
    setTime(time);

} // end of constructor

/************************************************************************
*
************************************************************************/
public void setTime(PreciseDate time) {

    this.time = TransformCache.makeCache(time);

} // end of setTime method

/************************************************************************
*
************************************************************************/
public ChartItem getItem() { return item; }

/************************************************************************
*
************************************************************************/
public TransformCache getTransformCache() { return time; }

/************************************************************************
*
************************************************************************/
public void update(ChartState state) {

    /*************************************************
    * change the chart time to our own internal time *
    *************************************************/
    ChartState state2 = state.changeTime(last_state, time);
    last_state = state2;

    item.update(state2);

} // end of update method

/************************************************************************
*
************************************************************************/
public void paint(Chart chart, Graphics2D g2) {

    item.paint(chart, g2);

} // end of paint method

/************************************************************************
*
************************************************************************/
public boolean respondToClick(MouseEvent e) {

    if(!(item instanceof Clickable)) return false;

    return ((Clickable)item).respondToClick(e);

} // end of respondToClick method

/************************************************************************
*
************************************************************************/
public boolean startDrag(MouseEvent e) {

    if(!(item instanceof Dragable)) return false;

    return ((Dragable)item).startDrag(e);

} // end of startDrag method

/************************************************************************
*
************************************************************************/
public void dragTo(MouseEvent e) {

    ((Dragable)item).dragTo(e);

} // end of dragTo method

/************************************************************************
*
************************************************************************/
public void dragDone(MouseEvent e) {

    ((Dragable)item).dragDone(e);

} // end of dragDone method

/*******************************************************************************
*
*******************************************************************************/
public void itemAdded(Chart chart) {}

/*******************************************************************************
*
*******************************************************************************/
public void itemRemoved(Chart chart) {}

} // end of FixedTimeItem class
