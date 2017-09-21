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

import java.awt.geom.*;

/*********************************************************************
*
*********************************************************************/
public class InclusiveEllipse extends Ellipse2D.Double {

/*********************************************************************
*
*********************************************************************/
public InclusiveEllipse(double x, double y, double width, double height) {

    super(x, y, width, height);

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public boolean contains(double x, double y) {


    double width = getWidth();
    if(width <= 0.0) return false;

    double height = getHeight();
    if (height <= 0.0) return false;

    double normx = (x - getX()) / width - 0.5;
    double normy = (y - getY()) / height - 0.5;

    return (normx * normx + normy * normy) <= 0.25;

} // end of conatains method


/*********************************************************************
*
*********************************************************************/
public String toString() {

   return "Ellipse["+getX()+","+getY()+","+getWidth()+","+getHeight()+"]";

} // end of toString method

} // end of InclusiveRectangle class