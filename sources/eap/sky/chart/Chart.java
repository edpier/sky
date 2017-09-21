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
import eap.sky.time.*;
import eap.sky.time.barycenter.*;
import eap.sky.time.clock.*;
import eap.sky.util.coordinates.*;

import java.io.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.imageio.*;

/***************************************************************************
*
***************************************************************************/
public class Chart extends JPanel implements Dragable {

private static final int DEFAULT_WIDTH  = 1000;
private static final int DEFAULT_HEIGHT =  500;

private static final int MEDIAN_BUFFER_SIZE = 10;


Clock clock;

Coordinates coordinates;
Projection projection;

int aspect_type;
Aspect aspect;

double scale;
Point2D center;

CurrentPointing pointing;
PointingCoordinates aspected_coords;
Plane plane;

boolean buffer_graphics;
MedianBuffer paint_time;
double sleep_ratio;
private Animation animation;

Dimension last_size;

ChartState state;


java.util.List<ChartItem> items;
java.util.List<ChartItem> read_only_items;

// stuff for dragging
Point2D orig_center;
Point2D orig_mouse;
double orig_scale;

ChartMonitor chart_monitor;




/***************************************************************************
* @param scale The number of pixels per radian at the center
***************************************************************************/
public Chart(Clock clock, Coordinates coordinates, int aspect_type,
             Projection projection, double scale, Point2D center,
             Dimension size) {


    this.clock = clock;
    this.coordinates= coordinates;
    this.projection = projection;

    if(aspect_type == Aspect.CUSTOM) {
        throw new IllegalArgumentException("Can't specify CUSTOM aspect here");
    }

    pointing = new CurrentPointing();
    setAspect(coordinates.getAspect(aspect_type), aspect_type);
    aspected_coords = new PointingCoordinates(coordinates, pointing);

    setPreferredSize(size);
    this.scale = scale;
    this.center = center;

    setBackground(Color.white);


    /********************************************
    * initialize the list of items. We
    * used a linked list, since it should give
    * better performance for insertions and
    * deletions in the middle
    ********************************************/
    items = Collections.synchronizedList(new LinkedList<ChartItem>());
    read_only_items = Collections.unmodifiableList(items);

    paint_time = new MedianBuffer(MEDIAN_BUFFER_SIZE);

    sleep_ratio = 1.0;

    /**************
    * key strokes *
    **************/
    KeyStroke up   = KeyStroke.getKeyStroke("UP");
    KeyStroke down = KeyStroke.getKeyStroke("DOWN");

    InputMap map;
    map = getInputMap(WHEN_FOCUSED);
    map.put(up,   "zoom_in");
    map.put(down, "zoom_out");

    map = getInputMap(WHEN_IN_FOCUSED_WINDOW);
    map.put(up,   "zoom_in");
    map.put(down, "zoom_out");

    map = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    map.put(up,   "zoom_in");
    map.put(down, "zoom_out");


    getActionMap().put("zoom_in", new ZoomInAction());
    getActionMap().put("zoom_out", new ZoomOutAction());

    setRequestFocusEnabled(true);

    /********
    * mouse *
    ********/
    ChartMouse mouse = new ChartMouse();
    addMouseMotionListener(mouse);
    addMouseListener(mouse);

    addComponentListener(new Resizer());

    updatePlane();

  //  chart_monitor = ChartMonitor.popUp();
    buffer_graphics = true;

    /********************
    * turn on tool tips *
    ********************/
   // setToolTipText("");

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public Clock getClock() { return clock; }

/***************************************************************************
*
***************************************************************************/
public void setChartMonitor(ChartMonitor monitor) {

    this.chart_monitor = monitor;

} // end of setChartMonitor method

/***************************************************************************
*
***************************************************************************/
public static void clearKeys(JComponent comp) {

    KeyStroke up   = KeyStroke.getKeyStroke("UP");
    KeyStroke down = KeyStroke.getKeyStroke("DOWN");

    InputMap map;

    map = comp.getInputMap(JComponent.WHEN_FOCUSED);
    map.put(up, "none");
    map.put(down, "none");

    map = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    map.put(up, "none");
    map.put(down, "none");

    map = comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    map.put(up, "none");
    map.put(down, "none");

} // end of clearKeys methof

/***************************************************************************
*
***************************************************************************/
// public Chart(Clock clock) {
// 
//     this(clock, Coordinates.RA_DEC, EQUATORIAL_ASPECT,
//          Projection.AITOFF, DEFAULT_WIDTH/6.0, new Point2D.Double(0.0, 0.0),
//          new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
// 
// } // end of constructor with defaults


/***************************************************************************
* Starts the chart painting animation
***************************************************************************/
public synchronized void start() {

    if(animation == null || animation.isDone() ) {

        animation = new Animation();
        animation.start();

    }

} // end of start method

/***************************************************************************
* Stops the chart painting animation
***************************************************************************/
public synchronized void stop() {

    if(animation != null) animation.quit();
    animation = null;

} // end of start method

/***************************************************************************
*
***************************************************************************/
public boolean isAnimated() {

    return animation != null && animation.keep_going;

}

/***************************************************************************
*
***************************************************************************/
public ChartState getChartState() { return state; }

/***************************************************************************
*
***************************************************************************/
private void updatePlane() {

    /*********************************************************
    * this is a little bit of a kludge to catch the
    * first painting, which can occur before the
    * components are laid out, when the actual size is zero
    *********************************************************/
    Dimension size = getSize();
    if(size.getWidth() == 0 || size.getHeight() == 0) {
        size = getPreferredSize();
    }

    last_size = size;

    /******************
    * aspect flipping *
    ******************/
    double scale_x = scale * aspect.getFlipX();
    double scale_y = scale * aspect.getFlipY();

    /***********************************************
    * construct the transform to pixel coordinates *
    ***********************************************/
    double offsetx =  size.getWidth()*0.5 - center.getX() *scale_x;
    double offsety = size.getHeight()*0.5 + center.getY() *scale_y;

    AffineTransform trans =  new AffineTransform(scale_x, 0.0, 0.0, -scale_y,
                                                 offsetx, offsety);

    AffineMapping map = new AffineMapping(trans);

    /*******************
    * make a new plane *
    *******************/
    plane = new Plane(aspected_coords, projection, map);

    /***************************************************************
    * any time we change the plane we should update, unless
    * we are animated, in which case it will happen automatically
    ***************************************************************/
    if(!isAnimated()) update();

} // end of updatePlane method


/***************************************************************************
*
***************************************************************************/
public void update() {

//System.out.println("\nchart update scale="+scale+" center="+center);

    /*********************************************************
    * this is a little bit of a kludge to catch the
    * first painting, which can occur before the
    * components are laid out, when the actual size is zero
    *********************************************************/
    Dimension size = getSize();
    if(size.getWidth() == 0 || size.getHeight() == 0) {
        size = getPreferredSize();
    }

    if(!size.equals(last_size)) updatePlane();

    /*********************************************************
    * construct a snapshot of the current state of the chart *
    *********************************************************/
    state = new ChartState(state, clock.currentTime(), plane, coordinates,
                           size);

//System.out.println("Chart update: made state");

    /**************************************************
    * update all the chart items to the current state *
    **************************************************/
    for(Iterator it = new ArrayList<ChartItem>(items).iterator(); it.hasNext(); ) {
        ChartItem item = (ChartItem)it.next();
//System.out.println("Chart update: updating "+item);

        try { item.update(state); }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
//System.out.println("Chart update: done updating ");
    /**********************************
    * tell the chart to redraw itself *
    **********************************/
    repaint();
//System.out.println("Chart update: done calling repaint");
} // end of update method

/***************************************************************************
*
***************************************************************************/
public void addItem(int index, ChartItem item) {
//System.out.println("adding "+item);
    items.add(index, item);
    item.itemAdded(this);
    if(!isAnimated()) update();

}

/***************************************************************************
*
***************************************************************************/
public void addItem(ChartItem item) {
    addItem(items.size(), item);

}

/***************************************************************************
*
***************************************************************************/
public void removeItem(ChartItem item) {

    items.remove(item);
    item.itemRemoved(this);
    if(!isAnimated()) update();

}

/***************************************************************************
*
***************************************************************************/
public java.util.List<ChartItem> getItems() { return read_only_items; }

/***************************************************************************
*
***************************************************************************/
public void setAspectType(int type) {

    /***************
    * sanity check *
    ***************/
    if(!Aspect.isValidType(type) ) {
        throw new IllegalArgumentException("Invalid aspect type "+type);
    }

    if(type != Aspect.CUSTOM) {
        setAspect(coordinates.getAspect(type), type);
        updatePlane();
    }


} // end of setAspectType method

/***************************************************************************
*
***************************************************************************/
private void setAspect(Aspect aspect, int type) {

    this.aspect_type = type;
    this.aspect = aspect;
    pointing.setPointing(aspect.getRotation());

} // end of setAspect method

/***************************************************************************
* Set a custom aspect
***************************************************************************/
public void setAspect(Aspect aspect) {

    setAspect(aspect, Aspect.CUSTOM);
    updatePlane();

} // end of setAspect method

/***************************************************************************
*
***************************************************************************/
// public void setAspect(Rotation aspect) {
// 
//     pointing.setPointing(aspect);
//     updatePlane();
// 
// } // end of setAspect method

/***************************************************************************
*
***************************************************************************/
public void setProjection(Projection projection) {

    this.projection = projection;
    updatePlane();

} // end of setProjection method

/***************************************************************************
*
***************************************************************************/
public void setCoordinates(Coordinates coordinates) {

    this.coordinates = coordinates;

    /*********************************************************
    * change the aspect, unless we are using a custom aspect *
    *********************************************************/
    setAspectType(aspect_type);

    /*********************************************
    * create the new aspected coordinates
    * these are the coordinates of the plane
    ********************************************/
    PointingCoordinates new_aspected_coords =
                          new PointingCoordinates(coordinates, pointing);

    /*************************************************************
    * get the direction corresponding to the center of the
    * chart and transform it to the new coordinates. Note
    * the center is in radians, not pixels. Also we use the
    * aspected coordinates,
    ************************************************************/
    Direction dir = plane.getProjection().unproject(center);

    if(dir != null) {
        Transform trans = aspected_coords.getTransformTo(new_aspected_coords,
                                                       clock.currentTime());
        dir = trans.transform(dir);
    }

    /*************************
    * update the coordinates *
    *************************/

    aspected_coords = new_aspected_coords;

    /************************
    * go to the new center *
    ***********************/
    if(dir != null) {
        Point2D point = plane.getProjection().project(dir);
        if(point != null) center = point;
    }

    updatePlane();


} // end of setCoordinates method

/***************************************************************************
*
***************************************************************************/
public void rescale(double factor) { setScale(scale*factor); }

/***************************************************************************
*
***************************************************************************/
public void setScale(double scale) {

    this.scale = scale;
    updatePlane();
}

/***************************************************************************
*
***************************************************************************/
public void setCenter(Point2D center) {

    this.center = (Point2D)center.clone();
    updatePlane();
}

/***************************************************************************
* Returns the coordinates of the center of the chart in radians.
***************************************************************************/
public Point2D getCenter() { return (Point2D)center.clone(); }

/***************************************************************************
*
***************************************************************************/
public double getScale() { return scale; }

/***************************************************************************
*
***************************************************************************/
public void paintComponent(Graphics g) {

    /**********************************************
    * record the painting start time, so we
    * can later determine how long this took
    **********************************************/
    long start = System.currentTimeMillis();

    /***************************************
    * this is just in case the superclass
    * does something important
    ***************************************/
    super.paintComponent(g);

    /*********************************************
    * there are two different modes for painting *
    *********************************************/
    if(buffer_graphics) {
        /*********************************************
        * paint into a buffered image, and then paint
        * that to the screen. This is a workaround for
        * some graphics trouble you can see with
        * certain versions of Java and Linux.
        * Swing components are sposed to do their own
        * double buffering, so this shouldn't be necessary
        * except as a bug workaround.
        ***********************************************/
        int width = getWidth();
        int height = getHeight();

        BufferedImage image = new BufferedImage(getWidth(), getHeight(),
                                                BufferedImage.TYPE_INT_ARGB);

        /*************************************************
        * paint a white background the same color as
        * would be used in the chart's graphics context
        *************************************************/
        Graphics2D buffer_g2 = image.createGraphics();
        buffer_g2.setColor(getBackground());
        buffer_g2.draw(new Rectangle(0, 0, width, height));

        /************************
        * paint into the image *
        ***********************/
        paintChart(buffer_g2);

        /********************************
        * paint the image to the screen *
        ********************************/
        ((Graphics2D)g).drawRenderedImage(image, new AffineTransform());


    } else {
        /************************************************
        * just paint directly into the graphics context *
        ************************************************/
        paintChart(g);
    }


    /********************************************
    * mark the end time and record this so we
    * can optimize our animation repaint rate
    ********************************************/
    long end = System.currentTimeMillis();
//System.out.println("chart paint took "+(end-start));
    paint_time.addValue(end-start);

} // end of paintComponent method

/**************************************************************************
*
**************************************************************************/
public void paintChart(Graphics g) {

    /*********************************
    * recast the graphics context *
    ******************************/
    Graphics2D g2 = (Graphics2D)g;

    /*******************************************************
    * iterate over the items
    * note we make a copy of the list so that
    * we can add and remove items while we are iterating
    * The alternative is synchronizing the list, but
    * I suspect this will give better performance
    *******************************************************/
    for(Iterator it = new ArrayList<ChartItem>(items).iterator(); it.hasNext(); ) {
        ChartItem item = (ChartItem)it.next();

        item.paint(this, g2);

    } // end of loop over items

} // end of paintChart method

/***************************************************************************
*
***************************************************************************/
// public String getToolTipText(MouseEvent e) {
// 
//     Point2D point = e.getPoint();
//     Direction dir = plane.toDirection(point);
// 
//     if(dir == null) return null;
//     Direction radec = coordinates.toRADec(clock.currentTime()).transform(dir);
// 
//     return SexigesimalFormat.HMS.format(radec.getLongitude()) +" / "+
//            SexigesimalFormat.DMS.format(radec.getLatitude());
// 
// } // end of getToolTipText method

/***************************************************************************
*
***************************************************************************/
public boolean startDrag(MouseEvent e) {

    orig_mouse = e.getPoint();
    orig_center  = center;
    orig_scale = scale;

    return true;
}

/***************************************************************************
*
***************************************************************************/
public void dragTo(MouseEvent e) {

// we're not *really* going to change scale in the middle of a drag are we?
//     if(scale != orig_scale ) {
//         /******************************************
//         * the scale changed, so we need
//         * to reset our reference points
//         * This should rarely happen in practice
//         ******************************************/
//         orig_scale  = scale;
//         orig_mouse  = e.getPoint();
//         orig_center = center;
//         return;
//     }


    double x = orig_center.getX() + (orig_mouse.getX() - e.getX())/(aspect.getFlipX()*scale);
    double y = orig_center.getY() - (orig_mouse.getY() - e.getY())/(aspect.getFlipY()*scale);

    setCenter(new Point2D.Double(x, y));

    if(!isAnimated()) update();

} // end of dragTo method


/***************************************************************************
*
***************************************************************************/
public void dragDone(MouseEvent e) {}

/**************************************************************************
*
**************************************************************************/
public static Icon makeIcon(String file) {

    ClassLoader loader = Chart.class.getClassLoader();

    java.net.URL url = loader.getResource("eap/sky/chart/icons/"+file);
    if(url==null) return null;

    return new ImageIcon(url);

} // end of getIcon method

/**************************************************************************
*
**************************************************************************/
public static BufferedImage makeIconImage(String file) {

    ClassLoader loader = Chart.class.getClassLoader();

    java.net.URL url = loader.getResource("eap/sky/chart/icons/"+file);
    if(url==null) return null;

    try { return ImageIO.read(url); }
    catch(IOException e) {
        e.printStackTrace();
        return null;
    }

} // end of getIconImage method

/**************************************************************************
*
**************************************************************************/
private class Animation extends Thread {

private boolean keep_going;
private boolean done;

private MedianBuffer median;

/***************************************************************************
*
***************************************************************************/
public Animation() {

    super("Chart Animation");

    median = new MedianBuffer(MEDIAN_BUFFER_SIZE);

    keep_going = true;
    done = false;

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public boolean isDone() { return done; }


/***************************************************************************
* Stops the animation. This method blocks until the animation has actually
* stopped.
***************************************************************************/
public synchronized void quit() {

    keep_going = false;

    /***************************************************
    * we want to wait until the run loop has exited.
    * but that might have happened before we get here.
    * so we check the done flag to see if the loop
    * has already exited.
    ***************************************************/
    try { if(! done) wait(); }
    catch(InterruptedException e) {}

} // end of stop method

/***************************************************************************
*
***************************************************************************/
public void run() {

    while(keep_going) {

        /**********************************************
        * record the painting start time, so we
        * can later determine how long this took
        **********************************************/
        long start = System.currentTimeMillis();

        /***********************************
        * force the chart to update itself *
        ***********************************/
      //  System.out.println("chart updating");
        update();

//System.out.println("chart done updating");
        /********************************************
        * mark the end time and record this so we
        * can optimize our animation repaint rate
        ********************************************/
        long end = System.currentTimeMillis();
        long time_to_update = end-start;
        median.addValue(end-start);

        /*****************************************************
        * sleep an amount which scales with the paint time
        * in order to keep the fraction of CPU use constant
        * This is to avoid poor response when painting hogs
        * the thread
        *****************************************************/
        long sleep_time = (long)((median.median() +
                                 paint_time.median())*sleep_ratio);

        if(sleep_time < 100) sleep_time = 100;

      //  System.out.println("sleep_time="+sleep_time);

//           System.out.println("update ="+(end-start)+
//                            " median="+median.median()+" sleep="+sleep_time);

        if(chart_monitor != null) chart_monitor.update(sleep_time,
                                                       paint_time.median(),
                                                       median.median(),
                                                       time_to_update);
//System.out.println("chart sleeping");
        try { sleep(sleep_time); }
        catch(InterruptedException e) {}

//System.out.println("chart done sleeping");
    } // end of infinite loop

    System.out.println("broke out of chart update loop");

    /************************************************************
    * presumably we got here because someone called our stop
    * method. That method is waiting  for us to notify it that
    * we have actually stopped
    * We synchronize this block and set the "done" flag to
    * keep the stop method from calling wait after we have
    * called notifyAll().
    ************************************************************/
    synchronized(this) {
        notifyAll();
        done = true;
    }


} // end of run method

} // end of Animation inner class

/***************************************************************************
*
***************************************************************************/
private class Resizer extends ComponentAdapter {

/***************************************************************************
*
***************************************************************************/
public void componentResized(ComponentEvent e) {

    if(!isAnimated()) update();

} // end of componentResized event

} // end of Resizer method
} // end of Chart class
