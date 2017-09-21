// Copyright 2013 Edward Alan Pier
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

package eap.sky.ephemeris.sgp4;

import eap.sky.time.*;
import eap.sky.util.*;

/***************************************************************************
* This is the full IAU 1980 nutation model.
***************************************************************************/
public class Nutation1980 extends Nutation {

private static final double TWO_PI = 2.0 * Math.PI;
private static final double RADIANS_PER_ARC_SECOND = Math.PI / 648000;

// lunisolar nutation elements
// Coefficients for l (Mean Anomaly of the Moon).
private static final double F10  = Math.toRadians(134.96298139);
private static final double F110 =    715922.633 * RADIANS_PER_ARC_SECOND;
private static final double F111 =      1325.0;
private static final double F12  =        31.310 * RADIANS_PER_ARC_SECOND;
private static final double F13  =         0.064 * RADIANS_PER_ARC_SECOND;

// Coefficients for l' (Mean Anomaly of the Sun).
private static final double F20  = Math.toRadians(357.52772333);
private static final double F210 =   1292581.224 * RADIANS_PER_ARC_SECOND;
private static final double F211 =        99.0;
private static final double F22  =        -0.577 * RADIANS_PER_ARC_SECOND;
private static final double F23  =        -0.012 * RADIANS_PER_ARC_SECOND;

// Coefficients for F = L (Mean Longitude of the Moon) - Omega.
private static final double F30  = Math.toRadians(93.27191028);
private static final double F310 =    295263.137 * RADIANS_PER_ARC_SECOND;
private static final double F311 =      1342.0;
private static final double F32  =       -13.257 * RADIANS_PER_ARC_SECOND;
private static final double F33  =         0.011 * RADIANS_PER_ARC_SECOND;

// Coefficients for D (Mean Elongation of the Moon from the Sun).
private static final double F40  = Math.toRadians(297.85036306);
private static final double F410 =   1105601.328 * RADIANS_PER_ARC_SECOND;
private static final double F411 =      1236.0;
private static final double F42  =        -6.891 * RADIANS_PER_ARC_SECOND;
private static final double F43  =         0.019 * RADIANS_PER_ARC_SECOND;

// Coefficients for Omega (Mean Longitude of the Ascending Node of the Moon).
private static final double F50  = Math.toRadians(125.04452222);
private static final double F510 =   -482890.539 * RADIANS_PER_ARC_SECOND;
private static final double F511 =        -5.0;
private static final double F52  =         7.455 * RADIANS_PER_ARC_SECOND;
private static final double F53  =         0.008 * RADIANS_PER_ARC_SECOND;

/** coefficients of l, mean anomaly of the Moon. */
private static final int[] CL = {
    +0,  0, -2,  2, -2,  1,  0,  2,  0,  0,
    +0,  0,  0,  2,  0,  0,  0,  0,  0, -2,
    +0,  2,  0,  1,  2,  0,  0,  0, -1,  0,
    +0,  1,  0,  1,  1, -1,  0,  1, -1, -1,
    +1,  0,  2,  1,  2,  0, -1, -1,  1, -1,
    +1,  0,  0,  1,  1,  2,  0,  0,  1,  0,
    +1,  2,  0,  1,  0,  1,  1,  1, -1, -2,
    +3,  0,  1, -1,  2,  1,  3,  0, -1,  1,
    -2, -1,  2,  1,  1, -2, -1,  1,  2,  2,
    +1,  0,  3,  1,  0, -1,  0,  0,  0,  1,
    +0,  1,  1,  2,  0,  0
};

/** coefficients of l', mean anomaly of the Sun. */
private static final int[] CLP = {
    +0,  0,  0,  0,  0, -1, -2,  0,  0,  1,
    +1, -1,  0,  0,  0,  2,  1,  2, -1,  0,
    -1,  0,  1,  0,  1,  0,  1,  1,  0,  1,
    +0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
    +0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
    +1,  1, -1,  0,  0,  0,  0,  0,  0,  0,
    -1,  0,  1,  0,  0,  1,  0, -1, -1,  0,
    +0, -1,  1,  0,  0,  0,  0,  0,  0,  0,
    +0,  0,  0,  1,  0,  0,  0, -1,  0,  0,
    +0,  0,  0,  0,  1, -1,  0,  0,  1,  0,
    -1,  1,  0,  0,  0,  1
};

/** coefficients of F = L - &Omega, where L is the mean longitude of the Moon. */
private static final int[] CF = {
    0,  0,  2, -2,  2,  0,  2, -2,  2,  0,
    2,  2,  2,  0,  2,  0,  0,  2,  0,  0,
    2,  0,  2,  0,  0, -2, -2,  0,  0,  2,
    2,  0,  2,  2,  0,  2,  0,  0,  0,  2,
    2,  2,  0,  2,  2,  2,  2,  0,  0,  2,
    0,  2,  2,  2,  0,  2,  0,  2,  2,  0,
    0,  2,  0, -2,  0,  0,  2,  2,  2,  0,
    2,  2,  2,  2,  0,  0,  0,  2,  0,  0,
    2,  2,  0,  2,  2,  2,  4,  0,  2,  2,
    0,  4,  2,  2,  2,  0, -2,  2,  0, -2,
    2,  0, -2,  0,  2,  0
};

/** coefficients of D, mean elongation of the Moon from the Sun. */
private static final int[] CD = {
    +0,  0,  0,  0,  0, -1, -2,  0, -2,  0,
    -2, -2, -2, -2, -2,  0,  0, -2,  0,  2,
    -2, -2, -2, -1, -2,  2,  2,  0,  1, -2,
    +0,  0,  0,  0, -2,  0,  2,  0,  0,  2,
    +0,  2,  0, -2,  0,  0,  0,  2, -2,  2,
    -2,  0,  0,  2,  2, -2,  2,  2, -2, -2,
    +0,  0, -2,  0,  1,  0,  0,  0,  2,  0,
    +0,  2,  0, -2,  0,  0,  0,  1,  0, -4,
    +2,  4, -4, -2,  2,  4,  0, -2, -2,  2,
    +2, -2, -2, -2,  0,  2,  0, -1,  2, -2,
    +0, -2,  2,  2,  4,  1
};

/** coefficients of &Omega, mean longitude of the ascending node of the Moon. */
private static final int[] COM = {
    1, 2, 1, 0, 2, 0, 1, 1, 2, 0,
    2, 2, 1, 0, 0, 0, 1, 2, 1, 1,
    1, 1, 1, 0, 0, 1, 0, 2, 1, 0,
    2, 0, 1, 2, 0, 2, 0, 1, 1, 2,
    1, 2, 0, 2, 2, 0, 1, 1, 1, 1,
    0, 2, 2, 2, 0, 2, 1, 1, 1, 1,
    0, 1, 0, 0, 0, 0, 0, 2, 2, 1,
    2, 2, 2, 1, 1, 2, 0, 2, 2, 0,
    2, 2, 0, 2, 1, 2, 2, 0, 1, 2,
    1, 2, 2, 0, 1, 1, 1, 2, 0, 0,
    1, 1, 0, 0, 2, 0
};

/** coefficients for nutation in longitude, const part, in 0.1milliarcsec. */
private static final double[] SL = {
    -171996.0, 2062.0, 46.0,   11.0,  -3.0,  -3.0,  -2.0,   1.0,  -13187.0, 1426.0,
    -517.0,    217.0,  129.0,  48.0,  -22.0,  17.0, -15.0, -16.0, -12.0,   -6.0,
    -5.0,      4.0,    4.0,   -4.0,    1.0,   1.0,  -1.0,   1.0,   1.0,    -1.0,
    -2274.0,   712.0, -386.0, -301.0, -158.0, 123.0, 63.0,  63.0, -58.0,   -59.0,
    -51.0,    -38.0,   29.0,   29.0,  -31.0,  26.0,  21.0,  16.0, -13.0,   -10.0,
    -7.0,      7.0,   -7.0,   -8.0,    6.0,   6.0,  -6.0,  -7.0,   6.0,    -5.0,
    +5.0,     -5.0,   -4.0,    4.0,   -4.0,  -3.0,   3.0,  -3.0,  -3.0,    -2.0,
    -3.0,     -3.0,    2.0,   -2.0,    2.0,  -2.0,   2.0,   2.0,   1.0,    -1.0,
    +1.0,     -2.0,   -1.0,    1.0,   -1.0,  -1.0,   1.0,   1.0,   1.0,    -1.0,
    -1.0,      1.0,    1.0,   -1.0,    1.0,   1.0,  -1.0,  -1.0,  -1.0,    -1.0,
    -1.0,     -1.0,   -1.0,    1.0,   -1.0,   1.0
};


/** coefficients for nutation in longitude, t part, in 0.1milliarcsec. */
private static final double[] SLT = {
    -174.2,  0.2,  0.0, 0.0, 0.0,  0.0, 0.0, 0.0, -1.6, -3.4,
    +1.2,   -0.5,  0.1, 0.0, 0.0, -0.1, 0.0, 0.1,  0.0,  0.0,
    +0.0,    0.0,  0.0, 0.0, 0.0,  0.0, 0.0, 0.0,  0.0,  0.0,
    -0.2,    0.1, -0.4, 0.0, 0.0,  0.0, 0.0, 0.1, -0.1,  0.0,
    +0.0,    0.0,  0.0, 0.0, 0.0,  0.0, 0.0, 0.0,  0.0,  0.0,
    +0.0,    0.0,  0.0, 0.0, 0.0,  0.0, 0.0, 0.0,  0.0,  0.0,
    +0.0,    0.0,  0.0, 0.0, 0.0,  0.0, 0.0, 0.0,  0.0,  0.0,
    +0.0,    0.0,  0.0, 0.0, 0.0,  0.0, 0.0, 0.0,  0.0,  0.0,
    +0.0,    0.0,  0.0, 0.0, 0.0,  0.0, 0.0, 0.0,  0.0,  0.0,
    +0.0,    0.0,  0.0, 0.0, 0.0,  0.0, 0.0, 0.0,  0.0,  0.0,
    +0.0,    0.0,  0.0, 0.0, 0.0,  0.0
};

/** coefficients for nutation in obliquity, const part, in 0.1milliarcsec. */
private static final double[] CO = {
    +92025.0, -895.0, -24.0,  0.0,    1.0,   0.0,   1.0,   0.0,   5736.0, 54.0,
    +224.0,   -95.0,  -70.0,  1.0,    0.0,   0.0,   9.0,   7.0,   6.0,    3.0,
    +3.0,     -2.0,   -2.0,   0.0,    0.0,   0.0,   0.0,   0.0,   0.0,    0.0,
    +977.0,   -7.0,    200.0, 129.0, -1.0,  -53.0, -2.0,  -33.0,  32.0,   26.0,
    +27.0,     16.0,  -1.0,  -12.0,   13.0, -1.0,  -10.0, -8.0,   7.0,    5.0,
    +0.0,     -3.0,    3.0,   3.0,    0.0,  -3.0,   3.0,   3.0,  -3.0,    3.0,
    +0.0,      3.0,    0.0,   0.0,    0.0,   0.0,   0.0,   1.0,   1.0,    1.0,
    +1.0,      1.0,   -1.0,   1.0,   -1.0,   1.0,   0.0,  -1.0,  -1.0,    0.0,
    -1.0,      1.0,    0.0,  -1.0,    1.0,   1.0,   0.0,   0.0,  -1.0,    0.0,
    +0.0,      0.0,    0.0,   0.0,    0.0,   0.0,   0.0,   0.0,   0.0,    0.0,
    +0.0,      0.0,    0.0,   0.0,    0.0,   0.0
};

/** coefficients for nutation in obliquity, t part, in 0.1milliarcsec. */
private static final double[] COT = {
    +8.9,  0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -3.1, -0.1,
    -0.6,  0.3,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,
    +0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,
    -0.5,  0.0,  0.0, -0.1,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,
    +0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,
    +0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,
    +0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,
    +0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,
    +0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,
    +0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,
    +0.0,  0.0,  0.0,  0.0,  0.0,  0.0
};

/***************************************************************************
*
***************************************************************************/
public NutationValues compute(double tc) {

    // mean anomaly of the Moon
    double l  = ((F13*tc + F12)*tc + F110)*tc + F10 + ((F111*tc)%1.0) * TWO_PI;
    // mean anomaly of the Sun
    double lp = ((F23*tc + F22)*tc + F210)*tc + F20 + ((F211*tc)%1.0) * TWO_PI;
    // L - &Omega; where L is the mean longitude of the Moon
    double f  = ((F33*tc + F32)*tc + F310)*tc + F30 + ((F311*tc)%1.0) * TWO_PI;
    // mean elongation of the Moon from the Sun
    double d  = ((F43*tc + F42)*tc + F410)*tc + F40 + ((F411*tc)%1.0) * TWO_PI;
    // mean longitude of the ascending node of the Moon
    double om = ((F53*tc + F52)*tc + F510)*tc + F50 + ((F511*tc)%1.0) * TWO_PI;

    // Initialize nutation elements.
    double dpsi = 0.0;
    double deps = 0.0;

    // Sum the nutation terms from smallest to biggest.
    for (int j = CL.length-1; j>=0; j--) {
        // Set up current argument.
        double arg = CL[j]*l +
                    CLP[j]*lp +
                     CF[j]*f +
                     CD[j]*d +
                    COM[j]*om;

        // Accumulate current nutation term.
        double s = SL[j] + SLT[j]*tc;
        double c = CO[j] + COT[j]*tc;

        if (s != 0.0) dpsi += s*Math.sin(arg);
        if (c != 0.0) deps += c*Math.cos(arg);

      //  System.out.println("Nutation 1980: "+j+" dpsi="+dpsi);

    } // end of loop over terms
//     System.out.println("Mine: dpsi="+dpsi);
//     System.out.println("Nutation1980: dpsi="+
//             (Angle.createFromArcsec(dpsi*1.e-4).getRadians()-0.0*Math.PI)+
//    " deps="+(Angle.createFromArcsec(deps*1.e-4).getRadians()-2.0*Math.PI)
//    );

    return new NutationValues(tc, meanObliquity(tc),
                                  Angle.createFromArcsec(deps*1.e-4),
                                  Angle.createFromArcsec(dpsi*1.e-4) );

} // end of compute method


} // end of Nutation1980 class