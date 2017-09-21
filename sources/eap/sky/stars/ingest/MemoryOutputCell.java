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
//
import java.util.*;
import java.io.*;

/**********************************************************************
*
**********************************************************************/
public class MemoryOutputCell extends CascadeOutputCell {

private static int instances=0;
private static int nclosed = 0;

List<Star> stars;

/**********************************************************************
*
**********************************************************************/
public MemoryOutputCell(CatalogGenerator generator, Cell cell) {

    super(generator, cell);

    stars = new ArrayList<Star>(generator.getStarsPerCell());

    ++instances;
//     System.out.println("memory instances="+instances+
//                        " open="+(instances-nclosed));

} // end of constructor

/**********************************************************************
*
**********************************************************************/
protected void finalize() {

    --instances;
    --nclosed;

} // end of finalize method


/*********************************************************************
*
*********************************************************************/
protected List<Star> getList() throws IOException { return stars; }

/*********************************************************************
*
*********************************************************************/
protected void addToList(Star star) throws IOException {

    stars.add(star);
    
} // end of addToList method

/*********************************************************************
*
*********************************************************************/
protected int getListSize() { return stars.size(); }


/***********************************************************************
* Write an encoded representation of the stars in this cell to a file.
* The name of the file written will be the same as the name of this cell.
* @throws IllegalArgumentException if the argument is not a directory
* @throws IOException if there is a problem writing the stars to disk.
***********************************************************************/
protected void save() throws IOException {

    if(stars.size() ==0) {
        throw new IllegalStateException("No stars");
    }

    ++nclosed;

    /************************************
    * open a file named after this cell *
    ************************************/
    DataOutputStream out = new DataOutputStream(
                           new BufferedOutputStream(
                           new FileOutputStream(getFile())));

    /**********************
    * write all the stars *
    **********************/
    StarFormat format = generator.getStarFormat();
    for(Star star : stars) {
        format.write(star, out);
    }

    /*****************
    * close the file *
    *****************/
    out.close();

    /*********************************************
    * set the list to null to release its memory *
    *********************************************/
    stars = null;

} // end of save method

/*********************************************************************
*
*********************************************************************/
protected void abortSave() throws IOException {

    ++nclosed;
    stars = null;

} // end of abortSave method


} // end of OutputCell class
