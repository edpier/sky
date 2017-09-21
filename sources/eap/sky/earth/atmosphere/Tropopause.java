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
* The conditions at the Tropopause. The Troposphere is the layer of the
* atmosphere closest to the ground. It is a region where the temperature
* declines roughly linearly with altitude. The Tropopause is the top of
* Troposphere. It is marked by a sharp increase in temperature.
* <p>
* This class is useful for calculations which assume a simple model for
* the troposphere in which the temperature varies exactly linearly with
* altitide and has a sharp well defined boundary at the tropopause.
* In particularly the Saastamoinen refraction approximation uses this model
* to calculate certain contributions to its high order terms.
* @see SaastamoinenDeflection
***************************************************************************/
public class Tropopause implements Serializable {

/**********************************************************************
* Values for the 1976 standard atmosphere
**********************************************************************/
public static final Tropopause STANDARD_1976
                                    = new Tropopause(11000.0, 216.6, -0.0065);

/*********************************************
* height of tropopause above center of earth *
*********************************************/
private double height;

/*******************************************
* temperature at the tropopause in Kelvins *
*******************************************/
private double kelvin;

/***************************
* lapse rate (a.k.a. beta) *
***************************/
private double lapse;

/***************************************************************************
* Create a new object.
* @param height The height of the tropopause above the center of the Earth
*        in meters.
* @param kelvin The temperature at the tropopause in Kelvins.
* @param lapse The lapse rate, i.e. the The rate at which the temperature
*        changes with height in the troposphere, in K/m. Note this is a negative
*        number.
***************************************************************************/
public Tropopause(double height, double kelvin, double lapse) {

    this.height = height;
    this.kelvin = kelvin;
    this.lapse  = lapse;

} // end of constructor;

/***************************************************************************
* Returns the height of the tropopause above the center of the Earth.
* @return The height of the tropopause above the center of the Earth
*        in meters.
***************************************************************************/
public double getHeight() { return height; }

/***************************************************************************
* Returns the temperature of the tropopause
* @return The temperature at the tropopause in Kelvins.
***************************************************************************/
public double getKelvinTemperature() { return kelvin; }

/***************************************************************************
* Returns the lapse rate.
* @return The lapse rate, i.e. the The rate at which the temperature
*        changes with height in the troposphere, in K/m. Note this is a negative
*        number.
***************************************************************************/
public double getLapseRate() { return lapse; }

/***************************************************************************
* Calculates a particular term in the Saastamoinen refraction expansion.
* The calculation is Taken from Chambers (2005).
* @param scale_height The scale height of the atmosphere at the observatory.
* @param gamma_ground The index of refraction of air minus one at the observatory.
* @param kelvin_ground The temperature at the observatory in Kelvins.
* @param observatory The geographic position of the observatory. 
* @see SaastamoinenDeflection
***************************************************************************/
public double refractionTerm(double scale_height, double gamma_ground,
                             double kelvin_ground, Observatory observatory) {

    /*****************************************
    * get some properties of the observatory *
    *****************************************/
    double radius_ground = observatory.getLocation().getLength();
    double height_ground = observatory.getHeight();


    /****************************************
    * index of refraction at the tropopause *
    ****************************************/
    double dist = height - height_ground;

//    System.out.println("dist="+dist);
    double gamma_trop = gamma_ground * Math.exp(-kelvin_ground * dist/
                                                (kelvin        * scale_height));
// System.out.println();
// System.out.println("dist="+dist);

    /******************************************
    * The temperature of the free troposphere *
    ******************************************/
    double kelvin_free = kelvin - lapse*dist;

    /**********************************************
    * index of refraction of the free troposphere *
    **********************************************/
    double gamma_free = gamma_trop*Math.pow(kelvin_free/kelvin,
                                     -kelvin_ground/(scale_height*lapse)-1.0);

// System.out.println();
// System.out.println("gamma_ground="+gamma_ground);
// System.out.println("gamma_trop="+gamma_trop);
// System.out.println("kelvin_free="+kelvin_free+" kelvin="+kelvin);
// System.out.println("gamma_free="+gamma_free);
// 
// System.out.println("t1/h1/beta="+kelvin_ground/(scale_height*lapse));
// System.out.println("exponent="+(-kelvin_ground/(scale_height*lapse)-1.0));
// System.out.println("kelvin_free/kelvin="+kelvin_free/kelvin);
// System.out.println();
// 
// System.out.println("kelvin_ground="+kelvin_ground);
// System.out.println("scale_height="+scale_height);
// System.out.println("lapse="+lapse);
// System.out.println();
//
// System.out.println("scale_height/radius_ground="+(scale_height/radius_ground));
// System.out.println("scale_height*lapse/kelvin_ground="+
//                    (scale_height*lapse/kelvin_ground));
    /**********************
    * the tropopause term *
    **********************/
    double thing = scale_height/(radius_ground*kelvin_ground);

    double delta = 5.0 * thing*thing *
                   ((gamma_free * kelvin_free * kelvin_free -
                     gamma_trop * kelvin * kelvin  )/
                    (1.0 - scale_height*lapse/kelvin_ground)
                    + gamma_trop*kelvin*kelvin);

// System.out.println("delta1c="+5.0 * thing*thing);
// System.out.println("deltapt="+(gamma_free * kelvin_free * kelvin_free -
//                      gamma_trop * kelvin * kelvin  ) );
// System.out.println("delta1pb="+(1.0 - scale_height*lapse/kelvin_ground) );
// 
// System.out.println("gamma_free="+gamma_free);
// System.out.println("kelvin_free="+kelvin_free);
// System.out.println("radius ground="+radius_ground);
// System.out.println();

// System.out.println("second factor="+((gamma_free * kelvin_free * kelvin_free -
//                      gamma_trop * kelvin * kelvin  )/
//                     (1.0 - scale_height*lapse/kelvin_ground)
//                     + gamma_trop*kelvin*kelvin));
// 
//    System.out.println("gamma_trop*kelvin*kelvin="+gamma_trop*kelvin*kelvin);
// 
//     System.out.println("thing="+thing+" delta="+delta);

    return delta;


} // end of refractionTerm method


} // end of Tropopause class
