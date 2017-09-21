// Copyright 2013 Edward Alan Pier
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

package eap.sky.ephemeris.sgp4;

import eap.sky.util.*;


/*****************************************************************************
*
****************************************************************************/
public class MotionState {

private ThreeVector position;
private ThreeVector velocity;

//private double radiansToDegrees = 180.0/Math.PI;

/*************************************************************************
* Constructor
* @param satNum
*************************************************************************/
public MotionState(ThreeVector position, ThreeVector velocity) {

    this.position = position;
    this.velocity = velocity;

} // end of constructor

/*************************************************************************
*
*************************************************************************/
public ThreeVector getPosition() { return position; }

/*************************************************************************
*
*************************************************************************/
public ThreeVector getVelocity() { return velocity; }

} // end of MotionState class
