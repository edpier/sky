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
* direction is encoded as 4 byte integers 3 vector
*********************************************************************/
public class StarFormat5 extends StarFormat {

Band band;

/*********************************************************************
*
*********************************************************************/
public StarFormat5 (BandMap bands) {

    List list = bands.getBands();
    if(list.size()!=1) {
        throw new IllegalArgumentException("More than one band in the map");
    }

    this.band = (Band)list.get(0);

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public StarFormat5 (Band band) {

    this.band = band;

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public void generalWrite(Star star, DataOutput out) throws IOException {

    out.writeUTF(star.getName());

    Direction dir = star.getDirection();
    out.writeInt((int)(dir.getX()*Integer.MAX_VALUE));
    out.writeInt((int)(dir.getY()*Integer.MAX_VALUE));
    out.writeInt((int)(dir.getZ()*Integer.MAX_VALUE));

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
    out.writeInt((int)(dir.getX()*Integer.MAX_VALUE));
    out.writeInt((int)(dir.getY()*Integer.MAX_VALUE));
    out.writeInt((int)(dir.getZ()*Integer.MAX_VALUE));

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

    /**********************************************************
    * read the X, Y, and Z vector components of the direction
    * this is faster than calling readInt three times
    * ints appear to give about as good accuracy as anything
    * ~5 milliarcsec
    **********************************************************/
    byte[] buffer = new byte[12];
    in.readFully(buffer);

    double x = ((buffer[0]) << 24) +
            ((buffer[1] & 255) << 16) +
            ((buffer[2] & 255) <<  8) +
            ((buffer[3] & 255) <<  0);

    double y = ((buffer[4]) << 24) +
            ((buffer[5] & 255) << 16) +
            ((buffer[6] & 255) <<  8) +
            ((buffer[7] & 255) <<  0);

    double z = ((buffer[8]) << 24) +
            ((buffer[9] & 255) << 16) +
            ((buffer[10] & 255) <<  8) +
            ((buffer[11] & 255) <<  0);

    short raw = in.readShort();
    Photometry photometry =  new ShortMagnitude(band, raw);

   return new Star(star_name, new Direction(x,y,z), photometry);

} // end of read method

} // end of StarFormat class
