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

import java.io.*;

/*****************************************************************************
* A generator for refraction transforms. Typicaly an implementation
* of this interface will take various monitors in its constructor.
* @see WeatherStation
* @see CO2Monitor
* @see TropopauseMonitor
*****************************************************************************/
public interface Refraction extends Serializable {

/**************************************************************************
* Generate the refraction transform at a a particular instant of time.
* @param time The time at which to calculate the refraction.
* @return The refraction transform at a given time.
**************************************************************************/
public Transform refractionTransform(PreciseDate time);

} // end of RefractionSource interface
