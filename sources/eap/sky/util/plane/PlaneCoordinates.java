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
* Represents a particular set of two dimensional coordinates, primarily
* used to describe an instrument focal plane. You usually use more than one
* set of coordinates to describe an instrument. Each coordinates may take into
* account the effects of different elements in the optical path, or may
* be used to describe a hierarchical structure of the detector itself.
* <p>
* A set of coordinates for an instrument must be arranged in an ordered list.
* So each PlaneCoordinates object has a parent, which is the next closest
* coordinates system to the sky, and a child which is one step farther form the
* sky.
* <p>
* A coordinates system is composed of one or more {@link PlaneSegment}
* objects. Each segment describes a set of axes, the bounds for the coordinates,
* and the transform to a segment in the parent coordinates.
* Typically, you use multiple segments to describe individual moasiced pieces
* of a detector.
*************************************************************************/
public class PlaneCoordinates implements Serializable {

String name;

PlaneCoordinates parent;
PlaneCoordinates child;

List<PlaneSegment> segments;
List<PlaneSegment> read_only_segments;



/*************************************************************************
* Create a new set of coordinates with no segments.
* @param name The unique name of this set of coordinates. You can use this name
* to retrieve this object from a {@link CoordConfig}.
* @param parent The parent of this coordinate system in a linked list structure.
*************************************************************************/
public PlaneCoordinates(String name, PlaneCoordinates parent) {

    this.name   = name;
    this.parent = parent;
    this.child  = null;

    if(parent != null) {
        /*****************************************
        * register us as the child of the parent *
        *****************************************/
        if(parent.child != null) {
            throw new IllegalArgumentException(parent.getName()+
                                                  " already has a child. Can't add "+name);
        }

        parent.child = this;
    }



    segments = new ArrayList<PlaneSegment>();
    read_only_segments = Collections.unmodifiableList(segments);

} // end of constructor

/*************************************************************************
* Returns the unique name of this coordinate set.
* @return the name of this object.
*************************************************************************/
public String getName() { return name; }

/*************************************************************************
* Returns the next closest set of coordinates to the sky.
* @return the parent coordinates.
*************************************************************************/
public PlaneCoordinates getParent() { return parent; }

/*************************************************************************
* Returns the next farthest set of coordinates from the sky.
* @return the child coordinates.
*************************************************************************/
public PlaneCoordinates getChild() { return child; }

/*************************************************************************
* Add a segment to this coordinate set. Each PlaneCoordinates must have at
* least one segment.
*************************************************************************/
public void addSegment(PlaneSegment seg) {

    segments.add(seg);
}

/*************************************************************************
* Returns the first segment. This is useful if the coordinates have only
* one segment.
* @return The first segment which was added to this set of coordinates.
*************************************************************************/
public PlaneSegment getSegment() { return (PlaneSegment)segments.get(0); }

/*************************************************************************
* Returns the segments for these coordinates.
* @return An unmodifiable view of the segments for these coordinates listed
* in the order in which they were added.
* @see #addSegment(PlaneSegment)
*************************************************************************/
public List<PlaneSegment> getSegments() { return read_only_segments; }

/*************************************************************************
* Returns the number of segments for these coordinates
* @return The number of segments.
*************************************************************************/
public int getSegmentCount() { return segments.size(); }

/*************************************************************************
* Retrieves a segment by name.
* @param name The name of one of these coordinates segments.
* @return The named segment or null if there is no such segment.
*************************************************************************/
public PlaneSegment getSegment(String name) {

    for(Iterator it = segments.iterator(); it.hasNext(); ) {
        PlaneSegment seg = (PlaneSegment)it.next();

        if(seg.getUniqueName().equals(name)) return seg;
    }

    return null;

} // end of getSegment method

/*************************************************************************
*
*************************************************************************/
public boolean isAbove(PlaneCoordinates coord) {

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
* Load a set of segments from a FITS fiel extension. You generally do not
* call this method directly, but instead use a {@link CoordConfig} to read
* all the coordinates for a given instrument.
* @throws IOException if there was trouble reading.
*************************************************************************/
// public void read(FitsHDU hdu) throws IOException {
// 
//     FitsHeader head = hdu.getHeader();
// 
//     String name = head.card("COORD").stringValue();
// 
//     FitsBinTableData table = (FitsBinTableData)hdu.getData();
//     int nrows = table.getRowCount();
// 
//     int  parent_col = table.findColumn("PARENT");
//     int segment_col = table.findColumn("SEGMENT");
//     int    minx_col = table.findColumn("MINX");
//     int    maxx_col = table.findColumn("MAXX");
//     int    miny_col = table.findColumn("MINX");
//     int    maxy_col = table.findColumn("MAXX");
// 
//     /*****************
//     * loop over rows *
//     *****************/
//     for(int row = 0; row < nrows; ++row) {
// 
//         String parent_name = (String)table.getValueAt(row, parent_col);
//         String segment = (String)table.getValueAt(row, segment_col);
// 
//         double minx = ((Double)table.getValueAt(row, minx_col)).doubleValue();
//         double maxx = ((Double)table.getValueAt(row, maxx_col)).doubleValue();
//         double miny = ((Double)table.getValueAt(row, miny_col)).doubleValue();
//         double maxy = ((Double)table.getValueAt(row, maxy_col)).doubleValue();
// 
//         double width  = maxx - minx;
//         double height = maxy - miny;
// 
//         Shape bounds = new Rectangle2D.Double(minx, miny, width, height);
// 
//     } // end of loop over rows
// 
// } // end of read method


} // end of PlaneCoordinates class
