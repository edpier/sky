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
public class PolarMethod extends InterpolationMethod {

public InterpolationInterval createInterpolationInterval(InterpolationPoint p1,
                                                         InterpolationPoint p2) {
    return new PolarInterval(p1, p2);

} // end of createInterpolationInterval method

} // end of PolarMethod class