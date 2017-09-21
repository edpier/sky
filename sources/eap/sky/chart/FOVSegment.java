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
import eap.sky.util.plane.*;
import eap.sky.util.coordinates.*;

import java.util.*;
import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

/**************************************************************************
*
**************************************************************************/
public class FOVSegment {

PlaneSegment seg;
Collection<FOVSegment> children;

Shape reprojected;
FixedLabel label;

Color color;


boolean paint_children;
boolean paint_label;

boolean fill;
boolean stroke;

/**************************************************************************
*
**************************************************************************/
public FOVSegment(PlaneSegment seg) {

    this.seg = seg;

    label = new FixedLabel(null, seg.getUniqueName());

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public PlaneSegment getSegment() { return seg; }

/***********************************************************************
*
***********************************************************************/
private void initChildren() {

    if(children != null) return;

    children = new ArrayList<FOVSegment>();
    for(Iterator it = seg.getChildren().iterator(); it.hasNext(); ) {
        PlaneSegment seg = (PlaneSegment)it.next();

        children.add(new FOVSegment(seg));
    } // end of loop over children

} // end of initChildren method

/**************************************************************************
*
**************************************************************************/
public void update(ImageParams params, ChartState state, Color color,
                   boolean fill, boolean stroke) {

    this.color = color;
    this.fill = fill;
    this.stroke = stroke;


    Mapping map = params.getMapping(seg, state.getPlane());
    reprojected = map.map(seg.getBounds(), 1);

    /***************
    * get our size *
    ***************/
    Rectangle2D bounds = reprojected.getBounds();
    double width = bounds.getWidth();
    double height = bounds.getHeight();

    double size = 0.0;
    if(size < width ) size = width;
    if(size < height) size = height;




    /*********************************************
    * decide whether we should draw our children *
    *********************************************/
    paint_children = size > 100.0 && seg.getChildCount() > 0 &&
                     bounds.intersects(state.getBounds());


    boolean child_labeled = false;
    if(paint_children) {

        /************************************
        * make sure the children are set up *
        ************************************/
        initChildren();

        /**************************
        * update all the children *
        **************************/
        for(Iterator it = children.iterator(); it.hasNext(); ) {
            FOVSegment child = (FOVSegment)it.next();

            child.update(params, state, color, fill, stroke);

            if(child.paint_label) child_labeled=true;

        } // end of loop over children
    } // end if we need to update the children

    /*******************************************
    * decide whether we should paint the label *
    *******************************************/
    if(child_labeled) paint_label = false;
    else              paint_label = width > label.getBounds().getWidth()*1.5;

    /******************************************
    * never label if there's only one segment *
    ******************************************/
    if(seg.getCoordinates().getSegmentCount() == 1) paint_label = false;

    if(paint_label) {
        label.setPosition(new Point2D.Double(bounds.getCenterX(),
                                             bounds.getCenterY()));
    }



} // end of update method

/***********************************************************************
*
***********************************************************************/
public void paint(Chart chart, Graphics2D g2) {

    /*******
    * self *
    *******/
    if(!paint_children && reprojected != null) {
    // System.out.println("painting "+seg.getUniqueName()+" fill? "+fill+" stroke? "+stroke);

        /****************************
        * save original color state *
        ****************************/
        Color orig = g2.getColor();
        g2.setColor(color);

        /*******
        * fill *
        *******/
        if(fill)   g2.fill(reprojected);

        /*********
        * stroke *
        *********/
        if(stroke) {
            if(fill) g2.setColor(Color.black);
            g2.draw(reprojected);
        }

        /*******************************
        * restore original color state *
        *******************************/
        g2.setColor(orig);
    }

    /***********
    * children *
    ***********/
    if(paint_children) {
        for(Iterator it = children.iterator(); it.hasNext(); ) {
            FOVSegment child = (FOVSegment)it.next();

            child.paint(chart, g2);
        }

    }

    /********
    * label *
    ********/
    if(paint_label) label.paint(chart, g2);

} // end of paint method

/**************************************************************************
*
**************************************************************************/
public boolean isUnder(Point2D point) {

//System.out.println("is under: "+seg.getName());

    if(paint_children) {
        /*****************************
        * refer this to the children *
        *****************************/
   //     System.out.println("refering to children");
        for(Iterator it = children.iterator(); it.hasNext(); ) {
            FOVSegment child = (FOVSegment)it.next();

            if(child.isUnder(point)) return true;
        }

        return false;

    } else {
        /*****************
        * check this one *
        *****************/
        return reprojected.contains(point);
    }


} // end of isUnder method

} // end of FOVSegment class
