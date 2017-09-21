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
* A cell used to construct a star catalog.
**********************************************************************/
public abstract class OutputCell extends WrapperCell {

CatalogGenerator generator;
boolean closed;


/**********************************************************************
*
**********************************************************************/
public OutputCell(CatalogGenerator generator, Cell cell) {

    super(cell);

    this.generator = generator;
    closed = false;

} // end of constructor

/*********************************************************************
*
*********************************************************************/
protected WrapperCell createChild(Cell cell) {

    CellPropagator propagator = generator.getCellPropagator();
    return propagator.createChild(this, cell);

} // end of createChild method

/**********************************************************************
*
**********************************************************************/
public CatalogGenerator getCatalogGenerator() { return generator; }

/*********************************************************************
*
*********************************************************************/
public File getFile() {

    return new File(generator.getDirectory(), getName());

} // end of getFile method


/*********************************************************************
* Called when this cell's star list is no longer needed. Subclasses
* should implement this method to save the star data to disk,
* (if needed) and release resources. By the time this method returns
* the cell should should know whether or not it has children.
* (i.e. has_childred != MAYBE).
*********************************************************************/
protected final void close() throws IOException {

    if(closed) return;
    doClose();
    closed = true;

} // end of close method

/***********************************************************************
*
***********************************************************************/
protected abstract void doClose() throws IOException;

/***********************************************************************
*
***********************************************************************/
public final boolean isClosed() { return closed; }

/***********************************************************************
*
***********************************************************************/
public abstract void addStar(Star star) throws IOException;



/***********************************************************************
* Write this cell and all its decendants to files. This method recursively
* calls {@link #close()} for each cell.
* @throws IOException if there is a problem writing the stars to disk.
***********************************************************************/
public void closeAll() throws IOException {

    /******************
    * write this cell *
    ******************/
    close();

    /*********************************
    * write the children recursively *
    *********************************/
    for(Iterator it = getChildren().iterator(); it.hasNext(); ) {
        OutputCell cell = (OutputCell)it.next();

        cell.closeAll();

    } // end of loop over children

} // end of closeAll method

} // end of OutputCell class
