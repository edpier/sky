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
import java.io.*;

/************************************************************************
* A virtual collection of output streams associated with files.
* This class is an attempt to get around the operating system
* restriction on the number of files which can be open at one time.
* it automatically closes streams should there be too manyand
* reopens them if needed.
* There are likely some problems with the implementation, and this class
* is currently not used. for a while it was used with DeadEndCell.
************************************************************************/
public class StreamCache {

Map<File, OutputStream> map;
int limit;

private static StreamCache INSTANCE;

/************************************************************************
*
************************************************************************/
private StreamCache() {

    map = new LinkedHashMap<File, OutputStream>();

    limit = Integer.MAX_VALUE;

} // end of constructor

/************************************************************************
*
************************************************************************/
public static StreamCache getInstance() {

    if(INSTANCE == null) INSTANCE = new StreamCache();
    return INSTANCE;

} // end of getInstance method

/************************************************************************
*
************************************************************************/
public DataOutputStream getStream(File file) throws IOException {

    DataOutputStream out = (DataOutputStream)map.get(file);
    if(out != null) return out;



    while(true) {

        while(map.size()>= limit) closeOldest();

        try {
            out = new DataOutputStream(
                new BufferedOutputStream(
                new FileOutputStream(file, true)));

            map.put(file, out);
            return out;

        } catch(FileNotFoundException e) {
            closeOldest();
            limit = map.size();
            System.out.println("set limit to "+limit);
        }

    } // end of loop over trials


} // end of getStream method

/************************************************************************
*
************************************************************************/
public void close(File file) throws IOException {

    DataOutputStream out = (DataOutputStream)map.remove(file);
    if(out != null) out.close();

} // end of close method

/************************************************************************
*
************************************************************************/
private void closeOldest() throws IOException {

   // System.out.println("size="+map.size()+" limit="+limit);

    if(map.size() ==0) {
        throw new IllegalStateException("No streams to close");
    }

    File file = (File)map.keySet().iterator().next();
  //  System.out.println("closing "+file);
    DataOutputStream out = (DataOutputStream)map.remove(file);
    out.close();

} // end of closeOldest method



} // end of StreamCache class
