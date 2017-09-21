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

/**************************************************************************
*
**************************************************************************/
public class DepthFirstGenerator extends CatalogGenerator {

CatalogSource source;
Cell root;

/************************************************************************
*
************************************************************************/
public DepthFirstGenerator(CatalogSource source, File dir, int max_stars,
                           BandMap bands, Band band, StarFormat format,
                           String name, String version, Cell root) {

    super(dir, max_stars, bands, band, format, name, version,
          new LimitedDepthPropagator(256), root);

    this.source = source;
    this.root = root;

} // end of constructor

/************************************************************************
*
************************************************************************/
protected void binStars() throws IOException {


    binStars(source, root);
   // binDeadEnds(root);

} // end of binStars method

/************************************************************************
* @param cell The tessellation cell containing all the stars in the source.
************************************************************************/
protected void binStars(CatalogSource source, Cell cell) throws IOException {

    ((LimitedDepthPropagator)propagator).reset();

    OutputCell root = propagator.createRoot(this, cell);

    /*********************
    * read all the stars *
    *********************/
    int count=0;
    Star star;
    while((star=source.nextStar()) != null) {

        root.addStar(star);
        ++count;

        if(count % 100000 == 0) {
            System.out.println("read "+count+
                               " stars to mag "+star.getMagnitude(band));
        }

    } // end of loop over stars;

    /************************************
    * write out all the remaining cells *
    ************************************/
    //System.out.println("closing all cells");
    root.closeAll();

    /***************************************************
    * we can now release the (sub)tree of output cells *
    ***************************************************/
    root = null;

    /******************************************
    * now recursively propagate the dead ends *
    ******************************************/
    binDeadEnds(cell);

} // end of binStars method

/************************************************************************
*
************************************************************************/
private void binDeadEnds(Cell cell) throws IOException {

   //  if(cell.getName().length()<6) System.out.println(cell.getName());

    /**********************************
    * see if we have a completed cell *
    **********************************/
    File file = new File(dir, cell.getName());
    if(file.exists()) {
        /***************************
        * look at all the children *
        ***************************/
        for(Cell child : new ArrayList<Cell>(cell.getChildren())) {
            binDeadEnds(child);
        }

        return;
    }

    /***********************************
    * see if we have a dead end for it *
    ***********************************/
    file = new File(dir, cell.getName()+DeadEndCell.SUFFIX);

    if(file.exists()) {
        /*********************
        * rebin the dead end *
        *********************/
        //if(cell.getName().length()<6)
        System.out.println(cell.getName());
        binStars(new NativeSource(file, format), cell);
        file.delete();
    }

} // end of binCells method

} // end of DepthFirstGenerator class
