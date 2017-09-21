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

import java.io.*;
import java.util.*;

/****************************************************************************
* A sum over harmonics of the fundamental arguments commonly used for precession
* and nutation.
* Specifically, this is the sum
* <blockquote>
* <font size=+3>&Sigma;</font><sub>i</sub> P<sub>i</sub> t<sup>i</sup> +
* <font size=+3>&Sigma;</font><sub>i</sub> t<sup>i</sup>
* <font size=+3>&Sigma;</font><sub>j</sub>
                   [S<sub>i,j</sub> sin &theta;<sub>i,j</sub> +
                    C<sub>i,j</sub> cos &theta;<sub>i,j</sub>  ],
* </blockquote>
* where
* &theta;<sub>i,j</sub> = <font size=+3>&Sigma;</font><sub>k</sub>
* A<sub>i,j,k</sub> f<sub>k</sub>, and f<sub>k</sub> are the fundamental
* tidal arguments as calculated by {@link TidalArguments}.
* Also, t is the number of Julian centuries since J2000 as calculated
* by {@link TidalArguments#getJulianCenturiesTDB()}.
* The coefiecients of the expansion, A<sub>i,j,k</sub>, S<sub>i,j</sub>,
* C<sub>i,j</sub>, and P<sub>i</sub> are all read from a file
* (See {@link #PrecessionExpansion(InputStream)}).
* @see IAU2000APrecession
****************************************************************************/
public class PrecessionExpansion extends TidalExpansion {

private double[] poly_coef;

private double[][] sin_weight;
private double[][] cos_weight;
private int[][][]  arg_weight;

int trig_orders;

/****************************************************************************
* Read a set of expansion coeficients from a file. The format is that used by the
* <A href="http://maia.usno.navy.mil">IERS rapid response service</A>
* in their machine-readable tables which accompany
* <A href="http://maia.usno.navy.mil/conv2003.html">IERS Technical Note 32</a>.
* Currently, the tables in this format for X, Y, and Z for the
* IAU 2000A precession model, are available from
* <A href="http://maia.usno.navy.mil/ch5tables.html">
*          http://maia.usno.navy.mil/ch5tables.html</A>.
* This method has not been tested with any other tables.*
* @param in The data source.
* @throws IOException if there is trouble reading the table.
****************************************************************************/
public PrecessionExpansion(InputStream in) throws IOException {

    /*************************************************
    * wrap a buffered reader around the input stream *
    *************************************************/
    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(in));

    /**********************************
    * loop over the lines in the file *
    **********************************/
    int bar_count = 0;
    int order=0;
    int row=0;
    int i_offset=1;
    boolean read_polynomial = false;
    String line;
    while((line = reader.readLine()) != null) {

        /**************************************
        * trim leading and trailing whitspace *
        **************************************/
        line = line.trim();

        /*******************
        * skip blank lines *
        *******************/
        if(line.length() == 0) continue;

        /**************************************
        * check if this line is a bar divider *
        **************************************/
        if(line.startsWith("------------------------------------------")) {
            ++bar_count;
            continue;
        }


        /****************************************************
        * check if we are at the line before the polynomial *
        ****************************************************/
        if(line.startsWith("Polynomial part") ) {
            read_polynomial = true;
            continue;
        }

        /********************************************************
        * check if it's time to read the polynomial coeficients *
        ********************************************************/
        if(read_polynomial) {
            StringTokenizer tokens = new StringTokenizer(line);

            /**************************
            * get the number of terms *
            **************************/
            int norders = (tokens.countTokens() -1)/3 +1;

            /********************************
            * read the first term specially *
            ********************************/
            poly_coef = new double[norders];
            poly_coef[0] = Double.parseDouble(tokens.nextToken());

            /*********************************
            * now read the rest of the terms *
            *********************************/
            for(int i=1; i<poly_coef.length; ++i) {

                String sign = tokens.nextToken();

                poly_coef[i] = Double.parseDouble(tokens.nextToken());
                if(sign.equals("-")) poly_coef[i] = - poly_coef[i];

                tokens.nextToken();
            }

            /***********************************************
            * now we can initialize the rest of the arrays *
            ***********************************************/
            arg_weight = new int[norders][][];
            sin_weight = new double[norders][];
            cos_weight = new double[norders][];

            /******************************************
            * reset the read flag so we don't try to
            * this again
            ******************************************/
            read_polynomial =false;

        } // end if reading the polynomial coeficients

        /**************************************
        * check if we are past the header yet *
        **************************************/
        if(bar_count < 5) continue;

        /*******************
        * skip blank lines *
        *******************/
        if(line.length() == 0) continue;

        /******************************************************
        * see if this line marks the beginning of a new order *
        ******************************************************/
        if(line.startsWith("j =")) {
   //   System.out.println(line);
            StringTokenizer tokens = new StringTokenizer(line);
            tokens.nextToken();
            tokens.nextToken();
            order = Integer.parseInt(tokens.nextToken());
            tokens.nextToken();
            tokens.nextToken();
            tokens.nextToken();
            tokens.nextToken();
            int nterms = Integer.parseInt(tokens.nextToken());

            /********************************************
            * the row number keeps counting up,
            * but we want the i index to reset to zero
            ********************************************/
            i_offset = row+1;

            /********************************************
            * allocate the arrays for the current order *
            ********************************************/
            sin_weight[order] = new double[nterms];
            cos_weight[order] = new double[nterms];
            arg_weight[order] = new int[nterms][14];

            continue;

        } // end if this is the start of a new order

        /*********************
        * read a normal line *
        *********************/
        StringTokenizer tokens = new StringTokenizer(line);

        /******************************************
        * first the row number, which we offset
        * to get the i index
        ******************************************/
        row = Integer.parseInt(tokens.nextToken());
        int i = row-i_offset;

        /***********************************
        * read the sine and cosine weights *
        ***********************************/
        sin_weight[order][i] = Double.parseDouble(tokens.nextToken());
        cos_weight[order][i] = Double.parseDouble(tokens.nextToken());

        /*****************************************************************
        * read the coeficients for the fundamental arguments of nutation *
        *****************************************************************/
        for(int k=0; k<14; ++k) {
             arg_weight[order][i][k] = Integer.parseInt(tokens.nextToken());
        }

    } // end of loop over lines in the file

    /*****************************************************************
    * the sum over trig functions may have fewer terms than
    * the polynomial. Since we don't know how many terms there are
    * until we are done reading them, we initialize the
    * trig arrays to the same dimension as the polynomial, and
    * then keep track of the actual number of trig terms
    *****************************************************************/
    trig_orders = order+1;

    //histogramArgWeights();

} // end of constructor


/**************************************************************************
* Evaluate the sum.
* @param args The fundamental arguments for which we calculate the sum.
**************************************************************************/
public double evaluate(TidalArguments args) {

    /***************************************************************
    * the expression for t is a polynomial expansion in t
    * The coeficient for each term is a sum of a constant term,
    * plus a weighted sum over the sines and cosines of
    * the fundamental arguments of precession
    **************************************************************/
    double[] sum = new double[poly_coef.length];

    /*****************************
    * loop over terms in the sum *
    *****************************/
    for(int order=0; order<sum.length; ++order) {

        /**************************************************
        * initialize the sum to the polynomial coeficient *
        **************************************************/
        sum[order] = poly_coef[order];

        /*******************************************
        * check if there are periodic terms to add *
        *******************************************/
        if(order >= trig_orders) continue;


        /********************************************************
        * calculate a weighted sum of the fundamental arguments *
        * for this order
        ********************************************************/
        for(int i=0; i< sin_weight[order].length; ++i) {

            /**********************************************************
            * calculate the weighted sum of the fundamental arguments *
            **********************************************************/
            double[] angle = args.weightedSum(arg_weight[order][i]);
            double sin_arg = angle[0];
            double cos_arg = angle[1];

            sum[order] += sin_weight[order][i] * sin_arg +
                          cos_weight[order][i] * cos_arg;

        } // end of loop over i

    } // end of loop over polynomial orders

    /******************************
    * now evaluate the polynomial *
    ******************************/
    double t = args.getJulianCenturiesTDB();
    double poly = 0.0;
    for(int i=sum.length-1; i>=0; --i) {

        poly = t*poly + sum[i];
    }

    /**************************************
    * rescale from arc seconds to radians *
    **************************************/

    return Math.toRadians(1e-6*poly/3600.0);


} // end of evaluate method

} // end of PrecessionExpansion class
