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

package eap.sky.time;

/***************************************************************************
* Represents the Terrestrial Time system. This gives the time in SI seconds
* on the rotating geoid (i.e. at sea level on the surface of the Earth),
* and is offset from TAI by 32.284 seconds on 1977-01-01T00:00:00 TAI.
* There are different realizations of TT. The most common one is to assume
* the offset from TAI is constant.
* Other realizations of TT attempt to correct for deviations of TAI from
* the SI second which can be determined after the fact.
* Approximately once a year the
* <a href="http://www.bipm.fr">Bureau International des Poids et Measures
* (BIPM)</a> releases a revised version of TT, referred to as TT(BIPMxx),
* where "xx" is the last two digits of the year of release.
* In these revised realizations of TT, TT-TAI can vary by a few microseconds
* from 32.184 seconds. As of this writing, the latest version is
* <a href="ftp://ftp2.bipm.fr/pub/tai/scale/ttbipm.04">TT(BIPM04)</a>.
* <p>
* Historically, TT is the descendant of ET, or Ephemeris Time.
* Ephemeris Time was based on the motion of the planets in the solar system.
* Previous to 1991 the equivalent of TT was known as TDT
* (Terrestrial Dynamic Time).
* <p>
* TT is defined by
* Recommendation IV of Resolution A4 of the International Astronomical
* Union, adopted at its XXIst General Assembly (1991), and updated by
* <A href="http://danof.obspm.fr/IAU_resolutions/Resol-UAI.htm">
* Resolution B1.9 at its XXIVth General Assembly (2000)</a>.
***************************************************************************/
public abstract class TTSystem extends TimeSystem {

private static TTSystem instance = PlainTTSystem.getInstance();

/** 32.184 **/
protected static double TAI_OFFSET = 32.184;

/****************************************************************************
* Create a new instance of TT
****************************************************************************/
protected TTSystem(String abbreviation) {

    super("Terrestrial Time", abbreviation);
}

/**************************************************************************
* Sets the lookup table to use to determine the offset from TAI.
* @param table The table to use. If this is null, a constant offset of
* {@link #TAI_OFFSET} will be used. This is the default when the class is
* loaded.
**************************************************************************/
public static void setDefaultTable(TTTable table) {

    if(table == null) instance = PlainTTSystem.getInstance();
    else              instance = table.getTTSystem();
}

/**************************************************************************
* Returns an instance of this class. The instance will use the lookup table
* defined by the last call to {@link #setDefaultTable(TTTable)}.
* If the lookup table is null (e.g. if {@link #setDefaultTable(TTTable)} has
* never been called, then the returned oject will be a {@link PlainTTSystem}.
* @return A TTSystem object.
****************************************************************************/
public static TTSystem getInstance() { return instance; }


} // end of TTSystem class
