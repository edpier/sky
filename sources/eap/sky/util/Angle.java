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

/*************************************************************************
* Represents the general concept of an angle. Angles may be represented
* as a measure in degrees or radians, or as trigonometric functions.
* The main purposes of this class are to avoid confusion about the units
* of an angle argument, and to optimize code to avoid unnecessary trigonometric
* functions.
*************************************************************************/
public class Angle implements Comparable<Angle>, Serializable {

/** zero degrees **/
public static final Angle ANGLE0 = new Angle(0.0, 1.0);

/** 45 degrees **/
public static final Angle ANGLE45 = new Angle(45.0);

/** 90 degrees **/
public static final Angle ANGLE90 = new Angle(1.0, 0.0);

/** 180 degrees **/
public static final Angle ANGLE180 = new Angle(0.0, -1.0);

private double radians;
private double degrees;
private double sin;
private double cos;
private double tan;

boolean has_angle;
boolean has_sin;
boolean has_cos;
boolean has_tan;

/**************************************************************************
* Represents and angle and its trig functions.
**************************************************************************/
private Angle() {

    has_angle = false;
    has_cos   = false;
    has_sin   = false;
    has_tan   = false;

} // end of private constructor

/**************************************************************************
* Create a new angle specified in degrees.
* @param degrees the angle in degrees.
**************************************************************************/
public Angle(double degrees) {

    this();

    if(Double.isInfinite(degrees) || Double.isNaN(degrees)) {
        throw new IllegalArgumentException("Illegal angle: "+degrees);
    }

    while(degrees<   0.0) degrees += 360.0;
    while(degrees>=360.0) degrees -= 360.0;

    this.degrees = degrees;
    radians = Math.toRadians(degrees);
    has_angle = true;

}

/**************************************************************************
* Create a new angle specified by its trig functions.
* @param sin the sine of the angle.
* @param cos the cosine of the angle.
**************************************************************************/
public Angle(double sin, double cos) {

    this();

    this.sin = sin;
    this.cos = cos;

    has_sin = true;
    has_cos = true;

}

/**************************************************************************
* Create a new angle specified in radians
* @param radians the size of the angle in radians.
* @return The angle.
**************************************************************************/
public static Angle createFromRadians(double radians) {

    if(Double.isInfinite(radians) || Double.isNaN(radians)) {
        throw new IllegalArgumentException("Illegal angle: "+radians);
    }

    Angle angle = new Angle();

    while(radians < 0.0) radians += 2.0*Math.PI;
    while(radians >= 2.0*Math.PI) radians -= 2.0*Math.PI;

    angle.radians = radians;
    angle.degrees = Math.toDegrees(radians);
    angle.has_angle = true;

    return angle;
}

/**************************************************************************
* Create a new Angle specified in arc seconds.
* @param arcsec the angle measures in arc seconds.
* @return The angle.
**************************************************************************/
public static Angle createFromArcsec(double arcsec) {

    return new Angle(arcsec/3600.0);

} // end of createFromArcsec method


/**************************************************************************
* Create a new Angle, specifying only its sine.
* @param sin The sine of the angle.
* @return the angle.
**************************************************************************/
public static Angle createFromSin(double sin) {

    Angle angle = new Angle();

    angle.sin = sin;
    angle.has_sin = true;

    return angle;
}

/**************************************************************************
* Create a new Angle, specifying only its cosine.
* @param cos The cosine of the angle.
* @return the angle.
**************************************************************************/
public static Angle createFromCos(double cos) {

    Angle angle = new Angle();

    angle.cos = cos;
    angle.has_cos = true;

    return angle;
}

/**************************************************************************
* Create a new Angle, specifying only its tangent.
* @param tan The tangent of the angle.
* @return the angle.
**************************************************************************/
public static Angle createFromTan(double tan) {

    Angle angle = new Angle();

    angle.tan = tan;
    angle.has_tan = true;

    return angle;
}

/**************************************************************************
* Returns the size of the angle measured in degrees.
* @return the angle in degrees.
**************************************************************************/
public double getDegrees() {

    if(has_angle) return degrees;

    if(has_sin && has_cos) radians = Math.atan2(sin, cos);
    else if(has_sin)       radians = Math.asin(sin);
    else if(has_cos)       radians = Math.acos(cos);
    else if(has_tan)       radians = Math.atan(tan);

    degrees = Math.toDegrees(radians);

    /***************************************
    * force angles to be between 0 and 360 *
    ***************************************/
    if(degrees<0.0) {
        degrees += 360.0;
        radians += 2.0*Math.PI;
    }

    has_angle = true;
    return degrees;

} // end of getAngle method
/**************************************************************************
*
**************************************************************************/
public double getDegrees(double min) {



    double degrees = getDegrees();
    if     (degrees <= min      ) degrees += 360.0;
    else if(degrees >  min+360.0) degrees -= 360.0;

    return degrees;

} // end of getDegrees method

/**************************************************************************
* Returns the size of the angle measured in radians.
* @return the angle in radians.
**************************************************************************/
public double getRadians() {

    if(has_angle) return radians;

    getDegrees();
    return radians;

}  // end of getRadians method

/**************************************************************************
* Returns the size of the angle measured in seconds of arc (1/3600 degree).
* @return the angle in arc seconds.
**************************************************************************/
public double getArcsec() {

    return getDegrees() * 3600.0;
}

/**************************************************************************
* Returns the sine of the angle
* @return The sine of the angle.
**************************************************************************/
public double getSin() {

    if(has_sin) return sin;

    if(has_angle)    sin = Math.sin(radians);
    else if(has_cos) sin = Math.sqrt(1.0-cos*cos);
    else if(has_tan) sin = tan*getCos();

    has_sin = true;
    return sin;

} // end of getSin method

/**************************************************************************
* Returns the cosine of the angle
* @return The cosine of the angle.
**************************************************************************/
public double getCos() {

    if(has_cos) return cos;

    if(has_angle)    cos = Math.cos(radians);
    else if(has_sin) cos = Math.sqrt(1.0-sin*sin);
    else if(has_tan) cos = Math.sqrt(1.0/(tan*tan+1.0));

    has_cos = true;
    return cos;

} // end of getCos method

/**************************************************************************
* Returns the tangent of the angle
* @return The tangent of the angle.
**************************************************************************/
public double getTan() {

    if(has_tan) return tan;

   // System.out.println("calculating tangent");

    if(has_angle)               tan = Math.tan(radians);
    else if(has_sin && has_cos) tan = sin/cos;
    else if(has_sin)            tan = sin/getCos();
    else if(has_cos)            tan = getSin()/cos;

    has_tan = true;
    return tan;

} // end of getCos method

/**************************************************************************
* Add two angles. If the angles are specified by trig functions,
* then we use the trig addition formulae to calculate the sum.
* @param angle the angle to add to this one.
* @return this plus angle.
**************************************************************************/
public Angle plus(Angle angle) {


    if(has_angle && angle.has_angle) {
        /***********************
        * we can add the angles *
        ************************/
        return new Angle(degrees+ angle.degrees);

    } else if(has_sin && has_cos && angle.has_sin && angle.has_cos) {
        /**********************************************
        * we can use the trig angle addition formulas *
        **********************************************/
        double new_sin = sin * angle.cos + cos * angle.sin;
        double new_cos = cos * angle.cos - sin * angle.sin;

        return new Angle(new_sin, new_cos);

    } else {
        /********************************
        * we have to do some conversion *
        ********************************/
        return new Angle(getDegrees() + angle.getDegrees());
    }

} // end of plus method

/**************************************************************************
* Subtract two angles. If the angles are specified by trig functions,
* then we use the trig addition formulae to calculate the difference.
* @param angle the angle to add to this one.
* @return this minus angle.
**************************************************************************/
public Angle minus(Angle angle) {


    if(has_angle && angle.has_angle) {
        /***********************
        * we can add the angles *
        ************************/
        return new Angle(degrees - angle.degrees);

    } else if(has_sin && has_cos && angle.has_sin && angle.has_cos) {
        /**********************************************
        * we can use the trig angle addition formulas *
        **********************************************/
        double new_sin = sin * angle.cos - cos * angle.sin;
        double new_cos = cos * angle.cos + sin * angle.sin;

        return new Angle(new_sin, new_cos);

    } else {
        /********************************
        * we have to do some conversion *
        ********************************/
        return new Angle(getDegrees() - angle.getDegrees());
    }

} // end of plus method

/**************************************************************************
* Returns an angle which is half of this one.
**************************************************************************/
public Angle half() {

    if(has_angle) return new Angle(0.5*degrees);

    if(has_cos) {
        /**********************
        * half angle formulas *
        **********************/
        double half_sin = Math.sqrt(0.5*(1.0-cos));
        double half_cos = Math.sqrt(0.5*(1.0+cos));

        /************************************************
        * we assume the result is betwene 0 and 180.
        * In this range sin is always positive, but cos
        * is negative >90 (i.e. if the original angle is
        * > 180, i.e. if the original sin is negative
        *************************************************/
        if(has_sin && sin<0.0) half_cos = -half_cos;

       // return createFromSin(half_sin);
        return new Angle(half_sin, half_cos);
    }

    return new Angle(0.5*getDegrees());

} // end of half method

/***************************************************************************
*
***************************************************************************/
public Angle times(double value) {

    if(value == 0.5) return half();
    else return new Angle(getDegrees()* value);

} // end of times method

/**************************************************************************
* Return an angle with is the negative of this one.
* @return The negative of this angle.
**************************************************************************/
public Angle negative() {

    if(has_angle) return new Angle(-degrees);

    if(has_sin && has_cos) return new Angle(-sin, cos);

    if(has_sin) return createFromSin(-sin);

    return new Angle(-getDegrees());

} // end of negative method

/**************************************************************************
* Make a new angle with the same value.
* @return a copy of this angle.
**************************************************************************/
public Angle copy() {

    if(has_angle)          return new Angle(degrees);
    if(has_sin && has_cos) return new Angle(sin, cos);
    if(has_sin)            return createFromSin(sin);
    if(has_cos)            return createFromCos(cos);
                           return createFromTan(tan);

} // end of copy method

/**************************************************************************
* Checks if two angles are the same. Note that roundoff error may make to
* errors compare as unequal if they were specified in different ways.
* @param angle The angle to compare with this one.
* @return true if the two angles are identical.
**************************************************************************/
public boolean equals(Angle angle) {

    if(has_angle && angle.has_angle) {
        return degrees == angle.degrees;
    } else if(has_sin && has_cos && angle.has_sin && angle.has_cos) {
        return sin == angle.sin && cos == angle.cos;
    } else {
        return getDegrees() == angle.getDegrees();
    }

} // end of equals method

/**************************************************************************
* Compare two angles. Note this could behave in unexpected ways if the
* angles are not restricted to some range like 0 - 180 degrees.
* @param angle An Angle object to compare to
* @return 1 if this angle is greater than the argument, 0 if they are
* identical, and -1 otherwise.
* @throws ClassCastException is o is not an Angle.
**************************************************************************/
public int compareTo(Angle angle) {

    if(has_angle && angle.has_angle) {
        /*****************
        * compare angles *
        *****************/
    //    System.out.println("comparing degrees "+degrees+" to "+angle.degrees);
        if(     degrees >  angle.degrees) return 1;
        else if(degrees == angle.degrees) return 0;
        else                              return -1;

    } else if(has_sin && has_cos && angle.has_sin && angle.has_cos) {
        /*************************
        * compare trig functions *
        *************************/
//   System.out.println("comparing trig");
//   System.out.println("    sin="+sin+" cos="+cos);
//   System.out.println("    sin="+angle.sin+" cos="+angle.cos);
        if(sin == angle.sin || cos == angle.cos) return 0;

        if(sin>0 && angle.sin<0) return -1;
        if(sin<0 && angle.sin>0) return  1;

        int result = 1;
        if(cos > angle.cos) result = -1;

        if(sin<0.0) result = -result;

        return result;

    } else {
        /***************
        * force angles *
        ***************/
        if(     getDegrees() >  angle.getDegrees()) return 1;
        else if(getDegrees() == angle.getDegrees()) return 0;
        else                                        return -1;
    }



} // end of compareTo method

/**************************************************************************
* Returns a string representation of the angle.
* @return A string representation of the angle.
**************************************************************************/
public String toString() {

    return "Angle: "+getDegrees()+" deg";
}

} // end of Angle class
