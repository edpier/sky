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

package eap.sky.util;

import java.awt.geom.*;

/***************************************************************************
* A wrapper around an AffineTransform.
***************************************************************************/
public class IdentityMapping extends Mapping {

/***************************************************************************
*
***************************************************************************/
protected IdentityMapping() {


} // end of constructor

/***************************************************************************
*
***************************************************************************/
public Point2D map(Point2D point) {

    return point;

} // end of map method

/***************************************************************************
*
***************************************************************************/
protected Mapping createInverse() {

    return this;

} // end of createInverse method

/***************************************************************************
*
***************************************************************************/
public Mapping makeOptimizedCombinationWith(Mapping map) { return map; }

/***************************************************************************
*
***************************************************************************/
public Mapping makeOptimizedCombinationAfter(Mapping map) { return map; }

} // end of Mapping method