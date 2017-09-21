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

import eap.sky.time.*;
import eap.sky.util.coordinates.*;

import java.util.*;

/**************************************************************************
*
**************************************************************************/
public class TransformCache extends CachedDate {

Map<Coordinates, Map<Coordinates, Transform>> cache;

/**************************************************************************
* Create a new cache. Note that the time is copied so that you may change
* it later without affecting the new cache.
* @param time The time at which to calculate transforms
**************************************************************************/
public TransformCache(PreciseDate time) {

    super(time);

    cache = new HashMap<Coordinates, Map<Coordinates, Transform>>();

} // end of constructor


/**************************************************************************
*
**************************************************************************/
public static TransformCache makeCache(PreciseDate time) {

    if(time instanceof TransformCache) return (TransformCache)time;
    else                               return new TransformCache(time);

} // end of makeCache method


/**************************************************************************
*
**************************************************************************/
public Transform getTransform(Coordinates from, Coordinates to) {

//System.out.println("cache "+from+" to "+to);

    /**********************
    * identity transforms *
    **********************/
    if(from.equals(to)) return Rotation.IDENTITY;

    /******************************************
    * see if we have anything cached for from *
    ******************************************/
    Map<Coordinates, Transform> map = cache.get(from);
    if(map == null) {
        map = new HashMap<Coordinates, Transform>();
        cache.put(from, map);
    }

    /*********************************
    * see what we have cached for to *
    *********************************/
    Transform trans = (Transform)map.get(to);
    if(trans != null) return trans;


    /*******************************
    * are we converting to RA/Dec? *
    *******************************/
    if(to.equals(Coordinates.RA_DEC)) {
        trans = from.toRADec(this);
        map.put(to, trans);
        return trans;
    }

    /*******************************
    * are we converting from RA/Dec? *
    *******************************/
    if(from.equals(Coordinates.RA_DEC)) {
        trans = to.toRADec(this).invert();
        map.put(to, trans);
        return trans;
    }

    /******************************
    * general optimized transform *
    ******************************/
    trans = from.getOptimizedTransformTo(to, this);
    if(trans != null) {
        map.put(to, trans);
        return trans;
    }

    /************************************
    * now check for an optimized inverse *
    ************************************/
    trans = to.getOptimizedTransformTo(from, this);
    if(trans != null) {
        trans = trans.invert();
        map.put(to, trans);
        return trans;
    }

    /***********************
    * transform via RA/Dec *
    ***********************/
    Transform first  = getTransform(from, Coordinates.RA_DEC);
    Transform second = getTransform(Coordinates.RA_DEC, to);

    trans = first.combineWith(second);
    map.put(to, trans);
    return trans;

} // end of getTransform method

/****************************************************************************
*
****************************************************************************/
public void  removeTransform(Coordinates from, Coordinates to) {

    Map map = (Map)cache.get(from);
    if(map == null) return;

    map.remove(to);

} // end of removeTransform method

/****************************************************************************
* Removes all transforms to or from the given coordinates
****************************************************************************/
public void  removeTransforms(Coordinates coord) {

    /*************************
    * remove transforms from *
    *************************/
    cache.remove(coord);

    /***********************
    * remove transforms to *
    ***********************/
    for(Iterator it = cache.values().iterator(); it.hasNext(); ) {
        Map map = (Map)it.next();
        map.remove(coord);
    }

} // end of removeTransforms method

} // end of TransformCache class
