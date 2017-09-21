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

import java.io.*;

/*************************************************************************
* Represents a star catalog encoded in a plain ASCII text file with one
* star per line in the file. This is a very common format for star catalogs,
* despite FITS being a standard file format for astronomy.
*************************************************************************/
public abstract class ASCIICatalog extends Catalog {

BufferedReader reader;

/*************************************************************************
* Create a new catalog which can be read from the given source.
* @param reader The data source for this catalog.
*************************************************************************/
public ASCIICatalog(BufferedReader reader) {

    this.reader = reader;

} // end of constructor

/*******************************************************************************
* Reads the stars into this catalog. This method calls {@link #parseLine(String)}
* for each line in the data source.
*******************************************************************************/
public final void read() throws IOException {

    String line;
    while((line = reader.readLine()) != null) {

        Star star = parseLine(line);
        if(star != null) add(star);

    } // end of loop over rows

} // end of read method

/*******************************************************************************
* Extract the data for a star from a string which was taken from a line of the
* data source.
* @return the star parsed from this line or null, if this line should be skipped.
*******************************************************************************/
protected abstract Star parseLine(String line);
} // end of ASCIICatalog class
