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

package eap.sky.earth;

import eap.sky.time.*;
import eap.sky.util.*;

import java.io.*;

/***************************************************************************
* The location and orientation of an observer, plus the local gravity vector.
***************************************************************************/
public class Observatory implements Serializable {

ThreeVector location;
double height;
double gravity;
Direction zenith;
ThreeVector velocity;

LocalTimeSystem local_time;
Horizon horizon;

/***************************************************************************
* Create a new observatory.
*
* @param location the position of the observatory in ITRS coordinates,
*        measured in meters.
* @param height the height in meters above the geoid. The geoid is an
*        equipotential surface which corresponds roughly to mean sea level.
*        The reference ellipsoid gives a first order approximation to
*        the geoid, but the geoid and the reference ellipsoid can differ
*        by tens of meters.
* @param gravity The local gravity vector in meters per second squared.
         The vector should point downward.
* @param velocity the velocity of the observatory in ITRS coordinates due to
*        the rotation of the Earth, measured in meters per second. Note that
*        strictly speaking, this value should include the effects of polar
*        motion and precession. However, if you assume the ITRS pole is
*        the rotation axis, and use the WGS84 reference ellipsoid standard
*        value for the angular velocity of the Earth, you get a value
*        which is accurate to ~0.001 m/s. The resulting error in aberration
*        is on the order of a microarcsecond.
* @see eap.sky.ephemeris.Aberration
* @see eap.sky.earth.gravity.Ellipsoid
***************************************************************************/
public Observatory(ThreeVector location, double height, ThreeVector gravity,
                   ThreeVector velocity, LocalTimeSystem local_time,
                   Horizon horizon) {

    this.location = location;
    this.height   = height;
    this.gravity  = gravity.getLength();
    this.zenith   = gravity.getDirection().oppositeDirection();
    this.velocity = velocity;
    this.local_time = local_time;
    this.horizon = horizon;

} // end of Observatory

/***************************************************************************
* Returns the position of the observatory in ITRS coordinates.
***************************************************************************/
public ThreeVector getLocation() { return location; }

/***************************************************************************
* Returns the position of the observatory in ITRS coordinates.
* @param eop A set of Earth orientation Parameters.
***************************************************************************/
public ThreeVector celestialPosition(EOP eop) {

    Rotation rot = eop.terrestrialToCelestial();

    return location.rotate(rot);

} // end of getCelestialLocation method

/***************************************************************************
* returns the velocity of the observatory in GCRS coordinates. This is
* calculated by transforming the velocity in ITRS coordinates specified in
* the constructor.
* @param eop A set of Earth orientation parameters.
* @return the velocity of the observatory with respect to the Earth in
*         meters per second
* @see #Observatory(ThreeVector, double, ThreeVector, ThreeVector, LocalTimeSystem, Horizon)
***************************************************************************/
public ThreeVector diurnalVelocity(EOP eop) {

 //   Rotation rot = eop.terrestrialToCelestial();

    return velocity.rotate(eop.terrestrialToCelestial());

} // end of diurnalVelocity method

/***************************************************************************
* Returns the height of the observatory above the geoid.
* @return Height above the geoid in meters.
***************************************************************************/
public double getHeight() { return height; }

/***************************************************************************
*
***************************************************************************/
public LocalTimeSystem getLocalTimeSystem() { return local_time; }

/***************************************************************************
*
***************************************************************************/
public Horizon getHorizon() { return horizon; }

/***************************************************************************
* Returns the magnitude of the local acceleration of gravity, including
* the rotation of the Earth.
* @return local gravity in m/s<sup>2</sup>
***************************************************************************/
public double getGravity() { return gravity; }

/***************************************************************************
* Returns the direction of the local zenith. This points in the opposite
* direction as the local gravity vector. This vector is in ITRS coordinates.
* @return the direction of the zenith.
***************************************************************************/
public Direction getZenith() { return zenith; }

/***************************************************************************
* Generates the transform from Azimuth and Altitude to ITRS coordinates.
* @return The transform from alt/az to ITRS.
***************************************************************************/
public Transform azAltToTerrestrial() {

    return FlipY.getInstance().combineWith(
           new Rotation(new Euler(zenith.getLongitude(),
                                  90.0-zenith.getLatitude(),
                                  180.0)).invert());

} // end of azAltToTerrestrial method


} // end of Observatory class
