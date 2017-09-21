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
public class Array1D extends ConstArray1D implements DataArray {

/**********************************************************************
*
**********************************************************************/
public Array1D(int dimen) {

    super(dimen);

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public void set(double value) { values[index] = value; }

/**********************************************************************
*
**********************************************************************/
public ConstDataArray getConstView() {

    return new ConstArray1D(values);

} // end of getConstView method

/**********************************************************************
*
**********************************************************************/
public void plus(ConstDataArray array) {

    array.start();
    for(int i=0; i< values.length; ++i, array.next()) {
        values[i] += array.get();
    }
    
} // end of plus method

/**********************************************************************
*
**********************************************************************/
public void minus(ConstDataArray array) {

    array.start();
    for(int i=0; i< values.length; ++i, array.next()) {
        values[i] -= array.get();
    }
    
} // end of minus method

/**********************************************************************
*
**********************************************************************/
public void times(double factor) {

    for(int i=0; i< values.length; ++i) {
        values[i] *=factor;
    }

} // end of times method

/**********************************************************************
*
**********************************************************************/
public void copyFrom(ConstDataArray array) {

    ConstArray1D cast = (ConstArray1D)array;
    System.arraycopy(cast.values, 0, values, 0, values.length);

} // end of copyFrom method

} // end of Array1D class