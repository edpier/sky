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

import eap.sky.util.*;

import java.util.*;
import java.io.*;

/*********************************************************************
* This format stores photometry as a list of shorts in the order
* described by the band map
*********************************************************************/
public class StarFormat2 extends StarFormat {

BandMap bands;
int nbands;

Band[] band_array;

/*********************************************************************
*
*********************************************************************/
public StarFormat2 (BandMap bands) {

    this.bands = bands;
    nbands = bands.getBands().size();

    band_array = bands.getBandArray();

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public void generalWrite(Star star, DataOutput out) throws IOException {

    out.writeUTF(star.getName());

    Direction dir = star.getDirection();
    out.writeDouble(dir.getLongitude());
    out.writeDouble(dir.getLatitude());

    Photometry photometry = star.getPhotometry();
    for(Iterator it = photometry.getMagnitudes().iterator();
        it.hasNext(); ) {
        Magnitude magnitude = (Magnitude)it.next();

        /*******************************
        * convert to a short magnitude *
        *******************************/
        ShortMagnitude mag;
        if(magnitude == null || magnitude instanceof ShortMagnitude) {
            mag = (ShortMagnitude)magnitude;
        } else {
            mag = new ShortMagnitude(magnitude.getBand(),
                                     magnitude.getValue());
        }

        /******************
        * write the value *
        ******************/
        if(mag == null) out.writeShort(Short.MIN_VALUE);
        else            out.writeShort(mag.getRawValue());

    } // end of loop over magnitudes


} // end of write method

/*********************************************************************
*
*********************************************************************/
public void write(Star star, DataOutput out) throws IOException {

    out.writeUTF(star.getName());

    Direction dir = star.getDirection();
    out.writeDouble(dir.getLongitude());
    out.writeDouble(dir.getLatitude());

    ListPhotometry photometry = (ListPhotometry)star.getPhotometry();

    for(Iterator it = photometry.getMagnitudes().iterator(); it.hasNext(); ) {
        ShortMagnitude mag = (ShortMagnitude)it.next();

        if(mag == null) out.writeShort(Short.MIN_VALUE);
        else            out.writeShort(mag.getRawValue());

    } // end of loop over magnitudes


} // end of write method

/*********************************************************************
*
*********************************************************************/
public Star read(DataInput in) throws IOException {

    String star_name = in.readUTF();

    double ra  = in.readDouble();
    double dec = in.readDouble();

   // ListPhotometry photometry = new ListPhotometry(bands);
    ArrayPhotometry photometry = new ArrayPhotometry(band_array);
    for(int i=0; i< nbands; ++i) {

      //  photometry.add(in.readShort());

      photometry.set(i, in.readShort());

    } // end of loop over bands


    return new Star(star_name, new Direction(ra, dec), photometry);

} // end of read method

} // end of StarFormat class
