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

package eap.sky.time;


/****************************************************************************
* Represents a conversion from one time system to another which first converts
* to an intermediate time system. Since a CompositeConversion is a Conversion,
* an unlimited number of conversions may be chained together.
****************************************************************************/
public class CompositeConversion extends Conversion {

private Conversion first;
private Conversion second;

private TimeSystem intermediate;

CachedTimeSystem cache;

/****************************************************************************
* Create a new conversion which represents the sucessive application
* of two other conversions.
* @param first The first conversion to apply
* @param second The second conversion to apply
****************************************************************************/
public CompositeConversion(Conversion first, Conversion second) {

    super(first.getFromSystem(), second.getToSystem());

    this.first  = first;
    this.second = second;

    this.intermediate = first.getToSystem();
    if(!intermediate.equals(second.getFromSystem())) {
        throw new IllegalArgumentException(first+" and "+second+
                                " do not use the same intermediate system");
    }

} // end of constructor

/*****************************************************************************
*
*****************************************************************************/
public void setCache(CachedTimeSystem cache) {

    this.cache = cache;
    first.setCache(cache);
    second.setCache(cache);

} // end of setCache method

/*****************************************************************************
* Returns the sum of the number of steps for each of the component conversions.
*****************************************************************************/
public int getSteps() { return first.getSteps() + second.getSteps(); }

/*****************************************************************************
* Performs two conversions in sucession, discarding the intermediate date.
*****************************************************************************/
public void convert(PreciseDate from, PreciseDate to) {

    PreciseDate intermediate = first.getToSystem().createDate();

     first.convert(from, intermediate);
    second.convert(intermediate,   to);

    if(cache != null) {
        cache.addDate(intermediate);
        cache.addDate(to);
    }

} // end of convert method

/*****************************************************************************
*
*****************************************************************************/
public String toString() {

    return from_system+" to "+intermediate+" to "+to_system;
}

} // end of CompositeConvertsion class
