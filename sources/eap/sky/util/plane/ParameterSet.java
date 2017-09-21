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

/*************************************************************************
* A collection of named parameters which are needed to describe a transform
* but are only know at runtime. Each parameter must have a unique name.
* We need something like this because not all tranform parameters are
* known a priori. For example, the transform for an instrument rotator
* depends on the position of the rotator. Other transforms might depend on
* wavelength or time as the equipment ages.
*************************************************************************/
public class ParameterSet implements Serializable {

Map<String, TransformParameter> map;


/**************************************************************************
* Create a new empty parameter set. Usually you do not use this constructor
* directly but instead call {@link CoordConfig#createParameterSet()},
* which will identify all the valid parameter names for you.
**************************************************************************/
public ParameterSet() {

    map   = new HashMap<String, TransformParameter>();

} // end of constructor

/**************************************************************************
* Tests if the set containes a parameter with the given name.
* @param name a possible parameter name.
* @return true if this set has a parameter with this name.
**************************************************************************/
public boolean contains(String name) { return map.containsKey(name); }

/**************************************************************************
* Add a new parameter to the set.
* @param param The new parameter.
**************************************************************************/
public void addParameter(TransformParameter param) {

    map.put(param.getName(), param);

} // end of addParameter method

/**************************************************************************
* locate a parameter by name.
* @param name The name of the desired paramete
* @return The named parameter or null if there is no such parameter.
**************************************************************************/
public TransformParameter getParameter(String name) {

    return (TransformParameter)map.get(name);
}

/**************************************************************************
* Get all the parameters
* @return an unmodifiable view of all the parameters in this set.
**************************************************************************/
public Collection<TransformParameter> getParameters() {

    return Collections.unmodifiableCollection(map.values());

} // end of getParameters method



/**************************************************************************
* Make an independant copy of this parameter set. The new set will have the
* same values, but can be modified without affecting this object.
**************************************************************************/
public ParameterSet copy() {

    ParameterSet set = new ParameterSet();
    for(Iterator it = getParameters().iterator(); it.hasNext(); ) {
        TransformParameter param = (TransformParameter)it.next();

        set.addParameter(param.copy());
    }

    return set;

} // end of copy method

} // end of ParameterSet class
