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

import eap.sky.util.*;
import eap.sky.util.coordinates.*;

import java.awt.*;

/**************************************************************************
*
**************************************************************************/
public class Grid extends CompositeItem {

/**************************************************************************
*
**************************************************************************/
public Grid(Coordinates coord) {

    /***********
    * vertical *
    ***********/
    for(int i=0; i<24; ++i) {
        double lon = i*15;

        add(new CurveItem(new LongitudeLine(lon, -75, 75), coord));

    } // end of loop over meridians

    /*************
    * horizontal *
    *************/
     for(int i=1; i<12; ++i) {
        double lat = i*15-90;

        add(new CurveItem(new LatitudeLine(lat, 0, 360), coord));

    } // end of loop over latitude lines

    /****************************************
    * draw the border around the projection *
    ****************************************/
    ProjectionBorder border = new ProjectionBorder();
    border.setColor(Color.lightGray);
    add(border);

} // end of constructor



} // end of Grid class
