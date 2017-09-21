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

package eap.sky.earth;

import eap.sky.util.numerical.*;

import java.util.*;

/**********************************************************************
* This is a cheesy implementation just to see how much better things
* get if I cut the precession calculation time.
**********************************************************************/
public class CachedPrecessionModel extends PrecessionModel {

PrecessionModel model;
double interval;

Map<Integer, Number> x_cache;
Map<Integer, Number> y_cache;
Map<Integer, Number> s_cache;

/**********************************************************************
*
**********************************************************************/
public CachedPrecessionModel(PrecessionModel model) {

    this.model = model;
    this.interval = 0.01/365.0/24.0; // once per hour

    x_cache = new HashMap<Integer, Number>();
    y_cache = new HashMap<Integer, Number>();
    s_cache = new HashMap<Integer, Number>();

} // end of constructor

/***************************************************************************
* Calculate the precession/nutation X coordinate in radians.
* Subclasses must implement this for their particular model.
* @param args A set of fundamental arguments.
***************************************************************************/
public double calculateX(TidalArguments args) {

    double t = args.getJulianCenturiesTDB();
    Integer index = new Integer((int)Math.floor(t/interval));

    Number value = (Number)x_cache.get(index);
    if(value == null) {
 //   System.out.println("calculating precession X");
        value = new Double(model.calculateX(args));
        x_cache.put(index, value);
    }


    return value.doubleValue();

} // end of calculateX method

/***************************************************************************
* Calculate the precession/nutation Y coordinate in radians.
* Subclasses must implement this for their particular model.
* @param args A set of fundamental arguments.
***************************************************************************/
public double calculateY(TidalArguments args) {

    double t = args.getJulianCenturiesTDB();
    Integer index = new Integer((int)Math.floor(t/interval));

    Number value = (Number)y_cache.get(index);
    if(value == null) {
        value = new Double(model.calculateY(args));
        y_cache.put(index, value);
    }


    return value.doubleValue();

} // end of calculateY method

/***************************************************************************
* Calculate the precession/nutation S coordinate in radians.
* Subclasses must implement this for their particular model.
* @param args A set of fundamental arguments.
* @param x The X value calculated by {@link #calculateX(TidalArguments)}
* @param y The Y value calculated by {@link #calculateY(TidalArguments)}
***************************************************************************/
public double calculateS(TidalArguments args, double x, double y) {

    double t = args.getJulianCenturiesTDB();
    Integer index = new Integer((int)Math.floor(t/interval));

    Number value = (Number)s_cache.get(index);
    if(value == null) {
        value = new Double(model.calculateS(args, x, y));
        s_cache.put(index, value);
    }


    return value.doubleValue();

} // end of calculateS method

} // end of CachedPrecessionModel class
