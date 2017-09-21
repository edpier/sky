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

/**********************************************************************
*
**********************************************************************/
public class GapList {

List<Gap> gaps;

int last_seg;
double last_max;

/**********************************************************************
*
**********************************************************************/
public GapList(int nsegments) {

    gaps = new ArrayList<Gap>();

    last_seg = -2;

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public void addSegment(int seg, double min, double max) {

    if(seg == last_seg+1) {
        gaps.add(new Gap(seg, last_max, min));
    }

    last_seg = seg;
    last_max = max;

} // end of addSegment method



/**********************************************************************
*
**********************************************************************/
public double findSpacing() {

    boolean first = true;
    double lo = Double.NaN;
    double hi = Double.NaN;

    /*********************************
    * loop over unique pairs of gaps *
    *********************************/
    for(Gap gap1 :  gaps) {
        for(Gap gap2 : gaps) {

            if(gap2.getIndex() <= gap1.getIndex()) continue;

            double delta = gap2.getIndex() - gap1.getIndex();
            double min = (gap2.getMin() - gap1.getMax())/delta;
            double max = (gap2.getMax() - gap1.getMin())/delta;

            if(first || min > lo) lo = min;
            if(first || max < hi) hi = max;
            first = false;

         //   System.out.println(gap1.getIndex()+" "+gap2.getIndex()+" "+min+" "+max);
        }
    } // end of loop over pairs

   // System.out.println("spacing: lo="+lo+" hi = "+hi);

    if(lo > hi) {
        throw new IllegalStateException("No valid spacing for all gaps");
    }

    return 0.5*(lo+hi);


} // end of findSpacing method

/************************************************************************
*
************************************************************************/
public double findOffset(double spacing) {

//     double sum=0.0;
//     for(Gap gap : gaps ) {
//
//         System.out.println("gap width="+gap.getWidth());
//
//         sum += gap.getCenter() - (gap.getIndex())*spacing;
//
//     } // end of loop over gaps
//
//     return sum / gaps.size();

    double lo = Double.NaN;
    double hi = Double.NaN;
    boolean first = true;
    for(Gap gap : gaps ) {

        double offset = spacing * gap.getIndex();
        double min = gap.getMin() - offset;
        double max = gap.getMax() - offset;

        if(first || min > lo) lo = min;
        if(first || max < hi) hi = max;
        first = false;

    } // end of loop over gaps

  //  System.out.println("offset: lo="+lo+" hi = "+hi);

    if(lo > hi) {
        throw new IllegalStateException("No valid offset for all gaps");
    }

    return 0.5*(lo+hi);

} // end of findOffset method

/************************************************************************
*
************************************************************************/
public void test(double spacing, double offset) {


    for(Gap gap : gaps) {
        int i = gap.getIndex();

        double x = i*spacing + offset;

        System.out.println("grid check: "+i+" "+gap.getMin()+" "+x+" "+gap.getMax());

        if((i != 0 && x < gap.getMin()) ||
           (i != gaps.size()-1 && x > gap.getMax()) ) {
            throw new IllegalStateException("Invalid grid spacing");
        }
    }
} // end of test method


} // end of GapList class
