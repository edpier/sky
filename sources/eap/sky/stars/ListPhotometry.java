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
public class ListPhotometry extends AbstractPhotometry {

List<Band> bands;
List<Magnitude> mags;

/********************************************************************
*
********************************************************************/
public ListPhotometry(BandMap bands) {

    this.bands = bands.getBands();
    mags = new ArrayList<Magnitude>(this.bands.size());

} // end of constructor

/********************************************************************
*
********************************************************************/
public void add(short raw) {

    int index = mags.size();
    Band band = (Band)bands.get(index);
    mags.add(new ShortMagnitude(band, raw));

} // end of add method

/********************************************************************
*
********************************************************************/
public void add(float value) {

    int index = mags.size();
    Band band = (Band)bands.get(index);
    mags.add(new FloatMagnitude(band, value));

} // end of add method

/***********************************************************************
* Returns the number of magnitude values in the collection.
***********************************************************************/
public int getCount() { return bands.size(); }


/***********************************************************************
* Returns the magnitude in the given band.
* @param band The band for the requested magnitude
* @return the magnitude in the given band or null if this collection does
*         not have a magnitude in that band.
***********************************************************************/
public Magnitude getMagnitude(Band band) {

   // it's actually slightly faster to iterate with indices
   // rathet than to use an interator
   // mean access times this way are only a few percent faster
   // hash access times with 5 magnitudes.

  //  for(Iterator it = mags.iterator(); it.hasNext(); ) {
  //        Magnitude mag = (Magnitude)it.next();
    int nmags = mags.size();
    for(int i=0; i< nmags; ++i) {
        Magnitude mag = (Magnitude)mags.get(i);


        if(mag.getBand().equals(band)) return mag;
    }

    return null;

} // end of getMagnitude method

/**********************************************************************
*
**********************************************************************/
public Magnitude getMagnitude(int index) {

    return (Magnitude)mags.get(index);

} // end of getmagnitude method

/***********************************************************************
* Returns all the bands in this collection.
* @return An unmodifiable Set of all the bands in this collection.
***********************************************************************/
public Collection<Band> getBands() { return bands; }

/***************************************************************************
* Returns all the magnitudes in this collection.
* @return an unmodifiable collection of all the magnitudes in this collection.
***************************************************************************/
public Collection<Magnitude> getMagnitudes() {
    return Collections.unmodifiableList(mags);

} // end of getMagnitudes method


} // end of ListPhotometry class
