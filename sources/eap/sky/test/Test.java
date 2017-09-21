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

package eap.sky.test;

import eap.sky.time.*;
import eap.sky.time.barycenter.*;
import eap.sky.time.clock.*;
import eap.sky.time.cycles.*;
import eap.sky.util.*;
import eap.sky.util.numerical.*;
import eap.sky.earth.*;
import eap.sky.earth.gravity.*;
import eap.sky.earth.atmosphere.*;
import eap.sky.earth.atmosphere.monitors.*;
import eap.sky.ephemeris.*;
import eap.sky.util.coordinates.*;
import eap.sky.util.plane.*;

import eap.sky.chart.*;
import eap.sky.stars.*;
import eap.sky.stars.archive.*;
import eap.sky.stars.ingest.*;

import java.text.*;
import java.util.*;
import java.util.zip.*;
import java.net.URL;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

/***************************************************************************
* A collection of regression tests
***************************************************************************/
public class Test {

/***************************************************************************
* Reads leapsecond and EOP tables and sets the default UTC and UT1 systems
***************************************************************************/
public static boolean initTime() {

    ClassLoader loader = Test.class.getClassLoader();


    URL leapsec = loader.getResource("eap/sky/test/tai-utc.dat");
    URL eop_url = loader.getResource("eap/sky/test/finals2000A.all");

    if(leapsec == null) {
        System.out.println("could not find leapsec file");
        return false;
    }

    if(eop_url == null) {
        System.out.println("could not find EOP table");
        return false;
    }

    /*****************
    * initialize UT1 *
    *****************/
    try {
        UTCSystem.setDefaultLeapTable(new USNOLeapTable(leapsec));

        EOPTable eop = new EOPBulletin(eop_url, UTCSystem.getInstance(),
                                             TDBSystem.getInstance());

        UT1System.setDefaultEOPTable(eop);

    } catch(IOException e) {
        e.printStackTrace();
        return false;
    }

    return true;

} // end of configureUT1 method

/**************************************************************************
* Creates an observatory at a given location
**************************************************************************/
public static Observatory initObservatory(double longitude,
                                          double latitude,
                                          double height) {



    Direction up =new Direction(longitude, latitude);
    Ellipsoid wgs84 = Ellipsoid.WGS84;

    ThreeVector location = wgs84.position(up, height);

    ThreeVector gravity = new ThreeVector(up.oppositeDirection(),
                              wgs84.gravity(up.getLatitude(), height) );

    ThreeVector velocity = wgs84.velocity(location);


    Horizon horizon = new ConstantHorizon(
                                     wgs84.estimateHorizonAltitude(location));

    return new Observatory(location, height, gravity, velocity,
                           LocalTimeSystem.getInstance(), horizon);


} // end of initObservatory method

/**************************************************************************
* Creates an observatory appropriate for LURE/PS-1 on Haleakala
**************************************************************************/
public static Observatory initObservatory() {

    /********************************************
    * Lure position assuming the geoid is WGS84 *
    ********************************************/
    double longitude = 203.7441;
    double latitude  =  20.7072;
    double height    = 3062.658; // above ellipsoid

    Direction up =new Direction(longitude, latitude);
    Ellipsoid wgs84 = Ellipsoid.WGS84;

    ThreeVector location = wgs84.position(up, height);

    ThreeVector gravity = new ThreeVector(up.oppositeDirection(),
                              wgs84.gravity(up.getLatitude(), height) );

    ThreeVector velocity = wgs84.velocity(location);


    Horizon horizon = new ConstantHorizon(
                                     wgs84.estimateHorizonAltitude(location));

    return new Observatory(location, height, gravity, velocity,
                           LocalTimeSystem.getInstance(), horizon);


} // end of initObservatory method

/**************************************************************************
* Returns a set of weather values reasonable for Haleakala
**************************************************************************/
public static Weather initWeather() {

    return new Weather(71000.0, 5.0, new RelativeHumidity(0.20, false));

} // end of initWeather method

/**************************************************************************
*
**************************************************************************/
public static Refraction initRefraction(Weather weather, Observatory obs) {

    int wavelength = 550; // nm
    ConstantWeather monitor = new ConstantWeather(weather);
    return new SaastamoinenRefraction(wavelength, obs,
                                      monitor, monitor, monitor);

} // end of initRefraction method

/**************************************************************************
*
**************************************************************************/
public static Ephemeris initEphemeris() {

    return new JPLDE405Ephemeris(new JarJPLFileReader());


} // end of initEphemeris method

/**************************************************************************
*
**************************************************************************/
public static AzAlt initAzAlt() {

    initTime();

    Observatory obs = initObservatory();
    Weather weather = initWeather();
    Refraction refraction = initRefraction(weather, obs);
    Ephemeris ephemeris = initEphemeris();

    return new AzAlt(obs, refraction, ephemeris);

} // end of initAzAlt method

/**************************************************************************
*
**************************************************************************/
public static void initNight() {

    initTime();

    AzAlt az_alt = initAzAlt();
   EphemerisRiseSet sun = new EphemerisRiseSet(az_alt, 1.0);
   Night.setDefaultRiseSet(sun);

} // end of initNight method

/**************************************************************************
*
**************************************************************************/
public static boolean testRotation() {

    double phi = 10.0;
    double theta = 20.0;
    double psi = 30.0;

    double tol = 2e-14;

    Rotation rot  = new Rotation(new Euler(10.0, 20.0, 30.0));
    Rotation rot1;
    Rotation rot2;
    Euler euler2;
    double[][] matrix;

    Direction x_axis = Direction.X_AXIS;
    Direction y_axis = Direction.Y_AXIS;
    Direction z_axis = Direction.Z_AXIS;

    /********************************************
    * rotating a vector  about the Z axis
    * This should have x->-y, y->x, and z->z
    * Note we are transforming a fixed vector into a
    * new coordinates system.
    ********************************************/
    rot2 = new Rotation(Angle.ANGLE90, Direction.Z_AXIS);

    Direction new_x = rot2.transform(x_axis);
    Direction new_y = rot2.transform(y_axis);
    Direction new_z = rot2.transform(z_axis);

    Angle angle1 = new_x.angleBetween(y_axis.oppositeDirection());
    Angle angle2 = new_y.angleBetween(x_axis);
    Angle angle3 = new_z.angleBetween(z_axis);
    if(angle1.getDegrees() != 0.0 ||
       angle2.getDegrees() != 0.0 ||
       angle3.getDegrees() != 0.0) {
        System.out.println("rotation by 90 around Z failed");
        System.out.println("angle1="+angle1);
        System.out.println("angle2="+angle2);
        System.out.println("angle3="+angle3);
        System.out.println("X -> "+new_x);
        System.out.println("Y -> "+new_y);
        System.out.println("Z -> "+new_z);
        return false;
    }

    /********************************************
    * rotating a vector  about the Y axis
    * This should have x -> z, y -> y, z -> -x
    ********************************************/
    rot2 = new Rotation(Angle.ANGLE90, Direction.Y_AXIS);

    new_x = rot2.transform(x_axis);
    new_y = rot2.transform(y_axis);
    new_z = rot2.transform(z_axis);

    angle1 = new_x.angleBetween(z_axis);
    angle2 = new_y.angleBetween(y_axis);
    angle3 = new_z.angleBetween(x_axis.oppositeDirection());
    if(angle1.getDegrees() != 0.0 ||
       angle2.getDegrees() != 0.0 ||
       angle3.getDegrees() != 0.0) {
        System.out.println("rotation by 90 around Y failed");
        System.out.println("angle1="+angle1);
        System.out.println("angle2="+angle2);
        System.out.println("angle3="+angle3);
        System.out.println("X -> "+new_x);
        System.out.println("Y -> "+new_y);
        System.out.println("Z -> "+new_z);
        return false;
    }



    /********************************************
    * rotating a vector  about the X axis
    * This should have x->x, y->z, z->y
    ********************************************/
    rot2 = new Rotation(Angle.ANGLE90, Direction.X_AXIS);

    new_x = rot2.transform(x_axis);
    new_y = rot2.transform(y_axis);
    new_z = rot2.transform(z_axis);

    angle1 = new_x.angleBetween(x_axis);
    angle2 = new_y.angleBetween(z_axis.oppositeDirection());
    angle3 = new_z.angleBetween(y_axis);
    if(angle1.getDegrees() != 0.0 ||
       angle2.getDegrees() != 0.0 ||
       angle3.getDegrees() != 0.0) {
        System.out.println("rotation by 90 around X failed");
        System.out.println("angle1="+angle1);
        System.out.println("angle2="+angle2);
        System.out.println("angle3="+angle3);
        System.out.println("X -> "+new_x);
        System.out.println("Y -> "+new_y);
        System.out.println("Z -> "+new_z);
        return false;
    }


    /*******************************
    * check conversion to a matrix *
    *******************************/
    rot = new Rotation(Angle.ANGLE90, Direction.Z_AXIS);
    matrix = rot.getMatrix();
    new_x = rot.transform(x_axis);
    new_y = rot.transform(y_axis);
    new_z = rot.transform(z_axis);

//             System.out.println(matrix[0][0]+" "+
//                             matrix[0][1]+" "+
//                             matrix[0][2]);
//             System.out.println(matrix[1][0]+" "+
//                             matrix[1][1]+" "+
//                             matrix[1][2]);
//             System.out.println(matrix[2][0]+" "+
//                             matrix[2][1]+" "+
//                             matrix[2][2]);
//
//                            System.out.println();
//         System.out.println(new_x);
//         System.out.println(new_y);
//         System.out.println(new_z);




    double[] ux = new_x.unitVector();
    for(int i=0; i<3; ++i) {
        double err = ux[i] - matrix[i][0];
        if(Math.abs(err) > 1e-15) {
            System.out.println("rotation inconsistent with matrix");
            System.out.println("ux["+i+"]="+err);
           // return false;
        }
    }

    double[] uy = new_y.unitVector();
    for(int i=0; i<3; ++i) {
        double err = uy[i] - matrix[i][1];
        if(Math.abs(err) > 1e-15) {
            System.out.println("rotation inconsistent with matrix");
            System.out.println("uy["+i+"]="+err);
           // return false;
        }
    }

    double[] uz = new_z.unitVector();
    for(int i=0; i<3; ++i) {
        double err = uz[i] - matrix[i][2];
        if(Math.abs(err) > 1e-15) {
            System.out.println("rotation inconsistent with matrix");
            System.out.println("uy["+i+"]="+err);
          //  return false;
        }
    }

    /***************************
    * combination of rotations *
    ***************************/
    rot1 = new Rotation(Angle.ANGLE90, Direction.X_AXIS);
    rot2 = new Rotation(Angle.ANGLE90, Direction.Y_AXIS);
    Rotation combined = new Rotation(rot1, rot2);

    matrix = combined.getMatrix();

    double[][] correct_matrix = {{0.0, 1.0, 0.0},
                                 {0.0, 0.0, 1.0},
                                 {1.0, 0.0, 0.0}};

    for(int i=0; i<3; ++i) {
        for(int j=0; j<3; ++j) {
            if(Math.abs(matrix[i][j] - correct_matrix[i][j]) > 1e-15) {
                System.out.println("Combination of rotations failed");
                System.out.println("combined Matrix:");
                System.out.println(matrix[0][0]+" "+
                                   matrix[0][1]+" "+
                                   matrix[0][2]);
                System.out.println(matrix[1][0]+" "+
                                   matrix[1][1]+" "+
                                   matrix[1][2]);
                System.out.println(matrix[2][0]+" "+
                                   matrix[2][1]+" "+
                                   matrix[2][2]);

                System.out.println();

                System.out.println("correct Matrix:");
                System.out.println(correct_matrix[0][0]+" "+
                                   correct_matrix[0][1]+" "+
                                   correct_matrix[0][2]);
                System.out.println(correct_matrix[1][0]+" "+
                                   correct_matrix[1][1]+" "+
                                   correct_matrix[1][2]);
                System.out.println(correct_matrix[2][0]+" "+
                                   correct_matrix[2][1]+" "+
                                   correct_matrix[2][2]);
                return false;
            }
        }
    } // end of loop over matrix elements



    /************************************
    * ... and now check with transforms *
    ************************************/
    angle1 = combined.transform(Direction.X_AXIS).angleBetween(
                    rot2.transform(rot1.transform(Direction.X_AXIS)));


    angle2 = combined.transform(Direction.Y_AXIS).angleBetween(
                    rot2.transform(rot1.transform(Direction.Y_AXIS)));

    angle3 = combined.transform(Direction.Z_AXIS).angleBetween(
                    rot2.transform(rot1.transform(Direction.Z_AXIS)));

    if(angle1.getDegrees() != 0.0 ||
       angle2.getDegrees() != 0.0 ||
       angle3.getDegrees() != 0.0   ) {
        System.out.println("combined transform failed");
        System.out.println("angle1="+angle1);
        System.out.println("angle2="+angle2);
        System.out.println("angle3="+angle3);
        return false;
    }

    /*************************************************
    * ... finally check combineWith v.s. constructor *
    *************************************************/
    Rotation combined2 = (Rotation)rot1.combineWith(rot2);

    double[] q  = combined .getQuaternionComponents();
    double[] q2 = combined2.getQuaternionComponents();

    for(int i=0; i< 4; ++i) {
        if(Math.abs(q[i]-q2[i]) > 1e-15) {

            System.out.println("combineWith inconsistent with constructor");
            System.out.println(rot);
            System.out.println(rot2);
            return false;
        }
    }

    /*****************************
    * convert to matrix and back *
    *****************************/
    rot2 = new Rotation(rot.getMatrix());

    q  =  rot.getQuaternionComponents();
    q2 = rot2.getQuaternionComponents();

    for(int i=0; i< 4; ++i) {
        if(Math.abs(q[i]-q2[i]) > 1e-15) {

            System.out.println("error converting to matrix and back");
            System.out.println(rot);
            System.out.println(rot2);
            return false;
        }
    }

    /************************************
    * Conversion to Euler angles -
    * just a rotation around the Z axis *
    ************************************/
    rot2 = new Rotation(new Angle(phi), Direction.Z_AXIS);
    euler2 = rot2.getEulerAngles();

    if(Math.abs(phi   - euler2.getPhi()  ) > tol ||
       Math.abs(euler2.getTheta()) > tol ||
       Math.abs(euler2.getPsi()  ) > tol   ) {
        System.out.println("Conversion to Euler (Z) failed");
        System.out.println("phi error   = "+(euler2.getPhi() - phi));
        System.out.println("theta error = "+(euler2.getTheta()    ));
        System.out.println("psi error   = "+(euler2.getPsi()      ));
        System.out.println("euler2="+euler2);
        return false;
    }

    /************************************
    * Conversion to Euler -
    * just a rotation around the Y axis
    ************************************/
    rot2 = new Rotation(new Angle(theta), Direction.Y_AXIS);
    euler2 = rot2.getEulerAngles();

    if(Math.abs(euler2.getPhi()  ) > tol ||
       Math.abs(theta-euler2.getTheta()) > tol ||
       Math.abs(euler2.getPsi()  ) > tol   ) {
        System.out.println("Conversion to Euler (Y) failed");
        System.out.println("phi error   = "+(euler2.getPhi() ));
        System.out.println("theta error = "+(euler2.getTheta()- theta   ));
        System.out.println("psi error   = "+(euler2.getPsi()      ));
        System.out.println("euler2="+euler2);
        return false;
    }

    /***************************************************************
    * now check the combination of rotations about individual axes
    * compared with Euler angles
    ***************************************************************/
    rot2 = (Rotation)
            new Rotation(new Angle(phi),   Direction.Z_AXIS ).combineWith(
            new Rotation(new Angle(theta), Direction.Y_AXIS)).combineWith(
            new Rotation(new Angle(psi),   Direction.Z_AXIS));
    euler2 = rot2.getEulerAngles();

    if(Math.abs(phi   - euler2.getPhi()  ) > tol ||
       Math.abs(theta - euler2.getTheta()) > tol ||
       Math.abs(psi   - euler2.getPsi()  ) > tol   ) {
        System.out.println("Combination of single axis rotations failed");
        System.out.println("phi error   = "+(euler2.getPhi()   - phi));
        System.out.println("theta error = "+(euler2.getTheta() - theta));
        System.out.println("psi error   = "+(euler2.getPsi()   - psi));
        System.out.println("rot ="+rot);
        System.out.println("rot2="+rot2);
        System.out.println("phi  ="+euler2.getPhi());
        System.out.println("theta="+euler2.getTheta());
        System.out.println("psi  ="+euler2.getPsi());
        return false;
    }


    /*******************************
    * from Euler - just the Z axis *
    *******************************/
    rot2 = new Rotation(new Euler(phi, 0.0, 0.0));
    euler2 = rot2.getEulerAngles();
    if(Math.abs(euler2.getPhi() -phi) > 2e-15) {
        System.out.println("Conversion from Euler (Z) failed");
        System.out.println("error="+(euler2.getPhi() -phi));
        System.out.println("phi="+phi);
        System.out.println(euler2);
        return false;
    }

    /*****************************
    * convert to Euler and back *
    *****************************/
    euler2 = rot.getEulerAngles();

    if(Math.abs(phi   - euler2.getPhi()  ) > tol ||
       Math.abs(theta - euler2.getTheta()) > tol ||
       Math.abs(psi   - euler2.getPsi()  ) > tol   ) {
        System.out.println("Conversion through Euler failed");
        System.out.println("phi error   = "+(euler2.getPhi()   - phi));
        System.out.println("theta error = "+(euler2.getTheta() - theta));
        System.out.println("psi error   = "+(euler2.getPsi()   - psi));
        System.out.println("Euler: "+euler2);

        return false;
    }




    /****************************************************
    * check conversions through Euler angles and matrix *
    ****************************************************/
   matrix = rot.getMatrix();
    rot2 = new Rotation(matrix);
    euler2 = rot2.getEulerAngles();

    if(Math.abs(phi   - euler2.getPhi()  ) > tol ||
       Math.abs(theta - euler2.getTheta()) > tol ||
       Math.abs(psi   - euler2.getPsi()  ) > tol   ) {
        System.out.println("Conversion through matrix and Euler failed");
        System.out.println("phi error   = "+(euler2.getPhi()   - phi));
        System.out.println("theta error = "+(euler2.getTheta() - theta));
        System.out.println("psi error   = "+(euler2.getPsi()   - psi));
        System.out.println("rot= "+rot);
        System.out.println("rot2="+rot2);
        return false;
    }

    /*******************
    * test the inverse *
    *******************/
    rot2 = (Rotation)rot.invert().invert();
    euler2 = rot2.getEulerAngles();

    if(Math.abs(phi   - euler2.getPhi()  ) > tol ||
       Math.abs(theta - euler2.getTheta()) > tol ||
       Math.abs(psi   - euler2.getPsi()  ) > tol   ) {
        System.out.println("Rotation inversion failed");
        System.out.println("phi error   = "+(euler2.getPhi()   - phi));
        System.out.println("theta error = "+(euler2.getTheta() - theta));
        System.out.println("psi error   = "+(euler2.getPsi()   - psi));
        return false;
    }


    /********************************
    * now test RA/Dec/Roll -> Euler *
    ********************************/
    Direction dir = new Direction(10.0, 20.0);
    rot2 = (Rotation)new Rotation(new Euler(dir, 0.0)).invert();
  //  rot2 = new Rotation(new Euler(10, 70, 0.);

//       rot2 = (Rotation)new Rotation(10.0, Direction.Z_AXIS ).combineWith(
//                      new Rotation(70.0, Direction.Y_AXIS));
//
//     rot2 = (Rotation)new Rotation(70.0, Direction.Y_AXIS ).combineWith(
//                      new Rotation(10.0, Direction.Z_AXIS));

    new_z = rot2.transform(Direction.Z_AXIS);
    new_x = rot2.transform(Direction.X_AXIS);

    angle1 = new_x.angleBetween(Direction.X_AXIS);

    System.out.println(new_z);
    System.out.println("angle1="+angle1);



    System.out.println(rot2);
    System.out.println(rot1);



    return true;

} // end of testRotation method

/**************************************************************************
*
**************************************************************************/
// public static boolean testArc() {
//
//     Arc equator = new Arc(Direction.X_AXIS,
//                           Direction.X_AXIS.oppositeDirection(),
//                           Direction.Z_AXIS);
//
//     Arc meridian = new Arc(Direction.Z_AXIS,
//                            Direction.Z_AXIS.oppositeDirection(),
//                            Direction.Y_AXIS);
//
//     System.out.println("equator length="+equator.getLength());
//
//     long start = System.currentTimeMillis();
//     Direction[] intersections = equator.intersection(meridian);
//     long end = System.currentTimeMillis();
//
//     System.out.println(intersections[0]);
//     System.out.println(intersections[1]);
//     System.out.println("time = "+(end-start));
//     return true;
//
// } // end of testArc method

/**************************************************************************
*
**************************************************************************/
// public static boolean testDirection() {
//
//     Direction dir1 = new Direction(10.0, 0.0);
//     Direction dir2 = new Direction(20.0, 0.0);
//
//     Angle between1 = dir1.angleBetween(dir2);
//     Angle between2 = dir2.angleBetween(dir1);
//
//     System.out.println("between1 = "+between1+" between2="+between2);
//
//     Angle to1 = dir1.angleTo(dir2);
//     Angle to2 = dir2.angleTo(dir1);
//
//     System.out.println("to1="+to1+" to2="+to2);
//
//
//     return true;
//
// } // end of testDirection method

/**************************************************************************
*
**************************************************************************/
public static boolean testAngle() {

    /*****************
    * angle addition *
    *****************/
    Angle ang1 = new Angle(Math.sin(Math.toRadians(67.0)),
                           Math.cos(Math.toRadians(67.0)));

    Angle ang2 = new Angle(Math.sin(Math.toRadians(13.0)),
                           Math.cos(Math.toRadians(13.0)));

    Angle sum = ang1.plus(ang2);
    if(sum.getDegrees() - 80.0 > 1e-13) {
        System.out.println("angle addition failed");
        System.out.println(sum.getDegrees() - 80.0);
    }

    /*************
    * transforms *
    *************/
    double degrees = 33.0;
    double radians = Math.toRadians(degrees);
    double arcsec = degrees*3600.0;
    double sin = Math.sin(Math.toRadians(degrees));
    double cos = Math.cos(Math.toRadians(degrees));
    double tan = Math.tan(Math.toRadians(degrees));

    Angle ang = Angle.createFromCos(cos);
    System.out.println("cos to angle "+(ang.getDegrees() - degrees));

    ang = Angle.createFromCos(cos);
    System.out.println("cos to sin "+(ang.getSin() - sin));

    ang = Angle.createFromSin(sin);
    System.out.println("sin to cos "+(ang.getCos() - cos));

    ang = Angle.createFromSin(sin);
    System.out.println("sin to angle "+(ang.getDegrees() - degrees));

    ang = new Angle(degrees);
    System.out.println("angle to sin "+(ang.getSin() - sin));

    ang = new Angle(degrees);
    System.out.println("angle to cos "+(ang.getCos() - cos));

    ang = new Angle(degrees);
    System.out.println("angle to tan "+(ang.getTan() - tan));

    ang = new Angle(sin, cos);
    System.out.println("sin/cos to tan "+(ang.getTan() - tan));

    ang = Angle.createFromSin(sin);
    System.out.println("sin to tan "+(ang.getTan() - tan));

    ang = Angle.createFromCos(cos);
    System.out.println("cos to tan "+(ang.getTan() - tan));

    ang = Angle.createFromTan(tan);
    System.out.println("tan to angle "+(ang.getDegrees() - degrees));

    ang = Angle.createFromTan(tan);
    System.out.println("tan to sin "+(ang.getSin() - sin));

    ang = Angle.createFromTan(tan);
    System.out.println("tan to cos "+(ang.getCos() - cos));


    ang = Angle.createFromRadians(radians);
    System.out.println("radians to arcsec "+(ang.getArcsec() - arcsec));

    ang = new Angle(sin,cos);
    System.out.println("half angle "+
                       (ang.half().getDegrees() - 0.5*degrees));

    return true;

} // end of testAngle method

/**************************************************************************
* tests the construction of the polar motion matrix, not the
* actual polar motion values
**************************************************************************/
// public static boolean testPolarMotion() {
//
//     /******************
//     * read the tables *
//     ******************/
//     try {
//         UTCSystem.setDefaultLeapTable(new USNOLeapTable(
//                   new URL("file:///home/flaxen/pier/iers/ut1/tai-utc.dat")));
//
//         EOPTable eop = new EOPBulletin(
//                new URL("file:///home/flaxen/pier/iers/ut1/finals2000A.all"),
//                UTCSystem.getInstance(), TDBSystem.getInstance(),
//                EOPBulletin.BULLETIN_A|EOPBulletin.BULLETIN_B,
//                EOPCorrection.IERS2003);
//
//         UT1System.setDefaultEOPTable(eop);
//
//     } catch(IOException e) {
//         e.printStackTrace();
//         return false;
//     }
//
//
//     /**********************************************************
//     * we need to specify the TDB date to
//     * tidal arguments
//     **********************************************************/
//     JulianDate jd = new JulianDate(TDBSystem.getInstance());
//     jd.set(2453411.5);
//     PreciseDate tdb = jd.toDate();
//
//     TidalArguments args = new TidalArguments(tdb, null);
//
//
//     /********************************************************
//     * create the EOP and set some dummy polar motion values *
//     ********************************************************/
//     EOP eop = (EOP)UT1System.getInstance().createDate();
//     eop.setTime(0l, 0, 0.0,
//                 new PolarMotionParameters(Math.toDegrees(0.1)*3600.0,
//                                           Math.toDegrees(0.2)*3600.0,
//                                           0.0, 0.0),
//                 null, args);
//
//     Rotation polar = (Rotation)eop.polarMotion();
//
//     double[][] matrix = polar.getMatrix();
//
//     // this is the celestial to terrestrial matrix
// //     double[][] correct =
// //        {{ 0.9950041652780258, -1.1586048093148449E-11,0.09983341664682815},
// //         { 0.019833838087621982,0.9800665778410107,   -0.19767681165408385},
// //         {-0.09784339500494235, 0.19866933079620053,   0.9751703272018158 }};
//
//     double[][] correct =
//        {{ 0.9950041652780258,    0.019833838087621982,-0.09784339500494235},
//         {-1.1586048093148449E-11,0.9800665778410107,   0.19866933079620053},
//         { 0.09983341664682815,  -0.19767681165408385,  0.9751703272018158}};
//
//
//     for(int i=0; i< matrix.length; ++i) {
//         for(int j=0; j< matrix[i].length; ++j) {
//             if(matrix[i][j] != correct[i][j] ) {
//                 System.out.println("Polar motion matrix not correct:");
//
//         System.out.println(matrix[0][0]+" "+matrix[0][1]+" "+matrix[0][2]);
//         System.out.println(matrix[1][0]+" "+matrix[1][1]+" "+matrix[1][2]);
//         System.out.println(matrix[2][0]+" "+matrix[2][1]+" "+matrix[2][2]);
//
//         System.out.println("Should be:\n");
//     System.out.println(correct[0][0]+" "+correct[0][1]+" "+correct[0][2]);
//     System.out.println(correct[1][0]+" "+correct[1][1]+" "+correct[1][2]);
//     System.out.println(correct[2][0]+" "+correct[2][1]+" "+correct[2][2]);
//
//                 return false;
//             } // end if there was an error
//         }
//     } // end of loop over matrix elements
//
//
//     return true;
// } // end of testPolarMotion method

/**************************************************************************
*
**************************************************************************/
public static boolean testPrecession() {

    /***************************
    * get the precession model *
    ***************************/
    PrecessionModel p = IAU2000APrecession.getInstance();

    /**************************************************
    * pick a date at which to evaluate the precession *
    **************************************************/
    JulianDate jd = new JulianDate(TDBSystem.getInstance());
    jd.set(2453411.5);
    PreciseDate tdb = jd.toDate();
    TidalArguments args = new TidalArguments(tdb, null);

    long start = System.currentTimeMillis();
    double x = p.calculateX(args);
    double y = p.calculateY(args);
    double s = p.calculateS(args,x,y);
    long end = System.currentTimeMillis();

    System.out.println("runtime = "+(end-start)+" milliseconds");

    /*******************************************************
    * these are reference values calculated by this code
    * and checked against the IERS FORTRAN code
    ******************************************************/
    double correct_x =  4.8539508471034460E-4;
    double correct_y =  4.0505956862197515E-5;
    double correct_s = -1.4258381632095503E-8;

    /*************************************
    * check against the reference values *
    *************************************/
    if(x != correct_x || y != correct_y || s != correct_s) {
        System.out.println("Bad precession calculation!");
        System.out.println("    x        ="+x);
        System.out.println("    should_be="+correct_x);
        System.out.println("    error    ="+(x-correct_x));
        System.out.println();

        System.out.println("    y        ="+y);
        System.out.println("    should_be="+correct_y);
        System.out.println("    error    ="+(y-correct_y));
        System.out.println();

        System.out.println("    s        ="+s);
        System.out.println("    should_be="+correct_s);
        System.out.println("    error    ="+(s-correct_s));
        System.out.println();

        return false;
    }


//     System.out.println("x="+x);
//     System.out.println("y="+y);
//     System.out.println("s="+s);


   // Precession p = new IAU2000APrecession();


    return true;

} // end of testPrecession method

/****************************************************************************
*
****************************************************************************/
// public static boolean testAberration() {
//
//     /*********************************************
//     * velocity of the observer
//     * 1e-4 is roughly the orbital velocity of
//     * the Earth around the Sun
//     *********************************************/
//     double v = 1e-4;
//     double[] vhat = {1.0, 1.0, 1.0};
//     Direction velocity = new Direction(vhat);
//
//     /***********************
//     * create the transform *
//     ***********************/
//     Aberration aberration = new Aberration(v, velocity);
//
//     /*************************************
//     * create a position and transform it *
//     *************************************/
//     Direction actual = new Direction(20.0, 30.0);
//     Direction apparent = aberration.transform(actual);
//
//     double diff = actual.angularDistance(apparent);
// //     System.out.println("actual  ="+actual);
// //     System.out.println("apparent="+apparent);
// //     System.out.println("diff="+(diff*3600.0)+" arcsec");
//
//     /************************************************
//     * calculate the amplitude of the shift in the
//     * non-relativistic limit
//     ************************************************/
//     double classical = v * 206265 *
//                  Math.sin(Math.toRadians(actual.angularDistance(velocity)));
//
// //     System.out.println("classical="+classical);
// //     System.out.println("relativistic="+(diff*3600-classical));
//
//     if(Math.abs(diff*3600-classical) > 2e-3) {
//         /***************************************
//         * amplitude of shift appears wrong *
//         ***********************************/
//         System.out.println("amplitude of shift appears wrong");
//         return false;
//     }
//
//     /*******************************************************
//     * make sure the shift is away from the velocity vector *
//     *******************************************************/
//
//     double   actual_theta =   actual.angularDistance(velocity);
//     double apparent_theta = apparent.angularDistance(velocity);
//
// //     System.out.println("  actual_theta="+actual_theta);
// //     System.out.println("apparent_theta="+apparent_theta);
// //     System.out.println("velocity="+velocity);
//
//     if(apparent_theta <= actual_theta) {
//         System.out.println("abberation in the wrong direction");
//         return false;
//     }
//
//     /*************************************************************
//     * make sure the shift is in the plane of the velocity vector *
//     *************************************************************/
//     double diff2 = apparent_theta - actual_theta;
//     if(Math.abs(diff2 - diff) > 1e-9 ) {
//         System.out.println("diff2-diff="+(diff2-diff));
//         System.out.println("shift not in plane of velocity vector");
//         return false;
//     }
//
//     /*********************************************
//     * now check the magnitude of the change *
//     ****************************************/
//     vhat[0] = 1.0;
//     vhat[1] = 0.0;
//     vhat[2] = 0.0;
//
//     aberration = new Aberration(v, new Direction(vhat));
//     double[] r = {0.0, 1.0, 0.0};
//
//
//
//
//
//     return true;
// } // end of testAberration method

/**************************************************************************
*
**************************************************************************/
public static boolean testSaturationVaporPressure() {

    double[] celsius = {100.0, 20.0, -10.0};
    double[] correct = {101417.97792346643, 2339.214766781664,
                        259.90391523121383};
    boolean[] ice = {false, false, true};



    for(int i=0; i< celsius.length; ++i) {

        WaterVapor water = new RelativeHumidity(0.0, ice[i]);

        double saturation = water.saturation(celsius[i]);
        System.out.println("celsius="+celsius[i]+
                           " saturation="+saturation);

        if(saturation != correct[i]) {
            System.out.println("should be "+correct[i]);
           // return false;
        }
    } // end of loop oer test points

    return true;

} // end of testSaturationVaporPressure method

/**************************************************************************
*
**************************************************************************/
public static boolean testIndexOfRefraction() {

    /*************************************************************
    * a set of test points taken from
    * http://emtoolbox.nist.gov/Wavelength/Documentation.asp
    * The correct values are from calculations by this code, but
    * have been hand checked for consistency with the emtoolbox
    **************************************************************/
    double[] celsius  = {20.0    , 20.0, 40.0, 50.0, 40.0, 40.0};
    double[] humidity = {0.0     ,  0.0,  0.75, 1.0, 1.0, 1.0};
    double[] pressure = {101325.0, 60e3,  120e3, 120e3, 110e3, 110e3};
    double[] wavelength = {633.0 , 633.0, 633.0, 633.0, 1700.0, 300.0};
    double[] correct = {2.717998316251713E-4, 1.6092400171831666E-4,
                        2.994183101588353E-4, 2.8792402015687254E-4,
                        2.7024646350077284E-4, 2.89000156649701E-4};


    /****************************************
    * emtoolbox only uses the lab CO2 value *
    ****************************************/
    double co2_fraction =Weather.INDOOR_CO2;

    for(int i=0; i< pressure.length; ++i) {

        /*****************************
        * create a weather structure *
        *****************************/
        Weather weather = new Weather(pressure[i], celsius[i],
                                 new RelativeHumidity(humidity[i], false));

        double water_fraction = weather.waterFraction();

        /**********************
        * index of refraction *
        **********************/
        double index = weather.indexOfRefraction(wavelength[i],
                                                 water_fraction,
                                                   co2_fraction,
                                 weather.compressibility(water_fraction));

        System.out.println(i+" index="+index);

        if(index != correct[i]) {
            System.out.println("should be "+correct[i]);
            return false;
        }

    } // end of loop over test points





    return true;

} // end of testIndexOfRefraction method

/**************************************************************************
*
**************************************************************************/
public static boolean testRefraction() {

    /**************************************
    * Pulkovo observatory refraction data *
    **************************************/
    double[] zenith_angle = { 2.5,  5.0,  7.5, 10.0, 12.5, 15.0, 17.5, 20.0,
                            22.5, 25.0, 27.5, 30.0, 32.5, 35.0, 37.5, 40.0,
                            42.5, 45.0, 47.5, 50.0, 52.5, 55.0, 57.5, 60.0,
                            62.5, 65.0, 67.5, 70.0, 72.5, 75.0, 77.5, 80.0,
                            82.5, 85.0, 87.5};

    double[] pulkovo = {0.39661, 0.69847, 0.87594, 1.00282, 1.10225, 1.18454,
                        1.25519, 1.31752, 1.37366, 1.42508, 1.47286, 1.51779,
                        1.56050, 1.60150, 1.64120, 1.67997, 1.71814, 1.75601,
                        1.79386, 1.83198, 1.87067, 1.91025, 1.95108, 1.99355,
                        2.03817, 2.08552, 2.13635, 2.19165, 2.25276, 2.32156,
                        2.40083, 2.49489, 2.61101, 2.76222, 2.97385};

    double lambda = 590.0;


    Tropopause tropopause = Tropopause.STANDARD_1976;
    double co2_fraction = Weather.STANDARD_1976_CO2;

   // co2_fraction = 450.0;


  //  eap.sky.altaz.Refraction refraction = new eap.sky.altaz.Refraction();

    /*************************************************************
    * create a weather object with values corresponding to the
    * Pulkovo Observatory table
    *************************************************************/
    double pressure = 101325.0;
    Weather weather = new Weather(pressure, 15,
                                  new RelativeHumidity(0.0, false));

    Ellipsoid wgs84 = Ellipsoid.WGS84;
    double latitude = 45.0;
    double height =0.0;

    Direction geodetic = new Direction(0.0, latitude);
    double radius = wgs84.radius(wgs84.toGeocentric(geodetic,0.0));
    double gravity = wgs84.gravity(latitude, 0.0);



//     double gravity = 9.806197694258959; //45 deg latitude on WGS84
//     double radius = 6367453.6345163295; //45 deg latitude on WGS84


// System.out.println("radius double check="+
//
// Ellipsoid.WGS84.position(new Direction(0.0, 45.0),0.0).getLength());
//
// System.out.println("radius using geocentric lat=45 ="+
// Ellipsoid.WGS84.radius(new Direction(0.0, 45.0)));
//
// System.out.println("gravity double check="+(gravity-wgs84.gravity(45.0, 0.0)));

    System.out.println("radius="+radius);
    System.out.println("gravity="+gravity);


    ThreeVector location = new ThreeVector(new Direction(0.0, 45.0),
                                           radius);
    ThreeVector g        = new ThreeVector(new Direction(0,0,1), gravity);
    Observatory observatory = new Observatory(location, height, g, null,
                                              null, null);

    Deflection model = new SaastamoinenDeflection(weather,
                                                   co2_fraction,
                                                   lambda, tropopause,
                                                   observatory);

    for(int i=0; i< zenith_angle.length; ++i) {
 //   for(int i=0; i< 1; ++i) {
//         double angle =refraction.refractionAngle(zenith_angle[i], lambda);
//         angle = Math.toDegrees(angle)*3600.0;

        double angle = model.calculateDeflection(new Angle(zenith_angle[i]))
                            .getArcsec();

        double table =Math.pow(10.0, pulkovo[i]);


        double error = angle - table;

       // double ratio = angle/Math.pow(10.0,pulkovo[i]);

      //  double diff = angle1 - angle;

        System.out.println(zenith_angle[i]+" "+angle+" "+
                           table+" "+error);

    // System.out.println(zenith_angle[i]+" "+angle+" "+angle1+" diff="+diff);
    }



    return true;

} // end of testRefraction method

/**************************************************************************
*
**************************************************************************/
public static boolean testSounding() {

    ClassLoader loader = Test.class.getClassLoader();

    /*************************
    * read the sounding data *
    *************************/
    Sounding sounding = new Sounding();
    try {
        InputStream in =
                 loader.getResourceAsStream("eap/sky/test/sounding.html");

        sounding.read(in);

    } catch(Exception e) {
        e.printStackTrace();
        return false;
    }

    /**********************
    * loop over elevation *
    **********************/
    for(int i=0; i< 150; ++i) {

        double height = i*100.0;

        Weather weather = sounding.getWeather(height);

        double water_fraction = weather.waterFraction();
        double compressibility = weather.compressibility(water_fraction);

        double gamma = weather.indexOfRefraction(590.0,
                                                 water_fraction,
                                                 Weather.STANDARD_1976_CO2,
                                                 compressibility);
        System.out.println(height+" "+gamma);
    }



    return true;

} // end of testSounding method

/**************************************************************************
*
**************************************************************************/
public static boolean testEllipsoid() {

    Ellipsoid earth = Ellipsoid.WGS84;

    double latitude = 0.0;
    double height = 0.0;

    double somigliana = earth.gravityOnTheEllipsoid(latitude);
    double full = earth.gravity(latitude, height);

    double approx = earth.approxGravity(latitude, height);

    System.out.println("somig ="+somigliana);
    System.out.println("full  ="+full);
    System.out.println("approx="+approx);


    return true;

} // end of testEarth method

/**************************************************************************
*
**************************************************************************/
public static boolean testGeopotential() {

    Potential gravitation = new EGM96();
    Potential centrifugal = new CentrifugalPotential(7292115e-11);

    Potential potential = new CombinedPotential(gravitation, centrifugal);

    Ellipsoid ellipsoid = Ellipsoid.WGS84;

    double longitude = 0.0;
    double level = 62636856.88; // potential on surface of WGS84

    /********************************************
    * Aitoff projection aspected at the equator *
    ********************************************/
    Projection projection = Projection.AITOFF;
    Rotation rotation = new Rotation(new Euler(new Direction(0.0,0.0),0.0));
    rotation = (Rotation)rotation.invert();

    /******************
    * create an image *
    ******************/
    int n=360;
    double range = 6.0;

    double max = 2e-4;

    BufferedImage image = new BufferedImage(n, n/2,
                                            BufferedImage.TYPE_INT_RGB);
    /*******************
    * loop over pixels *
    *******************/
    for(int j=0; j< n/2; ++j) {
        double y = j * range/n - range/4.0;

        for(int i=0; i<n; ++i) {
            double x = i*range/(n-1) - range/2.0;

            Point2D point = new Point2D.Double(x,y);

            Direction dir = rotation.transform(projection.unproject(point));

           // System.out.println("x="+x+" y="+y+" dir="+dir);

            int value=0;
            if(dir != null) {
                double N = potential.surface(dir, level) -
                           ellipsoid.radius(dir);
  System.out.println(i+" "+j+" N="+N);
                value = (int)(N+100);
                if(value > 255) value = 255;
                if(value < 0  ) value = 0;
            }
            int rgb = value + (value<<8) + (value << 16) ;

          //  System.out.println(i+" "+x+" rgb="+rgb+" "+dir);
            image.setRGB(i,j,rgb);

        }
    }

    JFrame frame = new JFrame();
    frame.getContentPane().add(new JLabel(new ImageIcon(image)));
    frame.pack();
    frame.setVisible(true);

    return true;

} // end of testGeopotential method




/**************************************************************************
*
**************************************************************************/
public static void testGeopotential2() {

    /*************************
    * interesting directions *
    *************************/
    Direction pole    = new Direction(0.0, 90.0);
    Direction equator = new Direction(0.0,  0.0);

    double r = 6378136.3;

    /************************************
    * find the potential at this radius *
    ************************************/
    Potential gravitation = new EGM96();
    Potential centrifugal = new CentrifugalPotential(7292115e-11);

    Potential potential = new CombinedPotential(gravitation, centrifugal);

    System.out.println("potential at pole ="+
                       gravitation.potential(pole, r) );

    System.out.println("potential at equator ="+
                       gravitation.potential(equator, r) );

    System.out.println("centrifugal potential at the equator="+
                       centrifugal.potential(equator, r));

    /**********************
    * reference ellipsoid *
    **********************/
    Ellipsoid ellipsoid = Ellipsoid.WGS84;




    double level = 62636856.88; // potential on surface of WGS84

    double r_pole = potential.surface(pole, level);
    double r_pole_ellipsoid = ellipsoid.radius(pole);

    System.out.println("r_pole="+r_pole+
                       " r_pole_ellipsoid="+r_pole_ellipsoid+
                       " N="+(r_pole - r_pole_ellipsoid));

  //  System.out.println("r="+potential.surface(pole, 6.3e7));

    System.exit(0);

//     double level = potential.potential(r_ellipsoid, theta, phi);
//     level = -62636856.88; // potential on surface of WGS84
//
//     System.out.println("level = "+level);
//
//
//
//
//     /********************************************
//     * now look at other positions on the sphere *
//     ********************************************/
//     int npoints=21;
//     for(int i=0; i<npoints; ++i) {
//
//
//         theta =180.0*i/(double)(npoints-1);
//         phi = 0.0;
//         geocentric = new Direction(phi, 90.0-theta);
//
//         /****************************************
//         * now find the radius at that potential *
//         ****************************************/
//         double r_geoid = potential.geoid(level, theta, phi);
//         r_ellipsoid = ellipsoid.radius(geocentric);
//
//        // System.out.println("theta="+theta+" N="+(r_geoid - r_ellipsoid)+" "+r_ellipsoid);
//
//      //  System.out.println("theta="+theta+" r_ellipsoid="+r_ellipsoid+" r_geoid="+r_geoid);
//
//          System.out.println(theta+" "+r_ellipsoid+" "+r_geoid+" "+(r_geoid-r_ellipsoid));
//
//     }



} // end of testGeopotential3 method


/**************************************************************************
*
**************************************************************************/
public static void testGeopotential3() {

    Potential gravitation = new EGM96();
    Potential centrifugal = new CentrifugalPotential(7292115e-11);

    Potential potential = new CombinedPotential(gravitation, centrifugal);

    Ellipsoid ellipsoid = Ellipsoid.WGS84;

    double longitude = 0.0;
    double level = 62636856.88; // potential on surface of WGS84


    /**********************
    * loop ovcer latitude *
    **********************/
    int npoints=21;
    for(int i=0; i< npoints; ++i) {
        double latitude = 90 - 180.0 * i/(double)(npoints-1);

        Direction geocentric = new Direction(longitude, latitude);

        double r_geoid = potential.surface(geocentric, level);
        double r_ellipsoid = ellipsoid.radius(geocentric);

        double N = r_geoid - r_ellipsoid;

        System.out.println(latitude+" "+N);

    }

} // end of testGeopotential3 method

/**************************************************************************
*
**************************************************************************/
public static void UTCtoTTOffset() {

    try {
        UTCSystem.setDefaultLeapTable(new USNOLeapTable(
                  new URL("file:///home/flaxen/pier/iers/ut1/tai-utc.dat")));

        EOPTable eop = new EOPBulletin(
               new URL("file:///home/flaxen/pier/iers/ut1/finals2000A.all"),
               UTCSystem.getInstance(), TDBSystem.getInstance(),
               EOPBulletin.BULLETIN_A|EOPBulletin.BULLETIN_B,
               EOPCorrection.IERS2003);

        UT1System.setDefaultEOPTable(eop);

    } catch(IOException e) {
        e.printStackTrace();
        return;
    }


    PreciseDate utc = UTCSystem.getInstance().createDate();
    PreciseDate tt = TTSystem.getInstance().createDate();

    utc.setTime(System.currentTimeMillis());
    tt.setTime(utc);

    double offset = 1e-3*(tt.getMilliseconds() - utc.getMilliseconds());

    System.out.println("TT-UTC = "+offset);

} // end of UTCtoTTOffset

/**************************************************************************
*
**************************************************************************/
public static void GMSTcomparisons() {

    try {
        UTCSystem.setDefaultLeapTable(new USNOLeapTable(
                  new URL("file:///home/flaxen/pier/iers/ut1/tai-utc.dat")));

        EOPTable eop = new EOPBulletin(
               new URL("file:///home/flaxen/pier/iers/ut1/finals2000A.all"),
               UTCSystem.getInstance(), TDBSystem.getInstance(),
               EOPBulletin.BULLETIN_A|EOPBulletin.BULLETIN_B,
               EOPCorrection.IERS2003);

        UT1System.setDefaultEOPTable(eop);

    } catch(IOException e) {
        e.printStackTrace();
        return;
    }





    /*****************************
    * get 2003-01-01T00:00:00 TT *
    *****************************/
    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("GMT"));

    cal.set(cal.YEAR, 2003);
    cal.set(cal.MONTH, cal.JANUARY);
    cal.set(cal.DAY_OF_MONTH, 1);

    cal.set(cal.HOUR_OF_DAY, 0);
    cal.set(cal.MINUTE, 0);
    cal.set(cal.SECOND, 0);
    cal.set(cal.MILLISECOND, 0);

    PreciseDate tt = TTSystem.getInstance().createDate();
    tt.setTime(cal.getTime().getTime());

 //   System.out.println(new Date(tt.getMilliseconds()));

    for(int i=0; i<100; ++i) {

        GMSTcomparison(tt);
        tt.increment(3024*3600);
    }

} // end of GMSTcomparisons method




/**************************************************************************
*
**************************************************************************/
public static void GMSTcomparison(PreciseDate date) {

    /***************
    * create dates *
    ***************/
    PreciseDate tai = TAISystem.getInstance().createDate();
    PreciseDate tt  =  TTSystem.getInstance().createDate();
    PreciseDate ut1 = UT1System.getInstance().createDate();

    tai.setTime(date);
    ut1.setTime(tai);
    tt.setTime(tai);

//     PreciseDate utc = UTCSystem.getInstance().createDate();
//     utc.setTime(tai);
//
//     System.out.println("TAI-UT1="+(tai.getMilliseconds() -
//                                    ut1.getMilliseconds())/1000.0);
//
//     System.out.println("TAI-UTC="+(tai.getMilliseconds() -
//                                    utc.getMilliseconds())/1000.0);
//
//     System.out.println("UT1-UTC="+(ut1.getMilliseconds() -
//                                    utc.getMilliseconds())/1000.0);


    /******************
    * Julian Date UT1 *
    ******************/
    JulianDate jd_ut1 = new JulianDate(ut1);
    double Tu_days = (jd_ut1.getNumber() - 2451545) + jd_ut1.getFraction();
    double Tu_centuries = Tu_days /36525.0;

    /********************
    * Julian Date TT *
    *****************/
    JulianDate jd_tt = new JulianDate(tt);
    double t = ((jd_tt.getNumber() - 2451545) + jd_tt.getFraction())/36525.0;

    /***********************
    * Earth Rotation Angle *
    ***********************/
    double era = 0.7790572732640 + 1.00273781191135448*Tu_days;
    era = Math.IEEEremainder(era,1.0);

    /************
    * GMST 1982 *
    ************/
    double GMST1982 = jd_ut1.getFraction() - 0.5 +
                         (24110.54841+
                         (8640184.812866+
                         (0.093104 -6.2e-6*Tu_centuries)
                                          *Tu_centuries)
                                          *Tu_centuries)/(24.0*3600.0);

    GMST1982 = Math.IEEEremainder(GMST1982,1.0);

    /************
    * GMST 2003 *
    ************/
    double GMST2003 = era + (    0.014506   +
                            ( 4612.15739966 +
                            (    1.39667721 +
                            (  - 0.00009344 +
                            (    0.00001882 )
                                      * t ) * t ) * t ) * t )/(360.0*3600.0);

    double scale = 3600.0*360.0;
//     System.out.println(jd_tt.getModifiedJulianDate()+" "+
//                        era*scale+" "+GMST1982*scale+" "+GMST2003*scale);

    System.out.println(jd_tt.getModifiedJulianDate()+" "+
                       (GMST2003-GMST1982)*scale );



} // end of GMSTcomparison method


/**************************************************************************
*
**************************************************************************/
public static boolean testUT1() {

  //  System.out.println("reading tables");

    /******************
    * read the tables *
    ******************/
    try {
        UTCSystem.setDefaultLeapTable(new USNOLeapTable(
                  new URL("file:///home/flaxen/pier/iers/ut1/tai-utc.dat")));

        EOPTable eop = new EOPBulletin(
               new URL("file:///home/flaxen/pier/iers/ut1/finals2000A.all"),
               UTCSystem.getInstance(), TDBSystem.getInstance());

        UT1System.setDefaultEOPTable(eop);

    } catch(IOException e) {
        e.printStackTrace();
        return false;
    }
  //  System.out.println("done reading tables");


    /*********************
    * prepare a UTC date *
    *********************/
    JulianDate jd = new JulianDate(UTCSystem.getInstance());
    jd.setModifiedJulianDate(53422.5);
    UTCDate utc = (UTCDate)jd.toDate();

    /********************
    * create a UT1 Date *
    ********************/
    EOP ut1 = (EOP)UT1System.getInstance().createDate();

    /********************************
    * this is what it should be
    * using the IERS2003 correction
    ********************************/
    EOP should_be = (EOP)UT1System.getInstance().createDate();
    should_be.setTime(1108987199463l, 271732);

    /*********************
    * convert UTC -> UT1 *
    *********************/
    ut1.setTime(utc);

    System.out.println(utc);
    System.out.println(ut1);

    if(! ut1.equals(should_be)) {
        System.out.println("UTC to UT1 conversion failed ");
        System.out.println("is:        "+ut1);
        System.out.println("should be: "+should_be);
        System.out.println("error: "+ut1.secondsAfter(should_be)+" seconds");
        return false;
    }

    System.out.println();

    /**************************************************
    * the reverse is not implemented
    * we use this goofy fake loop to implement a goto
    **************************************************/
    while(true) {
        try { utc.setTime(ut1); }
        catch(NoSuchConversionException e) {break; }
        return false;
    }

    /*************
    * TAI to UT1 *
    *************/
    PreciseDate tai = TAISystem.getInstance().createDate();
    tai.setTime(utc);
    ut1.setTime(tai);

    System.out.println(ut1);
    System.out.println("UT1 measurement error = "+ut1.getTimeError());


    if(! ut1.equals(should_be)) {
        System.out.println("TAI to UT1 conversion failed");
    }

    System.out.println();

    /*********************************
    * now step through a leap second *
    *********************************/
    Calendar cal = Calendar.getInstance();
    cal.set(cal.MILLISECOND, 0);
    cal.setTimeZone(TimeZone.getTimeZone("GMT"));

    cal.set(cal.YEAR, 1998);
    cal.set(cal.MONTH, cal.DECEMBER);
    cal.set(cal.DAY_OF_MONTH, 31);

    cal.set(cal.HOUR_OF_DAY,   23);
    cal.set(cal.MINUTE, 59);
    cal.set(cal.SECOND, 59);

    utc.setTime(cal.getTime().getTime(), 0, false);
    tai.setTime(utc);

    EOP before = (EOP)UT1System.getInstance().createDate();
    before.setTime(tai);


    tai.increment(1.0);
    EOP during = (EOP)UT1System.getInstance().createDate();
    during.setTime(tai);
    utc.setTime(tai);
    if(!utc.isLeapSecond()) {
        System.out.println(utc+" is not a leap second");
        return false;
    }

    tai.increment(1.0);
    EOP after = (EOP)UT1System.getInstance().createDate();
    after.setTime(tai);

    System.out.println(before);
    System.out.println(during);
    System.out.println(after);

    double diff1 = during.secondsAfter(before);
    double diff2 =  after.secondsAfter(during);

    System.out.println(diff1-1);
    System.out.println(diff2-1);

    if(Math.abs(diff1 - 1) > 2e-8 || Math.abs(diff2 - 1) > 2e-8) {
        System.out.println("bad leap second handling");
        return false;
    }

    return true;

} // end of testUT1 method

/**************************************************************************
*
**************************************************************************/
public static boolean testEphemeris() {

    /*****************
    * initialize UT1 *
    *****************/
    try {
        UTCSystem.setDefaultLeapTable(new USNOLeapTable(
                  new URL("file:///home/flaxen/pier/iers/ut1/tai-utc.dat")));

        EOPTable eop = new EOPBulletin(
               new URL("file:///home/flaxen/pier/iers/ut1/finals2000A.all"),
               UTCSystem.getInstance(), TDBSystem.getInstance());

        UT1System.setDefaultEOPTable(eop);

    } catch(IOException e) {
        e.printStackTrace();
        return false;
    }



    /*******************
    * create ephemeris *
    *******************/
    Ephemeris ephemeris = initEphemeris();


    /**************************
    * get a contemporary date *
    **************************/
    PreciseDate utc = UTCSystem.getInstance().createDate();
    utc.setTime(1114460383192l, 0);

    PreciseDate tdb = TDBSystem.getInstance().createDate();
    tdb.setTime(utc);

    EOP eop = (EOP)UT1System.getInstance().convertDate(tdb);

    /********************************
    * get the position of the Earth *
    ********************************/
    ThreeVector correct = new ThreeVector(-1.2167671028876007E11,
                                          -8.040764894107936E10,
                                          -3.487757525416001E10);


    ThreeVector position = ephemeris.barycentricPosition(ephemeris.EARTH,
                                                            tdb);
    if(!position.equals(correct)) {
        System.out.println("incorrect barycentric position for the Earth");
        System.out.println("position ="+position);
        System.out.println("should be "+correct);

        System.out.println("error = "+position.minus(correct));
        return false;
    }

    /***************************************
    * Now the topocentric position of mars *
    ***************************************/

    /****************
    * LURE position *
    ****************/
    ThreeVector location = Ellipsoid.WGS84.position(
                                          new Direction(203.7441, 20.7072),
                                            3062.658);

    Observatory obs = new Observatory(location, 3062.658,
                                      new ThreeVector(0,0,0),
                                      Ellipsoid.WGS84.velocity(location),
                                      LocalTimeSystem.getInstance(), null);

    position = ephemeris.position(ephemeris.MARS, tdb, eop, obs);

    correct = new ThreeVector(1.7661661882887326E11,
                              -1.0663404277368039E11,
                              -5.2402985091991844E10);

    if(!position.equals(correct)) {
        System.out.println("incorrect position for Mars");
        System.out.println("position ="+position);
        System.out.println("should be "+correct);

        System.out.println("error = "+position.minus(correct));
        return false;
    }

    /********************
    * now test velocity *
    ********************/
    ThreeVector velocity = ephemeris.barycentricVelocity(ephemeris.VENUS,
                                                         tdb);

    ThreeVector position1 = ephemeris.barycentricPosition(ephemeris.VENUS,
                                                          tdb);

    PreciseDate tdb2 = tdb.getTimeSystem().createDate();
    tdb2.setTime(tdb);
    tdb2.increment(1.0);

    ThreeVector position2 = ephemeris.barycentricPosition(ephemeris.VENUS,
                                                          tdb2);

    ThreeVector diff = position2.minus(position1);
    ThreeVector error = velocity.minus(diff);

    if(error.getLength() > 0.01) {
        System.out.println("Velocity is wrong:");
        System.out.println("computed velocity:  "+velocity);
        System.out.println();
        System.out.println("numerical velocity: "+diff);
        System.out.println();
        System.out.println("error="+error);
        return false;
    }


    return true;

} // end of testEphemeris method

/**************************************************************************
*
**************************************************************************/
public static boolean testGravitationalDeflection() {

//     Transform trans = new GravitationalDeflection(
//                        new ThreeVector(new Direction(0,0,1), 149597870.691e3),
//                            1.32712438e20);

    Deflection deflection = new GravitationalDeflection(1.32712438e20,
                                                        149597870.691e3);

    Transform trans = new DeflectionTransform(deflection,
                                              new Direction(0,0,1),
                                              false);


    double[] angle = {0.25, 0.5, 1.0, 2.0, 5.0, 10.0, 20.0, 50.0, 90.0};

    /***************************************************
    * these numbers checked against Seidelman page 138 *
    ***************************************************/
    double[] correct = {1.8664306980326728,
                        0.9332109070271599,
                        0.46659656884457945,
                        0.23328051718181086,
                        0.09326245150873547,
                        0.04654233376299999,
                        0.023093043176913852,
                        0.00873227467934612,
                        0.00407192661668887};


    for(int i=0; i< angle.length; ++i) {

        Direction dir0 = new Direction(0.0, 90.0-angle[i]);
        Direction dir1 = trans.transform(dir0);

        double delta = -3600.0 *(dir1.getLatitude() - dir0.getLatitude());


        if(delta != correct[i]) {
            System.out.println("angle="+angle[i]+
                               " delta= "+delta+
                               " correct="+correct[i]+
                               " error="+(delta-correct[i]));
            return false;
        }

    }

    /*********************
    * now test inversion *
    *********************/
    Transform inverse = trans.invert();

    for(int i=0; i< angle.length; ++i) {

        Direction dir0 = new Direction(0.0, 90.0-angle[i]);
        Direction dir1 = inverse.transform(trans.transform(dir0));

        double error =dir0.angleBetween(dir1).getDegrees();
        if(error >0.0) {
            System.out.println("error transforming and then inverting");
            System.out.println("angle="+angle[i]);
            System.out.println("error="+error);
            System.out.println("dir0="+dir0);
            System.out.println("dir1="+dir1);
            return false;
        }

    }

    return true;
} // end of testDeflection

/**************************************************************************
*
**************************************************************************/
public static boolean testEarthRotation() {
    /*****************
    * initialize UT1 *
    *****************/
    try {
        UTCSystem.setDefaultLeapTable(new USNOLeapTable(
                  new URL("file:///home/flaxen/pier/iers/ut1/tai-utc.dat")));

        EOPTable eop = new EOPBulletin(
               new URL("file:///home/flaxen/pier/iers/ut1/finals2000A.all"),
               UTCSystem.getInstance(), TDBSystem.getInstance());

        UT1System.setDefaultEOPTable(eop);

    } catch(IOException e) {
        e.printStackTrace();
        return false;
    }


    /****************
    * LURE location *
    ****************/
    double longitude = 203.7441;
    double latitude  =  20.7072;
    double height = 3062.658;

    Direction vertical = new Direction(longitude, latitude);

    Ellipsoid wgs84 = Ellipsoid.WGS84;

    ThreeVector location = wgs84.position(vertical, height);
    ThreeVector local_velocity = wgs84.velocity(location);

    ThreeVector gravity = new ThreeVector(0,0,0);

    Observatory obs = new Observatory(location, height, gravity,
                                      local_velocity, null, null);


  //  System.out.println("location="+location);

    /**************
    * pick a time *
    **************/
    PreciseDate utc = UTCSystem.getInstance().createDate();
    utc.setTime(1114460373192l, 0);

    EOP eop = (EOP)UT1System.getInstance().createDate();
    eop.setTime(utc);

    /************************
    * position and velocity *
    ************************/
    double era0 = eop.earthRotationAngle();
    Rotation rot0 = eop.terrestrialToCelestial();

    ThreeVector position0 = obs.celestialPosition(eop);
    ThreeVector velocity  = obs.diurnalVelocity(eop);

    /***********************************************************
    * increment the UTC time so we can calculate
    * a numerical derivative of the position.
    * a time interval of 0.1 seems to give the best accuracy
    **********************************************************/
    double delta_time = 0.1;
    utc.increment(delta_time);
    eop.setTime(utc);

    double era1 = eop.earthRotationAngle();
    Rotation rot1 = eop.terrestrialToCelestial();

    ThreeVector position1 = obs.celestialPosition(eop);
    ThreeVector diff = position1.minus(position0).times(1./delta_time);


    ThreeVector error = velocity.minus(diff);

    if(error.getLength() > 0.002) {
        System.out.println("Error in the Earth's rotational velocity:");
        System.out.println(error);
        System.out.println();


        Rotation delta_rot = (Rotation)rot0.invert().combineWith(rot1);
        System.out.println("3-D rotation in "+delta_time+" seconds:");
        System.out.println("delta_rot="+delta_rot);
        System.out.println("axis="+delta_rot.getAxis());
        System.out.println("angle/delta_t="+
                           delta_rot.getAngle()/delta_time+" degrees/s");
        System.out.println();


        System.out.println("era0="+era0+" degrees");
        System.out.println("era1="+era1+" degrees");
        System.out.println("diff/delta_time="+(era1-era0)/delta_time);
        System.out.println("WGS84          ="+
                           Math.toDegrees(0.00007292115));
        System.out.println();

        System.out.println("position0="+position0);
        System.out.println("position1="+position1);

        System.out.println();
        System.out.println("velocity="+velocity);
        System.out.println();
        System.out.println("numerical="+diff);

        return false;
    }


    return true;

} // end of testRotation method

/**************************************************************************
*
**************************************************************************/
// public static boolean testAzAlt() {
//
//     double longitude = 90.0;
//     double latitude = 0.0;
//     Direction up = new Direction(longitude, latitude);
//
//     ThreeVector location = new ThreeVector(up, 1.0);
//     ThreeVector gravity = new ThreeVector(up.oppositeDirection(), 1.0);
//     double height = 0.0;
//     ThreeVector velocity = new ThreeVector(0.0, 0.0, 0.0);
//
//     Observatory obs = new Observatory(location, height, gravity, velocity);
//
//     Transform rot = obs.azAltToTerrestrial();
//
//
//
//     System.out.println(rot.getEulerAngles());
//
//     Direction north = new Direction(0.0, 0.0);
//     Direction east = new Direction(90.0, 0.0);
//     Direction south = new Direction(180.0, 0.0);
//     Direction west = new Direction(270.0, 0.0);
//
//     System.out.println();
//     System.out.println("north="+rot.transform(north));
//     System.out.println("west="+rot.transform(west));
//     System.out.println("south="+rot.transform(south));
//     System.out.println("east="+rot.transform(east));
//
//     return true;
//
// } // end of testAzAlt method

/**************************************************************************
*
**************************************************************************/
public static boolean testAzAltRaDec() {

    /*****************
    * initialize UT1 *
    *****************/
    try {
        UTCSystem.setDefaultLeapTable(new USNOLeapTable(
                  new URL("file:///home/flaxen/pier/iers/ut1/tai-utc.dat")));

        EOPTable eop = new EOPBulletin(
               new URL("file:///home/flaxen/pier/iers/ut1/finals2000A.all"),
               UTCSystem.getInstance(), TDBSystem.getInstance());

        UT1System.setDefaultEOPTable(eop);

    } catch(IOException e) {
        e.printStackTrace();
        return false;
    }

    /******************************
    * create a UTC and a UT1 date *
    ******************************/
    PreciseDate utc = UTCSystem.getInstance().createDate();
    EOP eop = (EOP)UT1System.getInstance().createDate();
    PreciseDate tdb = TDBSystem.getInstance().createDate();

    /********************************************
    * Lure position assuming the geoid is WGS84 *
    ********************************************/
    double longitude = 203.7441; // measured east.
    double latitude  =  20.7072;
    double height    = 3062.658; // above ellipsoid

    Direction up =new Direction(longitude, latitude);
    Ellipsoid wgs84 = Ellipsoid.WGS84;

    ThreeVector location = wgs84.position(up, height);

    ThreeVector gravity = new ThreeVector(up.oppositeDirection(),
                              wgs84.gravity(up.getLatitude(), height) );

    ThreeVector velocity = wgs84.velocity(location);


    Observatory obs = new Observatory(location, height, gravity,
                                      velocity, null, null);

    /*********************
    * weather conditions *
    *********************/
    Weather weather = new Weather(65768.0, 5.0,
                                  new RelativeHumidity(0.20, false));


    Deflection refraction = new SaastamoinenDeflection(weather,
                                           Weather.STANDARD_1976_CO2,
                                           550.0, // V filter
                                           Tropopause.STANDARD_1976,
                                           obs);


    /************
    * ephemeris *
    ************/
    Ephemeris ephemeris = initEphemeris();

    /*******
    * time *
    *******/
    Calendar cal = Calendar.getInstance();
    cal.set(cal.YEAR, 2005);
    cal.set(cal.MONTH, cal.JUNE);
    cal.set(cal.DAY_OF_MONTH, 13);
    cal.set(cal.SECOND, 0);
    cal.set(cal.MILLISECOND, 0);

    // sunrise
    cal.set(cal.HOUR_OF_DAY, 5);
    cal.set(cal.MINUTE, 49);

    // sunset
    cal.set(cal.HOUR_OF_DAY, 19);
    cal.set(cal.MINUTE, 14);

    utc.setTime(cal.getTime());

    eop.setTime(utc);
    tdb.setTime(utc);



    /*************************
    * assemble the transform *
    *************************/
//     Transform toSky = obs.azAltToTerrestrial().combineWith(
//                       new DeflectionTransform(refraction,
//                                               obs.getZenith())).combineWith(
//                       eop.polarMotion()).combineWith(
//                       eop.earthRotation()).combineWith(
//                       eop.precession()).combineWith(
//                       ephemeris.aberration(tdb, eop, obs)).combineWith(
//                       ephemeris.deflection(tdb, obs) );

    Transform toSky = obs.azAltToTerrestrial()
           .combineWith(new DeflectionTransform(refraction,obs.getZenith()))
           .combineWith(eop.polarMotion())
           .combineWith(eop.earthRotation())
           .combineWith(eop.precession())
           .combineWith(ephemeris.aberration(tdb, eop, obs))
           .combineWith(ephemeris.deflection(tdb, eop, obs) )
                  ;

    Transform fromSky = toSky.invert();

    Direction sun = ephemeris.position(ephemeris.SUN, tdb, eop, obs)
                             .getDirection();
System.out.println();
     System.out.println(new Date(utc.getMilliseconds()));
System.out.println();
System.out.println("transforming");
    Direction apparent = fromSky.transform(sun);

  //  Direction back = toSky.transform(apparent);
    System.out.println();
    System.out.println("ra/dec="+sun);
    System.out.println();
    System.out.println("az/alt="+apparent);
    System.out.println();
  //  System.out.println("ra/dec="+back);



System.out.println(obs.azAltToTerrestrial().transform(Direction.Z_AXIS));




    return true;

} // end of testAltAzRaDec

/**************************************************************************
*
**************************************************************************/
public static void distortionMap() {

    /*****************
    * initialize UT1 *
    *****************/
    try {
        UTCSystem.setDefaultLeapTable(new USNOLeapTable(
                  new URL("file:///home/flaxen/pier/iers/ut1/tai-utc.dat")));

        EOPTable eop = new EOPBulletin(
               new URL("file:///home/flaxen/pier/iers/ut1/finals2000A.all"),
               UTCSystem.getInstance(), TDBSystem.getInstance());

        UT1System.setDefaultEOPTable(eop);

    } catch(IOException e) {
        e.printStackTrace();
        return;
    }

    /******************************
    * create a UTC and a UT1 date *
    ******************************/
    PreciseDate utc = UTCSystem.getInstance().createDate();
    EOP eop = (EOP)UT1System.getInstance().createDate();
    PreciseDate tdb = TDBSystem.getInstance().createDate();

    /********************************************
    * Lure position assuming the geoid is WGS84 *
    ********************************************/
    double longitude =203.7441;
    double latitude =  20.7072;
    double height =3062.658; // above ellipsoid

    Direction up =new Direction(longitude, latitude);
    Ellipsoid wgs84 = Ellipsoid.WGS84;

    ThreeVector location = wgs84.position(up, height);

    ThreeVector gravity = new ThreeVector(up.oppositeDirection(),
                              wgs84.gravity(up.getLatitude(), height) );

    ThreeVector velocity = wgs84.velocity(location);


    Observatory obs = new Observatory(location, height, gravity,
                                      velocity, null, null);

    /*********************
    * weather conditions *
    *********************/
    Weather weather = new Weather(65768.0, 5.0,
                                  new RelativeHumidity(0.20, false));


    Deflection refraction = new SaastamoinenDeflection(weather,
                                           Weather.STANDARD_1976_CO2,
                                           550.0, // V filter
                                           Tropopause.STANDARD_1976,
                                           obs);


    /************
    * ephemeris *
    ************/
    Ephemeris ephemeris = initEphemeris();

    /***************
    * current time *
    ***************/
    utc.setTime(1114815503721l - 1000*3600*8);
    eop.setTime(utc);
    tdb.setTime(utc);



    /*************************
    * assemble the transform *
    *************************/
    Transform toSky = obs.azAltToTerrestrial()
                      .combineWith(new DeflectionTransform(refraction,
                                                           obs.getZenith()))
                      .combineWith(eop.polarMotion())
                      .combineWith(eop.earthRotation())
                      .combineWith(eop.precession())
                      .combineWith(ephemeris.aberration(tdb, eop, obs))
                      .combineWith(ephemeris.deflection(tdb, eop, obs) )
                  ;

    Transform to_sky_rot =obs.azAltToTerrestrial()
                      .combineWith(new DeflectionTransform(refraction,
                                                          obs.getZenith()))
                      .combineWith(eop.polarMotion())
                      .combineWith(eop.earthRotation())
                      .combineWith(eop.precession())
                   //   .combineWith(ephemeris.aberration(tdb, eop, obs))
                   //   .combineWith(ephemeris.deflection(tdb, obs) )
                  ;

    Transform fromSky = toSky.invert();

     Direction sun = ephemeris.position(ephemeris.SUN, tdb, eop, obs)
                              .getDirection();

    Direction apparent = fromSky.transform(sun);

   // Direction back = toSky.transform(apparent);

    System.out.println("ra/dec="+sun);
    System.out.println("az/alt="+apparent);

    double az = 68.0;
    int npoints = 50;
    for(int i=0; i< npoints; ++i) {
        double alt = 30.0 + 3.*(double)i/(double)(npoints-1);

        Direction az_alt = new Direction(az, alt);
        Direction ra_dec_real = toSky.transform(az_alt);
        Direction ra_dec_fake = to_sky_rot.transform(az_alt);

        System.out.println(alt+" "+
                           ra_dec_fake.angleBetween(ra_dec_real)
                                      .getArcsec());

    }

    Direction center = new Direction(-68.0, 30.0);


} // end of distortionMap method

/************************************************************************
*
************************************************************************/
public static void azAltTime() {

    Coordinates coord = initAzAlt();

    PreciseDate utc = UTCSystem.getInstance().createDate();

    for(int i=0; i<20; ++i) {

    //    System.gc();
        long start =System.currentTimeMillis();
        utc.setTime(start, 0);
        coord.toRADec(utc).invert();
        long end =System.currentTimeMillis();

        System.out.println("Az/Alt computation time "+
                           (end-start)+" millis");

    } // end of loop


} // end of

/**************************************************************************
*
**************************************************************************/
public static void testClockControler() {

    initTime();

    AdjustableClock clock = new AdjustableClock();

    ClockControler controler = new ClockControler(clock);

    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    frame.getContentPane().add(controler);
    frame.pack();
    frame.setVisible(true);


} // end of testClockControler method

/**************************************************************************
*
**************************************************************************/
// public static void testFOV() {
//
//     Clock clock = new FrozenClock(TAISystem.getInstance().createDate());
//     Chart chart = new Chart(clock);
//
//     /******
//     * FOV *
//     ******/
//     try {
//
//         /**********************************
//         * read the coordinate config file *
//         **********************************/
//         CoordConfig config = new CoordConfig();
//
//         /******************************
//         * coord config file
//         ******************************/
//         File file = new File("gpc_current.coord");
//
//         /*******
//         * fits *
//         *******/
//         FitsFile fits = new RandomAccessFitsFile(new RandomAccessFile(file, "r"));
//
//         config.read(fits);
//
//         FOV fov = new FOV("dummy", new Direction(0,0), new Angle(0),
//                           config, Coordinates.RA_DEC);
//
//
//         chart.addItem(fov);
//
//     } catch(IOException e) {
//         e.printStackTrace();
//         return;
//     }
//
//     /********************
//     * put it in a frame *
//     ********************/
//     JFrame frame = new JFrame();
//     frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
//     frame.getContentPane().add(chart);
//     frame.pack();
//     frame.setVisible(true);
//
//     chart.start();
//
// } // end of testFOV method

/**************************************************************************
*
**************************************************************************/
public static void testChart() {

    initTime();

    Observatory obs       = initObservatory();
    Weather weather       = initWeather();
    Refraction refraction = initRefraction(weather, obs);
    Ephemeris ephemeris   = initEphemeris();

    AzAlt az_alt = new AzAlt(obs, refraction, ephemeris);

    AdjustableClock clock = new AdjustableClock();





    Chart chart = new Chart(clock, az_alt,
                            Aspect.POLAR,
                            Projection.POLAR,
                            1000.0/6.5, new Point2D.Double(0.0, 0.0),
                            new Dimension(500,500));
    /*******
    * grid *
    *******/
    eap.sky.chart.Grid grid = new eap.sky.chart.Grid(az_alt);
    chart.addItem(grid);

    /**********
    * horizon *
    **********/
    HorizonItem horizon = new HorizonItem(az_alt, 0.0);
    chart.addItem(horizon);

    /********
    * stars *
    ********/
    try {
        File stars_dir = new File("stars");
        File stars_file = new File(stars_dir, "hipparcos.zip");
        Archive archive = new ZipArchive(stars_file);
        StarCatalog cat = new StarCatalog(archive);
        StarField stars = new StarField(cat, StarField.SINGLE_SELECTION);
        chart.addItem(stars);
    } catch(IOException e) {e.printStackTrace(); }


    /**********
    * the sun *
    **********/
    Planet sun = new Planet(ephemeris, obs, ephemeris.SUN,
                            Chart.makeIcon("sun.gif") );
    chart.addItem(sun);

    /***********
    * the moon *
    ***********/
    Planet moon = new Planet(ephemeris, obs, ephemeris.MOON,
                             Chart.makeIcon("moon.gif") );
    chart.addItem(moon);

    /*******************
    * clock controller *
    *******************/
    ClockControler controller = new ClockControler(clock);

    /*********
    * layout *
    *********/
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    panel.add(chart);
    panel.add(controller, BorderLayout.NORTH);

    /********************
    * put it in a frame *
    ********************/
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    frame.getContentPane().add(panel);
    frame.pack();
    frame.setVisible(true);

    chart.start();

} // end of testChart method

/**************************************************************************
*
**************************************************************************/
public static void testStarIO() {

    try {

        /********
        * write *
        ********/
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);

        Johnson.V.write(out);
        TychoBand.V.write(out);

        out.close();

        /*******
        * read *
        *******/
        DataInputStream in = new DataInputStream(
                            new ByteArrayInputStream(buffer.toByteArray()));

        System.out.println(Band.read(in));
        System.out.println(Band.read(in));

    } catch(IOException e) {
        e.printStackTrace();
    }

} // end of testStarIO method



/**********************************************************************
*
**********************************************************************/
// public static boolean testHICCatalog() {
//
//     try {
//
//         /*******************
//         * read the catalog *
//         *******************/
//         System.out.println("reading FITS");
//         FitsFile fits = new RandomAccessFitsFile(
//         new RandomAccessFile(
//                 new File("/otis/current/data/stars/hic/hic2.fit"), "r"));
//
//         Catalog cat = new HIC2Catalog(fits);
//         cat.read();
//
//         /*********************
//         * arrange into cells *
//         *********************/
//         System.out.println("filling cells");
//         Cell root = new HTMRoot();
//
//         cat.fillCells(root, Johnson.V);
//
//
//         /******************
//         * write the cells *
//         ******************/
//         System.out.println("writing");
//         File dir = new File("/otis/current/data/stars/hic/htm");
//         root.writeAll(dir);
//
//
//
// //         /***************************
// //         * now read from a zip file *
// //         ***************************/
// //         System.out.println("reading from zip");
// //         ZipFile zip = new ZipFile("/otis/current/data/stars/hic/hic.zip");
// //
// //         Cell root2 = new HTMRoot();
// //
// //         root2.readAll(zip);
// //
// //         /****************************
// //         * now read from a directory *
// //         ****************************/
// //         System.out.println("reading from directory");
// //         Cell root3 = new HTMRoot();
// //         root2.readAll(dir);
//
//
//     } catch(IOException e) {
//         e.printStackTrace();
//         return false;
//     }
//
//     return true;
//
// } // end of testCatalog method

/**********************************************************************
*
**********************************************************************/
// public static void testTychoCatalog() {
//
//     File dir = new File("/otis/develop/data/stars/tycho2");
//
//     TychoCatalog tycho;
//     try {
//
//         /*******************
//         * read the catalog *
//         *******************/
//         BufferedReader reader = new BufferedReader(
//                                 new FileReader(
//                                 new File(dir, "catalog.dat")));
//
//         System.out.println("reading");
//         tycho = new TychoCatalog(reader);
//         tycho.read();
//
//
//
//         /*********************
//         * arrange into cells *
//         *********************/
//         System.out.println("filling cells");
//         Cell root = new HTMRoot();
//
//         tycho.fillCells(root, TychoBand.V);
//
//         /******************
//         * write the cells *
//         ******************/
//         System.out.println("writing");
//         root.writeAll(new File(dir, "htm"));
//
//
//     } catch(IOException e) {
//         e.printStackTrace();
//         return;
//     }
//
// } // end of testTychoCatalogMethod

/**************************************************************************
*
**************************************************************************/
// public static boolean testCoordConfig() {
//
//     try {
//
//         CoordConfig config = new CoordConfig();
//
//         FitsFile fits = new RandomAccessFitsFile(
//            new RandomAccessFile("/otis/develop/src/config/z.fits", "r"));
//
//         config.read(fits);
//
//
//         /****************
//         * create a plot *
//         ****************/
//         PlaneCoordinates coord = config.getCoordinates("FOCAL");
//         PlaneSegment seg = coord.getSegment("FOCAL");
//         CoordPlot plot = new CoordPlot(seg);
//
//         PlotPanel panel = new PlotPanel();
//         panel.addPlot(plot);
//
//         /********************
//         * put it in a frame *
//         ********************/
//         JFrame frame = new JFrame();
//         frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
//         frame.getContentPane().add(panel);
//         frame.pack();
//         frame.setVisible(true);
//
//     } catch(IOException e) {
//         e.printStackTrace();
//         return false;
//     }
//
//     return true;
//
// } // end of testCoordConfig method

/**************************************************************************
*
**************************************************************************/
public static boolean testDMSFormat() {

    try {

        SexigesimalFormat format = SexigesimalFormat.DMS;
        System.out.println(format.parse("0d 00' 01\""));
        System.out.println(format.parse("0d   00'   01\""));
        System.out.println(format.parse("0d00'01\""));
        System.out.println(format.parse("0d00'00\"."));
        System.out.println(format.parse("0d00'00\".111111111111"));
        System.out.println(format.parse("-0d00'00\""));
        System.out.println(format.parse("-0d00'01\""));


    } catch(ParseException e) {
        e.printStackTrace();
        return false;
    }

    NumberFormat format = SexigesimalFormat.HMS;
    System.out.println(format.format(0.0));



    return true;

} // end of testDMSFormat method

/**************************************************************************
*
**************************************************************************/
public static void testTransformAngle() {

    Direction dir = new Direction(10,89);
    Angle angle = new Angle(16);

    Rotation rot1 = new Rotation(angle, dir);

    Direction axis = dir.perpendicular(Direction.Z_AXIS);
    Rotation rot2 = new Rotation(new Angle(-1.0), axis);

    Rotation rot = (Rotation)rot2.combineWith(rot1);
    Direction offset = rot.transform(dir);

    System.out.println("offset="+offset);

    /******************
    * now the reverse *
    ******************/
    axis = dir.perpendicular(Direction.Z_AXIS);
  //  rot = new Rotation(dir.angleBetween(Direction.Z_AXIS).negative(), axis);

    rot = (Rotation)new Rotation(new Euler(dir, 0.0));

    offset = rot.transform(offset);

    System.out.println("should be Z: "+rot.transform(dir));
    System.out.println(offset);

    System.out.println("angle="+(90.0-offset.getLongitude()));

} // end of testTransformAngle method


/**************************************************************************
*
**************************************************************************/
// public static void testPolynomialTransform() {
//
//     double[][] poly_x = {{1.0, 2.0}, {3.0, 0.0}};
//     double[][] poly_y = {{4.0, 5.0}, {6.0, 0.0}};
//
//     PolynomialTransform poly = new PolynomialTransform(poly_x, poly_y);
//
//     PolynomialTransform inv  = (PolynomialTransform)poly.invert();
//
//     Point2D point = new Point2D.Double(0.0, 0.0);
//     Point2D transformed = poly.transform(point, null);
//     Point2D back = inv.transform(transformed, null);
//
//     System.out.println(point);
//     System.out.println(back);
//
// } // end of testPolynomialTransform method


/**************************************************************************
* This is to generate test numbers for comparing with pslib.
**************************************************************************/
public static void testRADecToAzAlt() {

    initTime();

    Observatory obs = initObservatory();
    Direction lonlat = obs.getZenith();
    NumberFormat lonlat_format = new DecimalFormat("###.####");
    System.out.println("Geodetic Longitude: "+
                       lonlat_format.format(lonlat.getLongitude()));
    System.out.println("Geodetic Latitude: "+
                       lonlat_format.format(lonlat.getLatitude()));
    System.out.println("Height above WGS84 Ellipsoid: "+obs.getHeight()+" m");
    System.out.println();

    Weather weather = initWeather();
    System.out.println("Atmospheric Pressure: "+weather.getPressure()+" Pa");
    System.out.println("Temperature: "+weather.getCelsiusTemperature()+" C");
    System.out.println("RelativeHumidity: "+
                       100.0*weather.getRelativeHumidity()+" %");

    SaastamoinenRefraction refraction =
                      (SaastamoinenRefraction)initRefraction(weather, obs);
    System.out.println("Wavelength: "+refraction.getWavelength()+" nm");
    System.out.println("Molar Fraction of CO2: "+
                       refraction.getCO2Monitor().reportCO2Fraction(null));

    Tropopause tropopause = refraction.getTropopauseMonitor()
                                      .reportTropopauseConditions(null);
    System.out.println("Tropopause height: "+tropopause.getHeight());
    System.out.println("Tropopause Temperature: "+
                                  tropopause.getKelvinTemperature()+" K");
    System.out.println("Lapse Rate: "+tropopause.getLapseRate()+" K/m");
    Ephemeris ephemeris = initEphemeris();

    /**************
    * pick a time *
    **************/
    System.out.println();
    System.out.println("Time: 2003-04-01T01:30:00 UTC");
    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("GMT"));

    cal.set(cal.YEAR, 2003);
    cal.set(cal.MONTH, cal.APRIL);
    cal.set(cal.DAY_OF_MONTH, 1);

    cal.set(cal.HOUR_OF_DAY, 1);
    cal.set(cal.MINUTE, 30);
    cal.set(cal.SECOND, 0);
    cal.set(cal.MILLISECOND, 0);

    UTCSystem UTC = UTCSystem.getInstance();
    UTCDate utc = (UTCDate)UTC.createDate();
    utc.setTime(cal.getTime().getTime(), 0, false);

    System.out.println("    UTC seconds since 1970-01-01: "+
                       utc.getMilliseconds()/1000);
    System.out.println("    Is this a leapsecond? "+utc.isLeapSecond());

    /*******************************
    * Earth Orientation parameters *
    *******************************/
    UT1System UT1 = UT1System.getInstance();
    EOP eop = (EOP) UT1.createDate();
    eop.setTime(utc);

    System.out.println("    UT1 seconds since 1970-01-01: "+
                       (eop.getMilliseconds()/1000.0 +
                        eop.getNanoseconds()/1.0e9)          );
    System.out.println("    using IERS Bulletin B");

    /******************
    * barycenter time *
    ******************/
    TDBSystem TDB = TDBSystem.getInstance();
    PreciseDate tdb = TDB.createDate();
    tdb.setTime(utc);

    /*******************
    * pick a direction *
    *******************/
    Direction dir = new Direction(45.0, 30.0);
    System.out.println();
    System.out.println("Azimuth: "+dir.getLongitude());
    System.out.println("Altitude: "+dir.getLatitude());


    /***********************
    * transform components *
    ***********************/
    System.out.println();
    System.out.println("Az/Alt:");
    System.out.println("    x="+dir.getX());
    System.out.println("    y="+dir.getY());
    System.out.println("    z="+dir.getZ());
    System.out.println();

    dir = obs.azAltToTerrestrial().transform(dir);
    System.out.println("Terrestrial cordinates:");
    System.out.println("    x="+dir.getX());
    System.out.println("    y="+dir.getY());
    System.out.println("    z="+dir.getZ());
    System.out.println();

     dir = refraction.refractionTransform(utc).transform(dir);
     System.out.println("Above atmosphere:");
    System.out.println("    x="+dir.getX());
    System.out.println("    y="+dir.getY());
    System.out.println("    z="+dir.getZ());
    System.out.println();

    dir = eop.polarMotion().transform(dir);
    System.out.println("After polar motion:");
    System.out.println("    x="+dir.getX());
    System.out.println("    y="+dir.getY());
    System.out.println("    z="+dir.getZ());
    System.out.println();

    dir = eop.earthRotation().transform(dir);
    System.out.println("After Earth Rotation:");
    System.out.println("    x="+dir.getX());
    System.out.println("    y="+dir.getY());
    System.out.println("    z="+dir.getZ());
    System.out.println();

    dir = eop.precession().transform(dir);
    System.out.println("After Precession and Nutation:");
    System.out.println("    x="+dir.getX());
    System.out.println("    y="+dir.getY());
    System.out.println("    z="+dir.getZ());
    System.out.println();

    dir = ephemeris.aberration(tdb, eop, obs).transform(dir) ;
    System.out.println("After Aberration:");
    System.out.println("    x="+dir.getX());
    System.out.println("    y="+dir.getY());
    System.out.println("    z="+dir.getZ());
    System.out.println();

    dir = ephemeris.deflection(tdb, eop, obs).transform(dir) ;
    System.out.println("After gravity deflection:");
    System.out.println("    x="+dir.getX());
    System.out.println("    y="+dir.getY());
    System.out.println("    z="+dir.getZ());
    System.out.println();

    System.out.println("R.A. = "+dir.getLongitude());
    System.out.println("Dec. = "+dir.getLatitude());



//     Transform to_sky = obs.azAltToTerrestrial()
//                       .combineWith(refraction.refractionTransform(time))
//                       .combineWith(eop.polarMotion())
//                       .combineWith(eop.earthRotation())
//                       .combineWith(eop.precession())
//                       .combineWith(ephemeris.aberration(tdb, eop, obs))
//                       .combineWith(ephemeris.deflection(tdb, obs) );

    /******************
    * ephmeris values *
    ******************/
    System.out.println("\nEphemeris values:");
    ThreeVector sun = ephemeris.position(Ephemeris.SUN, tdb, eop, obs);
    System.out.println("Topocentric position of the Sun in meters:");
    System.out.println("    x="+sun.getX());
    System.out.println("    y="+sun.getY());
    System.out.println("    z="+sun.getZ());

    ThreeVector earth = ephemeris.barycentricVelocity(ephemeris.EARTH, tdb);
    System.out.println("Barycentric velocity of the Earth in m/s:");
    System.out.println("    x="+earth.getX());
    System.out.println("    y="+earth.getY());
    System.out.println("    z="+earth.getZ());

    ThreeVector diurnal = obs.diurnalVelocity(eop);
    System.out.println("Diurnal velocity of the observer in m/s:");
    System.out.println("    x="+diurnal.getX());
    System.out.println("    y="+diurnal.getY());
    System.out.println("    z="+diurnal.getZ());


    /*************
    * precession *
    *************/
    System.out.println("\nModeled Precession values: (radians)");
    PrecessionModel model = UT1System.getInstance().getPrecessionModel();
    TidalArguments args = eop.getTidalArguments();
    double x = model.calculateX(args);
    double y = model.calculateY(args);
    double s = model.calculateS(args, x, y);
    System.out.println("    X="+x);
    System.out.println("    Y="+y);
    System.out.println("    S="+s);

    PrecessionCorrection correction = eop.getPrecessionCorrection();

    System.out.println("Precession Corrections: (milliarcseconds)");
    System.out.println("    dX = "+correction.getXCorrection());
    System.out.println("    dY = "+correction.getYCorrection());

    System.out.println("Precession rotation quaternion components:");
    Rotation rot = eop.precession();
    double[] q = rot.getQuaternionComponents();
    System.out.println("    "+q[0]);
    System.out.println("    "+q[1]);
    System.out.println("    "+q[2]);
    System.out.println("    "+q[3]);

    /*****************
    * Earth rotation *
    *****************/
    System.out.println("\nEarth Rotation Angle: "+eop.earthRotationAngle()+
                       " degrees");

    System.out.println("Earth rotation quaternion components:");
    rot = eop.earthRotation();
    q = rot.getQuaternionComponents();
    System.out.println("    "+q[0]);
    System.out.println("    "+q[1]);
    System.out.println("    "+q[2]);
    System.out.println("    "+q[3]);

    /***************
    * Polar motion *
    ***************/
    PolarMotionParameters pm = eop.getPolarMotionParameters();
    System.out.println("\nPolar motion parameters (arc seconds):");
    System.out.println("    xp="+pm.getX());
    System.out.println("    yp="+pm.getY());
    System.out.println("    s'="+
                          (-47e-6 * args.getJulianCenturiesTDB()) );

    System.out.println("Julian Centuries since J2000 = "+
                       args.getJulianCenturiesTDB());

    System.out.println("\nPolar Motion quaternion components:");
    rot = eop.polarMotion();
    q = rot.getQuaternionComponents();
    System.out.println("    "+q[0]);
    System.out.println("    "+q[1]);
    System.out.println("    "+q[2]);
    System.out.println("    "+q[3]);

    System.out.println("\nNote the Earth rotation and polar motion values");
    System.out.println("given above have been corrected for diurnal and");
    System.out.println("semi-diurnal tidal effects");



} // end of testRADecToAzAlt method

/**************************************************************************
*
**************************************************************************/
public static void testCachedDate() {

    initTime();

    /********************
    * create a UTC date *
    ********************/
    UTCSystem UTC = UTCSystem.getInstance();
    PreciseDate utc = UTC.createDate();
    utc.setTime(new Date());

    PreciseDate cached = new CachedDate(utc);

    TimeSystem TDB = TDBSystem.getInstance();
    TimeSystem UT1 = UT1System.getInstance();
    TimeSystem TAI = TAISystem.getInstance();
    TimeSystem TT  =  TTSystem.getInstance();

    long start;
    long ellapsed;

    PreciseDate date = TDB.createDate();

    /*****************
    * first uncached *
    *****************/
    start = System.currentTimeMillis();
    for(int i=0; i< 1000; ++i) date.setTime(utc);
    ellapsed = System.currentTimeMillis() - start;
    System.out.println(date+" "+ellapsed+" uncached");


    /*********
    * cached *
    *********/
    start = System.currentTimeMillis();
    for(int i=0; i< 1000; ++i) date.setTime(cached);
    ellapsed = System.currentTimeMillis() - start;
    System.out.println(date+" "+ellapsed+" cached");

   System.out.println(TT.convertDate(cached));
   System.out.println(TT.convertDate(utc));


} // end of testCachedDate method

/**************************************************************************
*
**************************************************************************/
public static void testMapping() {

    AffineTransform trans = AffineTransform.getTranslateInstance(5, 10);
    Mapping map = new AffineMapping(trans);

    Shape shape = new Rectangle2D.Double(0,0, 100, 200);

    double accuracy = 1;
    Shape shape2 = map.map(shape, accuracy);

    double[] coord = new double[6];
    for(PathIterator it = shape2.getPathIterator(null); !it.isDone();
        it.next()) {
        int type = it.currentSegment(coord);

        System.out.println(type+" "+coord[0]+" "+coord[1]);

    } // end of loop over segments


} // end of testMapping method

/**************************************************************************
*
**************************************************************************/
public static void testCachedPrecession() {

    /***************************
    * get the precession model *
    ***************************/
    PrecessionModel p = IAU2000APrecession.getInstance();

    /**************************************************
    * pick a date at which to evaluate the precession *
    **************************************************/
    JulianDate jd = new JulianDate(TDBSystem.getInstance());
    double date = 2453411.5;

    NumberFormat format = new DecimalFormat("#.00");

    double[] x = new double[3];
    double[] y = new double[3];
    double[] s = new double[3];

    for(int i=0; i< 3; ++i, date += .1 ) {

        jd.set(date);
        PreciseDate tdb = jd.toDate();

        long start = System.currentTimeMillis();
        TidalArguments args = new TidalArguments(tdb, null);

        x[i] = p.calculateX(args);
        y[i] = p.calculateY(args);
        s[i] = p.calculateS(args,x[i],y[i]);
        long end = System.currentTimeMillis();

        System.out.println(format.format(date-2453400)+" "+
                           args.getJulianCenturiesTDB()+" "+
                           Math.toDegrees(x[i])*3600.0+" "+
                           Math.toDegrees(y[i])*3600.0+" "+
                           Math.toDegrees(s[i])*3600.0+" "+(end-start));

    } // end of loop over time

    double xmid = (x[0]+x[2])*0.5;
    double ymid = (y[0]+y[2])*0.5;
    double smid = (s[0]+s[2])*0.5;

    System.out.println("x error = "+Math.toDegrees((xmid-x[1]))*3600.0);
    System.out.println("y error = "+Math.toDegrees((ymid-y[1]))*3600.0);
    System.out.println("s error = "+Math.toDegrees((smid-s[1]))*3600.0);
} // end of testCachedPrecession method

/******************************************************************
*
******************************************************************/
public static void testCachedEphemeris() {

    /*************
    * initialize *
    *************/
    initTime();
    Ephemeris ephemeris = initEphemeris();
    Observatory obs = initObservatory();

    /**************************
    * get a contemporary date *
    **************************/
    PreciseDate utc = UTCSystem.getInstance().createDate();
    utc.setTime(1114460383192l, 0);

    PreciseDate tdb = TDBSystem.getInstance().createDate();
    EOP eop = (EOP)UT1System.getInstance().convertDate(tdb);

    double interval = 100;
    for(int j=0; j< 1; ++j, interval *= 2) {

        tdb.setTime(utc);
        /**********************
        * position of the sun *
        **********************/
        ThreeVector[] pos = new ThreeVector[50];
        for(int i=0; i< pos.length; ++i) {
            pos[i] = ephemeris.position(Ephemeris.SUN, tdb, eop, obs);
          // pos[i] = ephemeris.barycentricVelocity(Ephemeris.EARTH, tdb);

          //  System.out.println(pos[i]);

          ThreeVector delta = pos[i].minus(pos[0]);
          System.out.println(delta.getX()/1e7+
                             " "+delta.getY()/1e7+
                             " "+delta.getZ()/1e7);

            tdb.increment(interval);

        } // end of loop over iterations


//         ThreeVector mid = pos[0].plus(pos[2]).times(0.5);
//
//       //  System.out.println(mid);
//         ThreeVector error = mid.minus(pos[1]);
//
//         Angle angle = mid.getDirection().
//                          angleBetween(pos[1].getDirection());
//
//
//         System.out.println(interval+" "+error.getLength()
//                                    +" "+angle.getArcsec());
//           System.out.println(error);

//         double ab = error.getLength()/Ephemeris.SPEED_OF_LIGHT;
//         ab = Math.toDegrees(ab)*3600.0;
//
//         System.out.println(interval+" "+error.getLength()+" "+ab);

    } // end of loop over intervals




} // end of testEphemeris method

/******************************************************************
*
******************************************************************/
public static void testEphemerisTiming() {

    /*************
    * initialize *
    *************/
    initTime();
    Ephemeris ephemeris = initEphemeris();
    Observatory obs = initObservatory();

    UT1System UT1 = UT1System.getInstance();

    /**************************
    * get a contemporary date *
    **************************/
    PreciseDate utc = UTCSystem.getInstance().createDate();
    utc.setTime(1114460383192l, 0);

  //  TransformCache cache = TransformCache.makeCache(utc);

    PreciseDate tdb = TDBSystem.getInstance().createDate();
    tdb.setTime(utc);

    EOP eop = (EOP) UT1.createDate();
    eop.setTime(utc);

    long sum=0;
    for(int i=0; i< 100; ++i) {

        long start = System.currentTimeMillis();
       // ephemeris.aberration(tdb, eop, obs);
       // ephemeris.barycentricVelocity(ephemeris.EARTH, tdb);
        //obs.diurnalVelocity(eop);
       // eop.terrestrialToCelestial();
      //  eop.precession();

       // ephemeris.deflection(tdb, obs);
        ephemeris.position(ephemeris.SUN, tdb, eop, obs);

        long end   = System.currentTimeMillis();

        long time = end-start;
        if(i>0) sum += time;

        System.out.println(i+" "+time+" "+(double)sum/(double)(i+1));

    }

} // end of testEphemerisTiming method

/**************************************************************************
*
**************************************************************************/
// public static void testInterpolation() {
//
//     NumberFormat format = new DecimalFormat("#.#####");
//
//     Function func = new TestFunction();
//
//     CachedFunction cache = new CachedFunction(func, 1.0, 10);
//
//     for(int i=0; i< 100; ++i) {
//         double x = i*0.01;
//
//       //  double real = func.evaluateFunction(x);
//
//         long start = System.currentTimeMillis();
//         double approx  = cache.evaluateFunction(x);
//         long end = System.currentTimeMillis();
//
//      //   System.out.println(x+" "+real+" "+approx+" "+(approx-real));
//
//          System.out.println(format.format(x)+" "+(end-start));
//
//     } // end of loop over points
//
//
// } // end of testInterpolation method

/**************************************************************************
*
**************************************************************************/
public static void testDirection() {


    Euler euler = new Euler(Direction.Z_AXIS,  0.0);

    Plane plane1 = new Plane(Coordinates.RA_DEC, Projection.TANGENT,
                             Mapping.IDENTITY);

    Direction aspect = new Direction(20, 89);

    CurrentPointing pointing = new CurrentPointing();
    pointing.setPointing(aspect, 0.0);
    PointingCoordinates coord = new PointingCoordinates(Coordinates.RA_DEC,
                                                        pointing);

    Plane plane2 = new Plane(coord, Projection.TANGENT, Mapping.IDENTITY);


    Mapping mapping = plane2.getMappingTo(plane1, null);


    Point2D origin = new Point2D.Double(0,0);
    Point2D x = new Point2D.Double(1,0);
    Point2D y = new Point2D.Double(0,1);

    Point2D origin1 = mapping.map(origin);
    Point2D x1 = mapping.map(x);
    Point2D y1 = mapping.map(y);

    System.out.println("origin1="+origin1);
    System.out.println("     x1="+x1);
    System.out.println("     y1="+y1);

    Direction dir = new Direction(30, 80);

    Projection proj = Projection.AITOFF;
    System.out.println(proj.unproject(proj.project(dir)));


} // end of testDirection method

/**************************************************************************
*
**************************************************************************/
public static void testAitoff() {

    Rotation inverse =new Rotation(new Euler(new Direction(0.0,0.0), 90.0));
    Rotation rotation = (Rotation)inverse.invert();




    Direction orig = new Direction(0,0);

    Direction dir = rotation.transform(orig);

    dir = new Direction(dir.getLongitude()/2.0, dir.getLatitude());

    dir = inverse.transform(dir);

    Point2D point = Projection.POLAR.project(dir);

    System.out.println("x="+2.0*point.getX()+" y="+point.getY());

    point = Projection.AITOFF.project(orig);
    System.out.println("x="+point.getX()+" y="+point.getY());

} // end fo TestAitoff method

/**************************************************************************
*
**************************************************************************/
// public static void testSunset() {
//
//     initTime();
//     Ephemeris ephemeris = initEphemeris();
//
//
//     AzAlt az_alt = initAzAlt();
//
//     Clock clock = new SystemClock();
//     PreciseDate now = clock.currentTime();
//
//     RiseAndSetTable table = new RiseAndSetTable(ephemeris, Ephemeris.SUN,
//                                                 az_alt);
//
//     System.out.println(table.altitude(now));
//
//
//     PreciseDate t1 = now.copy();
//     t1.increment(3600*24*7);
//
//     long start = System.currentTimeMillis();
//     java.util.List list = table.findCrossings(now, t1, 1.0);
//     long end = System.currentTimeMillis();
//
//     for(Iterator it = list.iterator(); it.hasNext(); ) {
//         eap.sky.util.coordinates.Crossing cross = (eap.sky.util.coordinates.Crossing)it.next();
//
//         System.out.println(cross);
//     }
//
//     System.out.println(end-start);
//
//
// } // end of testSunset method

/**************************************************************************
*
**************************************************************************/
public static void testHammerProjection() {

    Projection hammer = Projection.HAMMER;
    Projection aitoff = Projection.AITOFF;

    Direction dir = new Direction(10, -70);

    System.out.println(hammer.project(dir));
    System.out.println(aitoff.project(dir));

    long start = System.currentTimeMillis();
    for(int i=0; i< 100000; ++i) {
        aitoff.project(dir);
    }
    long end = System.currentTimeMillis();

    System.out.println(end-start);




} // end of testHammerprojection method

/**************************************************************************
*
**************************************************************************/
// public static void testLunation() throws Exception {
//
//     initTime();
//     Ephemeris ephemeris = initEphemeris();
//     Observatory obs = initObservatory();
//
//     AzAlt az_alt = initAzAlt();
//
//    // PhaseCalculator calc = new PhaseCalculator(ephemeris, obs);
//
//     RiseSetCalculator calc = new RiseSetCalculator(az_alt, 1);
//     Night.setDefaultRiseSetCalculator(calc);
//
//
//     Night night = new Night("2007-03-13");
//
//     Lunation lunation = new Lunation(night);
//
//     System.out.println(lunation);
//     System.out.println(lunation.nextLunation());
//     System.out.println(lunation.lastLunation());
//
//     for(int i=0; i< 12; ++i) {
//         System.out.println(lunation.getLength());
//         lunation = lunation.nextLunation();
//     }
//
//     Clock clock = new SystemClock();
//     PreciseDate now = clock.currentTime();
//
//     NightCache nights = new NightCache();
//     for(int i = 0; i<2; ++i) {
//
//         long start = System.currentTimeMillis();
//         night = new Night(now);
//         int mjd0 =night.getMJDAtStart();
//         for(int mjd=mjd0; mjd < mjd0+10; ++mjd) {
//             night = nights.getNight(mjd);
//             night.getSunset();
//             night.getSunrise();
//
//         }
//         long end = System.currentTimeMillis();
//
//         System.out.println(i+" time="+(end-start));
//     }
//
// } // end of testLunation method

/**************************************************************************
*
**************************************************************************/
public static void testRiseSet() {



    initTime();

    AzAlt az_alt = initAzAlt();
    Ephemeris ephemeris = az_alt.getEphemeris();
    Observatory obs = az_alt.getObservatory();

    PreciseDate time = new SystemClock().currentTime();
    double alt = 0.0;

    TDBSystem TDB = TDBSystem.getInstance();
    UT1System UT1 = UT1System.getInstance();


    for(int iteration=0; iteration < 100; ++iteration) {

        /*****************
        * cache the time *
        *****************/
        TransformCache cache = TransformCache.makeCache(time);

        /***************************************
        * find the current position of the sun *
        ***************************************/
        Direction radec = ephemeris.position(Ephemeris.SUN,
                                        TDB.convertDate(cache),
                                        (EOP)UT1.convertDate(cache),
                                        obs).getDirection();


        Transform trans = Coordinates.RA_DEC.getTransformTo(az_alt, cache);

        Direction dir  = trans.transform(radec);
        Direction pole = trans.transform(Direction.Z_AXIS);

        // this should really be the current rotational axis of the earth.
        ThreeVector n1 = new ThreeVector(pole);
        double d1 = dir.angleBetween(pole).getCos();

        ThreeVector n2 = new ThreeVector(Direction.Z_AXIS);
        double d2 = Math.sin(Math.toRadians(alt));

        ThreeVector cross = n1.cross(n2);
        double dot = n1.dot(n2);

        double det = 1.0-dot*dot;

        double c1 = (d1 - d2*dot)/det;
        double c2 = (d2 - d1*dot)/det;

        double cross2 = cross.dot(cross);

        double u = Math.sqrt(1.0-(c1*c1 + 2.0*c1*c2*dot + c2*c2));

        ThreeVector set = n1.times(c1).plus(n2.times(c2)).plus(cross.times(u));
        if(set.getX()<0) {
            set =n1.times(c1).plus(n2.times(c2)).plus(cross.times(-u));
        }
//         System.out.println("n1="+n1);
//         System.out.println("n2="+n2);
//         System.out.println("cross="+cross);
//         System.out.println("dot="+dot);
//         System.out.println("c1="+c1);
//         System.out.println("c2="+c2);
//         System.out.println("u="+u);
//         System.out.println("thing = "+(c1*c1 + 2.0*c1*c2*dot + c2*c2));
//         System.out.println(set);
//         System.out.println(set.getDirection());

        ThreeVector v1 = new ThreeVector(dir).minus(n1.times(d1));
        ThreeVector v2 = set.minus(n1.times(d1));

        double angle = v1.getDirection().angleBetween(v2.getDirection()).getRadians();

        double delta_time = angle/Ellipsoid.WGS84.getAngularVelocity();

        if(dir.getLatitude() < alt) delta_time = -delta_time;

        PreciseDate set_time = time.copy();
        set_time.increment(delta_time);

         System.out.println(iteration+" "+
                            LocalTimeSystem.getInstance().convertDate(set_time)+
                            " "+delta_time+
                            " "+dir.getLatitude());

         if(time.equals(set_time)) {
            System.out.println("n1="+n1);
            System.out.println("n2="+n2);
            System.out.println("cross="+cross);
            System.out.println("dot="+dot);
            System.out.println("c1="+c1);
            System.out.println("c2="+c2);
            System.out.println("u="+u);
            System.out.println("thing = "+(c1*c1 + 2.0*c1*c2*dot + c2*c2));
            System.out.println(set);
            System.out.println(set.getDirection());
             break;
         }

         time = set_time;



    } // end of loop over iterations




} // end of testRiseSet method

/**************************************************************************
*
**************************************************************************/
public static void testRiseSet2() {

    initTime();

    AzAlt az_alt = initAzAlt();
    Ephemeris ephemeris = az_alt.getEphemeris();
    Observatory obs = az_alt.getObservatory();

    PreciseDate time = new SystemClock().currentTime();
    double alt = 0.0;

    TDBSystem TDB = TDBSystem.getInstance();
    UT1System UT1 = UT1System.getInstance();



    for(int iteration=0; iteration < 10; ++iteration) {

        /*****************
        * cache the time *
        *****************/
        TransformCache cache = TransformCache.makeCache(time);

        /***************************************
        * find the current position of the sun *
        ***************************************/
        Direction radec = ephemeris.position(Ephemeris.SUN,
                                        TDB.convertDate(cache),
                                        (EOP)UT1.convertDate(cache),
                                        obs).getDirection();


       PreciseDate set_time = az_alt.estimateCrossingTime(radec, alt, true, cache, false);



      if(time.equals(set_time)) break;
      time = set_time;

   } // end of loop over iterations

   /*****************************
   * compare to a binary search *
   *****************************/
//    System.out.println("binary");
//    RiseSetCalculator calc = new RiseSetCalculator(Ephemeris.SUN, az_alt, 1e-9);
//
//    time = new SystemClock().currentTime();
//    PreciseDate time2 = time.copy();
//    time2.increment(12*3600);
//
//    Crossing cross = calc.findCrossing(time, time2, alt);
//    System.out.println(LocalTimeSystem.getInstance().convertDate(cross.getTime()));

   /********
   * night *
   ********/
//    System.out.println("night");
   EphemerisRiseSet sun = new EphemerisRiseSet(az_alt, 1.0);
   Night.setDefaultRiseSet(sun);
//
//    for(int i=0; i< 24; ++i) {
//
//        Night night = new Night(time);
//     System.out.println(LocalTimeSystem.getInstance().convertDate(time));   System.out.println(LocalTimeSystem.getInstance().convertDate(night.getSunset()));
// System.out.println();
//        time.increment(3600);
//
//    } // end of loop over a day



   Night night = new Night(time);

System.out.println(LocalTimeSystem.getInstance().convertDate(night.getSunset()));
//System.out.println(LocalTimeSystem.getInstance().convertDate(night.getEveningTwilight()));
//System.out.println(LocalTimeSystem.getInstance().convertDate(night.getMorningTwilight()));
System.out.println(LocalTimeSystem.getInstance().convertDate(night.getSunrise()));
} // end of testRiseSet2 method

/**************************************************************************
*
**************************************************************************/
public static void testNight() throws Exception  {

    initNight();

    Night night = new Night("2007-05-17");



    System.out.println(night);

    System.out.println(new Night(new SystemClock().currentTime()));

} // end of testNight method

/**************************************************************************
*
**************************************************************************/
public static void UTCtoHST() throws Exception {

    initTime();

    UTCSystem UTC = UTCSystem.getInstance();
    LocalTimeSystem HST = LocalTimeSystem.getInstance();

    String utc = "2007-05-15 06:47:30 UTC";
    PreciseDateFormat utc_format = UTC.createFormat();

    PreciseDate time = (PreciseDate)utc_format.parseObject(utc);

    PreciseDateFormat hst_format = HST.createFormat();
    System.out.println(HST.convertDate(time));

} // end of UTCtoHST method

/**************************************************************************
*
**************************************************************************/
public static void testGalacticCoordinates() {



    Transform to_radec = Coordinates.GALACTIC.toRADec(null);


    Direction pole = Direction.Z_AXIS;
    Direction radec = to_radec.transform(pole);

    System.out.println("pole at "+
            SexigesimalFormat.HMSF.format(radec.getLongitude()+360.0)+" "+
            SexigesimalFormat.DMSF.format(radec.getLatitude())       );

System.out.println("pole ra = "+ radec.getLongitude());

    Direction center = new Direction(0,0);
    radec = to_radec.transform(center);

    System.out.println("center at "+
            SexigesimalFormat.HMSF.format(radec.getLongitude()+360.0)+" "+
            SexigesimalFormat.DMSF.format(radec.getLatitude())       );

System.out.println("want      17h 45m 37s.224    -28d 56' 10.23");

} // end of testGalacticCoordinates method

/***********************************************************************
*
***********************************************************************/
// public static void testDVOCatalog() throws Exception {
//
//     BandMap bands = new BandMap();
//     bands.add(PanSTARRSBand.g);
//     bands.add(PanSTARRSBand.r);
//     bands.add(PanSTARRSBand.i);
//     bands.add(PanSTARRSBand.z);
//     bands.add(PanSTARRSBand.y);
//
//     File dir = new File("/home/flaxen/pier/catdir.synth.bright");
//     File mags_dir = new File(dir, "mags");
//
//     MagnitudeHistogram hist = MagnitudeHistogram.read(
//                                           new File(mags_dir, "histogram"));
//
//
//
//
//      DVOSource dvo = new DVOSource(dir);
//
//     DiskSorter sorter = new DiskSorter(dvo, mags_dir, bands, hist, 1000000);
//
//     sorter.binStars();
//
//
// } // end of testDVOCatalog method

/**************************************************************************
*
**************************************************************************/
// public static void testNativeSource() throws Exception {
//
//     BandMap bands = new BandMap(PanSTARRSBand.class);
//
//     File dir = new File("/home/flaxen/pier/catdir.synth.bright/mags");
//     NativeSource source = new NativeSource(new File(dir, "mag-033"), bands);
//
//     Star star;
//     while((star = source.nextStar())!=null) {
//         System.out.println(star);
//     }
//
//     bands.write(new File(dir, "bands"));
//
//
//     Band band = PanSTARRSBand.r;
//
//     DataOutputStream out = new DataOutputStream(
//                            new FileOutputStream(
//                            new File(dir, "band")));
//     band.write(out);
//     out.close();
//
//
//
// } // end of testNativeSource method

/**********************************************************************
*
**********************************************************************/
// public static void testDiskSortedSource() throws Exception {
//
//     File dir = new File("/home/flaxen/pier/catdir.synth.bright");
//     File mags_dir = new File(dir, "mags");
//     File cells_dir = new File(dir, "cells2");
//
//   //  DiskSortedSource source = new DiskSortedSource(mags_dir);
//     Band band = PanSTARRSBand.r;
//
//
//     BandMap bands = new BandMap();
//     bands.add(PanSTARRSBand.g);
//     bands.add(PanSTARRSBand.r);
//     bands.add(PanSTARRSBand.i);
//     bands.add(PanSTARRSBand.z);
//     bands.add(PanSTARRSBand.y);
//
//     NativeSource source2 = new NativeSource(new File(dir, "sorted"), bands, 1024*1024);
//
// //     Star star;
// //     while((star = source.nextStar())!=null) {
// //        // System.out.println(star.getMagnitude(band));
// //     }
//
//
//
// } // end of testDiskSortedSource method

/***********************************************************************
*
***********************************************************************/
// public static void testMagnitudeHistogram() throws Exception {
//
//
//     File dir = new File("/home/flaxen/pier/catdir.synth.bright");
//
//     DVOSource dvo = new DVOSource(dir);
//
//     MagnitudeHistogram hist = new MagnitudeHistogram(dvo,
//                                                      PanSTARRSBand.r);
//
//     hist.dump();
//     hist.write(new File(dir, "histogram"));
//
//
// } // end of testMagnitudeHistogram  method

/**********************************************************************
*
**********************************************************************/
public static void testPresort() throws IOException {

//     File dir = new File("/home/flaxen/pier/catdir.synth.bright");
//     File mags_dir = new File(dir, "mags");
//
//     DiskSortedSource source = new DiskSortedSource(mags_dir);
//
//     File file = new File(dir, "sorted2");
//     NativeSaver saver = new NativeSaver(source, file, source.getBandMap());
//     saver.save();


//     BandMap bands = bandMap.read(new File(mags_dir, "bands");
//
//     for(Iterator it = BinFile.list().iterator(); it.hasNext(); ) {
//         BinFile bin = (BinFile)it.next();
//
//         File out =
//
//         NativeSaver saver = new NativeSaver(new MemorySorter(
//                                    new NativeSource(bin.getFile(), bands)),
//                                   bands);
//
//
//
//     } // end of loop over files

} // end of testPresort method

/***********************************************************************
*
***********************************************************************/
// public static void testFilterStars() throws IOException {
//
//     File dir = new File("/home/flaxen/pier/catdir.synth.bright");
//
//     Band band = PanSTARRSBand.r;
//
//
//     BandMap bands = new BandMap();
//     bands.add(PanSTARRSBand.class);
//
//     NativeSource source = new NativeSource(new File(dir, "sorted"), bands,
//                                             1024*1024);
//
//     DataOutputStream out = new DataOutputStream(
//                            new BufferedOutputStream(
//                            new FileOutputStream(new File(dir, "filtered")),
//                            1024*1024));
//
//     Star star;
//     int count = 0;
//     while((star = source.nextStar()) != null) {
//
//         ++count;
//         if(count%100000 == 0) { System.out.println(count); }
//
//         Photometry phot = star.getPhotometry();
//         boolean good = true;
//         for(Iterator it = phot.getMagnitudes().iterator(); it.hasNext();) {
//             Magnitude mag = (Magnitude)it.next();
//             float value = mag.getValue();
//
//             if(value < -5.0) {
//                 good = false;
//                 break;
//             }
//
//         } // end of loop over magnitudes
//
//         if(!good) System.out.println("rejecting "+star);
//         else      star.write(out, bands);
//
//     } // end of loop over stars
//
//     out.close();
//
// } // end of testFilterStars method

/**********************************************************************
*
**********************************************************************/
// public static void testCatalogGenerator() throws Exception {
//
//     File dir = new File("/home/flaxen/pier/catdir.synth.bright");
//     File cells_dir = new File(dir, "cells");
//     File source_file = new File(dir, "filtered");
//
//     Band band = PanSTARRSBand.r;
//     BandMap bands = new BandMap();
//     bands.add(PanSTARRSBand.class);
//
//     NativeSource source = new NativeSource(source_file, bands,
//                                            1024*1024);
//
//     CatalogGenerator generator = new DepthFirstGenerator(source,
//                                                       cells_dir,
//                                                       1000,
//                                                       bands,
//                                                       band,
//                                                       "PS1 Synthetic",
//                                                       new HTMRoot());
//
//     generator.generate();
//
// } // end of testCatalogGenerator method

/**********************************************************************
*
**********************************************************************/
// public static void testStarCatalog(StarCatalog cat) throws Exception {
// //     File data_dir = new File("/home/pier/otis/sim/data");
// //     File cat_file = new File(data_dir, "/stars/tycho2/tycho2.zip");
// //    // Archive archive = new ZipArchive(cat_file)
// //    // String cat_name = "/stars/eos.zip";
// //
// //    Archive archive = new GZippedURLArchive(new URL("http://flaxen.ifa.hawaii.edu/otis/stars/cells2/"));
// //
// //    archive = new CachedArchive(archive, new File(data_dir, "stars/ps1"));
//
//  //   StarCatalog cat = new StarCatalog(archive);
//
// System.out.println(cat.getName());
// System.out.println(cat.getRootCell());
// System.out.println(cat.getSortBand());
//
//     initTime();
//
//     Clock clock = new SystemClock();
//     Chart chart = new Chart(clock, Coordinates.RA_DEC,
//                             Aspect.EQUATORIAL,
//                             Projection.HAMMER,
//                             500.0/6.5, new Point2D.Double(0.0, 0.0),
//                              new Dimension(500,500));
//
//     chart.addItem(new Grid(Coordinates.RA_DEC));
//
//     /********
//     * stars *
//     ********/
//     StarField stars = new StarField(cat, StarField.SINGLE_SELECTION);
//     chart.addItem(stars);
//
//
//     JFrame frame = new JFrame();
//     frame.getContentPane().add(chart);
//     frame.pack();
//     frame.setVisible(true);
//
//      chart.start();
//
// } // end of testStarCatalog method

/**********************************************************************
*
**********************************************************************/
public static void testInversePolynonialTransform() {

    double[][] poly_x = new double[3][3];

    poly_x[0][0] = 1.0;
    poly_x[0][1] = 2.0;
    poly_x[0][2] = 0.002;

    double[][] poly_y = new double[3][3];
    poly_y[0][0] = 2.0;
    poly_y[1][0] = 3.0;
    poly_y[2][0] = 0.001;

    PlaneTransform poly = new PolynomialTransform(poly_x, poly_y);
    PlaneTransform inverse = poly.invert();

    Point2D point = new Point2D.Double(100,150);
    Point2D result = new Point2D.Double(0,0);
    Point2D result2 = new Point2D.Double(0,0);

    poly.transform(point, result, null);

    System.out.println(result);

    inverse.transform(result, result2, null);

    System.out.println(result2);

} // end of testInversePolynonialTransform method


/**********************************************************************
*
**********************************************************************/
public static void testQuadrature() throws Exception {

    Function f = new TestFunction();
    double from = 0.0;
    double to = 1.0;
    double accuracy = 1e-13;
    DonenessTest test = new BasicDonenessTest(accuracy);

    DataArray answer = f.evaluateFunction(to).copy();
    answer.minus(f.evaluateFunction(from));

    Quadrature trap = new TrapezoidQuadrature(25);
    DataArray result = trap.integrate(f, from,to, test).copy();

    result.minus(answer);
    System.out.println("Trapezoid: "+result);

    Quadrature romb = new RombergQuadrature(1,1000, 100000);
    result = romb.integrate(f, from,to, test).copy();
    result.minus(answer);
    System.out.println("Romberg: "+result);

    //Quadrature adapt = new AdaptiveQuadrature(100000);
    //System.out.println("Adaptive: "+(adapt.integrate(f, from,to, accuracy)-answer));


} // end of testQuadrature method

/**********************************************************************
*
**********************************************************************/
public static void testCachedDateSerial() throws Exception {

    PreciseDateFormat format = TAISystem.getInstance().createFormat();
    PreciseDate tai = format.parsePreciseDate("1999-01-01 12:34:56.2 TAI");

    System.out.println(tai);

    CachedDate cache = new CachedDate(tai);
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(bytes);

    out.writeObject(cache);
    out.close();

    ObjectInputStream in = new ObjectInputStream(
                           new ByteArrayInputStream(bytes.toByteArray()));

    System.out.println(in.readObject());



} // end of testCachedDateSerial

/**********************************************************************
*
**********************************************************************/
public static void testGMST() throws Exception {

    initTime();

    TimeSystem UTC = UTCSystem.getInstance();

    JulianDate jd0 = new JulianDate(UTC, 2451545, 0.0);

    PreciseDate utc = UTC.createFormat().parsePreciseDate("2004-04-06 07:51:28.386 UTC");
    utc.increment(-0.439961);
    JulianDate jd = new JulianDate(utc);

    double d = jd.offsetFrom(jd0);
    double tu = d/36525.0;

    System.out.println("tu="+tu);

    //double offset = 67310.54841;
    double offset = 24110.54841; // these differ by half a day
    double tu2 = tu*tu;
    double tu3 = tu2*tu;
    double gmst = offset
               +8640184.812866*tu
                     +0.093104*tu2
                     -6.2e-6  *tu3;

    System.out.println("GMST="+gmst+" s");

    int cycles = (int)Math.floor(gmst/86400.0);
    System.out.println("cycles="+cycles);
    gmst -= cycles*86400.0;

    System.out.println("seconds="+gmst);

    System.out.println(gmst*360.0/86400.0);


} // end of testGMST method

/**********************************************************************
*
**********************************************************************/
public static void testGeodeticToGeocentric() {

    Ellipsoid wgs84 = Ellipsoid.WGS84;
    Direction lat_lon = new Direction(20, 30);
    double alt = 1000.0;

    ThreeVector pos = wgs84.position(lat_lon, alt);

    System.out.println("("+pos.getX()+", "+pos.getY()+", "+pos.getZ()+")");

} // end of testGeodeticToGeocentric method

/**********************************************************************
*
**********************************************************************/
public static void testStupid() {

    Direction dir1 = new Direction(184.56, 29.57);
    Direction dir2 = new Direction(342.05, 4.71);

    Angle angle = dir1.angleBetween(dir2);
    System.out.println(angle.getDegrees()+" deg");

} // end of testStupid method


/**********************************************************************
*
**********************************************************************/
public static void testMAMBASun() throws Exception {

    initTime();

    // Webster near St. Inigoes
    double longitude = -(76 + 25.0/60.0 +30.75/3600.0);
    double latitude = 38.0 +8.0/60. + 53.65/3600.0;
    double altitude = 5.0;
    Observatory obs = initObservatory(longitude, latitude, altitude);


    Weather weather = new Weather(102607.577, 8.9,
                                  new DewPoint(2.2, false));

    Refraction refraction = initRefraction(weather, obs);
    Ephemeris ephemeris = initEphemeris();

    AzAlt az_alt = new AzAlt(obs, refraction, ephemeris);


    //Direction dir = ephemeris.position(Ephemeris.SUN, tdb, ut1, obs);

    TimeSystem EST = new LocalTimeSystem(UTCSystem.getInstance(),
                                         TimeZone.getDefault());


    Clock clock = new SystemClock();
    PreciseDate time = EST.createFormat().parsePreciseDate("2016-03-08 08:00:00.0 EST");


    for(int i=0; i<60*48; ++i) {

        PreciseDate utc = UTCSystem.getInstance().convertDate(time);
        PreciseDate tdb = TDBSystem.getInstance().convertDate(utc);
        EOP ut1 =   (EOP)(UT1System.getInstance().convertDate(utc));

        Direction dir = ephemeris.position(Ephemeris.SUN, tdb, ut1, obs).getDirection();

        Direction dir2 = Coordinates.RA_DEC.getTransformTo(az_alt, utc).transform(dir);

        double az = dir2.getLongitude();
        double alt = dir2.getLatitude();
        System.out.println(time+" "+az+" "+alt);

        time.increment(60.0);

    }

} // end of testMAMBASun method

/**********************************************************************
*
**********************************************************************/
public static void main(String[] args) throws Exception {

    //testQuadrature();
   // testCachedDateSerial();

   //testGMST();



   // testGeodeticToGeocentric();

  // testStupid();


   // TestTLE.main(args);

    //testChart();

    testMAMBASun();


} // end of main method



} // end of Test class
