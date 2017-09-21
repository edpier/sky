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

import java.awt.geom.*;

/***************************************************************************
*
***************************************************************************/
public class CenteredLabel extends FixedLabel {

double offset_x;
double offset_y;

/***************************************************************************
*
***************************************************************************/
public CenteredLabel(String string, double offset_x, double offset_y) {

    super(null, string);

    this.offset_x = offset_x;
    this.offset_y = offset_y;

} // end of constructor


/***************************************************************************
*
***************************************************************************/
public void update(ChartState state) {

    Point2D center = state.getCenter();

   // System.out.println("centered label "+center);


    setPosition(new Point2D.Double(center.getX() + offset_x,
                                   center.getY() + offset_y));

} // end of update method

} // end of CenteredLabel class