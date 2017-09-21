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


import java.awt.geom.*;

/***************************************************************************
* This is actually the Hammer projection
***************************************************************************/
public  class Aitoff extends Projection {

private static final double SQRT2 = Math.sqrt(2.0);

private static final double scale = 2.828427124746193;

private static final Rotation rotation =
                      (Rotation)( new Rotation(new Euler(new Direction(0.0,0.0), 90.0)).invert());

private static final Rotation inverse = (Rotation)rotation.invert();

/***************************************************************************
* Create a new projection. This is protected because we need only one
* instamce of this class. Use {@link Projection#AITOFF} instead.
***************************************************************************/
protected Aitoff() {

    Arc cut = new Arc(Direction.Y_AXIS.oppositeDirection(),
                      Direction.Y_AXIS,
                      Direction.X_AXIS);

    cuts.add(cut);

    double size =scale;
    border = new Ellipse2D.Double(-size, -0.5*size, 2.0*size, size);

} // end of constructor

/***************************************************************************
* Project a set of speherical coordinates onto a plane. This could be
* optimized.
***************************************************************************/
private Point2D project(double lon, double lat) {

//System.out.println("projecting lon="+lon+" lat="+lat);


     if(Math.abs(lon) < 1e-6 && Math.abs(lat) < 1e-6) {

         return new Point2D.Double(0.0, 0.0);

    } else {
          while(lon> 180.0) lon -= 360.0;
          while(lon<-180.0) lon += 360.0;

          double coslat=Math.cos(Math.toRadians(lat));
          double halflon=Math.toRadians(lon*0.5);

          double  cosrho = coslat*Math.cos(halflon);
          if(     cosrho >  1.0) cosrho =  1.0;
          else if(cosrho < -1.0) cosrho = -1.0;


          double sintheta=coslat*Math.sin(halflon)/Math.sqrt(1.-cosrho*cosrho);

          double costheta=0.0;
          if(Math.abs(sintheta)<1.) costheta=Math.sqrt(1.-sintheta*sintheta);

          double sinhalfrho=Math.sqrt(.5*(1.-cosrho));

          double hori =  2.*sinhalfrho*sintheta/SQRT2;
          double vert =    sinhalfrho*costheta/SQRT2;

          if(lat<0.0) vert=-vert;

          Point2D point = new Point2D.Double(hori*scale, vert*scale);


         if(Double.isNaN(point.getX()) ) {

             System.out.println("Aitoff: lon="+lon+" lat="+lat);
             System.out.println("Aitoff: cosrho="+cosrho);
             System.out.println("Aitoff: sinhalfrho="+sinhalfrho+
                                       " sintheta="+sintheta);
             System.out.println("Aitoff: "+point);
           //  throw new IllegalArgumentException();
             return new Point2D.Double(0.0, 0.0);
          }


//           System.out.println("projected="+point);
//           System.out.println("border="+border);
          return point;
    }

} // end of project method

/****************************************************************************
*
****************************************************************************/
public Point2D getBorderIntersection(Line2D line) { return  null; }

/***************************************************************************
*
***************************************************************************/
public  Point2D project(Direction dir) {


    dir = rotation.transform(dir);

  //  System.out.println("projecting dir="+dir.getX()+" "+dir.getY()+" "+dir.getZ());

  // System.out.println("dir = lon "+dir.getLongitude());

    double lon = dir.getLongitude();
    double lat = dir.getLatitude();

//     try { Thread.sleep(0); }
//     catch(InterruptedException e) {}

  //  System.out.println("dir = lon "+dir.getLongitude());

    return project(lon, lat);

} // end of project method

/***************************************************************************
*
***************************************************************************/
public Point2D project(Direction dir, Arc arc, int place) {

    dir = rotation.transform(dir);

    double lon = dir.getLongitude();
    double lat = dir.getLatitude();

    /************************
    * see if we're on a cut *
    ************************/
    if(lon == 180.0 || lon == -180.0) {
        /*********************************
        * see which way the arc is going *
        *********************************/
        double direction = rotation.transform(arc.getNormal()).getZ();

    //    System.out.println("direction ="+direction);

        if(Math.abs(direction) < 1e-10) {
            /******************************************************
            * the arc is following along the cut, so we have to
            * check adjacent arcs
            ******************************************************/

            /********************
            * first look before *
            ********************/
           for(Arc adjacent = arc.getLast();
               adjacent != null && adjacent != arc ;
               adjacent = adjacent.getLast()) {

                direction = rotation.transform(adjacent.getNormal()).getZ();
                if(Math.abs(direction) < 1e-10) continue;

            //    System.out.println("    found before");

                if(direction > 0.0) lon = 180.0; // CCW
                else                lon = -180.0; // CW

                return project(lon, lat);
            }

            /******************
            * then look after *
            ******************/
           for(Arc adjacent = arc.getNext();
               adjacent != null && adjacent != arc ;
               adjacent = adjacent.getNext()) {

                direction = rotation.transform(adjacent.getNormal()).getZ();
                if(Math.abs(direction) < 1e-10) continue;
           //     System.out.println("    found after");
                if(direction > 0.0) lon = -180.0; // CCW
                else                lon =  180.0; // CW

                return project(lon, lat);
            }
              //  System.out.println("    arbitrarily picking");
            /****************************************************
            * if we get here, there are no adjacent arcs
            * which are not on the cut, so we can pick a side
            * arbitrarily
            ****************************************************/
            lon = 180.0;

        } else {
            /*****************************************
            * the arc is plowing through the cut.
            * What we do depends on whether this is
            * the beginning or end point of the arc
            *****************************************/
            if(place == Arc.BEGINNING) {
                if(direction > 0.0) lon = -180.0; // CCW
                else                lon =  180.0; // CW
            } else if(place == Arc.END) {
                if(direction > 0.0) lon =  180.0; // CCW
                else                lon = -180.0; // CW
            } else {
                /*********************************************************
                * if we are in the middle and the arc intersects
                * the cut, it means that the arc was not properly split
                ********************************************************/
                System.out.println("arc length = "+arc.getLength());
                throw new IllegalArgumentException(arc+
                                                 " is not properly split");
            }



        } // end if the arc intersects the cut.
     } // end if we are on a cut

     return project(lon, lat);

} // end of projectEndpoint method

/*******************************************************************************
*
*******************************************************************************/
public Direction unproject(Point2D point) {

    double x = point.getX()/scale;
    double y = point.getY()/scale;

    /*********************************
    * check if the point is in bounds *
    **********************************/
//     System.out.println("x="+x+" y="+y);
//     System.out.println("radius2 = "+(x*x*0.25 + y*y) );
    if(x*x + y*y*4.0 > 1.0) return null;

    x *=  SQRT2;
    y *= -SQRT2;

    double a = Math.sqrt(4.0 - x*x - 4.0*y*y);

    double lat = -     Math.asin(a*y);
    double lon = 2.0 * Math.asin(a*x /(2.0 * Math.cos(lat)));

    Direction coord = new Direction(Math.toDegrees(lon),
                                    Math.toDegrees(lat));
    return inverse.transform(coord);

} // end of unproject method

/*****************************************************************************
*
*****************************************************************************/
// public Rectangle2D getBounds() {
// 
//     return new Rectangle2D.Double(-1.0, -0.5, 2.0, 1.0);
// 
// 
// } // end of getBounds method

} // end of Aitoff class
