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

import eap.sky.time.*;

/****************************************************************************
*
****************************************************************************/
public class Crossing {

PreciseDate time;
boolean rise;

/****************************************************************************
*
****************************************************************************/
public Crossing(PreciseDate time, boolean rise) {

    this.time = time.copy();
    this.rise = rise;

} // end of constructor

/****************************************************************************
*
****************************************************************************/
public PreciseDate getTime() { return time.copy(); }

/****************************************************************************
*
****************************************************************************/
public boolean isRise() { return rise; }

/****************************************************************************
*
****************************************************************************/
public String toString() {

    StringBuffer buffer = new StringBuffer();
    buffer.append(new PreciseDateFormat(LocalTimeSystem.getInstance()).
                  format(time));
    buffer.append(" ");
    if(rise) buffer.append("Rise");
    else     buffer.append("Set");

    return buffer.toString();

} // end of toString method

} // end of Crossing class
