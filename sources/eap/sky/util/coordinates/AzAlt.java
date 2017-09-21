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

package eap.sky.util.coordinates;

import eap.sky.util.*;
import eap.sky.time.*;
import eap.sky.time.barycenter.*;
import eap.sky.ephemeris.*;
import eap.sky.earth.*;
import eap.sky.earth.atmosphere.*;
import eap.sky.earth.atmosphere.monitors.*;

/*************************************************************************
* Represents the Azimuth and Altitude coordinate system used to specify the
* position of an object with respect to the surface of the Earth.
* Azimuth is measured east of north, with North being defined by the
* <A href="http://www.iers.org/iers/products/itrs">
* International Terrestrial Reference System</a> (ITRS) north pole.
* Altitude is 90 degrees minus the distance from the zenith, where the
* zenith is defined by the local gravity vector.
* <p>
* AzAlt coordinates come in different flavors, depending on how you define
* the transform to {@link RADec}. The transform consists of the
* following parts:
* <ol>
* <li> General relativistic deflection of light rays by the gravity of the Sun.
* <li> Aberration due to the velocity of the observer. We include both the
*      annual component due to the orbit of the Earth, and the diurnal
*      component dure to its rotation. We apply the full special relativistic
*      correction for both.
* <li> Precession/Nutation, which is roughly equivalent to the motion of the
*      Earth's rotational axis with respect to the celestial sphere.
* <li> The rotation of the Earth.
* <li> Polar motion, which is roughly equivalent to the motion of the Earth's
*      rotational axis with respect to the Earth's crust.
* <li> Atmospheric refraction.
* <li> A rotation to account for the position of the observer on the Earth.
* </ol>
* <p>
* A {@link Direction} in these coordinates has longitude corresponding
* to Azimuth, and latitude corresponding to Altitude.
*************************************************************************/
public class AzAlt extends Coordinates {

private static final Aspect POLAR = Aspect.createPolarAspect(-90, 1,1);
private static final Aspect EQUATORIAL = Aspect.createEquatorialAspect(90, 1,1);

Observatory obs;
Refraction refraction;
Ephemeris ephemeris;
UT1System UT1;
TDBSystem TDB;


/*************************************************************************
* Create a new AzAlt coordinate system.
* @param obs The observatory parameters, such as longitude and latitude.
* @param refraction The method for calculating the atmospheric refraction.
* @param ephemeris The ephemeris used to calculate aberration and
* gravitational bending of light rays.
* @param UT1 The UT1 time system to use to specify the motion of the Earth.
* Note this gives precession, nutation, and polar motion as well as the
* rotation of the Earth.
* @param TDB The TDB time system used to calculate the positions of
* celestial bodies.
*************************************************************************/
public AzAlt(Observatory obs, Refraction refraction, Ephemeris ephemeris,
             UT1System UT1, TDBSystem TDB) {

    this.obs = obs;
    this.refraction = refraction;
    this.ephemeris = ephemeris;
    this.UT1 = UT1;
    this.TDB = TDB;

} // end of constructor



/*************************************************************************
* Create a new AltAz coordinate system, using the default instances of
* the {@link UT1System} and {@link TDBSystem}.
*************************************************************************/
public AzAlt(Observatory obs, Refraction refraction, Ephemeris ephemeris) {

    this(obs, refraction, ephemeris, UT1System.getInstance(),
                                     TDBSystem.getInstance() );

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public Aspect getAspect(int type) {

    if(type == Aspect.POLAR) return POLAR;
    else if(type == Aspect.EQUATORIAL) return EQUATORIAL;
    else throw new IllegalArgumentException("Unsupported aspect "+type);

} // end of getAspect method

/*************************************************************************
* Return the observatory specified in the constructor.
* @return The observatory.
*************************************************************************/
public Observatory getObservatory() { return obs; }

/*************************************************************************
*
*************************************************************************/
public UT1System getUT1System() { return UT1; }

/*************************************************************************
*
*************************************************************************/
public TDBSystem getTDBSystem() { return TDB; }

/*************************************************************************
*
*************************************************************************/
public Ephemeris getEphemeris() { return ephemeris; }


/*************************************************************************
*
*************************************************************************/
public Refraction getRefraction() { return refraction; }

/*************************************************************************
* Construct the transform to {@link RADec}. We make this a separate
* method from {@link #toRADec(PreciseDate)} to make it easier to
* write subclasses.
* @param time The time at which the transform will be applied.
*************************************************************************/
protected Transform buildTransform(PreciseDate time) {

    /*******************************
    * Earth Orientation parameters *
    *******************************/
    EOP eop   = (EOP) UT1.convertDate(time);
    PreciseDate tdb = TDB.convertDate(time);

//     long start = System.currentTimeMillis();
//     Transform pos = obs.azAltToTerrestrial();
//     long after_pos = System.currentTimeMillis();
//
//     Transform ref =refraction.refractionTransform(time);
//     long after_ref = System.currentTimeMillis();
//
//     Transform polar = eop.polarMotion();
//     long after_polar = System.currentTimeMillis();
//
//     Transform rot =eop.earthRotation();
//     long after_rot = System.currentTimeMillis();
//
//     Transform prec =eop.precession();
//     long after_prec = System.currentTimeMillis();
//
//     Transform ab =ephemeris.aberration(tdb, eop, obs);
//     long after_ab = System.currentTimeMillis();
//
//     Transform def = ephemeris.deflection(tdb, eop, obs);
//     long end = System.currentTimeMillis();
//
//     System.out.println("    pos:   "+(after_pos - start));
//     System.out.println("    ref:   "+(after_ref - after_pos));
//     System.out.println("    polar: "+(after_polar - after_ref));
//     System.out.println("    rot:   "+(after_rot - after_polar));
//     System.out.println("    prec:  "+(after_prec - after_rot));
//     System.out.println("    ab:    "+(after_ab - after_prec));
//     System.out.println("    def:   "+(end - after_ab));
//     System.out.println("    total: "+(end-start));
//
//     return pos.combineWith(ref)
//               .combineWith(polar)
//               .combineWith(rot)
//               .combineWith(prec)
//               .combineWith(ab)
//               .combineWith(def);

    /*************************
    * assemble the transform *
    *************************/
    Transform to_sky = obs.azAltToTerrestrial()
                          .combineWith(refraction.refractionTransform(time))
                          .combineWith(eop.polarMotion())
                          .combineWith(eop.earthRotation())
                          .combineWith(eop.precession())
                          .combineWith(ephemeris.aberration(tdb, eop, obs))
                          .combineWith(ephemeris.deflection(tdb, eop, obs) );

    return to_sky;




} // end of buildTransform method

/*************************************************************************
* Approximate the time that a celestial coordinate will cross a
* given altitude. Convergence is very rapid, typically giving one second
* accuracy in one or two iterations.
* If you want to find the rise or set time of a slowly moving object
* like the Sun or Moon, then you can iterate, recalculating the position
* for each new approximation of the time.
* <p>
* Although an iteration with this method will reliably converge to the
* nanosecond precision of a PreciseDate, don't expect the results to be
* accurate to better than about a millisecond. However, this is not a
* problem, because rise/set times are not particularly meaningful
* at that level of precision anyway.
* @param radec A direction in RA/Dec coordinates
* @param offset The altitude where the target will be near at the returned time.
*            For example you can use this to choose between the different
*            types of twilight, or to compensate for the angular diameter
*            of a body.
* @param from_horizon If true the offset is measured from the local horizon.
*        Otherwise, it is measured from alt=0.
* @param time A rough guess of the crossing time. This guess can be very
*        rough - any time will do - without harming the convergence rate.
* @param rise - True if you want the time the coordinate is rising,
*               false if you want the set time.
* @return The time that the given RA/Dec crosses the given altitude
*         which is closest to the time argument. If the coordinate is at
*         too high or low a declination, it will never rise or set and
*         this method returns null
*************************************************************************/
public PreciseDate estimateCrossingTime(Direction radec,
                                        double offset, boolean from_horizon,
                                        PreciseDate time, boolean rise) {

        double   test_sign =  1.0;
        if(rise) test_sign = -1.0;

        /*****************
        * cache the time *
        *****************/
        TransformCache cache = TransformCache.makeCache(time);

        EOP eop   = (EOP) UT1.convertDate(cache);
        PreciseDate tdb = TDB.convertDate(cache);

        /******************************************
        * transform the RA/Dec position to Az/Alt *
        ******************************************/
        Direction dir = null;
        {
            Transform trans =  obs.azAltToTerrestrial()
                        .combineWith(eop.polarMotion())
                        .combineWith(eop.earthRotation())
                        .combineWith(eop.precession())
                        .combineWith(ephemeris.aberration(tdb, eop, obs))
                        .combineWith(ephemeris.deflection(tdb, eop, obs) )
                        .invert();

            dir  = trans.transform(radec);

        }

        /******************************************************
        * estimate the crossing altitude from the horizon
        * at the current azimuth of the object. This is OK
        * if the horizon is smooth near the actual crossing
        * azimuth
        ******************************************************/
        double alt = offset;
        if(from_horizon) offset += obs.getHorizon()
                                      .getHorizonAltitude(dir.getLongitude());


      //  System.out.println("    alt="+alt);

        /***************************************
        * adjust the target alt for refraction *
        ***************************************/
        {
            Transform trans = obs.azAltToTerrestrial()
                     .combineWith(refraction.refractionTransform(cache))
                     .combineWith(obs.azAltToTerrestrial().invert());

            alt = trans.transform(new Direction(0, alt)).getLatitude();
        }

      //  System.out.println("    corrected alt="+alt+" offset="+offset);



        /**************************************************
        * get the position of the Earth's rotational axis *
        **************************************************/
        Direction pole = null;
        {
            Transform trans = obs.azAltToTerrestrial()
                            .combineWith(eop.polarMotion())
                            .invert();

            pole = trans.transform(Direction.Z_AXIS);
        }

        /*********************************************
        * these are the two planes for which we need
        * to find the intersection.
        * Note the two normals are unit vectors
        *********************************************/
        ThreeVector n1 = new ThreeVector(pole);
        double d1 = dir.angleBetween(pole).getCos();

        ThreeVector n2 = new ThreeVector(Direction.Z_AXIS);
        double d2 = Math.sin(Math.toRadians(alt));

        /********************************************
        * vector products of the two normal vectors *
        * plus some other stuff we'll need below
        ********************************************/
        ThreeVector cross = n1.cross(n2);
        double dot = n1.dot(n2);

        double det = 1.0-dot*dot;
        double cross2 = cross.dot(cross);

        /*****************************************************
        * coefficients of the line lying on the intersection *
        *****************************************************/
        double c1 = (d1 - d2*dot)/det;
        double c2 = (d2 - d1*dot)/det;


        /*********************************************************
        * find the intersection of the line with the unit sphere *
        * note that there isn't always an intersection.
        *********************************************************/
        double argument = 1.0-(c1*c1 + 2.0*c1*c2*dot + c2*c2);

        double u = 0;
        if(argument>0.0) u = Math.sqrt(argument/cross2);
     //   if(u ==0) System.out.println("no crossing");

        /*************************************************************
        * we want the setting position, so take advantage of the
        * fact the we know things set in the west
        *************************************************************/
        ThreeVector set = n1.times(c1).plus(n2.times(c2)).plus(cross.times(u));
        if(test_sign*set.getY()>0) {
            /******************************
            * nope, we want the other one *
            ******************************/
            set = n1.times(c1).plus(n2.times(c2)).plus(cross.times(-u));
        }

        /*************************************************************
        * get the angle of the Earth's rotation needed to bring
        * current position to the set position.
        * Project the two vectors into the plane of the Earth's rotation
        * and then find the angle between them.
        **************************************************************/
        ThreeVector v1 = new ThreeVector(dir).minus(n1.times(d1));
        ThreeVector v2 = set.minus(n1.times(d1));

        double angle = v1.getDirection()
                         .angleBetween(v2.getDirection())
                         .getRadians();

        /*******************************************
        * angular velocity of the Earth's rotation *
        *******************************************/
        double angular_velocity
                = eap.sky.earth.gravity.Ellipsoid.WGS84.getAngularVelocity();



        /*************************************************************
        * convert that to a time, picking a sign, based on whether
        * we are above or below the horizon
        *****************************************************************/
        double delta_time = angle/angular_velocity;
        if(v1.cross(v2).dot(n1) <0) delta_time = -delta_time;

        /******************
        * adjust the time *
        ******************/
        PreciseDate set_time = TAISystem.getInstance().convertDate(cache);
        set_time.increment(delta_time);

        return set_time;

} // end of estimateCrossingTime method

/*************************************************************************
*
*************************************************************************/
public PreciseDate estimateTimeOnAzimuth(Direction ra_dec, Angle az, boolean plus,
                                  PreciseDate time) {

    time = TransformCache.makeCache(time);
    EOP eop   = (EOP) UT1.convertDate(time);
    PreciseDate tdb = TDB.convertDate(time);

    Transform trans = Coordinates.RA_DEC.getTransformTo(this, time);

    Direction dir = trans.transform(ra_dec);


    /**************************************************
    * current position of the Earth's rotational axis
    * in Az/Alt coordinates
    **************************************************/
    Direction pole = obs.azAltToTerrestrial()
                      .combineWith(refraction.refractionTransform(time))
                      .combineWith(eop.polarMotion())
                      .invert()
                      .transform(Direction.Z_AXIS);

    System.out.println("pole="+pole);

    /***************************************************
    * this is the plane describing the path the object
    * object will follow as the Earth rotates
    ***************************************************/
    ThreeVector n1 = new ThreeVector(pole);
    double d1 = dir.angleBetween(pole).getCos();

    ThreeVector n2 = new ThreeVector(new Direction(az.getSin(), -az.getCos(), 0.0));
    double d2 = 0.0;

    if(!plus) n2 = n2.times(-1.0);

    System.out.println(n2.getDirection());

    /********************************************
    * vector products of the two normal vectors *
    * plus some other stuff we'll need below
    ********************************************/
    ThreeVector cross = n1.cross(n2);
    double dot = n1.dot(n2);

    double det = 1.0-dot*dot;
    double cross2 = cross.dot(cross);

    /*****************************************************
    * coefficients of the line lying on the intersection *
    *****************************************************/
    double c1 = (d1 - d2*dot)/det;
    double c2 = (d2 - d1*dot)/det;


    /*********************************************************
    * find the intersection of the line with the unit sphere *
    * note that there isn't always an intersection.
    *********************************************************/
    double argument = 1.0-(c1*c1 + 2.0*c1*c2*dot + c2*c2);
    if(argument < 0.0) return null;

    double u = Math.sqrt(argument/cross2);

    /**********************************
    * first try the positive solution *
    **********************************/
    ThreeVector soln = n1.times(c1).plus(n2.times(c2)).plus(cross.times( u));
  //  ThreeVector soln2 = n1.times(c1).plus(n2.times(c2)).plus(cross.times(-u));

//     System.out.println(soln1.getDirection());
//     System.out.println(soln2.getDirection());


    if(soln.getX()*az.getCos() + soln.getY()*az.getSin() <0.0) {
        /***********************
        * wrong side of zenith *
        ***********************/
        return null;
    }

        /*************************************************************
        * get the angle of the Earth's rotation needed to bring
        * current position to the set position.
        * Project the two vectors into the plane of the Earth's rotation
        * and then find the angle between them.
        **************************************************************/
        ThreeVector v1 = new ThreeVector(dir).minus(n1.times(d1));
        ThreeVector v2 = soln.minus(n1.times(d1));

        double angle = v1.getDirection()
                         .angleBetween(v2.getDirection())
                         .getRadians();

        /*******************************************
        * angular velocity of the Earth's rotation *
        *******************************************/
        double angular_velocity
                = eap.sky.earth.gravity.Ellipsoid.WGS84.getAngularVelocity();



        /*************************************************************
        * convert that to a time, picking a sign, based on whether
        * we are above or below the horizon
        *****************************************************************/
        double delta_time = angle/angular_velocity;
        if(v1.cross(v2).dot(n1) <0) delta_time = -delta_time;

        /******************
        * adjust the time *
        ******************/
        PreciseDate cross_time = TAISystem.getInstance().convertDate(time);
        cross_time.increment(delta_time);

        return cross_time;





} // end of estimateTimeOnAzimuth method

/*************************************************************************
*
*************************************************************************/
public Transform toRADec(PreciseDate time) {

    return buildTransform(time);

}

/*************************************************************************
*
*************************************************************************/
public Angle PAToRotator(Angle pa, Direction dir, PreciseDate time,
                         Angle radius) {

    /*****************************************************
    * make sure we are caching time stuff for efficiency *
    *****************************************************/
    time = TransformCache.makeCache(time);

    /*********************************************
    * get the current rotation axis of the Earth *
    *********************************************/
    EOP eop = (EOP)UT1System.getInstance().convertDate(time);
    PreciseDate tdb = TDBSystem.getInstance().convertDate(time);
    Direction pole = eop.precession()
                        .combineWith(ephemeris.aberration(tdb, eop, obs))
                        .combineWith(ephemeris.deflection(tdb, eop, obs) )
                        .transform(Direction.Z_AXIS);

    /********************************
    * get the transform from RA/Dec *
    ********************************/
    Transform trans = Coordinates.RA_DEC.getTransformTo(this, time);

    // do I need this?
    Angle angle = pa.negative();

    /***************************************************
    * get the direction which is offset by one degree
    * toward the pole from the given direction
    ***************************************************/
    Direction offset;
    if(dir.equals(pole)) {
        offset = null;
    } else {
        Rotation rot1 = new Rotation(angle, dir);

        Direction axis = pole.perpendicular(dir);
        Rotation rot2 = new Rotation(radius, axis);

        Rotation rot = (Rotation)rot2.combineWith(rot1);
        offset = rot.transform(dir);
    }

    /************************************************************
    * transform the original direction and the offset direction *
    ************************************************************/
    offset = trans.transform(offset);
    dir    = trans.transform(dir);

    Direction norm1 = dir.perpendicular(offset);
    Direction norm2 = dir.perpendicular(Direction.Z_AXIS);

    Angle angle1 = norm1.angleBetween(norm2);
    Angle angle2 = norm1.perpendicular(norm2).angleBetween(dir);

    if(angle2.getCos() < 0.0) {
        angle1 = angle1.negative();
    }

    return angle1.negative();

} // end of PAToRotator method


/***************************************************************************
*
***************************************************************************/
public Direction zenithRADec(PreciseDate time) {

    /*****************************************
    * build the transform without refraction *
    *****************************************/
    EOP eop   = (EOP) UT1.convertDate(time);
    PreciseDate tdb = TDB.convertDate(time);

    Transform trans = obs.azAltToTerrestrial()
                      .combineWith(eop.polarMotion())
                      .combineWith(eop.earthRotation())
                      .combineWith(eop.precession())
                      .combineWith(ephemeris.aberration(tdb, eop, obs))
                      .combineWith(ephemeris.deflection(tdb, eop, obs) );

    return trans.transform(Direction.Z_AXIS);

} // end of zenithRADec method


} // end of AzAlt class
