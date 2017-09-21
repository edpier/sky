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
import eap.sky.earth.atmosphere.*;

/**********************************************************************
*
**********************************************************************/
public abstract class WeatherAdapter implements WeatherStation,
                                                 CO2Monitor,
                                                 TropopauseMonitor {


/**************************************************************************
* Report the CO<sub>2</sub> fraction
* @return the CO<sub>2</sub> fraction specified in the constructor in
* micromoles per mole (ppm).
**************************************************************************/
public double reportCO2Fraction(PreciseDate time) {

    return Weather.STANDARD_1976_CO2;

} // end of reportCO2Fraction method

/**************************************************************************
*
**************************************************************************/
public Tropopause reportTropopauseConditions(PreciseDate time) {

    return Tropopause.STANDARD_1976;

} // end of reportTropopauseConditions method

} // end of CombinedWeather abstract class