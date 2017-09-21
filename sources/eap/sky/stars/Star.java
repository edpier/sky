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

import eap.sky.util.*;
import eap.sky.util.coordinates.*;

import java.io.*;
import java.util.*;


/***************************************************************************
* Represents an entry in a star catalog. Each star has a unique name,
* a position in the sky, and a set of photometric measurements.
***************************************************************************/
public class Star {

String name;
Direction dir;
Photometry photometry;

/****************************************************************************
* Create a new star. Note that this constructor calls
* {link Photometry#freeze()} on the photometry to make it read-only.
* @param name The name of this star. This name should be a unique identitfier.
* @param dir The positon of thestar in the sky
* @param photometry A collection of measurements of the brightness of the star.
***************************************************************************/
public Star(String name, Direction dir, Photometry photometry) {

    this.name = name;
    this.dir = dir;
    this.photometry = photometry;

} // end of constructor

/**************************************************************************
*
**************************************************************************/
public String getName() { return name; }

/****************************************************************************
* Returns the position fo the star in the sky. The coordinates and ephoch of this
* position are arbitrary, but shoudl be consistent and well documented
* for a particular collection of stars.
* @return The position of the star in the sky.
***************************************************************************/
public Direction getDirection() { return dir; }

/***************************************************************************
* Returns the photometry of this star.
* @return The collection of magnitudes for this star.
***************************************************************************/
public Photometry getPhotometry() { return photometry; }

/**************************************************************************
*
**************************************************************************/
public float getMagnitude(Band band) {

    return photometry.getMagnitude(band).getValue();

} // end of getMagnitude method

/**************************************************************************
*
**************************************************************************/
public int compareTo(Magnitude mag) {

    return photometry.getMagnitude(mag.getBand()).compareTo(mag);

} // end of compareTo

/*****************************************************************************
* Returns a string representation of the data for this star.
* @return A string representation of this star.
*****************************************************************************/
public String toString() {
    return "Star: "+name+
           " RA="+SexigesimalFormat.HMS.format(dir.getLongitude())+
           " Dec="+SexigesimalFormat.DMS.format(dir.getLatitude())+
           " "+photometry;
}

} // end of Star class
