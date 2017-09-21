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

/**********************************************************************
* An abstract representation of a collection of files. This could be
* a set of files in a directory, or a zip file, or a set of files
* available over a network, perhaps cached locally.
* Star catalogs are stored in archives.
**********************************************************************/
public abstract class Archive {

/**********************************************************************
* Check if a file exists. The default implementation calls
* getInputStream and closes the stream if it is not null. This is
* probbaly not very efficient, so subclasses should override this method
* if they can.
**********************************************************************/
// public boolean has(String name) throws IOException {
// 
//     InputStream in = getInputStream(name);
//     if(in == null) return false;
// 
//     in.close();
//     return true;
// 
// } // end of has method

/**********************************************************************
* @return null if there is no entry with the requested name.
**********************************************************************/
public abstract InputStream getInputStream(String name) throws IOException;

/**********************************************************************
*
**********************************************************************/
public abstract boolean equals(Object o);

} // end of Archive class
