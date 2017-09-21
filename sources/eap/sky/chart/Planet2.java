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

import eap.sky.time.*;
import eap.sky.time.barycenter.*;
import eap.sky.util.*;
import eap.sky.util.coordinates.*;
import eap.sky.ephemeris.*;
import eap.sky.earth.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;

/***************************************************************************
*
***************************************************************************/
public class Planet2 extends CompositeItem implements PointingSource, Clickable {

String name;
Ephemeris ephemeris;
Observatory obs;
int body;
double diameter;
BufferedImage image;
int width;
int height;

Direction dir;

boolean show_phase;

TDBSystem TDB;
UT1System UT1;

PreciseDate tdb;

ImageItem image_item;
SkyLabel label;


AffineTransform translation;

Shape clip;
double click_radius2;
Collection<ActionListener> listeners;

/***************************************************************************
*
***************************************************************************/
public Planet2(String name, Ephemeris ephemeris, Observatory obs, int body,
               double diameter, BufferedImage image, TDBSystem TDB) {

    this.name = name;
    this.ephemeris = ephemeris;
    this.obs = obs;
    this.body = body;
    this.image = image;
    this.diameter = diameter;
    this.TDB = TDB;
    this.UT1 = UT1System.getInstance();

    width  = image.getWidth();
    height = image.getHeight();

    show_phase = true;

    translation = AffineTransform.getTranslateInstance(0.5*width, 0.5*height);

    image_item = new ImageItem(image, null);
    add(image_item);

    click_radius2 = 5*5;
    listeners = new HashSet<ActionListener>();


} // end of constructor

/***************************************************************************
*
***************************************************************************/
public void addActionListener(ActionListener l) { listeners.add(l); }

/***************************************************************************
*
***************************************************************************/
public void removeActionListener(ActionListener l) { listeners.remove(l); }

/***************************************************************************
*
***************************************************************************/
public String getName() { return name; }

/***************************************************************************
*
***************************************************************************/
public void update(ChartState state) {

    /*********************************
    * get the position of the planet *
    *********************************/
    PreciseDate tdb = state.getTime(TDB);
    EOP eop = (EOP)state.getTime(UT1);
    ThreeVector position = ephemeris.position(body, tdb, eop, obs);

    /*********************************************************************
    * coordinates. The coordinates change on each iteration because the
    * planet moves. The coordinates have the the planet at the pole and
    * celectial North toward the top of the image (at least I think so).
    *********************************************************************/
    dir = position.getDirection();
    Coordinates coord = new PointingCoordinates(Coordinates.RA_DEC,
                              new FixedPointing(Coordinates.RA_DEC,
                                                Coordinates.RA_DEC,
                                                dir, 0.0));

    /*******************************************************************
    * mapping. We create a new mapping on each iteration because
    * the distance to the planet changes, so we have to change the
    * pixel scale.
    *****************************************************************/
    double distance = position.getLength();
    double pixels_per_radian = width*distance/diameter;

    AffineTransform trans = AffineTransform.getScaleInstance(pixels_per_radian,
                                                             pixels_per_radian );
    trans.preConcatenate(translation);

    Mapping mapping = new AffineMapping(trans);


    /********
    * plane *
    ********/
    Plane plane = new Plane(coord, Projection.TANGENT, mapping);
    image_item.setPlane(plane);

    if(show_phase && body != Ephemeris.SUN) {
        /**********************************************************
        * find the position of the sun so we can handle the phase *
        * we transform this to coordinates where Z points from the
        * earth to the planet, and the origin is at the center of
        * the planet
        **********************************************************/
        ThreeVector sun = ephemeris.position(ephemeris.SUN, tdb, eop, obs);
        Transform from_ra_dec = Coordinates.RA_DEC.getTransformTo(coord,
                                                                state.getTime());
        sun = new ThreeVector(from_ra_dec.transform(sun.getDirection()),
                            sun.getLength());

        sun = new ThreeVector(sun.getX(), sun.getY(), sun.getZ() - distance);
        Direction sun_dir = sun.getDirection();

        /********************************************************
        * determinate the shape of the terminator
        * The terminator (projected onto a plane) is an
        * ellipse squeezed by a factor of -1 for a full moon
        * to +1 for a new moon.
        ********************************************************/
        Rectangle2D rect = new Rectangle2D.Double(0, 0, width, height);

        Shape terminator = new Arc2D.Double(rect, -90, 180, Arc2D.OPEN);
        double factor = Math.sin(Math.toRadians(sun_dir.getLatitude()));

        AffineTransform squish = AffineTransform.getScaleInstance(factor, 1.0);
        squish.translate(0.5*(1.0-factor)/factor*width, 0.0);

        terminator = squish.createTransformedShape(terminator);


       /*********************************
       * draw the rest of the clip path *
       *********************************/
        GeneralPath path = new GeneralPath();
        path.append(terminator, false);

        path.lineTo(width, 0.f);
        path.lineTo(width, height);
        path.closePath();




        AffineTransform spin = AffineTransform.getRotateInstance(
                            Math.toRadians(sun_dir.getLongitude()),
                            width*0.5, height*0.5);

        clip  = spin.createTransformedShape(path);



        /***************************
        * copy the original image *
        **************************/
        BufferedImage copy = new BufferedImage(width, height,
                                            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = copy.createGraphics();
        g2.setClip(clip);
        g2.drawImage(image, 0,0, null);

        image_item.setImage(copy);

    } // end if we are applying phase

    /****************************************************************
    * label - we have to redo the label each time because it moves
    * with the planet
    ****************************************************************/
    synchronized(this) {
        if(label != null) remove(label);
        label = new SkyLabel(Direction.Z_AXIS, name, coord);
        label.setAnchor(0.5, 0.5);
        label.update(state);
        add(label);
    } // end of synched block


    super.update(state);


} // end of update method

/***************************************************************************
*
***************************************************************************/
public Direction getDirection() { return dir; }

/***************************************************************************
*
***************************************************************************/
public Rotation getRotation(PreciseDate time) {

    PreciseDate tdb = TDB.convertDate(time);
    EOP eop = (EOP)UT1.convertDate(time);

    ThreeVector position = ephemeris.position(body, tdb, eop, obs);
    Direction dir = position.getDirection();

   return (Rotation)new Rotation(new Euler(dir, 0.0)).invert();


} // end of getRotation method

/*************************************************************************
*
*************************************************************************/
public boolean respondToClick(MouseEvent e) {

    Point2D point = e.getPoint();

    // I suspect the clip path is not in the chart pixel coordinates
    // We are seeing "phantom moons", and no response when I click on the
    // moon image. So I'm disabling this. For now, just click on the label
    // -ED 2007-11-12
    if(//(clip != null && clip.contains(point)) ||
       label.isUnder(point)) {
        /**********
        * clicked *
        **********/
        System.out.println("Clicked "+name);

        ActionEvent event = new ActionEvent(this,
                                            ActionEvent.ACTION_PERFORMED,
                                            name);
        for(Iterator it = listeners.iterator(); it.hasNext(); ) {
            ActionListener l = (ActionListener)it.next();
            l.actionPerformed(event);
        }

        return true;
    }

    return false;

} // end of respondTo method

} // end of Planet2 class
