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

/**************************************************************************
* A transform which inverts the Y axis. This is commonly used to switch between
* look up and look down representations. This transform is so simple it
* only needs one instantiation.
**************************************************************************/
public class FlipY extends Transform {

private static final FlipY instance = new FlipY();

/**************************************************************************
* Create a new transform.
**************************************************************************/
private FlipY() {}

/**************************************************************************
* Returns the only instance of this class.
* @return The only instance of this class.
**************************************************************************/
public static FlipY getInstance() { return instance; }

/**************************************************************************
* Applies the transform.
* @return a direction with it's Y axis the negative of the original.
**************************************************************************/
public Direction transform(Direction dir) {

    return new Direction(dir.getX(), -dir.getY(), dir.getZ());

}

/**************************************************************************
* Returns the inverse transform. Note this transform is its own inverse.
* @return This transform.
**************************************************************************/
public Transform invert() { return this; }


} // end of FlipY class
