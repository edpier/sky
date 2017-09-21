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

import java.util.*;
import java.io.*;

/***********************************************************************
* Represents a collection of magnitudes in different bands.
***********************************************************************/
public interface Photometry {

/***********************************************************************
* Returns the number of magnitude values in the collection.
***********************************************************************/
public int getCount();


/***********************************************************************
* Returns the magnitude in the given band.
* @param band The band for the requested magnitude
* @return the magnitude in the given band or null if this collection does
*         not have a magnitude in that band.
***********************************************************************/
public Magnitude getMagnitude(Band band);

/***********************************************************************
* Returns all the bands in this collection.
* @return An unmodifiable Set of all the bands in this collection.
***********************************************************************/
public Collection<Band> getBands();

/***********************************************************************
* Returns all the magnitudes in this collection.
* @return an unmodifiable collection of all the magnitudes in this collection.
***********************************************************************/
public Collection<Magnitude> getMagnitudes();

} // end of Photometry class
