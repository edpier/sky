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
import java.util.zip.*;

/*************************************************************************
* Represents the root cell for a Hierarchical Triangular Mesh (HTM) tesselation
* of the sky. The root cell covers the entire celestial sphere.
* @see HTMCell
*************************************************************************/
public class HTMRoot extends Cell {

/***************************************************************************
* Returns null, since this cell covers the enture sky.
* @return null
***************************************************************************/
public ArcPath getBoundary() { return null; }


/*************************************************************************
* Returns "root".
* @return The string "root".
*************************************************************************/
public String getName() { return "root"; }

/*************************************************************************
* Returns a point in this cell. Since the cell covers the entire sky, the
* point is arbitrary.
* @return the X axis.
*************************************************************************/
public Direction getCenter() { return Direction.X_AXIS; }

/*************************************************************************
* Returns 180 degrees, since this cell ccovers the entire sky
* @return {@link Angle#ANGLE180}
*************************************************************************/
public Angle getRadius() { return Angle.ANGLE180; }

/*************************************************************************
* Always returns true, since this cell convers the entire sky.
* @return true.
*************************************************************************/
public boolean contains(Direction dir) { return true; }

/**************************************************************************
* Creates the top level HTM children consisting of the faces of an octohedron
* projected onto the sphere. The octohedron has vertices at each pole.
**************************************************************************/
protected void createChildren() {

    Direction x_plus = Direction.X_AXIS;
    Direction y_plus = Direction.Y_AXIS;
    Direction z_plus = Direction.Z_AXIS;

    Direction x_minus = x_plus.oppositeDirection();
    Direction y_minus = y_plus.oppositeDirection();
    Direction z_minus = z_plus.oppositeDirection();

    addChild(new HTMCell("S0", x_plus , z_minus, y_plus ));
    addChild(new HTMCell("S1", y_plus , z_minus, x_minus));
    addChild(new HTMCell("S2", x_minus, z_minus, y_minus));
    addChild(new HTMCell("S3", y_minus, z_minus, x_plus ));
    addChild(new HTMCell("N0", x_plus , z_plus, y_minus));
    addChild(new HTMCell("N1", y_minus, z_plus, x_minus));
    addChild(new HTMCell("N2", x_minus, z_plus, y_plus ));
    addChild(new HTMCell("N3", y_plus , z_plus, x_plus ));

} // end of initChildren method

/*************************************************************************
* Returns a string representation of this cell.
* @return a string representation of this cell.
*************************************************************************/
public String toString() { return "HTMCell "+getName(); }

} // end of HTMRoot class
