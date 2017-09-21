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

import java.io.*;

/*************************************************************************
* An OutputCell which writes its stars directly to disk. The idea is that
* some time later we will open that file and continue cascading its stars
* into the hierarchy of cells.
*************************************************************************/
public class DeadEndCell extends OutputCell {

//private static final StreamCache streams = StreamCache.getInstance();
public static final String SUFFIX = ".end";

DataOutputStream out;

//File file;

/*************************************************************************
*
*************************************************************************/
public DeadEndCell(CatalogGenerator generator, Cell cell) {

    super(generator, cell);

    markAsLeaf();

} // end of constructor



/*************************************************************************
*
*************************************************************************/
public void addStar(Star star) throws IOException {

    /****************************************
    * open the stream if we haven't already *
    ****************************************/
    if(out == null) {
        File file = getFile();
        file = new File(file.getParent(), file.getName()+SUFFIX);
        out = new DataOutputStream(
              new BufferedOutputStream(
              new FileOutputStream(file), 1024*64));
    }

    /*****************
    * write the star *
    *****************/
    StarFormat format = generator.getStarFormat();
    format.write(star, out);



} // end of addStar method

/*************************************************************************
* Close the cell. This cell could potentially have children, but there
* is nothing below it to close, so this method always returns false.
*************************************************************************/
public void doClose() throws IOException {

   /*****************************************************************
   * the data are already on disk, so just close the stream 
   * We first check if we ever opened the stream in the first place 
   *****************************************************************/
   if(out != null) out.close();

} // end of close method

} // end of DeadEndCell class
