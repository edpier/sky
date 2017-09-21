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

package eap.sky.earth;

import eap.sky.time.*;

import java.io.*;
import java.util.*;

/***************************************************************************
* The highest accuracy precession/nutation model currently endorsed by the IAU.
* See Chapter 5 of
* <A href="http://maia.usno.navy.mil/conv2003.html">IERS Technical Note 32</a>
* for a descripton of this model. It is expressed as a
* {@link PrecessionExpansion} in the standard precession quantities
* X, Y, and S. The coeficient tables used in these expansions are located
* as {@link ClassLoader} resources in the directory eap/sky/earth/iau2000a.
*
***************************************************************************/
public class IAU2000APrecession extends PrecessionModel {

private static PrecessionModel instance;

PrecessionExpansion x_constants;
PrecessionExpansion y_constants;
PrecessionExpansion s_constants;

private static final String directory="eap/sky/earth/iau2000a/";

/****************************************************************************
* Create a new instance, reading the expansion tables as resources.
* This method is private, since we only need one instance of this class.
* Use {@link #getInstance()} instead.
* @throws IOException if there is trouble reading the tables, but this
* should never happen.
****************************************************************************/
private IAU2000APrecession() throws IOException {

    /***************************
    * initialize the constants *
    ***************************/
    ClassLoader loader = getClass().getClassLoader();

    x_constants = new PrecessionExpansion(
                  loader.getResourceAsStream(directory+"tab5.2a.txt"));

    y_constants = new PrecessionExpansion(
                  loader.getResourceAsStream(directory+"tab5.2b.txt"));

    s_constants = new PrecessionExpansion(
                  loader.getResourceAsStream(directory+"tab5.2c.txt"));


} // end of constructor

/****************************************************************************
* Returns an instance of this class. This method will always return the
* same instance.
* @return The one and only instance of this class.
****************************************************************************/
public static PrecessionModel getInstance() {

    /*****************************************************************
    * create the instance. This shouldn't ever throw an IOException,
    * but we need to catch it just in case
    *****************************************************************/
    try { if(instance == null) instance = new IAU2000APrecession(); }
    catch(IOException e) {e.printStackTrace(); }

    return instance;

} // end of getInstance


/***************************************************************************
* Evaluate the expansion for the standard precession X value.
* @param arg The fundamental tidal aruments for which to evaluate the expansion.
* @return X in radians.
***************************************************************************/
public double calculateX(TidalArguments arg) {

    return x_constants.evaluate(arg);

} // end of calculateX method

/***************************************************************************
* Evaluate the expansion for the standard precession Y value.
* @param arg The fundamental tidal aruments for which to evaluate the expansion.
* @return Y in radians.
***************************************************************************/
public double calculateY(TidalArguments arg) {


    return y_constants.evaluate(arg);

} // end of calculateY method

/*****************************************************************************
* Evaluate the expansion for the standard precession S value.
* @param arg The fundamental tidal aruments for which to evaluate the expansion.
* @param x X as calaulated by {@link #calculateX(TidalArguments)}
* @param y Y as calaulated by {@link #calculateY(TidalArguments)}
* @return Y in radians.
*****************************************************************************/
public double calculateS(TidalArguments arg, double x, double y) {

    /**************************************************
    * the S series is in terms of s + xy/2,
    * since that converges faster than just plain s
    **************************************************/
    return s_constants.evaluate(arg) - 0.5*x*y;

} // end of calculateS method

} // end of IAU2000APrecession class
