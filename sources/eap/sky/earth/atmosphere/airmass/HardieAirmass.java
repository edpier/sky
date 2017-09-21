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

package eap.sky.earth.atmosphere.airmass;

/***********************************************************************
* The Hardie Airmass formula.
* Hardie, R. H. 1962. In Astronomical Techniques. Hiltner, W. A., ed. Chicago:
* University of Chicago Press, 184?. LCCN 62009113.
* Note the SLALIB software package also uses the same formula.
***********************************************************************/
public class HardieAirmass extends AirmassFormula {

/*************************************************************************
*
*************************************************************************/
public double calculateAirmass(double alt) {

    double z = Math.toRadians(90.0-alt);

    double s = 1.0/(Math.cos(Math.min(1.52,z)))-1.0;

   // System.out.println("s="+s);

    return 1.0 + s*(0.9981833
               - s*(0.002875
               + s*(0.0008083)));

} // end of calculateAirMass method

/*********************************************************************
*
*********************************************************************/
public double calculateAltitude(double airmass) {

    double a = -0.002875;
    double b = 0.9981833;

    double s = 0;

    while(true) {
        double c = 1.0 - airmass - 0.0008083*s*s*s;

        double q = -0.5*(b+Math.sqrt(b*b - 4.0*a*c));
        double next_s = c/q;
      //  System.out.println("s="+s);

        if(Math.abs(next_s - s)< 1e-6) {
            return 90.0 - Math.toDegrees(Math.acos(1.0/(next_s+1.0)));
        }
        s = next_s;

    } // end of loop over iterations

} // end of calculateAltitude method


} // end of HardieAirmass class
