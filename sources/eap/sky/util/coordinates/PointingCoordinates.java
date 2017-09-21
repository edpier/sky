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

import eap.sky.util.*;
import eap.sky.time.*;

/*****************************************************************************
*
*****************************************************************************/
public class PointingCoordinates extends Coordinates {

Aspect polar;
Aspect equatorial;

Coordinates coord;
PointingSource pointing;
boolean is_radec;


/*****************************************************************************
*
*****************************************************************************/
public PointingCoordinates(Coordinates coord, PointingSource pointing) {

    if(coord instanceof PointingCoordinates) {
        /*********************************************
        * we are composing one rotation with another *
        *********************************************/
        PointingCoordinates pc = (PointingCoordinates)coord;
        this.coord = pc.coord;
        this.pointing = new CompositePointing(pointing, pc.pointing);
    } else {
        /********************************
        * this is what happens normally *
        ********************************/
        this.coord = coord;
        this.pointing = pointing;
    }

    is_radec = coord.equals(Coordinates.RA_DEC);

    polar      = Aspect.createPolarAspect(0,1,1);
    equatorial = Aspect.createEquatorialAspect(0,1,1);

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public Aspect getAspect(int type) {

    if(     type == Aspect.POLAR     ) return polar;
    else if(type == Aspect.EQUATORIAL) return equatorial;
    else throw new IllegalArgumentException("Unsupported aspect "+type);

} // end of getAspect method

/*****************************************************************************
*
*****************************************************************************/
public PointingSource getPointingSource() { return pointing; }

/*****************************************************************************
*
*****************************************************************************/
public Transform getOptimizedTransformTo(Coordinates coord, PreciseDate time) {

    if(coord.equals(this.coord)) return pointing.getRotation(time);
    else if(coord instanceof PointingCoordinates) {
        /******************************************
        * nested pointings, combine the rotations *
        ******************************************/
        PointingCoordinates pc = (PointingCoordinates)coord;
        if(pc.coord.equals(this.coord)) {
            /**********************************************
            * we both have the same reference coordinates *
            **********************************************/
            return pointing.getRotation(time).combineWith(
                pc.pointing.getRotation(time).invert());
        }
    }

    return null;

} // end of getOptimizedTransformTo method

/*****************************************************************************
*
*****************************************************************************/
public Transform toRADec(PreciseDate time) {

    Rotation rot = pointing.getRotation(time);

    if(is_radec) return rot;
    else return rot.combineWith(coord.toRADec(time));

} // end of toRADec method

/*****************************************************************************
*
*****************************************************************************/
public String toString() {

    return "PointingCoordinates from "+coord;

} // end of toString method

} // end of PointingCoordinates class
