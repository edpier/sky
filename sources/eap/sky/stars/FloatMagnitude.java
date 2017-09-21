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

/**************************************************************************
* Represents the brightness of a star in a particular photometric filter band.
* Magnitude is a logartithmic measure of the brightness of a star with
* smaller numbers indicating brighter stars.
**************************************************************************/
public class FloatMagnitude extends Magnitude {


float value;

/**************************************************************************
* Create a new magnitude
* @param band The wavelength band in which the brightness is measured.
* @param value the magnitude of the star in that band.
**************************************************************************/
public FloatMagnitude(Band band, float value) {

    super(band);
    this.value = value;

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public FloatMagnitude(Band band, double value) {

    this(band, (float)value);

} // end of double constructor

/**************************************************************************
* Returns the numerical value of this magnitude
* @return The numerical value of this magnitude
**************************************************************************/
public float getValue() { return value; }

/**************************************************************************
*
**************************************************************************/
public int compareTo(Object o) {

    if(!(o instanceof FloatMagnitude)) {
        /*****************************
        * compare generic magnitudes *
        *****************************/
        return super.compareTo(o);

    } else {
        /***********************
        * comparing two floats *
        ***********************/
        FloatMagnitude mag = (FloatMagnitude)o;

        /************************************************
        * make sure the magnitudes are in the same band *
        ************************************************/
        if(!band.equals(mag.band)) {
            throw new IllegalArgumentException("Can't compare "+band+
                                            " and "+mag.band);
        }

        /*************************
        * now compare the values *
        *************************/
        if(      value > mag.value) return  1;
        else if(value == mag.value) return  0;
        else                        return -1;
    } // end if comparing two floats

} // end of compareTo method

} // end of FloatMagnitude class
