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

package eap.sky.earth;

import eap.sky.util.*;

/**************************************************************************
*
**************************************************************************/
public class NoPolarMotion extends PolarMotionParameters {

private static PolarMotionParameters INSTANCE;

/**************************************************************************
*
**************************************************************************/
private NoPolarMotion() {

    super(0,0,0,0);

} // end of constructor

/**************************************************************************
*
**************************************************************************/
public static PolarMotionParameters getInstance() {

    if(INSTANCE == null) INSTANCE = new NoPolarMotion();
    return INSTANCE;

} // end of getInstance method

/****************************************************************************
* Returns the transform due to polar motion. Specifically this is the
* transform from International Terrestrial Reference System cordinates
* to the coordinates formed by the Celestial Intermediate Pole and the
* Terrestrial Ephemeris Origin.
* @return The polar motion transform from ITRS to CIP/TEO coordinates.
****************************************************************************/
public Rotation rotation(double julian_centuries) { return Rotation.IDENTITY; }

} // end of NoPolarMotion class