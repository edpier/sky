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

/*****************************************************************************
*
*****************************************************************************/
public class CompositePointing implements PointingSource {

PointingSource first;
PointingSource second;

/*****************************************************************************
*
*****************************************************************************/
public CompositePointing(PointingSource first, PointingSource second) {

    this.first = first;
    this.second = second;

} // end of constructor

/*****************************************************************************
*
*****************************************************************************/
public Rotation getRotation(PreciseDate time) {

    return (Rotation)first.getRotation(time).combineWith(second.getRotation(time));

} // end of getRotation method

} // end of CompositePointing class