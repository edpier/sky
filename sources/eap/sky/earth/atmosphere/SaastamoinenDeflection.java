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

package eap.sky.earth.atmosphere;

import eap.sky.util.*;
import eap.sky.earth.*;

/**************************************************************************
* Represents an expansion of refraction in odd powers of the tangent
* of the apparent zenith angle. Such an expansion was originally made
* in <A href="http://physics.ucsd.edu/~tmurphy/apollo/doc/Saas_1.pdf">
* Saastamoinen 1972 Bulletin Geodesique 105, 279</a>.
* This class uses a modified treatment from
* Chambers 2005, in print in Astronomy in the Age of the Next Generation
* of Large Telescopes.
* <p>
* We currently use two terms, which gives miliarcsecond agreement with
* the fiducial Pulkovo refraction values for zenith angles up to 60 degrees.
* The agreement with Pulkovo diverges rapidly beyond 60 degrees.
* @see eap.sky.util.DeflectionTransform
* @see eap.sky.earth.atmosphere.monitors.SaastamoinenRefraction
**************************************************************************/
public class SaastamoinenDeflection implements Deflection {

private Weather weather;
private double co2_fraction;
private double wavelength;
private Tropopause tropopause;
private Observatory observatory;

double[] coef;

/**************************************************************************
* Construct a new refraction model. The constructor calculates the
* expansion coeficients, so that they do not need to be recalculated
* for each zenith angle.
* @param weather The weather condifitons at the observatory
* @param co2_fraction The fraction of carbon dioxide in the air, in
*        micromoles/mole.
* @param wavelength The wavelength of light under consideration in nanometers.
* @param tropopause The conditions at the tropause above the observatory.
*        If this is null, then the tropopause temrs in the expansion are
*        ignored
* @param observatory The location of the observatory and the local gravity.
**************************************************************************/
public SaastamoinenDeflection(Weather weather, double co2_fraction,
                              double wavelength, Tropopause tropopause,
                              Observatory observatory) {

//System.out.println("SaastamoinenDeflection: constructor start");

    /********************************
    * molar fraction of water vapor *
    ********************************/
    double water_fraction = weather.waterFraction();

  //  System.out.println("water_fraction="+water_fraction);

    /*************************
    * compressibility of air *
    *************************/
    double compressibility = weather.compressibility(water_fraction);

    /************************************************
    * index of refraction of air at the observatory *
    ************************************************/
    double gamma = weather.indexOfRefraction(wavelength, water_fraction,
                                             co2_fraction, compressibility);

//    System.out.println("gamma="+gamma);

    /*********************************
    * scale height of the atmosphere *
    *********************************/
    double scale_height = weather.scaleHeight(observatory.getGravity(),
                                              water_fraction,
                                              co2_fraction, compressibility);

//      System.out.println("gravity="+observatory.getGravity());
//      System.out.println("scale_height="+scale_height);

    /********************
    * tropospheric term *
    ********************/
    double delta = 0;
    if(tropopause != null) {
        double kelvin = weather.getKelvinTemperature();

        delta = tropopause.refractionTerm(scale_height, gamma,
                                          kelvin, observatory);
    }

   // System.out.println("delta="+delta);

    /**************************************
    * calculate the refraction in radians *
    **************************************/
    double hr = scale_height/observatory.getLocation().getLength();

//System.out.println("mine: gamma="+gamma+" hr="+hr);

    coef = new double[2];
    coef[0] = gamma*(1.0 - hr);
    coef[1] = gamma*(0.5*gamma - hr) + delta;

//     System.out.println( "R1="+3600.0*Math.toDegrees(coef[0])+
//                        " R3="+3600.0*Math.toDegrees(coef[1])+
//                        " delta="+3600.0*Math.toDegrees(delta));

} // end of constructor

/******************************************************************************
* Returns a new array containing the expension coeficients.
* @return The expansion coeficients in radians.
******************************************************************************/
public double[] getCoeficients() {

    double[] array = new double[coef.length];
    System.arraycopy(coef, 0, array, 0, coef.length);

    return array;

} // end of getCoeficients


/**************************************************************************
* Calculates the refraction for a given apparent zenith angle.
* @param ground_angle the apparent zenith angle as viewed by an observer
*        on the ground.
* @return The refraction
**************************************************************************/
public Angle calculateDeflection(Angle ground_angle) {

    /***************************************
    * take the tangent of the zenith angle *
    ***************************************/
    double tanz = ground_angle.getTan();
    if(tanz > 11.4 || tanz<0.0) {
       // System.out.println("capping refraction at z=85");
        tanz = 11.4;
    }

  //  if(tanz>100.0) tanz = 100.0;

    double tanz2 = tanz*tanz;

    /*******************
    * evaluate the sum *
    *******************/
    double sum = coef[coef.length-1];
    for(int i=coef.length-2; i>=0; --i) {

        sum = tanz2 * sum + coef[i];

    }

    sum *= tanz;

//     System.out.println("refraction"+
//                        " = "+Math.toDegrees(sum)*3600.0+" arcsec");

    return Angle.createFromRadians(sum);

} // end of calculateRefraction method

} // end of SaastamoinenDeflection class
