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
* This format stores Magnitudes as a band-mapped name of the band and
* a float
*********************************************************************/
public class StarFormat1 extends StarFormat {

BandMap bands;

/*********************************************************************
*
*********************************************************************/
public StarFormat1(BandMap bands) {

    this.bands = bands;

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public void write(Star star, DataOutput out) throws IOException {

    out.writeUTF(star.getName());

    Direction dir = star.getDirection();
    out.writeDouble(dir.getLongitude());
    out.writeDouble(dir.getLatitude());

    Photometry photometry = star.getPhotometry();
    out.writeInt(photometry.getCount());

    for(Iterator it = photometry.getMagnitudes().iterator(); it.hasNext(); ) {
        Magnitude mag = (Magnitude)it.next();

        Band band = mag.getBand();
        String name = bands.getName(band);
        if(name == null) {
            throw new IOException("No map entry for "+band);
        }

        out.writeUTF(name);
        out.writeFloat(mag.getValue());

    } // end of loop over magnitudes

} // end of write method

/*********************************************************************
*
*********************************************************************/
public Star read(DataInput in) throws IOException {

    String star_name = in.readUTF();

    double ra  = in.readDouble();
    double dec = in.readDouble();

    int count = in.readInt();
    MapPhotometry photometry = new MapPhotometry(count);

    for(int i=0; i<count; ++i) {

        String name = in.readUTF();
        Band band = bands.getBand(name);
        if(band == null) {
            throw new IOException("Unknown Band name "+name);
        }

        float mag = in.readFloat();

        photometry.addMagnitude(new FloatMagnitude(band, mag));

    } // end of loop over magnitudes

    return new Star(star_name, new Direction(ra, dec), photometry);

} // end of read method

} // end of StarFormat class
