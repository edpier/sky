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
* A high accuracy one magnitude format. Positions are stored as
* RA/Dec two doubles, and magnitudes as a single float.
*********************************************************************/
public class StarFormat6 extends StarFormat {

Band band;

/*********************************************************************
*
*********************************************************************/
public StarFormat6 (BandMap bands) {

    List list = bands.getBands();
    if(list.size()!=1) {
        throw new IllegalArgumentException("More than one band in the map");
    }

    this.band = (Band)list.get(0);

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public StarFormat6 (Band band) {

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
    Magnitude mag = photometry.getMagnitude(band);

    if(mag == null) {
        throw new IllegalArgumentException("Don't have the required magnitude");
    }

    /******************
    * write the value *
    ******************/
    out.writeFloat(mag.getValue());

} // end of generalWrite method

/*********************************************************************
*
*********************************************************************/
public void write(Star star, DataOutput out) throws IOException {

    out.writeUTF(star.getName());

    Direction dir = star.getDirection();
    out.writeDouble(dir.getLongitude());
    out.writeDouble(dir.getLatitude());

    Magnitude mag = (Magnitude)star.getPhotometry();

    if(!mag.getBand().equals(band)) {
        throw new IllegalArgumentException("Dont have the right band");
    }

    out.writeFloat(mag.getValue());

} // end of write method

/*********************************************************************
*
*********************************************************************/
public Star read(DataInput in) throws IOException {

    String star_name = in.readUTF();

    double ra = in.readDouble();
    double dec = in.readDouble();

    float value = in.readFloat();
    Photometry photometry =  new FloatMagnitude(band, value);

   return new Star(star_name, new Direction(ra, dec), photometry);

} // end of read method

} // end of StarFormat class
