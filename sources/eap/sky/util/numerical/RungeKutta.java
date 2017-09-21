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

/******************************************************************************
*
******************************************************************************/
public class RungeKutta {


private int order;
private double[]   indep_coef;
private double[][] depen_coef;
private double[]  result_coef;
private double[]   error_coef;


/***************************************************************************
*
***************************************************************************/
public RungeKutta() {

    setCoeficients();

}

/***************************************************************************
* initialize the Runge-Kutta Coeficients
***************************************************************************/
public void setRungeKutta4Coeficients() {

order=4;

indep_coef=new double[order];
indep_coef[0]=0.0;
indep_coef[1]=0.5;
indep_coef[2]=0.5;
indep_coef[3]=1.0;

depen_coef= new double[order][order];
depen_coef[0][0]=0.0;
depen_coef[0][1]=0.0;
depen_coef[0][2]=0.0;
depen_coef[0][3]=0.0;

depen_coef[1][0]=0.5;
depen_coef[1][1]=0.0;
depen_coef[1][2]=0.0;
depen_coef[1][3]=0.0;

depen_coef[2][0]=0.0;
depen_coef[2][1]=0.5;
depen_coef[2][2]=0.0;
depen_coef[2][3]=0.0;

depen_coef[3][0]=0.0;
depen_coef[3][1]=0.0;
depen_coef[3][2]=0.1;
depen_coef[3][3]=0.0;

result_coef=new double[order];
result_coef[0]=1./6.;
result_coef[1]=1./3.;
result_coef[2]=1./3.;
result_coef[3]=1./6.;

error_coef=new double[order];
error_coef[0]=0.0;
error_coef[1]=0.0;
error_coef[2]=0.0;
error_coef[3]=0.0;

} // end of setCoeficients method

/***************************************************************************
* initialize the Runge-Kutta Coeficients
***************************************************************************/
public void setCoeficients() {

order=6;

indep_coef=new double[order];
indep_coef[0]=0.0;
indep_coef[1]=1./5.;
indep_coef[2]=3./10.;
indep_coef[3]=3./5.;
indep_coef[4]=1.0;
indep_coef[5]=7./8.;

depen_coef= new double[order][order];
depen_coef[0][0]=0.0;
depen_coef[0][1]=0.0;
depen_coef[0][2]=0.0;
depen_coef[0][3]=0.0;
depen_coef[0][4]=0.0;
depen_coef[0][5]=0.0;

depen_coef[1][0]=1./5.;
depen_coef[1][1]=0.0;
depen_coef[1][2]=0.0;
depen_coef[1][3]=0.0;
depen_coef[1][4]=0.0;
depen_coef[1][5]=0.0;

depen_coef[2][0]=3./40.;
depen_coef[2][1]=9./40.;
depen_coef[2][2]=0.0;
depen_coef[2][3]=0.0;
depen_coef[2][4]=0.0;
depen_coef[2][5]=0.0;

depen_coef[3][0]=3./10.;
depen_coef[3][1]=-9./10.;
depen_coef[3][2]=6./5.;
depen_coef[3][3]=0.0;
depen_coef[3][4]=0.0;
depen_coef[3][5]=0.0;

depen_coef[4][0]=-11./54.;
depen_coef[4][1]=5./2.;
depen_coef[4][2]=-70./27.;
depen_coef[4][3]=35./27.;
depen_coef[4][4]=0.0;
depen_coef[4][5]=0.0;

depen_coef[5][0]=1631./55296.;
depen_coef[5][1]=175./512.;
depen_coef[5][2]=575./13825.;
depen_coef[5][3]=44275./110592;
depen_coef[5][4]=253./4096.;
depen_coef[5][5]=0.0;


result_coef=new double[order];
result_coef[0]=37./378.;
result_coef[1]=0.;
result_coef[2]=250./621.;
result_coef[3]=125./594.;
result_coef[4]=0.;
result_coef[5]=512./1771.;

error_coef=new double[order];
error_coef[0]=result_coef[0] - 2825./27648.;
error_coef[1]=result_coef[1] - 0. ;
error_coef[2]=result_coef[2] - 18575./48384.;
error_coef[3]=result_coef[3] - 13525./55296.;
error_coef[4]=result_coef[4] - 277./14336. ;
error_coef[5]=result_coef[5] - 1./4. ;


} // end of setCoeficients method




/***********************************************************************
* make one runge kutta step
***********************************************************************/
public State tryAdvance(State state, DifferentialEquation eq, double x) {


    double h = x - state.getX();

    int dimen = state.getDimension();

    /**************************
    * take intermediate steps *
    **************************/
    State[] trial = new State[order];
    trial[0] = state;
    for(int i=1; i<order; ++i) {

        double trial_x = state.getX() + h * indep_coef[i];

        double[] trial_y = new double[dimen];
        state.getY(trial_y);

        for(int j=0; j<i; ++j) {

            for(int index=0; index<dimen; ++index) {

                trial_y[index] += trial[j].getY(index) * depen_coef[i][j] * h;
            }
        }

        trial[i] = new State(trial_x, trial_y);

    } // end of loop over orders



    /***********************
    * get the final result *
    ***********************/
    double[] y     = new double[dimen];
    double[] error = new double[dimen];

    state.getY(y);

    /*************************
    * loop over trial points *
    *************************/
    for(int j=0; j<order; ++j) {

        /***********************************************************
        * use the differential equation to compute the derivatives *
        ***********************************************************/
        double[] dydx = new double[dimen];
        eq.dydx(trial[j], dydx);

        for(int index=0; index<dimen; ++index) {

            y[    index] += dydx[index] * result_coef[j] * h;
            error[index] += dydx[index] *  error_coef[j] * h;
        }
    }

    return new State(x, y, error);

} // end of try_advance method




/**************************************************************************
* advance the state
**************************************************************************/
public State advance(State initial, DifferentialEquation eq, double x,
                     double tolerance) {

    /*************
    * initialize *
    *************/
    double step = x - initial.getX();


    while(initial.getX() < x) {

        /*******************************
        * make sure we don't overshoot *
        *******************************/
        double remaining = x - initial.getX();
        if(step > remaining) step = remaining;

        /*************
        * try a step *
        *************/
        State trial = tryAdvance(initial,eq, initial.getX() + step);
        double error = Math.abs(eq.error(trial));
        
      //  System.out.println("step="+step+" error `="+error);
        if(error < tolerance) {
            /**********************
            * we made a good step *
            **********************/
            initial = trial;
            step = Math.pow(tolerance/error, 0.2);
        } else {
            /*********************
            * try a shorter step *
            *********************/

            step *= Math.pow(tolerance/error, 0.2)*.9;
           // System.out.println("shorter step="+step);
        }


    } // end of loop over trials

    return initial;

} // end of advance method





} // end of RungeKutta class
