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

import java.util.*;

/***************************************************************************
*
***************************************************************************/
public class CatalogLayer {

CellRegion region;

Set<InputCell> leaves;
Set<InputCell> branches;

int nstars;

/***************************************************************************
*
***************************************************************************/
public CatalogLayer(CellRegion region, StarCatalog catalog) {

    this.region = region;

    nstars = 0;
    add(catalog.getRootCell());

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public int getStarCount() { return nstars; }

/***************************************************************************
*
***************************************************************************/
public void step() {

    /**************************
    * find the brightest leaf *
    **************************/
    InputCell brightest = null;
    for(InputCell cell : leaves) {

        if(brightest == null || brightest.getDimmestMag() >
                                     cell.getDimmestMag()   ) {
            brightest = cell;
        }
    } // end of loop over leaves

    /*******************************************************
    * take the brightest out of the list of leaves
    * because we are going to replace it with its children
    *******************************************************/
    remove(brightest);

    /********************************
    * sub-divide the brightest cell *
    ********************************/
    for(Cell c : brightest.getChildren()) {
        InputCell child = (InputCell)c;

        /***************************************
        * discard the cells outside the region *
        ***************************************/
        if(region.contains(child)) add(child);

    } // end of loop over child cells

} // end of step method

/***************************************************************************
*
***************************************************************************/
private void add(InputCell cell) {

    if(cell.hasChildren()) branches.add(cell);
    else                     leaves.add(cell);

    nstars += cell.getStarCount();

} // end of add method


/***************************************************************************
*
***************************************************************************/
private void remove(InputCell cell) {

    if(!leaves.remove(cell)) {
        throw new IllegalArgumentException(cell.getName()+" is not a leaf");
    }

    nstars -= cell.getStarCount();

} // end of remove method

} // end of CatalogLayer class