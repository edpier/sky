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

package eap.sky.time.barycenter;

import eap.sky.time.*;

/**************************************************************************
* A simple model of the solar system, which can compute TDB-TT to within
* tens of microseconds. The computation requires only two sine computations.
* The formula is taken from equation (2.222-1) of Seidelmann 1992,
* The Explanatory Supplement to the Astronomical Almanac.
**************************************************************************/
public class SimpleTDBModel extends TDBModel {

private static final SimpleTDBModel instance = new SimpleTDBModel();

/**************************************************************************
* Returns the only instance of this class. Use this method instead of a
* constructor.
**************************************************************************/
public static SimpleTDBModel getInstance() { return instance; }

/**************************************************************************
* Makes the constructor private.
**************************************************************************/
private SimpleTDBModel() {}

/**************************************************************************
*
**************************************************************************/
public double getTDBminusTT(PreciseDate tdb) {

    checkCompatibility(tdb);

    JulianDate jd = new JulianDate(tdb);

    double g = 357.53 + 0.9856003*((jd.getNumber() - 2451545)+jd.getFraction());

    g = Math.toRadians(g);

    return 0.001658 * Math.sin(    g) +
           0.000014 * Math.sin(2.0*g);

} // end of getTDBminusTT method

} // end of SimpleTDBModel class