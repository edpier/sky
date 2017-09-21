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

package eap.sky.time.cycles;

/*************************************************************************
*
*************************************************************************/
public class Twilight implements Comparable {

public static final Twilight ASTRONOMICAL = new Twilight(-18.0);
public static final Twilight CIVIL        = new Twilight(-12.0);
public static final Twilight NEAR_IR      = new Twilight( -8.0);
public static final Twilight RISE_SET     = new Twilight(  0.0);

String name;
private double alt_offset;

/*************************************************************************
*
*************************************************************************/
public Twilight(double alt_offset) {

    this.alt_offset = alt_offset;

} // end of constructor

/*************************************************************************
*
*************************************************************************/
public double getAltOffset() { return alt_offset; }

/*************************************************************************
*
*************************************************************************/
public boolean equals(Object o) {

    Twilight twilight = (Twilight)o;
    return alt_offset == twilight.alt_offset;

} // end of equals method

/*************************************************************************
*
*************************************************************************/
public int hashCode() { return (int)alt_offset; }

/*************************************************************************
* Compare two twilights such that the earliest one in the evening will be first
*************************************************************************/
public int compareTo(Object o) {

    Twilight twilight = (Twilight)o;

    if(     alt_offset  < twilight.alt_offset) return -1;
    else if(alt_offset == twilight.alt_offset) return  0;
    else                                       return  1;

} // end of compareTo method



} // end of Twilight class
