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
import eap.sky.util.*;
import eap.sky.util.coordinates.*;

/*************************************************************************
*
*************************************************************************/
public class StationaryRiseSet extends RiseSet {

Direction ra_dec;

/*************************************************************************
*
*************************************************************************/
public StationaryRiseSet(Direction ra_dec, AzAlt az_alt, double accuracy) {

    super(az_alt, accuracy);

    this.ra_dec = ra_dec;

} // end of constructor

/************************************************************************
*
************************************************************************/
public Direction direction(CachedDate time) { return ra_dec; }

} // end of StationaryRiseSet class
