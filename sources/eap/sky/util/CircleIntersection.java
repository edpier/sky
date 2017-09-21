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

package eap.sky.util;




/************************************************************************
* The intersection of two circles on a unit sphere.
************************************************************************/
public class CircleIntersection {

ThreeVector perpendicular;
ThreeVector parallel;
double u;
boolean has_intersection;
boolean has_closest;


/************************************************************************
*
************************************************************************/
public CircleIntersection(Direction center1, Angle radius1,
                          Direction center2, Angle radius2 ) {

    ThreeVector n1 = new ThreeVector(center1);
    double d1 = radius1.getCos();

    ThreeVector n2 = new ThreeVector(center2);
    double d2 = radius2.getCos();

    /********************************************
    * vector products of the two normal vectors *
    * plus some other stuff we'll need below
    ********************************************/
    ThreeVector cross = n1.cross(n2);
    double dot = n1.dot(n2);

    double det = 1.0-dot*dot;
    has_closest = det>0.0;
    if(!has_closest) return;

    double cross2 = cross.dot(cross);

    /*****************************************************
    * coefficients of the line lying on the intersection *
    *****************************************************/
    double c1 = (d1 - d2*dot)/det;
    double c2 = (d2 - d1*dot)/det;


    /*********************************************************
    * find the intersection of the line with the unit sphere *
    * note that there isn't always an intersection.
    *********************************************************/
    double argument = 1.0-(c1*c1 + 2.0*c1*c2*dot + c2*c2);

    has_intersection = argument >= 0.0;
    if(has_intersection) u = Math.sqrt(argument/cross2);
    else                 u = 0.0;

    perpendicular = n1.times(c1).plus(n2.times(c2));
    parallel = cross;


} // end of constructor

/************************************************************************
*
************************************************************************/
public boolean hasIntersection() { return has_intersection; }


/************************************************************************
*
************************************************************************/
public boolean hasClosest() { return has_closest; }

/************************************************************************
*
************************************************************************/
public Direction getDirection1() {

    return perpendicular.plus(parallel.times(u)).getDirection();

} // end of getDirection1 method

/************************************************************************
*
************************************************************************/
public Direction getDirection2() {

    return perpendicular.plus(parallel.times(-u)).getDirection();

} // end of getDirection2 method

} // end of CircleIntersection class