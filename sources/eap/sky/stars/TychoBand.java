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
* Represents the photometry bands used in the Tycho catalogs.
* These are similar to but not quite the same as the familiar Johnson
* B and V.
* These are approximately related to The Johnson Magnitudes by
* <PRE>
*    V   =  VT -   0.090*(BT-VT)
*    B-V =         0.850*(BT-VT)
* </PRE>
* See section 1.3 of Volume 1 of "The Hipparcos and Tycho Catalogues",
* ESA SP-1200, 1997,
************************************************************************/
public class TychoBand extends Band {

/** Tycho B magnitude **/
public static Band B = new TychoBand("BT");

/** Tycho V magnitude **/
public static Band V = new TychoBand("VT");

/************************************************************************
* Create a new Tycho band.
************************************************************************/
protected TychoBand(String name) { super(name); }


} // end of TychoBand class
