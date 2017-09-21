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

import java.util.*;
import java.awt.geom.*;

/***********************************************************************
* A polygon on a sphere. This class encapsulates a collection of connected
* {@link Arc}s.
***********************************************************************/
public class ArcPath {

Arc begin;
Arc end;

boolean loop;

HashSet<Projection> split_for;

/***********************************************************************
* Create a new path.
***********************************************************************/
private ArcPath() {

    split_for = new HashSet<Projection>();

}

/***********************************************************************
* Create a new path consisting of the arcs linked to the given one.
* @param begin The first arc in the linked list of arcs which will describe
* this path.
***********************************************************************/
public ArcPath(Arc begin) {

    this();

    this.begin = begin;

    loop = begin.getLast() != null;

    /***************
    * find the end *
    ***************/
    end=begin;
    while(end.getNext() != begin && end.getNext() != null) {
        end = end.getNext();
    }

    /**********************************
    * make sure this really is a loop *
    **********************************/
    if(loop  && end.getNext() == null) {
        throw new IllegalArgumentException(begin+
                                     " is not the beginning of an arc");
    }

} // end of public constructor

/***********************************************************************
* Create a new path.
* @param the first arc in the path
* @param end the last arc in the path. Note this must be linked to the first arc.
* @param loop True if the path is closed.
***********************************************************************/
private ArcPath(Arc begin, Arc end, boolean loop) {

    this();

    this.begin = begin;
    this.end = end;
    this.loop = loop;
}


/***********************************************************************
* Apply a rotation to the entire path.
* @param rotation The rotation to apply.
***********************************************************************/
public ArcPath rotate(Rotation rotation) {

    Arc first_rotated = null;
    Arc previous = null;
    for(Arc arc = begin; ; arc = arc.getNext() ) {

//System.out.println("            "+arc);

        Arc rotated = arc.rotate(rotation);
        if(previous != null) previous.connectBefore(rotated);

        previous = rotated;
        if(first_rotated==null) first_rotated = rotated;

        if(arc==end) break;
    }

    /***************************
    * close the loop if needed *
    ***************************/
    if(loop) previous.connectBefore(first_rotated);

    return new ArcPath(first_rotated, previous, loop);

} // end of rotate method

/**************************************************************************
*
**************************************************************************/
public ArcPath transform(Transform trans) {

    Arc first_transformed = null;
    Arc previous = null;
    for(Arc arc = begin; ; arc = arc.getNext() ) {

        Arc transformed = arc.transform(trans);
        if(previous != null) previous.connectBefore(transformed);

        previous = transformed;
        if(first_transformed==null) first_transformed = transformed;

        if(arc==end) break;
    }

    /***************************
    * close the loop if needed *
    ***************************/
    if(loop) previous.connectBefore(first_transformed);

    return new ArcPath(first_transformed, previous, loop);

} // end of flip method

/**************************************************************************
* Returns true if the path has been split for the cuts in the given projection.
* @see Arc#isSplitFor(Projection)
**************************************************************************/
public boolean isSplitFor(Projection projection) {

    return split_for.contains(projection);

}

/***********************************************************************
* Split this path so that no arcs intersect the cuts of the projection
* in the middle. This is necessary before projecting the path.
* @see Arc#split(Projection)
***********************************************************************/
public void split(Projection projection) {

    /********************************
    * check if we are already split *
    ********************************/
    if(isSplitFor(projection)) return;

    /*********************************************
    * mark that we are split for this projection *
    *********************************************/
    split_for.add(projection);


    /*********************
    * split all the arcs *
    *********************/
    Arc arc = begin;
    while(true) {
        /************************************
        * get the next arc before splitting *
        ************************************/
        Arc next = arc.getNext();

        /****************
        * split the arc *
        ****************/
        Arc split = arc.split(projection);

        /****************************************************
        * change the start of the arc to the start of the
        * split arc
        ****************************************************/
        if(arc == begin) begin = split;

        if(arc == end) {
            /***************************************************
            * we just processed the last of the original arcs
            * so we need to find the end of the split arcs
            ***************************************************/
            if(loop) {
                end = split;
                while(end.getNext() != begin) end = end.getNext();
            } else {
                end = split;
                while(end.getNext() != null) end = end.getNext();
            }

            /********************
            * and now were done *
            ********************/
            break;
        } // end if we just split the last original arc

        /*************************************
        * increment to the next original arc *
        *************************************/
        arc = next;

    } // end of loop over arcs


} // end of split method

/**************************************************************************
* Project this path onto the plane.
* @param projection The projection to apply.
* @param flatness The maximum deviationbetween the returned shape and the true
* projection in the plane.
* @see Arc#project(Projection, double)
**************************************************************************/
public GeneralPath project(Projection projection, double flatness) {

    /*********************************************
    * make sure we are split for this projection *
    *********************************************/
    split(projection);


    /****************************************************************
    * square the flatness so we don't have to square root the error *
    ****************************************************************/
    double flatness2 = flatness*flatness;

    /***********************
    * project all the arcs *
    ***********************/
    GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
    for(Arc arc = begin; ; arc = arc.getNext() ) {

        /*************************************************
        * make sure the arc is split for this projection *
        *************************************************/
        GeneralPath subpath =arc.project(projection, flatness2);



        /**************************************************
        * get the distance between the current point and
        * the start of the new subpath. If they are in the
        * same pixel, we will logically connect the two
        * subpaths
        **************************************************/
        boolean connect;
        Point2D last = path.getCurrentPoint();
        if(last == null) connect = false;
        else {
            /*****************************************
            * get the first point in the new subpath *
            *****************************************/
            double[] coords = new double[6];
            subpath.getPathIterator(null).currentSegment(coords);
            Point2D first = new Point2D.Double(coords[0], coords[1]);
            double dist = last.distanceSq(first);

            connect = dist <= 1.0;

        } // end if this is not the first subpath


        path.append(subpath, connect);

        if(arc == end) break;

    } // end of loop over arcs

    if(loop) path.closePath();


    return path;

} // end of project method





} // end of ArcPath class
