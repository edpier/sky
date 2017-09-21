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

import java.awt.*;
import java.awt.geom.*;

/***************************************************************************
* Represents a general transform from one two dimensional plane to another.
***************************************************************************/
public abstract class Mapping {

public static final IdentityMapping IDENTITY = new IdentityMapping();

private Mapping inverse;

/***************************************************************************
*
***************************************************************************/
public abstract Point2D map(Point2D point);

/***************************************************************************
* Returns the inverse of this mapping.
* @return the inverse or null if this mapping cannot be inverted - i.e. it
* is not one-to-one.
***************************************************************************/
protected abstract Mapping createInverse();

/***************************************************************************
* Returns the inverse of this mapping.
* @return the inverse or null if this mapping cannot be inverted - i.e. it
* is not one-to-one.
***************************************************************************/
public final Mapping invert() {

    if(inverse == null) {
        inverse = createInverse();
        inverse.inverse = this;
    }

    return inverse;

} // end of invert method

/***************************************************************************
*
***************************************************************************/
public Mapping makeOptimizedCombinationWith(Mapping map) { return null; }

/***************************************************************************
*
***************************************************************************/
public Mapping makeOptimizedCombinationAfter(Mapping map) { return null; }

/***************************************************************************
*
***************************************************************************/
public final Mapping combineWith(Mapping map) {

    /*************************************
    * we can do this easily, so why not? *
    *************************************/
    if(map == inverse) return IDENTITY;

    /******************************************
    * see if we have an optimized combination *
    ******************************************/
    Mapping optimized = makeOptimizedCombinationWith(map);
    if(optimized != null) return optimized;

    /*******************************************************
    * see if we have an optmized combination the other way *
    *******************************************************/
    optimized = map.makeOptimizedCombinationAfter(this);
    if(optimized != null) return optimized;

    /*****************************************************
    * can't do anything clever, so do it the generic way *
    *****************************************************/
    return new CompositeMapping(this, map);

} // end of combineWith method

/***************************************************************************
*
***************************************************************************/
protected void refineSegment(Point2D point1, Point2D point2,
                             LinkedPoint map1, double accuracy2) {


    /**********************************************
    * find the midpoint of the segment and map it *
    **********************************************/
    Point2D mid = new Point2D.Double((point1.getX()+point2.getX())*0.5,
                                     (point1.getY()+point2.getY())*0.5 );
    Point2D actual = map(mid);

    /************************************************
    * find the linear approximation of the midpoint *
    ************************************************/
    LinkedPoint map2 = map1.getNext();
    Point2D.Double linear = new Point2D.Double((map1.getX()+map2.getX())*0.5,
                                               (map1.getY()+map2.getY())*0.5 );

    /********************************
    * are we within the tollerance? *
    ********************************/
    double error2 = actual.distanceSq(linear);

//     System.out.println();
//     System.out.println("map1  ="+map1);
//     System.out.println("actual="+actual);
//     System.out.println("linear="+linear);
//     System.out.println("map2  ="+map2);
//     System.out.println("error2="+error2);

    // put in extra checks that the midpoint is not close to the endpoints
    // to avoid infinite recustion. The better solution is to properly
    // detect discontinuities and jump over them
    // -ED 2007-02-07
    if(error2 > accuracy2 &&
       actual.distanceSq(map1) > accuracy2 && actual.distanceSq(map2) > accuracy2)  {

        /********************
        * link in the point *
        ********************/
        LinkedPoint linked = new LinkedPoint(actual);
        map1.connectBefore(linked);
        linked.connectBefore(map2);

        /*********************
        * refine recursively *
        *********************/
        refineSegment(point1, mid, map1,   accuracy2);
        refineSegment(mid, point2, linked, accuracy2);

    } // end if we need to refine further

} // end of refineSegment method

/***************************************************************************
*
***************************************************************************/
public Shape map(Shape shape, double accuracy) {

    GeneralPath path = new GeneralPath();

    PathIterator it = shape.getPathIterator(null);
    double[] coords = new double[6];

    Point2D point0 = null;
    LinkedPoint mapped0 = null;
    Point2D point1 = null;
    LinkedPoint mapped1 = null;


    for( ; !it.isDone(); it.next()) {
        int type = it.currentSegment(coords);
//System.out.println(type+" "+coords[0]+" "+coords[1]);
        if(type == it.SEG_MOVETO) {
            /************************
            * close any old subpath *
            ************************/
            if(mapped0 != null) {
                path.append(mapped0.pathAfter(), false);
            }

            /**********************
            * move to a new point *
            **********************/
            point1 = new LinkedPoint(coords[0], coords[1]);
            mapped1 = new LinkedPoint(map(point1));
            point0 = point1;
            mapped0 = mapped1;

        } else if(type == it.SEG_LINETO) {
            /***************
            * line segment *
            ***************/
            Point2D point2 = new Point2D.Double(coords[0], coords[1]);
        //    System.out.println(this);
 // System.out.println("mapping "+point2);
            LinkedPoint mapped2 = new LinkedPoint(map(point2));
 // System.out.println("mapped  "+mapped2);
            mapped1.connectBefore(mapped2);

            refineSegment(point1, point2, mapped1, accuracy);

            point1 = point2;
            mapped1 = mapped2;




        } else if(type == it.SEG_CLOSE) {
            /*******************************************
            * connect back to the start of the subpath *
            *******************************************/
            Point2D point2 = point0;
            LinkedPoint mapped2 = mapped0;

            mapped1.connectBefore(mapped2);
            refineSegment(point1, point2, mapped1, accuracy);



            path.append(mapped0.pathAfter(), false);
            point0 = null;
            mapped0 = null;
            point1 = null;
            mapped1 = null;

            /*********************************************************
            * explicitly close the path in case we'd like to fill it *
            *********************************************************/
            path.closePath();


        } else {
            /***********************************
            * maybe we can handle curves later *
            ***********************************/
            throw new IllegalArgumentException("segment type "+type+
                                               " not implemented");
        }

    } // end of loop over segments

    /**************************
    * finish the last subpath *
    **************************/
    if(mapped0 != null) path.append(mapped0.pathAfter(), false);

    return path;

} // end of map shape method


} // end of Mapping method
