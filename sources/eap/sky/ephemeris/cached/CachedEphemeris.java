// Copyright 2014 Edward Alan Pier
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

package eap.sky.ephemeris.cached;

import eap.sky.earth.*;
import eap.sky.ephemeris.*;
import eap.sky.time.*;
import eap.sky.util.*;

import java.util.*;

/***************************************************************************
*
***************************************************************************/
public class CachedEphemeris extends Ephemeris {

Map<Integer, InterpolationTable> pos_tables;
Map<Integer, InterpolationTable> vel_tables;

/***************************************************************************
*
***************************************************************************/
public CachedEphemeris(UT1System ut1) {
    super(ut1);

    pos_tables = new HashMap<Integer, InterpolationTable>();
    vel_tables = new HashMap<Integer, InterpolationTable>();

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public void addInterpolationTable(InterpolationTable table) {

    VectorType type = table.getVectorType();
    Integer body = new Integer(table.getBody());

    if(type == VectorType.POSITION) pos_tables.put(body, table);
    else                            vel_tables.put(body, table);

} // end of addInterpolationTable method

/***************************************************************************
*
***************************************************************************/
public ThreeVector barycentricPosition(int body, PreciseDate tdb) {

    InterpolationTable table = pos_tables.get(new Integer(body));
    if(table == null) {
        throw new IllegalArgumentException("No position table for body "+body);
    }

    return table.interpolate(tdb);

} // end of barycentricPosition method

/**************************************************************************
*
**************************************************************************/
public ThreeVector barycentricVelocity(int body, PreciseDate tdb) {

    InterpolationTable table = vel_tables.get(new Integer(body));
    if(table == null) {
        throw new IllegalArgumentException("No velocity table for body "+body);
    }

    return table.interpolate(tdb);

} // end of barycentricVelocity method

} // end of CachedEphemeris class