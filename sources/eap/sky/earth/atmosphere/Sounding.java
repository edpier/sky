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

package eap.sky.earth.atmosphere;

import java.io.*;
import java.util.*;

/************************************************************************
* A collection of weather measurements as a function of altitude.
* This can be used to calculate refraction by integrating through the
* atmosphere. We have not yet implemented such integrations.
************************************************************************/
public class Sounding {

SortedMap<Double, Weather> data;
int index;

/************************************************************************
* Create a new empty sounding.
************************************************************************/
public Sounding() {

    data = new TreeMap<Double, Weather>();

} // end of constructor

/************************************************************************
* Add a new sounding.
* @param height The altitude of the measurement.
* @param weather The measurement.
************************************************************************/
public void addRecord(double height, Weather weather) {

    data.put(new Double(height), weather);

} // end of addRecord method

/************************************************************************
* Reads a radiosonde profile of the atmosphere from a standard format
* available on the world wide web.
* @param in The data source
* @throws IOException if there is trouble reading the data.
************************************************************************/
public void read(InputStream in) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

    boolean header=true;
    String line;
    while((line=reader.readLine()) != null) {

        /***********************************************
        * the header is terminated by a horizontal bar *
        ***********************************************/
        if(header) {
            if(line.startsWith("--------------------------------")) {
                header = false;
            }
            continue;
        }

        /*****************************
        * the data end with "</pre>" *
        *****************************/
        if(line.startsWith("</pre>")) break;

        /***********************
        * parse a line of data *
        ***********************/
        StringTokenizer tokens = new StringTokenizer(line);

        double pressure  = Double.parseDouble(tokens.nextToken()) * 100.0;
        double height    = Double.parseDouble(tokens.nextToken());
        double celsius   = Double.parseDouble(tokens.nextToken());
        double dew_point = Double.parseDouble(tokens.nextToken());

        addRecord(height, new Weather(pressure, celsius,
                                      new DewPoint(dew_point, false)) );

        /******************************
        * the data ends with "</pre>" *
        ******************************/
        if(line.startsWith("</pre>")) break;
    }


} // end of read method

/************************************************************************
* Returns the weather at an arbitrary altitude, interpolating as necessary.
* @param height The altitude.
* @return the weather at that altitude.
************************************************************************/
public Weather getWeather(double height) {

    double h0=-Double.NEGATIVE_INFINITY;
    Weather weather0=null;

    double h1;
    Weather weather1;

    for(Iterator it = data.entrySet().iterator(); it.hasNext(); ) {
        /************************
        * get the current entry *
        ************************/
        Map.Entry entry = (Map.Entry)it.next();

        h1 = ((Number)entry.getKey()).doubleValue();
        weather1 = (Weather)entry.getValue();

        /*********************************
        * check if we found our interval *
        *********************************/
        if(weather0 != null && (h1 > height || !it.hasNext()) ) {

            /***************************
            * see if we hit it exactly *
            ***************************/
            if(h0 == height) return weather0;
            if(h1 == height) return weather1; // for the last table entry

            /*******************************
            * interpolate (or extrapolate) *
            *******************************/
            double hat = (height-h0)/(h1-h0);
            return weather0.interpolate(weather1, hat);

        } // end if we found the interval


     //   System.out.println("h="+h);

        /***************************
        * save the previous values *
        ***************************/
        h0 = h1;
        weather0 = weather1;


    } // end of loop over entries

    /**************************************************************
    * we only get here if there are less than two data entries
    * so handle the cases of one or none entry
    **************************************************************/
    if(data.size() == 0) return null;
    else                 return (Weather)data.get(data.lastKey());


} // end of getWeather method

} // end of Sounding class
