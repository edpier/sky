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
* Represents a star catalog encoded in a plain ASCII text file with one
* star per line in the file. This is a very common format for star catalogs,
* despite FITS being a standard file format for astronomy.
*************************************************************************/
public abstract class ASCIISource implements CatalogSource {

BufferedReader reader;

/*************************************************************************
* Create a new catalog which can be read from the given source.
* @param reader The data source for this catalog.
*************************************************************************/
public ASCIISource(BufferedReader reader) {

    this.reader = reader;
    
} // end of constructor

/**************************************************************************
*
***************************************************************************/
public final Star nextStar() throws IOException {

    /*************************************************
    * read lines until we find one with a star in it *
    *************************************************/
    while(true) {
        String line = reader.readLine();
        if(line == null) return null;

        Star star = parseLine(line);
        if(star != null) return star;

    } // end of loop over lines

} // end of read method

/***************************************************************************
* Extract the data for a star from a string which was taken from a line of the
* data source.
* @return the star parsed from this line or null, if this line should be skipped.
***************************************************************************/
protected abstract Star parseLine(String line);

} // end of ASCIISource class
