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

package eap.sky.util;

/****************************************************************************
*
****************************************************************************/
public class LongitudeLine implements SphereCurve {


double longitude;
double latitude0;

double range;

/****************************************************************************
*
****************************************************************************/
public LongitudeLine(double longitude, double latitude0, double latitude1) {

    this.longitude = longitude;
    this.latitude0 = latitude0;

    range = latitude1 - latitude0;


} // end of constructor

/****************************************************************************
*
****************************************************************************/
public Direction direction(double t) {

    double latitude = latitude0 + range*t;

    return new Direction(longitude, latitude);

} // end of direction method

} // end of LongitudeCurve class