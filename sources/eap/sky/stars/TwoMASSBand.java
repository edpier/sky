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

package eap.sky.stars;



/************************************************************************
* Filters used by the 2MASS catalog.
* The Filter data comes from
* http://www.ipac.caltech.edu/2mass/releases/allsky/doc/sec6_4a.html
************************************************************************/
public class TwoMASSBand extends Band {

public static Band J  = new TwoMASSBand("J", 1.235e-6, 0.162e-6,
                                        3.129e-9*0.162 *1.235e-6/(PLANCK*LIGHT));

public static Band H  = new TwoMASSBand("H", 1.662e-6, 0.251e-6,
                                        1.133e-9* 0.251 * 1.662e-6/(PLANCK*LIGHT));
                                        
public static Band Ks = new TwoMASSBand("Ks", 2.159e-6, 0.262e-6,
                                        4.283e-10 * 0.262 * 2.159e-6/(PLANCK*LIGHT));

/************************************************************************
* Construct a new 2MASS photometry band.
************************************************************************/
protected TwoMASSBand(String name, double wavelength, double width, double zero) {
    super(name, wavelength, width, zero );
}


} // end of TwoMASSBand class