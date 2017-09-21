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

package eap.sky.stars.catalog;

import eap.sky.stars.*;

import java.util.*;
import java.io.*;

/**************************************************************************
* Represents a general collection of stars which may be read from a data
* source. Star catalogs may contain millions of stars, and can be cumbersome
* to hold in memory all at once. So typically, this class (or a subclass)
* is used to read some standard format of a catalog, and then
* arrange those stars in to a hierarcical tesselation, which can be searched
* more efficiently, and has more clever strategies for memory management.
* @see Cell
**************************************************************************/
public class Catalog {

List<Star> stars;

/**************************************************************************
* Create a new empty catalog.
**************************************************************************/
public Catalog() {
    stars = new ArrayList<Star>();
}

/***************************************************************************
* Add a star to the catalog
* @param star the star to add.
***************************************************************************/
public void add(Star star) {

    stars.add(star);
}

/******************************************************************************
* Returns all the stars in the catalog.
* @return an unmodifiable view of the stars in this catalog.
******************************************************************************/
public List getStars() {

    return Collections.unmodifiableList(stars);

} // end of getStars method

/******************************************************************************
* Returns the number of stars in this catalog.
* @return The number of stars in this catalog.
******************************************************************************/
public int size() { return stars.size(); }

/******************************************************************************
* Fill this catalog with stars from some data source. Subclasses must
* implement this method to read from a particular data format.
* Note that this method does not have any arguments. This allows subclasses
* maximum freedom in their source of data. Generally, you will specify
* the data source in the subclasses constructor.
* @throws IOException if there is trouble reading.
******************************************************************************/
public void read() throws IOException {

    throw new IOException("Read not implemented");

} // end of read method

/******************************************************************************
* Arrange the stars in this catalog into a hierarchical tesselation.
* This method first sorts the stars in the catalog from brightest to dimmest.
* @param root The root cell of the tesselation.
* @param band The photometry band to usefor sorting.
******************************************************************************/
public void fillCells(Cell root, Band band) {

    /********************************************************
    * sort the stars by magnitude, with the brightest first *
    ********************************************************/
    System.out.println("Sorting");
    Collections.sort(stars, new MagComparator(band) );

    /**************************
    * loop over all the stars *
    **************************/
    System.out.println("filling");
    for(java.util.Iterator it=stars.iterator(); it.hasNext(); ) {
        Star star = (Star)it.next();

      //  root.addStar(star);
    }

    } // end of fillTree method


} // end of Catalog class
