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

package eap.sky.util.coordinates;

import eap.sky.time.*;
import eap.sky.util.*;

/****************************************************************************
*
****************************************************************************/
public class FixedTimeCoordinates extends Coordinates {

Coordinates coord;
PreciseDate time;

Transform to_ra_dec;

/****************************************************************************
*
****************************************************************************/
public FixedTimeCoordinates(Coordinates coord, PreciseDate time) {

    this.coord = coord;
    this.time = time;

    to_ra_dec = coord.toRADec(time);


} // end of constructor

/****************************************************************************
*
****************************************************************************/
public Aspect getAspect(int type) { return coord.getAspect(type); }

/****************************************************************************
*
****************************************************************************/
public Transform toRADec(PreciseDate time) { return to_ra_dec; }

} // end of FixedTimeCoordintes class
