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

package eap.sky.earth.atmosphere;

import eap.sky.util.*;

/******************************************************************************
*
******************************************************************************/
public class SimpleDeflection implements Deflection {

double density;

/******************************************************************************
*
******************************************************************************/
public SimpleDeflection(Weather weather) {

    density = weather.getPressure()*0.01/weather.getKelvinTemperature();

} // end of constructor

/**************************************************************************
* Calculates the refraction for a given apparent zenith angle.
* @param ground_angle the apparent zenith angle as viewed by an observer
*        on the ground.
* @return The refraction
**************************************************************************/
public Angle calculateDeflection(Angle ground_angle) {

    double alt = 90.0-ground_angle.getDegrees();

    if(alt > 89.9225) {
        return Angle.ANGLE0;

    } else if(alt > 23.95565) {
        /********************************
        * use the high altitude formula *
        ********************************/
        return new Angle(0.28*0.0167*density/
                         Math.tan(Math.toRadians(alt+7.31/(alt+4.4))));

    } else if(alt > -.8245) {
        /*******************************
        * use the low altitude formula *
        *******************************/
        double alt2 = alt*alt;
        return new Angle(density*(0.1594 + 0.0196*alt + 0.00002*alt2)/
                                 (1.0    + 0.505 *alt + 0.0845 *alt2)  );

    } else {
        /******************************
        * we cap it at high altitudes *
        ******************************/
        return new Angle(density*0.223572104);
    }



} // end of calculateDeflection method

} // end of SimpleDeflection class
