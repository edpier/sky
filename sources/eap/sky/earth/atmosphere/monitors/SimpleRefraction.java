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

package eap.sky.earth.atmosphere.monitors;

import eap.sky.time.*;
import eap.sky.util.*;
import eap.sky.earth.*;
import eap.sky.earth.atmosphere.*;

/****************************************************************************
* A Simple refraction formula which gives results within a few arcseconds
* of the Saastamoinen refraction, but which is valid to very low altitudes.
* This one is better to use when calculating rise and set times.
****************************************************************************/
public class SimpleRefraction implements Refraction {

WeatherStation station;
Observatory obs;

/****************************************************************************
*
****************************************************************************/
public SimpleRefraction(WeatherStation station, Observatory obs) {

    this.station  = station;
    this.obs = obs;

} // end of constructor

/****************************************************************************
*
****************************************************************************/
public Transform refractionTransform(PreciseDate time) {

    Weather weather = station.reportWeather(time);
    Deflection deflection = new SimpleDeflection(weather);
    return new DeflectionTransform(deflection, obs.getZenith());

} // end of refractionTransform method

} // end of SimpleRefraction class