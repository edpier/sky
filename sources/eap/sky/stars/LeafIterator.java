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

package eap.sky.stars;

import java.util.*;

/***********************************************************************
*
***********************************************************************/
public class LeafIterator implements Iterator {

Cell root;

List<LeafIterator> iterators;

Object next;


/***********************************************************************
*
***********************************************************************/
public LeafIterator(Cell root) {

    this.root = root;

} // end of LeafIterator class

/***********************************************************************
*
***********************************************************************/
public boolean hasNext() {

    if(next != null) return true;
    else {
        try {
            next = next();
            return true;

        } catch(NoSuchElementException e) {
            return false;
        }
    }

} // end of hasNext method

/***********************************************************************
*
***********************************************************************/
public Object next() {

    /************************************************
    * see if we already got the next one by testing *
    ************************************************/
    if(next != null) {
        Object next = this.next;
        this.next = null;
        return next;
    }

    /*******************
    * get the next one *
    *******************/
    if(root == null) {
        /***************************************************
        * we're a leaf and we've already iterated over us *
        **************************************************/
        throw new NoSuchElementException();

    } else if(iterators == null) {
        /********************************************************
        * we havent tried this yet, so maybe the root is a leaf *
        ********************************************************/
        if(root.hasChildren()) {
            List<Cell> children = root.getChildren();
            iterators = new LinkedList<LeafIterator>();
            for(Iterator it = root.getChildren().iterator(); it.hasNext();) {
                Cell cell = (Cell)it.next();
                iterators.add(new LeafIterator(cell));
            }

        } else {
            /************************************
            * the root is a leaf, so we're done *
            ************************************/
            Cell root = this.root;
            this.root = null;
            return root;
        }
    } // end if this is the first call to next;

    while(iterators.size() >0) {

        LeafIterator it = (LeafIterator)iterators.get(0);
        if(it.hasNext()) return it.next();

        /****************************************************
        * if we get here we're done with the first iterator *
        ****************************************************/
        iterators.remove(0);

    } // end of loop until we find a valid iterator



    throw new NoSuchElementException();

} // end of next method

/***********************************************************************
*
***********************************************************************/
public void remove() {

    throw new UnsupportedOperationException();

} // end of remove method


} // end of LeafIterator class
