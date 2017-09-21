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

import java.util.*;

/**********************************************************************
*
**********************************************************************/
public abstract class AbstractSmoother implements Smoother {

protected int nmax;
protected List<Double> list;
protected int null_count;


/****************************************************************************
*
****************************************************************************/
public AbstractSmoother(int nmax) {

    this.nmax = nmax;
    list = new LinkedList<Double>();

    for(int i=0; i< nmax; ++i) {
        list.add(Double.NaN);
    }

    null_count = nmax;


} // end of constructor

/****************************************************************************
*
****************************************************************************/
public void addValue(double value) {

    list.add(0, value);
    if(Double.isNaN(value)) ++null_count;

    double removed = ((Number)list.remove(nmax)).doubleValue();
    if(Double.isNaN(removed)) --null_count;

    addValue(value, removed);

} // end of addValue method

/****************************************************************************
*
****************************************************************************/
protected abstract void addValue(double value, double removed);

/****************************************************************************
*
****************************************************************************/
public abstract double getSmoothedValue();

} // end of Smoother class
