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

package eap.sky.image;

import java.io.*;

/*********************************************************************
*
*********************************************************************/
public class Detection implements Comparable<Detection>, Serializable {

String name;
double x;
double y;
double mag;
double fwhm;

/*********************************************************************
*
*********************************************************************/
public Detection(String name, double x, double y,
                 double mag, double fwhm) {

    this.name = name;
    this.x    = x;
    this.y    = y;
    this.mag  = mag;
    this.fwhm = fwhm;

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public Detection(String name, double x, double y, double mag) {

    this(name, x, y, mag, Double.NaN);

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public String getName() { return name; }

/*********************************************************************
*
*********************************************************************/
public double getX() { return x; }

/*********************************************************************
*
*********************************************************************/
public double getY() { return y; }

/*********************************************************************
*
*********************************************************************/
public double getMagnitude() { return mag; }

/*********************************************************************
*
*********************************************************************/
public double getFWHM() { return fwhm; }

/*********************************************************************
*
*********************************************************************/
public double distanceSquared(Detection det) {

    double dx = x - det.x;
    double dy = y - det.y;

    return dx*dx + dy*dy;

} // end of distanceSquared method

/*********************************************************************
*
*********************************************************************/
public boolean equals(Object o) {

    Detection det = (Detection)o;

    return mag == det.mag &&
             x == det.x   &&
             y == det.y;

} // end of equals method

/*********************************************************************
*
*********************************************************************/
public int compareTo(Detection det) {

    if(     mag < det.mag) return -1;
    else if(mag > det.mag) return  1;

    if(     y<det.y) return -1;
    else if(y>det.y) return  1;

    if(     x<det.x) return -1;
    else if(x>det.x) return  1;

    else return 0;

} // end of compareTo method

} // end of Detection class