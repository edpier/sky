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

import eap.sky.earth.*;

import java.io.*;

/***************************************************************************
* Represents the weather conditionas at a a particular point in the atmosphere.
* Specifically, this class holds pressure, temperature, and a measure
* of the water vapor content of the air - either relative humidity or
* dew point. This class can calculate the index of refraction of air,
***************************************************************************/
public class Weather implements Serializable {

/** index of refraction of air without CO2 **/
private static final double k0 = 238.0185;
private static final double k1 = 5792105.0;
private static final double k2 = 57.362;
private static final double k3 = 167917.0;

/** index of refraction of water vapor **/
private static final double[] omega = {295.235, 2.6422, -0.03238, 0.004028};

/** compressibility constants **/
private static final double a0 =  1.58123e-6; //    K/Pa
private static final double a1 = -2.9331e-8;  //     /Pa
private static final double a2 =  1.1043e-10;  //     /PaK
private static final double b0 =  5.707e-6;   //    K/Pa
private static final double b1 = -2.051e-8;   //     /Pa
private static final double c0 =  1.9898e-4;  //    K/Pa
private static final double c1 = -2.376e-6;   //     /Pa
private static final double d  =  1.83e-11;   // K^2/Pa^2
private static final double e  = -0.765e-8;   // K^2/Pa^2 (not e-82)

/** The gas constant **/
private static final double R = 8.314472; // J/mol/K

/** The molar mass of water vapor **/
private static final double molar_mass_water = 0.018015; // kg/mol

/** standard temperature and pressure **/
private static final double pressure_stp = 101325.0; // Pa
private static final double   kelvin_stp = 288.15; // K

/** Compressibility of air at STP */
private static final double compressibility_stp = 0.9995922115;

/** density of water vapor at STP **/
private static final double density_water_stp = 0.00985938; // kg/m3

/***************************************************
* 1976 standard atmosphere CO<sub>2</sub> fraction in
* micromoles per mole. (i.e. ppm).
***************************************************/
public static final double STANDARD_1976_CO2 = 375.0; // micromole/mole

/***************************************************************
* A CO<sub>2</sub> fraction more representative of an
* indoor laboratory measurement.
* This value is in micromoles per mole (i.e. ppm).
* Carbon dioxide levels
* indoors are usually higher
* people breathing. This is useful for comparing with
* laboratory measurements of the index of refraction
* fo air, but you should not use this value for astronomical
* calculations.
*****************************************************************/
public static final double INDOOR_CO2 = 450.0; // micromole/mole
// member variables
private double celsius;
private double pressure;

private WaterVapor water_vapor;


// derived quantities

private double kelvin;

/****************************************************************************
* Create a new weather object.
* @param pressure The barometric pressure  in Pa
* @param celsius The temperature in degrees Celsius
* @param water_vapor a measure of the water vapor content of the air.
*        this could be dew point, relative humidity, or some other measurement.
****************************************************************************/
public Weather(double pressure, double celsius, WaterVapor water_vapor) {

    this.pressure = pressure;
    this.celsius = celsius;
    this.water_vapor = water_vapor;

    /*******************************
    * calculate derived quantities *
    *******************************/
    kelvin = celsius + 273.15;

} // end of constructor

/***************************************************************************
* Returns the temperature in Celsius.
* @return The temperature in Celsius.
***************************************************************************/
public double getCelsiusTemperature() { return celsius; }

/***************************************************************************
* Returns the temperature in Kelvin.
* @return The temperature in Kelvin.
***************************************************************************/
public double getKelvinTemperature() { return kelvin; }

/****************************************************************************
* Returns the water vapor content of the air.
* @return the water vapor content.
****************************************************************************/
public WaterVapor getWaterVapor() { return water_vapor; }

/****************************************************************************
* Returns the relative humidity as a fraction betwen zero and one.
* Note that this may require an internal conversion from dew point.
* @return relative humidity.
****************************************************************************/
public double getRelativeHumidity() {

    return water_vapor.getRelativeHumidity(this);

}

/****************************************************************************
* Returns the barometric pressure in Pa. You divide this by 100 to get millibars
* @return Atmospheric pressure in Pa.
****************************************************************************/
public double getPressure() { return pressure; }

/***************************************************************************
* Interpolate between two weather reading. This is useful if you have a set
* of soundings throuh the atmosphere.
* @param weather Another weather reading.
* @param hat The relative weighting between this weather reading and the one
*        specified as a parameter. If this is zero this metod will return a copy
*        of this object. If it is one, this method will return a copy of the
*        argument.
* @return a weather value interpolated between this value and another.
***************************************************************************/
public Weather interpolate(Weather weather, double hat) {

    double hat1 = 1.0 - hat;

    double p = pressure * hat1 + weather.pressure * hat;
    double t = celsius  * hat1 + weather.celsius  * hat;

    /********************************************
    * limit the values to their physical minima *
    ********************************************/
    if(t< -273.15) t = 273.15;
    if(p<0.0) p =0.0;

    WaterVapor w = water_vapor.interpolate(weather.water_vapor, hat);

    return new Weather(p,t,w);

} // end of interpolate method


/****************************************************************************
* Calculates the index of refraction of air at the observatory. We use the
* Ciddor equation as described by the
* <a href="http://emtoolbox.nist.gov/Wavelength/Documentation.asp">
* NIST Engineering Metrology Toolbox</a>.
* @param wavelength Wavelength of light in nanometers
* @param water_fraction The mole fraction of water vapor in the air as
* returned by {@link #waterFraction()}.
* @param co2_fraction The molar fraction of carbon dioxide in the air.
* @param compressibility the compressibility of air giving its deviation
         from a perfect gas as returned by {@link #compressibility(double)}.
* @return the quantity gamma = n -1, where n is the index of refraction.
*         It is more convenient to use this quantity, since n is very close
*         to unity.
****************************************************************************/
public double indexOfRefraction(double wavelength, double water_fraction,
                                double co2_fraction,
                                double compressibility) {

    /****************************************
    * index of refraction of dry air at STP *
    * Note we convert the wavelength to microns
    * from nanometers because the "k" constants are in
    * units of micron^-2
    ****************************************/
    double sigma = 1e3/wavelength;
    double sigma2 = sigma * sigma;

    double gamma_stp = 1e-8*(k1/(k0-sigma2) + k3/(k2 - sigma2));

//     System.out.println("sigma2="+sigma2);
//     System.out.println("k0="+k0+" k2="+k2);

    /*********************
    * correction for CO2 *
    *********************/
    double gamma_air = gamma_stp * (1.0 + 5.34e-7*(co2_fraction - 450.0));

    /**************************************
    * index of refraction for water vapor *
    **************************************/
    double gamma_water=0.0;
    for (int i=omega.length-1; i>=0; --i) {
        gamma_water = gamma_water*sigma2 + omega[i];
    }

    gamma_water *= 1.022e-8;

//     System.out.println("gamma_water = "+gamma_water);
//     System.out.println("water_fraction="+water_fraction);

    /**************************************
    * calculate the molar mass of dry air *
    **************************************/
    double molar_mass_air = 0.0289635 + 1.2001e-8*(co2_fraction-400.0);

    /************
    * densities *
    ************/
    double density_air = (1.0-water_fraction)*pressure*molar_mass_air/
                         (compressibility *R*kelvin);

    double density_water = water_fraction * pressure * molar_mass_water/
                           (compressibility*R*kelvin);

    double density_air_stp = pressure_stp*molar_mass_air/
                             (compressibility_stp * R * kelvin_stp);

//     System.out.println("gamma_air="+gamma_air);
//
//     System.out.println("density_water=    "+density_water);
//     System.out.println("density_water_stp="+density_water_stp);


    /*******************************
    * combined index of refraction *
    *******************************/
    double gamma = (density_air  /density_air_stp  ) * gamma_air +
                   (density_water/density_water_stp) * gamma_water;

//     System.out.println("density_air="+density_air+
//                        " density_air_stp="+density_air_stp);
//     System.out.println("gamma_air="+gamma_air+" gamma_stp="+gamma_stp);

    return gamma;


} // end of refraction method

/****************************************************************************
* Calculates the compressibility of air. Compressibility is one for
* a perfect gass, but slightly less than one for a real gass whose
* component particles can interact with each other. The equation used is
* taken from the NIST EM Toolbox, but the origin is unclear.
* This is used by a number of other methods.
* @param water_fraction The mole fraction of water vapor in the air as
* returned by {@link #waterFraction()}.
* @return The compressibility of the air.
* @see #indexOfRefraction(double, double, double, double)
* @see #scaleHeight(double, double, double, double)
****************************************************************************/
public double compressibility(double water_fraction) {

    double pt = pressure/kelvin;

    double compressibility = 1.0 -
                    pt * ( a0 + a1*celsius + a2*celsius*celsius +
                          (b0 + b1*celsius) * water_fraction +
                          (c0 + c1*celsius) * water_fraction*water_fraction)
               + pt*pt * (  d +           e * water_fraction*water_fraction);

    return compressibility;

} // end of compressibility method

/****************************************************************************
* Returns the mole fraction of water vapor. This is used by a number of other
* methods.
* @return The mole fraction of water vapor.
* @see #indexOfRefraction(double, double, double, double)
* @see #compressibility(double)
* @see #scaleHeight(double, double, double, double)
****************************************************************************/
public double waterFraction() {

    return water_vapor.molarFraction(pressure, celsius);
}


/****************************************************************************
* The scale height of the atmosphere above the observatory.
* @param gravity The local acceleration of gravity in m/s<sup>2</sup>
* @param water_fraction The mole fraction of water vapor in the air as
* returned by {@link #waterFraction()}.
* @param co2_fraction The molar fraction of carbon dioxide in the air.
* @param compressibility The compressibility of air giving its deviation
*        from a perfect gas as returned by {@link #compressibility(double)}.
* @return the scale height in meters.
****************************************************************************/
public double scaleHeight(double gravity, double water_fraction,
                          double co2_fraction, double compressibility) {

//System.out.println("compressibility="+compressibility);
    /********************
    * molar mass of air *
    ********************/
    double molar_mass_dry = 0.0289635 + 1.2001e-8*(co2_fraction-400.0);

    double molar_mass = (1.0 - water_fraction) * molar_mass_dry +
                               water_fraction  * molar_mass_water;

    /***************
    * scale height *
    ***************/
    double scale_height = compressibility * R * kelvin/(gravity*molar_mass);

    return scale_height;

} // end of scaleheight method

/***********************************************************************
* Return a string representation of the weather
* @return a string representation.
***********************************************************************/
public String toString() {

    return "Weather: "+celsius+" C  "+pressure*1e-3+" kPa  "+water_vapor;

}

} // end of Weather class
