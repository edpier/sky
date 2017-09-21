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
* The Johnson photometry system. These are the familiar U, B, V, R, I, etc.
* Currently, we only define static variables for  B and V.
************************************************************************/
public class Johnson extends Band {

/** The Johnson B (blue) band. **/
public static Band B = new Johnson("B");

/** The Johnson V (visible) band **/
public static Band V = new Johnson("V");


public static Band J = new Johnson("J");
public static Band H = new Johnson("H");
public static Band K = new Johnson("K");
public static Band Ks = new Johnson("Ks");
public static Band L = new Johnson("L");
public static Band M = new Johnson("M");

/************************************************************************
* Construct a new Johnson photometry band.
************************************************************************/
protected Johnson(String name) { super(name); }


} // end of Johnson class
