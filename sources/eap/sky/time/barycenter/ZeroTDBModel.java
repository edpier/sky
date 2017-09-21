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
* Represents a Eucliden model of the SolarSystem, for which TDB is identical
* to TT. This is useful if you have an ephemeris which requires times in
* TT and not TDB, or if you do not require millisecond accuracy, and
* compute time is a premium.
**************************************************************************/
public class ZeroTDBModel extends TDBModel {

private static final ZeroTDBModel instance = new ZeroTDBModel();

/**************************************************************************
* Returns the only instance of this class. Use this instead of a constructor.
**************************************************************************/
public static ZeroTDBModel getInstance() { return instance; }

/**************************************************************************
* Makes the constructor private.
**************************************************************************/
private ZeroTDBModel() {}

/**************************************************************************
* Always returns zero.
**************************************************************************/
public double getTDBminusTT(PreciseDate tdb) {  return 0.0; }



} // end of ZeroTDBModel class
