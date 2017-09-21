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

package eap.sky.time.cycles;

import eap.sky.time.*;
import eap.sky.time.barycenter.*;
import eap.sky.time.clock.*;
import eap.sky.util.*;
import eap.sky.earth.*;
import eap.sky.ephemeris.*;

/***************************************************************************
*
***************************************************************************/
public class PhaseCalculator {

public static final double FULL = 1.0;
public static final double NEW = -1.0;

Observatory obs;
Ephemeris ephemeris;
TDBSystem TDB;
UT1System UT1;

/***************************************************************************
*
***************************************************************************/
public PhaseCalculator(Ephemeris ephemeris, Observatory obs,
                       TDBSystem TDB, UT1System UT1) {

    this.ephemeris = ephemeris;
    this.obs = obs;
    this.TDB = TDB;
    this.UT1 = UT1;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public PhaseCalculator(Ephemeris ephemeris, Observatory obs) {

    this(ephemeris, obs, TDBSystem.getInstance(), UT1System.getInstance());

} // end of default constructor

/*************************************************************************
* Calculates the phase of the moon at midnight of the given Night
*  The phase is the cosine of the angle between the
* observer and the sun, with the moon at the vetex. Therefore it is 1.0 at
* full moon and -1 at new moon.
*************************************************************************/
public double phase(int body, Night night) {

    PreciseDate midnight = night.getMidnight();

    /****************************************************
    * do the time conversions we need for the ephemeris *
    ****************************************************/
    PreciseDate tdb = TDB.convertDate(midnight);
    EOP eop = (EOP)UT1.convertDate(midnight);

    return ephemeris.getPhaseAngle(body, tdb, eop, obs).getCos();

} // end of phase method

/**************************************************************************
*
**************************************************************************/
public double phase(Night night) {

    return phase(Ephemeris.MOON, night);

} // end of phase of the moon method

/**************************************************************************
*
**************************************************************************/
public Direction phaseDirection(int body, Night night) {

    PreciseDate midnight = night.getMidnight();
    PreciseDate tdb = TDB.convertDate(midnight);
    EOP eop = (EOP)UT1.convertDate(midnight);

    return ephemeris.getPhaseDirection(body, tdb, eop, obs);

} // end of phaseDirection

/**************************************************************************
*
**************************************************************************/
public Direction phaseDirection(Night night) {

    return phaseDirection(Ephemeris.MOON, night);

} // end of phaseDirection for the moon

} // end of PhaseCycle class
