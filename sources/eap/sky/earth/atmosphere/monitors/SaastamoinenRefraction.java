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

import eap.sky.util.*;
import eap.sky.time.*;
import eap.sky.earth.*;
import eap.sky.earth.atmosphere.*;

/*************************************************************************
* An expansion in odd powers of the tangent of the zenith angle.
* @see SaastamoinenDeflection
*************************************************************************/
public class SaastamoinenRefraction implements Refraction {

double wavelength;
Observatory obs;
WeatherStation weather_station;
CO2Monitor co2_monitor;
TropopauseMonitor tropopause_monitor;

/*************************************************************************
* Create a new object.
* @param wavelength The wavelength at which to calculate the refraction
*        in nanometers.
* @param obs The location, orientation, and local gravity for the observer.
* @param weather_station A source of weather data.
* @param co2_monitor A source of CO<sub>2</sub> concentration data.
* @param tropopause_monitor A source of information about the tropopause.
*************************************************************************/
public SaastamoinenRefraction(double wavelength,
                             Observatory obs,
                             WeatherStation weather_station,
                             CO2Monitor co2_monitor,
                             TropopauseMonitor tropopause_monitor) {

    this.wavelength = wavelength;
    this.obs = obs;
    this.weather_station = weather_station;
    this.co2_monitor = co2_monitor;
    this.tropopause_monitor = tropopause_monitor;

} // end of constructor

/*************************************************************************
* Returns the wavelength of the light being refracted.
* @return The wavelength in nanometers.
*************************************************************************/
public double getWavelength() { return wavelength; }

/*************************************************************************
* Returns the CO<sub>2</sub> monitor used by this object.
* @return the CO<sub>2</sub> monitor.
*************************************************************************/
public CO2Monitor getCO2Monitor() { return co2_monitor; }

/*************************************************************************
* Returns the Tropopause monitor used by this object.
* @return The Tropopause monitor.
*************************************************************************/
public TropopauseMonitor getTropopauseMonitor() { return tropopause_monitor; }

/*************************************************************************
* Generates the refraction transform
* @param time The time at which to calculate the refraction.
* This value is passed to the monitors to indicate the time at which to
* report the atmospheric conditions. Often this is just the current time.
* @return The refraction transform at the given time.
*************************************************************************/
public Transform refractionTransform(PreciseDate time) {

    /*************************************
    * get current atmospheric conditions *
    *************************************/
    Weather weather = weather_station.reportWeather(time);
    double co2_fraction = co2_monitor.reportCO2Fraction(time);
    Tropopause tropopause =
                         tropopause_monitor.reportTropopauseConditions(time);

    /*********************
    * get the deflection *
    *********************/
    Deflection deflection = new SaastamoinenDeflection(weather,
                                                       co2_fraction,
                                                       wavelength,
                                                       tropopause,
                                                       obs);

                                                       
    return new DeflectionTransform(deflection, obs.getZenith());

} // end of RefractionTransform method

} // end of SaastamoinenRefraction class
