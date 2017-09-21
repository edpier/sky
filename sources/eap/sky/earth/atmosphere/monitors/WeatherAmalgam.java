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
public class WeatherAmalgam extends WeatherAdapter {

WeatherStation weather;
CO2Monitor co2;
TropopauseMonitor tropopause;

/**********************************************************************
*
**********************************************************************/
public WeatherAmalgam(WeatherStation weather, CO2Monitor co2,
                      TropopauseMonitor tropopause) {

    this.weather = weather;
    this.co2 = co2;
    this.tropopause = tropopause;

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public Weather reportWeather(PreciseDate time) {

    return weather.reportWeather(time);

} // end of reportWeather method

/**************************************************************************
* Report the CO<sub>2</sub> fraction
* @return the CO<sub>2</sub> fraction specified in the constructor in
* micromoles per mole (ppm).
**************************************************************************/
public double reportCO2Fraction(PreciseDate time) {

    return co2.reportCO2Fraction(time);

} // end of reportCO2Fraction method

/**************************************************************************
*
**************************************************************************/
public Tropopause reportTropopauseConditions(PreciseDate time) {

    return tropopause.reportTropopauseConditions(time);

} // end of reportTropopauseConditions method


} // end of WeratherAmalgam class
