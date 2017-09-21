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

package eap.sky.util.numerical;

/**********************************************************************
*
**********************************************************************/
public class ConstArray2D implements ConstDataArray {

double[][] values;
int width;
int height;
int nvalues;

int i;
int j;

/**********************************************************************
*
**********************************************************************/
protected ConstArray2D(int width, int height) {

    values = new double[height][width];
    this.width = width;
    this.height = height;

    nvalues = width*height;

} // end of constructor


/**********************************************************************
*
**********************************************************************/
protected ConstArray2D(ConstArray2D array) {

    this.values = array.values;
    this.width  = array.width;
    this.height = array.height;

    nvalues = width*height;

} // end of wrapping constructor

/**********************************************************************
*
**********************************************************************/
public int getValueCount() { return nvalues; }

/**********************************************************************
*
**********************************************************************/
public void start() {

    i=0;
    j=0;

} // end of start method

/**********************************************************************
*
**********************************************************************/
public void next() {

    ++i;
    if(i>=width) {
        i=0;
        ++j;
    }

} // end of next method

/**********************************************************************
*
**********************************************************************/
public boolean isValid() { return j<height; }

/**********************************************************************
*
**********************************************************************/
public double get() { return values[j][i]; }

/**********************************************************************
*
**********************************************************************/
public DataArray copy() {

    Array2D copy = new Array2D(width, height);
    copy.copyFrom(this);
    return copy;

} // end of copy method

/**********************************************************************
*
**********************************************************************/
public DataArray makeSimilar() {

    return new Array2D(width, height);

} // end of makeSimilar method

/**********************************************************************
*
**********************************************************************/
// public String toString() {
// 
//     StringBuffer buffer = new StringBuffer("[");
//     for(int i=0; i< values.length-1; ++i) {
//         buffer.append(values[i]).append(", ");
//     }
// 
//     /*****************
//     * the last value *
//     *****************/
//     if(values.length>0) buffer.append(values[values.length-1]);
//     buffer.append("]");
// 
//     return buffer.toString();
// 
// } // end of toString method

} // end of ConstArray2D class