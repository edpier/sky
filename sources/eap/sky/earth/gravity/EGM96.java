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

package eap.sky.earth.gravity;

import java.io.*;
import java.util.*;

/***************************************************************************
* <a href="http://cddis.gsfc.nasa.gov/926/egm96/egm96.html">
* The NASA GSFC and NIMA Joint Geopotential Model</a>.
* This is the highest accuracy sperical harmonic expansion of the
* Earth's gravitational potential currently available.
* It has terms up to degree 360, and can be used to compute the geoid with
* accuracy of one meter.
* <p>
* Note this class is currently not well tested.
***************************************************************************/
public class EGM96 extends SphericalHarmonicPotential {


/***************************************************************************
* Create a new potential.
***************************************************************************/
public EGM96() {

    super(360);
    
    GM=3986004.415E+8; // m**3/s**2
    a=6378136.3; //m

    /***********************
    * locate the data file *
    ***********************/
    Class c = getClass();
    ClassLoader loader = c.getClassLoader();
//     c.getPackage();
//     String name = c.getPackage().getName();

    try {
        InputStream in = loader.getResourceAsStream("eap/sky/earth/egm96_to360.ascii");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        
        String line;
        while((line=reader.readLine())!= null) {

            StringTokenizer tokens = new StringTokenizer(line);
            
            int n = Integer.parseInt(tokens.nextToken());
            int m = Integer.parseInt(tokens.nextToken());
            
            C[n][m] = Double.parseDouble(tokens.nextToken());
            S[n][m] = Double.parseDouble(tokens.nextToken());

        } // end of loop over lines

    } catch(IOException e) { e.printStackTrace(); }







} // end of constructor

/***************************************************************************
*
***************************************************************************/
// public static void main(String[] args) {
// 
// //     LegendrePoly poly = new LegendrePoly();
// //     System.out.println(poly.evaluate(100, 100, 0));
// //     System.exit(0);
// 
//     Geoid geoid = new EGM96();
// 
//     double theta = 90.0;
//     double phi = 0.0;
// 
//     double r =6378136.3;
//     r = 6378137.0; // WGS84 a
//     r = 6356752.312; // WGS84 b
//     double potential = geoid.potential(r, theta, phi);
//    // System.out.println("potential ="+ potential);
// 
//     double potential1 = geoid.potential(r+1.0, theta, phi);
// 
//     double gravity = potential1 - potential;
// 
//     System.out.println("gravity="+gravity+" potential="+potential);
// 
// } // end of main method


} // end of EMG96 class
