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

/***********************************************************************
*
***********************************************************************/
public class Equator extends CurveItem {

public static final Equator MILKYWAY = new Equator("Galactic Plane",
                                                   Coordinates.GALACTIC,
                                                   Color.cyan,
                                                   new BasicStroke());

public static final Equator ECLIPTIC = new Equator("Ecliptic",
                                                   new EclipticCoordinates(),
                                                   Color.orange,
                                                   new BasicStroke());

/***********************************************************************
*
***********************************************************************/
public Equator(String name, Coordinates coord, Color color, Stroke stroke) {

    super(new LatitudeLine(0, 0, 360), coord);

    setColor(color);
    setStroke(stroke);

} // end of constructor

} // end of Equator class
