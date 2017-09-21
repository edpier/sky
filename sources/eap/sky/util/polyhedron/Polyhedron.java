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

/***************************************************************************
*
***************************************************************************/
public class Polyhedron {

List<Face> faces;
List<Face> read_only_faces;
boolean closed;

/***************************************************************************
*
***************************************************************************/
public Polyhedron() {

    faces = new ArrayList<Face>();
    read_only_faces = Collections.unmodifiableList(faces);

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public List<Face> getFaces() { return read_only_faces; }

/***************************************************************************
*
***************************************************************************/
public void addFace(Face face) {

    if(closed) throw new IllegalStateException("Polyhedron is closed");
    faces.add(face);

} // end of addFace method

/*********************************************************************************
*
*********************************************************************************/
public void close() { closed = true; }

} // end of Polyhedron class

