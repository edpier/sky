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
import java.awt.geom.*;

/**********************************************************************
*
**********************************************************************/
public class Grid {

Map<GridIndex, PlaneSegment> segs;

int nx;
int ny;

/**********************************************************************
*
**********************************************************************/
public Grid() {

    this.segs = new HashMap<GridIndex, PlaneSegment>();

    nx = -1;
    ny = -1;

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public void addAll(Collection col) {

    for(Iterator it = col.iterator(); it.hasNext(); ) {
        PlaneSegment seg = (PlaneSegment)it.next();

        add(seg);

    } // end of loop over segments

} // end of addAll method

/**********************************************************************
*
**********************************************************************/
public void add(PlaneSegment seg) {

    GridIndex index = (GridIndex)seg.getLayoutIndex();
    segs.put(index, seg);

    if(index.getX() > nx-1) nx = index.getX()+1;
    if(index.getY() > ny-1) ny = index.getY()+1;

   // System.out.println("added segment nx="+nx+" ny="+ny);

} // end of addPlaneSegment method

/**********************************************************************
*
**********************************************************************/
public PlaneSegment get(int x, int y) {

    return (PlaneSegment)segs.get(new GridIndex(x,y));

} // end of get method

/**********************************************************************
*
**********************************************************************/
public GapList createGapList(boolean horizontal) {

//System.out.println("nx="+nx+" ny="+ny);

    ParameterSet params = new ParameterSet();

    int ni;
    int nj;
    if(horizontal) {
        nj = nx;
        ni = ny;
    } else {
        nj = ny;
        ni = nx;
    }

    GapList gaps = new GapList(nj);
    for(int j=0; j< nj; ++j) {

        boolean first = true;
        double min=Double.NaN;
        double max=Double.NaN;

        for(int i=0; i< ni; ++i) {

            PlaneSegment seg = null;
            if(horizontal) seg = get(j,i);
            else           seg = get(i,j);

            if(seg == null) continue;

            PlaneTransform trans = seg.getTransformToParent();

            Rectangle2D bounds = trans.transform(seg.getBounds(), null)
                                      .getBounds2D();

            double seg_min;
            double seg_max;
            if(horizontal) {
                seg_min = bounds.getMinX();
                seg_max = bounds.getMaxX();
            } else {
                seg_min = bounds.getMinY();
                seg_max = bounds.getMaxY();
            }


            if(first || seg_min < min) min = seg_min;
            if(first || seg_max > max) max = seg_max;


            first = false;

//             System.out.println(" "+i+" "+seg.getName()+" "+
//                                seg.getLayoutIndex()+" "+
//                                seg_min+" "+seg_max);

        } // end of loop over x


//        System.out.println("j="+j+" min="+min+"  max="+max);

        gaps.addSegment(j, min, max);


    } // end of loop over y

//System.exit(1);
    return gaps;

} // end of createGapList method

/*******************************************************************
*
*******************************************************************/
PlaneSegment[][] toArray() {

   // System.out.println("Grid.toArray: nx="+nx+" ny="+ny);

    PlaneSegment[][] array = new PlaneSegment[nx][ny];

    for(int i=0; i<nx; ++i) {
        for(int j=0; j<ny; ++j) {
            array[i][j] = get(i,j);
        }
    }

    return array;

} // end of toArray method

} // end of Grid class
