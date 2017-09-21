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

/************************************************************************
* The JPL DE405 Chebychev coeficients for a particular time interval.
* @see JPLFile
* @see JPLDE405Ephemeris
************************************************************************/
public class JPLInterval implements Serializable {

public static final int NUTATION  = 11;
public static final int LIBRATION = 12;

/***********************************************************************
* For each planet (the Moon makes 10, and the Sun makes 11), each
* interval contains several complete sets of coefficients, each
* covering a
* fraction of the interval duration
************************************************************************/
private static final int[] NSUBINTERVALS= {4, 2, 2, 1, 1, 1, 1, 1, 1, 8, 2,
                                           4, 4};

/*****************************************************************
* Each planet (the Moon makes 10, and the Sun makes 11) has
* a different
* number of Chebyshev coefficients used to calculate each component of
* position and velocity.
********************************************************************/
private static final int[] NCOEFS = {14, 10, 13, 11,  8, 7,
                                    6,  6,  6, 13, 11, 4, 4};

double start;
double end;
double duration;

double[][][][] coef;



/*************************************************************************
*
*************************************************************************/
private static StringTokenizer readMore(BufferedReader reader)
               throws IOException {

    /*********************
    * read the next line *
    *********************/
    String line = reader.readLine();

    if(line == null) throw new EOFException();
//System.out.println(line);
    /*******************************************
    * The standard ASCII files use the 'D' as
    * the exponent indicator, since they were
    * written with FORTRAN. Java doesn't understand
    * this, so we change it to 'e'
    **************************************************/
    line = line.replace('D', 'e');

    /***********
    * tokenize *
    ***********/
    StringTokenizer tokens = new StringTokenizer(line);

    return tokens;

} // end of getMoreTokens method

/*************************************************************************
* Read the coeficients.
* @param reader The data source
* @throws IOException if there is trouble reading.
*************************************************************************/
public void read(BufferedReader reader) throws IOException {

    /*********************************************************
    * The first line contains the interval number and length *
    *********************************************************/
    String line = reader.readLine();
//System.out.println(line);

    /**************************************
    * read the start and end Julian Dates *
    **************************************/
    StringTokenizer tokens = readMore(reader);

    start = Double.parseDouble(tokens.nextToken());
    end   = Double.parseDouble(tokens.nextToken());
    duration = end-start;


    /********************
    * loop over planets *
    ********************/
    coef = new double[NSUBINTERVALS.length][][][];
    for(int planet=0; planet < NSUBINTERVALS.length; ++planet) {

        /******************************************************
        * get the time and spacial resolution for this planet
        * we need these to know how to dimension the array
        ******************************************************/
        int nsub  = NSUBINTERVALS[planet];
        int ncoef =        NCOEFS[planet];

        coef[planet] = new double[nsub][][];

        /*************************
        * loop over subintervals *
        *************************/
        for(int sub=0; sub<nsub; ++sub) {

            coef[planet][sub] = new double[3][];

            /*****************
            * loop over axes *
            *****************/
            int naxes = 3;
            if(planet == NUTATION) naxes=2;
            for(int axis = 0; axis < naxes; ++axis) {

                coef[planet][sub][axis] = new double[ncoef];

                for(int index=0; index<ncoef; ++index) {
                    /****************************************
                    * make sure we have some tokens to read *
                    ****************************************/
                    if(!tokens.hasMoreTokens()) tokens = readMore(reader);

                    /***************************
                    * read the next coeficient *
                    ***************************/
                    coef[planet][sub][axis][index] =
                                   Double.parseDouble(tokens.nextToken());

                } // end of loop over coeficients
            }  // end of loop over axes
        } // end of loop over subintervals
    } // end of loop over planets

    /*****************************
    * skip lines 275 through 341 *
    *****************************/
  //  for (int i=275;i<=341;i++) {
  //      line = reader.readLine();
//
    //   System.out.println("i="+i+" line="+line);
  //  }

    for (int i=302;i<=341;i++) {
        line = reader.readLine();
//
    //   System.out.println("i="+i+" line="+line);
    }
//System.exit(1);


} // end of read method

/***********************************************************************
* Returns the Julian date of the start of the interval as a double.
* The Julain date is expressed in TDB.
* @return The start of the interval.
***********************************************************************/
public double getStartJD() { return start; }

/***********************************************************************
* Returns the length of the interval in Julian days.
* @return the length of the interval.
***********************************************************************/
public double getDuration() { return duration; }

/***********************************************************************
* Test if the interval contains a date.
* @param jd A Julian date in TDB.
***********************************************************************/
public boolean contains(JulianDate jd) {

    double date = jd.getJulianDate();

    return date>= start && date < end;

} // end of contains method


/***********************************************************************
* Compute the Chebychev polynomial terms.
* @param The value at which to evaluate the polynomial
* @param n The number of terms.
* @return an array of polynomial values.
***********************************************************************/
private double[] computeChebyshev(double x, int n) {

    double[] poly = new double[n];
    poly[0] = 1;
    poly[1] = x;
    for (int j=2;j<poly.length;j++) {
        poly[j] = 2*x* poly[j-1] - poly[j-2];
    }

    return poly;

} // end of computeChebyshev method

/***********************************************************************
* Compute the derivatives of the Chebychev polynomial terms.
* @param The value at which to evaluate the polynomial
* @param n The number of terms.
* @return an array of polynomial derivatives.
***********************************************************************/
private double[] computeChebyshevDerivative(double x, int n) {

    double[] poly = computeChebyshev(x,n);

    double[] deriv = new double[n];

    deriv[0] = 0.0;
    deriv[1] = 1.0;
    deriv[2] = 4.0*x;
    for (int j=3;j<n;j++) {
        deriv[j] = 2*x*deriv[j-1] + 2*poly[j-1] - deriv[j-2];
    }

    return deriv;

} // end of computeChebyshev method

/***********************************************************************
* Calculate either the position or the velocity.
* @param body One of the static variables in {@link Ephemeris}.
* @param jd A TDB Julian data in this interval.
* @param velocity If true calculate the velocity, otherwise calculate the
* position.
***********************************************************************/
public ThreeVector evaluate(int body, JulianDate jd, boolean velocity) {

    /*******************************
    * find the correct subinterval *
    *******************************/
    double hat = ((jd.getNumber() - start) + jd.getFraction())/duration
                 * NSUBINTERVALS[body];

    int sub = (int)Math.floor(hat);

    /***********************************************************
    * the chebyshev polynomials range between -1 and 1,
    * so we have to normalize our position in the subinterval
    * to that range
    ***********************************************************/
    double x = 2.0*(hat-sub)-1.0;

    /***********************************************
    * Calculate the Chebyshev position polynomials
    * using a recursion relation
    ***********************************************/
    double[] poly;
    if(velocity) poly = computeChebyshevDerivative(x, NCOEFS[body]);
    else         poly = computeChebyshev(          x, NCOEFS[body]);

    /*********************************************
    * compute the sum over chebyshev polynomials *
    *********************************************/
    double[][] c = coef[body][sub];

    double[] vector = new double[3];
    for (int j=0;j<3;j++) {

        for (int k=0; k<poly.length; k++) {
            vector[j] += c[j][k]*poly[k];

         //   System.out.println("body="+body+" j="+j+" k="+k+" c="+c[j][k]);
        }

    }

   // System.exit(0);

    /********************************************
    * there's an additional factor for velocity *
    ********************************************/
    if(velocity) {
        double factor = 2.0*NSUBINTERVALS[body]/(duration*86400.0);

        for(int i=0; i< 3; ++i) vector[i] *= factor;
    }


    return new ThreeVector(vector[0], vector[1], vector[2]).times(1e3);

} // end of position method

} // end of JPLCoeficients class
