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
public class ConstArray1D implements ConstDataArray {

double[] values;
int index;

/**********************************************************************
*
**********************************************************************/
protected ConstArray1D(int dimen) {

    values = new double[dimen];
    index = values.length;

} // end of constructor


/**********************************************************************
*
**********************************************************************/
protected ConstArray1D(double[] values) {

    this.values = values;
    index = values.length;

} // end of wrapping constructor

/**********************************************************************
*
**********************************************************************/
public int getValueCount() { return values.length; }

/**********************************************************************
*
**********************************************************************/
public void start() { index=0; }

/**********************************************************************
*
**********************************************************************/
public void next() { ++index; }

/**********************************************************************
*
**********************************************************************/
public boolean isValid() { return index<values.length; }

/**********************************************************************
*
**********************************************************************/
public double get() { return values[index]; }

/**********************************************************************
*
**********************************************************************/
public DataArray copy() {

    Array1D copy = new Array1D(values.length);
    copy.copyFrom(this);
    return copy;

} // end of copy method

/**********************************************************************
*
**********************************************************************/
public DataArray makeSimilar() {

    return new Array1D(values.length);

} // end of makeSimilar method

/**********************************************************************
*
**********************************************************************/
public String toString() {

    StringBuffer buffer = new StringBuffer("[");
    for(int i=0; i< values.length-1; ++i) {
        buffer.append(values[i]).append(", ");
    }

    /*****************
    * the last value *
    *****************/
    if(values.length>0) buffer.append(values[values.length-1]);
    buffer.append("]");

    return buffer.toString();

} // end of toString method

} // end of Array1D class