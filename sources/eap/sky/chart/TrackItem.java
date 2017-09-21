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

package eap.sky.chart;

import eap.sky.util.*;
import eap.sky.util.coordinates.*;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;

/*************************************************************************
*
*************************************************************************/
public class TrackItem implements ChartItem {

Coordinates coord;
java.util.List<ProjectedPoint> points;
GeneralPath path;

Color color;

/*******************************************************************************
*
*******************************************************************************/
public TrackItem(Coordinates coord, Color color) {

    this.coord = coord;
    this.color = color;

    points = Collections.synchronizedList(new ArrayList<ProjectedPoint>());

} // end of constructor

/*****************************************************************************
*
*****************************************************************************/
public void addPoint(Direction dir) {

    points.add(new ProjectedPoint(dir, coord));

} // end of addPoint method

/*****************************************************************************
*
*****************************************************************************/
public void clear() { points.clear(); }

/*****************************************************************************
*
*****************************************************************************/
public void update(ChartState state) {

    /************************
    * update all the points *
    ************************/
    GeneralPath path = new GeneralPath();
    synchronized(points) {
        for(Iterator it = points.iterator(); it.hasNext(); ) {
            ProjectedPoint point = (ProjectedPoint)it.next();
            point.update(state);

            float x = (float)point.getX();
            float y = (float)point.getY();
            if(path.getCurrentPoint() == null) path.moveTo(x,y);
            else                               path.lineTo(x,y);
        }
    } // end of synchronized block


    this.path = path;


} // end of update method

/*****************************************************************************
*
*****************************************************************************/
public void paint(Chart chart, Graphics2D g2) {

    if(path == null) return;

    Color orig_color = g2.getColor();
    g2.setColor(color);

    g2.draw(path);

    g2.setColor(orig_color);

} // end of paint method

/*******************************************************************************
*
*******************************************************************************/
public void itemAdded(Chart chart) {}

/*******************************************************************************
*
*******************************************************************************/
public void itemRemoved(Chart chart) {}


} // end of TrackClass
