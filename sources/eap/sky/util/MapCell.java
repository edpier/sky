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

/***************************************************************************
*
***************************************************************************/
public class MapCell {

Point2D mapped0;
Point2D point0;
int width;
int height;

double dxdx;
double dydx;
double dxdy;
double dydy;

/***************************************************************************
*
***************************************************************************/
public MapCell(Point2D  point0, int width, int height,
               Point2D mapped0, Point2D mapped10, Point2D mapped01 ) {

    this.point0 = (Point2D)point0.clone();
    this.mapped0 = (Point2D)mapped0.clone();

    this.width = width;
    this.height = height;

    dxdx = (mapped10.getX() - mapped0.getX())/width;
    dydx = (mapped10.getY() - mapped0.getY())/width;

    dxdy = (mapped01.getX() - mapped0.getX())/height;
    dydy = (mapped01.getY() - mapped0.getY())/height;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public int getWidth() { return width; }

/***************************************************************************
*
***************************************************************************/
public int getHeight() { return height; }

/***************************************************************************
*
***************************************************************************/
public Point2D getCorner() { return (Point2D)point0.clone(); }

/***************************************************************************
*
***************************************************************************/
public Point2D getMappedCorner() { return (Point2D)mapped0.clone(); }

/***************************************************************************
*
***************************************************************************/
public void incrementX(Point2D point) {

    point.setLocation(point.getX() + dxdx,
                      point.getY() + dydx);
}

/***************************************************************************
*
***************************************************************************/
public void incrementY(Point2D point) {

    point.setLocation(point.getX() + dxdy,
                      point.getY() + dydy);
}

/***************************************************************************
*
***************************************************************************/
public Point2D map(Point2D point) {

    double dx = point.getX() - point0.getX();
    double dy = point.getY() - point0.getY();

    double x1 = mapped0.getX() + dxdx*dx + dxdy*dy;
    double y1 = mapped0.getY() + dydx*dx + dydy*dy;

    return new Point2D.Double(x1, y1);

} // end of map method

} // end of MapCell class
