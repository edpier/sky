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

import java.util.*;
import java.io.*;

/*************************************************************************
*
*************************************************************************/
public class CellInfo {

String name;
boolean leaf;
float dimmest;

/*************************************************************************
*
*************************************************************************/
public CellInfo(String name, boolean leaf, float dimmest) {

    this.name = name;
    this.leaf = leaf;
    this.dimmest = dimmest;

} // end of constructor


/*************************************************************************
*
*************************************************************************/
public String getName() { return name; }

/*************************************************************************
*
*************************************************************************/
public boolean isLeaf() { return leaf; }

/*************************************************************************
*
*************************************************************************/
public float getDimmestMagnitude() { return dimmest; }

/*************************************************************************
*
*************************************************************************/
public static CellInfo read(BufferedReader reader) throws IOException {

    String line = reader.readLine();
    if(line == null) return null;

    StringTokenizer tokens = new StringTokenizer(line);

    String name = tokens.nextToken();
    boolean leaf = tokens.nextToken().equals("T");
    float dimmest = Float.parseFloat(tokens.nextToken());

    return new CellInfo(name, leaf, dimmest);

} // end of read method

/*************************************************************************
*
*************************************************************************/
public void write(PrintWriter writer) {

    writer.print(name);
    if(leaf) writer.print(" T ");
    else     writer.print(" F ");
    writer.println(dimmest);

} // end of write method

/*************************************************************************
*
*************************************************************************/
public static CellInfo read(DataInput in) throws IOException {


    String name = in.readUTF();
    boolean leaf = in.readBoolean();
    float dimmest = in.readFloat();

    return new CellInfo(name, leaf, dimmest);

} // end of read method

/*************************************************************************
*
*************************************************************************/
public void write(DataOutput out) throws IOException {

    out.writeUTF(name);
    out.writeBoolean(leaf);
    out.writeFloat(dimmest);

} // end of write method


} // end of CellInfo class
