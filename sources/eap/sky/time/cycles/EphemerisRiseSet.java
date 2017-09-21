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
import eap.sky.util.*;
import eap.sky.util.coordinates.*;
import eap.sky.earth.*;
import eap.sky.ephemeris.*;

/**************************************************************************
*
**************************************************************************/
public class EphemerisRiseSet extends RiseSet {

public static final double ASTRONOMICAL_TWILIGHT = -18.0;

Ephemeris ephemeris;
int body;
Observatory obs;
TDBSystem TDB;
UT1System UT1;



/************************************************************************
*
************************************************************************/
public EphemerisRiseSet(int body, AzAlt az_alt, Ephemeris ephemeris,
                        double accuracy, TDBSystem TDB, UT1System UT1) {

    super(az_alt, accuracy);

    this.ephemeris = ephemeris;
    this.body = body;

    this.obs = az_alt.getObservatory();
    this.TDB = TDB;
    this.UT1 = UT1;

} // end of constructor

/************************************************************************
*
************************************************************************/
public EphemerisRiseSet(int body, AzAlt az_alt,
                         Ephemeris ephemeris, double accuracy) {

    this(body, az_alt,
         ephemeris, accuracy,
         az_alt.getTDBSystem(),
         az_alt.getUT1System() );

} // end of default constructor

/************************************************************************
*
************************************************************************/
public EphemerisRiseSet(int body, AzAlt az_alt, double accuracy) {

    this(body, az_alt, az_alt.getEphemeris(), accuracy);

} // end of default constructor

/************************************************************************
*
************************************************************************/
public EphemerisRiseSet(AzAlt az_alt, double accuracy) {

    this(Ephemeris.SUN, az_alt, accuracy);

} // end of default constructor

/************************************************************************
*
************************************************************************/
public AzAlt getAzAlt() { return az_alt; }

/************************************************************************
*
************************************************************************/
public Ephemeris getEphemeris() { return ephemeris; }

/************************************************************************
*
************************************************************************/
public PhaseCalculator createPhaseCalculator() {

    return new PhaseCalculator(ephemeris, obs, TDB, UT1);

} // end of createPhaseCalculator method

/************************************************************************
*
************************************************************************/
public boolean isSun() { return body == ephemeris.SUN; }

/************************************************************************
*
************************************************************************/
public Direction direction(CachedDate time) {

    EOP eop = (EOP)UT1System.getInstance().convertDate(time);
    PreciseDate tdb = TDBSystem.getInstance().convertDate(time);

    return ephemeris.position(body, tdb, eop, obs).getDirection();

} // end of position method

} // end of RiseSetCalculator class
