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

/**************************************************************************
* A source of atmospheric carbon dioxide concentration data.
**************************************************************************/
public interface CO2Monitor {

/**************************************************************************
* Return the molar fraction of carbon dioxide.
* @param time The time at which to report the CO<sub>2</sub> fraction.
* @return The CO<sub>2</sub> fraction in micromoles per mole (ppm).
**************************************************************************/
public double reportCO2Fraction(PreciseDate time);

} // end of CO2Monitor interface

