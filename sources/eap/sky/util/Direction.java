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

import java.io.*;

/*****************************************************************************
* Represents a direction, or alternatively a point on a unit sphere.
* This may be specified as a cartesian unit vector or a longitude and latitude
* pair. A Direction object is immutable, meaning that once it is created,
* it cannot be reset to refer to a different direction.
*****************************************************************************/
public class Direction implements Serializable {

/** The direction of the X axis **/
public static final Direction X_AXIS = new Direction(1.0, 0.0, 0.0);

/** The direction of the Y axis **/
public static final Direction Y_AXIS = new Direction(0.0, 1.0, 0.0);

/** The direction of the pole **/
public static final Direction Z_AXIS = new Direction(0.0, 0.0, 1.0);

double lon;
double lat;
boolean have_angles;

double[] vec;

/***************************************************************************
* Create a new direction in terms of angles.
* @param lon the angle along the equator from the X axis in degrees.
* @param lat The angle above the equator in degrees.
***************************************************************************/
public Direction(double lon, double lat) {

    this.lon = lon;
    this.lat = lat;
    have_angles=true;

    vec=null;

} // end of constructor

/******************************************************************************
* Creates a new 3 element array containing the given values.
* This is a utility used by constructors. and assumes that the components
* are normalized to unity.
* @param x The cartesian X coordinate
* @param y The cartesian Y coordinate
* @param z The cartesian Z coordinate
******************************************************************************/
private static double[] makeVector(double x, double y, double z) {

    /*******************
    * create the array *
    *******************/
    double[] vec = {x, y, z};
    return vec;

} // end of makeVector function


/**************************************************************************
* Creates a new direction from a vector. The vector will be normalized to
* a unit vector if it is not already.
* @param x The cartesian X coordinate
* @param y The cartesian Y coordinate
* @param z The cartesian Z coordinate
**************************************************************************/
public Direction(double x, double y, double z) {

    vec = new double[3];
    vec[0] = x;
    vec[1] = y;
    vec[2] = z;
    have_angles=false;

    /**************************
    * check the normalization *
    **************************/
    double length2 = x*x + y*y + z*z;
    if(Math.abs(length2 - 1.0)<1e-15) return;

    /************
    * normalize *
    ************/
   // System.out.println("normalizing "+(length2-1.0));
    double norm = 1.0/Math.sqrt(length2);
    vec[0] *= norm;
    vec[1] *= norm;
    vec[2] *= norm;

} // constructor from x,y,z



/****************************************************************************
* Creates a new direction with the given vector components. The vector is
* copied, so the user may subsequently modify the argument without affecting
* the created object. The vector is normalized to unity it it is not
* already. If the argument has more than three elements, only the first three
* are used. If the vector has fewer than three elements, an exception is
* thrown.
* @param vec a three element vector
* @throws ArrasyIndexOutOfBoundsException if the vector has fewer than three
*         elements.
****************************************************************************/
public Direction(double[] vec) {

    this.vec = new double[3];
  //  System.arraycopy(vec, 0, this.vec, 0, 3);
    this.vec[0] = vec[0];
    this.vec[1] = vec[1];
    this.vec[2] = vec[2];
    have_angles=false;

    /**************************
    * check the normalization *
    **************************/
    double length2 = vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2];
    if(Math.abs(length2 - 1.0)<1e-15) return;

    /************
    * normalize *
    ************/
   // System.out.println("normalizing "+(length2-1.0));
    double norm = 1.0/Math.sqrt(length2);
    this.vec[0] *= norm;
    this.vec[1] *= norm;
    this.vec[2] *= norm;

} // end of constructor from a unit vector



/***************************************************************************
* A utility method for calculating the latitude and longitude of the direction.
***************************************************************************/
private void calculateAngles() {

    lat = 90.0 - Math.toDegrees(Math.acos(vec[2]));

    if(vec[0]==0.0 && vec[1]==0.0) {
        lon = 0.0;
    } else {
        lon = Math.toDegrees(Math.atan2(vec[1], vec[0]));
    }

    if(lon < 0.0) lon += 360.0;

  //  System.out.println("calculated angles lon="+lon+" lat="+lat);

    have_angles = true;

} // end of calculateAngles method

/***************************************************************************
* Returns the angle along the equator from the X axis in degrees.
* @return the longitude of the point in degrees.
***************************************************************************/
public double getLongitude() {
    if(!have_angles) calculateAngles();
    return lon;
}

/***************************************************************************
* Returns the angle above the equator in degrees.
* @return the latitude in degrees.
***************************************************************************/
public double getLatitude() {

    if(!have_angles) calculateAngles();
    return lat;
}

/***************************************************************************
* Returns the X component of the unit vector pointing in this direction.
* @return The cartesian X component.
***************************************************************************/
public double getX() { return getUnitVector()[0]; }

/***************************************************************************
* Returns the Y component of the unit vector pointing in this direction.
* @return The cartesian Y component.
***************************************************************************/
public double getY() { return getUnitVector()[1]; }

/***************************************************************************
* Returns the Z component of the unit vector pointing in this direction.
* @return The cartesian Z component.
***************************************************************************/
public double getZ() { return getUnitVector()[2]; }

/***************************************************************************
* Returns the actual unit vector that this class uses to store the direction
* internally. If the direction was specified by angles, the unit vector
* is calculated.
***************************************************************************/
protected double[] getUnitVector() {

    /**********************************************
    * see if we need to calculate the unit vector *
    **********************************************/
    if(vec==null) {
        vec = new double[3];

        double sin = Math.sin(Math.toRadians(90.0-lat));

        vec[0] = sin* Math.cos(Math.toRadians(lon));
        vec[1] = sin* Math.sin(Math.toRadians(lon));

        vec[2] = Math.cos(Math.toRadians(90.0-lat));

    } // end if we need to calculate the unit vector

    return vec;

} // end of getUnitVector method


/***************************************************************************
* Returns a new array containing the unit vector representation of this
* direction.
* @return A new vector, {x, y, z}
***************************************************************************/
public double[] unitVector() {

    return getUnitVector();

    /*****************
    * copy the array *
    *****************/
//     double[] vec = getUnitVector();
//     double[] copy = {vec[0], vec[1], vec[2]};
//     return copy;

} // end of unitVector method

/***************************************************************************
* Returns the direction opposite to this one. The returned vector's
* cartesian coordinates are the negative of this ones.
* @return The opposite direction.
***************************************************************************/
public Direction oppositeDirection() {

    if(vec == null) {
        /*****************************
        * need to invert the angles *
        ****************************/
        return new Direction(Math.IEEEremainder(lon+180, 360), -lat);
    } else {
        /*************************
        * invert the unit vector *
        *************************/
        return new Direction(-vec[0], -vec[1], -vec[2]);
    }


} // end of oppositeDirection method

/***************************************************************************
* Returns the vector dot product of the unit vector pointing in this
* direction and and unit vector pointing in the given direction. This is
* the same as the cosine of the angle between the two directions and
* the component of this vector which is parallel to the given vector.
* @param dir The vector with which to dot this one.
* @return The cosine of the angle between the directions.
***************************************************************************/
private double dotProduct(Direction dir) {

    double[] vec1 = this.unitVector();
    double[] vec2 =  dir.unitVector();

    double dot = vec1[0]*vec2[0] +
                 vec1[1]*vec2[1] +
                 vec1[2]*vec2[2];

    /*******************************
    * guard against roundoff error *
    *******************************/
    if(dot >  1.0) dot =  1.0;
    if(dot < -1.0) dot = -1.0;

    return dot;

} // end of dotProduct method

/***************************************************************************
* Computes the cross product of the corresponding unit vectors.
* @param dir The unit vector to be "crossed" with.
* @return this cross dir
***************************************************************************/
private double[] crossProduct(Direction dir) {

    /**********************************************
    * calculate the cross product to get the sign *
    **********************************************/
    double[] vec1 = this.unitVector();
    double[] vec2 =  dir.unitVector();

    double[] cross = new double[3];

    cross[0] = vec1[1]*vec2[2] - vec1[2]*vec2[1];
    cross[1] = vec1[2]*vec2[0] - vec1[0]*vec2[2];
    cross[2] = vec1[0]*vec2[1] - vec1[1]*vec2[0];

    return cross;

   // return new Direction(cross);
}

/***************************************************************************
* Returns the angluar distance between this direction and another.
* This is obtained from the vector dot product of the two unit vectors,
* so it will always range between 0 and 180 degrees. The order of the
* two directions does not affect the returned value.
***************************************************************************/
public Angle angleBetween(Direction dir) {

    return Angle.createFromCos(dotProduct(dir));


} // end of angularDistance method

/***************************************************************************
* The angle between two direction with this direction as the vertex.
* Strictly speaking this is the angle between the planes containing this
* direction and each of the others. The angle is measured counter-clockwise
* for an observer at the center of the unit sphere (i.e. "look-up").
***************************************************************************/
public Angle angleBetween(Direction dir1, Direction dir2) {

    /*************************************************
    * find the normals to the great circles going
    * through this direction and each of the others
    *************************************************/
    Direction norm1 = perpendicular(dir1);
    Direction norm2 = perpendicular(dir2);

    /************************************************************
    * the cosine of the angle is the dot product of the normals
    ************************************************************/
    double cos = norm1.dotProduct(norm2);

    /**************************************************************
    * The sine of the angle is the cross product dotted into
    * this vector. Note we are sure to have our vector
    * components because of the operations above.
    **************************************************************/
    double[] cross = norm1.crossProduct(norm2);
    double sin = cross[0]*vec[0] +
                 cross[1]*vec[1] +
                 cross[2]*vec[2];

//     System.out.println(this);
//     System.out.println(dir2);
//     System.out.println("sign="+norm1.perpendicular(norm2).angleBetween(this).getCos());
//
//     System.out.println("sin="+sin+" cos="+cos);

    return new Angle(-sin, cos);

} // end of anguleBetween method

/***************************************************************************
* returns the angle between two directions, accounting for the order of the
* two directions with respect to a right handed coordinate system.
* The resulting angle can range from 0 to 360 degrees. Swapping the
* order of the two directions causes the sign of the result to flip.
* This is calculated by taking the dot product and the length of the
* cross product of the two vectors. So it is slower to compute than
* {@link angleBetween(Direction)}.
***************************************************************************/
// commented out because it doesn't work right. the sqrt wipes out
// direction information.
// public Angle angleTo(Direction dir) {
//
//     /******************************************
//     * get the ansolute value of the distance
//     * using the dot product
//     ******************************************/
//     double cos = dotProduct(dir);
//
//     double[] cross = crossProduct(dir);
//     double sin = Math.sqrt(cross[0]*cross[0] +
//                            cross[1]*cross[1] +
//                            cross[2]*cross[2]);
//
//     System.out.println("sin="+sin);
//
//     return new Angle(sin,cos);
//
// } // end of angleTo method

/*************************************************************************
* Returns the direction of the vector cross product with another
* direction. This new direction is perpendicular to the other two.
* @param dir another direction
* @return the direction of this x dir
*************************************************************************/
public Direction perpendicular(Direction dir) {

    return new Direction(crossProduct(dir));

} // end of perpendicular method

/***************************************************************************
* Returns true if the argument is a Direction representing exactly the
* same direction as this one. Note that floating point roundoff error
* may cause unexpected results if e.g. one direction was originally specified
* by angles and the other by a unit vector. A more robust way to compare
* directions might be to use {@link #angleBetween(Direction)}.
* @param o an object to compare to.
* @return true if the two directions are identical
* @throws ClassCastException if the argument is not a Direction.
****************************************************************************/
public boolean equals(Object o) {

    Direction dir = (Direction)o;

    if(vec==null && dir.vec==null) {
        /**********************
        * both are in lon/lat *
        **********************/
        return lon == dir.lon &&
               lat == dir.lat;

    } else if(!have_angles && !dir.have_angles) {
        /************************
        * both are unit vectors *
        ************************/
         return vec[0] == dir.vec[0] &&
                vec[1] == dir.vec[1] &&
                vec[2] == dir.vec[2];


    } else {
        /********************************
        * convert both to a unit vector *
        ********************************/
        double[] vec1 =     unitVector();
        double[] vec2 = dir.unitVector();

        return vec1[0] == vec2[0] &&
               vec1[1] == vec2[1] &&
               vec1[2] == vec2[2];

    }

} // end of equals method

/***************************************************************************
* Generates a string representation of the direction
* @return A string representation of the direction
***************************************************************************/
public String toString() {

    StringBuilder builder = new StringBuilder();

    /************************************
    * report the angles if we have them *
    ************************************/
    if(have_angles) {
        builder.append("Lon=")
               .append(getLongitude())
               .append(" Lat=")
               .append(getLatitude());
        if(vec != null) builder.append(" ");

    } // end if we have the angles

    /***********************************************
    * report the vector components if we have them *
    ***********************************************/
    if(!have_angles) builder.append("Direction ");

    if(vec != null) {
        builder.append("(")
               .append(getX())
               .append(", ")
               .append(getY())
               .append(" ")
               .append(getZ())
               .append(")");

    } // end if we have the unit vector

    return builder.toString();

//     return "Lon="+getLongitude()+" Lat="+getLatitude()+
//            "("+getX()+", "+getY()+" "+getZ()+")";
} // end f toString method


} // end of Direction class
