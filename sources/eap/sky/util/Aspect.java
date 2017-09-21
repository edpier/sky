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

package eap.sky.util;

/***************************************************************************
*
***************************************************************************/
public class Aspect {

public static final int CUSTOM = 0;
public static final int POLAR = 1;
public static final int EQUATORIAL = 2;

int type;

Rotation rot;
int flip_x;
int flip_y;

/***************************************************************************
*
***************************************************************************/
public static boolean isValidType(int type) {

    return type == CUSTOM ||
           type == POLAR ||
           type == EQUATORIAL;
}

/***************************************************************************
*
***************************************************************************/
public Aspect(Rotation rot, int flip_x, int flip_y) {

    this.rot = rot;
    this.flip_x = flip_x;
    this.flip_y = flip_y;
    type = CUSTOM;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public static Aspect createPolarAspect(double twist, int flip_x, int flip_y) {

    Aspect aspect = new Aspect((Rotation)new Rotation(new Euler(Direction.Z_AXIS, twist)).invert(), flip_x, flip_y);

    aspect.type = POLAR;

    return aspect;

} // end of createPolarAspect factory method

/***************************************************************************
*
***************************************************************************/
public static Aspect createEquatorialAspect(double twist, int flip_x, int flip_y) {

    Aspect aspect = new Aspect((Rotation)new Rotation(new Euler(Direction.X_AXIS, twist)).invert(), flip_x, flip_y);

    aspect.type = EQUATORIAL;

    return aspect;

} // end of createEquatorialAspect factory method

/***************************************************************************
*
***************************************************************************/
public Rotation getRotation() { return rot; }

/***************************************************************************
*
***************************************************************************/
public int getFlipX() { return flip_x; }

/***************************************************************************
*
***************************************************************************/
public int getFlipY() { return flip_y; }

} // end of Aspect class