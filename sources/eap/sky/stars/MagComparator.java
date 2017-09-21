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

import eap.sky.stars.*;

import java.util.*;

/***************************************************************************
* Compares the magnitudes of two stars. Sorting with this comparator
* arranges the brightest stars first.
***************************************************************************/
public class MagComparator implements Comparator<Star> {

private Band band;

/***************************************************************************
* Create a new comparator for a particular filter.
***************************************************************************/
public MagComparator(Band band) {

    this.band = band;
}

/*****************************************************************************
* Compares the magnitudes of two stars in the band of this comparator.
* If a star has no magnitude data in the given band, it is considered dimmer
* than a star with magnitude data. Two stars with no magnitude data in the
* band are considered equal.
* @param star1 A {@link Star}
* @param star2 Another {@link Star}
* @return 1 if the first star is brighter than the second, 0 if the stars have
* the same magnitude, and -1 otherwise.
* @throws ClassCastException if either argument is not a {@link Star}.
*****************************************************************************/
public int compare(Star star1, Star star2) {

    Photometry phot1 = star1.getPhotometry();
    Photometry phot2 = star2.getPhotometry();

    Magnitude mag1 = star1.getPhotometry().getMagnitude(band);
    Magnitude mag2 = star2.getPhotometry().getMagnitude(band);

    /*******************************************
    * treat no data as dimmer than having data *
    *******************************************/
    if(mag1 == null && mag2 == null) return 0;
    if(mag1 == null)                return 1;
    if(mag2 == null)                return -1;

    /*************************
    * compare the magnitudes *
    *************************/
    return mag1.compareTo(mag2);

} // end of compare method


} // end of MagComparator class
