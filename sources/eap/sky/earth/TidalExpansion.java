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

import java.io.*;

/*************************************************************************
* A generic form of an expansion in terms of harmonics of the fundamental
* arguments. There's isn't much use for this class, except to conceptually
* tie together the precession and diurnal forms of the expansion.
* @see TidalArguments
**************************************************************************/
public abstract class TidalExpansion implements Serializable {


/***************************************************************************
* Create a new expansion.
***************************************************************************/
protected TidalExpansion() {}

/***************************************************************************
* Calculate the sum.
***************************************************************************/
public abstract double evaluate(TidalArguments args);

} // end of TidalExpansion
