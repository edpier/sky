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

package eap.sky.time;

import java.io.*;
import java.util.*;

/****************************************************************************
*
****************************************************************************/
public class CachedTimeSystem extends TimeSystem {

private static final Conversion[] EMPTY = {};

Map<TimeSystem, Fetch> map;
Conversion[] array;

TimeSystem original;

/****************************************************************************
*
****************************************************************************/
protected CachedTimeSystem(PreciseDate date) {

    super(null, null);

    if(date instanceof CachedDate) {
        throw new IllegalArgumentException("Can't CachedDate from a CachedDate");
    }

    original = date.getTimeSystem();

    map = new HashMap<TimeSystem, Fetch>();
    array = new Conversion[0];

    addDate(date);

//     array = new Conversion[1];
//     array[0] = new Fetch(this, date);

} // end of constructor

/****************************************************************************
*
****************************************************************************/
private void writeObject(ObjectOutputStream out) throws IOException {

    PreciseDate orig_date = map.get(original).converted;
    out.writeObject(orig_date);


} // end of writeObject method

/****************************************************************************
*
****************************************************************************/
private void readObject(ObjectInputStream in) throws IOException,
                                           ClassNotFoundException  {

    map = new HashMap<TimeSystem, Fetch>();
    array = new Conversion[0];

    addDate((PreciseDate)in.readObject());

} // end of writeObject method

/****************************************************************************
*
****************************************************************************/
protected Conversion[] getConversionsTo() {

    return EMPTY;

} // end of getConversions

/****************************************************************************
*
****************************************************************************/
protected Conversion[] getConversionsFrom() {

    return array;

} // end of getConversions

/**************************************************************************
*
**************************************************************************/
public void addDate(PreciseDate date) {

    /*****************************************************
    * see if we already have a conversion to this system *
    *****************************************************/
    TimeSystem system = date.getTimeSystem();
    if(map.containsKey(system)) return;

//System.out.println("adding "+system);
    /*******************************
    * create a new fetch transform *
    *******************************/
    Fetch fetch = new Fetch(this, date);

    /**************************************
    * store the new conversion in the map *
    **************************************/
    map.put(system, fetch);

    /**************************************
    * add the new conversion to the array *
    **************************************/
    Conversion[] longer = new Conversion[array.length+1];
    System.arraycopy(array, 0, longer, 0, array.length);
    longer[longer.length-1] = fetch;

    array = longer;

} // end of addDate method

/*****************************************************************************
*
*****************************************************************************/
public Set<TimeSystem> getCachedTimeSystems() {

    return new HashSet<TimeSystem>(map.keySet());


} // end of getCommonTimeSystems method

/*****************************************************************************
*
*****************************************************************************/
public Conversion getConversionTo(TimeSystem to) {

    /**************************************************
    * check if we have something in the cache already *
    **************************************************/
    Fetch fetch = (Fetch)map.get(to);
    if(fetch != null) return fetch;

    /*********************************************************
    * if we get here, we have to find a composite conversion *
    *********************************************************/
    CompositeConversion conv = (CompositeConversion)findConversionTo(to);
    conv.setCache(this);

    return conv;

} // end of getConversionTo method

/****************************************************************************
*
****************************************************************************/
private class Fetch extends Conversion {

PreciseDate converted;

/****************************************************************************
*
****************************************************************************/
public Fetch(CachedTimeSystem from, PreciseDate converted) {

    super(from, converted.getTimeSystem());

    this.converted = converted.copy();

} // end of constructor

/*****************************************************************************
* This method always returns 0. Since we already know the answer.
*****************************************************************************/
public int getSteps() { return 0; }

/*****************************************************************************
* Do the actual conversion.
*****************************************************************************/
public void convert(PreciseDate from, PreciseDate to) {

    to.setTime(converted);

} // end of convert method

} // end of Fetch inner class




} // end of CachedTimeSystem class
