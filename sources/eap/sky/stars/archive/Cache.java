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

/***********************************************************************
*
***********************************************************************/
public class Cache extends DirectoryArchive {

/***********************************************************************
*
***********************************************************************/
public Cache(File dir) {

    super(dir);

    /**********************************
    * create the directory if need be *
    **********************************/
    if(!dir.isDirectory()) {
        if(!dir.mkdirs()) {
            throw new IllegalArgumentException("Could not create "+dir);
        }
    }

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public void clear() {

    System.out.println("clearing "+dir);

    /******************************************
    * find an unused temporary directory name *
    ******************************************/
    File tmp = null;
    for(int index = 0; tmp == null || tmp.exists() ; ++index) {
        tmp = new File(dir.getPath()+"_delete"+index);
    }

    /*****************************
    * rename the cache directory
    * and remake a new one
    *****************************/
    dir.renameTo(tmp);
    dir.mkdir();

    /************************************************************
    * now delete the temporary directory in a background thread *
    ************************************************************/
    new DeleteThread(tmp).start();

} // end of clear method

/**********************************************************************
*
**********************************************************************/
public void create(String name) throws IOException {

    new File(dir, name).createNewFile();

} // end of create method

/**********************************************************************
*
**********************************************************************/
public CachingInputStream createCachingInputStream(String name,
                                                   InputStream in)
                                                      throws IOException {
    File file = new File(dir, name);
    return new CachingInputStream(in, file);

} // end of createCachingInputStream method


} // end of Cache class