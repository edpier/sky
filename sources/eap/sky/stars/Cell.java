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

package eap.sky.stars;

import eap.sky.util.*;

import java.lang.ref.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;

/**************************************************************************
* Represents a particular region of the sky in a tessellation scheme.
* The is an abstract superclass. You need to extend this class to implement
* a particular tessellation scheme.
* <p>
* Each cell fits into a tree structure,
* where the children of a cell are contained within and completely fill
* its parent. Each cell holds a list of stars. Successive levels of refinement
* in the hierarchy can hold a greater number of stars. Typically, the stars
* are sorted so that only the brightest stars are in the top level, and lower
* levels hold successively dimmer stars.
* The cells in a particular
* level of the hierarchy should contain copies of all the stars in the
* parent level.
* <p>
* Since star catalogs may
* contain millions of stars, this class has a mechanism for holding
* the star list as a soft reference, which may be reclaimed by the
* garbage collector and then reread later from disk. However, this mechanism
* only works when you specify a zip file in the constructor. Creating a new
* hierarchy requires holding all the stars in memory at once.
**************************************************************************/
public abstract class Cell {

protected static final int YES = 1;
protected static final int NO  = 2;
protected static final int MAYBE = 3;

private Cell parent;
private int has_children;
private ArrayList<Cell> children;
private List<Cell> read_only_children;


/***************************************************************************
* Create a new cell whose star list can be read from a zip compressed file
* when needed. If the argument is null, then the stars will be held in a hard
* reference. Generally you do this when you want to arrange a flat list
* of stars into a tesselation.
***************************************************************************/
public Cell() {

    parent = null;
    children = new ArrayList<Cell>(0);
    read_only_children = Collections.unmodifiableList(children);
    has_children = MAYBE;

} // end of constructor

/***************************************************************************
* Returns the geometry boundary of this cell on the sphere. This can be useful
* for plotting the cell on a chart for debgging, or to visualize the
* tessellation scheme.
* @return The boundary.
***************************************************************************/
public abstract ArcPath getBoundary();

/**************************************************************************
* returns true if this cell has children. Note that this may create the
* objects representing the children of this cell.
* @return true of this cell has children in the tessellation scheme.
**************************************************************************/
public boolean hasChildren() {

    if(has_children == MAYBE) {
        initChildren();
        if(children.size() > 0) has_children = YES;
        else                    has_children = NO;
    }

    return has_children == YES;
}

/**************************************************************************
*
**************************************************************************/
public void markAsLeaf() {

    if(has_children == YES) {
        throw new IllegalStateException("Trying to mark a branch as a leaf");
    }

    has_children = NO;

} // end of markAsLeaf method

/**************************************************************************
* Returns a list of cells which are children of this cell.
* @return an unmodifiable view of the children.
**************************************************************************/
public List<Cell> getChildren() {

    /*********************************************
    * this forces the children to be initialized *
    *********************************************/
    hasChildren();

    return read_only_children;

} // end of getChildren method

/**************************************************************************
*
**************************************************************************/
protected void addChild(Cell child) {

    child.parent = this;
    children.add(child);

} // end of addChild method

/**************************************************************************
*
**************************************************************************/
public void detach() {

    if(parent != null) parent.removeChild(this);

} // end of detach method

/**************************************************************************
*
**************************************************************************/
private void removeChild(Cell child) {

    /***************
    * sanity check *
    ***************/
    if(has_children == MAYBE) {
        throw new IllegalStateException("Child uninitialized");
    }

    /*******************
    * remove the child *
    *******************/
    if(!children.remove(child)) {
        throw new IllegalArgumentException("Not my child");
    }

    /****************************************
    * if we get here the removal went OK
    * mark the parent to null
    ****************************************/
    child.parent = null;

    /***************************************************
    * if that was our last child, then remove us from
    * our parent.
    ***************************************************/
    if(children.size() == 0 && parent != null) {
        parent.removeChild(this);
    }

} // end of removeChild method

/**************************************************************************
* Tests if a given point on the sphere is contained within this cell
* or on its boundary.
* @param dir A point on the sphere.
* @return true of the direction is in the this cell.
**************************************************************************/
public abstract boolean contains(Direction dir);

/**************************************************************************
* Returns the geometric center of the cell. The exact definition of the
* center depends on the tesselation scheme. { @link #contains(Direction) }
* must return true for the center. The center should conform to some
* intuitive concept of being in the middle of the cell.
* @return a point inside the cell.
**************************************************************************/
public abstract Direction getCenter();

/**************************************************************************
* Returns the maximum angular separation between the point returned by
* {@link #getCenter()} and any point in the cell.
* @return the angle between the center and the farthest point.
**************************************************************************/
public abstract Angle getRadius();

/**************************************************************************
* Create the objects representing the children of this cell.
**************************************************************************/
protected final void initChildren() {

    createChildren();
    children.trimToSize();

} // end of initChildren method
    

/**************************************************************************
* Create the objects representing the children of this cell.
**************************************************************************/
protected abstract void createChildren();

/**************************************************************************
* Returns the name of this cell. Each cell in the hierarchy must have a
* unique name. This name is used to locate the list of stars in this
* cell in a zip file.
* @return the unique name of this cell.
**************************************************************************/
public abstract String getName();

/**************************************************************************
* Returns a child of this cell which contains the given direction.
* If the direction lies on a boundary between child cells, this method will
* arbitrarily pick one of the child cells to return. This method will
* always return the same cell for a given direction.
* @param dir A point on the sphere.
* @return a child cell containing the direction, or null if the direction
* is not in this cell.
* Note that the children of a cell must completely cover its parent.
**************************************************************************/
public Cell getChildUnder(Direction dir) {

    for(Iterator it = children.iterator(); it.hasNext(); ) {
        Cell cell = (Cell)it.next();

        if(cell.contains(dir)) {
            /******************************************
            * found the cell which can hold this star *
            ******************************************/
            return cell;
        }

    } // end of loop over children

    return null;

} // end of getChildUnder method

/***************************************************************************
* Collect all the "leaf" cells at the bottom-most level of the hierarchy
* below this cell, which might be within
* a given angular diatance of a given direction. Every cell which contains a
* point within the given circle on the sphere must be in the collection.
* However, the collection may contain additional cells which are nearby.
* @param dir a point on the sphere
* @param radius An angular distance from the point for ehich we want to collect
*  cells.
***************************************************************************/
public Collection<Cell> getCellsNear(Direction dir, Angle radius) {

    List<Cell> cells = new ArrayList<Cell>();
    getCellsNear(dir, radius, cells);
    return cells;

} // end of getCellsNear method

/***************************************************************************
* Recursive method to collect all the cells which might be within
* a given angular distance of a given direction.
* @param dir a point on the sphere
* @param radius An angular distance from the point for which we want to collect
*  cells.
***************************************************************************/
private void getCellsNear(Direction dir, Angle radius, Collection<Cell> cells) {

    /*************************************************
    * see if we are close to the specified direction *
    *************************************************/
    Angle distance = getCenter().angleBetween(dir);
    if(distance.compareTo(radius.plus(getRadius())) > 0) return;

    /*****************************************************
    * if we get here, we are close, but we only want to
    * collect the leaf node cells
    *****************************************************/
    if(hasChildren()) {
        for(Iterator it = children.iterator(); it.hasNext(); ) {
            Cell child = (Cell)it.next();
            child.getCellsNear(dir, radius, cells);
        }
    } else {
        /***********************************************
        * we're it, so add this cell to the collection *
        ***********************************************/
        cells.add(this);
    }

} // end of recursive getCellsNear method

/************************************************************************
*
************************************************************************/
public Iterator getLeaves() { return new LeafIterator(this); }

} // end of Cell class
