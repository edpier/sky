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
public class BasicDonenessTest implements DonenessTest {

double accuracy;

/**********************************************************************
*
**********************************************************************/
public BasicDonenessTest(double accuracy) {

    this.accuracy = accuracy;

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public boolean isDone(ConstDataArray error, ConstDataArray scale) {

    for(error.start(),     scale.start();
        error.isValid() && scale.isValid();
        error.next(),      scale.next()    ) {

        if(Math.abs(error.get()) >= accuracy*(Math.abs(scale.get())+1.0)) {
            return false;
        }
    } // end of loop over values

    return true;

} // end of areClose method

} // end of BasicClosenessTest interface