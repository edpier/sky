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

/*****************************************************************************
* The Earth's gravity field. Note that this can be due to gravitational
* attraction,centrifugal repulsion or a combination of the two.
* In general a potential may take into account the small scale mass
* distribution of the Earth.
******************************************************************************/
public abstract class Potential {

/************************************************************************
* Create a new potential.
************************************************************************/
protected Potential() {}


/******************************************************************************
* Calculate the Earth's gravity potential at some location.
* @param geocentric The genocentric longitude and latitude of a position in
* space. This is the true "polar coordinates" longitude and latitude.
* measures with a vertex at the center of the Earth.
* @param r The distance from the center of the Earth in meters.
* @return The Earths gravitational potential in MKS units.
******************************************************************************/
public abstract double potential(Direction geocentric, double r);

/****************************************************************************
* Calculates an equiptential surface, such as the geoid.
* @param geocentric The genocentric longitude and latitude of a position in
* space. This is the true "polar coordinates" longitude and latitude.
* measures with a vertex at the center of the Earth.
* @param potential The Earth's gravity potential in MKS units.
* @return The distance from the center of the Earth at which the given
* potential occurs in the given direction.
****************************************************************************/
public double surface(Direction geocentric, double potential) {

    /****************
    * initial guess *
    ****************/
    double r0 = 6378136.3;

    /***********************************************
    * figure out which direction we need to go to
    * bracket the solution
    ***********************************************/
    double dr = 1000.0;
    double y0 = potential(geocentric, r0);

    double r1 = r0 + dr;
    double y1 = potential(geocentric, r1);

    if((y0<potential && y1<potential && y1>y0) ||
       (y0>potential && y1>potential && y1<y0)   ) {
            r0 = r1;
            y0 = y1;
    } else {
        dr = -dr;
    }

    /***********************
    * bracket the solution *
    ***********************/
    while((y0<potential && y1<potential) ||
          (y0>potential && y1>potential)   ) {

        r0 = r1;
        y0 = y1;

        r1 = r0 + dr;
        y1 =potential(geocentric, r1);

        dr *=2.0;

    }


    /********************************
    * now search within the bracket *
    ********************************/
    while(true) {

        double r = 0.5*(r0 + r1);
        double y = potential(geocentric, r);

        if(r == r0 || r == r1 ) return r;

      //  System.out.println(r+" "+(y-potential)+" "+(r1-r0));

        if((y > potential && y0 < potential) ||
           (y < potential && y0 > potential)   ) {
           /**********************************************
           * the left sub-interval brackets the solution *
           **********************************************/
            r1 = r;
            y1 = y;
        } else {
            r0 = r;
            y0 = y;
        }
    }

   // System.out.println(y-potential);


   // return r;


} // end of geoid method


} // end of Potential class
