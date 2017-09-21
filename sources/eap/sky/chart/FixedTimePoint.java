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

package eap.sky.chart;

import eap.sky.time.*;
import eap.sky.util.*;
import eap.sky.util.coordinates.*;

/***********************************************************************
*
***********************************************************************/
public class FixedTimePoint extends ProjectedPoint {

TransformCache time;
ChartState last_state;

/************************************************************************
*
************************************************************************/
public FixedTimePoint(Direction original, Coordinates coord,
                      PreciseDate time) {

    super(original, coord);
    setTime(time);

} // end of constructor

/************************************************************************
*
************************************************************************/
public void setTime(PreciseDate time) {

    this.time = TransformCache.makeCache(time);

} // end of setTime method

/************************************************************************
*
************************************************************************/
public boolean update(ChartState state) {

    /*************************************************
    * change the chart time to our own internal time *
    *************************************************/
  //  long start = System.currentTimeMillis();
    ChartState state2 = state.changeTime(last_state, time);
    last_state = state2;
//     long end = System.currentTimeMillis();
//     System.out.println("    point time substitution time="+(end-start));
    return super.update(state2);

} // end of update method

} // end of FixedTimePoint class
