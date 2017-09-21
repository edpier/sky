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

package eap.sky.util.polyhedron;

import eap.sky.util.*;

import java.util.*;
import java.io.*;

/****************************************************************************
*
****************************************************************************/
public class OFFReader {

BufferedReader reader;

int nvertices;
int nfaces;
int nedges;

List<ThreeVector> vertices;

/****************************************************************************
*
****************************************************************************/
public OFFReader(File file) throws IOException {
    vertices = new ArrayList<ThreeVector>();

    reader = new BufferedReader(new FileReader(file));

    readFormatID();

} // end of constructor


/*******************************************************************************
*
*******************************************************************************/
private String readLine() throws IOException {

    while(true) {
        String line = reader.readLine();
        if(line == null) throw new EOFException("Premature end of file");

        line = line.trim();
        if(line.length()>0 && !line.startsWith("#")) return line;


    }

} // end of readLine

/*******************************************************************************
*
*******************************************************************************/
private void readFormatID() throws IOException {

    if(!readLine().equals("OFF")) {
        throw new IOException("File does not start with OFF");
    }

} // end of readFormatID method


/*******************************************************************************
*
*******************************************************************************/
private void readCounts() throws IOException {

    StringTokenizer tokens = new StringTokenizer(readLine());

    try {
        nvertices = Integer.parseInt(tokens.nextToken());
        nfaces   = Integer.parseInt(tokens.nextToken());
        nedges   = Integer.parseInt(tokens.nextToken());
    } catch(NumberFormatException e) {
        throw (IOException)(new IOException("Could not read counts").initCause(e));
    }


} // end of readCounts method

/*******************************************************************************
*
*******************************************************************************/
private void readVertices() throws IOException {

    for(int i=0; i< nvertices; ++i) {
        StringTokenizer tokens = new StringTokenizer(readLine());

        try {
            double x = Double.parseDouble(tokens.nextToken());
            double y = Double.parseDouble(tokens.nextToken());
            double z = Double.parseDouble(tokens.nextToken());

            vertices.add(new ThreeVector(x,y,z));

        } catch(NumberFormatException e) {
            throw (IOException)(new IOException("Could not read vertex "+i)
                   .initCause(e));
        }

    } // end of loop over vertices

} // end of readVertices

/*******************************************************************************
*
*******************************************************************************/
private Face readFace() throws IOException {

    StringTokenizer tokens = new StringTokenizer(readLine());

    try {
        int count = Integer.parseInt(tokens.nextToken());
    //System.out.println("vertex count "+count);
        Face face = new Face();
        for(int i=0; i< count; ++i) {
            int index = Integer.parseInt(tokens.nextToken());
            ThreeVector vertex = vertices.get(index);
            face.addVertex(vertex);
        }

        face.close();
        return face;

    } catch(Exception e) {
        throw (IOException)(new IOException("Could not read face")
                .initCause(e));
    }




} // end of readFace method

/*******************************************************************************
*
*******************************************************************************/
public Polyhedron read() throws IOException {

    readCounts();
    readVertices();

    Polyhedron poly = new Polyhedron();

    for(int i=0; i< nfaces; ++i) {
        poly.addFace(readFace());
    }

    poly.close();

    reader.close();

    return poly;

} // end of read method

} // end of OFFReader class

