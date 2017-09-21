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

import java.io.*;
import java.util.*;

/****************************************************************************
* A file containing JPL DE405 ephemeris Chebychev coeficients. This
* class is a utility used by {@link JPLDE405Ephemeris}.
****************************************************************************/
public class JPLFile implements Serializable {

private TDBSystem TDB;

private List<JPLInterval> intervals;

/****************************************************************************
* Create a new ephemeris file.
****************************************************************************/
public JPLFile() {


    intervals = new ArrayList<JPLInterval>();

} // end of constructor


/****************************************************************************
* Read the contents of the file.
* @param in The data source
* @throws IOException if there is trouble reading the file.
****************************************************************************/
public void read(InputStream in ) throws IOException {

    /*************************************************
    * wrap a buffered reader around the input stream *
    *************************************************/
    BufferedReader reader= new BufferedReader(new InputStreamReader(in));

    /*****************************************
    * clear out any intervals previously read *
    *****************************************/
    intervals.clear();

    /*************************************
    * read all the intervals in the file *
    *************************************/
    while(true) {
        JPLInterval interval = new JPLInterval();

        try { interval.read(reader); }
        catch(EOFException e) { break; }

        intervals.add(interval);

    } // end of loop over intervals


} // end of read method

/***********************************************************************
* Fetches a particular time interval from the file
* param index The zero offset index of the desired interval.
***********************************************************************/
public JPLInterval getInterval(int index) {

//System.out.println("finding JPL interval for index "+index);
//System.out.println("intervals size ="+intervals.size());
    return (JPLInterval)intervals.get(index);
}

/***********************************************************************
* Locates the a time interval by time.
* @param jd A Julian date whch the interval should contain.
***********************************************************************/
public JPLInterval findInterval(JulianDate jd) {

//System.out.println("finding JPL interval for "+jd);

    /*****************************************************
    * make a first guess what interval we should look at *
    *****************************************************/
    JPLInterval interval = getInterval(0);

    int index = (int)Math.floor((jd.getJulianDate() -
                                 interval.getStartJD())/interval.getDuration());

    while(!(interval = getInterval(index)).contains(jd)) {

     //   System.out.println("didn't get it");

    }

    return interval;

} // end of findInterval method

/***********************************************************************
* Calculate the barycentric position of a body.
* @param body The body in question. This should be one of the static
* variables in {@link Ephemeris}.
* @param jd The Julian date at which to calculate the position. This
* should be expressed in TDB.
* @see TDBSystem
***********************************************************************/
public ThreeVector position(int body, JulianDate jd) {

    return findInterval(jd).evaluate(body, jd, false);
}

/***********************************************************************
* Calculate the barycentric velocity of a body.
* @param body The body in question. This should be one of the static
* variables in {@link Ephemeris}.
* @param jd The Julian date at which to calculate the velocity. This
* should be expressed in TDB.
* @see TDBSystem
***********************************************************************/
public ThreeVector velocity(int body, JulianDate jd) {

    return findInterval(jd).evaluate(body, jd, true);
}

} // end of JPLFile class
