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

package eap.sky.image;

import eap.sky.stars.*;
import eap.sky.time.*;
import eap.sky.util.*;
import eap.sky.util.coordinates.*;
import eap.sky.util.plane.*;

import java.awt.geom.*;
import java.io.*;
import java.text.*;
import java.util.*;

/*********************************************************************
*
*********************************************************************/
public class StarDetectionList extends DetectionList {

ImageParams params;

PlaneCoordinates focal_coord;
PlaneSegment focal_seg;

StarCatalog catalog;

/*********************************************************************
*
*********************************************************************/
public StarDetectionList(StarCatalog catalog) {

    this.catalog = catalog;

} // end of constructor


/*********************************************************************
*
*********************************************************************/
public void set(ImageParams params) throws IOException {

    /***************************
    * get the focal plane info *
    ***************************/
    CoordConfig config = params.getCoordConfig();
    focal_coord = config.getCoordinates("FOCAL");
    focal_seg = focal_coord.getSegment();

    /***************************************************
    * find the center and radius of the displayed area *
    ***************************************************/
    Rectangle2D bounds = focal_seg.getBounds().getBounds2D();
    double x0 = bounds.getCenterX();
    double y0 = bounds.getCenterY();
    Direction ra_dec0 = params.toRADec(focal_seg, new Point2D.Double(x0, y0));

    Direction ra_dec1 = params.toRADec(focal_seg,
                                       new Point2D.Double(bounds.getX(),
                                                          bounds.getY()));

    Angle radius = ra_dec0.angleBetween(ra_dec1).times(1.2);

    /*************************************************
    * find the catalog cells covering the image area *
    *************************************************/
    Collection cells = catalog.getRootCell().getCellsNear(ra_dec0, radius);

    Band band = catalog.getSortBand();

    /*********************
    * find all the stars *
    *********************/
    clear();
    for(Iterator it = cells.iterator(); it.hasNext(); ) {
        InputCell cell = (InputCell)it.next();

        /***************************************
        * loop over all the stars in this cell *
        ***************************************/
        for(Iterator it2 = cell.getStars().iterator(); it2.hasNext(); ) {
            Star star = (Star)it2.next();

            if(star.getDirection().angleBetween(ra_dec0).compareTo(radius)>0) {
                continue;
            }

            /**************************************
            * transform to focal plane coordinates
            **************************************/
            Location focal_loc = params.toCoordinates(star.getDirection(),
                                                      focal_coord);

            if(focal_loc == null) continue;

            Point2D point = focal_loc.getPoint();
            double x = point.getX();
            double y = point.getY();

            /********************************
            * get the magnitude of the star *
            ********************************/
            float mag = star.getPhotometry().getMagnitude(band).getValue();

            /**********************
            * store the detection *
            **********************/
            add(new Detection(star.getName(), x, y, mag));

       } // end of loop over stars

    } // end of loop over cells

    updateMagnitudeSelection();

} // end of set method

} // end of StarDetectionList class