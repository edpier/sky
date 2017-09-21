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

import java.io.*;
import java.util.*;
import javax.swing.event.*;

/*************************************************************************
* A named value which specifies some aspect of a transform not known
* a priori.
* @see ParameterSet
*************************************************************************/
public class TransformParameter implements Serializable {

String name;
double value;
Map<PlaneTransform, ParamCache> cache;

/*************************************************************************
* Create a new parameter
* @param name The name of the parameter.
* @param value The initial value of the parameter.
*************************************************************************/
public TransformParameter(String name, double value) {

    this.name = name;
    this.value = value;
    cache = new HashMap<PlaneTransform, ParamCache>();


} // end of constructor

/*************************************************************************
* Returns the name of the parameter.
* @return The name of the parameter.
*************************************************************************/
public String getName() { return name; }

/*************************************************************************
* Set the value of the parameter.
* @param value The value of the parameter.
*************************************************************************/
public void setValue(double value) {

    /****************
    * set the value *
    ****************/
    this.value = value;

    /*****************************
    * update all the cached data *
    *****************************/
    for(Iterator it= this.cache.values().iterator(); it.hasNext(); ) {
        ParamCache cache = (ParamCache)it.next();

        cache.update(value);
    }

} // end of setValue method

/*************************************************************************
* Returns the value of the parameter.
* @return The value of the parameter.
*************************************************************************/
public double getValue() { return value; }

/**************************************************************************
* Set the information cache for a particular transform.
* A particular transform can store values related to this parameter in
* a cache so that they don't have to be recalculated each time you
* perform the transform. For example if the parameter is an angle, a
* transform might want to cache the sine and cosine of that angle.
* @param trans The transform which uses this cache.
* @param cache The cache.
**************************************************************************/
public void setCache(PlaneTransform trans, ParamCache cache) {

    cache.update(value);
    this.cache.put(trans, cache);

} // end of setCache method

/**************************************************************************
* Returns the cache used by a particular transform. The cahche must have
* previously been set with {@link #setCache(PlaneTransform, ParamCache)}.
* @param trans The transform whose cache we are looking for.
* @return The cache or null if there is no cache registered for the transform.
**************************************************************************/
public ParamCache getCache(PlaneTransform trans) {

    return (ParamCache)cache.get(trans);

} // end of getCache method


/*************************************************************************
* Create a new parameter with the same name and value. The new object is
* independant of this one.
* @return A clone of this object.
*************************************************************************/
public TransformParameter copy() {

    return new TransformParameter(name, value);

} // end of copy method

} // end of TransformParameter class
