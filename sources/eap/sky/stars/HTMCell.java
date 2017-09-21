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

import eap.sky.util.*;

import java.util.*;
import java.util.zip.*;

/***************************************************************************
* Represents a cell in a Hierarchical Triangular Mesh tesselation of the
* sky. This is a tesselation of the sky used by the Sloan Digital Sky Survey.
* See <a href="http://skyserver.org/htm">http://skyserver.org/htm</a>.
* At the top level, it consists of the projected faces of an octohedron with
* vertices at the poles. You construct sucessive levels by connecting the
* midpoints of the sides of each triangle to create four new triangles.
* <p>
* We add an additional top level to the hierarchy, which is a root cell
* covering the entire sphere. So to create an Hierarchical Triangular Mesh,
* you should create a {@link HTMRoot} object, which can then create the
* levels of refinement as needed.
* @see HTMRoot
***************************************************************************/
public class HTMCell extends Cell {

Direction corner0;
Direction corner1;
Direction corner2;

Direction center;

ThreeVector cross01;
ThreeVector cross12;
ThreeVector cross20;

Angle radius;

String name;

/***************************************************************************
* Create a new HTM cell with the given name and corners.
* @param name The name of the new cell
* @param corner0 One of the vertices of the triangle.
* @param corner1 One of the vertices of the triangle.
* @param corner2 One of the vertices of the triangle.
***************************************************************************/
protected HTMCell(String name,
                  Direction corner0, Direction corner1, Direction corner2) {

    this.name = name;

    this.corner0 = corner0;
    this.corner1 = corner1;
    this.corner2 = corner2;

    ThreeVector v0 = new ThreeVector(corner0);
    ThreeVector v1 = new ThreeVector(corner1);
    ThreeVector v2 = new ThreeVector(corner2);

    center = v0.plus(v1).plus(v2).getDirection();

    /*****************************************
    * cross products used for determining if
    * a point is inside
    *****************************************/
    cross01 = v0.cross(v1);
    cross12 = v1.cross(v2);
    cross20 = v2.cross(v0);

    /*************************************************
    * determine the radius by finding the largest
    * angle between the center and any of the points
    * maybe the corners are equidistant?
    *************************************************/
    radius = center.angleBetween(corner0);

    Angle angle =center.angleBetween(corner1);
    if(angle.compareTo(radius) >0 ) radius = angle;

    angle =center.angleBetween(corner2);
    if(angle.compareTo(radius) >0 ) radius = angle;

} // end of constructor

/**************************************************************************
* Returns the center of the triangle.
* @return The direction of the vector sum of the unit vectors pointing
* toward each corner.
**************************************************************************/
public Direction getCenter() { return center; }

/**************************************************************************
* Returns the angular distance from the center to the farthest corner.
**************************************************************************/
public Angle getRadius() { return radius; }

/***************************************************************************
* Returns the triangular boundary of the cell.
* @return The triangular boundary of the cell.
***************************************************************************/
public ArcPath getBoundary() {

    Arc arc01 = new Arc(corner0, corner1);
    Arc arc12 = new Arc(corner1, corner2);
    Arc arc20 = new Arc(corner2, corner0);

    arc01.connectBefore(arc12);
    arc12.connectBefore(arc20);
    arc20.connectBefore(arc01);

    return new ArcPath(arc01);

} // end of getBoundary method

/*************************************************************************
* Returns the name of the cell. We follow the Sloan naming convention.
* The top level of subdivision are named N0 through N3, for the triangles
* in the northern hemisphere, and S0 through S3 for the triangles in the
* southern hemisphere. Then sucessive refinements append 0 through 3 to the
* name of the parent cell.
* @return the name of the cell in the standard Sloan convention.
*************************************************************************/
public String getName() { return name; }


/*************************************************************************
* Returns true if this cell contains the given point.
* This method performs up to three vector cross products.
* @param dir A point on the celestial sphere.
* @return true if the point is inside the cell or on the border.
*************************************************************************/
public boolean contains(Direction dir) {

    ThreeVector v = new ThreeVector(dir);

    if(cross01.dot(v) < 0.0 ) return false;
    if(cross12.dot(v) < 0.0 ) return false;
    if(cross20.dot(v) < 0.0 ) return false;

    return true;

} // end of contains method

/**************************************************************************
* Create a new child cell and add it to the list of this cell's children.
**************************************************************************/
private void addChild(String name, Direction corner0, Direction corner1,
                                   Direction corner2) {

    addChild(new HTMCell(name, corner0, corner1, corner2));

} // end of addChild method

/**************************************************************************
* Create and register the four children of this cell.
**************************************************************************/
protected void createChildren() {

    ThreeVector v0 = new ThreeVector(corner0);
    ThreeVector v1 = new ThreeVector(corner1);
    ThreeVector v2 = new ThreeVector(corner2);

    Direction mid01 = v0.plus(v1).getDirection();
    Direction mid12 = v1.plus(v2).getDirection();
    Direction mid20 = v2.plus(v0).getDirection();

    addChild(name+"0", corner0, mid01  , mid20  );
    addChild(name+"1", mid01  , corner1, mid12  );
    addChild(name+"2", mid20  , mid12  , corner2);
    addChild(name+"3", mid01  , mid12  , mid20  );

} // end of createChildren method


/*************************************************************************
* Returns a string representation of this cell.
* @return A string representation of this cell.
*************************************************************************/
public String toString() { return "HTMCell "+getName(); }




} // end of HTMCell class
