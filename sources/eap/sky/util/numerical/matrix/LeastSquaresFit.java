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

package eap.sky.util.numerical.matrix;

import eap.sky.util.numerical.*;

/************************************************************************
* A tool For doing linear least squares fits.
* Note that "linear" means fitting a linear combination of functions.
* The functions themselves may be nonlinear
* (e.g. the terms of a polynominal). Here is an example of how to use
* this class:
* <code>
* // assume we have arrays x, y, and sigma, giving the points and errors
* // and we want to fit a parabola
*
* int npoints = 10; // number of data points
* int nterms = 3; // number of functions
* LeastSquaresFit fit = new LeastSquaresFit(npoints, nterms);
* for(int i=0; i&lt;npoints; ++i) {
*
*     fit.addError(sigma[i]) // set the standard deviation (optional).
*
*     fit.addFuctionValue(1.0); // constant term
*     fit.addFuctionValue(x[i]); // linear term
*     fit.addFuctionValue(x[i]*x[i]); // quadratic term
*
*     fit.addMeasuredValue(y[i]);
* } // end of loop over points
*
* // compute the fit. the resulting polynomial is
* // terms[0] + terms[1]*x + terms[2]*x*x
* double[] terms = fit.fit();
* </code>
*
************************************************************************/
public class LeastSquaresFit {

Matrix matrix;
double[] rhs;
double[][] array;

int point;
int index;

double error;

/************************************************************************
* @param points The number of points which will be fit.
* @param nterms The number of functions which will be fit to the data.
************************************************************************/
public LeastSquaresFit(int npoints, int nterms) {

    /***************************
    * create the design matrix *
    ***************************/
    matrix = new Matrix(npoints, nterms);
    rhs  = new double[npoints];

    array = matrix.getData();

    index = 0;
    point = 0;

    error = 1.0;

} // end of constructor

/************************************************************************
*
************************************************************************/
public void addError(double error) {

    this.error = error;

} // end of addError method

/************************************************************************
*
************************************************************************/
public void addFunctionValue(double value) {

    array[point][index++] = value/error;

} // end of addFunction value

/************************************************************************
*
************************************************************************/
public void addMeasuredValue(double value) {

    rhs[point++] = value/error;
    index = 0;
    error = 1.0;

} // end of addMeasuredValue method

/************************************************************************
*
************************************************************************/
public double[] fit() throws NoConvergenceException {

    /************************************
    * do a singular value decomposition *
    ************************************/
    SingularValueDecomposition svd = new SingularValueDecomposition(matrix);

    /****************************************
    * this accounts for roundoff error and
    * possible degeneracies in the terms
    ****************************************/
    svd.clipSingularValues(1e-10);

    /******************************************
    * this step solves for the best fit norms *
    ******************************************/
    return svd.solve(rhs);

} // end of fit method

} // end of Fit class
