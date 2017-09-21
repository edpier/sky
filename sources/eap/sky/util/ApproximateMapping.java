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

import java.util.*;

import java.awt.geom.*;

/***************************************************************************
*
***************************************************************************/
public class ApproximateMapping {

List<MapCell> cells;
List<MapCell> unmodifiable_cells;


/***************************************************************************
*
***************************************************************************/
public ApproximateMapping(Mapping map, Point2D point00, int width, int height,
                          double accuracy) {

    cells = new ArrayList<MapCell>();
    unmodifiable_cells = Collections.unmodifiableList(cells);

    double x0 = point00.getX();
    double y0 = point00.getY();

    Point2D point20 = new Point2D.Double(x0 + width, y0         );
    Point2D point02 = new Point2D.Double(x0        , y0 + height);
    Point2D point22 = new Point2D.Double(x0 + width, y0 + height);

    Point2D mapped00 = map.map(point00);
    Point2D mapped02 = map.map(point02);
    Point2D mapped20 = map.map(point20);
    Point2D mapped22 = map.map(point22);

    refineCell(map, accuracy*accuracy,
               point00, point20, point02, point22,
               width, height,
               mapped00, mapped20, mapped02, mapped22);


} // end of constructor

/***************************************************************************
*
***************************************************************************/
private void refineCell(Mapping map, double accuracy2,
                        Point2D point00, Point2D point20,
                        Point2D point02, Point2D point22,
                        int width, int height,
                        Point2D mapped00, Point2D mapped20,
                        Point2D mapped02, Point2D mapped22 ) {



    MapCell cell = new MapCell(point00, width, height,
                               mapped00, mapped20, mapped02);

    double x0 = point00.getX();
    double y0 = point00.getY();

   // System.out.println("refining cell x0="+x0+" y0="+y0+" "+width+" x "+height);

    int half_width = width/2;
    int half_height = height/2;

    int half_width2  = width  - half_width;
    int half_height2 = height - half_height;

    Point2D point10 = new Point2D.Double(x0 + half_width, y0              );
    Point2D point01 = new Point2D.Double(x0             , y0 + half_height);
    Point2D point11 = new Point2D.Double(x0 + half_width, y0 + half_height);
    Point2D point21 = new Point2D.Double(x0 +      width, y0 + half_height);
    Point2D point12 = new Point2D.Double(x0 + half_width, y0 +      height);


    Point2D mapped10 = map.map(point10);
    Point2D mapped01 = map.map(point01);
    Point2D mapped11 = map.map(point11);
    Point2D mapped21 = map.map(point21);
    Point2D mapped12 = map.map(point12);


    Point2D approx10 = cell.map(point10);
    Point2D approx01 = cell.map(point01);
    Point2D approx11 = cell.map(point11);
    Point2D approx21 = cell.map(point21);
    Point2D approx12 = cell.map(point12);
    Point2D approx22 = cell.map(point22);

    if(mapped10.distanceSq(approx10) <= accuracy2 ||
       mapped01.distanceSq(approx01) <= accuracy2 ||
       mapped11.distanceSq(approx11) <= accuracy2 ||
       mapped21.distanceSq(approx21) <= accuracy2 ||
       mapped12.distanceSq(approx12) <= accuracy2 ||
       mapped22.distanceSq(approx22) <= accuracy2 ||
       width == 1 ||height == 1  ) {
        /********************************************
        * the approximation is good, save this cell *
        ********************************************/
        cells.add(cell);


    } else {
        /********************************************
        * not so good - refine the cell recursively *
        ********************************************/
  // we need to be more careful about the refined widths and heights
        refineCell(map, accuracy2, point00,  point10,  point01,  point11,
                   half_width, half_height, mapped00, mapped10, mapped01, mapped11);

        refineCell(map, accuracy2, point10,  point20,  point11,  point21,
                   half_width2, half_height, mapped10, mapped20, mapped11, mapped21);

        refineCell(map, accuracy2, point01,  point11,  point02,  point12,
                   half_width, half_height2, mapped01, mapped11, mapped02, mapped12);

        refineCell(map, accuracy2, point11,  point21,  point12,  point22,
                   half_width2, half_height2, mapped11, mapped21, mapped12, mapped22);
    }





} // end of refineCell method

/***************************************************************************
*
***************************************************************************/
public List getCells() { return unmodifiable_cells; }

} // end of ApproximateMapping class
