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

package eap.sky.util.numerical;

import java.util.*;

/****************************************************************
* Represents a table of NevilleElement objects used for
* interpolation or extrapolation. Neville's Algorithm is described
* section 3.1 of Numerical Recipies. This is an entirely different
* implementation from their "polint" function.
* This implementation is geared toward doing Romberg integration.
* @see RombergQuadrature
****************************************************************/
public class NevilleTableau {

double x;

List<NevilleElement> last_row;
List<NevilleElement> next_row;

NevilleElement apex;

/**********************************************************************
* Create a new NevilleTableau
* @param x The X value to which we wish to interpolate or extrapolate
**********************************************************************/
public NevilleTableau(double x) {

    this.x = x;

    last_row = new ArrayList<NevilleElement>();
    next_row = new ArrayList<NevilleElement>();

    apex = null;

} // end of constructor

/****************************************************************
* Add a new data point for the interpolation. Points should (probably)
* added sequentially (i.e. with x increasing for each one.
* @param x The X value of the new point
* @param y the Y value of the new point
****************************************************************/
public void add(double x, double y) {

    NevilleElement parent2 = new NevilleElement(x, y);
    next_row.add(parent2);

    /*****************************************
    * loop over the elements in the last row *
    *****************************************/
    for(NevilleElement parent1 : last_row) {

        parent2 = new NevilleElement(parent1, parent2, this.x);
        next_row.add(parent2);

    } // end of loop over rows

    /**********************************************
    * save the element at the apex of the tableau *
    **********************************************/
    apex = parent2;

    /******************************************
    * discard the old row of elements and
    * and swap the new and old rows
    *****************************************/
    last_row.clear();

    List<NevilleElement> dummy = last_row;
    last_row = next_row;
    next_row = dummy;

} // end of add method

/************************************************************//**
* Delete the first-entered point from the interpolation.
****************************************************************/
public void remove() {

    last_row.remove(last_row.size()-1);
    int size = last_row.size();
    if(size == 0) apex = null;
    else          apex = last_row.get(size-1);

} // end of remove method


/****************************************************************
* Return the interpolated value. Note this is calculated by the
* sum of the differences between elements along the bottom rows
* of the tableau. This is appropriate for extrapolation beyond
* the end of the tableau. It may be subject to additional roundoff
* error if this is not the case.
****************************************************************/
double getValue() {

    if(apex == null) return 0;
    else             return apex.getValue();

} // end of getValue method


/****************************************************************
* return an estimate of the interpolation error. This is the
* difference between the interpolated value and the interpolated
* value if we did not have the first point in the list. Note that
* this is appropriate if the point to which we are interpolating
* is located in the second half of the list.
****************************************************************/
public double getError() {

    if(apex == null) return 0;
    else             return apex.getDiff2();

} // end of getError method



} // end of NevilleTableau class