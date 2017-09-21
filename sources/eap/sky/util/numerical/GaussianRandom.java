package eap.sky.util.numerical;

import java.util.*;

/******************************************************************
*
******************************************************************/
public class GaussianRandom {

Random random;

double next_value;
boolean have_next;

/******************************************************************
*
******************************************************************/
public GaussianRandom(Random random) {

    this.random = random;
    have_next = false;

} // end of constructor

/******************************************************************
*
******************************************************************/
public GaussianRandom() {

    this(new Random());

} // end of default constructor

/******************************************************************
*
******************************************************************/
public double next() {

    /**********************************************************
    * we generate two values at a time,
    * so check if we have a leftover value from the last call
    **********************************************************/
    if(have_next) {
        have_next = false;
        return next_value;
    }

    /***************************************
    * if we get here we need to calculate
    * a new pair of values
    ***************************************/
    double v1;
    double v2;
    double r2;
    do {
        v1 = 2.0*random.nextDouble()-1.0;
        v2 = 2.0*random.nextDouble()-1.0;
        r2 = v1*v1 + v2*v2;

    } while(r2 >=1.0 || r2 == 0.0);

    double factor = Math.sqrt(-2.0*Math.log(r2)/r2);

    /*************************
    * save one of the values *
    *************************/
    next_value = v1*factor;
    have_next = true;

    /***********************
    * and return the other *
    ***********************/
    return v2*factor;

} // end of next method

} // end of GaussianRandom class