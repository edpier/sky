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

package eap.sky.util.coordinates;

import eap.sky.util.*;
import eap.sky.time.*;

import java.io.*;

/***************************************************************************
* Represents a coordinate system for describing locations on a shpere.
***************************************************************************/
public abstract class Coordinates implements Serializable {

/** The instance of {@link RADec} **/
public static final Coordinates RA_DEC = new RADec();

public static final Coordinates GALACTIC = new GalacticCoordinates();

/***************************************************************************
* Generates the transform from these coordinates to {@link RADec}.
* This way we can transform between any arbitrary pair of coordinates
* by first transforming to RA/Dec.
* @param time The time at which the transform is to be done. Many transforms
* are a function of time.
***************************************************************************/
public abstract Transform toRADec(PreciseDate time);

/***************************************************************************
*
***************************************************************************/
public abstract Aspect getAspect(int type);

/****************************************************************************
* Returns a transform from this coordinate system to another, which is
* optimized to me more efficient that transforming via RA/Dec. This method
* always returns null, to indicate there are no optimized transforms available.
* Subclasses should override this if necessary. This method is called by
* {@link #getTransformTo(Coordinates, PreciseDate)}.
* @param coord The other coordinates system.
* @param time The time at which to calculate the transform
* @return An optimized transform, or null if there is no optimized transform
* to a particular coordinates system.
****************************************************************************/
public Transform getOptimizedTransformTo(Coordinates coord, PreciseDate time) {

    return null;

} // end of getOptimizedTransformTo method

/****************************************************************************
* Returns a transform to another coordinates system. No caching is
* done. 
* @param coord The other coordinates system.
* @param time The time at which to calculate the transform
* @return the transform to another coordinate system.
****************************************************************************/
private Transform constructTransformTo(Coordinates coord, PreciseDate time) {

    if(coord.equals(this)) return Rotation.IDENTITY;
    if(coord.equals(RA_DEC)) return toRADec(time);

    Transform optimized = getOptimizedTransformTo(coord, time);
    if(optimized != null) return optimized;

    Transform inverse = coord.getOptimizedTransformTo(this, time);
    if(inverse != null) return inverse.invert();

    return toRADec(time).combineWith(coord.toRADec(time).invert());

} // end of getTransformTo method

/****************************************************************************
* Returns a transform to another coordinates system. If the time argument is
* a {@link TransformCache}, then this method will use the cache.
* @param coord The other coordinates system.
* @param time The time at which to calculate the transform.
* @return the transform to another coordinate system.
****************************************************************************/
public final Transform getTransformTo(Coordinates coord, PreciseDate time) {

    if(time instanceof TransformCache) {
        /********************************************************************
        * the time is actually a cache of previously constructed transforms *
        ********************************************************************/
        TransformCache cache = (TransformCache)time;
        return cache.getTransform(this, coord);

    } else {
        /***********************************************
        * there is no caching going on, so construct
        * a fresh transform
        ************************************************/
        return constructTransformTo(coord, time);
    }


} // end of getTransformTo method

} // end of CoordinateSystem class
