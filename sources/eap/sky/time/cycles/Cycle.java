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

package eap.sky.time.cycles;

/************************************************************************
*
************************************************************************/
public class Cycle {


Cadence cadence;
int index;

Night first;
Night last;

/************************************************************************
*
************************************************************************/
public Cycle(Cadence cadence, int index) {

    this.cadence = cadence;
    this.index = index;

} // end of constructor

/************************************************************************
*
************************************************************************/
public Cycle(Cadence cadence, Night night) {

    this(cadence, cadence.findIndex(night));

}

/************************************************************************
*
************************************************************************/
public Night getFirstNight() {

    if(first == null) first = cadence.firstNight(index);

    return first;


} // end of getFirstNight method

/************************************************************************
*
************************************************************************/
public Night getLastNight() {

    if(last == null) {
        last = getFirstNight().plus(cadence.getPeriod()-1);
    }

    return last;


} // end of getFirstNight method

/************************************************************************
*
************************************************************************/
public boolean contains(Night night) {

    return getFirstNight().compareTo(night) <=0 &&
            getLastNight().compareTo(night) >=0;

} // end of contains method

/************************************************************************
*
************************************************************************/
public int nightOfCycle(Night night) {

    return night.daysAfter(getFirstNight());

} // end of nightOfCycle method

} // end of Cycle class