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

package eap.sky.util.plane;

/************************************************************************
*
************************************************************************/
public class GridIndex extends LayoutIndex {

int x;
int y;

/************************************************************************
*
************************************************************************/
public GridIndex(int x, int y) {

    this.x = x;
    this.y = y;

} // end of constructor

/************************************************************************
*
************************************************************************/
public int hashCode() {

    int s = x+y;

    return y + (s*(s+1))/2;

} // end of hashCode method

/************************************************************************
*
************************************************************************/
public boolean equals(Object o) {

    GridIndex index = (GridIndex)o;

    return x == index.x && y == index.y;

} // end of equals method

/************************************************************************
*
************************************************************************/
public int getX() { return x; }

/************************************************************************
*
************************************************************************/
public int getY() { return y; }

/************************************************************************
*
************************************************************************/
public String toString() {

    return "("+x+","+y+")";

} // end of toString method

} // end of GridIndex class
