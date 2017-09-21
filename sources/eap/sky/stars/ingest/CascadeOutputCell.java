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

package eap.sky.stars.ingest;

import eap.sky.stars.*;

import java.util.*;
import java.io.*;

/**********************************************************************
*
**********************************************************************/
public abstract class CascadeOutputCell extends OutputCell {

boolean have_stars;
boolean flushed;
private float dimmest;

/**********************************************************************
*
**********************************************************************/
public CascadeOutputCell(CatalogGenerator generator, Cell cell) {

    super(generator, cell);

    have_stars = false;
    flushed = false;

    dimmest = -100.f;

} // end of constructor

/*********************************************************************
*
*********************************************************************/
protected abstract List<Star> getList() throws IOException;

/*********************************************************************
*
*********************************************************************/
protected abstract void addToList(Star star) throws IOException;

/*********************************************************************
*
*********************************************************************/
protected abstract int getListSize();


/***********************************************************************
* Add a star to the child cell which contains it.
* @param star The star to add.
* @throws IllegalArgumentException if the star is not in any of the children.
***********************************************************************/
private void addStarToChild(Star star) throws IOException {

    /**************************************
    * find the child containing this star *
    **************************************/
    OutputCell child = (OutputCell)getChildUnder(star.getDirection());
    if(child==null) {
        /***************************
        * this should never happen *
        ***************************/
        throw new IllegalArgumentException(star+" is not in "+this);
    }

    /****************************
    * add the star to the child *
    ****************************/
    child.addStar(star);

} // end of addStarToChild method

/***********************************************************************
* Add all the stars in this cell to the child cells. Only the first call to
* this method flushes the stars. Subsequent calls do nothing.
***********************************************************************/
private void flush() throws IOException {

    /****************
    * sanity checks *
    ****************/
    if(flushed)    throw(new IllegalStateException("Already flushed"));
    if(isClosed()) throw(new IllegalStateException("Already closed" ));

    /*************************
    * create the child cells *
    *************************/
    initChildren();

    /*************************************
    * copy all our stars to our children *
    *************************************/
    for(Star star : getList()) {

        addStarToChild(star);

    } // end of loop over stars

    /**********************************************************
    * mark us as flushed so that subsequent stars will pass
    * straight through to the children
    * Note we have to do this before we close so that we can tell
    * if we are a leaf.
    **********************************************************/
    flushed = true;

    /**************************************************
    * we don't need to hold the star list any more,
    * so dump it to save memory
    **************************************************/
    close();

} // end of flush method


/*********************************************************************
*
*********************************************************************/
protected final void doClose() throws IOException {

    /************************************************
    * write the stars to disk and release resources *
    ************************************************/
    if(have_stars) save();
    else {
        abortSave();
        return;
    }

    /*********************************************
    * are we a leaf?
    * If so mark us as as having to children
    * up at the generic Cell level
    *********************************************/
    boolean leaf = !flushed;
    if(leaf) markAsLeaf();


    /**************************************
    * put an entry in the cell info table *
    **************************************/
    if(dimmest == -100.0) {
        System.out.println(getName()+" closing dimmest=-100");
        System.exit(1);
    }

    CellInfoTable cell_info = generator.getCellInfoTable();
    cell_info.add(new CellInfo(getName(), leaf, dimmest));

    /******************************************************
    * if this is a leaf, then release the underlying cell *
    * to save memory.
    ******************************************************/
    if(leaf) getWrappedCell().detach();


} // end of doClose method

/*********************************************************************
*
*********************************************************************/
protected abstract void save() throws IOException;


/*********************************************************************
*
*********************************************************************/
protected abstract void abortSave() throws IOException;

/***********************************************************************
* Add a star to this cell. If the resulting number of stars in this cell
* exceeds the maximum that it can hold, then this star will be added
* to one of the children of this cell. This process is recursive, so that
* if the child cell is full, the star will be added to a grandchild.
* The first time a star is "bumped" down the hirerarchy, this method
* adds all the stars in this cell to the appropriate child cells.
* <p>
* This method is usually used when you are constructing a new hierarchy of
* cells. However it will work even if the cells were constructed with
* a non-null zip file. In this case it will make a hard reference to the
* stars in this cell so the new star is not lost.
* @param star The star too add.
* @throws IOException If the contents of the cell needs to be written 
* to disk, and there is an error doing so.
***********************************************************************/
public void addStar(Star star) throws IOException {

    if(flushed) {
        /*****************************************************
        * this cell has already overflowed into its children *
        *****************************************************/
        addStarToChild(star);

    } else {
        /****************************
        * we haven't overflowed yet *
        ****************************/
        if(getListSize() == generator.getStarsPerCell()) {
            /**************************************
            * this star will put us one past full *
            **************************************/
            flush();
            addStarToChild(star);

        } else {
            /***********************************
            * we still have room for this star *
            ***********************************/
            have_stars = true;
            dimmest = star.getMagnitude(generator.getSortBand());
            addToList(star);
        }

    } // end if we had room in this cell

} // end of addStar method

} // end of OutputCell class
