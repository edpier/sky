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

package eap.sky.earth;

import java.util.*;
import java.io.*;
import java.net.URL;

import eap.sky.time.*;
import eap.sky.time.barycenter.*;

/***************************************************************************
* Represents a table of Earth orientation parameters as a function of time.
* The <a href="http://www.iers.org">
* International Earth Rotation and Reference Systems Service (IERS)</a> and
* the <a href="http://maia.usno.navy.mil">IERS Rapid Service/Prediction
* Center</a>
* are responsible for determining the rotation and polar motion of the Earth
* with high accuracy and providing the results to the public. 
* See {@link #EOPBulletin(URL, UTCSystem, TDBSystem, int, EOPCorrection)}
* for details about the source of these data.
* <p>
* Typically you do not use this class by itself. Instead you use a
* {@link  UT1System}
* which holds an instance of an EOPTable to create an {@link EOP} object
* and then use {@link EOP#setTime(PreciseDate)} to get the Earth Orientation
* Parameters at a given time.
***************************************************************************/
public class EOPBulletin implements EOPTable {

/** IERS Bulletin A - Values updated daily for near real-time use **/
public static final int BULLETIN_A = 1;

/** IERS Bulletin B - Higher accuracy values released a month after the fact **/
public static final int BULLETIN_B = 2;



private static final int CHUNK = 1024*16;

private static final TAISystem TAI = TAISystem.getInstance();

UTCSystem UTC;
UT1System UT1;
TDBSystem TDB;


URL source;

List<PreciseDate> times;
List<EOP> eops;

double first_dut1;
double last_dut1;


int index;

int bulletin;

EOPCorrection correction;
//PrecessionModel precession;

/***************************************************************************
* Create a new table of Earth orientation parameters. The parameters
* are read from a URL. This URL
* must refer to a file in the format for combined Bulletin A and B
* values from the IERS using the IAU2000A Nutation/Precession Theory.
* This format is described in the document
* <a href="http://maia.usno.navy.mil/ser7/readme.finals2000A">
* http://maia.usno.navy.mil/ser7/readme.finals2000A</a> whose content
* is copied below:
* <pre>
* The format of the finals2000A.data, finals2000A.daily, and finals2000A.all files is:
*
* Col.#    Format  Quantity
* -------  ------  -------------------------------------------------------------
* 1-2      I2      year (to get true calendar year, add 1900 for MJD<=51543 or add 2000 for MJD>=51544)
* 3-4      I2      month number
* 5-6      I2      day of month
* 7        X       [blank]
* 8-15     F8.2    fractional Modified Julian Date (MJD)
* 16       X       [blank]
* 17       A1      IERS (I) or Prediction (P) flag for Bull. A polar motion values
* 18       X       [blank]
* 19-27    F9.6    Bull. A PM-x (sec. of arc)
* 28-36    F9.6    error in PM-x (sec. of arc)
* 37       X       [blank]
* 38-46    F9.6    Bull. A PM-y (sec. of arc)
* 47-55    F9.6    error in PM-y (sec. of arc)
* 56-57    2X      [blanks]
* 58       A1      IERS (I) or Prediction (P) flag for Bull. A UT1-UTC values
* 59-68    F10.7   Bull. A UT1-UTC (sec. of time)
* 69-78    F10.7   error in UT1-UTC (sec. of time)
* 79       X       [blank]
* 80-86    F7.4    Bull. A LOD (msec. of time) -- NOT ALWAYS FILLED
* 87-93    F7.4    error in LOD (msec. of time) -- NOT ALWAYS FILLED
* 94-95    2X      [blanks]
* 96       A1      IERS (I) or Prediction (P) flag for Bull. A nutation * values
* 97       X       [blank]
* 98-106   F9.3    Bull. A dX wrt IAU2000A Nutation (msec. of arc), Free Core Nutation NOT Removed
* 107-115  F9.3    error in dX (msec. of arc)
* 116      X       [blank]
* 117-125  F9.3    Bull. A dY wrt IAU2000A Nutation (msec. of arc), Free Core Nutation NOT Removed
* 126-134  F9.3    error in dY (msec. of arc)
* 135-144  F10.6   Bull. B PM-x (sec. of arc)
* 145-154  F10.6   Bull. B PM-y (sec. of arc)
* 155-165  F11.7   Bull. B UT1-UTC (sec. of time)
* 166-175  F10.3   Bull. B dX wrt IAU2000A Nutation (msec. of arc)
* 176-185  F10.3   Bull. B dY wrt IAU2000A Nutation (msec. of arc)
* </pre>
* Files in this format are available from the
* <a href="http://maia.usno.navy.mil">IERS Rapid Service/Prediction
* Center</a>. Currently The URLs for these files are
* <ul>
* <li> <a href=http://maia.usno.navy.mil/ser7/finals2000A.data>
*      http://maia.usno.navy.mil/ser7/finals2000A.data</a> -
*      containing data since 1992-01-01, updated weekly.
* <li> <a href="http://maia.usno.navy.mil/ser7/finals2000A.all">
*      http://maia.usno.navy.mil/ser7/finals2000A.all</a> -
*      containing data since 1973-01-02, updated weekly
* <li> <a href="http://maia.usno.navy.mil/ser7/finals2000A.daily">
*      http://maia.usno.navy.mil/ser7/finals2000A.daily</a> -
*      containing recent data, but updated daily.
* </ul>
* <p>
* The IERS Bulletin B contains final values plus future predctions
* and is published once a month.
* The IERS Bulletin A contains quickly determined measured values and
* predictions up to a year in the future. It is published at least
* weekly.
* <p>
* Bulletin B does not provide measurement errors,
* so this table sets the errors to zero for Bulletin B values.
* @param source The URL from which to read the paramaters.
* @param UTC The UTC time system used by this table.
*        This system must contain all
*        the leap seconds which are taken into account in the source data.
*        See {@link LeapTable}.
* @param TDB The TDB time system used by this table
* @param bulletin Indicates whether to use IERS Bulletin A or B. Must be
*        {@link #BULLETIN_A} or {@link #BULLETIN_B} or
         BULLETIN_A | BULLETIN_B.
*        In the last case, the created object will read Bulletin B values
*        when they are available and not predicted, and will use
*        Bulletin A otherwise.
* @param correction The type of diurnal/semidiurnal corrections to be
*        applied to the EOP parameters. The values tabulated by the IERS
*        are smoothed over a day or two. However short term variability
*        due to tidal effects can intruduce significant systematic effects.
*        This parameter specifies the model-dependant corrections to add to
*        compensdate for this.
***************************************************************************/
public EOPBulletin(URL source, UTCSystem UTC, TDBSystem TDB,
                int bulletin, EOPCorrection correction) throws IOException {

    /*********************************************
    * make sure the bulletin value is 1, 2, or 3 *
    *********************************************/
    if(bulletin == 0 || bulletin >> 2 != 0 ) {
        throw new IllegalArgumentException("Illegal bulletin value "+bulletin);
    }

    /**************
    * copy values *
    **************/
    this.source = source;
    this.UTC = UTC;
    this.TDB = TDB;
    this.UT1 = new UT1System(this, null);
    this.bulletin = bulletin;
    this.correction = correction;

    /************************
    * initialize the arrays *
    ************************/
    times = new ArrayList<PreciseDate>();
    eops  = new ArrayList<EOP>();

    /**************************
    * initialize the corrections *
    *****************************/


    /*****************
    * read the table *
    *****************/
    update();

} // end of constructor

/***************************************************************************
* A convience constructor which chooses the best available of bulletin A and B,
*  and
* the latest diurnal and semidiurnal
* tidal corrections and precession model.
* @param source The data source
* @param UTC the UTC time system to use to interpret the table
* @param TDB The TDB tie system used to calculate tidal corrections.
* @see #EOPBulletin(URL, UTCSystem, TDBSystem, int, EOPCorrection)
***************************************************************************/
public EOPBulletin(URL source, UTCSystem UTC, TDBSystem TDB)
                                                        throws IOException {

    this(source, UTC, TDB, BULLETIN_A|BULLETIN_B,
         EOPCorrection.IERS2003);
}

/***************************************************************************
* Return the UTC system used by this table.
***************************************************************************/
//public UTCSystem getUTCSystem() { return UTC; }

/***************************************************************************
* Deletes all data from the table.
***************************************************************************/
public void clearTable() {

    times.clear();
    eops.clear();

} // end of clearTable method

/***************************************************************************
* Adds an entry to the end of this table. Note that entries must be added
* chronologically.
* @param time the time at which the Earth orientation parameters are valid.
* @param eop The Earth orientation parameters at that time.
* @throws IllegalArgumentException if the given time does not come after all
*         the entries currently in the table.
***************************************************************************/
public void addRow(PreciseDate time, EOP eop) {

    /*****************************************************
    * make sure the table data are in chonological order *
    *****************************************************/

    if(times.size() > 0 && !time.after(getTime(times.size()-1)) ) {

        throw new IllegalArgumentException(time+" is not after "+
                                           getTime(times.size()-1));
    }

    /*****************
    * add the values *
    *****************/
    times.add(time);
    eops.add(eop);

} // end of addRow method

/***************************************************************************
* Reads the data from the source specified in the constructor.
* Note that this may change data already in this table. It is not safe
* to access the table while this method is executing.
* This method is called by the constructor, so it is not necessary to
* call it, unless you want to account for new data not available when the
* object was constructed.
* @throws IOException if there is an error accessing the data.
***************************************************************************/
public void update() throws IOException {

    /*************************
    * clear out any old data *
    *************************/
    clearTable();

    /***********************
    * open the data source *
    ***********************/
    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(source.openStream()));

    JulianDate jd = new JulianDate(UTC);


    /**********************************
    * loop over the lines in the file *
    **********************************/
    String line;
    while((line = reader.readLine()) != null &&
          !line.substring(18,27).trim().equals("") ) {

        /********************************
        * read the modified julian date *
        ********************************/
        double mjd    = Double.parseDouble(line.substring(  7,  14));
        jd.setModifiedJulianDate(mjd);




        /**********************************
        * determine which bulletin to use *
        **********************************/
        int use;
        if(bulletin == BULLETIN_A || bulletin == BULLETIN_B) {
            use = bulletin;
        } else {
            /**************************************************
            * use bulletin B when available and not predicted
            * bulleting A otherwise
            **************************************************/
            if(!line.substring( 16,  16).equals("P") &&
               !line.substring(134, 144).trim().equals("") ) {
                use = BULLETIN_B;
            } else {
                use = BULLETIN_A;
            }
        }

        /***********************************************
        * these are all the things we're going to read *
        ***********************************************/
        double dut1;
        double x;
        double y;
        double dX;
        double dY;

        double dut1_err = 0.0;
        double x_err    = 0.0;
        double y_err    = 0.0;
        double dX_err = 0.0;
        double dY_err = 0.0;

        PrecessionCorrection precession = null;

        String source;

        /*****************************************************
        * We prefer the bulletin B values, unless they are
        * predictions or not available
        ****************************************************/
        if(use == BULLETIN_B) {
            /************************
            * use Bulletin B values *
            ************************/
            source = "B";
            x        = Double.parseDouble(line.substring(134, 144));
            y        = Double.parseDouble(line.substring(144, 154));
            dut1     = Double.parseDouble(line.substring(154, 165));
            dX       = Double.parseDouble(line.substring(165, 175));
            dY       = Double.parseDouble(line.substring(175, 185));

            precession = new PrecessionCorrection(dX, dY, dX_err, dY_err);
        } else {
            /************************
            * use bulletin A values *
            ************************/
            source = "A";
            x        = Double.parseDouble(line.substring( 18, 27));
            x_err    = Double.parseDouble(line.substring( 27, 36));
            y        = Double.parseDouble(line.substring( 37, 46));
            y_err    = Double.parseDouble(line.substring( 46, 55));
            dut1     = Double.parseDouble(line.substring( 58, 68));
            dut1_err = Double.parseDouble(line.substring( 68, 78));

            if(line.substring( 97, 134).trim().length() != 0) {
                dX       = Double.parseDouble(line.substring( 97, 106));
                dX_err   = Double.parseDouble(line.substring(106, 115));
                dY       = Double.parseDouble(line.substring(116, 125));
                dY_err   = Double.parseDouble(line.substring(125, 134));
            } else {
                /***********************************************
                * no correction information, so set it to zero *
                * perhaps the error should have a finite value?
                ***********************************************/
                dX = 0.0;
                dY = 0.0;
                dX_err = 0.0;
                dY_err = 0.0;
            }

            precession = new PrecessionCorrection(dX, dY, dX_err, dY_err);




        } // end if we are using bulletin A

        /********************************
        * convert the mjd to a TAI date *
        ********************************/
        PreciseDate utc = jd.toDate();
        PreciseDate tai = TAI.convertDate(utc);

        /************************************************
        * determine the UT1 time by offsetting from UTC *
        ************************************************/
        EOP ut1 = (EOP)UT1.createDate();
        ut1.setTime(utc.getMilliseconds(), utc.getNanoseconds());
        ut1.increment(dut1);

        /*********************************************
        * set the polar motion paramaters and errors *
        *********************************************/
        ut1.setTime(ut1.getMilliseconds(), ut1.getNanoseconds(),
                    dut1_err,
                    new PolarMotionParameters(x, y, x_err, y_err),
                    precession, null);

        if(times.size() == 0) first_dut1 = dut1;
        last_dut1 = dut1;

        /****************************
        * Add the rows to the table *
        ****************************/
        addRow(tai, ut1);

    } // end of loop over lines



} // end of update method

/***************************************************************************
* Returns the time stamp for a given row of the table.
* @param index the index of a row, with zero for the first row
* @return The time stamp
***************************************************************************/
private PreciseDate getTime(int index) {
    return (PreciseDate)times.get(index);
}

/***************************************************************************
* Returns the Earth orientation parameters for a given row of the table.
* @param index the index of a row, with zero for the first row
* @return The Earth orientation parameters.
***************************************************************************/
private EOP getEOP(int index) {
    return (EOP)eops.get(index);
}


/***************************************************************************
* Interpolates a value in the given data array.
* The values are interpolated with a cubic polynomial which
* passes through
* the the four points at index-1 through index+2. 
* the errors are propagated as a root weighted mean square.
* @param x The Modified Julian Day at which to interpolate the value
* @param index The index returned by {@link #findInterval(double)}
* @param y The array of values to interpolate.
***************************************************************************/
private void interpolate(PreciseDate tai, EOP ut1) {



    /*********************************
    * find the interval containing x *
    *********************************/
    int index = this.index;
    while(index > 1 && tai.before(getTime(index)) ) --index;

    while(index < times.size()-2 && !tai.before(getTime(index+1)) ) ++index;


    this.index = index;

    /*******************************************
    * check if we are off the end of the table *
    *******************************************/
//     boolean off_left = index == 1 && tai.before(getTime(0));
//     boolean off_right = index == times.size() - 2 &&
//                         tai.after(getTime(times.size()-1));

//     System.out.println(tai);
//     System.out.println(getTime(0));
//     System.out.println("off_left="+off_left+" off_right="+off_right+
//     " index="+index+
//     " "+tai+" "+getTime(0));


//     System.out.println("index="+index+" nrows="+nrows);
//     System.out.println(mjd[index]+" "+x+" "+mjd[index+1]);

    /**************************************************
    * back the index one interval away from the edges *
    **************************************************/

    if(index == 0) ++index;
    if(index == times.size() - 2) --index;


    /************************
    * initialize everything *
    ************************/
    double time   = 0.0;
    double error  = 0.0;
    double x      = 0.0;
    double y      = 0.0;
    double x_err  = 0.0;
    double y_err  = 0.0;
    double dX     = 0.0;
    double dY     = 0.0;
    double dX_err = 0.0;
    double dY_err = 0.0;

    /******************************************
    * get the EOP at the index for reference *
    ******************************************/
    EOP eop0 = getEOP(index);

    /******************
    * loop over dates *
    ******************/
    for(int i=index-1; i<= index+2; ++i) {

        /***********************************************
        * calculate the weighting factor for this date *
        ***********************************************/
        double factor = 1.0;

//         if(     off_left  && i!= 0             ) factor = 0.0;
//         else if(off_right && i!= times.size()-1) factor = 0.0;
//         else {
            /******************
            * interpolate *
            **************/
            for(int j=index-1; j<=index+2; ++j) {

                if(i==j) continue;

                factor *=        tai.secondsAfter(getTime(j))/
                                getTime(i).secondsAfter(getTime(j));

            } // end of weighting factor loop
       // }

        /***********************************
        * get the EOP at the current point *
        ***********************************/
        EOP eop = getEOP(i);

        /*********************
        * increment the sums *
        *********************/
 //  System.out.println("time offset: "+eop.secondsAfter(eop0));
        time  += eop.secondsAfter(eop0)     * factor;

        PolarMotionParameters polar_motion = eop.getPolarMotionParameters();
        x += polar_motion.getX() * factor;
        y += polar_motion.getY() * factor;

        PrecessionCorrection precession = eop.getPrecessionCorrection();
        dX += precession.getXCorrection() * factor;
        dY += precession.getYCorrection() * factor;

        /***********************
        * propagate the errors *
        ***********************/
        double factor2 = factor*factor;

        error += eop.getTimeError() * eop.getTimeError() * factor2;

        x_err += polar_motion.getXError() *
                 polar_motion.getXError() * factor2;

        y_err += polar_motion.getYError() *
                 polar_motion.getYError() * factor2;

        dX_err += precession.getXCorrectionError() *
                  precession.getXCorrectionError() * factor2;

        dY_err += precession.getYCorrectionError() *
                  precession.getYCorrectionError() * factor2;

    } // end of loop over dates

    /**************************************
    * take the square roots of the errors *
    **************************************/
    error  = Math.sqrt(error);
    x_err  = Math.sqrt(x_err);
    y_err  = Math.sqrt(y_err);
    dX_err = Math.sqrt(dX_err);
    dY_err = Math.sqrt(dY_err);

//    System.out.println("error ="+error);

    /*****************************************
    * set the time to the reference time and
    * then offset
    *****************************************/
    ut1.setTime(eop0.getMilliseconds(), eop0.getNanoseconds());
    ut1.increment(time);

    /*************************************
    * set the polar motion an the errors *
    *************************************/
    ut1.setTime(ut1.getMilliseconds(), ut1.getNanoseconds(),
                error,
                new PolarMotionParameters(x,y,x_err, y_err),
                new PrecessionCorrection(dX, dY, dX_err, dY_err),
                null );


} // end of interpolate method

/**************************************************************************
*
**************************************************************************/
private void setFromPoint(PreciseDate tai, EOP ut1, EOP eop0, double dut1) {

    PreciseDate utc = UTC.convertDate(tai);
    ut1.setTime(utc.getMilliseconds(), utc.getNanoseconds());
    ut1.increment(dut1);

    ut1.setTime(ut1.getMilliseconds(), ut1.getNanoseconds(),
                eop0.getTimeError(), eop0.getPolarMotionParameters(),
                eop0.getPrecessionCorrection(),
                null);

} // end of setFromPoint method

/**************************************************************************
* Calculates the remainder after dividing a/b.
* This is needed by the FORTRAN-translated
* {@link #correct(EOP) }.
* Note that {@link Math#IEEEremainder(double)} can sometimes return a
* negative number.
* @param a the numberator
* @param b the denominator
* @return the remainder from a/b
**************************************************************************/
private static double mod(double a, double b) {

    double remainder = Math.IEEEremainder(a,b);
    if(a>0 && remainder<0) remainder += b;

    return remainder;

}


/***************************************************************************
* Determines the Earth orientation parameters which correspond
* to a given instant specified in TAI.
* These values are interpolated from this table using the prescription of
* <A href="http://maia.usno.navy.mil/iers-gaz13">IERS Gazette
* No 13, 30 January 1997</a>. The table is interpolated using a cubic
* polynomial, and then corrections are added to account for variations
* on < ~1 day timescales using a tidal model.
* The tidal model calculations have been translated to Java from FORTRAN,
* with minor changes which maintain agreement within ~1e-8 arcsec with
* the original code.
* <p>
* Errors are propagated from the tabulated value with the formula for
* a weighted sum:
*  a<sup<2> &sigma<sub>1</sub><sup>2</sup> +
*  b<sup<2> &sigma<sub>2</sub><sup>2</sup> + ...
* <p>
* Note that we interpolate the table in TAI, even though it is customary
* to report UT1 as an offset from UTC. This is because using TAI makes it
* easier to properly account for leap seconds.
* @param tai the TAI time at which to determine UT1
* @param ut1 The correspondiong UT1 time and polar motion parameters.
*        this method sets the values in this argument.
***************************************************************************/
public void getUT1(PreciseDate tai, EOP ut1) {


    /**********************************************
    * check that the UT1 date uses this EOP table *
    **********************************************/
    if(! ((UT1System)ut1.getTimeSystem()).getEOPTable().equals(this)) {
        throw new IllegalArgumentException(ut1+
                          " uses a different EOP table from "+UT1);
    }


    if(tai.before(getTime(0))) {
        /********************************
        * off the left end of the table *
        ********************************/
        setFromPoint(tai, ut1, getEOP(0), first_dut1);

    } else if(tai.after(getTime(times.size()-1))) {
        /*********************************
        * off the right end of the table *
        *********************************/
        setFromPoint(tai, ut1, getEOP(times.size()-1), first_dut1);

    } else {
        /**************
        * interpolate *
        **************/
        interpolate(TAI.convertDate(tai), ut1);
    }

    /********************************
    * calculate the tidal arguments *
    ********************************/
    PreciseDate tdb = TDB.createDate();
    tdb.setTime(tai);

    ut1.setTime(ut1.getMilliseconds(), ut1.getNanoseconds(),
                ut1.getTimeError(),
                ut1.getPolarMotionParameters(),
                ut1.getPrecessionCorrection(),
                new TidalArguments(tdb, ut1) );


    /**********************************
    * add short time scale corrections *
    ***********************************/
    if(correction != null) correction.correct(ut1);



} // end of getUT1 method




} // end of EOPTable class
