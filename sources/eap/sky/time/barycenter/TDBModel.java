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

package eap.sky.time.barycenter;

import eap.sky.time.*;

import java.io.*;

/**************************************************************************
* Represents a model for the motion of objects in the solar system which
* can be used to calculate the difference in time between the surface of
* the Earth and the Solar System barycenter.
**************************************************************************/
public abstract class TDBModel implements Serializable {


/**************************************************************************
* Calculate the offsets of TDB time from TT time.
* @param tdb The TDB time at which to calculate the offset.
**************************************************************************/
public abstract double getTDBminusTT(PreciseDate tdb);

/***************************************************************************
* A Utility method for checking if a time uses this class as it's model.
* @param tdb The time to be checked.
* @throws ClassCastException if the time is not in TDB
* @throws IllegalArgumentException if the time is in TDB, but its TDB system
*         does not use this model of the Solar System.
***************************************************************************/
protected void checkCompatibility(PreciseDate tdb) {

    if(! ((TDBSystem)tdb.getTimeSystem()).getTDBModel().equals(this)) {

        throw new IllegalArgumentException(tdb+
                                           " uses a different model from "+this);
    }

} // end of checkCompatibility method

/*****************************************************************************
* Checks if two models are the same. This method just compares classes.
* a subclass with changeable parameters should override this method.
* @param o The model to which we will compare
* @return true if the argument is the same class as this one.
*****************************************************************************/
public boolean equals(Object o) {

    return o.getClass() == getClass();

} // end of equals method


} // end of TDBModel
