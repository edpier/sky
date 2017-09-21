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

/*********************************************************************
*
*********************************************************************/
public abstract class AbstractPhotometry implements Photometry {

/***********************************************************************
* Returns a string representation of this collection.
* @return A string representation of all the magnitudfes in this collection.
***********************************************************************/
public String toString() {

    StringBuffer buffer = new StringBuffer();
    for(Iterator it = getMagnitudes().iterator(); it.hasNext(); ) {
        Magnitude mag = (Magnitude)it.next();

        buffer.append(mag.toString());
        if(it.hasNext()) buffer.append(", ");

    } // end of loop over magnitudes

    return buffer.toString();

} // end of toString method

} // end of AbstractPhotometry