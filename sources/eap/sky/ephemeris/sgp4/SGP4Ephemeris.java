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

import eap.sky.earth.*;
import eap.sky.ephemeris.*;
import eap.sky.time.*;
import eap.sky.util.*;

/******************************************************************************
* An Ephemeris that uses the SGP4/SDP4 model.
* This is for Earth orbiting satellites whose orbital elements are given in
* NORAD standard Two Line Element (TLE) format.
*******************************************************************************/
public class SGP4Ephemeris extends Ephemeris {

TLE tle;
SGP4Propagator propagator;
Precession1980 precession;
Nutation nutation;
NutationTransform nut_trans;

public static int SATELLITE = -1;

/***************************************************************************
* Create a new Ephemeris.
* @param UT1 The UT1 system we will use to calculate the rotation of the
* Earth in order to apply certain corrections.
***************************************************************************/
public SGP4Ephemeris(UT1System UT1, TLE tle,
                     Precession1980 precession,
                     Nutation nutation,
                     NutationTransform nut_trans) throws OrbitDecayedException {
    super(UT1);

    this.tle = tle;
    this.propagator = new SGP4Propagator(tle);
    this.precession = precession;
    this.nutation   = nutation;
    this.nut_trans = nut_trans;

} // end of constructor

/***************************************************************************
* Return the position of the given body with respect to the
* system barycenter.
* @param body The body in question. If possible this must be one of the static
* variables of this class.
* @param tdb The time in TDB at which to calculate the position.
* @return the position in meters.
* @see TDBSystem
***************************************************************************/
public ThreeVector barycentricPosition(int body, PreciseDate tdb) {

    if(body == Ephemeris.EARTH) return ThreeVector.ZERO;
    if(body != SATELLITE) {
        throw new IllegalArgumentException("Body "+body+" not supported");
    }

   // System.out.println("computing position");

    PreciseDate time = TransformCache.makeCache(tdb);

    /******************************************************
    * the propagator takes time in units of UTC seconds
    * since the TLE epoch
    ******************************************************/
    PreciseDate utc = tle.getEpoch().getTimeSystem().convertDate(time);
    double minutes = utc.secondsAfter(tle.getEpoch())/60.0;

    /**********************************************************
    * propagate the orbit and pull out the position vector.
    * This position is in the flaky TEME coordinates, which
    * we have to convert to GCRS
    **********************************************************/
    try {
        MotionState state = propagator.propagate(minutes);
        ThreeVector position = state.getPosition();

        double t = precession.getJulianCenturies(time);
        nut_trans.set(nutation.compute(t));

        return precession.compute(t)
                         .transform(nut_trans.transform(position));

    } catch(OrbitDecayedException e) {
        /************************************************
        * repackage the exception because we don't know
        * what else to do.
        ************************************************/
        throw (IllegalStateException)
            (new IllegalStateException().initCause(e));
    }


} // end of barycentricPosition method


/**************************************************************************
* Return the velocity of the given body with respect to the
* system barycenter.
* @param body The body in question. If possible this must be one of the static
* variables of this class.
* @param tdb The time in TDB at which to calculate the position.
* @return the velocity in meters per second.
* @see TDBSystem
**************************************************************************/
public ThreeVector barycentricVelocity(int body, PreciseDate tdb) {

    if(body == Ephemeris.EARTH) return ThreeVector.ZERO;
    if(body != SATELLITE) {
        throw new IllegalArgumentException("Body "+body+" not supported");
    }

    return null;

} // end of barycentricVelocity method


} // end of SGP4Ephemeris class