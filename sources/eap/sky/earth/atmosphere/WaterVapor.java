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

import java.io.*;

/***************************************************************************
* A generic representation of the amount of water vapor in the air.
* This is useful for calculating the index of refraction of air.
***************************************************************************/
public abstract class WaterVapor implements Serializable {

/** saturation constants **/
private static final double K1 =  1.16705214528e3;
private static final double K2 = -7.24213167032e5;
private static final double K3 = -1.70738469401e1;
private static final double K4 =  1.20208247025e4;
private static final double K5 = -3.23255503223e6;
private static final double K6 =  1.49151086135e1;
private static final double K7 = -4.82326573616e3;
private static final double K8 =  4.05113405421e5;
private static final double K9 = -2.38555575678e-1;
private static final double K10 = 6.50175348448e2;
private static final double A1 = -13.928169;
private static final double A2 = 34.7078238;

boolean ice;

/***************************************************************************
* Create a new water vapor object.
* @param ice true if the ground is covered with ice or snow. This is used to
* calculate the saturation water vapor pressure.
* @see #saturation(double)
***************************************************************************/
public WaterVapor(boolean ice) {

    this.ice = ice;
}

/***************************************************************************
*
***************************************************************************/
public boolean isIcy() { return ice; }

/***************************************************************************
* return the relative humidity as a value between 0 and 1.
* @param weather - auxilliary data in case we need to do a conversion e.g.
*        from dew point.
***************************************************************************/
public abstract double getRelativeHumidity(Weather weather);

/***************************************************************************
* Interpolate between two water vapor values. The interpolation should be linear.
* @param water_vapor Another water vapor reading.
* @param hat The fractional weighting of the two values. If hat=0,
* then this method returns this object or a copy of it. If hat=1,
* this method returns the argument or a copy of it.
* @return The value between this one and the given one.
***************************************************************************/
public abstract WaterVapor interpolate(WaterVapor water_vapor, double hat);

/***************************************************************************
* Calculates the molar fraction of water vapor.
* @param pressure The atmospheric pressure in Pascals.
* @param celsius The air temperature in Celsius.
* @return The molar fraction of water vapor.
***************************************************************************/
public abstract double molarFraction(double pressure, double celsius);

/***************************************************************************
* Calculates the enhancement factor for the molar fraction of water vapor.
* This is used to calculate the index of refraction of air.
* @param pressure Pressure in Pascsals
* @param celsius Temperaure in degrees Celsius.
* @return The enhancement factor for the molar fraction of water vapor
***************************************************************************/
protected double enhancement(double pressure, double celsius) {

    double enhancement = 1.00062 + 3.14e-8*pressure
                                 + 5.60e-7* celsius*celsius;

    return enhancement;

} // end of enhancement method

/***************************************************************************
* Calculates the saturation water vapor pressure. This uses the
* The International Association for the Properties of Water and Steam
* (IAPWS) formula, as given by
* <a href="http://emtoolbox.nist.gov/Wavelength/Documentation.asp">
* The NIST EM Toolbox</a>. The saturation vapor pressure is needed to
* calculate the molar fraction of water vapor from either relative humidity
* or dew point.
* @param celsius temperature in celsius
* @return The saturation water vapor pressure in Pascals
***************************************************************************/
public double saturation(double celsius) {

    double kelvin = celsius + 273.15;

    double saturation;

    if(ice) {
        /*******************
        * over ice or snow *
        *******************/
        double Theta = kelvin/273.16;
        double root_Theta = Math.sqrt(Theta);
        double Y = A1*(1.0 - 1./(Theta*root_Theta            )) +
                   A2*(1.0 - 1./(Theta*Math.sqrt(root_Theta) ));

        saturation =  611.657*Math.exp(Y);

    } else {
        /**************
        * no ice/snow *
        **************/
        double Omega = kelvin + K9/(kelvin-K10);
        double Omega2 = Omega*Omega;

        double A =    Omega2 + K1*Omega + K2;
        double B = K3*Omega2 + K4*Omega + K5;
        double C = K6*Omega2 + K7*Omega + K8;

        double X = -B + Math.sqrt(B*B - 4*A*C);

        saturation = 2.0*C/X;
        saturation *= saturation;
        saturation *= saturation;
        saturation *= 1e6;
    }

    return saturation;


} // end of saturation method



} // end of WaterVapor class
