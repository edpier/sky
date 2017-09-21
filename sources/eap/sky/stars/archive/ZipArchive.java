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
public class ZipArchive extends Archive {

ZipFile zip;

/**********************************************************************
*
**********************************************************************/
public ZipArchive(File file) throws IOException {

    this(new ZipFile(file, ZipFile.OPEN_READ));

} // end of convenience constructor from a file

/**********************************************************************
*
**********************************************************************/
public boolean equals(Object o) {

    if(!(o instanceof ZipArchive)) return false;

    ZipArchive archive = (ZipArchive)o;
    return new File(zip.getName()).equals(new File(archive.zip.getName()));

} // end of equals method

/**********************************************************************
*
**********************************************************************/
public ZipFile getZipFile() { return zip; }

/**********************************************************************
*
**********************************************************************/
public ZipArchive(ZipFile zip) {

    this.zip = zip;

} // end of constructor


/**********************************************************************
*
**********************************************************************/
public boolean has(String name) throws IOException {

    ZipEntry entry = zip.getEntry(name);
    return entry != null;

} // end of has method


/**********************************************************************
*
**********************************************************************/
public InputStream getInputStream(String name) throws IOException {

    ZipEntry entry = zip.getEntry(name);
    if(entry == null) return null;

    return zip.getInputStream(entry);

} // end of getInputStream method

} // end of ZipArchive class
