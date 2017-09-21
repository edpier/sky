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

package eap.sky.test;

import eap.sky.util.numerical.*;

/**********************************************************************
*
**********************************************************************/
public class TestFunction implements Function {

Scalar scalar;
ConstDataArray result;

/**********************************************************************
*
**********************************************************************/
public TestFunction() {

    scalar = new Scalar();
    result = scalar.getConstView();

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public ConstDataArray evaluateFunction(double x) {

    scalar.set(Math.exp(x));
    return result;

} // end of evaluateFunction method

} // end of TestFunction class