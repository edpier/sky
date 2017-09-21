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
public class TrapezoidQuadrature implements Quadrature {

int max_iterations;

/****************************************************************************
*
****************************************************************************/
public TrapezoidQuadrature(int max_iterations) {

    this.max_iterations = max_iterations;

} // end of constructor


/****************************************************************************
*
****************************************************************************/
public TrapezoidQuadrature() {

    this(25);

} // end of constructor

/****************************************************************************
*
****************************************************************************/
public ConstDataArray integrate(Function f, double from, double to,
                                DonenessTest test) throws NoConvergenceException {

    TrapezoidStepper stepper = new TrapezoidStepper(f, from, to);

    /**********************
    * take the first step *
    **********************/
    DataArray last_result = stepper.step().copy();

    /**********
    * iterate *
    **********/
    for(int iteration=1; iteration< max_iterations; ++iteration) {

        /*********************
        * take the next step *
        *********************/
        ConstDataArray result = stepper.step();
        System.out.println(iteration+" "+result);

        /**************************************************
        * see if we're done. The error is the difference
        * between this result and the last
        **************************************************/
        last_result.minus(result);
        if(test.isDone(last_result, result)) return result;

        /***************************
        * remember the last result *
        ***************************/
        last_result.copyFrom(result);

    } // end of loop over iterations

    throw new NoConvergenceException();

} // end of evaluate method

} // end of TrapezoidQuadrature class