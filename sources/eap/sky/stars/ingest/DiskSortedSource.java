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

/********************************************************************
*
********************************************************************/
public class DiskSortedSource implements CatalogSource {

File dir;
StarFormat format;
Band band;

List files;
Iterator it;

CatalogSource source;

/********************************************************************
*
********************************************************************/
public DiskSortedSource(File dir, Band band, StarFormat format) throws IOException {

    this.dir = dir;
    this.band = band;
    this.format = format;

    /*******************************
    * list the magnitude bin files *
    *******************************/
    files = BinFile.list(dir);
    it = files.iterator();

    nextFile();

} // end of costructor

/********************************************************************
*
********************************************************************/
private void nextFile() throws IOException {

    if(!it.hasNext()) {
        source = null;
        return;
    }

    BinFile bin = (BinFile)it.next();

    source = null;
    System.gc();
    source = new MemorySorter(new NativeSource(bin.getFile(), format), band);



} // end of nextFile method

/********************************************************************
*
********************************************************************/
public Band getSortBand() { return band; }

/********************************************************************
*
********************************************************************/
public Star nextStar() throws IOException {

    while(true) {
        Star star = source.nextStar();
        if(star == null) {
            nextFile();
            if(source == null) return null;
        } else {
            return star;
        }
    } // end of loop over trials


} // end of nextStar method

} // end of DiskSortedSource class
