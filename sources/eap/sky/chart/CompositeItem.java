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

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/*******************************************************************************
*
*******************************************************************************/
public class CompositeItem implements ChartItem, Clickable {

protected java.util.List<ChartItem> items;

protected Chart chart;
ChartState last_state;


/*******************************************************************************
*
*******************************************************************************/
public CompositeItem() {
    items = Collections.synchronizedList(new ArrayList<ChartItem>());
}

/*******************************************************************************
*
*******************************************************************************/
public void add(ChartItem item) {

    if(last_state != null) item.update(last_state);

    items.add(item);

} // end of add method

/*******************************************************************************
*
*******************************************************************************/
public void add(int index, ChartItem item) {

    if(last_state != null) item.update(last_state);

  //  System.out.println("index="+index+" size="+items.size());
    if(index == items.size()) items.add(item);
    else items.add(index, item);

} // end of add at index method

/***************************************************************************
*
***************************************************************************/
public void set(int index, ChartItem item) {

    if(last_state != null) item.update(last_state);

    items.set(index, item);

} // end of set method

/***************************************************************************
*
***************************************************************************/
public void remove(ChartItem item) {
    items.remove(item);
}

/***************************************************************************
*
***************************************************************************/
public void remove(int index) {
    items.remove(index);
}

/***************************************************************************
*
***************************************************************************/
public ChartItem get(int index) { return (ChartItem)items.get(index); }

/***************************************************************************
*
***************************************************************************/
public int size() { return items.size(); }

/*******************************************************************************
*
*******************************************************************************/
public void clear() { items.clear(); }

/*******************************************************************************
*
*******************************************************************************/
//public List getItems() { return Collections.unmodifiableList(items); }

/*******************************************************************************
*
*******************************************************************************/
public void update(ChartState state) {

    last_state = state;

    synchronized(items) {
        for(Iterator it=items.iterator(); it.hasNext(); ) {
            ChartItem item = (ChartItem)it.next();

            item.update(state);
        }
    } // end of synchronized block

} // end of project method

/*******************************************************************************
*
*******************************************************************************/
public void paint(Chart chart, Graphics2D g2) {

    synchronized(items) {
        for(Iterator it=items.iterator(); it.hasNext(); ) {
            ChartItem item = (ChartItem)it.next();

            item.paint(chart, g2);
        }
    } // end of synchronized block

} // end of scale method

/*******************************************************************************
*
*******************************************************************************/
public void itemAdded(Chart chart) {

    this.chart = chart;

    synchronized(items) {
        for(Iterator it=items.iterator(); it.hasNext(); ) {
            ChartItem item = (ChartItem)it.next();

            item.itemAdded(chart);
        }
    } // end of synchronized block


} // end of itemAdded method

/*******************************************************************************
*
*******************************************************************************/
public void itemRemoved(Chart chart) {

    synchronized(items) {
        for(Iterator it=items.iterator(); it.hasNext(); ) {
            ChartItem item = (ChartItem)it.next();

            item.itemRemoved(chart);
        }
    } // end of synchronized block

    this.chart = null;

} // end of itemRemoved method


/*************************************************************************
*
*************************************************************************/
public boolean respondToClick(MouseEvent e) {

    synchronized(items) {
        for(ChartItem item : items) {
            if(item instanceof Clickable) {
                Clickable clickable = (Clickable)item;
                if(clickable.respondToClick(e)) return true;
            } // end if the item is clickable
        } // end of loop over items
    } // end of synchronized block

    return false;

} // end of respondToClick method

} // end of CompositeItem class
