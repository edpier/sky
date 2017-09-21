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

import eap.sky.time.*;
import eap.sky.util.*;

/***************************************************************************
*
***************************************************************************/
public class NutationRotation extends NutationTransform {

Rotation rot;

/***************************************************************************
*
***************************************************************************/
public void set(NutationValues values) {

    Angle moe = values.getMeanObliquity();
    Angle toe = moe.plus(values.getEpsilon());
    Angle psi = values.getPsi();

    Rotation rot1 = new Rotation(toe.negative(), Direction.X_AXIS);
    Rotation rot2 = new Rotation(psi.negative(), Direction.Z_AXIS);
    Rotation rot3 = new Rotation(moe, Direction.X_AXIS);

    rot = (Rotation)(rot3.combineWith(rot2).combineWith(rot1).invert());

} // end of set method

/***************************************************************************
* @param t Number of Julian centuries since the epoch
***************************************************************************/
public ThreeVector transform(ThreeVector teme) {

    return rot.transform(teme);

} // end of compuute method

} // end of NutationRotation class