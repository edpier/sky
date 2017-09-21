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

import eap.sky.time.*;
import eap.sky.util.*;
import eap.sky.util.coordinates.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;

/**************************************************************************
*
**************************************************************************/
public class ChartState {

//PreciseDate time;
Coordinates coord;
//Rotation aspect;

//AffineTransform scale;
int width;
int height;

//Point2D center;

Plane plane;

boolean time_changed;
boolean coords_changed;
boolean aspect_changed;
boolean projection_changed;
boolean scale_changed;
boolean dimensions_changed;

//Rotation aspect_inverse;



//boolean to_radec_changed;


TransformCache cache;

/**************************************************************************
*
**************************************************************************/
public ChartState(ChartState last, PreciseDate time,
                  Plane plane,
                  Coordinates coord,
                   Dimension size) {

    /**************
    * copy values *
    **************/
    cache = TransformCache.makeCache(time);
    this.plane = plane;
    this.coord = coord;

//     this.aspect = aspect;
//     aspect_inverse = (Rotation)aspect.invert();

    this.width  = (int)size.getWidth();
    this.height = (int)size.getHeight();

   // this.center = center;
//     double offsetx =  width*0.5 - center.getX() *scale;
//     double offsety = height*0.5 + center.getY() *scale;
//
//     this.scale = new AffineTransform(scale, 0.0, 0.0, -scale,
//                                      offsetx, offsety);


   // Transform to_radec = cache.getTransform(coord, Coordinates.RA_DEC);

    /********************
    * check for changes *
    ********************/
    if(last == null) {

        time_changed       = true;
        coords_changed     = true;
        aspect_changed     = true;
        projection_changed = true;
        scale_changed      = true;
        dimensions_changed = true;
       // to_radec_changed   = true;
    } else {
        time_changed       = ! time.equals(last.cache);
        coords_changed     = ! coord.equals(last.coord);
        aspect_changed     = ! getAspect().equals(last.getAspect());
        projection_changed = !getProjection().equals(last.getProjection());
        scale_changed      = ! plane.getMappingToPixels().equals(
                               last.plane.getMappingToPixels());
        dimensions_changed = (width != last.width || height != last.height);
      //  to_radec_changed   = to_radec.equals(
     //                              last.getTransformTo(Coordinates.RA_DEC));
    }


} // end of constructor

/**************************************************************************
*
**************************************************************************/
public ChartState changeTime(ChartState last_state, PreciseDate time) {

    return new ChartState(last_state, time, plane, coord,
                          new Dimension(width, height) );

} // end of changeTime method




/**************************************************************************
*
**************************************************************************/
private Rotation getAspect() {

    return ((PointingCoordinates)plane.getCoordinates()).getPointingSource()
                                                     .getRotation(cache);
} // end of getAspect method

/**************************************************************************
*
**************************************************************************/
public boolean timeChanged() { return time_changed; }

/**************************************************************************
*
**************************************************************************/
public boolean coordinatesChanged() { return coords_changed; }


/**************************************************************************
*
**************************************************************************/
public boolean aspectChanged() { return aspect_changed; }

/**************************************************************************
*
**************************************************************************/
public boolean projectionChanged() { return projection_changed; }

/**************************************************************************
*
**************************************************************************/
public boolean scalingChanged() { return scale_changed; }

/**************************************************************************
*
**************************************************************************/
public boolean dimensionsChanged() { return dimensions_changed; }


/**************************************************************************
*
**************************************************************************/
// public boolean transformChanged() {
// 
//     return to_radec_changed || aspect_changed;
// }

/**************************************************************************
*
**************************************************************************/
public boolean anyChanged() {

    return  time_changed       ||
            coords_changed     ||
            aspect_changed     ||
            projection_changed ||
            scale_changed      ||
            dimensions_changed ;

} // end of anyChanged method

/**************************************************************************
*
**************************************************************************/
public PreciseDate getTime() {

    return cache;

} // end of getTime method

/**************************************************************************
*
**************************************************************************/
public PreciseDate getTime(TimeSystem system) {

    return system.convertDate(cache);

} // end of getTime method

/**************************************************************************
* returns the transform from the Chart's coordinates to the given coordinates
**************************************************************************/
public Transform getTransformTo(Coordinates coord) {

    return cache.getTransform(plane.getCoordinates(), coord);

} // end of getTransformTo method

/**************************************************************************
*
**************************************************************************/
public Transform getTransformFrom(Coordinates coord) {

    return cache.getTransform(coord, plane.getCoordinates());

} // end of getTransformFrom method


/**************************************************************************
*
**************************************************************************/
public Projection getProjection() { return plane.getProjection(); }


/**************************************************************************
*
**************************************************************************/
//public AffineTransform getScaling() { return scale; }

/**************************************************************************
*
**************************************************************************/
public Plane getPlane() { return plane; }

/**************************************************************************
*
**************************************************************************/
public int getWidth() { return width; }

/**************************************************************************
*
**************************************************************************/
public int getHeight() { return height; }

/**************************************************************************
*
**************************************************************************/
public Rectangle2D getBounds() {

    return new Rectangle2D.Double(0.0, 0.0, width, height);

}

/**************************************************************************
*
**************************************************************************/
public Point2D getCenter() {

    return new Point2D.Double(width*0.5, height*0.5);

} // end of getCenter method

/**************************************************************************
* returns the direction in the given coordinates corresponding to the
* center of the projection.
**************************************************************************/
public Direction getCenterDirection(Coordinates coord) {

    Direction dir = plane.toDirection(getCenter());
    if(dir == null) return null;

    return getTransformTo(coord).transform(dir);

} // end of getCenterDirection method

/**************************************************************************
* Tests if a given pixel location has valid coordinates
**************************************************************************/
public boolean isOnSky(Point2D point) {

    return plane.toDirection(point) != null;

} // isOnSky

/**************************************************************************
* Transform a given point in the display to a direction in the given\
* coordinate system.
**************************************************************************/
public Direction toCoordinates(Point2D point, Coordinates coord) {


    Direction dir = plane.toDirection(point);
    if(dir == null) return null;
    
    return getTransformTo(coord).transform(dir);


} // end of toCoordinates method

/**************************************************************************
* Converts from sky coordinates to display coordinates
**************************************************************************/
public Point2D toDisplay(Direction dir, Coordinates coord) {

    Direction transformed = getTransformFrom(coord).transform(dir);
    return plane.toPixels(transformed);

//     Point2D unscaled = projection.project(transformed);
//     return scale.transform(unscaled, null);


} // end of toCoordinates method

/**************************************************************************
* Buffered image whose dimensions match the viewable area of the chart.
* The pixels are initialized to all be transparent.
**************************************************************************/
public BufferedImage createBuffer() {

    BufferedImage image = new BufferedImage(width, height,
                                            BufferedImage.TYPE_INT_ARGB);

    /*******************************
    * init the background to clear *
    *******************************/
    Color clear = new Color(0,0,0,0);

    Graphics2D g2 = image.createGraphics();
    g2.setColor(clear);
    g2.fill(new Rectangle2D.Double(0,0, width, height));
    g2.setColor(Color.black);

    return image;

} // end of createBuffer method


} // end of ChartState class
