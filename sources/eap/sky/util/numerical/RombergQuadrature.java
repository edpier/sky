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
public class RombergQuadrature implements Quadrature {

int min_order;
int max_order;
int max_iterations;

/****************************************************************************
* Create a new Romberg integrator.
* @param min_order The minimum order of the polynomial to use.
* @param max_order The maximum order of the polynomial to use.
* @param max_iterations The maximum number of successive refinements
*        of the trapezoid integration. The number of function
*        evaluations is 2^(n-1)+1, where n is the number of iterations.

****************************************************************************/
public RombergQuadrature(int min_order, int max_order, int max_iterations) {

    this.min_order      = min_order;
    this.max_order      = max_order;
    this.max_iterations = max_iterations;

} // end of constructor


/****************************************************************************
*
****************************************************************************/
public ConstDataArray integrate(Function f, double from, double to,
                                DonenessTest test) throws NoConvergenceException {

    TrapezoidStepper trapezoid = new TrapezoidStepper(f, from ,to);


    NevilleTableau[] tableaus = null;
 //   NevilleTableau tableau     = new NevilleTableau(0.0);

    double x = 1.0;
    for(int iteration = 0; iteration < max_iterations; ++iteration, x*=0.25) {

System.out.println("Romberg iteration="+iteration);

        /*******************************************************
        * make another refinement of the trapezoid integration *
        *******************************************************/
        ConstDataArray y = trapezoid.step();

        /*************************************************
        * initialize the tableaus now that we know the
        * dimensionality of the function
        *************************************************/
        if(tableaus == null) {
            tableaus = new NevilleTableau[y.getValueCount()];
            for(int i=0; i< tableaus.length; ++i) {
                tableaus[i] = new NevilleTableau(0.0);
            }
        }

        /******************************************
        * add the result elements to the tableaus *
        ******************************************/
        y.start();
        for(int i=0; y.isValid(); ++i, y.next()) {
            tableaus[i].add(x, y.get());
        }

        /**********************
        * check if we're done *
        **********************/
        if(iteration >= min_order-1) {

            /*******************************************
            * it's ugly, but we copy the
            * tableus results and error estimates
            * into some arrays so we can run the
            * doneness test on them
            ********************************************/
            DataArray errors  = y.makeSimilar();
            DataArray answers = y.makeSimilar();

            errors.start();
            answers.start();

            for(int i=0; i<tableaus.length; ++i, errors.next(), answers.next()) {

                 errors.set(tableaus[i].getError() );
                answers.set(tableaus[i].getValue());
            } // end of loop over tableaus

            if(test.isDone(errors, answers)) return answers;

        } // end if we have done enough iterations

    } // end of loop over iterations

    /*************************************
    * if we get here we did not converge *
    *************************************/
    throw new NoConvergenceException();

} // end of evaluate method

} // end of RombergQuadrature class