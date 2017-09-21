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

package eap.sky.stars.ingest;

import java.util.*;

/*********************************************************************
*
*********************************************************************/
public class BinLayout {

SortedMap<Float, Integer> bins;

/*********************************************************************
*
*********************************************************************/
private BinLayout() {

    bins = new TreeMap<Float, Integer>();

} // end of empty constructor

/*********************************************************************
*
*********************************************************************/
public BinLayout(MagnitudeHistogram hist, int size) {

    this();

    float width = hist.getBinWidth();

    int sum=0;
    for(int i=0; i< hist.getBinCount(); ++i) {
        int count = hist.getCount(i);

        sum += count;
        if(sum > size) {
            float mag = hist.getMinMagnitude(i)+width;
            System.out.println(mag+" "+sum);
            addBin(hist.getMinMagnitude(i)+width);
            sum=0;
        }
    } // end of loop over histogram bins

    if(sum >0) {
        float mag = hist.getMinMagnitude(hist.getBinCount()-1)+width;
        System.out.println(mag+" "+sum);
        addBin(mag);
    }






} // end of constructor from a histogram

/*********************************************************************
*
*********************************************************************/
public void addBin(float end_mag) {

    bins.put(new Float(end_mag), new Integer(bins.size()));

} // end of addBin method

/*********************************************************************
*
*********************************************************************/
public int getBin(float mag) {

    Iterator it = bins.tailMap(new Float(mag)).entrySet().iterator();
    if(it.hasNext()) {
        Map.Entry entry = (Map.Entry)it.next();
        return ((Number)entry.getValue()).intValue();
    } else {
        return bins.size()-1;
    }


} // end of getBin method

/*********************************************************************
*
*********************************************************************/
public int getBinCount() { return bins.size(); }

} // end of BinLayout class
