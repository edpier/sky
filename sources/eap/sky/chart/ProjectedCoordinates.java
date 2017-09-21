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

package eap.sky.chart;

import eap.sky.util.*;
import eap.sky.util.coordinates.*;
import eap.sky.time.*;

/************************************************************************
*
************************************************************************/
public class ProjectedCoordinates {


Coordinates coordinates;
Rotation aspect;
Projection projection;
PreciseDate time;

Transform to_ra_dec;
Transform from_ra_dec;
/************************************************************************
*
************************************************************************/
public ProjectedCoordinates(Coordinates coordinates, Rotation aspect,
                             Projection projection, PreciseDate time) {

    this.coordinates = coordinates;
    this.aspect = aspect;
    this.projection = projection;

    this.time = (PreciseDate)time.clone();

    to_ra_dec = coordinates.toRADec(time);


} // end of constructor




/************************************************************************
*
************************************************************************/
public Transform getTransformToProjection() {

    /************************************************
    * if we are in the same coordinates, then it's
    * just the aspect transform
    ************************************************/
    if(coordinates.equals(Coordinates.RA_DEC)) return aspect;

    /***********************************************
    * going from RA/Dec to some other coordinates *
    **********************************************/
    return from_ra_dec.combineWith(aspect);

} // end of getTransformToProjection method

/************************************************************************
*
************************************************************************/
public Transform getTransformToProjection(Coordinates coord) {

    /************************************************
    * if we are in the same coordinates, then it's
    * just the aspect transform
    ************************************************/
    if(coord.equals(coordinates)) return aspect;

    /***********************************************
    * going from RA/Dec to some other coordinates *
    **********************************************/
    if(coord.equals(Coordinates.RA_DEC)) {
        if(from_ra_dec == null) from_ra_dec = to_ra_dec.invert();
        return from_ra_dec.combineWith(aspect);
    }

    /*********************************************
    * going from some other coordinates to RADec *
    *********************************************/
    if(coordinates.equals(Coordinates.RA_DEC)) {
        return coord.toRADec(time).combineWith(aspect);
    }

    /**********************************************
    * going between two non-RA/Dec coordinates *
    *******************************************/
    return coord.toRADec(time).combineWith(from_ra_dec).combineWith(aspect);


} // end of getTransformToProjection method

/************************************************************************
*
************************************************************************/
public boolean equals(Object o) {

    ProjectedCoordinates proj = (ProjectedCoordinates)o;

    return projection.equals(proj.projection) &&
               aspect.equals(proj.aspect    ) &&
            to_ra_dec.equals(proj.to_ra_dec );

} // end of equals method


} // end of ProjectedCoordinates class
