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

import java.util.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.geom.*;

/*********************************************************************
*
*********************************************************************/
public class ChartMouse extends MouseInputAdapter {

private static final int CHART_DRAG = 0;
private static final int ITEM_DRAG  = 1;
private static final int ITEM_SPIN  = 2;

Dragable dragable;
Spinable spinable;
int type;

/*************************************************************************
*
*************************************************************************/
private int dragType(MouseEvent e) {

    if(   e.isControlDown() || e.getButton() == e.BUTTON3) return  ITEM_SPIN;
    else if(e.isShiftDown() || e.getButton() == e.BUTTON2) return  ITEM_DRAG;
    else                                                   return CHART_DRAG;

} // end of dragType method

/*************************************************************************
* start of a drag
*************************************************************************/
public void mousePressed(MouseEvent e) {

    /****************
    * get the chart *
    ****************/
    Chart chart = (Chart)e.getSource();

    /****************************************
    * determine what kind of action this is *
    ****************************************/
    type = dragType(e);

   // System.out.println("drag type = "+type);

    dragable = null;
    spinable = null;

    /***********************************
    * see if we are dragging the chart *
    ***********************************/
    if(type == CHART_DRAG) {
        dragable = chart;
        dragable.startDrag(e);
        return;
    }

    /********************************************************
    * for anything else we have to search for an item to
    * interact with
    ********************************************************/
    if(type == ITEM_DRAG) {
 //   System.out.println("dragging");
        /******************************
        * look for something dragable *
        ******************************/
        for(Iterator it = chart.getItems().iterator(); it.hasNext(); ) {
            ChartItem item = (ChartItem)it.next();
//System.out.println("    "+item);
            /**************************************
            * skip anything which is not dragable *
            **************************************/
            if(! (item instanceof Dragable)) continue;
            Dragable dragable = (Dragable)item;
//System.out.println("    "+dragable);
            /*******************************************
            * check if this item responds to the click *
            *******************************************/
            if(dragable.startDrag(e) ) {

                this.dragable = dragable;
                return;
            }

        } // end of loop over items

        return;

    } else if(type == ITEM_SPIN) {
        /******************************
        * look for something spinable *
        ******************************/
        for(Iterator it = chart.getItems().iterator(); it.hasNext(); ) {
            ChartItem item = (ChartItem)it.next();

            /**************************************
            * skip anything which is not dragable *
            **************************************/
            if(! (item instanceof Spinable)) continue;
            Spinable spinable = (Spinable)item;

            /*******************************************
            * check if this item responds to the click *
            *******************************************/
            if(spinable.startSpin(e) ) {

                this.spinable = spinable;
                return;
            }

        } // end of loop over items

        return;

    } // end if this is a spin

    /**************************
    * shouldn't ever get here *
    **************************/

} // end of mousePressed method

/**************************************************************************
* middle of a drag
**************************************************************************/
public void mouseDragged(MouseEvent e) {

    if(type == ITEM_DRAG || type == CHART_DRAG) {
        /**********
        * draging *
        **********/
        if(dragable==null) return;
        dragable.dragTo(e);

    } else if(type == ITEM_SPIN) {
        /***********
        * spinning *
        ***********/
        if(spinable == null) return;
        spinable.spinTo(e);
    }

    /***************************************************
    * Update the chart to show any visible changes in
    * response to the click
    **************************************************/
    Chart chart = (Chart)e.getSource();
    if(!chart.isAnimated()) chart.update();

} // end of mouseDragged method

/***********************************************************************
*
***********************************************************************/
public void mouseReleased(MouseEvent e) {

   // System.out.println("Mouse released");

    if(dragable != null) {
       // System.out.println(dragable);
        dragable.dragDone(e);
        dragable = null;
    }

    if(spinable != null) {
      //  System.out.println(spinable);
        spinable.spinDone(e);
        spinable = null;
    }

    /***************************************************
    * Update the chart to show any visible changes in
    * response to the click
    **************************************************/
    Chart chart = (Chart)e.getSource();
    if(!chart.isAnimated()) chart.update();

} // end of mouseReleased method

/***********************************************************************
*
***********************************************************************/
public void mouseClicked(MouseEvent e) {

    Chart chart = (Chart)e.getSource();

    /*************************************************
    * search for an item which responds to the click *
    *************************************************/
    for(Iterator it = new ArrayList<ChartItem>(chart.getItems()).iterator();
        it.hasNext(); ) {
        ChartItem item = (ChartItem)it.next();

        /****************************************
        * skip anything which is not selectable *
        ****************************************/
        if(! (item instanceof Clickable)) continue;
        Clickable clickable = (Clickable)item;

        /*******************************************
        * check if this item responds to the click *
        *******************************************/
        if(! clickable.respondToClick(e) ) continue;

        /*********************************************************
        * if we get here, then something responded to the click
        * need to check if I really mean to keep looking for
        * more things to click.
        **********************************************************/


    } // end of loop over items

    /************************************************************
    * Update the chart to show any visible changes in response
    * response to the click
    ************************************************************/
    if(!chart.isAnimated()) chart.update();

} // end of mouseClicked method


} // end of ChartMouse method
