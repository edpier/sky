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
public class Scalar implements DataArray {

double value;
boolean valid;


/**********************************************************************
*
**********************************************************************/
public Scalar(double value) {

    this.value = value;
    valid = false;
}

/**********************************************************************
*
**********************************************************************/
public Scalar() {

    this(0.0);

}

/**********************************************************************
*
**********************************************************************/
public int getValueCount() { return 1; }

/**********************************************************************
*
**********************************************************************/
public void start() { valid = true; }

/**********************************************************************
*
**********************************************************************/
public void next() { valid = false; }

/**********************************************************************
*
**********************************************************************/
public boolean isValid() { return valid; }

/**********************************************************************
*
**********************************************************************/
public double get() { return value; }


/**********************************************************************
*
**********************************************************************/
public DataArray copy() { return new Scalar(value); }


/**********************************************************************
*
**********************************************************************/
public DataArray makeSimilar() { return new Scalar(); }

/**********************************************************************
*
**********************************************************************/
public String toString() { return ""+value; }

/**********************************************************************
*
**********************************************************************/
public void set(double value) { this.value = value; }


/**********************************************************************
*
**********************************************************************/
public ConstDataArray getConstView() { return new ConstScalar(); }

/**********************************************************************
*
**********************************************************************/
public void plus(ConstDataArray array) { value += array.get(); }

/**********************************************************************
*
**********************************************************************/
public void minus(ConstDataArray array) { value -= array.get(); }


/**********************************************************************
*
**********************************************************************/
public void times(double factor) { value *= factor; }


/**********************************************************************
*
**********************************************************************/
public void copyFrom(ConstDataArray array) { value = array.get(); }

/**********************************************************************
*
**********************************************************************/
private class ConstScalar implements ConstDataArray {

/**********************************************************************
*
**********************************************************************/
public int getValueCount() { return 1; }

/**********************************************************************
*
**********************************************************************/
public void start() { valid = true; }

/**********************************************************************
*
**********************************************************************/
public void next() { valid = false; }

/**********************************************************************
*
**********************************************************************/
public boolean isValid() { return valid; }

/**********************************************************************
*
**********************************************************************/
public double get() { return value; }

/**********************************************************************
*
**********************************************************************/
public String toString() { return ""+value; }


/**********************************************************************
*
**********************************************************************/
public DataArray copy() { return new Scalar(value); }


/**********************************************************************
*
**********************************************************************/
public DataArray makeSimilar() { return new Scalar(); }

} // end of ConstScalar inner class

} // end of DataArray interface