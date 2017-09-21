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

package eap.sky.stars.archive;

import java.util.zip.*;
import java.io.*;

/*********************************************************************
*
*********************************************************************/
public class GZippedArchive extends Archive {

Archive archive;

/*********************************************************************
*
*********************************************************************/
public GZippedArchive(Archive archive) {

    this.archive = archive;

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public boolean equals(Object o) {

    if(!(o instanceof GZippedArchive)) return false;

    GZippedArchive gzipped = (GZippedArchive)o;
    return gzipped.archive.equals(archive);

} // end of equals method

/**********************************************************************
*
**********************************************************************/
// public boolean has(String name) throws IOException {
// 
//     return archive.has(name+".gz");
// 
// } // end of has method

/**********************************************************************
*
**********************************************************************/
public InputStream getCompressedInputStream(String name) throws IOException {

    return archive.getInputStream(name+".gz");

} // end of getCompressedInputStream method

/**********************************************************************
*
**********************************************************************/
public InputStream getInputStream(String name) throws IOException {

    InputStream in = getCompressedInputStream(name);
    if(in == null) return null;
    else           return new GZIPInputStream(in);

} // end of getInputStream method

} // end of GzippedArchive class
