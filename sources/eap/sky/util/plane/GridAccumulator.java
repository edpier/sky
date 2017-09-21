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

import java.util.*;

/************************************************************************
*
************************************************************************/
public class GridAccumulator {

Map<GridIndex, PlaneSegment> segments;

/************************************************************************
*
************************************************************************/
public GridAccumulator() {

    segments = new HashMap<GridIndex, PlaneSegment>();

} // end of constructor

/************************************************************************
*
************************************************************************/
public void add(int x, int y, PlaneSegment seg) {

    segments.put(new GridIndex(x,y), seg);

} // end of add method

/************************************************************************
*
************************************************************************/
public PlaneSegment[][] toArray() {

    /***************************
    * get the array dimensions *
    ***************************/
    int max_x = -1;
    int max_y = -1;
    for(Iterator it = segments.keySet().iterator(); it.hasNext(); ) {
        GridIndex index = (GridIndex)it.next();

        if(index.getX() > max_x) max_x = index.getX();
        if(index.getY() > max_y) max_y = index.getY();

    } // end of first pass

    /*******************
    * create the array *
    *******************/
    PlaneSegment[][] array = new PlaneSegment[max_y+1][max_x+1];

    /*********************************
    * another pass to fill the array *
    *********************************/
    for(Iterator it = segments.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry entry = (Map.Entry)it.next();
        GridIndex index  =    (GridIndex)entry.getKey();
        PlaneSegment seg = (PlaneSegment)entry.getValue();

        array[index.getY()][index.getX()] = seg;

    } // end of second pass

    return array;

} // end of toArray method

} // end of GridAccumulator class
