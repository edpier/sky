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


/********************************************************************
*
********************************************************************/
public class ArrayPhotometry extends AbstractPhotometry {

Band[] bands;
Magnitude[] mags;

/********************************************************************
*
********************************************************************/
public ArrayPhotometry(Band[] bands) {

    this.bands = bands;
    mags = new Magnitude[bands.length];

} // end of constructor

/********************************************************************
*
********************************************************************/
public void set(int index, short raw) {

    mags[index] = (new ShortMagnitude(bands[index], raw));

} // end of add method

/***********************************************************************
* Returns the number of magnitude values in the collection.
***********************************************************************/
public int getCount() { return bands.length; }


/***********************************************************************
* Returns the magnitude in the given band.
* @param band The band for the requested magnitude
* @return the magnitude in the given band or null if this collection does
*         not have a magnitude in that band.
***********************************************************************/
public Magnitude getMagnitude(Band band) {

    for(int i=0; i< mags.length; ++i) {
        Magnitude mag = mags[i];


        if(mag.getBand().equals(band)) return mag;
    }

    return null;

} // end of getMagnitude method

/**********************************************************************
*
**********************************************************************/
public Magnitude getMagnitude(int index) {

    return mags[index];

} // end of getmagnitude method

/***********************************************************************
* Returns all the bands in this collection.
* @return An unmodifiable Set of all the bands in this collection.
***********************************************************************/
public Collection<Band> getBands() {

    return Collections.unmodifiableList(Arrays.asList(bands));

} // end of getBands method

/***************************************************************************
* Returns all the magnitudes in this collection.
* @return an unmodifiable collection of all the magnitudes in this collection.
***************************************************************************/
public Collection<Magnitude> getMagnitudes() {
    return Collections.unmodifiableList(Arrays.asList(mags));

} // end of getMagnitudes method


} // end of ArrayPhotometry class
