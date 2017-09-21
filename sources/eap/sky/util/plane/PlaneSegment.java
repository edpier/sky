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

import java.awt.Shape;
import java.awt.geom.*;
import java.io.*;
import java.util.*;

/*************************************************************************
* A set of axes, a boundary, and a transform.
* Each {@link PlaneCoordinates} is composed of one or more PlaneSegment.
* segments are arranged in a tree such that
* each segment has a parent and one or more children. A segment's parent belongs
* to the its coordinates parent.
*************************************************************************/
public class PlaneSegment implements Serializable {

PlaneCoordinates coordinates;
String name;

Shape bounds;

PlaneSegment parent;
List<PlaneSegment> children;
List<PlaneSegment> read_only_children;

LayoutIndex index;
SegmentLayout layout;

PlaneTransform   to_parent;
PlaneTransform from_parent;

/*************************************************************************
* Create a new segment.
* @param coordinates The coordinates to which this segment belongs.
* @param name The name of this segment, unique within its coordinates
* @param parent The segment in the parent coordinates to which we can define
*        a transform.
* @param bounds The boundary of these coordinates. Points outside this boundary
*        are not valid. Typcially the bounds are rectangular. Currently, this is
*        all the FIIS format supports.
* @param to_parent The transform to the parent segment.
*************************************************************************/
public PlaneSegment(PlaneCoordinates coordinates, String name,
                    PlaneSegment parent, Shape bounds,
                    PlaneTransform to_parent, LayoutIndex index) {

   // System.out.println("creating "+name+" to_parent = "+to_parent);

    this.name = name;
    this.parent = parent;
    this.coordinates = coordinates;
    this.bounds = bounds;
    this.to_parent = to_parent;
    this.index = index;

    if(to_parent != null) this.from_parent = to_parent.invert();
    else                  this.from_parent = null;

    /******************************************
    * add us as a child of our parent segment *
    ******************************************/
    if(parent != null) {
        /***************
        * sanity check *
        ***************/
        if(coordinates.getParent() != parent.coordinates) {
            throw new IllegalArgumentException(parent+
                                              " is not a segment of "+
                                            coordinates.getParent());
        }

        parent.children.add(this);
    }

    /*****************************************
    * add us as a segment of our coordinates *
    *****************************************/
    coordinates.addSegment(this);

    /************************
    * create the child list *
    ************************/
    children = new ArrayList<PlaneSegment>();
    read_only_children = Collections.unmodifiableList(children);

   // layout = new IrregularLayout(this);

} // end of Segment class

/*************************************************************************
*
*************************************************************************/
public LayoutIndex getLayoutIndex() { return index; }

/*************************************************************************
*
*************************************************************************/
public void setSegmentLayout(SegmentLayout layout) { this.layout = layout; }

/*************************************************************************
* Returns the name of this segment.
* @return The name of this segment.
*************************************************************************/
public String getName() { return name; }

/*************************************************************************
* Returns the full name of this segment. This prepends the names of
* parent segments to produce a name which is unique within the entire
* {@link CoordConfig}.
* @return the full name of the segment.
*************************************************************************/
public String getUniqueName() {

    String name = getUniqueNamePart();
    if(name.length()==0) return getName();
    else                 return name;


} // end of getUniqueName method

/*************************************************************************
*
*************************************************************************/
public String getUniqueNamePart() {

    if(coordinates.getSegmentCount()==1) {
        if(parent == null) return "";
        else               return parent.getUniqueNamePart();
    } else {

        if(parent==null) return name;
        else {
            String parent_name = parent.getUniqueNamePart();
            if(parent_name.length()>0) return parent_name+"/"+name;
            else                       return name;
        }
    }

} // end of getUniqueName method

/*************************************************************************
* Returns the parent of this segment.
* @return The parent segment.
*************************************************************************/
public PlaneSegment getParent() { return parent; }

/*************************************************************************
* Returnws the children of this segment.
* @return an unmodifieable view of the children of this segment.
*************************************************************************/
public List getChildren() { return read_only_children; }

/*************************************************************************
* Returns the number of children of this segment.
* @return the number of children.
*************************************************************************/
public int getChildCount() { return children.size(); }

/*************************************************************************
*
*************************************************************************/
public PlaneSegment getChild(String name) {

    for(Iterator it = children.iterator(); it.hasNext(); ) {
        PlaneSegment child = (PlaneSegment)it.next();

        if(child.getName().equals(name)) return child;
    }

    return null;

} // end of getChild method

/*************************************************************************
* Returns the bounds of this segment. Points outside this boundary are
* not valid. Typically this is used to define the extent of a detector.
* @return The bounds of the this segment.
*************************************************************************/
public Shape getBounds() {

    return bounds;

} // end of getBounds method

/*************************************************************************
* Return the set of coordinates to which this segment belongs.
*************************************************************************/
public PlaneCoordinates getCoordinates() { return coordinates; }


/*************************************************************************
*
*************************************************************************/
public PlaneTransform getTransformToParent() { return to_parent; }

/*************************************************************************
* Return the transform to a segment in the given set of coordinates.
* This method only works when transforming up, because in this case
* the destination
* segment is known before doing the transform.
* @param coord A set of coordinates closer to the sky than this segment.
* @see #transformDownTo(PlaneCoordinates, Point2D, ParameterSet)
*************************************************************************/
public PlaneTransform getTransformUpTo(PlaneCoordinates coord) {

    List<PlaneTransform> list = new ArrayList<PlaneTransform>();

    for(PlaneSegment seg = this; seg.coordinates != coord;
        seg = seg.getParent()) {

     //   System.out.println(seg.getName()+" to_parent = "+seg.to_parent);

        list.add(seg.to_parent);
    }

    return new CompositePlaneTransform(list);


} // end of getTransformUpTo method

/*************************************************************************
* Tests if a point it within the boundary of this segment.
* @return true if the point is valid.
*************************************************************************/
public boolean contains(Point2D point) {

    return bounds.contains(point);

} // end of contains method

/*************************************************************************
* Test if the given segment is an ancestor of this one in the tree structure.
* @return true if the given segment is above this one in the tree.
*************************************************************************/
public boolean isAbove(PlaneSegment coord) {

    /**************************
    * check if we're the same *
    **************************/
    if(this == coord) return false;
    coord = coord.getParent();

    /*********************
    * travel up the tree *
    *********************/
    while(coord != null ) {

        if(this == coord) return true;
        coord = coord.getParent();
    }

    return false;

} // end of isAbove method


/*************************************************************************
* Transform a point to a segment below this one.
* Note that we don't know the destination segment until we actually
* transform the point, because a segment can have multiple children.
* @param coord A set of coordinates below this one.
* @param point The point to transform.
* @param params a set of transform parameters.
* @return The transformed point and the segment in which is lies, or null if
* the transformed point does not lie within the bounds of any segment.
* @see CoordConfig#createParameterSet()
*************************************************************************/
public Location transformDownTo(PlaneCoordinates coord, Point2D point,
                                ParameterSet params) {

    Point2D result = new Point2D.Double();
    PlaneSegment seg =  transformDownTo(coord, point, result, params);

    if(seg == null) return null;
    else            return new Location(seg, result);

} // end of public transformDownTo method

// //System.out.println("down to "+coord);
//
//
//
//     /**********************
//     * check if we're done *
//     **********************/
//     if(coordinates == coord) {
//         return new Location(this,point);
//     }
//
//     /***********************************
//     * travel recursively down the tree *
//     ***********************************/
//     Location location = toChild(point, point, params);
//     if(location == null) return null;
//
//     PlaneSegment child =location.getSegment();
//     return child.transformDownTo(coord, location.getPoint(), params);
//
// } // end of transformDownTo method

/************************************************************************
*
************************************************************************/
private PlaneSegment transformDownTo(PlaneCoordinates coord, Point2D point,
                                Point2D result, ParameterSet params) {

    /**********************
    * check if we're done *
    **********************/
    if(coordinates == coord) {
        result.setLocation(point.getX(), point.getY());
        return this;
    }

    /***********************************
    * travel recursively down the tree *
    ***********************************/
    PlaneSegment child = toChild(point, result, params);
    if(child == null) return null;

    return child.transformDownTo(coord, result, result, params);

} // end of transformDownTo method

/*************************************************************************
* Transform a point to a higher set of coordinates,
* @param coord A set of higher coordinates.
* @param point The point to transform
* @param params A set of runtime parameters for the transform.
* @return The transformed point and the segment in which is lies, or null if
* the given coordinates are not above this segment.
* @see CoordConfig#createParameterSet()
*************************************************************************/
public Location transformUpTo(PlaneCoordinates coord, Point2D point,
                              ParameterSet params) {

    /**********************
    * check if we're done *
    **********************/
    if(coordinates == coord) {
        return new Location(this,point);
    }

    if(parent == null ) return null;

    /*********************************
    * travel recursively up the tree *
    *********************************/
    return parent.transformUpTo(coord, toParent(point, params), params);

} // end of transformUpTo method

/*************************************************************************
* Transform a point to the parent segment.
* @param point A point in this segment.
* @param params A set of runtime parameters for the transform
* @return a point in the parent segment.
* @see CoordConfig#createParameterSet()
*************************************************************************/
public Point2D toParent(Point2D point, ParameterSet params) {

    return to_parent.transform(point, params);
}

/*************************************************************************
* Transform to one of this segment's children.
* @param point A point in this segment.
* @param params A set of runtime parameters for the transform
* @return The child segment containing the transformed point and the
*         point itself,
* or null if the transformed point is not within the boundary of any of the
* children of this segment.
* @see CoordConfig#createParameterSet()
*************************************************************************/
private PlaneSegment toChild(Point2D point, Point2D result,
                             ParameterSet params) {

    List list = layout.childrenNear(point, params);
    if(list.size() ==0) {
        return null;

    } else if(list.size() == 1) {
        /*********
        * single *
        *********/
        PlaneSegment child = (PlaneSegment)list.get(0);

        child.from_parent.transform(point, result, params);
      //  System.out.println(child.getName()+" "+result);
        if(child.contains(result))  return child;
        else                        return null;


    } else {
        /***********
        * multiple *
        ***********/
        System.out.println("multiple");
        for(Iterator it = list.iterator(); it.hasNext(); ) {
            PlaneSegment child = (PlaneSegment)it.next();

            child.from_parent.transform(point, result, params);
            if(child.contains(result)) return child;

        } // end of loop over segments

        /**************************************************
        * if we get here the point wasn't on any of the
        * possible child segments
        **************************************************/
        return null;

    } // end of different grid cases




} // end of toChild method

/**********************************************************************
*
**********************************************************************/
public Location transformTo(PlaneCoordinates coord, Point2D point,
                            ParameterSet params) {


    if(coord.isAbove(coordinates)) {
        /************************
        * we're transforming up *
        ************************/
        return transformUpTo(coord, point, params);
    } else {
        /**************************
        * we're transforming down *
        **************************/
        return transformDownTo(coord, point, params);
    }



} // end of transformTo method

} // end of PlaneCoordinates class
