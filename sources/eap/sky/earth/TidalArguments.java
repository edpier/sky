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

import eap.sky.time.*;
import eap.sky.time.barycenter.*;


/*****************************************************************************
* Represents a collection of the fundamental arguments of lunisolar and
* planetary nutation.
* These represent the frequencies of the torques which Solar system bodies
* exert on the Earth. Insofar as the Earth responds linearly to these
* torques, its response will be a linear combination of various harmonics
* of these frequencies with some set of amplitudes and phase offsets.
* <p>
* So these arguments are used to calculate precession/nutation models
* (e.g. {@link IAU2000APrecession})
* as well as diurnal and semi-diurnal corrections to tabulated values of the
* Earth's rotation and polar motion (see {@link EOPCorrection}).
* <p>
* The expressions for these are taken from
* <a href="">IERS Technical Note 32</a>, sections 5.8.2 and 5.8.3.
* Note that unlike this reference, we use the IAU 2003 expression for GMST
* to calculate {@link #ARG_chi}. This argument is only used for the
* corrections to the Earth Orientation Parameters (see {@link EOPCorrection}).
* The change accounts for only a few nanosecond difference in UT1.
* <p>

*****************************************************************************/
public class TidalArguments {

/** Mean anomaly of the Moon **/
public static final int ARG_l = 0;

/** Mean anomaly of the Sun **/
public static final int ARG_lprime = 1;

/** L-Omega. Where L = the mean longitude of the Moon. **/
public static final int ARG_F = 2;

/** Mean elongation of the Moon from the Sun **/
public static final int ARG_D = 3;

/** Mean Longitude of the Ascending Node of the Moon **/
public static final int ARG_Omega = 4;

/** Mean longitude of Mercury **/
public static final int ARG_Mercury = 5;

/** Mean longitude of Venus **/
public static final int ARG_Venus = 6;

/** Mean longitude of Earth **/
public static final int ARG_Earth = 7;

/** Mean longitude of Mars **/
public static final int ARG_Mars = 8;

/** Mean longitude of Jupiter **/
public static final int ARG_Jupiter = 9;

/** Mean longitude of Saturn **/
public static final int ARG_Saturn = 10;

/** Mean longitude of Uranus **/
public static final int ARG_Uranus = 11;

/** Mean longitude of Neptune **/
public static final int ARG_Neptune = 12;

/** General accumulated precession in longitude **/
public static final int ARG_pa = 13;

/** GMST + 180 degrees **/
public static final int ARG_chi = 14;

private static final double ARCSEC_PER_DEGREE = 3600.0;
private static final double ARCSEC_PER_CIRCLE = 360.0 * ARCSEC_PER_DEGREE;
private static final double PI2 = 2.0 * Math.PI;

double t;
EOP ut1;

double[] arg;
double[] sin;
double[] cos;
double[] sin2;
double[] cos2;

/***************************************************************************
* Calculate a set of tidal arguments for a given time.
* @param tdb The time in TDB. Strictly speaking this should be TDB, but
*        the IERS and SOFA codes both use TT.
* @param ut1 The corresponding UT1 time. Note this can be null if you
* do not need {@link #ARG_chi}, since that is the only argument which
* requires this.
* @throws IllegalArgumentException if tdb is not an instance of
* {@link TDBSystem}
***************************************************************************/
public TidalArguments(PreciseDate tdb, EOP ut1) {

    this.ut1 = ut1;

    /******************************
    * make sure the date is in TT *
    ******************************/
    if(!(tdb.getTimeSystem() instanceof TDBSystem)) {
        throw new IllegalArgumentException(tdb+" is not in TDB");
    }

    /*******************************************
    * calculate Julian centuries since J2000.0 *
    *******************************************/
    JulianDate jd = new JulianDate(tdb);
    t = ((jd.getNumber() - 2451545) + jd.getFraction())/36525.0;

} // end of constructor

/*****************************************************************************
*
*****************************************************************************/
private void calculate() {

    if(arg != null) return;

// System.out.println("calculating tidal arguments");
// try {  throw new IllegalStateException(); }
// catch(Exception e) { e.printStackTrace(); }

    /**********************************************************
    * calculate the fundamental arguments.
    * This code is taken from the IERS implementation and
    * is slightly different from the SOFA version.
    * The IERS code occaisionally includes an extra decimal
    * place of precision in the coeficients, and it splits
    * up the linear terms
    * so that we don't overflow the precision of a double
    * before we do the mod.
    *********************************************************/
    if(ut1 == null) arg = new double[14];
    else            arg = new double[15];

    /***************************
    * Mean anomaly of the Moon *
    ***************************/
    arg[0]  = Math.IEEEremainder(         485868.249036 +
                 Math.IEEEremainder(1325.0*t, 1.0)*ARCSEC_PER_CIRCLE +
                 t*( 715923.2178 +
                 t*(         31.8792 +
                 t*(          0.051635 +
                 t*(        - 0.00024470 )))), ARCSEC_PER_CIRCLE );

    /***************************
    *  Mean anomaly of the Sun *
    ***************************/
    arg[1] = Math.IEEEremainder(        1287104.793048 +
                 Math.IEEEremainder(99.0*t, 1.0)*ARCSEC_PER_CIRCLE +
                 t*(    1292581.0481 +
                 t*(        - 0.5532 +
                 t*(          0.000136 +
                 t*(        - 0.00001149 )))), ARCSEC_PER_CIRCLE );


    /*********************************************
    *  Mean argument of the latitude of the Moon
    * a.k.a. Mean Longitude of the Moon minus Mean Longitude
    * of the Ascending Node of the Moon.
    *********************************************/
    arg[2]   = Math.IEEEremainder(         335779.526232 +
                 Math.IEEEremainder(1342.0*t, 1.0)*ARCSEC_PER_CIRCLE +
                 t*( 295262.8478 +
                 t*(       - 12.7512 +
                 t*(       -  0.001037 +
                 t*(          0.00000417 )))), ARCSEC_PER_CIRCLE );


    /********************************************
    *  Mean elongation of the Moon from the Sun *
    ********************************************/
    arg[3]   = Math.IEEEremainder(        1072260.703692 +
                 Math.IEEEremainder(1236.0*t, 1.0)*ARCSEC_PER_CIRCLE+
                 t*( 1105601.2090 +
                 t*(        - 6.3706 +
                 t*(          0.006593 +
                 t*(        - 0.00003169 )))), ARCSEC_PER_CIRCLE );

    /***************************************************
    * Mean longitude of the ascending node of the Moon *
    ***************************************************/
    arg[4]  = Math.IEEEremainder(         450160.398036 +
                 Math.IEEEremainder(-5.0*t, 1.0)*ARCSEC_PER_CIRCLE +
                 t*(  - 482890.5431 +
                 t*(          7.4722 +
                 t*(          0.007702 +
                 t*(        - 0.00005939 )))), ARCSEC_PER_CIRCLE );

    /*************************************************************
    * the above expressions are in arc seconds, so we need to
    * convert to radians
    ************************************************************/
    for(int i=0; i<5; ++i) {
        arg[i] = Math.toRadians(arg[i]/ARCSEC_PER_DEGREE);
    }

    /**********************************************************************
    * Planetary longitudes, Mercury through Neptune (Souchay et al. 1999) *
    **********************************************************************/
    arg[5]  = Math.IEEEremainder(4.402608842 + 2608.7903141574 * t, PI2);
    arg[6]  = Math.IEEEremainder(3.176146697 + 1021.3285546211 * t, PI2);
    arg[7]  = Math.IEEEremainder(1.753470314 +  628.3075849991 * t, PI2);
    arg[8]  = Math.IEEEremainder(6.203480913 +  334.0612426700 * t, PI2);
    arg[9]  = Math.IEEEremainder(0.599546497 +   52.9690962641 * t, PI2);
    arg[10] = Math.IEEEremainder(0.874016757 +   21.3299104960 * t, PI2);
    arg[11] = Math.IEEEremainder(5.481293871 +    7.4781598567 * t, PI2);
    arg[12] = Math.IEEEremainder(5.321159000 +    3.8127774000 * t, PI2);

    /**********************************************
    * General accumulated precession in longitude *
    **********************************************/
    arg[13]  = (0.02438175 + 0.00000538691 * t) * t;

    /*********************************************************
    * GMST + 180 degrees (I think). This is mostly used for
    * polar motion. We only calculate this if we have a
    * non-null UT1 time.
    *********************************************************/
    if(ut1 != null) {

        /*****************************
        * make sure it really is UT1 *
        *****************************/
        if(!(ut1.getTimeSystem() instanceof UT1System )) {
            throw new IllegalArgumentException(ut1+" is not in UT1");
        }

        /***************************
        * the Earth rotation angle *
        ***************************/
        double era = ut1.earthRotationAngle();

        arg[14] = era + (    0.014506   +
                        ( 4612.15739966 +
                        (    1.39667721 +
                        (   -0.00009344 +
                        (    0.00001882 )
                                     * t ) * t ) * t ) * t )/(360.0*3600.0);

        arg[14] = PI2 * Math.IEEEremainder(arg[14]+0.5,1.0);

    } // end if we have a non-null UT1 time.


    /***************************************************
    * calculate the sines and cosines of the arguments *
    ***************************************************/
    sin  = new double[arg.length];
    cos  = new double[arg.length];
    sin2 = new double[arg.length];
    cos2 = new double[arg.length];

    for(int i=0; i< arg.length; ++i) {
        sin[i] = Math.sin(arg[i]);
        cos[i] = Math.cos(arg[i]);

        sin2[i] = 2.0*sin[i]*cos[i];
        cos2[i] = 2.0*cos[i]*cos[i] - 1.0;
    }

} // end of FundamentalArguments class

/***************************************************************************
* Returns the value of a particular argument.
* @param index One of the static variables of this class.
* @return An argument.
***************************************************************************/
public double getArgument(int index) {

    calculate();
    return arg[index];

} // end of getArgument method


/***************************************************************************
* Returns the number fo Julian centuries since J2000. A Julian century
* is 36525 julain days long. J2000 is the Julian date 2451545 TDB.
* This value is used to calculate the argument, and can be useful in
* expansions based on them.
* @return Julian centuries since J2000.
***************************************************************************/
public double getJulianCenturiesTDB() { return t; }



/***************************************************************************
* Calculates the sine and cosine of a harmonic of the fudamental arguments.
* @param weights an array of integer weights for the fundamental
*        arguments. The order of the weights must be the same as the
*        internal order of the arguments, as specified by this class's static
*        constants. The dimension of this array may be less than the number
*        of arguments.
* @return a new array containing the sin and cosine of the sum in that order.
* Note At some point we should modify this to return an
* {@link eap.sky.util.Angle}.
***************************************************************************/
public double[] weightedSum(int[] weights) {

    calculate();

    /***********************************************************
    * since the weights are all integers, we can use angle
    * addition formulas instead of summing the angles and
    * taking the trig functions of the results.
    * we can get away with this because nearly all the weights are
    * between -2 and 2.
    **********************************************************/
    double sin_sum = 0.0;
    double cos_sum = 1.0;

    /**************************************
    * loop over all the weights/arguments *
    **************************************/
    for(int i=0; i< weights.length; ++i) {
        int weight = weights[i];

        /********************
        * skip zero weights *
        ********************/
        if(weight == 0 ) continue;

        /************************************************************
        * for positive weights we use the angle addition formulas
        * and for negative weights we use the subtraction formulas
        ************************************************************/
        if(weight > 0 ) {
            /******************
            * positive weight *
            ******************/
            while(weight >0) {
                if(weight==1) {
                    /**********
                    * add one *
                    **********/
                    double new_sin = sin_sum*cos[i] + cos_sum*sin[i];
                    double new_cos = cos_sum*cos[i] - sin_sum*sin[i];

                    sin_sum = new_sin;
                    cos_sum = new_cos;

                    --weight;
                } else {
                    /**********
                    * add two *
                    **********/
                    double new_sin = sin_sum*cos2[i] + cos_sum*sin2[i];
                    double new_cos = cos_sum*cos2[i] - sin_sum*sin2[i];

                    sin_sum = new_sin;
                    cos_sum = new_cos;

                    weight-=2;
                }
            }

        } else {
            /******************
            * negative weight *
            ******************/
            weight=-weight;
            while(weight >0) {
                if(weight==1) {
                    /**********
                    * add one *
                    **********/
                    double new_sin = sin_sum*cos[i] - cos_sum*sin[i];
                    double new_cos = cos_sum*cos[i] + sin_sum*sin[i];

                    sin_sum = new_sin;
                    cos_sum = new_cos;

                    --weight;
                } else {
                    /**********
                    * add two *
                    **********/
                    double new_sin = sin_sum*cos2[i] - cos_sum*sin2[i];
                    double new_cos = cos_sum*cos2[i] + sin_sum*sin2[i];

                    sin_sum = new_sin;
                    cos_sum = new_cos;

                    weight-=2;
                }

            } // end of loop over weights

        } // end if the weight is negative

    } // end of loop over arguments

    double[] array = {sin_sum, cos_sum};

    return array;


} // end of weightedSum method


} // end of FundamentalArguments class
