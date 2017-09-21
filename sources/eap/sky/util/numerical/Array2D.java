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
public class Array2D extends ConstArray2D implements DataArray {

/**********************************************************************
*
**********************************************************************/
public Array2D(int width, int height) {

    super(width, height);

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public double[][] getArray() { return values; }

/**********************************************************************
*
**********************************************************************/
public void set(double value) { values[j][i] = value; }

/**********************************************************************
*
**********************************************************************/
public ConstDataArray getConstView() {

    return new ConstArray2D(this);

} // end of getConstView method

/**********************************************************************
*
**********************************************************************/
public void plus(ConstDataArray array) {

    for(start(), array.start();
        isValid() && array.isValid();
        next(), array.next()) {

        set(get() + array.get());
    }
    
} // end of plus method

/**********************************************************************
*
**********************************************************************/
public void minus(ConstDataArray array) {

    for(start(), array.start();
        isValid() && array.isValid();
        next(), array.next()) {

        set(get() - array.get());
    }

} // end of minus method

/**********************************************************************
*
**********************************************************************/
public void times(double factor) {

    for(start(); isValid(); next()) {

        set(get()*factor);
    }

} // end of times method

/**********************************************************************
*
**********************************************************************/
public void copyFrom(ConstDataArray array) {

    ConstArray2D cast = (ConstArray2D)array;
    for(int j=0; j<height; ++j) {
        System.arraycopy(cast.values[j], 0, values[j], 0, width);
    }

} // end of copyFrom method

} // end of Array2D class