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

import java.awt.*;
import java.awt.geom.*;

/***************************************************************************
*
***************************************************************************/
public class PathItem implements ChartItem {

Color color;
Stroke stroke;

Coordinates coord;
Transform last_trans;

double flatness;

ArcPath original;
ArcPath transformed;
GeneralPath projected;
Shape scaled;

boolean fill;

/***************************************************************************
*
***************************************************************************/
public PathItem(ArcPath path, double flatness, Coordinates coord) {

    original = path;
    this.flatness = flatness;
    this.coord = coord;

    this.color = Color.black;
    this.stroke = new BasicStroke();

    this.fill = false;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public Rectangle2D getRenderedBounds() { return scaled.getBounds2D(); }

/***************************************************************************
*
***************************************************************************/
public void setColor(Color color) { this.color = color; }

/***************************************************************************
*
***************************************************************************/
public void setStroke(Stroke stroke) { this.stroke = stroke; }

/***************************************************************************
*
***************************************************************************/
public void update(ChartState state) {

    boolean redo = false;

    /*************************************
    * check if the transform has changed *
    *************************************/
    Transform trans = state.getTransformFrom(coord);
    if(last_trans == null || !trans.equals(last_trans)) {
        /*************************************************
        * yes, the transform is different from last time *
        *************************************************/
        redo = true;
        last_trans = trans;

    }

    /*********************************
    * apply the transform if need be *
    *********************************/
    try {
    if(redo) transformed = original.transform(trans);

    } catch(IllegalArgumentException e) {
        System.out.println("path coordinates "+coord);
        System.out.println("chart coordinates "+state.getPlane().getCoordinates());
        System.out.println(trans);
      //  System.out.println(trans);
       // Compositetransform composite = (CompositeTransform)trans;
      // System.exit(0);

        scaled = new Rectangle2D.Double(0,0,0,0);
        return;
    }

    /*************
    * projection *
    *************/
    redo = redo || state.projectionChanged();
    if(redo) projected = transformed.project(state.getProjection(), flatness);

   // System.out.println("projected="+projected+" redo="+redo);
    /**********
    * scaling *
    **********/
    redo = redo || state.scalingChanged();
    if(redo) scaled = state.getPlane().getMappingToPixels().map(projected, 1);

//if(redo) System.out.println("redid path scaling scaled="+projected.getBounds());

   // projected.createTransformedShape(state.getScaling());



} // end of update method



/****************************************************************************
*
****************************************************************************/
public void paint(Chart chart, Graphics2D g2) {

    if(scaled==null) return;

    Color orig_color = g2.getColor();
    Stroke orig_stroke = g2.getStroke();

    g2.setColor(color);
    g2.setStroke(stroke);

    if(fill) g2.fill(scaled);
    else     g2.draw(scaled);

    g2.setColor(orig_color);
    g2.setStroke(orig_stroke);

} // end of paint method

/****************************************************************************
*
****************************************************************************/
public boolean contains(Point2D point) {


  //  System.out.println("scaled="+scaled);

    return scaled.contains(point);
}

/*******************************************************************************
*
*******************************************************************************/
public void itemAdded(Chart chart) {}

/*******************************************************************************
*
*******************************************************************************/
public void itemRemoved(Chart chart) {}


} // end of PathItem class
