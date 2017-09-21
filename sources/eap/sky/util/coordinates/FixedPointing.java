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

package eap.sky.util.coordinates;

import eap.sky.time.*;
import eap.sky.util.*;

/******************************************************************************
*
******************************************************************************/
public class FixedPointing implements PointingSource {

Coordinates specified;
Coordinates reported;
Direction dir;
double angle;

/******************************************************************************
*
******************************************************************************/
public FixedPointing(Coordinates specified, Coordinates reported, Direction dir,
                     double angle) {

    this.specified = specified;
    this.reported  = reported;
    this.dir = dir;
    this.angle = angle;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public Rotation getRotation(PreciseDate time) {

    Transform trans = specified.getTransformTo(reported, time);
    Direction dir = trans.transform(this.dir);

    return (Rotation)new Rotation(new Euler(dir, angle)).invert();

} // end of getRotation method

} // end of FixedPointing class