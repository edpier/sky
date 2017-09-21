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

/***************************************************************************
* Represents a transform composed of a pair of individual transforms
* applied in  sucession. This provides a default generic way of representing
* a pair of sucessive transforms when it is not possible to mathematically
* merge them. Note that you can chain an arbitrary number of transforms
* together this way.
* @see Transform#transform(Direction)
***************************************************************************/
public class CompositeTransform extends Transform {

Transform first;
Transform second;

/***************************************************************************
* Create a new composite transform.
* @param first The first transform to apply
* @param second The second transform to apply.
***************************************************************************/
public CompositeTransform(Transform first, Transform second) {

    this.first = first;
    this.second = second;


} // end of constructor

/***************************************************************************
*
***************************************************************************/
public Transform getFirstTransform() { return first; }

/***************************************************************************
*
***************************************************************************/
public Transform getSecondTransform() { return second; }

/***************************************************************************
* Applies the two component transforms in sucession.
* @param dir the original direction
* @return the transformed direction.
***************************************************************************/
public Direction transform(Direction dir) {

    return second.transform(first.transform(dir));


} // end of CompositeTransform class

/***************************************************************************
* Inverts this transform.
* @return a new CompositeTransform with the inverses of the component
* transforms in the opposite order. If either component transform is
* not invertable, this method returns null.
***************************************************************************/
public Transform invert() {

    /****************************************************************
    * This is not a perfect way to catch if one of the component
    * transforms is not invertable, because the inversions methods
    * might throw a NullPointerException for other reasons.
    * However, that's unlikely enough, and harmless enough
    * that we won't worry about it.
    ****************************************************************/
    try {  return new CompositeTransform(second.invert(), first.invert()); }
    catch(NullPointerException e) { return null; }

} // end of invert method

/*****************************************************************************
*
*****************************************************************************/
public Transform combineWith(Transform trans) {

    Transform trial = second.combineWith(trans);
    if(trial instanceof CompositeTransform) {
        /*********************
        * unable to simplify *
        *********************/
        return new CompositeTransform(this, trans);
    } else {
        return new CompositeTransform(first, trial);
    }


} // end of combineWith method

/*****************************************************************************
*
*****************************************************************************/
public String toString() {

    return first+" "+second;

} // end of toString method

} // end of CompositeTransform class
