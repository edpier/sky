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
* This format assumes there is only one magnitude encoded as a short.
*********************************************************************/
public class StarFormat4 extends StarFormat {

Band band;

/*********************************************************************
*
*********************************************************************/
public StarFormat4 (BandMap bands) {

    List list = bands.getBands();
    if(list.size()!=1) {
        throw new IllegalArgumentException("More than one band in the map");
    }

    this.band = (Band)list.get(0);

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public StarFormat4 (Band band) {

    this.band = band;

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

    if(mag == null) {
        throw new IllegalArgumentException("Don't have the required magnitude");
    }

    /******************
    * write the value *
    ******************/
    out.writeShort(mag.getRawValue());

} // end of generalWrite method

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

    short raw = in.readShort();
    Photometry photometry = new ShortMagnitude(band, raw);

   return new Star(star_name, new Direction(ra, dec), photometry);

} // end of read method

} // end of StarFormat class
