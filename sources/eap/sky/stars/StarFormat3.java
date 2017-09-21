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
* This is a hybrid format. It only reads one of the bands from a star
* encoded with format 2. It writes only this band. Note a read and then
* a write looses information.
*********************************************************************/
public class StarFormat3 extends StarFormat {

Band band;
int nbands;
int index;

/*********************************************************************
*
*********************************************************************/
public StarFormat3 (BandMap bands, Band band) {

    this.band = band;

    index = -1;
    List list = bands.getBands();
    nbands = list.size();
    for(int i=0; i< nbands; ++i) {
        Band b = (Band)list.get(i);
        if(b.equals(band)) {
            index = i;
            break;
        }
    } // end of loop over bands

    if(index == -1) {
        throw new IllegalArgumentException("Band "+band+" is not in band map");
    }

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
    Magnitude magnitude = photometry.getMagnitude(band);

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




} // end of write method

/*********************************************************************
*
*********************************************************************/
public void write(Star star, DataOutput out) throws IOException {

    out.writeUTF(star.getName());

    Direction dir = star.getDirection();
    out.writeDouble(dir.getLongitude());
    out.writeDouble(dir.getLatitude());

    ShortMagnitude mag = (ShortMagnitude)star.getPhotometry();

    if(!mag.getBand().equals(band)) {
        throw new IllegalArgumentException("Dont have the right band");
    }

    out.writeShort(mag.getRawValue());

} // end of write method

/*********************************************************************
*
*********************************************************************/
public Star read(DataInput in) throws IOException {

    String star_name = in.readUTF();

    double ra  = in.readDouble();
    double dec = in.readDouble();

    Photometry photometry = null;
    for(int i=0; i< nbands; ++i) {
        short raw = in.readShort();
        if(i==index) {
            photometry = new ShortMagnitude(band, raw);
        }
    } // end of loop over bands


   return new Star(star_name, new Direction(ra, dec), photometry);

} // end of read method

} // end of StarFormat class
