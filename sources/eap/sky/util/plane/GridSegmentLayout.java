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
public class GridSegmentLayout extends SegmentLayout {

Grid grid;
PlaneSegment[][] array;

double spacing_x;
double spacing_y;

double scale_x;
double scale_y;

double offset_x;
double offset_y;

/**********************************************************************
*
**********************************************************************/
public GridSegmentLayout(Grid grid, double spacing_x, double offset_x,
                                    double spacing_y, double offset_y ) {

    this.grid = grid;
    this.spacing_x = spacing_x;
    this.spacing_y = spacing_y;
    this.offset_x = offset_x;
    this.offset_y = offset_y;

    scale_x = 1.0/spacing_x;
    scale_y = 1.0/spacing_y;

    array = grid.toArray();

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public List childrenNear(Point2D point, ParameterSet params) {



    int i = (int)Math.floor((point.getX() - offset_x)*scale_x);
    int j = (int)Math.floor((point.getY() - offset_y)*scale_y);

//     System.out.println("grid offset_x = "+offset_x+" spacing_x="+1.0/scale_x+" "+spacing_x);
//     System.out.println("grid x="+(point.getX() - offset_x)*scale_x);
//     System.out.println("grid "+i+" "+j);

    PlaneSegment seg = null;
    if(i>=0 && i< array.length &&
       j>=0 && j< array[i].length) {
        seg = array[i][j]; //grid.get(i,j);
    }

    if(seg != null) return Collections.singletonList(seg);
    else            return Collections.EMPTY_LIST;

} // end of childrenNear method

} // end of gridSegmentLayout class

