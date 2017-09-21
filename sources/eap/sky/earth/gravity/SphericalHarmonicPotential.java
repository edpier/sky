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

package eap.sky.earth.gravity;

import eap.sky.util.*;

/***************************************************************************
* A gravitational potential which is represented by a spherical harmonic
* expansion. Spherical harmonics are a set of orthogonal functions which
* are solutions to Laplace's Equation. Therefore the gravitational potential
* in a mass-free region can be described exactly by an infinite expansion
* of spherical harmonics.
* <p>
* Specifically, this class calculates the sum
* <blockquote>
* GM/r {1 + <font size="+2">&Sigma;</font><sub>n=2</sub> (a/r)<sup>n</sup>
*            <font size="+2">&Sigma;</font><sup>n</sup><sub>m=0</sub>
*            P<sub>n</sub><sup>m</sup>(cos &theta;)
*            [ C<sub>n,m</sub> cos(m&phi;) + S<sub>n,m</sub> sin(m&phi;)]},
* </blockquote>
* where G is the gravitational constant,
* M is the mass of the Earth,
* a is the equatorial radius of the Earth,
* P<sub>n</sub><sup>m</sup> are the Associated Legendre Polynomials,
* r, &theta; and &phi; are the usual spherical coordinates,
* and C<sub>n,m</sub>, and S<sub>n,m</sub> are the coeficients of the expansion.
***************************************************************************/
public abstract class SphericalHarmonicPotential extends Potential {

private int max;

/** Cosine coeficients. C[n][m] = C<sub>n,m</sub> **/
protected double[][] C;

/** Sine coeficients S[n][m] = S<sub>n,m</sub> **/
protected double[][] S;

/** The equatorial radius of the Earth in meters. **/
protected double a;

/** The mass of the Earth times the gravitational constant in MKS units. **/
protected double GM;


/*************************************************************************
* Create a new expansion.
* @param max the maximum order of the expansion.
*************************************************************************/
protected SphericalHarmonicPotential(int max) {

    this.max = max;

    /*************************************************
    * allocate the storage for the coeficient arrays *
    *************************************************/
    C = new double[max+1][];
    S = new double[max+1][];
    
    for(int i=0; i<=max; ++i) {
    
        C[i] = new double[i+1];
        S[i] = new double[i+1];

    } // end of loop over n
    
}


/****************************************************************************
*
****************************************************************************/
public double potential(Direction geocentric, double r) {

    /*********************************************
    * precalculate (a/r)^n because we
    * have to loop over n fastest to take
    * advantage of the recurrence relationships
    * for the associated Legendre polynomials
    *********************************************/
    double[] ar = new double[C.length];
    ar[0] = 1.0;
    ar[1] = a/r;
    for(int i=2; i< ar.length; ++i) {
        ar[i] = ar[i-1] * ar[1];
    }


    /*******************************************************
    * The Associated Legendre Polynomials are in terms of 
    * cos(theta), so calculate that now
    *******************************************************/
    double x = geocentric.getZ();

    /***********************************************************
    * initialize the Associated Legendre Polynomial recurrence *
    ***********************************************************/
    double Pmm=0.5/Math.sqrt(Math.PI);
    Pmm=1.0;
    double root = Math.sqrt((1.0+x)*(1.0-x));

    /******************************************
    * initialize the recurrence for the sine
    * and cosine of m*phi
    ******************************************/
    double radians = Math.toRadians(geocentric.getLongitude());
    double sin_phi = Math.sin(radians);
    double cos_phi = Math.cos(radians);

    double sin = 0.0;
    double cos = 1.0;

    double max_p=0.0;

    /***********************************************************
    * Sum over the spherical harmonics.
    * Normally this sum is written to cycle over m fastest.
    * However, we have swapped the order of the sums in order
    * to take advantage of recurrence relationships for the
    * Associated Legendre Polynomials.
    ***********************************************************/
    double sum = 0.0;
    for(int m=0; m<= max; ++m) {
    
        /*************************************************
        * 2m crops up a lot in the normalization factors *
        *************************************************/
        double m2 = 2.0*m;
    
  //  System.out.println("m="+m+" Pmm="+Pmm+" root="+root);
  //System.out.println("m="+m+" sin="+sin+" cos="+cos);

        /*******************************************************
        * the recurrence relationship requires the
        * previous two n's. So we need to apply a different
        * recurrence relationship to get the next n
        *******************************************************/
        double Pmn2 = Pmm;
        double Pmn1 = x*Math.sqrt(m2+3.0)*Pmm;

        /**************************************************************
        * now loop over n
        * like I said above, this is backwards from the normal order
        * Note m <= n, so n <= m <= max.
        **************************************************************/
        for(int n=m; n<=max; ++n) {
            /****************************************************
            * calculate the next Associated Legendre polynomial *
            ****************************************************/
            double P;
            if(     n==m  ) P = Pmn2;
            else if(n==m+1) P = Pmn1;
            else {
                /********************************************************
                * apply the recurrence relationship
                * and then update the previous two.
                * the previous ones don't enter into this
                * term of the sum, so we can safely increment them now
                ********************************************************/
                double n2 = 2.0+n;
                P = x*Math.sqrt( (n2+1)*(n2-1)/((n+m)*(n-m)) ) * Pmn1
                    - Math.sqrt( (n2+1)*(n-m-1)*(n+m-1)/
                                ((n2-3)*(n-m  )*(n+m  )) ) * Pmn2;

   if(Math.abs(P) > max_p) max_p = Math.abs(P);

                Pmn2 = Pmn1;
                Pmn1 = P;
            }
            
            /*************************************
            * calculate the next term in the sum *
            *************************************/
            sum += ar[n] * P * (C[n][m] * cos + S[n][m] * sin);

      //  System.out.println("m="+m+" n="+n+" P="+P+" sum="+sum);

        } // end of loop over n

        /********************************************************
        * increment the diagonal Associated Legendre Polynomial *
        * Note that we multiply by a normalization factor in 
        * order to keep these from blowing up.
        ********************************************************/
        Pmm *= -root * Math.sqrt((m2+3.0)/(m2+2.0));

        /*****************************************
        * increment the sine and cosine of m*phi *
        *****************************************/
        double new_sin = sin*cos_phi + cos*sin_phi;
        double new_cos = cos*cos_phi - sin*sin_phi;
        
        sin = new_sin;
        cos = new_cos;

    } // end of loop over m


//     System.out.println("sum="+sum+" GM/r = "+GM/r);
// 
//     System.out.println("max_p="+max_p);
    
//     System.out.println("sum="+sum);
//

// double should_be =(62427443.21865819 *r/GM -1.0);
// 
// System.out.println("sum should be "+should_be);
// double ratio =should_be/sum;
// System.out.println("ratio="+ratio+" ratio2="+(ratio*ratio));
     return GM/r * (1.0 + sum);

} // end of potential method



} // end of Geoid class
