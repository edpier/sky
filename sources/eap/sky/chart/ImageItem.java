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
import eap.sky.util.plane.*;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

/*******************************************************************************
*
*******************************************************************************/
public class ImageItem implements ChartItem {

Plane plane;

BufferedImage image;

BufferedImage transformed;
Point2D point;
Shape clip;

boolean image_changed;

Mapping last_map;
ApproximateMapping approx;

/*******************************************************************************
*
*******************************************************************************/
public ImageItem(BufferedImage image, Plane plane) {

    this.image = image;
    this.plane = plane;

} // end of constructor

/*******************************************************************************
*
*******************************************************************************/
public ImageItem(BufferedImage image, Coordinates coord, Direction dir,
                 PlaneCoordinates top, PlaneSegment seg, ParameterSet params) {

//System.out.println("creating image item");

    this.image = image;

    CurrentPointing pointing = new CurrentPointing();
    pointing.setPointing(dir,0.0);

    this.plane = new Plane(new PointingCoordinates(coord, pointing),
                          Projection.TANGENT,
                          seg.getTransformUpTo(top).getMapping(params).invert());

//System.out.println("done creating image item");

} // end of constructor

/****************************************************************************
*
****************************************************************************/
public synchronized void setImage(BufferedImage image) {

  //  System.out.println("Setting image");

    this.image = image;
    image_changed = true;

    if(image == null) transformed = null;

} // end of setImage methof

/*******************************************************************************
*
*******************************************************************************/
public synchronized void setPlane(Plane plane) {

    this.plane = plane;


} // end of setPlane method

/*******************************************************************************
*
*******************************************************************************/
public synchronized void update(ChartState state) {

    if(image == null) return;

//System.out.println("updating image item");
    /****************************************************
    * get the mapping from the image plane to the chart *
    ****************************************************/
    Mapping map = plane.getMappingTo(state.getPlane(), state.getTime());
    boolean mapping_changed = false;

    /*****************************************
    * check if we need to update the mapping *
    *****************************************/
    if(last_map == null || !map.equals(last_map)) {

        mapping_changed = true;

        /*******************************************
        * find the bounds of the transformed image *
        *******************************************/
        Rectangle2D orig_bounds =new Rectangle2D.Double(0,0,
                                                        image.getWidth(),
                                                        image.getHeight());
        Area area = new Area(map.map(orig_bounds, 1));
        area.intersect(new Area(state.getBounds()));
        clip = area;

        Rectangle2D bounds = clip.getBounds();
        int width  = (int)bounds.getWidth();
        int height = (int)bounds.getHeight();
        if(width==0 && height==0) {
            /*****************************************************
            * the transformed image does not appear on the chart *
            *****************************************************/
            transformed = null;
            return;
        }

        point = new Point2D.Double(bounds.getX(), bounds.getY());

        /*******************************
        * create the transformed image *
        *******************************/
        ColorModel color_model = image.getColorModel();
        if(color_model instanceof IndexColorModel) {
            transformed = new BufferedImage(width, height, image.getType(),
                                           (IndexColorModel)color_model);
        } else {
            transformed = new BufferedImage(width, height, image.getType());
        }

        /***********************************************************
        * make a linearized approximation of the mapping from the
        * chart plane to the original image
        ***********************************************************/
     //   System.out.println("approximating...");
        approx = new ApproximateMapping(map.invert(), point, width, height, 0.5);

    } // end if we needed to redo the mapping

    if(!mapping_changed && ! image_changed) return;
    image_changed = false;

   // System.out.println("transforming image");

    /**********************
    * transform the image *
    **********************/
    WritableRaster raster1 = transformed.getRaster();
    WritableRaster raster2 = image.getRaster();
    Object pixel = null;

  //  System.out.println("width="+transformed.getWidth()+" height="+transformed.getHeight());

    for(Iterator it = approx.getCells().iterator(); it.hasNext(); ) {
        MapCell cell = (MapCell)it.next();

        /***************************************
        * find the unmapped corner of the cell *
        ***************************************/
        Point2D point0 = cell.getCorner();
        int x0 = (int)(point0.getX() - point.getX());
        int y0 = (int)(point0.getY() - point.getY());

       // System.out.println("x0="+x0+" y0="+y0);

        /***********************************
        * loop over the points in the cell *
        ***********************************/
        int y = y0;
        Point2D pointy = cell.getMappedCorner();
        for(int j=0; j<cell.getHeight(); ++j, cell.incrementY(pointy), ++y) {

            int x = x0;
            Point2D point = (Point2D)pointy.clone();
            for(int i=0; i<cell.getWidth(); ++i, cell.incrementX(point), ++x) {

           // System.out.println("i="+i+" x="+x+" j="+j+" y="+y);

                /*********************************************
                * get the transformed pixel coordinates and
                * check if they are in bounds
                *********************************************/
                int x2 = (int)point.getX();
                int y2 = (int)point.getY();

                if(x2 < 0 || x2 >= image.getWidth() ||
                   y2 < 0 || y2 >= image.getHeight()  ) continue;

                /*****************
                * copy the pixel *
                *****************/
                try { 
                pixel = raster2.getDataElements(x2,y2, pixel);
                raster1.setDataElements(x, y, pixel);
                } catch(ArrayIndexOutOfBoundsException e) {
                    System.out.println("x="+x+" y="+y+" x2="+x2+" y2="+y2);
                    System.out.println("raster1: "+raster1.getWidth()+
                                       " x "+raster1.getHeight());
                    System.out.println("raster2: "+raster2.getWidth()+
                                       " x "+raster2.getHeight());

                    System.out.println("cell: x0="+x0+" y0="+y0+" "+
                                       cell.getWidth()+" x "+cell.getHeight());
                    System.out.println("i="+i+" j="+j);
                    e.printStackTrace();
                }
            }
        } // end of loop over pixels in the cell

    } // end of loop over cells




} // end of update method

/*******************************************************************************
*
*******************************************************************************/
public void paint(Chart chart, Graphics2D g2) {

    if(transformed == null) return;

  //  System.out.println("painting image "+transformed.getWidth()+" x "+transformed.getHeight());

    Shape orig_clip = g2.getClip();
    g2.clip(clip);

    g2.drawImage(transformed, (int)point.getX(), (int)point.getY(), chart);

    g2.setClip(orig_clip);

    /*************************************
    * draw cell boundaries for debugging *
    *************************************/
//     g2.setColor(Color.blue);
//     for(Iterator it = approx.getCells().iterator(); it.hasNext(); ) {
//         MapCell cell = (MapCell)it.next();
// 
//         /*********
//         * corner *
//         *********/
//         Point2D point0 = cell.getCorner();
// //         int x0 = (int)(point0.getX() - point.getX());
// //         int y0 = (int)(point0.getY() - point.getY());
//         int x0 = (int)point0.getX();
//         int y0 = (int)point0.getY();
// 
//         g2.draw(new Rectangle2D.Double(x0, y0, cell.getWidth(), cell.getHeight()));
// 
// 
// 
//     } // end of loop over cells

} // end of paint method

/*******************************************************************************
*
*******************************************************************************/
public void itemAdded(Chart chart) {}

/*******************************************************************************
*
*******************************************************************************/
public void itemRemoved(Chart chart) {}

} // end of ChartItem interface
