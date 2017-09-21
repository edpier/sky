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

/**************************************************************************
*
**************************************************************************/
public class StarIterationList {

Band band;
Star star;
Iterator<Star> it;

/**************************************************************************
*
**************************************************************************/
public StarIterationList(List<Star> list, Band band) {

    this.band = band;

    it = list.iterator();
    step();

} // end of constructor


/**************************************************************************
*
**************************************************************************/
int compareTo(StarIterationList list) {

    return           star.getPhotometry().getMagnitude(band).compareTo(
           list.getStar().getPhotometry().getMagnitude(band));

} // end of compareTo method

/**************************************************************************
*
**************************************************************************/
public Star getStar() { return star; }

/**************************************************************************
*
**************************************************************************/
public void step() {

    if(it.hasNext()) star = it.next();
    else             star = null;

} // end of next method


} // end of StarIterationList class