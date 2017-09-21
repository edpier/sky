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
*
***************************************************************************/
public class SimpleNutation extends Nutation {

/***************************************************************************
* @param t Number of Julian centuries since the epoch
***************************************************************************/
public NutationValues compute(double t) {

    double t2 = t * t;
    double t3 = t2 * t;

    Angle mean_obliquity = Angle.createFromArcsec(84381.448
                                                   - 46.815   *t
                                                    - 0.00059 *t2
                                                    + 0.001813*t3);

    // Compute the arguments of the nutation series in radians
    double [] a = new double[5];

    a[0] = 2.3555483935439407 + t * (8328.691422883896
                              + t * (1.517951635553957e-4
                              + 3.1028075591010306e-7 * t));

    a[1] = 6.240035939326023 + t * (628.3019560241842
                             + t * (-2.7973749400020225e-6
                             - 5.817764173314431e-8 * t));

    a[2] = 1.6279019339719611 + t * (8433.466158318453
                              + t * (-6.427174970469119e-5
                              + 5.332950492204896e-8 * t));

    a[3] = 5.198469513579922 + t * (7771.377146170642
                             + t * (-3.340851076525812e-5
                             + 9.211459941081184e-8 * t));

    a[4] = 2.1824386243609943 + t * (-33.75704593375351
                              + t * (3.614285992671591e-5
                              + 3.878509448876288e-8 * t));

    double ap, dp, de;

    /*********
    * term 1 *
    *********/
    ap  = a[4];
    dp  = (-171996 - 174.2*t)*Math.sin(ap);
    de  = (92025   +   8.9*t)*Math.cos(ap);

    /*********
    * term 2 *
    *********/
    ap  = 2.0 * a[4];
    dp += (2062 + 0.2 * t) * Math.sin(ap);
    de += (-895 + 0.5 * t) * Math.cos(ap);

    /*********
    * term 3 *
    *********/
    ap  = -2.0 * a[0] + 2.0 * a[2] + a[4];
    dp += 46 * Math.sin(ap);
    de -= 24 * Math.cos(ap);

    /*********
    * term 4 *
    *********/
    ap  = 2.0 * a[0] - 2.0 * a[2];
    dp += 11 * Math.sin(ap);

    Angle psi     = Angle.createFromArcsec(dp*1e-4);
    Angle epsilon = Angle.createFromArcsec(de*1e-4);

    return new NutationValues(t, meanObliquity(t), epsilon, psi);

} // end of compute method

} // end of SimpleNutation class