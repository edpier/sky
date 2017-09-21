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

package eap.sky.time;

import java.net.*;
import java.io.*;
import java.util.*;

/******************************************************************************
* Represents a LeapTable which can initialize itself from an ASCII list in the
* format used by the US Naval Observatory's web site. As of this writing the
* URL for this table is <a href="http://maia.usno.navy.mil/ser7/tai-utc.dat">
* http://maia.usno.navy.mil/ser7/tai-utc.dat</a>.
******************************************************************************/
public class USNOLeapTable extends LeapTable {

/** The URL from which to obtain the leap secnd data **/
private URL source;

/******************************************************************************
* Create a new leap second table which is initialized from the given URL.
* @param source The URL from which to obtain the data.
******************************************************************************/
public USNOLeapTable(URL source) throws IOException {

    this.source = source;

    update();

} // end of constructor

/******************************************************************************
* Parses the data source and adds leapseconds to the table.
* @throws IOException If there was an error obtaining or parsing the data.
******************************************************************************/
public void update() throws IOException {

    /*******************************
    * make a reader for the source *
    *******************************/
    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(source.openStream()));

    /**************************************************
    * initialize some stuff we will need when reading *
    **************************************************/
    double last_offset=10.0;
    String string;
    JulianDate jd = new JulianDate(new UTCSystem(this));
    for(int line=0; (string=reader.readLine()) != null; ++line) {

        StringTokenizer tokens = new StringTokenizer(string);

        /**************************************************
        * ignore leapsecond information before 1972 UTC
        * since this refers to a different time keeping
        * system in effect before the advent of UTC
        **************************************************/
        int year = Integer.parseInt(tokens.nextToken());
        if(year< 1972) continue;

        /*************************************
        * three character month abbreviation *
        *************************************/
        String month = tokens.nextToken();
        if(month.length() != 3 ) {
            throw new IOException("Second field of line "+line+" is "+month);
        }

        /*****************************************************
        * the day field must always be "1" according to the
        * definition of UTC
        *****************************************************/
        int day = Integer.parseInt(tokens.nextToken());
        if(day != 1) {
            throw new IOException("Third field of line "+line+" is "+day);
        }

        /*****************************
        * this field is always "=JD" *
        *****************************/
        if(!tokens.nextToken().equals("=JD")) {
                throw new IOException("Fourth field of line "+line+
                                      " is not =JD");
        }

        /*************
        * Julian day *
        *************/
        jd.set(Double.parseDouble(tokens.nextToken()));

        /*************
        * "TAI-UTC=" *
        *************/
        if(!tokens.nextToken().equals("TAI-UTC=")) {
               throw new IOException("Sixth field of line "+line+
                                     " is not TAI-UTC=");
        }

        /********************
        * offset in seconds *
        ********************/
        double offset = Double.parseDouble(tokens.nextToken());
        double change = offset - last_offset;
        last_offset = offset;

        boolean plus;
        if(      change==0.0) continue;
        else if(change== 1.0) plus = true;
        else if(change==-1.0) plus = false;
        else {
            throw new IOException("Invalid offset change "+change+
                                  " in line "+line);
        }

     //   System.out.println(year+" "+jd+" "+change);

        /********************************************
        * convert the Julian date to a precise date *
        ********************************************/
        UTCDate date = (UTCDate)jd.toDate();
        if(plus) {
            /*************************************************************
            * The USNO table always gives the second after the leapsec,
            * but for positive leap seconds, the LeapTable class
            * requires the leap second itself. So we need to
            * subtract one second and flip on the leapsec flag
            *************************************************************/
            date.setTime(date.getMilliseconds() - 1000,
                         date.getNanoseconds(), true);

  //System.out.println("after offset: "+ date);
        }

//         System.out.println(jd);
//         System.out.println(date);
//         System.out.println(new Date(date.getMilliseconds()));
//         System.out.println("________________________________");

        /************************************
        * add the leap second to this table *
        ************************************/
        // NOTE we oughtta modify this to allow incremental updates.
        // i.e. ignore leapseconds which are already in the table.
        addLeapSecond(date, plus);


    } // end of loop over lines


} // end of update method



} // end of USNOLeapTable class
