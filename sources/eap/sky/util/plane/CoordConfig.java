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

/**************************************************************************
* Represents a full set of focal plane coordinates and the transformations
* between them. A CoordConfig consists of a list of {@link PlaneCoordinates}
* and a {@link ParameterSet}, which includes all the transform parameters
* which are known only at runtime.
**************************************************************************/
public class CoordConfig implements Serializable {

Map<String, PlaneCoordinates> coordinates;

ParameterSet params;

PlaneCoordinates top;
PlaneCoordinates bottom;

/**************************************************************************
* Create a new object with no coordinates.
**************************************************************************/
public CoordConfig() {

    coordinates = new HashMap<String, PlaneCoordinates>();
    params = new ParameterSet();

} // end of constructor

/**************************************************************************
*
**************************************************************************/
public void add(PlaneCoordinates coord) {

    boolean was_empty = coordinates.size() ==0;
    coordinates.put(coord.getName(), coord);

    if(was_empty) top = coord;
    bottom = coord;

} // end of add method

/**************************************************************************
*
**************************************************************************/
public static CoordConfig createSimple(int width, int height,
                                       double arcsec_per_pixel) {
    
    return createSimple(width, height, arcsec_per_pixel, 0.0, 0.0);
    
} // end of createSimple method

/**************************************************************************
*
**************************************************************************/
public static CoordConfig createSimple(int width, int height,
                                       double arcsec_per_pixel,
                                       double center_x, double center_y) {

    CoordConfig config = new CoordConfig();

    /********************
    * some calculations *
    ********************/
    double radians_per_pixel = arcsec_per_pixel/206265.0;

    double angular_width  = width  * radians_per_pixel;
    double angular_height = height * radians_per_pixel;

    double max_angular_size = Math.sqrt(angular_width *angular_width +
                                        angular_height*angular_height  );

    /*********************
    * TANGET coordinates *
    *********************/
    PlaneCoordinates coord = new PlaneCoordinates("TANGENT", null);
    config.add(coord);

    Shape bounds = new Rectangle2D.Double(-max_angular_size*0.5,
                                          -max_angular_size*0.5,
                                           max_angular_size,
                                           max_angular_size);

    PlaneSegment seg = new PlaneSegment(coord, "TAN", null, bounds, null, null);
    seg.setSegmentLayout(new IrregularLayout(seg));

    /**********************
    * ROTATED coordinates *
    **********************/
    PlaneCoordinates parent_coord = coord;
    PlaneSegment parent_seg = seg;

    coord = new PlaneCoordinates("ROTATED", parent_coord);
    config.coordinates.put(coord.getName(), coord);

    bounds = new Rectangle2D.Double(-angular_width*0.5,
                                    -angular_height*0.5,
                                     angular_width,
                                     angular_height);

    Point2D center = new Point2D.Double(0.0, 0.0);
    PlaneTransform trans = new  RotatorTransform(center, center, "rotator",
                                                 false, 0.0);
    config.addParameter("rotator");
    seg = new PlaneSegment(coord, "ROT", parent_seg, bounds, trans, null);
    seg.setSegmentLayout(new IrregularLayout(seg));

    /********************
    * FOCAL coordinates *
    ********************/
    parent_coord = coord;
    parent_seg = seg;

    coord = new PlaneCoordinates("FOCAL", parent_coord);
    config.add(coord);

    bounds = new Rectangle2D.Double(0.0, 0.0, width, height);

    trans = new AffinePlaneTransform(
            new AffineTransform(radians_per_pixel, 0.0,
                                0.0, radians_per_pixel,
                                -0.5*angular_width - radians_per_pixel*center_x,
                                -0.5*angular_height- radians_per_pixel*center_y));

    seg = new PlaneSegment(coord, "FOCAL", parent_seg, bounds, trans, null);
    seg.setSegmentLayout(new IrregularLayout(seg));

    return config;

} // end of createSimple method

/**************************************************************************
* Create a new copy of the parameter set for these coordinates.
**************************************************************************/
public ParameterSet createParameterSet() { return params.copy(); }

/**************************************************************************
* Add a new parameter to the parameter set for this object. The value
* of this parameter is initialized to zero.
**************************************************************************/
public void addParameter(String name) {

    if(!params.contains(name)) {
        params.addParameter(new TransformParameter(name, 0.0));
    }

} // end of addParameter method

/**************************************************************************
* Returns the top-most coordinates. These are the ones "closest to the sky".
* @return the top-most coordinates.
**************************************************************************/
public PlaneCoordinates getTopCoordinates() { return top; }

/**************************************************************************
* Returns the bottom-most coordinates. These are the ones "farthest to the sky".
* @return the bottom-most coordinates.
**************************************************************************/
public PlaneCoordinates getBottomCoordinates() { return bottom; }

/**************************************************************************
* Returns the names coordinates.
* @param name The name of the coordinates.
* @return the named coordinates or null if there are no such coordinates.
**************************************************************************/
public PlaneCoordinates getCoordinates(String name) {

    return (PlaneCoordinates)coordinates.get(name);

}

} // end of CoordConfig class
