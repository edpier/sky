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

import java.awt.geom.*;

/***************************************************************************
*
***************************************************************************/
public class ProjectedPoint extends UpdatablePoint {

Coordinates coord;

Transform last_trans;

Direction original;
Direction transformed;
Point2D projected;
Point2D scaled;

boolean initialized;

/***************************************************************************
*
***************************************************************************/
public ProjectedPoint(Direction original, Coordinates coord) {

    /********************************************
    * initialize the projected location to be
    * outside the field of view
    ********************************************/
    super(-100.0, -100.0);

    this.original = original;
    this.coord = coord;

    initialized=false;
}

/***************************************************************************
*
***************************************************************************/
public Direction getDirection() { return original; }

/***************************************************************************
*
***************************************************************************/
public Direction getChartCoordinatesDirection() { return transformed; }

/***************************************************************************
*
***************************************************************************/
public ProjectedPoint(Direction original) {

    this(original, Coordinates.RA_DEC);

}

/***************************************************************************
*
***************************************************************************/
public boolean update(ChartState state) {

    if(updateProjected(state) || state.scalingChanged()) {

        Point2D pixels = state.getPlane().getMappingToPixels().map(projected);
        setLocation(pixels);

        initialized = true;

      return true;
    }

    return false;

} // end of update method

/***************************************************************************
*
***************************************************************************/
private boolean updateProjected(ChartState state) {


    if(updateTransformed(state) || state.projectionChanged() ) {

        projected = state.getProjection().project(transformed);
        return true;
    }

    return false;

} // end of updateProjected method

/***************************************************************************
*
***************************************************************************/
private boolean updateTransformed(ChartState state) {


    Transform trans = state.getTransformFrom(coord);


    if(!initialized || !trans.equals(last_trans)) {

        last_trans = trans;
        transformed = trans.transform(original);

        return true;
    }

    return false;

} // end of updateTransformed method


} // end of ProjectedPoint class



