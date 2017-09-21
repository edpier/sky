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

/*********************************************************************************
*
*********************************************************************************/
public class Edge {

ThreeVector from;
ThreeVector to;

ThreeVector center;

/*********************************************************************************
*
*********************************************************************************/
public Edge(ThreeVector from, ThreeVector to) {

    this.from = from;
    this.to = to;

} // end of constructor


/*********************************************************************************
*
*********************************************************************************/
public ThreeVector getCenter() {

    if(center == null) {
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        x = from.getX() + to.getX();
        y = from.getY() + to.getY();
        z = from.getZ() + to.getZ();

        center = new ThreeVector(x*0.5, y*0.5, z*0.5);
    }

    return center;

} // end of getCenterMethod

} // end of Edge class