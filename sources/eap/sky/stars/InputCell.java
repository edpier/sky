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
import eap.sky.stars.archive.*;

import java.lang.ref.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;

/**********************************************************************
* A cell used to read a star catalog.
**********************************************************************/
public class InputCell extends WrapperCell {

StarCatalog catalog;

List<Star> hard_stars;
SoftReference<List<Star>> stars_ref;

CellInfo info;

/**********************************************************************
*
**********************************************************************/
public InputCell(StarCatalog catalog, Cell cell, CellInfo info) {

    super(cell);

    this.catalog = catalog;
    this.info = info;

    if(info == null) {
        throw new IllegalArgumentException("Cell info is null");
    }


    /************************************************
    * determine if we are a leaf by looking at the
    * the cell info
    ************************************************/
    if(info.isLeaf()) markAsLeaf();

} // end of constructor


/*********************************************************************
*
*********************************************************************/
protected WrapperCell createChild(Cell cell) {

    CellInfo info = catalog.getCellInfo(cell.getName());

   // System.out.println(cell.getName()+" "+info);

    if(info == null) return null;
    else             return new InputCell(catalog, cell, info);

} // end of createChild method

/***************************************************************************
* Returns a hard reference to the list of stars which we can then modify.
* @return The list of stars.
***************************************************************************/
private List<Star> getStarList() {

    /**************************************************
    * see if we still have the stars cached in memory *
    **************************************************/
    List<Star> stars = null;
    if(stars_ref != null) stars = stars_ref.get();

    /*******************************************
    * if we don't have the stars cached, then
    * read them from disk
    *******************************************/
    if(stars == null) return loadStars();
    else              return stars;

} // end of getStarList method

/**************************************************************************
* Returns the number of stars in this cell. Note that this will force the
* cell to read the stars from disk to memory if they are not there already.
* But this would only be a soft reference, which the garbage collector
* could reclaim if there were no hard references.
**************************************************************************/
public int getStarCount() {

    return getStarList().size();

} // end of getStarCount method

/***************************************************************************
* Returns a readonly view of this cells stars. Note this method creates
* a hard reference to the star list. So you should make sure you get rid of the
* returned list (set it to null or let it go out of scope) when you are done
* with it. This will allow the garbage collector to reclaim the memory
* occupied by the star list if necessary.
* @return A list of stars in this cell.
***************************************************************************/
public List<Star> getStars() {

    return Collections.unmodifiableList(getStarList());

} // end of getStars method

/*************************************************************************
* Read the stars in this cell from the archive. This method locates the
* file in the zip with the same name as this cell.
* @param zip A Zip file containing the stars for this cell.
* @param stars A list which will be filled with the stars. Note the list is
*        not cleared first.
* @throws FileNotFoundException if the zip file does not contain an entry
* with the same name as this cell.
* @throws IOException if there is trouble reading the data.
*************************************************************************/
private void read(List<Star> stars) throws IOException {

    Archive archive = catalog.getCellArchive();
    InputStream in = archive.getInputStream(getName());
    if(in == null) {
        System.out.println(archive);
        throw new FileNotFoundException(getName());
    }

    read(in, stars);

} // end of read from zip file method

/***********************************************************************
* Read the stars in this cell from a data stream
* @param the data stream.
* @param stars A list which will be filled with the stars. Note the list is
*        not cleared first.
* @throws IOException if there is trouble reading the data.
***********************************************************************/
private void read(InputStream in, List<Star> stars) throws IOException {

    /********************************************
    * wrap a data input stream around the input *
    ********************************************/
    DataInputStream data = new DataInputStream(new BufferedInputStream(in));

    /**********************
    * get the star format *
    **********************/
    StarFormat format = catalog.getStarFormat();

    /*********************
    * read all the stars *
    *********************/
    try {

        BandMap bands = catalog.getBandMap();
        while(true) {
            /*********************
            * read the next star *
            *********************/
            Star star = format.read(data);
            stars.add(star);

        }

    } catch(EOFException e) {}


    /*****************
    * close the file *
    *****************/
    data.close();

} // end of read method

/**************************************************************************
* Locate the correct entry in the zip file for this cell, and read the star
* list into memory.
* @return the list of stars read.
**************************************************************************/
private List<Star> loadStars() {


    List<Star> stars = new ArrayList<Star>();

    try {
        /*****************
        * read the stars *
        *****************/
        read(stars);

        /*****************************************
        * make a soft reference to the star list *
        *****************************************/
        stars_ref = new SoftReference<List<Star>>(stars);

        return stars;

    } catch(IOException e) {
        e.printStackTrace();
        return null;
    }

} // end of loadStars method


/***********************************************************************
*
***********************************************************************/
public Magnitude getDimmestMagnitude() {

    return new ShortMagnitude(catalog.getSortBand(),
                              info.getDimmestMagnitude());


} // end of getDimmestMagnitude method

/***********************************************************************
*
***********************************************************************/
public float getDimmestMag() {

    return info.getDimmestMagnitude();

} // end of getDimmestMag method

/***************************************************************************
*
***************************************************************************/
public Collection<Cell> getCellsNear(Direction dir, Angle radius, float dimmest) {

    List<Cell> cells = new ArrayList<Cell>();
    getCellsNear(dir, radius, dimmest, cells);
    return cells;

} // end of getCellsNear method

/***************************************************************************
* Recursive method to collect all the cells which might be within
* a given angular distance of a given direction.
* @param dir a point on the sphere
* @param radius An angular distance from the point for which we want to collect
*  cells.
***************************************************************************/
private void getCellsNear(Direction dir, Angle radius, float dimmest,
                          Collection<Cell> cells) {

    /*************************************************
    * see if we are close to the specified direction *
    *************************************************/
    Angle distance = getCenter().angleBetween(dir);
    if(distance.compareTo(radius.plus(getRadius())) > 0) return;

    /*****************************************************
    * if we get here, we are close, but we only want to
    * collect the leaf node cells
    *****************************************************/
    if(hasChildren() && getDimmestMag() <= dimmest) {
        for(Iterator it = getChildren().iterator(); it.hasNext(); ) {
            InputCell child = (InputCell)it.next();
            child.getCellsNear(dir, radius, dimmest, cells);
        }
    } else {
        /***********************************************
        * we're it, so add this cell to the collection *
        ***********************************************/
        cells.add(this);
    }

} // end of recursive getCellsNear method

} // end of InputCell class
