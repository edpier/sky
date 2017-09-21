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

/*********************************************************************************
*
*********************************************************************************/
public class Face {

private List<ThreeVector> vertices;
private List<Edge> edges;
private List<Edge> read_only_edges;
private boolean closed;

ThreeVector center;

/*********************************************************************************
*
*********************************************************************************/
public Face() {

    vertices = new ArrayList<ThreeVector>();
    edges = new ArrayList<Edge>();

    read_only_edges = Collections.unmodifiableList(edges);

    closed = false;

} // end of constructor


/*********************************************************************************
*
*********************************************************************************/
public List<Edge> getEdges() { return read_only_edges; }

/*********************************************************************************
*
*********************************************************************************/
public void addVertex(ThreeVector vertex) {

    if(closed) throw new IllegalStateException("Face is closed");

    if(vertices.size()>0) {
        Edge edge = new Edge(vertices.get(vertices.size()-1), vertex);
        edges.add(edge);
    }

    vertices.add(vertex);

} // end of addVertex method

/*********************************************************************************
*
*********************************************************************************/
public void close() {

    Edge edge = new Edge(vertices.get(vertices.size()-1), vertices.get(0));
    edges.add(edge);

    closed = true;

} // end of close method

/*********************************************************************************
*
*********************************************************************************/
public int getVertexCount() { return vertices.size(); }


/*********************************************************************************
*
*********************************************************************************/
public ThreeVector getCenter() {

    if(center == null) {

        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        for(ThreeVector vertex: vertices) {
            x += vertex.getX();
            y += vertex.getY();
            z += vertex.getZ();
        }

        double factor = 1.0/getVertexCount();
        center = new ThreeVector(x*factor, y*factor, z*factor);

    } // end if we have to find the center

    return center;

} // end of getCenter method

} // end of Face class