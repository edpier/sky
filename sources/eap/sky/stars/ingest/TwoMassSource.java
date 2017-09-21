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

import eap.sky.stars.*;
import eap.sky.util.*;

import java.util.*;
import java.util.zip.*;
import java.io.*;

/*********************************************************************
* The Two Micro All Sky Survey (2MASS) point source catalog.
* The data are assumed to be contained in a number of gzipped files
* located in the same directory. You can download these files from
* <a href="ftp://ftp.ipac.caltech.edu/pub/2mass/allsky/">
* ftp://ftp.ipac.caltech.edu/pub/2mass/allsky/</a> Get all files
* whose names start with "psc_" and end with ".gz". The files are
* also available on a set of DVD-ROMs See the Data Access link on the
* <a href="http://www.ipac.caltech.edu/2mass/">IPAC 2MASS home page</a>
* for more information.
*********************************************************************/
public class TwoMassSource implements CatalogSource {

BufferedReader reader;

File[] files;
int index;

Band band;
int mag_col;

/*********************************************************************
*
*********************************************************************/
public TwoMassSource(File dir, Band band) {

    files = dir.listFiles(new TwoMassPSCFilter());
    Arrays.sort(files);
    index = 0;

    this.band = band;
//     if(     band.equals(Johnson.J) ) mag_col = 6;
//     else if(band.equals(Johnson.H) ) mag_col = 10;
//     else if(band.equals(Johnson.Ks)) mag_col = 14;
//     else throw new IllegalArgumentException("Invalid band "+band);

} // end of constructor

/*********************************************************************
*
*********************************************************************/
private boolean nextFile() throws IOException {

    if(index >= files.length) return false;

    System.out.println("opening "+files[index]);

    reader = new BufferedReader(
             new InputStreamReader(
             new GZIPInputStream(
             new FileInputStream(files[index]))));

    ++index;

    return true;

} // end of nextFile method

/*********************************************************************
*
*********************************************************************/
public Star nextStar() throws IOException {

    while(true) {
        /**************************************
        * check if we need to open a new file *
        **************************************/
        if(reader == null) if(!nextFile()) return null;

        /*********************
        * read the next line *
        *********************/
        String line = reader.readLine();
        if(line == null) {
            /***************************************************
            * end of file - so close this one up and try again *
            ***************************************************/
            reader.close();
            reader = null;
            continue;
        }

        Star star = parseLine(line);

        if(star != null) return star;

    } // end of loop over multiple tries

} // end of nextStar method


/*********************************************************************
*
*********************************************************************/
public Star parseLine(String line) {

    /*****************
    * parse the line *
    *****************/
    String name = null;
    double ra = 0.0;
    double dec = 0.0;

    float mag_j  = Float.NaN;
    float mag_h  = Float.NaN;
    float mag_ks = Float.NaN;



    StringTokenizer tokens = new StringTokenizer(line, "|");
    for(int field = 0; field<=28; ++field) {

        String token = tokens.nextToken().trim();

        if(field == 0) {
            /*****
            * RA *
            *****/
            if(token.equals("\\N")) return null;
            ra = Double.parseDouble(token);

        } else if(field == 1) {
            /******
            * Dec *
            ******/
            if(token.equals("\\N")) return null;
            dec = Double.parseDouble(token);

//         } else if(field == mag_col) {
//             /************
//             * magnitude *
//             ************/
//             if(token.equals("\\N")) return null;
//             float mag = Float.parseFloat(token);
//             photometry = new ShortMagnitude(band, mag);

        } else if(field == 6) {
            /**************
            * J magnitude *
            **************/
            if(!token.equals("\\N")) {
                mag_j = Float.parseFloat(token);
            }

        } else if(field == 10) {
            /**************
            * H magnitude *
            **************/
            if(!token.equals("\\N")) {
                mag_j = Float.parseFloat(token);
            }

        } else if(field == 14) {
            /***************
            * Ks magnitude *
            ***************/
            if(!token.equals("\\N")) {
                mag_ks = Float.parseFloat(token);
            }

        }  else if(field == 28) {
            /***********************
            * sequential numbering *
            ***********************/
            name = token;
        }

    } // end of loop over fields

    /*******************************************
    * determine the magnitude in the sort band *
    *******************************************/
    float mag = convertMagnitude(mag_j, mag_h, mag_ks);
    if(Float.isNaN(mag)) return null;
    Photometry photometry = new ShortMagnitude(band, mag);

    /******************
    * create the star *
    ******************/
    Direction dir = new Direction(ra, dec);
    return new Star(name, dir, photometry);

} // end of parseLine method

/*********************************************************************
*
*********************************************************************/
public float convertMagnitude(float mag_j, float mag_h, float mag_ks) {

    if(     band.equals(Johnson.J) ) return mag_j;
    else if(band.equals(Johnson.H) ) return mag_h;
    else if(band.equals(Johnson.Ks)) return mag_ks;
    else if(band.equals(Johnson.K)) {
        /******************************
        * convert to Mauna Kea K band *
        ******************************/
        return (float)(mag_ks - 0.002 - (0.267/1.037)*(mag_j-mag_ks + 0.001));
    }

    return Float.NaN;

} // end of convert magnitude method

/*********************************************************************
*
*********************************************************************/
public static void main(String[] args) throws Exception {

    /****************************
    * band (mandatory argument) *
    ****************************/
    Band band = null;
    if(     args[0].equals("J" )) band = Johnson.J;
    else if(args[0].equals("H" )) band = Johnson.H;
    else if(args[0].equals("Ks")) band = Johnson.Ks;
    else if(args[0].equals("K" )) band = Johnson.K;
    else throw new IllegalArgumentException("Invalid band "+args[0]);

    /******************************
    * directory (defaults to ".") *
    ******************************/
    File top;
    if(args.length>1) top = new File(args[0]);
    else              top = new File(".");

    /**********************************
    * catalog version (defaults to 0) *
    **********************************/
    String version = "0";
    if(args.length>2) version = args[2];

    /**********************************
    * create some things we will need *
    **********************************/
    CatalogSource source = new TwoMassSource(new File(top, "psc"), band);

    File work = new File(top, "work_"+band);
    String name = "2MASS "+band;

    /*********
    * ingest *
    *********/
    Ingester ingester = new Ingester(work, band, source, name, version);
    ingester.ingest();


} // end of main


} // end of CatalogSource class