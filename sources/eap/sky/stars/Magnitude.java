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

/**************************************************************************
* Represents the brightness of a star in a particular photometric filter band.
* Magnitude is a logartithmic measure of the brightness of a star with
* smaller numbers indicating brighter stars.
**************************************************************************/
public abstract class Magnitude implements Comparable, Photometry {

Band band;


/**************************************************************************
*
**************************************************************************/
public Magnitude(Band band) {

    this.band = band;

} // end of constructor

/**************************************************************************
* Returns the band of this magnitude.
* @return The band of this magnitude.
**************************************************************************/
public Band getBand() { return band; }

/**************************************************************************
* Returns the numerical value of this magnitude
* @return The numerical value of this magnitude
**************************************************************************/
public abstract float getValue();

/**************************************************************************
*
**************************************************************************/
public Magnitude plus(float increment) {

    return new FloatMagnitude(getBand(), getValue());

} // end of plus method

/**************************************************************************
* Compares two magnitudes. Magnitudes soreted with this comparison will have
* the brightest magnitudes first. You can only compare magnitudes in the
* same band.
* @param o A Magnitude to which we wil compare this one.
* @return 1 if this magnitude is brighter than the given one, 0 if they are
*         equal and -1 otherwise.
* @throws IllegalArgumentException if the magnitudes are not in the same band.
**************************************************************************/
public int compareTo(Object o) {

    Magnitude mag = (Magnitude)o;

    /************************************************
    * make sure the magnitudes are in the same band *
    ************************************************/
    if(!band.equals(mag.band)) {
        throw new IllegalArgumentException("Can't compare "+band+
                                           " and "+mag.band);
    }

    float value1 =     getValue();
    float value2 = mag.getValue();

    /*************************
    * now compare the values *
    *************************/
    if(      value1 > value2) return  1;
    else if(value1 == value2) return  0;
    else                      return -1;


} // end of compareTo method

/***********************************************************************
*
***********************************************************************/
public int getCount() { return 1; }

/***********************************************************************
*
***********************************************************************/
public Magnitude getMagnitude(Band band) {

    if(this.band.equals(band)) return this;
    else                       return null;

} // end of getMagnitude method

/***********************************************************************
*
***********************************************************************/
public Collection<Band> getBands() {

    return Collections.singletonList(band);

} // end of getBands method

/***************************************************************************
*
***************************************************************************/
public Collection<Magnitude> getMagnitudes() {

    return Collections.singletonList(this);

} // end of getMagnitudes method


/***************************************************************************
*
***************************************************************************/
public double getFlux() { return band.toFlux(getValue()); }

/**************************************************************************
* Represents the magnitude as a string.
* @return A string representation of the band and numerical value of
* the magnitude.
**************************************************************************/
public String toString() {
    return band+"="+getValue();
}


} // end of Magnitude class
