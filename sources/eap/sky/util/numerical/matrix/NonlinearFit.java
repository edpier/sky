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

/*************************************************************************
*
*************************************************************************/
public class NonlinearFit {

Matrix matrix;
double[] rhs;
double chi2;
double[] param;


Matrix old_matrix;
double[] old_rhs;
double old_chi2;
double[] old_param;

int nparam;

double[] deriv;
int point;
int index;





double lambda;




double[] coeff;
int ncoeff;

double[][] covar;
double[][] alpha;
double[] atry;
double[] beta;
double[] da;
double[][] oneda;

/*************************************************************************
*
*************************************************************************/
public NonlinearFit(double[] guess) {

    nparam = guess.length;
    this.param = new double[nparam];
    System.arraycopy(guess, 0, this.param, 0, nparam);

    old_param = new double[nparam];

    matrix = new Matrix(nparam, nparam);
    rhs = new double[nparam];

    old_matrix = new Matrix(nparam, nparam);
    old_rhs = new double[nparam];

    deriv = new double[nparam];

    index = 0;
    point = 0;

    chi2 = 0.0;
    old_chi2 = Double.NaN;
    lambda = 0.001;

} // end of constructor

/*************************************************************************
*
*************************************************************************/
public double getParameter(int i) { return param[i]; }

/*************************************************************************
*
*************************************************************************/
public double getParameterChange(int i) {

    return Math.abs(param[i] - old_param[i]);
}

/*************************************************************************
*
*************************************************************************/
public double getChiSquared() { return chi2; }

/*************************************************************************
*
*************************************************************************/
private void reset() {

    /********************************
    * init chi2 to zero
    ********************************/
 //   old_chi2 = chi2;
    chi2=0.0;

    /*********************************
    * set the matrix and rhs to zero *
    *********************************/
    double[][] matrix = this.matrix.getData();
    for(int i=0; i<nparam; ++i) {
        rhs[i] = 0;
        for(int j=0; j<nparam; ++j) {
            matrix[i][j] = 0;
        }
    }

    /********************
    * reset the indices *
    ********************/
    point = 0;
    index = 0;

} // end of reset method

/*************************************************************************
*
*************************************************************************/
public void addErrorDerivative(double deriv) {

    if(Double.isNaN(deriv)) {
        System.out.println("derivative is not a number");
        System.exit(1);
    }

    this.deriv[index++] = deriv;

} // end of addErrorDerivative method


/*************************************************************************
*
*************************************************************************/
public void addError(double error) {

    /********************************
    * end of derivative information *
    ********************************/
    double[][] matrix = this.matrix.getData();
    for(int i=0; i< nparam; ++i) {
        rhs[i] -= error * deriv[i];

        for(int j=i; j<nparam; ++j) {

            matrix[i][j] += deriv[i]*deriv[j];
        }
    }

    /*************************
    * accumulate chi-squared *
    *************************/
    chi2 += error*error;
   // System.out.println("    chi2="+chi2);

    /****************************
    * reset the parameter index *
    ****************************/
    index = 0;

} // end of addError method

/*************************************************************************
*
*************************************************************************/
private void endOfData() {

    /*******************************************
    * fill in the symmetric half of the matrix *
    *******************************************/
    double[][] matrix = this.matrix.getData();
    for(int i=0; i< nparam; ++i) {
        for(int j=0; j<i; ++j) {
            matrix[i][j] = matrix[j][j];
        }
    }

    /************************
    * reset the point index *
    ************************/
    point = 0;


} // end of endOfData method

/*************************************************************************
*
*************************************************************************/
private void swap() {

    {
    Matrix dummy = matrix;
    matrix = old_matrix;
    old_matrix = dummy;
    }

    {
    double[] dummy = rhs;
    rhs = old_rhs;
    old_rhs = dummy;
    }

    {
    double dummy = chi2;
    chi2 = old_chi2;
    old_chi2 = dummy;
    }

    {
    double[] dummy = param;
    param = old_param;
    old_param = dummy;
    }

} // end of swap method

/*************************************************************************
*
*************************************************************************/
private void doMarquardt() {

    double[][] matrix = this.matrix.getData();

    double factor = 1.0+lambda;
    for(int j=0;j<nparam;j++) {

        matrix[j][j] *= factor;
    }

} // end of doMarquardt method

/*************************************************************************
*
*************************************************************************/
public void step() {

    endOfData();

    System.out.println("at "+param[0]+" "+
                             param[1]+" "+
                             param[2]+" "+
                             param[3]+" "+
                             param[4]+" chi2="+chi2);

    /*********************************************************
    * adjust lambda according to how we did on our last step *
    *********************************************************/
    if(Double.isNaN(old_chi2)) {
        /*************************
        * this is the first step *
        *************************/
        lambda = 0.001;
      //  lambda=100.0;
        System.out.println("    first");


    } else if(old_chi2 > chi2) {
        /****************
        * we got better *
        ****************/
        lambda *= 0.1;
        System.out.println("    better");

    } else {
        /************************************************
        * we got worse or the same
        * increase lambda and go back to the old params
        ************************************************/
        lambda *= 10.0;

        swap();
        System.out.println("    worse");

    }



   // System.out.println(old_chi2+" "+chi2+" lambda="+lambda);


    /***************************************
    * apply the Levinberg-Marquardt factor *
    ***************************************/
    doMarquardt();

    /*************************************************
    * solve the matrix equation to get our next step *
    *************************************************/
    double[] step = null;
    try {
        LUDecomposition lu = new LUDecomposition(matrix);
        step = lu.solve(rhs);
    } catch(SingularMatrixException e) {
        step = new double[nparam];
        chi2 = 0.0;
        System.out.println("singular");
        System.exit(1);
    }



    /*******************************************
    * take the step, saving the old parameter
    * values in case we got worse
    *******************************************/
    swap();
    for(int i=0; i<nparam; ++i) {
        param[i] = old_param[i] + step[i];
    }

    reset();




} // end of step method


} // end of NonlinearFit class
