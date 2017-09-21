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


import eap.sky.chart.*;


import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*************************************************************************
*
*************************************************************************/
public abstract class ListItem extends CompositeItem
                            implements ListDataListener, Selectable,
                                  ListSelectionListener {

protected ListModel list;
private ListSelectionModel selection;
private boolean selected;

/*************************************************************************
*
*************************************************************************/
public ListItem(ListModel list, ListSelectionModel selection) {

    this.list = list;
    this.selection = selection;

    selected = false;

    /****************************************
    * create chart items for the elements
    * already in the list 
    ****************************************/
    for(int i=0; i< list.getSize(); ++i) {
        add(i, createItem(i, list.getElementAt(i)));
    }
        
    list.addListDataListener(this);
    selection.addListSelectionListener(this);

} // end of constructor

/*************************************************************************
*
*************************************************************************/
protected abstract ChartItem createItem(int index, Object element);

/*************************************************************************
*
*************************************************************************/
public void intervalAdded(ListDataEvent e) {

    ListModel source = (ListModel)e.getSource();

    for(int i=e.getIndex0(); i<=e.getIndex1(); ++i) {
        add(i, createItem(i, source.getElementAt(i)));
    }


} // end of intervalAdded method

/*************************************************************************
*
*************************************************************************/
public void intervalRemoved(ListDataEvent e) {

    for(int i=e.getIndex1(); i>= e.getIndex0(); --i) {
    System.out.println("removing "+i);
        remove(i);
    }

} // end of intervalRemoved method

/*************************************************************************
*
*************************************************************************/
public void contentsChanged(ListDataEvent e) {

    ListModel source = (ListModel)e.getSource();

    for(int i=e.getIndex0(); i<=e.getIndex1(); ++i) {
        set(i, createItem(i, source.getElementAt(i)));
    }

} // end of contentsChanged method


/*************************************************************************
*
*************************************************************************/
public boolean respondToClick(MouseEvent e) {

    if(selection == null) return false;

    selection.setValueIsAdjusting(true);
    boolean selected = false;

    /******************
    * loop over items *
    ******************/
    for(int i=0; i< size(); ++i) {

        ChartItem item = get(i);
        if(! (item instanceof Selectable)) continue;
        Selectable selectable = (Selectable)item;

        if(selectable.respondToClick(e)) {
            selection.setSelectionInterval(i,i);
            selected = true;
            
        } else {
            selection.removeSelectionInterval(i,i);
        }

    } // end of loop over satellites

    this.selected = selected;
    selection.setValueIsAdjusting(false);

    return selected;
    

} // end of respondToClick method


/*********************************************************************
*
*********************************************************************/
public void valueChanged(ListSelectionEvent e) {

//System.out.println("list got selection change");

    if(e.getValueIsAdjusting()) return;

    ListSelectionModel model = (ListSelectionModel)e.getSource();

    /********************
    * loop over indexes *
    ********************/
    for(int i=e.getFirstIndex(); i<= e.getLastIndex(); ++i) {

        ChartItem item = get(i);
        if(! (item instanceof Selectable)) continue;
        Selectable selectable = (Selectable)item;

        selectable.setSelected(model.isSelectedIndex(i));

    } // end of loop over indexes

} // end of valueChanged method


/*************************************************************************
*
*************************************************************************/
public boolean isSelected() {

    return selected;

} // end of isSelected method

/*************************************************************************
*
*************************************************************************/
public void setSelected(boolean selected) {}

} // end of ListItem method