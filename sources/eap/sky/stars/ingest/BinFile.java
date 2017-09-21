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

import java.util.*;
import java.text.*;
import java.io.*;

/***********************************************************************
*
***********************************************************************/
public class BinFile implements Comparable<BinFile> {

public static final String  BAND_MAP_FILENAME = "bands";
public static final String SORT_BAND_FILENAME = "band";

private static final String prefix = "bin";
private static final NumberFormat format = new DecimalFormat("0000");

int index;
File file;



/***********************************************************************
*
***********************************************************************/
public static List<BinFile> list(File dir) {


    List<BinFile> list = new ArrayList<BinFile>();
    File[] files = dir.listFiles();
    for(int i=0; i< files.length; ++i) {
        File file = files[i];

        try { list.add(new BinFile(file)); }
        catch(IllegalArgumentException e) {}

    } // end of loop over files

    /*****************
    * sort the files *
    *****************/
    Collections.sort(list);

    return list;

} // end of listFiles method

/***********************************************************************
*
***********************************************************************/
public BinFile(File dir, int index) {

    this.index = index;
    file = new File(dir, prefix+format.format(index));

} // end of constructor from magnitude

/***********************************************************************
*
***********************************************************************/
public BinFile(File file) {

    this.index = parse(file);
    this.file = file;

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public File getFile() { return file; }


/***********************************************************************
*
***********************************************************************/
private static int parse(File file) {

    String name = file.getName();
    if(!name.startsWith(prefix)) {
        throw new IllegalArgumentException();
    }

    try {
        return format.parse(name.substring(prefix.length()))
                     .intValue();
    } catch(ParseException e) {
        throw (RuntimeException)new IllegalArgumentException().initCause(e);
    }

} // end of parse method

/***********************************************************************
*
***********************************************************************/
public int compareTo(BinFile bin) {

    if(     index  < bin.index) return -1;
    else if(index == bin.index) return  0;
    else                        return  1;

} // end of compareTo method


} // end of BinFile class
