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

package eap.sky.ephemeris;

import eap.sky.time.*;
import eap.sky.time.barycenter.*;
import eap.sky.earth.*;
import eap.sky.util.*;

import java.io.*;

/***************************************************************************
* A generic ephemeris for determining the positions of celestial bodies
* as a function of time. Subclasses provide the calculations of position and
* velocity in barycentric coordinates. This class provides methods which
* correct for light travel time and the position of the observer.
* This class also provides methods for generating distortion transforms
* which require an ephemeris to calculate, specifically aberration and gravity
* deflection.
* <p>
* Note that all times should be specified in TDB, distances are in meters,
* and velocities in meters per second.
* @see TDBSystem
***************************************************************************/
public abstract class Ephemeris implements Serializable {

public static final int MERCURY =  0;
public static final int VENUS   =  1;
public static final int EARTH   =  2;
public static final int MARS    =  3;
public static final int JUPITER =  4;
public static final int SATURN  =  5;
public static final int URANUS  =  6;
public static final int NEPTUNE =  7;
public static final int PLUTO   =  8;
public static final int MOON    =  9;
public static final int SUN     = 10;

public static final double SPEED_OF_LIGHT = 299792458.0;

public static final Direction NEP = new Direction(270.0, 66.56083333);

private UT1System UT1;

/***************************************************************************
* Create a new Ephemeris.
* @param UT1 The UT1 system we will use to calculate the rotation of the
* Earth in order to apply certain corrections.
***************************************************************************/
public Ephemeris(UT1System UT1) {

    this.UT1 = UT1;
}

/***************************************************************************
*
***************************************************************************/
public UT1System getUT1System() { return UT1; }

/***************************************************************************
* Return the position of the given body with respect to the
* system barycenter.
* @param body The body in question. If possible this must be one of the static
* variables of this class.
* @param tdb The time in TDB at which to calculate the position.
* @return the position in meters.
* @see TDBSystem
***************************************************************************/
public abstract ThreeVector barycentricPosition(int body,
                                                   PreciseDate tdb);


/**************************************************************************
* Return the velocity of the given body with respect to the
* system barycenter.
* @param body The body in question. If possible this must be one of the static
* variables of this class.
* @param tdb The time in TDB at which to calculate the position.
* @return the velocity in meters per second.
* @see TDBSystem
**************************************************************************/
public abstract ThreeVector barycentricVelocity(int body,
                                                   PreciseDate tdb);



/***************************************************************************
* Returns the light-delayed topocentric position of a body. This takes into
* account the position of the observer on the surface of the Earth, plus
* the fact that a body appears to be where it was when the light
* left it.
* <p>
* Note that strictly speaking we don't know the distance to the body
*  before calling this method.
* So usually you call {@link #position(int, PreciseDate, EOP, Observatory)},
* which determines this value by calling this method iteratively.
* However, this method could be useful if you want to ignore the light delay
* (i.e. set distance=0), but not the topocentric correction.
*
* @param body The body in question. If possible this must be one of the static
* variables of this class.
* @param tdb The time in TDB at which to calculate the position.
* @param obs the position of the observer on the surface of the Earth.
* @param distance The distance from the observer to the body in meters.
* @return the position in meters.
* @see TDBSystem
***************************************************************************/
public ThreeVector position(int body, PreciseDate tdb, Observatory obs,
                            double distance, ThreeVector earth,
                            ThreeVector topocentric) {

    /*****************************
    * get the light delayed time *
    *****************************/
    PreciseDate delayed = tdb.copy();
    delayed.increment(-distance/SPEED_OF_LIGHT);

    ThreeVector position  = barycentricPosition(body , delayed);

    return position.minus(earth).minus(topocentric);

} // end of position method

/***************************************************************************
* Returns the light-delayed topocentric position of a body.
* This takes into
* account the position of the observer on the surface of the Earth, plus
* the fact that a body appears to be where it was when the light
* left it. This method determines the distance from the body to the
* observer iteratively.
* @param body The body in question. If possible this must be one of the static
* variables of this class.
* @param tdb The time in TDB at which to calculate the position.
* @param obs the position of the observer on the surface of the Earth.
* @return the position in meters.
* @see TDBSystem
***************************************************************************/
public ThreeVector position(int body, PreciseDate tdb, EOP eop,
                            Observatory obs) {


    double distance = 0.0;

    /**************************************************************
    * note these values are evaluated now, and
    * are not delayed to the time the light left the
    * body we're looking at. So we do not need to include these
    * in the iteration loop
    **************************************************************/
    ThreeVector earth = barycentricPosition(EARTH, tdb);
    ThreeVector topocentric = obs.celestialPosition(eop);

   // System.out.println("topocentric="+topocentric);

    for(int iteration = 0; true; ++iteration) {

        double last_distance = distance;
        ThreeVector position = position(body, tdb, obs, distance,
                                        earth, topocentric);
        distance = position.getLength();

      //  System.out.println(iteration+" delay = "+(distance/SPEED_OF_LIGHT));

        if(distance == last_distance || iteration > 3) return position;
    } // end of iteration


} // end of lightDelayedVelocity method

/***************************************************************************
* Returns the light-delayed topocentric velocity of a body.
* This takes into
* account the motion of the observer on the surface of the Earth, plus
* the fact that a body appears to be where it was when the light
* left it.
* @param body The body in question. If possible this must be one of the static
* variables of this class.
* @param tdb The time in TDB at which to calculate the position.
* @param obs the position of the observer on the surface of the Earth.
* @param distance The distance from the observer to the body in meters.
* @return the velocity in meters per second.
* @see TDBSystem
***************************************************************************/
public ThreeVector velocity(int body, PreciseDate tdb,
                                        Observatory obs,
                                        double distance) {

    /*****************************
    * get the light delayed time *
    *****************************/
    PreciseDate delayed = tdb.getTimeSystem().createDate();
    delayed.setTime(tdb);
    delayed.increment(-distance/SPEED_OF_LIGHT);

    ThreeVector velocity  = barycentricVelocity(body , delayed);
    ThreeVector earth     = barycentricVelocity(EARTH, tdb);

    EOP eop = (EOP)UT1.createDate();
    eop.setTime(tdb);
    ThreeVector topocentric = obs.diurnalVelocity(eop);

    return velocity.minus(earth).minus(topocentric);

} // end of lightDelayedVelocity method

/***************************************************************************
* Returns the aberration transform for an observer on the Earth viewing
* objects outside the solar system.
* @param tdb The time in TDB
* @param eop The corresponding Earth Orientation Parameters. If this
*        is null only annual aberration will be computed.
* @param obs The position of The observer on the Earth. If this is null
*        only annual aberration will be computed.
* @return The fully relativistic aberration including both annual and
*         diurnal components, unless eop or obs are null.
* @see Aberration
***************************************************************************/
public Transform aberration(PreciseDate tdb, EOP eop, Observatory obs) {

    ThreeVector velocity = barycentricVelocity(EARTH, tdb);
    if(eop != null && obs != null) {

        velocity = velocity.plus(obs.diurnalVelocity(eop));
    }

    return new Aberration(velocity);

} // end of aberration method

/************************************************************************
* Returns the transform which accounts for the general relativistic
* bending of light rays by the gravity of the Sun. This implementation
* makes the standard first order approximation (see Gravitation and
* Cosmology, Steven Weinberg, John Wiley and Sons, 1972), and ignores
* the affects of all other bodies. We could do a more precise job with this
* in the future.
************************************************************************/
public Transform deflection(PreciseDate tdb, EOP eop,
                            Observatory obs) {


    ThreeVector sun = position(SUN, tdb, eop, obs);

    Deflection deflection = new GravitationalDeflection(1.32712438e20,
                                                         sun.getLength());

    return new DeflectionTransform(deflection, sun.getDirection(), false);

} // end of deflection method

/*************************************************************************
*
*************************************************************************/
public Angle getPhaseAngle(int body, PreciseDate tdb, EOP eop, Observatory obs) {

    if(body == SUN) {
        throw new IllegalArgumentException("Can't calculate the "+
                                           "phase of the Sun");
    }

    ThreeVector sun = position(SUN,  tdb, eop, obs);
    ThreeVector pos = position(body, tdb, eop, obs);

    Direction obs_dir = pos.times(-1.0).getDirection();
    Direction sun_dir = sun.minus(pos).getDirection();

    return obs_dir.angleBetween(sun_dir);

} // end of getPhaseAngle method

/*************************************************************************
*
*************************************************************************/
public Direction getPhaseDirection(int body, PreciseDate tdb, EOP eop,
                                   Observatory obs) {
    /***************
    * sanity check *
    ***************/
    if(body == SUN) {
        throw new IllegalArgumentException("Can't calculate the "+
                                           "phase of the Sun");
    }

    /*******************************************
    * get the position of the sun and the body *
    *******************************************/
    ThreeVector sun = position(SUN,  tdb, eop, obs);
    ThreeVector pos = position(body, tdb, eop, obs);

    Transform trans = new Rotation(new Euler(pos.getDirection(), 0.0));



    sun = new ThreeVector(trans.transform(sun.getDirection()),
                          sun.getLength());

    sun = new ThreeVector(sun.getX(), sun.getY(), sun.getZ() - pos.getLength());
    return sun.getDirection();

} // end of getPhase direction

} // end of Ephemeris class