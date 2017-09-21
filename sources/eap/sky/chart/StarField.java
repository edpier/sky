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
import eap.sky.stars.*;

import java.util.*;
import java.lang.ref.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.event.*;

/***********************************************************************
*
***********************************************************************/
public class StarField implements ChartItem, Selectable {

public static final int       NO_SELECTION = 0;
public static final int   SINGLE_SELECTION = 1;
public static final int MULTIPLE_SELECTION = 2;

Band band;
String catalog_name;

int selection_mode;

java.util.List<List<SelectableDot>> hard_star_lists;
Map<Cell, Reference<List<SelectableDot>>> stars;
InputCell root;

int max_stars;

Set<SelectableDot> selected;
Set<SelectableDot> read_only_selected;

Set<ChangeListener> listeners;

BufferedImage image;

Magnitude magnitude_limit;
Magnitude brightest;

/***************************************************************************
*
***************************************************************************/
public StarField(StarCatalog catalog, int selection_mode) {

    this.root = catalog.getRootCell();
    this.selection_mode = selection_mode;
    this.band = catalog.getSortBand();
    this.catalog_name = catalog.getName();

    stars = new HashMap<Cell, Reference<List<SelectableDot>>>();
    hard_star_lists = new ArrayList<List<SelectableDot>>();

    selected = new HashSet<SelectableDot>();
    read_only_selected = Collections.unmodifiableSet(selected);

    listeners = new HashSet<ChangeListener>();

   // max_stars = 10000;
    max_stars = 5000;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public String getCatalogName() { return catalog_name; }

/***************************************************************************
*
***************************************************************************/
private double starRadius(Magnitude mag) {

    return 1.0 + 0.5*(magnitude_limit.getValue() - mag.getValue());

//                 double radius =  4.0 - 0.5*mag.getValue();
//                 if(radius < 1.0) radius = 1.0;

} // end of starRadius method


/***************************************************************************
*
***************************************************************************/
public void update(ChartState state) {

  //  if(!state.anyChanged()) return; doesn't check if transform changed

    long start_time = System.currentTimeMillis();

    /**********************************************
    * lazy init the brightest star in the catalog *
    **********************************************/
    if(brightest == null) {
        for(Iterator it = root.getStars().iterator();
            it.hasNext() && brightest == null;  ) {
            Star star = (Star)it.next();
            brightest = star.getPhotometry().getMagnitude(band);
        }
        if(brightest == null) brightest = new ShortMagnitude(band, -2.f);

    } // end if we need to init the brightest star


    /********************************************************
    * get the center of the projection. We need this
    * to determine which cells are visible
    ********************************************************/
    Direction view_center = state.getCenterDirection(Coordinates.RA_DEC);

    /*************************************************
    * loop over successive levels of cell refinement *
    *************************************************/
    Magnitude dimmest = brightest.plus(2.f);
    Set<InputCell> dead_ends      = new HashSet<InputCell>();
    Set<InputCell> parent_visible = null;
    Set<InputCell>        visible = new HashSet<InputCell>();
    java.util.List<Cell> children = null;

    int nstars=0;
    int depth=0;
    for(java.util.List<Cell> parents = Collections.singletonList((Cell)root);
        parents.size() >0 && nstars <= max_stars;
        parents = children, ++depth) {

        /***********************************************
        * create a list of cells visible at this level *
        ***********************************************/
        nstars =0;
        parent_visible = visible;
        visible = new HashSet<InputCell>();

        /****************************************
        * loop over all the cells at this level *
        ****************************************/
        children = new ArrayList<Cell>();
        for(Cell c : parents) {
            InputCell cell = (InputCell)c;

            /************************************
            * determine if this cell is visible *
            ************************************/
            if(!cellIsVisible(cell, state, view_center)) continue;

//System.out.println(cell+" is visible");
            /*************************************
            * if we get here the cell is visible *
            *************************************/
            visible.add(cell);
            nstars += cell.getStarCount();

            /************************************
            * keep track of the magnitude limit *
            ************************************/
            Magnitude mag = cell.getDimmestMagnitude();
            if(mag != null && mag.compareTo(dimmest) >0) dimmest = mag;

            /***************************
            * collect all the children *
            ***************************/
            if(cell.hasChildren() ) {
                children.addAll(cell.getChildren());
            } else {
                dead_ends.add(cell);
            }

        } // end of loop over visible cells
//System.out.println("nstars="+nstars);
     //   if(children.size() == 0) System.out.println("bottom");

    } // end of loop over levels

    boolean limit_changed = ( magnitude_limit == null ||
                             !magnitude_limit.equals(dimmest));
    magnitude_limit = dimmest;

    /**************************************************************
    * if we hit bottom, use the final, not the previous iteration *
    **************************************************************/
    if(nstars > max_stars) visible = parent_visible;

    visible.addAll(dead_ends);

    long found_cells_time = System.currentTimeMillis();

    /******************************************************
    * make sure we have a star list for each visible cell *
    ******************************************************/
    java.util.List<List<SelectableDot>> hard_star_lists =
                                           new ArrayList<List<SelectableDot>>();

    for(Iterator it = visible.iterator(); it.hasNext(); ) {
        InputCell cell = (InputCell)it.next();

        /*************************************************
        * Make sure we have a list of dots for this cell *
        *************************************************/
        List<SelectableDot> list = null;
        Reference<List<SelectableDot>> ref = (Reference<List<SelectableDot>>)stars.get(cell);
        if(ref != null) list = (java.util.List<SelectableDot>)ref.get();

        if(list == null) {
            /*************************************************
            * we don't have a list, so we need to create one *
            *************************************************/
            list = new ArrayList<SelectableDot>();
            stars.put(cell, new SoftReference<List<SelectableDot>>(list));
            for(Iterator it2 = cell.getStars().iterator();
                it2.hasNext(); ) {
                Star star = (Star)it2.next();

                /***************************************
                * get the magnitude of the star
                * skip stars with no data in this band
                ***************************************/
                Magnitude mag = star.getPhotometry().getMagnitude(band);
                if(mag == null) continue;
//                 double radius =  4.0 - 0.5*mag.getValue();
//                 if(radius < 1.0) radius = 1.0;

                double radius = starRadius(mag);

                list.add(new SelectableDot(star.getDirection(), radius,
                                        Coordinates.RA_DEC, star ));

            } // end of loop over stars
        } else if(limit_changed) {
            /**********************************************
            * we have a list, but our limiting magnitude
            * has changed, so we need to change the dot
            * sizes
            **********************************************/
            for(Iterator it2 = list.iterator(); it2.hasNext(); ) {
                SelectableDot  dot = (SelectableDot)it2.next();

                Star star = (Star)dot.getObject();
                Magnitude mag = star.getPhotometry().getMagnitude(band);

                dot.setRadius(starRadius(mag));

            } // end of loop over dots

        } // end of whether or not we have a list

        /*************************************************
        * Collect the lists in current use into a list.
        * We need to keep hard references to these lists
        * so they don't evaporate until we use them
        * Plus we are likely to reuse these lists on the next
        * update, so we will swap this list of hard refs into
        * an object member
        *******************************************************/
        hard_star_lists.add(list);

      //  System.out.println(cell+" lists: "+hard_star_lists.size());

    } // end of loop over visible cells

    long made_lists_time = System.currentTimeMillis();



    /***************************
    * now update all the stars *
    ***************************/
    for(Iterator it = hard_star_lists.iterator(); it.hasNext(); ) {
        List list = (List)it.next();
 // System.out.println("updating star list");
        for(Iterator it2 = list.iterator(); it2.hasNext(); ) {
            ChartItem  item = (Dot)it2.next();

            item.update(state);
        }

    } // end of loop over visible cells


    long update_time = System.currentTimeMillis();


    /************************************************************
    * swap in our current notion of the cells which are visible *
    ************************************************************/
    this.hard_star_lists = hard_star_lists;


    /********************************************************************
    * paint to a buffer
    * We do this since painting all these stars is expensive,
    * and we prefer not to do it in the painting/event handling thread
    ********************************************************************/
    BufferedImage image = state.createBuffer();
    internalPaint(image.createGraphics());

    long paint_time = System.currentTimeMillis();

    this.image = image;


//     System.out.println("depth="+depth+" nstars="+nstars+
//                        " cells="+parent_visible.size());

//     System.out.println("cells="+(found_cells_time-start_time)+
//                        " lists="+(made_lists_time-found_cells_time)+
//                        " update="+(update_time-made_lists_time) +
//                        " paint="+(paint_time - update_time) );

} // end of update method

/*************************************************************************
*
*************************************************************************/
private boolean cellIsVisible(Cell cell, ChartState state,
                              Direction view_center) {

    if(view_center == null) return false;
    if(cell == root) return true;

     /******************************************
     * the center of the cell in RA and Dec *
     ***************************************/
     Direction cell_center = cell.getCenter();

    /*******************************************************************
    * check if the center of the view is within the radius of the cell *
    *******************************************************************/
    Angle dist = cell_center.angleBetween(view_center);
    Angle radius = cell.getRadius();

    if(dist.compareTo(radius)<=0) return true;

    /*************************************************
    * check if the center of the cell is in the view *
    *************************************************/
    Point2D cell_point = state.toDisplay(cell_center, Coordinates.RA_DEC);

    if(cell_point != null) {
        double x = cell_point.getX();
        double y = cell_point.getY();

        if(x>=0.0 && x<=state.getWidth() &&
           y>=0.0 && y<=state.getHeight()  ) return true;
    }

    /**************************************************************
    * find the point on the circle circumscribed around the cell
    * which is closest to the center of the view
    **************************************************************/
    Direction pole = cell_center.perpendicular(view_center);
    pole = pole.oppositeDirection();
    Rotation rot = new Rotation(radius, pole);

    Direction border = rot.transform(cell_center);
    Point2D border_point = state.toDisplay(border, Coordinates.RA_DEC);

    if(border_point != null) {
        double x = border_point.getX();
        double y = border_point.getY();

        if(x>=0.0 && x<=state.getWidth() &&
           y>=0.0 && y<=state.getHeight()  ) return true;

    }

    return false;

} // end of cellIsVisible method

/*************************************************************************
*
*************************************************************************/
public Set getSelected() {

    return read_only_selected;

} // end of getSelected method

/*************************************************************************
*
*************************************************************************/
public void clearSelections() {

     for(Iterator it = selected.iterator(); it.hasNext(); ) {
         SelectableDot dot = (SelectableDot)it.next();

         dot.unselect();
     }

     selected.clear();

} // end of clearSelections method

/*************************************************************************
*
*************************************************************************/
public boolean respondToClick(MouseEvent e) {

//System.out.println("starfield responding mode="+selection_mode);

    if(selection_mode == NO_SELECTION) return false;

  //  System.out.println("seaching stars");

    for(Iterator it = hard_star_lists.iterator(); it.hasNext(); ) {
        List list = (List)it.next();
        for(Iterator it2 = list.iterator(); it2.hasNext(); ) {
            SelectableDot dot = (SelectableDot)it2.next();

            if(dot.respondToClick(e)) {
                if(selection_mode == SINGLE_SELECTION) {
                    clearSelections();
                }

                if(dot.isSelected()) selected.add(dot);
                else                 selected.remove(dot);

                /***********************
                * notify our listsners *
                ***********************/
                fireChangeEvent();

                return true;
            } // end if we got a selection


        } // end of loop over stars

    } // end of loop over cells

    return false;

} // end of respondToClick method

/***************************************************************************
*
***************************************************************************/
public boolean isSelected() { return getSelected().size()!=0; }

/*************************************************************************
*
*************************************************************************/
public void setSelected(boolean selected) {}

/***************************************************************************
*
***************************************************************************/
private void internalPaint(Graphics2D g2) {

    if(hard_star_lists==null) return;

    Color orig_color = g2.getColor();
    g2.setColor(Color.black);

    for(Iterator it = hard_star_lists.iterator(); it.hasNext(); ) {
        List list = (List)it.next();
//System.out.println("painting star list");
        for(Iterator it2 = list.iterator(); it2.hasNext(); ) {
            ChartItem item = (ChartItem)it2.next();

            item.paint(null, g2);


        } // end of loop over stars

    } // end of loop over cells

    g2.setColor(orig_color);

} // end of paint method

/***************************************************************************
*
***************************************************************************/
public synchronized void paint(Chart chart, Graphics2D g2) {

//     if(visible==null) return;
//
//     for(Iterator it = visible.iterator(); it.hasNext(); ) {
//         Cell cell = (Cell)it.next();
//
// //         PathItem path = (PathItem)paths.get(cell);
// //         path.paint(chart, g2);
//
//         /******************
//         * paint the stars *
//         ******************/
//         List list = (List)stars.get(cell);
//         for(Iterator it2 = list.iterator(); it2.hasNext(); ) {
//             ChartItem item = (ChartItem)it2.next();
//
//             item.paint(chart, g2);
//
//
//         } // end of loop over stars
//
//     } // end of loop over cells

    if(image == null) return;
    g2.drawRenderedImage(image, new AffineTransform());

} // end of paint method

/**************************************************************************
*
**************************************************************************/
public void addChangeListener(ChangeListener l) { listeners.add(l); }

/**************************************************************************
*
**************************************************************************/
public void removeChangeListener(ChangeListener l) { listeners.remove(l); }

/**************************************************************************
*
**************************************************************************/
private void fireChangeEvent() {

    ChangeEvent e = new ChangeEvent(this);
    for(Iterator it = listeners.iterator(); it.hasNext(); ) {
        ChangeListener l = (ChangeListener)it.next();

        l.stateChanged(e);

    } // end of loop over listeners

} // end of fireChangeEvent method

/*******************************************************************************
*
*******************************************************************************/
public void itemAdded(Chart chart) {}

/*******************************************************************************
*
*******************************************************************************/
public void itemRemoved(Chart chart) {}

} // end of StarField class
