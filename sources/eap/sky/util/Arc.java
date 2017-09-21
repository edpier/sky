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

import eap.sky.util.*;

import java.util.*;

import java.awt.geom.*;

/***************************************************************************
* Corresponds to a "straight" line on a sphere. An arc is a contiguous
* subset of the
* intersection of a plane and the unit sphere. Note that an arc may or
* may not be a "great circle" for which the plane goes through the origin.
* This class designates one endpoint of the arc as the beginning, and the other
* as the end.
* <p>
* Arcs may be arranged into a linked list and encapsulated in an
* {@link ArcPath} to describe a polygon on the sphere.
* <p>
* The main use of this class is to project spherical shapes onto a plane.
* In order to do this we have to "split" a linked list of arcs so that
* the arcs only intersect the projections cuts and the beginning and
* end points of the arcs. The usual proceedure is to construct the desired
* Arcs, link them together, and then create an {@link ArcPath} from them.
***************************************************************************/
public class Arc {

/*************************************************
* Indicates the beginning of the arc.
* @see Projection#project(Direction, Arc, int)
*************************************************/
public static final int BEGINNING = -1;

/*************************************************
* Indicates the middle of the arc.
* @see Projection#project(Direction, Arc, int)
*************************************************/
public static final int MIDDLE = 0;

/*************************************************
* Indicates the end of the arc.
* @see Projection#project(Direction, Arc, int)
*************************************************/
public static final int END = 1;

private static final Angle TINY_ANGLE = new Angle(Math.sin(1e-10),
                                                  Math.cos(1e-10));

ThreeVector point0;
ThreeVector point1;

ThreeVector normal;

double distance;


Angle length;

Arc next;
Arc last;

Set<Projection> split_for;

/**************************************************************************
* Create a new arc.
**************************************************************************/
private Arc() {

    split_for = new HashSet<Projection>();

}

/**************************************************************************
* Create a new general arc.
* @param p0 The beginning of the arc.
* @param p1 The end of the arc.
* @param n A direction perpendicular to the plane whose intersection with
* the sphere defines the path of the arc.
**************************************************************************/
public Arc(Direction p0, Direction p1, Direction n) {

    this();

    this.point0 = new ThreeVector(p0);
    this.point1 = new ThreeVector(p1);
    this.normal = new ThreeVector(n);

//     System.out.println("creating p0="+p0);
//     System.out.println("creating p1="+p1);
//     System.out.println("creating  n="+n);

    /****************************************
    * distance of the plane from the origin *
    ****************************************/
    distance = normal.dot(point0);

    double sin = point0.cross(point1).dot(normal);
    double cos = point0.dot(point1) - point0.dot(normal)*point1.dot(normal);

    length = rotation(point1);

    next = null;
    last = null;

} // end of constructor



/**************************************************************************
* Create a new great circle arc. A great circle is the shortest path
* between two points in the sphere.
* @param p0 The beginning of the arc
* @param p1 The end of the arc.
**************************************************************************/
public Arc(Direction p0, Direction p1) {

    this(p0, p1, p0.perpendicular(p1));


} // end of great circle constructor

/**************************************************************************
* returns the normal to the plane which contain the three points on the
* sphere.
**************************************************************************/
public static Direction findNormal(Direction p0, Direction mid,
                                   Direction p1) {

//   System.out.println("finding normal p0="+p0);
//   System.out.println("finding normal mid="+mid);
//   System.out.println("finding normal p1="+p1);

    ThreeVector v1 = new ThreeVector(mid).minus(new ThreeVector(p0 ));
    ThreeVector v2 = new ThreeVector(p1 ).minus(new ThreeVector(mid));

    ThreeVector normal = v1.cross(v2);

    return normal.getDirection();

} // end of findNormal method

/**************************************************************************
* Returns the next arc in a linked list.
* @return The next arc in a linked list.
**************************************************************************/
public Arc getNext() { return next; }

/**************************************************************************
* Returns the previous arc in a linked list.
* @return The previous arc in a linked list.
**************************************************************************/
public Arc getLast() { return last; }

/**************************************************************************
* Link two arcs together in a linked list.
* @param arc The arc to follow this one in the list.
**************************************************************************/
public void connectBefore(Arc arc) {

    /************************
    * break old connections *
    ************************/
    if(arc.last!= null) arc.last.disconnectNext();
    disconnectNext();

    /***********************
    * make new connections *
    ***********************/
    this.next = arc;
    arc.last = this;

} // end of connectBefore method

/**************************************************************************
* Unlink this arc from the next one in a linked list.
**************************************************************************/
public void disconnectNext() {

    if(next==null) return;

    next.last = null;
    next = null;

}

/**************************************************************************
* Returns the point at the beginning of the arc.
* @return The point at the beginning of the arc.
**************************************************************************/
public Direction getStart() { return point0.getDirection(); }

/**************************************************************************
* Returns the point at the end of the arc.
* @return The point at the end of the arc.
**************************************************************************/
public Direction getEnd() { return point1.getDirection(); }

/**************************************************************************
* Return the normal to the plane which defined the path of this arc.
**************************************************************************/
public Direction getNormal() { return normal.getDirection(); }

/**************************************************************************
* Return the length of this arc.
* @return The length of this arc.
**************************************************************************/
public Angle getLength() { return length; }

/**************************************************************************
* Produce a list of points at which two arcs intersect.
* The returned list will hold {@link Direction} objects.
* @param arc the arc to intesect with this one.
* @return A list of Directions of all the intersections.
**************************************************************************/
public List<Direction> intersections(Arc arc) {

    List<Direction> intersections = new ArrayList<Direction>();

    /****************************************************
    * dot the normals to see if the planes are parallel *
    ****************************************************/
    ThreeVector cross = normal.cross(arc.normal);
    double cross2 = cross.dot(cross);
    if(cross2 == 0.0) {
        return intersections;
    }

    double dot = normal.dot(arc.normal);

    /********************************************************
    * find the line which is the intersection of the planes *
    ********************************************************/
    double det = 1.0 - dot*dot;
    double c1 = (distance - arc.distance*dot)/det;
    double c2 = (arc.distance - distance*dot)/det;

//     System.out.println("c1="+c1+" c2="+c2+" det="+det);
//     System.out.println("    normal="+normal);
//     System.out.println("arc.normal="+arc.normal);

    /*********************************************************
    * find the intersection of the line with the unit sphere *
    *********************************************************/
    double u = Math.sqrt((1.0 - (c1*c1 + c2*c2 + 2.0*c1*c2*dot))/cross2);

    ThreeVector v0 = normal.times(c1).plus(arc.normal.times(c2));
    ThreeVector v1 = cross.times(u);

    ThreeVector p1 = v0.plus(v1);
    ThreeVector p2 = v0.minus(v1);


//     System.out.println("v0="+v0);
//     System.out.println("v1="+v1);
//     System.out.println("u="+u);

    /*******************************
    * check the first intersection *
    *******************************/
    if(contains(p1) && arc.contains(p1)) {

        intersections.add(p1.getDirection());
    }

    /********************************
    * check the second intersection *
    ********************************/
    if(contains(p2) && arc.contains(p2)) {

        intersections.add(p2.getDirection());
    }

    return intersections;

} // end of intersection method

/**************************************************************************
* Returns true if you have previously called {@link #split(Projection)}
* for the given projection.
* @param projection The projection in question.
**************************************************************************/
public boolean isSplitFor(Projection projection) {

    return split_for.contains(projection);

}


/**************************************************************************
* Splits this arc into a list of contiguous arcs wherever it intersects
* the cut(s) of the given projection. This is necessary before projecting
* the arcs onto the plane.
* @param projection The projection whose cuts we are splitting for.
**************************************************************************/
public Arc split(Projection projection) {

//System.out.println("\nsplitting...");

    /********************************
    * check if we are already split *
    ********************************/
    if(isSplitFor(projection)) return this;

    /********************************
    * collect all the intersections *
    ********************************/
    List<Direction> points = new ArrayList<Direction>();
    points.add(point0.getDirection());

    for(Iterator it = projection.getCuts().iterator(); it.hasNext(); ) {
        Arc cut = (Arc)it.next();

        points.addAll(intersections(cut));
    }
    points.add(point1.getDirection());

    /**********************************************
    * if we only have two points, then there are
    * no intersections, so we can just return
    * ourself and skip all this nonsense
    **********************************************/
    if(points.size() == 2) {
        split_for.add(projection);
        return this;
    }

  //  System.out.println("we have "+points.size()+" points");

    /***************************************************
    * now calculate the Angle for each intersection
    * so that we can sort them. Note this will
    * discard perfectly redundant points
    ***************************************************/
    Map<Angle, Direction> map = new HashMap<Angle, Direction>();
    for(Iterator it = points.iterator(); it.hasNext(); ) {
        Direction point = (Direction)it.next();

        Angle angle = rotation(new ThreeVector(point));
// trouble here if we take this out
     //   System.out.println("angle="+angle+" point="+point);

        map.put(angle, point);
    }

    /***************************
    * sort the points by angle *
    ***************************/
    List<Angle> angles = new ArrayList<Angle>(map.keySet());
    Collections.sort(angles);

    /******************************************
    * make an arc between each pair of points *
    ******************************************/
    Angle from_angle = null;
    Direction from = null;
    Arc first_arc = null;
    Arc previous_arc = last;
    for(Iterator it = angles.iterator(); it.hasNext(); ) {
        Angle angle = (Angle)it.next();
        Direction to = (Direction)map.get(angle);

 //        System.out.println("angle="+angle);
//         System.out.println("from="+from);
//         System.out.println("to="+to);
//         System.out.println();

        /******************************************
        * wait until we have two points
        * and skip arcs with a teeny tiny length
        ******************************************/
//         if(from!= null)
//             System.out.println("angle="+angle+" from_angle="+from_angle+
//                       " sin length="+ angle.minus(from_angle));
        if(from!=null &&
            angle.minus(from_angle).compareTo(TINY_ANGLE) > 0) {
     //   System.out.println("diff="+angle.minus(from_angle)+" "+TINY_ANGLE);
             /*********************************************
            * create the sub-arc. Note we don't use the
            * regular constructor, since we don't need
            * to recalculate the distance or the angle
            **********************************************/
            Arc arc = new Arc(from, to, normal.getDirection());

            /***********************************************
            * mark that the new arc is split for this
            * projection, and inherit any splitting marks
            * from this arc
            ***********************************************/
            arc.split_for.addAll(split_for);
            arc.split_for.add(projection);

            /********************************
            * conect the arc into the chain *
            ********************************/
            if(previous_arc != null) previous_arc.connectBefore(arc);

            if(first_arc == null) first_arc = arc;
            previous_arc = arc;


        } // end if we have two points to connect

        /************************
        * shift ahead one point *
        ************************/
        from = to;
        from_angle = angle;
    }

    /******************************************************
    * finally link the last arc to the one after this one *
    ******************************************************/
    if(next!= null) previous_arc.connectBefore(next);

    return first_arc;

} // end of split method


/**************************************************************************
* Returns the distance along the arc between the initial point and some
* arbitrary point on the arc. This method is private because it
* does not check that the given point actually is on the arc.
* @param p A point on the arc.
**************************************************************************/
private Angle rotation(ThreeVector p) {

    if(p.equals(point0)) return Angle.ANGLE0;

    double sin = point0.cross(p).dot(normal);
    double cos = point0.dot(p) - point0.dot(normal)*p.dot(normal);

    /*****************************
    * we need to normalize these *
    *****************************/
    double norm = Math.sqrt(sin*sin + cos*cos);
    sin /= norm;
    cos /= norm;

  //  System.out.println("sin="+sin+" cos="+cos);

    return new Angle(sin, cos);

} // end of rotation method

/**************************************************************************
* Returns true if the direction of the gicven vector is on the arc.
**************************************************************************/
private boolean  contains(ThreeVector p) {

    Angle angle = rotation(p);

    return angle.compareTo(Angle.ANGLE0) >= 0 &&
           angle.compareTo(length) <=0;

} // end of getHat method

/**************************************************************************
* creates a new arc which is rotated with respect to this one.
**************************************************************************/
public Arc rotate(Rotation rotation) {

    Arc arc = new Arc();

    /**********************************
    * transform the reference vectors *
    **********************************/
    arc.point0 = new ThreeVector(rotation.transform(point0.getDirection()));
    arc.point1 = new ThreeVector(rotation.transform(point1.getDirection()));
    arc.normal = new ThreeVector(rotation.transform(normal.getDirection()));

    /*****************************************
    * copy the rotation-invariant quantities *
    *****************************************/
    arc.distance = distance;
    arc.length = length;

    return arc;

} // end of rotate method

/**************************************************************************
* creates a new arc which is flipped with respect to this one.
**************************************************************************/
public Arc flip(FlipY flip) {

    Arc arc = new Arc();

    /**********************************
    * transform the reference vectors *
    **********************************/
    arc.point0 = new ThreeVector(flip.transform(point0.getDirection()));
    arc.point1 = new ThreeVector(flip.transform(point1.getDirection()));
    arc.normal = new ThreeVector(flip.transform(normal.getDirection()));

    /*****************************************
    * copy the rotation-invariant quantities *
    *****************************************/
    arc.distance = distance;
    arc.length = length;

    // this is a rush implementation to make an RA/Dec grid work on the
    // rotator plot. Have to check if this is correct at some point.
    // and really all this should be replaced with a transform plus
    // projection to a plane, with accuracy tied to the pixel size.

    return this;
   // return arc;

} // end of rotate method

/**************************************************************************
*
**************************************************************************/
public Arc transform(Transform trans) {

    if(trans instanceof Rotation) {
        return rotate((Rotation)trans);
    } else if(trans instanceof FlipY) {
        return flip((FlipY)trans);
    } else if(trans instanceof CompositeTransform) {
        /********************************************
        * composite - apply each piece in sucession *
        ********************************************/
        CompositeTransform composite = (CompositeTransform)trans;

        Arc arc  = transform(composite.getFirstTransform());
        return arc.transform(composite.getSecondTransform());
    } else {
        /*******************************************
        * anything else is not currently supported *
        *******************************************/
        throw new IllegalArgumentException("Can't transform arc by "+trans);
    }

} // end of transform method

/**************************************************************************
* Returns a direction which is the given angle along the arc.
* @return A direction in the arc.
**************************************************************************/
public Direction direction(Angle angle) {

    if(angle.equals(Angle.ANGLE0)) return point0.getDirection();

//System.out.println("arc at "+angle);

    Rotation rot = new Rotation(angle.negative(), normal.getDirection());
    return rot.transform(point0.getDirection());

} // end of direction method



/*******************************************************************************
* Transform project this arc onto the plane.
* This method assumes that the arc does not cross any of the projection's cuts.
* @param projection The projection to apply.
* @param flatness2 A measure of how closely the returned shape should follow
* the true projected path. Specicifally this is the square of the maximum
* distance between the returned shape and the actual shape in the plane.
*******************************************************************************/
public GeneralPath project(Projection projection, double flatness2) {

    /*************************
    * project the end points *
    *************************/
    Point2D p0 = projection.project(getStart(), this, BEGINNING);
    Point2D p1 = projection.project(getEnd()  , this, END      );

    System.out.println();
    System.out.println("arc beginning "+p0);
    System.out.println("arc end       "+p1);
    System.out.println("end: "+getEnd());

    LinkedPoint point0 = new LinkedPoint(p0);
    LinkedPoint point1 = new LinkedPoint(p1);

    point0.connectBefore(point1);

// System.out.println("point0="+this.point0.getDirection());
// System.out.println("point1="+this.point1.getDirection());
// System.out.println("normal="+this.normal.getDirection());

    /************************************
    * recursively refine the projection *
    ************************************/
    refineProjection(projection, flatness2, Angle.ANGLE0, length, point0);

    return point0.pathAfter();

} // end of project method

/**************************************************************************
* Recursive method to refine the set of points along an arc.
* @param projection The projection to apply.
* @param flatness2 A measure of how closely the returned shape should follow
* the true projected path. Specicifally this is the square of the maximum
* distance between the returned shape and the actual shape in the plane.
* @param angle0 The beginning of the segment of the arc under consideration.
* @param step the length of the segment of the arc under consideration.
* @param point0 The point previosuly calculated to correspond to angle0.
**************************************************************************/
private void refineProjection(Projection projection,
                              double flatness2,
                              Angle angle0, Angle step,
                              LinkedPoint point0) {



    /*******************
    * get the midpoint *
    *******************/
    Angle half_step = step.half();
    Angle angle = angle0.plus(half_step);
    Direction dir = direction(angle);



//     System.out.println(this);
//     System.out.println("point0="+this.point0+" point1="+this.point1);
//     System.out.println("angle="+angle+" half_step="+half_step);
//     System.out.println("dir="+dir);

//System.out.println("angle in arc="+angle);
    Point2D p = projection.project(dir, this, MIDDLE);
    LinkedPoint point = new LinkedPoint(p);

  //  System.out.println("refining angle0="+angle0+" angle="+angle);

    /******************
    * check the error *
    ******************/
    double error = point0.lineToNext().ptLineDistSq(point);

//     System.out.println("refining "+angle0.getDegrees()+" step="+step.getDegrees()+" error="+error);

//     System.out.println("angle="+angle);
//     System.out.println("dir="+dir);
//     System.out.println("point="+point);
     System.out.println("angle="+angle+" error="+error+" x="+p.getX());
//     System.out.println();

    if(error > flatness2) {
        /**********************************************
        * the error is too big, so we need this point *
        **********************************************/
        LinkedPoint point1 = point0.getNext();

        point0.connectBefore(point);
        point.connectBefore(point1);

        /******************************************************
        * try refining the left and right subarcs recursively *
        ******************************************************/
        refineProjection(projection, flatness2, angle0, half_step, point0);
        refineProjection(projection, flatness2, angle , half_step, point );
    }


} // end of refine method


} // end of Arc class
