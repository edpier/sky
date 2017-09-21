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

/*****************************************************************************
* Thrown when there is no way to convert between a pair of time systems.
* This is a {@link RuntimeException}, so it does not need to be declared
* or caught. This is because there <em>should</em> be a way to convert
* between any two time systems> If there isn't one, it is probably due
* to an error in the code.
*****************************************************************************/
public class NoSuchConversionException extends RuntimeException {

public NoSuchConversionException(TimeSystem from, TimeSystem to) {

    super("Can't convert "+from+" to "+to);

} // end of constructor



} // end of NoSuchConversionException
