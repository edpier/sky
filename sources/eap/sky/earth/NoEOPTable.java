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

package eap.sky.earth;

import eap.sky.time.*;
import eap.sky.time.barycenter.*;

/***************************************************************************
*
***************************************************************************/
public class NoEOPTable implements EOPTable {

UTCSystem UTC;
TDBSystem TDB;

/***************************************************************************
*
***************************************************************************/
public NoEOPTable(UTCSystem UTC, TDBSystem TDB) {

    this.UTC = UTC;
    this.TDB = TDB;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public NoEOPTable() {

    this(UTCSystem.getInstance(), TDBSystem.getInstance());

} // end of default constructor

/****************************************************************************
*
****************************************************************************/
public void getUT1(PreciseDate tai, EOP ut1) {

    PreciseDate tdb = TDB.convertDate(tai);


    PreciseDate utc = UTC.convertDate(tai);
    ut1.setTime(utc.getMilliseconds(), utc.getNanoseconds(), 0.0,
                NoPolarMotion.getInstance(), PrecessionCorrection.ZERO,
                new TidalArguments(tdb, ut1));

} // end of getUT1 method


} // end of NoEOPTable class