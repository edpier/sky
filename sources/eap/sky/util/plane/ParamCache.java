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

package eap.sky.util.plane;

import java.io.*;

/***************************************************************************
* Stores information which a transform might find expensive to
* derive from a parameter. It is common to do multiple transforms with
* the same parameter. For example you might want to transform the positions
* of several stars with a particular rotator position.
* @see TransformParameter
***************************************************************************/
public interface ParamCache extends Serializable {

/***************************************************************************
* This method is called by
* {@link TransformParameter#setCache(PlaneTransform, ParamCache)}.
* Implementing classes should use this to perform and store some expensive
* calculation from this value.
***************************************************************************/
public void update(double value);

} // end of ParamCache interface
