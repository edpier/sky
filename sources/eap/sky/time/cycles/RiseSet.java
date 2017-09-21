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
import eap.sky.earth.*;


/**************************************************************************
*
**************************************************************************/
public abstract class RiseSet {

public static final double ASTRONOMICAL_TWILIGHT = -18.0;
public static final double CIVIL_TWILIGHT = -12.0;
public static final double NEAR_IR_TWILIGHT = -8.0;

AzAlt az_alt;
double accuracy;

/************************************************************************
*
************************************************************************/
public RiseSet(AzAlt az_alt, double accuracy) {

    this.az_alt = az_alt;
    this.accuracy = accuracy;

    /**************************************************
    * the PreciseDate timekeeping has a granularity
    * of 1 nanosecond, so we can't be more accurate than that
    *************************************************/
    if(accuracy < 1e-9) accuracy = 1e-9;

} // end of constructor

/************************************************************************
*
************************************************************************/
public AzAlt getAzAltCoordinates() { return az_alt; }

/************************************************************************
*
************************************************************************/
public abstract Direction direction(CachedDate time);

/************************************************************************
*
************************************************************************/
public PreciseDate findClosestCrossing(PreciseDate time, boolean rise,
                                       double alt) {

    /************************
    * iterate to a solution *
    ************************/
    for(int iteration=0; iteration < 10; ++iteration) {

        /*****************
        * cache the time *
        *****************/
        TransformCache cache = TransformCache.makeCache(time);

        /******************************************
        * find the current position of the object *
        ******************************************/
        Direction radec = direction(cache);

        /***********************************
        * estimate the closest rise or set *
        ***********************************/
        PreciseDate new_time = az_alt.estimateCrossingTime(radec, alt, true,
                                                           cache, rise);


        /*************************
        * check if we're done
        * and increment the time
        *************************/
        double error = Math.abs(new_time.secondsAfter(time));
        time = new_time;
     //   System.out.println("    "+LocalTimeSystem.getInstance().convertDate(time)+" "+error);
        if(error < accuracy) break;

    } // end of loop over iterations

    return time;

} // end of findClosestCrossing


} // end of RiseSetCalculator class
