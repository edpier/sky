// Copyright 2014 Edward Alan Pier
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

package eap.sky.ephemeris.cached;

import eap.sky.ephemeris.*;
import eap.sky.time.*;
import eap.sky.util.*;

import java.util.*;

/***************************************************************************
* An interpolation interval that uses polar coordinates.
***************************************************************************/
public class PolarInterval extends InterpolationInterval {

double radius1;
double delta_r;

Direction dir1;
double angle;
Direction axis;

/***************************************************************************
*
***************************************************************************/
public PolarInterval(InterpolationPoint p1, InterpolationPoint p2) {
    super(p1, p2);

    /***********************
    * pull out the vectors *
    ***********************/
    ThreeVector v1 = p1.getThreeVector();
    ThreeVector v2 = p2.getThreeVector();

    /****************
    * get the radii *
    ****************/
    radius1 = v1.getLength();
    delta_r = v2.getLength() - radius1;

    /**************************
    * pull out the directions *
    **************************/
    dir1 = v1.getDirection();
    Direction dir2 = v2.getDirection();

    /***********
    * rotation *
    ***********/
    axis  = dir2.perpendicular(dir1);
    angle = dir1.angleBetween(dir2).getDegrees();

   // System.out.println("radius1="+radius1+" delta_r="+delta_r+" angle="+angle);

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public ThreeVector interpolate(PreciseDate time) {

    double hat = time.secondsAfter(time1)/duration;

   // System.out.println("hat="+hat);

    /****************************
    * interpolate the direction *
    ****************************/
    Rotation rot = new Rotation(hat*angle, axis);
    Direction dir = rot.transform(dir1);

    /*************************
    * interpolate the radius *
    *************************/
    double r = radius1 + delta_r*hat;

    return new ThreeVector(dir, r);

} // end of interpolate method

} // end of PolarInterval class