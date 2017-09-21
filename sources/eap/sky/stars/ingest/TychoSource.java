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
import eap.sky.stars.*;

import java.io.*;
import java.util.*;

/*************************************************************************
* Represents the Tycho star catalog. This class has been tested with the
* <A href="http://www.astro.ku.dk/~erik/Tycho-2/">Tycho-2</a> catalog
* in particular. No guarantee it would work with Tycho-1.
* For more information on the Hipparcos/Tycho mission see
* <a href="http://www.rssd.esa.int/Hipparcos">
*          http://www.rssd.esa.int/Hipparcos</a>
* and <a href="http://www.astro.ku.dk/~erik/Tycho-2">
*              http://www.astro.ku.dk/~erik/Tycho-2"</a>.
* as well as Astronomy and Astrophysics 355L, 27 (2000).
*************************************************************************/
public class TychoSource extends ASCIISource {

Band band;

/*************************************************************************
* Create a new instance of the catalog.
* @param reader The data source.
*************************************************************************/
public TychoSource(BufferedReader reader, Band band) {

    super(reader);

    this.band = band;

} // end of constructor

/***************************************************************************
* Pare a single star from a line in the catalog.
***************************************************************************/
protected Star parseLine(String line) {

//System.out.println(line);

    StringTokenizer tokens = new StringTokenizer(line, "|");

    /**********************************************
    * assemble the star name from the TYC indices *
    **********************************************/
    int[] tyc = new int[3];
    int index=0;
    for(StringTokenizer fields = new StringTokenizer(tokens.nextToken().trim());
        fields.hasMoreTokens(); ++index) {

        tyc[index] = Integer.parseInt(fields.nextToken());
      //  System.out.println(index+" "+tyc[index]);
      //  if(index==2) System.out.println(fields.nextToken());
    }

    String name = "TYC"+tyc[0]+"-"+tyc[1]+"-"+tyc[2];

    tokens.nextToken(); // pflag;

    /*************
    * RA and Dec *
    *************/
    Direction dir = null;
    try {
        double ra  = Double.parseDouble(tokens.nextToken().trim());
        double dec = Double.parseDouble(tokens.nextToken().trim());
        dir = new Direction(ra, dec);
    } catch(NumberFormatException e) {
        return null;
    }

    for(int i=0; i<13; ++i) tokens.nextToken();

    /*************
    * photometry *
    *************/
    Magnitude mag_b = null;
    Magnitude mag_v = null;
    try {
        float value = Float.parseFloat(tokens.nextToken().trim());
        mag_b = new ShortMagnitude(TychoBand.B, value);
    } catch(NumberFormatException e) {}

    tokens.nextToken();

    try {
        double value = Float.parseFloat(tokens.nextToken().trim());
        mag_v = new ShortMagnitude(TychoBand.V, value);
    } catch(NumberFormatException e) {}

   Photometry photometry = null;

   if(     band.equals(TychoBand.B)) photometry = mag_b;
   else if(band.equals(TychoBand.V)) photometry = mag_v;

   if(photometry == null) {
       System.out.println("skipping "+name+" no photometry in "+band);
       return null;
   }

    return new Star(name, dir, photometry);


} // end of parseLine method

/*************************************************************************
*
*************************************************************************/
public static void main(String[] args) throws Exception {

    /************************
    * get the band from the command line *
    ************************************/
    String name = args[0];

    Band band = null;
    if(     name.equals("B")) band = TychoBand.B;
    else if(name.equals("V")) band = TychoBand.V;
    else {
        System.err.println("Argument must be B or V");
        System.exit(1);
    }

    BandMap bands = new BandMap();
    bands.add(band);

    File dir = new File(name);

    /************************
    * open the catalog file *
    ************************/
    File file = new File("catalog.dat");
    BufferedReader reader = new BufferedReader(new FileReader(file));

    CatalogSource source = new TychoSource(reader, band);

    /******************************
    * are we selecting dim stars? *
    ******************************/
    if(args.length>1) {
        float value = Float.parseFloat(args[1]);
        Magnitude limit = new ShortMagnitude(band, value);

        source = new DimFilterSource(source, limit);
        
        dir = new File(name+"_"+args[1]);
        name = name+">"+args[1];


    } // end if we are selecting sim stars


    MemorySorter sorter = new MemorySorter(source, band);



    CatalogGenerator generator = new OnePassGenerator(sorter, dir,
                                                      1000, bands, band,
                                                      new StarFormat5(bands),
                                                      "Tycho2 "+name, "1",
                                                      new HTMRoot(),
                             new UniformPropagator(MemoryOutputCell.class));

    generator.generate();



} // end of main method


} // end of TychoSource class
