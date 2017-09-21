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
public abstract class Nutation {

// Coefficients for the Mean Obliquity of the Ecliptic.
private static final double MOE_0 = 84381.448;
private static final double MOE_1 =   -46.8150;
private static final double MOE_2 =    -0.00059;
private static final double MOE_3 =     0.001813;

/***************************************************************************
* @param t Number of Julian centuries since the epoch
***************************************************************************/
public abstract NutationValues compute(double t);

/***************************************************************************
*
***************************************************************************/
public Angle meanObliquity(double t) {

    return Angle.createFromArcsec(((MOE_3*t + MOE_2)*t + MOE_1)*t + MOE_0);

} // end of meanObliquity method

} // end of Nutation class