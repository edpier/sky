package eap.sky.util.plane;

import eap.sky.util.*;

import java.awt.geom.*;
import java.io.*;
import java.util.*;

/*****************************************************************
*
*****************************************************************/
public class DitherPattern implements Serializable {

List<Point2D> points;

/*****************************************************************
*
*****************************************************************/
protected DitherPattern(List<Point2D> points) {

    this.points = points;

} // end of empty constructor

/*****************************************************************
* Create an empty dither pattern.
*****************************************************************/
public DitherPattern() {

    this(new ArrayList<Point2D>());

} // end of empty constructor

/*****************************************************************
* Create a polygon dither pattern. The first point will be in the
* center of the polygon, and then subsequent points will be at the
* corners of the polygon.
* @param npoints The number of points in the dither pattern. The
* number of vertices of the polynomial is one less than this.
* If npoints &lt; 1, the dither pattern will be empty.
* @param radius The radius of a circle which circumscribes the
* polygon in arc seconds. In other words, this is the distance
* from the first point to each of the others.
*****************************************************************/
public DitherPattern(int npoints, double radius) {

    this();

    if(npoints<1) return;

    /***********************************
    * the first point is in the center *
    ***********************************/
    points.add(new Point2D.Double(0.0, 0.0));
    if(npoints == 1) return;

    int ncorners = npoints-1;

    Angle angle = Angle.ANGLE0;
    Angle delta = new Angle(360.0/ncorners);
    delta.getCos();
    delta.getSin();

    for(int i=0; i<ncorners; ++i) {

        addPoint(angle.getCos()*radius,
                 angle.getSin()*radius );

        angle = angle.plus(delta);

    } // end of loop over corners

} // end of constructor


/*****************************************************************
*
*****************************************************************/
public DitherPattern(File file) throws IOException {

    this();
    read(file);

} // end of constructor from a file



/*****************************************************************
*
*****************************************************************/
public int getPointCount() { return points.size(); }

/*****************************************************************
* Adds an arbitrary point to the dither pattern.
* @param dx The X coordinate of the point in arc seconds
* @param dy The Y coordinate of the point in arc seconds
*****************************************************************/
public void addPoint(double dx, double dy) {

    points.add(new Point2D.Double(dx, dy));

} // end of addPoint method

/*****************************************************************
* Returns a point in the dither pattern.
* @return The point coordinates in arc seconds.
*****************************************************************/
public Point2D getPoint(int index) {

    Point2D point = points.get(index);
    return new Point2D.Double(point.getX(), point.getY());

} // end of getPoint method


/*****************************************************************
*
*****************************************************************/
public void read(File file) throws IOException {

    read(new BufferedReader(
         new FileReader(file)));

} // end of read from a file method

/*****************************************************************
*
*****************************************************************/
public void read(BufferedReader reader) throws IOException {

    String line;
    int number = 0;
    while((line=reader.readLine()) != null) {
        ++number;
        line = line.trim();
        if(line.startsWith("#") || line.length() ==0) continue;

        try {
            StringTokenizer tokens = new StringTokenizer(line);
            double dx = Double.parseDouble(tokens.nextToken());
            double dy = Double.parseDouble(tokens.nextToken());

            addPoint(dx, dy);
        } catch(Exception e) {
            IOException e2 = new IOException("Parse error line "+
                                             number+": "+line);
            e2.initCause(e);
            throw(e2);
        }

    } // end of loop over lines

} // end or read method

} // end of DitherPattern class
