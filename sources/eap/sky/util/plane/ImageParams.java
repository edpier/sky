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

import eap.sky.time.*;
import eap.sky.util.*;
import eap.sky.util.coordinates.*;

import java.awt.geom.*;
import java.io.*;

/*************************************************************************
*
*************************************************************************/
public class ImageParams implements Serializable {

CoordConfig config;

PlaneCoordinates tan_coord;
PlaneSegment tan_seg;
ParameterSet params;

Direction ra_dec;
Angle pa;
TransformCache time;
AzAlt az_alt;

Transform trans;
Rotation rot;

Transform trans_inverse;
Rotation rot_inverse;

/*************************************************************************
*
*************************************************************************/
public ImageParams(CoordConfig config) {

    this.config = config;

    tan_coord = config.getTopCoordinates();
    tan_seg = tan_coord.getSegment();
    params = config.createParameterSet();



} // end of constructor

/*************************************************************************
*
*************************************************************************/
public CoordConfig getCoordConfig() { return config; }

/*************************************************************************
*
*************************************************************************/
public void setCoordConfig(CoordConfig config) {

    this.config = config;

    tan_coord = config.getTopCoordinates();
    tan_seg = tan_coord.getSegment();
    params = config.createParameterSet();

    if(ra_dec != null && pa != null && time != null && az_alt != null) {

        Angle pa = this.pa;
        this.pa = null;

        set(ra_dec, pa, time, az_alt);
    }

} // end of setCoordConfig method

/*************************************************************************
*
*************************************************************************/
public Direction getDirection() { return ra_dec; }

/*************************************************************************
*
*************************************************************************/
public Angle getPA() { return pa; }

/*************************************************************************
*
*************************************************************************/
public PreciseDate getTime() {

    if(time != null) return TransformCache.makeCache(time);
    else             return null;

} // end of getTime method

/*************************************************************************
*
*************************************************************************/
public AzAlt getAzAlt() { return az_alt; }

/*************************************************************************
*
*************************************************************************/
public boolean isSet() {

    return ra_dec != null &&
           pa     != null &&
           time   != null &&
           az_alt != null;

} // end of isSet method

/*************************************************************************
*
*************************************************************************/
public void set(Direction ra_dec, Angle pa, PreciseDate time,
                AzAlt az_alt) {

    /*****************************
    * determine what has changed *
    *****************************/
    boolean ra_dec_changed = objectHasChanged(ra_dec, this.ra_dec);
    boolean pa_changed     = objectHasChanged(pa,     this.pa);
    boolean time_changed   = objectHasChanged(time,   this.time);
    boolean az_alt_changed = objectHasChanged(az_alt, this.az_alt);

    /******************
    * save the values *
    ******************/
    this.ra_dec = ra_dec;
    this.pa = pa;
    this.az_alt = az_alt;

    if(time_changed) this.time = TransformCache.makeCache(time);
    else             time = this.time; // to make sure we have a cache

    /***********************
    * update the transform *
    ***********************/
    boolean transform_changed = time_changed || az_alt_changed;
    if(transform_changed) {
        if(time == null || az_alt == null) trans = null;
        else trans = Coordinates.RA_DEC.getTransformTo(az_alt, time);

        trans_inverse = null;
    }

    /**********************
    * update the rotation *
    **********************/
    if(ra_dec_changed || transform_changed) {

        if(ra_dec == null || trans == null) {
            rot = null;
        } else {
            Direction dir = trans.transform(ra_dec);
            rot = (Rotation)new Rotation(Euler.fromDirAndRoll(dir, 0.0));
           // rot = (Rotation)new Rotation(new Euler(dir, 0.0));
        }
        rot_inverse = null;
    }

    /*********************
    * update the rotator *
    *********************/
    if(ra_dec_changed || pa_changed || transform_changed) {
        if(trans != null && ra_dec != null && pa != null) {
            TransformParameter param = params.getParameter("rotator");
            if(param != null) {
                Angle rotator  = trans.transform(ra_dec, pa);

                // this is an arbitrary hack to make it work
                rotator = rotator.negative();

                param.setValue(rotator.getDegrees());
            }
        }
    }

} // end of set method

/*************************************************************************
*
*************************************************************************/
private void updateInverseTransform() {

    if(trans_inverse == null) trans_inverse = trans.invert();

} // end of getInverseTransform method

/*************************************************************************
*
*************************************************************************/
private void updateInverseRotation() {

    if(rot_inverse == null) rot_inverse = (Rotation)rot.invert();

} // end of updateInverseRotation method


/*************************************************************************
*
*************************************************************************/
private static boolean objectHasChanged(Object o1, Object o2) {

    if(o1 == null && o2 == null) return false;
    if(o1 == null || o2 == null) return true;
    return !o1.equals(o2);

} // end of objectHasChanged method


/*************************************************************************
* @return The location on the tangent plane in radians at the tangent point.
*************************************************************************/
public Point2D toTangentPlane(Direction ra_dec) {

    return Projection.TANGENT.project(
                          rot.transform(
                        trans.transform(ra_dec)));

} // end of toTangentPlane method

/*************************************************************************
*
*************************************************************************/
public Direction toRADec(Point2D tan) {

    updateInverseTransform();
    updateInverseRotation();


    return  trans_inverse.transform(
              rot_inverse.transform(
       Projection.TANGENT.unproject(tan)));

} // end of toRADec method


/*************************************************************************
*
*************************************************************************/
public Direction toAzAlt(Direction ra_dec) {

    return trans.transform(ra_dec);

} // end of toAzAlt method


/*************************************************************************
*
*************************************************************************/
public Direction toRADec(Direction az_alt) {

    updateInverseTransform();

    return trans_inverse.transform(az_alt);

} // end of toAzAlt method

/*************************************************************************
*
*************************************************************************/
public Location transformDown(Location loc,
                              PlaneCoordinates coord) {

    return transformDown(loc.getSegment(), loc.getPoint(), coord);

} // end of transformDown method

/*************************************************************************
*
*************************************************************************/
public Location transformDown(PlaneSegment seg, Point2D point,
                              PlaneCoordinates coord) {

    return seg.transformDownTo(coord, point, params);

} // end of transformDown method


/*************************************************************************
*
*************************************************************************/
public Location transformUp(PlaneSegment seg, Point2D point,
                            PlaneCoordinates coord) {

    return seg.transformUpTo(coord, point, params);

} // end of transformUp method


/*************************************************************************
*
*************************************************************************/
public Location toCoordinates(Direction ra_dec, PlaneCoordinates coord) {

    return transformDown(tan_seg, toTangentPlane(ra_dec), coord);

} // end of toCoordinates method

/*************************************************************************
*
*************************************************************************/
public Direction toRADec(Location loc) {

    return toRADec(loc.getSegment(), loc.getPoint());

} // end of toRADec method

/*************************************************************************
*
*************************************************************************/
public Direction toRADec(PlaneSegment seg, Point2D point) {

// System.out.println("to rot: "+seg.getTransformToParent());
//
// System.out.println("FOCAL coords: "+point.getX()+" "+point.getY());
//
// PlaneCoordinates rot_coord = config.getCoordinates("ROTATED");
// Point2D p_rot = seg.transformUpTo(rot_coord, point, params).getPoint();
// System.out.println("ROTATED coords: "+p_rot.getX()+" "+p_rot.getY());
//
// Point2D p = transformUp(seg, point, tan_coord).getPoint();
// System.out.println("TAN coords: "+p.getX()+" "+p.getY());

    return toRADec(transformUp(seg, point, tan_coord).getPoint());

} // end of toCoordinates method


/***************************************************************************
*
***************************************************************************/
public Mapping getMapping(PlaneSegment seg, Plane plane) {

    updateInverseRotation();

    Transform trans;
    if(plane.getCoordinates().equals(Coordinates.RA_DEC)) {
        /*************************************************
        * the plane is in RA/Dec coordinates, so we can
        * use our inverse transform
        *************************************************/
        updateInverseTransform();

        trans = rot_inverse.combineWith(trans_inverse);
    } else {
        /***************************************************
        * we need to get the transform from our Az/Alt to
        * the plane coordinates
        ***************************************************/
        trans = rot_inverse.combineWith(
                     az_alt.getTransformTo(plane.getCoordinates(), time));
    }



    Mapping reprojection = Projection.TANGENT
                                 .createReprojection(trans,
                                                     plane.getProjection());

    Mapping to_tangent = seg.getTransformUpTo(tan_coord).getMapping(params);


//System.out.println("plane mapping: "+plane.getMappingToPixels());
     return to_tangent.combineWith(reprojection)
                      .combineWith(plane.getMappingToPixels());


} // end of getMappingTo method


} // end of ImageParams class