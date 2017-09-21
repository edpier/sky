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

import eap.sky.earth.atmosphere.*;
import eap.sky.time.*;

import java.io.*;

/**************************************************************************
* A simple implementation of all three monitor classes.
* This class returns the same values for every time.
**************************************************************************/
public class ConstantWeather extends WeatherAdapter implements Serializable {

Weather weather;
double co2_fraction;
Tropopause tropopause;

/**************************************************************************
* Create a new monitor specifying all the values explicitly.
* @param weather The weather data to report.
* @param co2_fraction The CO<sub>2</sub> fraction in micromoles per mole
* (ppm) to report.
* @param tropopause The tropopause data to report.
**************************************************************************/
public ConstantWeather(Weather weather, double co2_fraction,
                       Tropopause tropopause) {

    this.weather = weather;
    this.co2_fraction = co2_fraction;
    this.tropopause = tropopause;

} // end of constructor

/**************************************************************************
* Create a new monitor with default CO<sub>2</sub> and tropopause
* data from the 1976 standard atmosphere.
* @param weather The weather data to report.
**************************************************************************/
public ConstantWeather(Weather weather) {

    this(weather, Weather.STANDARD_1976_CO2, Tropopause.STANDARD_1976);

} // end of simple constructor

/**************************************************************************
* Report the weather
* @return The weather specified in the constructor.
**************************************************************************/
public Weather reportWeather(PreciseDate time) { return weather; }

/**************************************************************************
* Report the CO<sub>2</sub> fraction
* @return the CO<sub>2</sub> fraction specified in the constructor in
* micromoles per mole (ppm).
**************************************************************************/
public double reportCO2Fraction(PreciseDate time) { return co2_fraction; }

/**************************************************************************
* Report the tropopause conditions.
* @return The tropopause considitions specified in the constructor.
**************************************************************************/
public Tropopause reportTropopauseConditions(PreciseDate time) {

    return tropopause;
}

} // end of ConstantWeather class
