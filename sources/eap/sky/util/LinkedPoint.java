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

import java.awt.geom.*;

/*************************************************************************
* A utility for projecting an {@link Arc} onto a plane.
* This represents a point in the plane, which is also an element in a linked
* list.
*************************************************************************/
public class LinkedPoint extends Point2D.Double {

LinkedPoint next;
LinkedPoint last;

/*************************************************************************
* Create a new point.
* @param x The X coordinate.
* @param y The Y coordinate.
*************************************************************************/
public LinkedPoint(double x, double y) {

    super(x,y);
}

/*************************************************************************
* Create a new point.
* @param point The location of the point in the plane.
*************************************************************************/
public LinkedPoint(Point2D point) {

    this(point.getX(), point.getY());

}

/**************************************************************************
* Connect two points together in a linked list.
* @param point the point to follow this on in a linked list.
**************************************************************************/
public void connectBefore(LinkedPoint point) {

    /************************
    * break old connections *
    ************************/
    if(point.last != null) point.last.disconnectNext();
    disconnectNext();

    /***********************
    * make new connections *
    ***********************/
    this.next = point;
    point.last = this;

} // end of connectBefore method

/**************************************************************************
* Returns the point following this on in a linked list.
* @return The next point.
**************************************************************************/
public LinkedPoint getNext() { return next; }

/**************************************************************************
* Returns the point before this one in a linked list.
* @return The previous point.
**************************************************************************/
public LinkedPoint getLast() { return last; }

/**************************************************************************
* Unlink this point from the one after it.
**************************************************************************/
public void disconnectNext() {

    if(next==null) return;

    next.last = null;
    next = null;

} // end of disconnectNext method

/**************************************************************************
* Returns the line connecting this point an the following one.
* @return A line from here to the next point.
**************************************************************************/
public Line2D lineToNext() {

    if(next==null) return null;

    return new Line2D.Double(this, next);

} // end of lineToNext method

/**************************************************************************
* Constructs a shape describing the path starting at this point and ending
* at the last point in the list.
* @return the shape connecting all the points after this one.
**************************************************************************/
public GeneralPath pathAfter() {

    GeneralPath path = new GeneralPath();
    path.moveTo((float)getX(), (float)getY());

  //  int count =1;

    for(LinkedPoint point = next; point != null; point = point.next) {

     //   ++count;

        if(point == this) {
            /**************************
            * we have closed the loop *
            **************************/
            path.closePath();
            break;
        }

        /************************
        * draw the line segment *
        ************************/
        path.lineTo((float)point.getX(), (float)point.getY());

    } // end of loop over points

  //  System.out.println("projected arc has "+count+" points");

    return path;


} // end of pathAfter method


} // end of LinkedPoint class
