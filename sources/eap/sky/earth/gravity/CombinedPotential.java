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

import eap.sky.util.*;

/******************************************************************************
* The sum of two potentials. This is usually used to combine a centrifugal
* and gravitational potential.
******************************************************************************/
public class CombinedPotential extends Potential {

Potential pot1;
Potential pot2;

/******************************************************************************
* Create a new potential which is the sum of two others.
* Typically one is centrifugal and the other is gravitational.
* @param pot1 One potential
* @param pot2 The other potential.
******************************************************************************/
public CombinedPotential(Potential pot1, Potential pot2) {

    this.pot1 = pot1;
    this.pot2 = pot2;
}

/******************************************************************************
*
******************************************************************************/
public double potential(Direction geocentric, double r) {

    return pot1.potential(geocentric, r) +
           pot2.potential(geocentric, r);


} // end of potential method




} // end of Potential class
