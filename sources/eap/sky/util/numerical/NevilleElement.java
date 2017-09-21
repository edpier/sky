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

/****************************************************************
* Represents one element in a Neville's Algorithm tableau for
* interpolation or extrapolation. Neville's Algorithm is described
* section 3.1 of Numerical Recipies. This is an entirely different
* implementation from their "polint" function.
* @see NevilleTableau
****************************************************************/
class NevilleElement {

double x1;
double x2;

double diff1;
double diff2;

double value;

/****************************************************************
* Create a new root element. This represents one of the tabulated
* data values that we are using to interpolate.
* @param x The "X" coordinates of a tabulated point
* @param value The value of the function being interpolate at x.
****************************************************************/
NevilleElement(double x, double value) {

    x1 = x;
    x2 = x;

    diff1 = value;
    diff2 = value;

    this.value = value;

} // end of constructor

/****************************************************************
* Create a new element from its two parents. Note this class does
* not take ownership of either parent class. It does not even save
* the pointers, so you are free to delete them after calling this
* constructor.
* @param parent1 The first (upper) parent.
* @param parent2 The second (lower) parent.
* @param x The x coordinate to which we wish to interpolate.
****************************************************************/
public NevilleElement(NevilleElement parent1, NevilleElement parent2,
                      double x) {

    x1 = parent1.getX1();
    x2 = parent2.getX2();

    double thing = (parent2.getDiff1() -
                    parent1.getDiff2())/(x1-x2);

    diff1 = (x1-x)*thing;
    diff2 = (x2-x)*thing;

    /**************************************************************
    * note this is appropriate if we are extrapolating to a
    * value beyond the last interpolation point in the sequence.
    **************************************************************/
    value = parent2.getValue() + diff2;

} // end of constructor

/****************************************************************
* Returns the "upper" X value. This is the value of X for the root
* element found by tracing back through all our upper ancestors.
****************************************************************/
double getX1() { return x1; }

/****************************************************************
* Returns the "lower" X value. This is the value of X for the root
* element found by tracing back through all our lower ancestors.
****************************************************************/
double getX2() { return x2; }


/****************************************************************
* Returns the difference between this element's value and it's upper
* parent's value.
****************************************************************/
double getDiff1() { return diff1; }


/****************************************************************
* Returns the difference between this element's value and it's lower
* parent's value.
****************************************************************/
double getDiff2() { return diff2; }


/****************************************************************
* return the value of this element. The value is the estimates
* interpolation using all the ancester root elements.
****************************************************************/
double getValue() { return value; }

} // end of NevilleElement class