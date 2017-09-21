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

package eap.sky.stars;

import eap.sky.stars.archive.*;

import java.util.*;
import java.io.*;

/***********************************************************************
*
***********************************************************************/
public class CellInfoTable {

Map<String, CellInfo> map;

/***********************************************************************
*
***********************************************************************/
public CellInfoTable() {

    map = new HashMap<String, CellInfo>();

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public void add(CellInfo info) {

    map.put(info.getName(), info);

} // end of add method

/***********************************************************************
*
***********************************************************************/
// public boolean add(Archive archive, StarFormat format, Band band, Cell cell) throws IOException {
// 
//     String name = cell.getName();
//    // System.out.println("name="+name);
//     InputStream in = archive.getInputStream(name);
//     if(in == null) return false;
// 
//     DataInputStream data = new DataInputStream(
//                            new BufferedInputStream(in));
// 
//     /************************
//     * find the dimmest star *
//     ************************/
//    // System.out.println("reading");
//     Star star = null;
//     try {
//         while(true) { star = format.read(data); }
//     } catch(EOFException e) {}
// 
//     data.close();
// 
//    // System.out.println("done reading");
// 
//     float dimmest = star.getMagnitude(band);
// 
//     if(name.length()<5) System.out.println(name+" "+dimmest);
// 
//     /********************************************************
//     * recurse through the children and see if we are a leaf *
//     ********************************************************/
//     boolean leaf = true;
//     for(Iterator it = cell.getChildren().iterator(); it.hasNext(); ) {
//         Cell child = (Cell)it.next();
// 
//         if(add(archive, format, band, child)) leaf = false;
// 
//     } // end of loop over children
// 
//     add(new CellInfo(name, leaf, dimmest));
// 
// 
//     return true;
// 
// } // end of add method


/***********************************************************************
*
***********************************************************************/
public CellInfo get(String name) {

    return (CellInfo)map.get(name);

} // end of get method

/***********************************************************************
*
***********************************************************************/
public void write(File file) throws IOException {

    PrintWriter writer = new PrintWriter(
                         new FileWriter(file));

    for(Iterator it = map.values().iterator(); it.hasNext(); ) {
        CellInfo info = (CellInfo)it.next();
        info.write(writer);
    }

    writer.close();


} // end of write method

/***********************************************************************
*
***********************************************************************/
public void writeBinary(File file) throws IOException {

    DataOutputStream out = new DataOutputStream(
                           new BufferedOutputStream(
                           new FileOutputStream(file)));

    for(Iterator it = map.values().iterator(); it.hasNext(); ) {
        CellInfo info = (CellInfo)it.next();
        info.write(out);
    }

    out.close();


} // end of writeBinary method

/***********************************************************************
*
***********************************************************************/
public static CellInfoTable read(InputStream in) throws IOException {

    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(in));

    CellInfoTable table = new CellInfoTable();

    CellInfo info = null;
    while((info=CellInfo.read(reader)) != null) {
        table.add(info);
    }

    reader.close();

    return table;

} // end of read method

/***********************************************************************
*
***********************************************************************/
public static CellInfoTable readBinary(InputStream in) throws IOException {

    DataInputStream data = new DataInputStream(
                           new BufferedInputStream(in));

    CellInfoTable table = new CellInfoTable();

    try {
        CellInfo info = null;
        while((info=CellInfo.read(data)) != null) {
            table.add(info);
        }
    } catch(EOFException e) {}

    data.close();

    return table;

} // end of read method


} // end of CellInfoTable class
