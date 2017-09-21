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
public class ShortMagnitude extends Magnitude {

public  static final float MIN_MAG = -10.f;

private static final float FROM_MAG = 1000.f;
private static final float TO_MAG = 1.f/FROM_MAG;
private static final float OFFSET = MIN_MAG - Short.MIN_VALUE * TO_MAG;

public static final float MAX_MAG = Short.MAX_VALUE *TO_MAG + OFFSET;

short raw;

/**************************************************************************
* Create a new magnitude
* @param band The wavelength band in which the brightness is measured.
* @param value the magnitude of the star in that band.
**************************************************************************/
public ShortMagnitude(Band band, float value) {

    super(band);

    /**********************************************
    * a range check - because we are not savages! *
    **********************************************/
    if(value < MIN_MAG || value > MAX_MAG) {
        throw new IllegalArgumentException("Mag "+value+" out of range");
    }

    raw = (short)((value - OFFSET)*FROM_MAG);

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public ShortMagnitude(Band band, double value) {

    this(band, (float)value);

} // end of double constructor

/***********************************************************************
*
***********************************************************************/
public ShortMagnitude(Band band, short raw) {

    super(band);
    this.raw = raw;

} // end of constructor

/**************************************************************************
* Returns the numerical value of this magnitude
* @return The numerical value of this magnitude
**************************************************************************/
public float getValue() { return raw*TO_MAG + OFFSET; }

/**************************************************************************
*
**************************************************************************/
public Magnitude plus(float increment) {

    return new ShortMagnitude(getBand(),
                              (short)(raw + (short)(FROM_MAG*increment)));

} // end of plus method

/***********************************************************************
*
***********************************************************************/
public short getRawValue() { return raw; }

/***********************************************************************
*
***********************************************************************/
public int compareTo(Object o) {

    if(o instanceof ShortMagnitude) {
        /***********************
        * comparing two shorts *
        ***********************/
         ShortMagnitude mag = (ShortMagnitude)o;

        if(!band.equals(mag.band)) {
            throw new IllegalArgumentException("Can't compare "+band+
                                            " and "+mag.band);
        }


        return raw - mag.raw;

    } else {
        /*********************************
        * use the generic compare method *
        *********************************/
        return super.compareTo(o);
    }

} // end of compareTo method


} // end of FloatMagnitude class
