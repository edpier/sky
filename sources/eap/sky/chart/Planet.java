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
import eap.sky.earth.*;
import eap.sky.ephemeris.*;
import eap.sky.time.*;
import eap.sky.time.barycenter.*;

import javax.swing.Icon;
import java.awt.*;
import java.awt.geom.*;

/****************************************************************************
*
****************************************************************************/
public class Planet implements ChartItem {

private Ephemeris ephemeris;
private Observatory obs;
private int body;
private Icon icon;
private TDBSystem TDB;
private UT1System UT1;

double half_width;
double half_height;

Point2D scaled;


/****************************************************************************
*
****************************************************************************/
public Planet(Ephemeris ephemeris, Observatory obs,
              int body, Icon icon, TDBSystem TDB) {

    this.ephemeris = ephemeris;
    this.obs       = obs;
    this.body      = body;
    this.icon      = icon;
    this.TDB       = TDB;

    this.UT1 = UT1System.getInstance();


    half_width  = 0.5 * icon.getIconWidth();
    half_height = 0.5 * icon.getIconHeight();

} // end of constructor

/****************************************************************************
*
****************************************************************************/
public Planet(Ephemeris ephemeris, Observatory obs,
              int body, Icon icon) {

    this(ephemeris, obs, body, icon, TDBSystem.getInstance());
}

/***************************************************************************
*
***************************************************************************/
public void update(ChartState state) {

    /************************************
    * get the current chart time In TDB *
    ************************************/
    PreciseDate tdb = state.getTime(TDB);
    EOP eop = (EOP)state.getTime(UT1);

    /*********************************
    * get the position of the planet *
    *********************************/
    Direction dir = ephemeris.position(body, tdb, eop, obs).getDirection();

    /**************************
    * transform the direction *
    **************************/
    Direction transformed =
                 state.getTransformFrom(Coordinates.RA_DEC).transform(dir);

    /**********
    * project *
    **********/
  //  Point2D projected = state.getProjection().project(transformed);

    /********
    * scale *
    ********/
   // scaled = state.getScaling().transform(projected, null);

   scaled = state.getPlane().toPixels(transformed);

} // end of update method




/****************************************************************************
*
****************************************************************************/
public void paint(Chart chart, Graphics2D g2) {

    /***************************************
    * don't paint if we aren't initialized *
    ***************************************/
    if(scaled==null) return;

    /*****************
    * paint the icon *
    *****************/
    icon.paintIcon(chart, g2, (int)(scaled.getX()-half_width),
                              (int)(scaled.getY()-half_height));


} //end of paint method

/*******************************************************************************
*
*******************************************************************************/
public void itemAdded(Chart chart) {}

/*******************************************************************************
*
*******************************************************************************/
public void itemRemoved(Chart chart) {}

} // end of Planet class
