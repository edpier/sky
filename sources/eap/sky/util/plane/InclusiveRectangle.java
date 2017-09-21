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
public class InclusiveRectangle extends Rectangle2D.Double {

/*********************************************************************
*
*********************************************************************/
public InclusiveRectangle(double x, double y, double width, double height) {

    super(x, y, width, height);

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public boolean contains(double x, double y) {

    double x0 = getX();
    double y0 = getY();

    return (x >= x0 && y >= y0   &&
            x <= x0 + getWidth() &&
            y <= y0 + getHeight()  );

} // end of contains method

/*********************************************************************
*
*********************************************************************/
public String toString() {

   return "Rectangle["+getX()+","+getY()+","+getWidth()+","+getHeight()+"]";

} // end of toString method

} // end of InclusiveRectangle class