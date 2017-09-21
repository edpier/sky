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
public class Cadence {

/**************************************************************************
* a weekly cadence with the first night of each cycle falling on a sunday *
**************************************************************************/
public static final Cadence WEEK = new Cadence(new Night(54101), 7);

Night night0;
int mjd0;
int period;

/************************************************************************
*
************************************************************************/
public Cadence(int period) {

    this(new Night(0), period);

} // end of constructor with arbitrry phase

/************************************************************************
*
************************************************************************/
public Cadence(Night night0, int period) {

    this.night0 = night0;
    this.period = period;

    mjd0 = night0.getMJDAtStart();

} // end of constructor

/************************************************************************
*
************************************************************************/
public int findIndex(Night night) {

    return (night.getMJDAtStart() - mjd0)/period;

} // end of findIndex method

/************************************************************************
*
************************************************************************/
public Night firstNight(int index) {

    return night0.plus(index*period);

} // end of firstNight method

/************************************************************************
*
************************************************************************/
public int getPeriod() { return period; }

/************************************************************************
*
************************************************************************/
// not a good idea, cuz indeces in Cycuels would be different.
// public boolean equals(Object o) {
// 
//     Cadence cadence = (Cacence)o;
//     if(period != cadence.period) return false;
// 
//     return (mjd0 - cadence.mjd0)%period == 0;
// 
// } // end of equals method

} // end of Cadence class