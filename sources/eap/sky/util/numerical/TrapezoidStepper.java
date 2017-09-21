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

/****************************************************************************
*
****************************************************************************/
public class TrapezoidStepper {

Function function;
double from;
double to;
double width;

int iteration;
int npoints;

DataArray sum;
ConstDataArray const_sum;

/****************************************************************************
*
****************************************************************************/
public TrapezoidStepper(Function function, double from, double to) {

    this.function = function;
    this.from = from;
    this.to = to;

    width = to-from;

    iteration = 0;
    npoints = 0;

} // end of constructor

/************************************************************//**
* Perform the next refinement of the integration by halving the
* step size.
* @return The value of the integral after performing the
*          refinement.
****************************************************************/
public ConstDataArray step() {

    if(iteration ==0) {
        /******************
        * first iteration *
        ******************/
        sum = function.evaluateFunction(from).copy();
        const_sum = sum.getConstView();

        sum.plus(function.evaluateFunction(to));
        sum.times(0.5*width);

        npoints = 1;
        ++iteration;

    } else {
        /**************
        * normal case *
        **************/
        double delta = width/npoints;
        double x = from + 0.5*delta;

        /*****************************************
        * do the first point outside the loop
        * to initialize the sum
        *****************************************/
        DataArray tmp_sum = function.evaluateFunction(x).copy();
        x += delta;

        for(int i=1; i< npoints; ++i, x+=delta) {

            tmp_sum.plus(function.evaluateFunction(x));

        } // end of loop over points

        tmp_sum.times(delta);
        sum.plus(tmp_sum);
        sum.times(0.5);

        ++iteration;
        npoints *=2;

    } // end of normal case

    return const_sum;

} // end of step method

} // end of TrapezoidStepper method