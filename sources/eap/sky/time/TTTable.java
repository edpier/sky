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

import java.net.URL;
import java.util.*;
import java.io.*;

/****************************************************************************
* A lookup table giving the offset between TAI and TT.
* @see TTSystem
****************************************************************************/
public class TTTable {

private static final TAISystem TAI = TAISystem.getInstance();

URL source;

String abbreviation;

List<PreciseDate> tais;
List<PreciseDate> tts;

TTSystem system;

int index;

/****************************************************************************
* Create a new object.
* @param source The URL from which to read the table.
* @param UTC We need this because the standard tables are given as a function
* of the UTC Julian Date.
****************************************************************************/
public TTTable(URL source, UTCSystem UTC) throws IOException {

    tais = new ArrayList<PreciseDate>();
    tts  = new ArrayList<PreciseDate>();

    /***************
    * open the URL *
    ***************/
    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(source.openStream()));

    /***************************************************************
    * the first string in the file should be the abbreviation name *
    ***************************************************************/
    String line = reader.readLine();
    int space = line.indexOf(" ");
    if(space == -1) throw new IOException("No spaces in the first line");
    abbreviation = line.substring(0,space);
    if(!abbreviation.startsWith("TT(BIPM")) {
        throw new IOException(abbreviation+" is not of the form TT(BIPMxx)");
    }

    /**********************************************************
    * now that we have the date, we can create the time system
    * for this table
    ***********************************************************/
    system = new BIPMTTSystem(this);

    /******************************************************
    * skip to the start of the table, which comes after
    * two consecutive blank lines
    ******************************************************/
    int blank_count=0;
    while(blank_count<2) {

        /**************
        * read a line *
        **************/
        line = reader.readLine();

        /****************
        * check for EOF *
        ****************/
        if(line==null) {
            throw new IOException("Could not find table data");
        }

        /**********************
        * count the blank lines *
        ************************/
        if(line.trim().equals("")) ++blank_count;
        else                       blank_count = 0;

    } // end of loop over header lines

    /****************
    * read the data *
    ****************/
    JulianDate jd = new JulianDate(UTC);
    while((line=reader.readLine())!=null) {

        /***********
        * read MJD *
        ***********/
        int sep = line.indexOf(".");
        if(sep==-1) {
            throw new IOException("No '.' in "+line);
        }

        double mjd = Double.parseDouble(line.substring(0, sep+1));

        /********************************
        * convert MJD UTC to a TAI date *
        ********************************/
        jd.setModifiedJulianDate(mjd);
        PreciseDate tai = TAI.createDate();
        tai.setTime(jd.toDate());

        /**************
        * read TT-TAI *
        **************/
        line = line.substring(sep+1).trim();
        sep = line.indexOf(" ");
        double delta = Double.parseDouble(line.substring(sep));

        /************************
        * determine the TT date *
        ************************/
        PreciseDate tt = system.createDate();
        tt.setTime(tai.getMilliseconds(), tai.getNanoseconds());
        tt.increment(TTSystem.TAI_OFFSET);
        tt.increment(delta*1e-6);

//         System.out.println(tai);
//         System.out.println(tt);
//         System.out.println();

        /*************************************
        * add the pair of times to the table *
        *************************************/
        addEntry(tai, tt);


    } // end of loop over data rows



} // end of constructor

/****************************************************************************
* Returns the name of the time system, which specifies the table used.
****************************************************************************/
public String getAbbreviation() { return abbreviation; }

/****************************************************************************
* Returns a TTSystem which uses this table.
****************************************************************************/
public TTSystem getTTSystem() { return system; }

/****************************************************************************
* Add an entry to the table.
****************************************************************************/
private void addEntry(PreciseDate tai, PreciseDate tt) {

    if(! tt.getTimeSystem().equals(system)) {
        throw new IllegalArgumentException(tt+" is not in "+system);
    }

        if(! tai.getTimeSystem().equals(TAI)) {
        throw new IllegalArgumentException(tai+
                                    " is not in "+TAISystem.getInstance());
    }

    tais.add(tai);
    tts.add(tt);

} // end of addEntry method

/****************************************************************************
* Find a particular date in a lost of dates.
* @param date The date to locate
* @param list The list of dates.
****************************************************************************/
private int findInterval(PreciseDate date, List list) {

    int index = this.index;

    if(index<0) ++index;
    if(index>list.size()-2) --index;

    while(index >=0 && date.compareTo((Date)list.get(index))<0 ) --index;

    while(index< list.size()-1 && date.compareTo((Date)list.get(index+1)) >=0) ++index;

    this.index = index;
    return index;

} // end of findInterval method

/****************************************************************************
* Returns an independant copy of a date from a list.
* @param list The list of dates we are selecting from.
* @param index the date we are selecting.
****************************************************************************/
private PreciseDate getClone(List list, int index) {

    PreciseDate date = (PreciseDate)list.get(index);
    return (PreciseDate)date.clone();
}

/****************************************************************************
* Interpolate a value between two dates.
* @param xx
****************************************************************************/
private void interpolate(PreciseDate xx, PreciseDate yy,
                                List x, List y) {

    /*********************************
    * find where we are in the table *
    *********************************/
    int index = findInterval(xx, x);

    /********************************
    * check if we are off the table *
    ********************************/
    if(index < 0) {
        throw new IllegalArgumentException(xx+
                                    " is before the valid range of "+system);
    }


    if(index >= x.size()-1) {
            throw new IllegalArgumentException(xx+
                                    " is after the valid range of "+system);
    }

    /**************
    * interpolate *
    **************/
    PreciseDate x0 = (PreciseDate)x.get(index);
    PreciseDate x1 = (PreciseDate)x.get(index+1);

    PreciseDate y0 = (PreciseDate)y.get(index);
    PreciseDate y1 = (PreciseDate)y.get(index+1);

//     System.out.println("xx="+xx);
//     System.out.println("x0="+x0);
//     System.out.println("x1="+x1);

    double hat = xx.secondsAfter(x0) / x1.secondsAfter(x0);

//     System.out.println("hat="+hat);

    double offset = hat * y1.secondsAfter(y0);

    yy.setTime(y0);
    yy.increment(offset);

} // end of interpolate method

/************************************************************************
* Applies the offset to a date.
* @param tai The TAI date to be converted.
* @param tt The result of the conversion will be set in this object.
************************************************************************/
public void convertTAItoTT(PreciseDate tai, PreciseDate tt) {

    if(!tai.getTimeSystem().equals(TAI)) {
        throw new IllegalArgumentException(tai+" is not in "+TAI);
    }

    if(!tt.getTimeSystem().equals(system)) {
        throw new IllegalArgumentException(tt+" is not in "+system);
    }

    interpolate(tai, tt, tais, tts);

} // end of convertTAItoTT method

/************************************************************************
* Applies the offset to a date.
* @param tt The TT date to be converted.
* @param tai The result of the conversion will be set in this object.
************************************************************************/
public void convertTTtoTAI(PreciseDate tt, PreciseDate tai) {

    if(!tai.getTimeSystem().equals(TAI)) {
        throw new IllegalArgumentException(tai+" is not in "+TAI);
    }

    if(!tt.getTimeSystem().equals(system)) {
        throw new IllegalArgumentException(tt+" is not in "+system);
    }

    interpolate(tt, tai, tts, tais);

} // end of convertTTtoTAI method

} // end of TTTable class
