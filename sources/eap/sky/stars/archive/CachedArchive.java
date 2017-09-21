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
*
**********************************************************************/
public class CachedArchive extends Archive {

Archive archive;
File dir;

Cache data;
Cache have;
Cache have_not;

/**********************************************************************
*
**********************************************************************/
public CachedArchive(Archive archive, File dir) {

    this.archive = archive;
    this.dir = dir;

    data     = new Cache(new File(dir, "data"));
    have     = new Cache(new File(dir, "have"));
    have_not = new Cache(new File(dir, "have_not"));

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public boolean equals(Object o) {

    if(!(o instanceof CachedArchive)) return false;

    CachedArchive cached = (CachedArchive)o;
    return archive.equals(cached.archive) && dir.equals(cached.dir);

} // end of equals method

/**********************************************************************
*
**********************************************************************/
public File getCacheDirectory() { return dir; }


/**********************************************************************
*
**********************************************************************/
// public boolean has(String name) throws IOException {
// 
// 
//     if(data.has(name) || have.has(name)) return true;
//     else if(         have_not.has(name)) return false;
//     else {
//         /**************************************************
//         * no cached information, check the source archive *
//         **************************************************/
//         boolean has = archive.has(name);
// 
//         /******************
//         * save the status *
//         ******************/
//         if(has)  have.create(name);
//         else have_not.create(name);
// 
//         return has;
// 
//     } // end if we needed to check the source archive
// 
// 
// } // end of has method

/**********************************************************************
*
**********************************************************************/
public InputStream getInputStream(String name) throws IOException {

    InputStream in = data.getInputStream(name);
    if(in != null) return in;

    in = archive.getInputStream(name);
    if(in == null) {
        have_not.create(name);
        return null;
    }

    return data.createCachingInputStream(name, in);

} // end of getInputStream method

/**********************************************************************
*
**********************************************************************/
public void clear() {

    data.clear();
    have.clear();
    have_not.clear();

} // end of clear method



} // end of CachedArchive class
