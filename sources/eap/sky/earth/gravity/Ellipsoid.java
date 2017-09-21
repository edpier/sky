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

package eap.sky.earth.gravity;

import eap.sky.util.*;

/**************************************************************************
* A reference ellipsoid. This is an ellipsoid of revolution used as a
* reference reference surface for mapping. It describes an equipotential
* surface for an idealized Earth whose mass is distributed evenly.
* A refernce ellipsoid can be considered a first order approximation
* to the geoid, which is a true equipotential surface, taking local
* mass variations into account.
**************************************************************************/
public class Ellipsoid {

/*****************************************************************
* The standard ellipsoid used by GPS and the ITRF
* <a href="http://www.wgs84.com">http://www.wgs84.com</a>.
* This is important, since GPS altitudes are measured with respect to
* this surface.
*****************************************************************/
public static final Ellipsoid WGS84 = new Ellipsoid(6378137.0,
                                                    1./298.257223563,
                                                    0.00007292115,
                                                    3986004.418 * 1e8);

// /**********************************************************
// * A similar but older one.
// **********************************************************/
// public static final Earth GRS80 = new Earth(6378137.0,
//                                             1./298.257222101,
//                                             0.00007292115,
//                                             3986005.0 * 1e8);

double semi_major;
double flattening;
double angular_velocity; // rad/s
double GM;

double equatorial_gravity;
double polar_gravity;

double semi_minor;
double eccentricity;
double gravity_ratio;
double somigliana;


/**************************************************************************
* Create a new ellipsoid
* @param semi_major Semi-major axis of the ellipsoid in meters
* @param flattening The flattening of the ellipsoid = (a-b)/a, where
* a and b are the semi-major and semi-minor axes of the ellipsoid.
* @param angular_velocity The rotational velocity in radians per second.
* @param GM The mass, including atmosphere times the gravitational constant
**************************************************************************/
public Ellipsoid(double semi_major, double flattening,
                 double angular_velocity, double GM) {

    /***************************
    * copy the defining values *
    ***************************/
    this.semi_major = semi_major;
    this.flattening = flattening;
    this.angular_velocity = angular_velocity;
    this.GM = GM;

    /*****************
    * derived values *
    *****************/
    eccentricity = Math.sqrt(flattening*(2.0-flattening));
    semi_minor = (1.0-flattening)*semi_major;

    equatorial_gravity = gravity(0.0,0.0);
    polar_gravity = gravity(90.0,0.0);

    gravity_ratio = angular_velocity * angular_velocity *
                    semi_major * semi_major * semi_minor /GM;

    somigliana = (semi_minor/semi_major)*
                 (polar_gravity/equatorial_gravity) -1.0;

} // end of constructor

/***************************************************************************
* Returns the agular velocity of this ellipsoid.
* @return The angular velocity in radians per second.
***************************************************************************/
public double getAngularVelocity() { return angular_velocity; }

/***************************************************************************
* Returns the velocity of a point rotating with the ellipsoid. The velocity
* vector is in co-rotating terrestrial coordinates.
* @param location A position in space, with the center of the ellipsoid as
* an origin.
* @return The rotating velocity of that point in meters per second.
***************************************************************************/
public ThreeVector velocity(ThreeVector location) {

    return new ThreeVector(0.0, 0.0, angular_velocity).cross(location);

} // end of velocity method

/**************************************************************************
* Somigliana's equation for normal gravity on the surface of the ellipsoid.
* @param geodetic_latitude The geodetic latitude of the point. This describes
* the normal to the surface of the ellipsoid, not the spherical coordinates
* of the point.
* @return The gravity in m/s<sup>2</sup>
**************************************************************************/
public double gravityOnTheEllipsoid(double geodetic_latitude) {

    double sin = Math.sin(Math.toRadians(geodetic_latitude));
    double sin2 = sin*sin;

    return equatorial_gravity *
           (1.0 + somigliana * sin2)/
           Math.sqrt(1.0 - eccentricity*eccentricity * sin2);


} // end of

/**************************************************************************
* Calculates the gravity due to the idealized Earth with evenly
* distributed mass at an arbitrary point outside the ellipsoid.
* @param geodetic_latitude The angle between the local normal to the
*        ellipsoid and the semi-major axis of the ellipsoid.
* @param geodetic_height The distance to the ellipsoid, measured along
*        the normal to its surface.
* @return an exact calculation of the gravity at a point.
**************************************************************************/
public double gravity(double geodetic_latitude, double geodetic_height) {

    /*********************************
    * trig functions of the latitude *
    *********************************/
    geodetic_latitude = Math.toRadians(geodetic_latitude);
    double sin_lat = Math.sin(geodetic_latitude);
    double cos_lat = Math.cos(geodetic_latitude);
    double tan_lat = sin_lat / cos_lat;


    /***********************
    * the reduced latitude *
    ***********************/
    double tan_beta = (1.0 - flattening)*tan_lat;
    double sin_beta = tan_beta/Math.sqrt(1.0 + tan_beta*tan_beta);
    double cos_beta = 1.0;
    if(sin_beta != 0) cos_beta = sin_beta/tan_beta;

    double z_p = semi_minor * sin_beta + geodetic_height * sin_lat;
    double r_p = semi_major * cos_beta + geodetic_height * cos_lat;
    double z_p2 = z_p * z_p;
    double r_p2 = r_p * r_p;


    double d_pp2 = r_p2 - z_p2;
    double r_pp2 = r_p2 + z_p2;

    double E = eccentricity * semi_major;
    double E2 = E*E;

    double D = d_pp2/E2;
    double R = r_pp2/E2;

    double cos_beta_p2 = 0.5*(1.0 + R - Math.sqrt(1.0 + R*R - 2.0*D));
    double sin_beta_p2 = 1. - cos_beta_p2;

    double b_p2 = r_pp2 - E2*cos_beta_p2;
    double b_p = Math.sqrt(b_p2);

    double q0 = 0.5*(
                 (1.0+3.0*semi_minor*semi_minor/E2)*Math.atan(E/semi_minor)
                - 3.0 * semi_minor/E);

    double q_p = 3.0*(1.0 + b_p2/E2)*(1.0 - b_p/E*Math.atan(E/b_p)) -1.0;



    double W = Math.sqrt((b_p2 + E2*sin_beta_p2)/(b_p2 + E2));

    double gravity = 1.0/W*(
    GM/(b_p2 + E2)
    + angular_velocity*angular_velocity*semi_major*semi_major*E*q_p/
      ((b_p2 + E2)*q0)*
      (0.5*sin_beta_p2 - 1.0/6.0)
    - angular_velocity*angular_velocity * b_p * cos_beta_p2);

    return gravity;

} // end of gravity method

/*************************************************************************
* Appriximates the gravity at some point.
* This approximation is good for most purposes, and is faster to
* calculate than {@link #gravity(double, double)}.
* @param geodetic_latitude The angle between the local normal to the
*        ellipsoid and the semi-major axis of the ellipsoid.
* @param geodetic_height The distance to the ellipsoid, measured along
*        the normal to its surface.
* @return An approximate calculation of the gravity at a point.
*************************************************************************/
public double approxGravity(double geodetic_latitude,
                            double geodetic_height) {

    double sin_lat = Math.sin(Math.toRadians(geodetic_latitude));
    double sin_lat2 = sin_lat*sin_lat;

    double thing = 1.0
        - 2.0/semi_major*(1.0 + flattening
                          + gravity_ratio
                          - 2.0 * flattening*sin_lat2) *geodetic_height

        + 3.0*geodetic_height*geodetic_height/(semi_major*semi_major);

        return gravityOnTheEllipsoid(geodetic_latitude) * thing;
}

/*************************************************************************
* Calculates the surface of the ellipsoid.
* @param geocentric The geocentric latitude and longitude of the point.
* Note these are the spherical coordinates of the point, not the
* geodetic longitude and latitude, which refer to the normal to the surface.
* @return The radius of the ellipsoid in a given direction in meters.
*************************************************************************/
public double radius(Direction geocentric) {

//System.out.println("latitude="+geocentric.getLatitude());

    double latitude = Math.toRadians(geocentric.getLatitude());

    double x = semi_major * Math.cos(latitude);
    double y = semi_minor * Math.sin(latitude);

//     System.out.println("radius = "+Math.sqrt(x*x+y*y)+" alternate="+
//     semi_minor/Math.sqrt(1.0 - (semi_major*semi_major - semi_minor*semi_minor)/(semi_major*semi_major)* Math.cos(latitude)*Math.cos(latitude)));

    return Math.sqrt(x*x+y*y);

} // end of radius method

/**************************************************************************
* Creates a horizon calculate by modeling the Earth as a sphere with a radius
* equal to the radius of this Ellipsoid "under the feet" of the observer.
* @param position The location of the observer in meters.
* @throws IllegalArgumentException if the observer is under the surface of the
*         ellipsoid.
**************************************************************************/
public double estimateHorizonAltitude(ThreeVector position) {

    double radius = radius(position.getDirection());
    double distance = position.getLength();

    if(distance < radius) {
        throw new IllegalArgumentException("position is below surface of ellipsoid");
    }

    return -Math.toDegrees(Math.acos(radius/distance));

} // end of estimateHorizonAngle method

/*************************************************************************
* Converts a geodetic direction to a geocentric one. A geodetic direction
* is a normal to the ellipsoid. This method returns the direction from
* the center of the Earth to the point on the surface of the ellipsoid
* which has the given normal to the surface.
* @param geodetic A set of geodetic longitude and latitude
* @param height The height of a point above the ellipsoid.
* Currently this only works for height = 0.0.
* @return The geocentric longitude and latitude.
*************************************************************************/
public Direction toGeocentric(Direction geodetic, double height) {

    if(height!= 0.0) System.out.println("need to fix for height != 0");

    double[] vec = geodetic.unitVector();

//     double thing = eccentricity * vec[2];
//     double curvature = semi_major/(Math.sqrt(1.0 -thing*thing));

    double[] geocentric = { vec[0],
                            vec[1],
                           (1-eccentricity*eccentricity)*vec[2]};

    return new Direction(geocentric);

} // end of toGeocentric method

/*************************************************************************
* Determines the position of a point with respect to the center of the
* Ellipsoid.
* @param geodetic The direction normal to the surface of the ellipsoid
*        This gives the geodetic longitude and latitude
* @param height The height above the ellipsoid along the normal to its
*         surface.
* @return the position of the point with respect to the center of the Earth.
*************************************************************************/
public ThreeVector position(Direction geodetic, double height) {

    Direction geocentric = toGeocentric(geodetic, 0.0);
    double radius = radius(geocentric);

    ThreeVector to_ellipsoid = new ThreeVector(geocentric, radius);
    ThreeVector from_ellipsoid = new ThreeVector(geodetic, height);

    return to_ellipsoid.plus(from_ellipsoid);

} // end of position method

} // end of Ellipsoid class
