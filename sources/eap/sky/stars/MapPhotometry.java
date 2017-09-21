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

package eap.sky.stars;

import java.util.*;
import java.io.*;

/***********************************************************************
* Represents a collection of magnitudes in different bands.
***********************************************************************/
public class MapPhotometry extends AbstractPhotometry {

Map<Band, Magnitude> map;

/***********************************************************************
* Create a new empty photometry collection.
***********************************************************************/
public MapPhotometry(int count) {

    float load = 0.75f;
    int capacity = (int)Math.ceil(count/load);
    map = new LinkedHashMap<Band, Magnitude>(capacity, load);
   // map = new HashMap(capacity, load);
}

/***********************************************************************
* Create a new empty photometry collection.
***********************************************************************/
public MapPhotometry() {

    map = new LinkedHashMap<Band, Magnitude>();
}

/***********************************************************************
* Returns the number of magnitude values in the collection.
***********************************************************************/
public int getCount() { return map.size(); }


/***********************************************************************
* Add a magnitude to the collection. The collection can hold one one
*  magnitude per band. If it already holds a magnitude in the same band, the
* old one will be deleted.
* @param mag The new magnitude.
***********************************************************************/
public void addMagnitude(Magnitude mag) {

    map.put(mag.getBand(), mag);

} // end of addMagnitude method

/***********************************************************************
* Returns the magnitude in the given band.
* @param band The band for the requested magnitude
* @return the magnitude in the given band or null if this collection does
*         not have a magnitude in that band.
***********************************************************************/
public Magnitude getMagnitude(Band band) {

    return (Magnitude)map.get(band);
}

/***********************************************************************
* Returns all the bands in this collection.
* @return An unmodifiable Set of all the bands in this collection.
***********************************************************************/
public Collection<Band> getBands() {

    return Collections.unmodifiableSet(map.keySet());
}
/***********************************************************************
* Returns all the magnitudes in this collection.
* @return an unmodifiable collection of all the magnitudes in this collection.
***********************************************************************/
public Collection<Magnitude> getMagnitudes() {

    return Collections.unmodifiableCollection(map.values());

} // end of getMagnitudes


} // end of Photometry class
