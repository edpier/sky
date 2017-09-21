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

import java.io.*;

/*********************************************************************
*
*********************************************************************/
public class DirectoryArchive extends Archive {

File dir;

/*********************************************************************
*
*********************************************************************/
public DirectoryArchive(File dir) {

    this.dir = dir;

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public boolean equals(Object o) {

    if(!(o instanceof DirectoryArchive)) return false;

    DirectoryArchive archive = (DirectoryArchive)o;
    return dir.equals(archive.dir);

} // end of equals method

/*********************************************************************
*
*********************************************************************/
public File getDirectory() { return dir; }

/**********************************************************************
*
**********************************************************************/
protected File getFile(String name) {

    return new File(dir, name);

} // end of getFile method

/**********************************************************************
*
**********************************************************************/
public boolean has(String name) throws IOException {

    File file = getFile(name);
    return file.exists();

} // end of has method

/**********************************************************************
*
**********************************************************************/
public InputStream getInputStream(String name) throws IOException {

    File file = getFile(name);
    if(!file.exists()) return null;

    return new FileInputStream(file);

} // end of getInputStream method

} // end of DirectoryArchive class
