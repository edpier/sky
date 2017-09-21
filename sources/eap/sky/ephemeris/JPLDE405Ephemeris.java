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

package eap.sky.ephemeris;

import eap.sky.time.*;
import eap.sky.time.barycenter.*;
import eap.sky.util.*;
import eap.sky.earth.*;

import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

/***************************************************************************
* Implements the JPL DE405 ephemeris. As of this writing this is the standard
* high quality ephemeris. Planetary positions are integrated numerically,
* fit to observed data, and then the calculated positions are approximated by
* Chebychev polynomial expensions. This class reads the resulting polynomial
* coeficients from the standard ASCII format files provided by NASA.
* <p>
* See
* <a href="http://ssd.jpl.nasa.gov/eph_info.html">
*          http://ssd.jpl.nasa.gov/eph_info.html</a>
* For a description of the ephemeris, and to download the coeficient files.
* The coeficient files compatible with this class are available from
* <A href="ftp://ssd.jpl.nasa.gov/pub/eph/export/ascii/">
*          ftp://ssd.jpl.nasa.gov/pub/eph/export/ascii/</a>.
* They have names of the form ascpYYYY.405, where "YYYY" is the starting year
* of the file. Each file covers 20 years, and data are available from
* 1900 to 2200. You need to place these in a directory on a disk visible to
* your program. You don't need to download all the files, only the ones
* for the times in which you are interested.
* This ephemeris uses the default {@link UT1System} at the time you
* called the constructor.
***************************************************************************/
public class JPLDE405Ephemeris extends Ephemeris {

/** Ratio of mass of Earth to mass of Moon */
private static final double EARTH_MOON_RATIO = 81.30056;

private static final double[] file_dates = {
        2414992.5, 2422320.5, 2429616.5, 2436912.5, 2444208.5,
        2451536.5, 2458832.5, 2466128.5, 2473456.5, 2480752.5,
        2488048.5, 2495344.5, 2502672.5, 2509968.5, 2517264.5};

private static final String[] file_names = {
        "1900", "1920", "1940", "1960", "1980", "2000", "2020", "2040",
        "2060", "2080", "2100", "2120", "2140", "2160", "2180"};

private JPLFileReader file_reader;
Remote file_reader_stub;

private Map<String, JPLFile> files;

/***************************************************************************
* Create a new ephemeris using the default {@link UT1System}.
***************************************************************************/
private JPLDE405Ephemeris()  {

    super(UT1System.getInstance());

    files = new HashMap<String, JPLFile>();

}

/***************************************************************************
* Create a new ephemeris.
* @param data_dir The directory containing the Chebychev coeficient files.
***************************************************************************/
public JPLDE405Ephemeris(File data_dir)  {

    this();

    file_reader = new LocalJPLFileReader(data_dir);

} // end of constructor.

/***************************************************************************
* Create a new ephemeris.
* @param data_dir The directory containing the Chebychev coeficient files.
***************************************************************************/
public JPLDE405Ephemeris(JPLFileReader file_reader)  {

    this();

    this.file_reader = file_reader;

} // end of constructor.

/****************************************************************************
*
****************************************************************************/
private void writeObject(ObjectOutputStream out) throws IOException {

    out.writeObject(files);

    if(file_reader_stub == null) {
        file_reader_stub = UnicastRemoteObject.exportObject(file_reader, 0);
    }
    out.writeObject(file_reader_stub);

} // end of writeObject method

/****************************************************************************
*
****************************************************************************/
private void readObject(ObjectInputStream in) throws IOException,
                                           ClassNotFoundException  {

    files = (Map<String, JPLFile>)in.readObject();

    file_reader = (JPLFileReader)in.readObject();
    file_reader_stub = file_reader;

} // end of writeObject method


/***************************************************************************
*
***************************************************************************/
public ThreeVector barycentricPosition(int body, PreciseDate tdb) {

    /************************************
    * convert the time to a Julian Date *
    ************************************/
    JulianDate jd = new JulianDate(tdb);

    /***************************************
    * locate the file for this Julian Date *
    ***************************************/
    JPLFile jpl_file = findFile(jd);

    /************************************************
    * we need to do some special handling for
    * the Earth and moon
    ************************************************/
    if(body == EARTH || body == MOON) {

        /**************************************************
        * get the position of the Earth-moon barycenter *
        ************************************************/
        ThreeVector bary = jpl_file.position(EARTH, jd);

        /*********************************************************
        * get the position of the moon with respect to the earth *
        *********************************************************/
        ThreeVector moon = jpl_file.position(MOON,  jd);

        /********************************
        * get the position of the Earth *
        ********************************/
        double moon_mass_fraction = 1.0/(1.0+EARTH_MOON_RATIO);
        ThreeVector earth = bary.minus(moon.times(moon_mass_fraction));

        /**********************************
        * ...and the position of the moon *
        **********************************/
        moon = moon.plus(earth);

        if(body == EARTH) return earth;
        else              return moon;


    } else {
        /**************
        * normal case *
        **************/
        return jpl_file.position(body, jd);

    }


} // end of getSolarCentricPosition method

/***************************************************************************
*
***************************************************************************/
public ThreeVector barycentricVelocity(int body, PreciseDate tdb) {


    /************************************
    * convert the time to a Julian Date *
    ************************************/
    JulianDate jd = new JulianDate(tdb);

    /***************************************
    * locate the file for this Julian Date *
    ***************************************/
    JPLFile jpl_file = findFile(jd);

    /************************************************
    * we need to do some special handling for
    * the Earth and moon
    ************************************************/
    if(body == EARTH || body == MOON) {

        /**************************************************
        * get the position of the Earth-moon barycenter *
        ************************************************/
        ThreeVector bary = jpl_file.velocity(EARTH, jd);

        /*********************************************************
        * get the position of the moon with respect to the earth *
        *********************************************************/
        ThreeVector moon = jpl_file.velocity(MOON,  jd);

        /********************************
        * get the position of the Earth *
        ********************************/
        double moon_mass_fraction = 1.0/(1.0+EARTH_MOON_RATIO);
        ThreeVector earth = bary.minus(moon.times(moon_mass_fraction));

        /**********************************
        * ...and the position of the moon *
        **********************************/
        moon = moon.plus(earth);

        if(body == EARTH) return earth;
        else              return moon;


    } else {
        /**************
        * normal case *
        **************/
        return jpl_file.velocity(body, jd);

    }

} // end of getBarycentricVelocity method

/***************************************************************************
* Locates the Chebychev coeficient file covering a given Julian Date.
* @param jd The Julan date.
* @throws IllegalArgumentException If there is no corresponding file or if
* there was trouble reading the file.
***************************************************************************/
private JPLFile findFile(JulianDate jd) {

    double date = jd.getJulianDate();

    /**********************************************
    * get the file year corresponding to the date *
    **********************************************/
    String year = null;
    for(int index = 0; index< file_names.length; ++index) {

        if(date >= file_dates[index] &&
           date <  file_dates[index+1]  ) {
            year = file_names[index];
        }
    }

    /*****************************************
    * check if there is a file for this date *
    *****************************************/
    if(year == null) {
        throw new IllegalArgumentException("No Ephemeris file for "+jd);
    }

    /************************************
    * check if we already have this file *
    *************************************/
    JPLFile file = (JPLFile)files.get(year);
    if(file != null) return file;

    /***********************
    * try reading the file *
    ***********************/
    try {
        file = file_reader.readFile(year);
        files.put(year, file);
        return file;

    } catch(IOException e) {
        IllegalArgumentException e2 =
                   new IllegalArgumentException("Could not read file for "+year);
        e2.initCause(e);
        throw e2;
    }

} // end of findFile method

} // end of Ephemeris class
