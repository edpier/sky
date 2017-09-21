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

package eap.sky.util.plane;

/**********************************************************************
*
**********************************************************************/
public class Gap {

int seg;
double min;
double max;

/**********************************************************************
*
**********************************************************************/
public Gap(int seg, double min, double max) {

    this.seg = seg;
    this.min = min;
    this.max = max;

    if(max < min) {
        throw new IllegalArgumentException("Negative size gap min="+min+
                                                            " max="+max);
    }

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public int getIndex() { return seg; }

/**********************************************************************
*
**********************************************************************/
public double getMin() { return min; }

/**********************************************************************
*
**********************************************************************/
public double getMax() { return max; }

/**********************************************************************
*
**********************************************************************/
public double getCenter() { return 0.5*(min+max); }

/**********************************************************************
*
**********************************************************************/
public double getWidth() { return max-min; }

} // end of Gap class
