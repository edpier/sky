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

import eap.sky.ephemeris.*;
import eap.sky.time.*;
import eap.sky.util.*;

import java.util.*;

/***************************************************************************
*
***************************************************************************/
public abstract class InterpolationTable {

int body;
private Ephemeris ephemeris;
private InterpolationMethod method;
VectorType type;

/***************************************************************************
*
***************************************************************************/
public InterpolationTable(Ephemeris ephemeris, int body, VectorType type,
                          InterpolationMethod method) {

    this.ephemeris = ephemeris;
    this.body = body;
    this.type = type;
    this.method = method;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public int getBody() { return body; }

/***************************************************************************
*
***************************************************************************/
public VectorType getVectorType() { return type; }

/***************************************************************************
*
***************************************************************************/
public InterpolationPoint compute(PreciseDate time) {

    return new InterpolationPoint(time, type.compute(ephemeris, body, time));

} // end of compute method

/***************************************************************************
*
***************************************************************************/
public InterpolationInterval createInterpolationInterval(InterpolationPoint point1,
                                                         InterpolationPoint point2) {

    return method.createInterpolationInterval(point1, point2);

} // end of createInterpolationInterval method


/***************************************************************************
*
***************************************************************************/
public abstract ThreeVector interpolate(PreciseDate tdb);

} // end of InterpolationTable class