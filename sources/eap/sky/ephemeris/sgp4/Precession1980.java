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

import java.text.*;

/***************************************************************************
*
***************************************************************************/
public class Precession1980 {

private static final double ZETA_1 = 2306.2181;
private static final double ZETA_2 =    0.30188;
private static final double ZETA_3 =    0.017998;

private static final double THETA_1 = 2004.3109;
private static final double THETA_2 =   -0.42665;
private static final double THETA_3 =   -0.041833;

private static final double Z_1 = 2306.2181;
private static final double Z_2 =    1.09468;
private static final double Z_3 =    0.018203;

TimeSystem system;
PreciseDate epoch;

/***************************************************************************
*
***************************************************************************/
public Precession1980(TimeSystem system) {

    this.system = system;

    try {
        /************************
        * create the epoch time *
        ************************/
        epoch = system.createFormat()
                      .parsePreciseDate("2000-01-01 12:00:00 "+
                                        system.getAbbreviation());

    } catch(ParseException e) {
        /***************************
        * this should never happen *
        ***************************/
        throw (IllegalStateException)
            (new IllegalStateException().initCause(e));
    }

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public double getJulianCenturies(PreciseDate time) {

    return system.convertDate(time)
                 .secondsAfter(epoch)/(36525.0*86400.0);

} // end of getJulianCenturies method

/***************************************************************************
*
***************************************************************************/
public Rotation compute(double t) {

    /********************
    * precession angles *
    ********************/
    Angle zeta  = Angle.createFromArcsec((( ZETA_3*t +  ZETA_2)*t +  ZETA_1)*t);
    Angle theta = Angle.createFromArcsec(((THETA_3*t + THETA_2)*t + THETA_1)*t);
    Angle z     = Angle.createFromArcsec(((    Z_3*t +     Z_2)*t +     Z_1)*t);

    /***********************************************
    * construct the rotation to GCRS coordinates *
    ***********************************************/
    Rotation rot1 = new Rotation(z.negative(),    Direction.Z_AXIS);
    Rotation rot2 = new Rotation(theta,           Direction.Y_AXIS);
    Rotation rot3 = new Rotation(zeta.negative(), Direction.Z_AXIS);

    Rotation rot = (Rotation)(rot3.combineWith(rot2).combineWith(rot1));
    return (Rotation)rot.invert();

} // end of getPrecession method

} // end of Precession1980 class