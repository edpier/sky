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

package eap.sky.chart;

import java.util.*;

/**********************************************************************
*
**********************************************************************/
public class MedianBuffer {

int size;
List<Long> values;
List<Long> sorted;

/**********************************************************************
*
**********************************************************************/
public MedianBuffer(int size) {

    this.size = size;

    values = new LinkedList<Long>();
    sorted = new ArrayList<Long>();

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public synchronized void addValue(long value) {

    Long number = new Long(value);
    values.add(number);
    sorted.add(number);

    if(sorted.size() > size) {
        Object first = values.get(0);
        values.remove(0);
        sorted.remove(first);
    }

    Collections.sort(sorted);

} // end of addValue method


/**********************************************************************
*
**********************************************************************/
public synchronized long median() {

    if(values.size() == 0) return 0;

    int index;
    if(size < sorted.size() ) index = size/2;
    else                      index = sorted.size()/2;

    return ((Long)sorted.get(index)).longValue();

} // end of median method

} // end of MedianBuffer class
