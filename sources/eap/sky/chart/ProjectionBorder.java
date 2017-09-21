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

import java.awt.*;
import java.awt.geom.*;

/****************************************************************************
*
****************************************************************************/
public class ProjectionBorder implements ChartItem {

Shape border;

Color color;

/****************************************************************************
*
****************************************************************************/
public ProjectionBorder() {

    color = Color.black;

} // end of constructor

/****************************************************************************
*
****************************************************************************/
public void update(ChartState state) {

    /****************************************************
    * get the unscaled shape of the projection's border *
    ****************************************************/
    Shape shape = state.getProjection().getBorder();

    if(shape == null) {
        /********************************
        * this projection has no border *
        ********************************/
        border = null;
    } else {
        /********************************
        * scale the projection's border *
        ********************************/
      //  AffineTransform scale = state.getScaling();
      //  border = new Area(shape).createTransformedArea(scale);
        border = state.getPlane().getMappingToPixels().map(shape, 1);
    }




} // end of update method

/****************************************************************************
*
****************************************************************************/
public void setColor(Color color) { this.color = color; }

/****************************************************************************
*
****************************************************************************/
public void paint(Chart chart, Graphics2D g2) {

    if(border==null) return;

    Color orig_color = g2.getColor();
    g2.setColor(color);

    g2.draw(border);

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

} // end of ProjectionBorder class
