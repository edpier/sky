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

package eap.sky.earth.gravity;

import eap.sky.util.*;

/******************************************************************************
* The gravity potential of the Earth due to centrifugal repulsion.
******************************************************************************/
public class CentrifugalPotential extends Potential {


double angular_velocity; // radians/s

/******************************************************************************
* Create a new potential.
* @param angular_velocity The angular velocity of the Earth in radians
* per second.
******************************************************************************/
public CentrifugalPotential(double angular_velocity) {

    this.angular_velocity = angular_velocity;
}

/******************************************************************************
*
******************************************************************************/
public double potential(Direction geocentric, double r) {

   double cos_lat = Math.cos(Math.toRadians(geocentric.getLatitude()));
   double thing = angular_velocity * r * cos_lat;

   return 0.5 * thing*thing;

} // end of potential method

} // end of Potential class
