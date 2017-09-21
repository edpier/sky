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

package eap.sky.util.numerical.smoothing;

/***************************************************************************
*
***************************************************************************/
public class BoxcarSmoother extends AbstractSmoother {

double sum;

/***************************************************************************
*
***************************************************************************/
public BoxcarSmoother(int nmax) {

    super(nmax);
    sum = 0.0;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
protected void addValue(double value, double removed) {

    if(!Double.isNaN(value  )) sum += value;
    if(!Double.isNaN(removed)) sum -= removed;

} // end of addValue method

/***************************************************************************
*
***************************************************************************/
public double getSmoothedValue() {

    return sum/(nmax-null_count);
    
} // end of smoothed method

} // end of BoxcarSmoother class
