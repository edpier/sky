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

package eap.sky.stars.ingest;

import eap.sky.util.*;
import eap.sky.util.coordinates.*;
import eap.sky.stars.*;


import java.util.*;
import java.text.*;
import java.io.*;

/**************************************************************************
*
**************************************************************************/
public class HipparcosSource extends ASCIISource {

NumberFormat  ra_format;
NumberFormat dec_format;

/*************************************************************************
*
*************************************************************************/
public HipparcosSource(BufferedReader reader) {

    super(reader);

    ra_format = SexigesimalFormat.HMS;
    dec_format = SexigesimalFormat.DMS;

} // end of constructor

/***************************************************************************
* Parse a single star from a line in the catalog.
***************************************************************************/
protected Star parseLine(String line) {



    String name = null;
    double ra=0.0;
    double dec=0.0;

    Photometry photometry = null;

    /*******************
    * loop over fields *
    *******************/
    StringTokenizer tokens = new StringTokenizer(line, "|");
    for(int field = 0; tokens.hasMoreTokens(); ++field) {

        String token = tokens.nextToken().trim();

        if(field ==  1) name = "HIP"+token;
        else if(field == 8) {
            /*****
            * RA *
            *****/
            if(token.equals("")) return null;
            ra = Double.parseDouble(token);

        } else if(field == 9) {
            /******
            * Dec *
            ******/
            if(token.equals("")) return null;
            dec = Double.parseDouble(token);

//         } else if(field ==5 && !token.equals("")) {
//             /***************
//             * V magnitude *
//             **************/
//             double mag = Double.parseDouble(token);
//             photometry.addMagnitude(new Magnitude(Johnson.V, mag));
//         } else if(field ==32 && !token.equals("")) {
//             /***************
//             * BT magnitude *
//             **************/
//             double mag = Double.parseDouble(token);
//             photometry.addMagnitude(new Magnitude(TychoBand.B, mag));
// 
//         } else if(field ==34 && !token.equals("")) {
//             /***************
//             * VT magnitude *
//             **************/
//             double mag = Double.parseDouble(token);
//             photometry.addMagnitude(new Magnitude(TychoBand.V, mag));

        } else if(field ==44 && !token.equals("")) {
            /***************
            * Hp magnitude *
            **************/
            float mag = Float.parseFloat(token);
            photometry = new FloatMagnitude(HipparcosBand.Hp, mag);
        }


    } // end of loop over tokens;

    /********************
    * assemble the star *
    ********************/
    Direction dir = new Direction(ra, dec);

    Star star = new Star(name, dir, photometry);

    return star;

} // end of parseLine method

/*************************************************************************
*
*************************************************************************/
public static void main(String[] args) throws Exception {

    Band band = HipparcosBand.Hp;

    BandMap bands = new BandMap();
    bands.add(band);

    File file = new File("hip_main.dat");
    BufferedReader reader = new BufferedReader(new FileReader(file));

    HipparcosSource source = new HipparcosSource(reader);
    MemorySorter sorter = new MemorySorter(source, band);



    CatalogGenerator generator = new OnePassGenerator(sorter,
                                                      new File("cells"),
                                                      1000, bands, band,
                                                      new StarFormat6(bands),
                                                      "Hipparcos", "1",
                                                      new HTMRoot(),
                             new UniformPropagator(MemoryOutputCell.class));

    generator.generate();



} // end of main method

} // end of HipparcosCatalog class
