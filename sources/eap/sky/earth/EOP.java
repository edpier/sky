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

/***************************************************************************
* Represents a set of Earth orientation parameters. This inherits from
* PreciseDate, since the rotational orientation of the Earth is commonly
* expressed as a time in the UT1 system (see {@link UT1System}).
* <p>
* In addition to the UT1 time, this class holds the polar motion x and
* y parameters, as well as observed corrections to precession. It is also able
* to calculate precession/nutation using the precession model of the
* corresponding {@link UT1System}.
* <p>
* One reason for including the polar motion parameters in this class
* is that they are necessary to convert to and from UT0,
* should we later decide to implement that time system.
***************************************************************************/
public class EOP extends PreciseDate {

double error;

PolarMotionParameters polar_motion;
PrecessionCorrection precession_correction;

TidalArguments args;

Rotation precession;


/***************************************************************************
* Create a new EOP in the given UT1 time system. Typically, you would
* use {@link UT1System#createDate()} instead of this constructor.
* @param system The time system for this date.
***************************************************************************/
public EOP(TimeSystem system) {

    super(system);

    error = 0.0;
    polar_motion = null;
    precession_correction = null;

} // end of constructor

/***************************************************************************
* Returns the error in UT1
* @return the error in UT1 in seconds or zero if this is not available.
***************************************************************************/
public double getTimeError() { return error; }

/****************************************************************************
* Returns the polar motion parameters at this time.
* @return Polar motion.
****************************************************************************/
public PolarMotionParameters getPolarMotionParameters() {
    return polar_motion;
}

/****************************************************************************
* Returns the observed correction to the modeled precession/nutation.
* @return The precession correction.
****************************************************************************/
public PrecessionCorrection getPrecessionCorrection() {
    return precession_correction;
}

/****************************************************************************
* Returns a set of tidal arguments  useful for calculating precession/nutaton.
* @return the tidal arguments at this time.
****************************************************************************/
public TidalArguments getTidalArguments() {
    return args;
}

/***************************************************************************
* Copies the time, polar motion and precession values.
* @param date a EOP using the same time system as this one. Note this
*        method does not check that the systems are the same.
* @throws ClassCastException if the argument is now a EOP.
***************************************************************************/
protected void copyContentsFrom(PreciseDate date) {

    EOP eop = (EOP)date;

    setTime(eop.getMilliseconds(), eop.getNanoseconds(),
            eop.error, eop.polar_motion, eop.precession_correction,
            eop.args);
}

/***************************************************************************
* Sets the UT1 time, and sets
***************************************************************************/
public void setTime(long millisec, int nanosec) {
    setTime(millisec, nanosec, 0.0, null, null, null);
}

/****************************************************************************
* Sets the UT1 time and all of this classes additional data.
* @param millisec The number of UT1 milliseconds since 1970-01-01T00:00:00.
* @param nanosec The number of nanoseconds UT1 since the last intergral
*        millisecond.
* @param error The accuracy of the UT1
* @param polar The polar motion parameters.
* @param prec A set of observed corrections to the precession model.
* @param args A set of values used for calculating tidal effects which
*        are a calculated from the itme in TDB.
****************************************************************************/
public void setTime(long millisec, int nanosec, double error,
                    PolarMotionParameters polar,
                    PrecessionCorrection prec,
                    TidalArguments args) {

    super.setTime(millisec, nanosec);

    this.error = error;
    this.polar_motion = polar;
    this.precession_correction = prec;
    this.args = args;

    precession = null;

} // end of full setTime method

/****************************************************************************
* Generates the transform due to precession. Specifially this is the
* transform from the coordinates formed by the Celestial Intermediate Pole
* and the Celestial Ephemeris Origin and the Geocentric Celestial Reference
* System.
* @return the precession/nutation transform (CIP/CEO to GCRS).
****************************************************************************/
public Rotation precession() {

    precession = Rotation.IDENTITY;

    if(precession == null) {

        /************************************************
        * get the precession model for this time system *
        ************************************************/
     //   long start = System.currentTimeMillis();

        PrecessionModel model =
                            ((UT1System)getTimeSystem()).getPrecessionModel();

        double x = model.calculateX(args);
        long x_time = System.currentTimeMillis();
        double y = model.calculateY(args);
        long y_time = System.currentTimeMillis();
        double s = model.calculateS(args, x, y);
        long s_time = System.currentTimeMillis();

        double dX = precession_correction.getXCorrection();
        double dY = precession_correction.getXCorrection();

        /******************************************************
        * note the conversion from milliarcseconds to radians *
        ******************************************************/
        x += Math.toRadians(dX/(3600*1000));
        y += Math.toRadians(dY/(3600*1000));

        precession =  model.calculateRotation(x, y, s);
//         long end = System.currentTimeMillis();
//         System.out.println("precession took "+(end-start)+
//                            " x "+(x_time-start)+
//                            " y "+(y_time-x_time)+
//                            " s "+(s_time-y_time)  );
    } // end of we need to calculate the precession

    return precession;

} // end of getPrecession method

/****************************************************************************
* Returns the Earth rotation angle.
* @return the Earth Rotation angle in degrees.
****************************************************************************/
public double earthRotationAngle() {

        JulianDate jd_ut1 = new JulianDate(this);
      //  System.out.println("MJD UT1 = "+jd_ut1.getModifiedJulianDate() );
        double Tu =(jd_ut1.getNumber() - 2451545) + jd_ut1.getFraction();

        return 360.0 * (0.7790572732640 + 1.00273781191135448*Tu);

} // end of earthRotationAngle method

/****************************************************************************
* Returns the transform due to the rotation of the Earth. Specifically this
* is the transform from the coordinate system formed by the
* Celestial Intermediate Pole, and the Terrestrial Ephemeris Origin, and
* the coordinate system formed by the Celestial Intermediate Pole and
* the celestial Ephemeris Origin.
* @return The CIP/TEO to CIP/CEO transform.
****************************************************************************/
public Rotation earthRotation() {

    return new Rotation(new Angle(-earthRotationAngle()), Direction.Z_AXIS);

} // end of earthRotation method

/****************************************************************************
* Returns the transform due to polar motion. Specifically this is the
* transform from International Terrestrial Reference System cordinates
* to the coordinates formed by the Celestial Intermediate Pole and the
* Terrestrial Ephemeris Origin.
* @return The polar motion transform from ITRS to CIP/TEO coordinates.
****************************************************************************/
public Rotation polarMotion() {

    return polar_motion.rotation(args.getJulianCenturiesTDB());

//     /*****************************************************************
//     * calculate s-prime, which describes the motion of Terrestrial
//     * Ephemeris Origin. It moves very slowly. The time should
//     * *probably be in TT, but we have TDB handy, so we'll use that
//     * since the difference is trivial.
//     ****************************************************************/
//     double x = polar_motion.getX()/3600.0;
//     double y = polar_motion.getY()/3600.0;
//     double sprime =  -47e-6/3600.0 * args.getJulianCenturiesTDB();
// 
// //System.out.println("x="+polar_motion.getX()+" y="+polar_motion.getY()+" arcsec");
// 
// //     Rotation first  = new Rotation(sprime   , Rotation.Z_AXIS);
// //     Rotation second = new Rotation(-x, Rotation.Y_AXIS);
// //     Rotation third  = new Rotation(-y, Rotation.X_AXIS);
// 
//     Rotation first  = new Rotation(y, Direction.X_AXIS);
//     Rotation second = new Rotation(x, Direction.Y_AXIS);
//     Rotation third  = new Rotation(-sprime   , Direction.Z_AXIS);
// 
// //System.out.println(((Rotation) first.combineWith(second).combineWith(third)).getAngle()*3600.0);
// 
//     return (Rotation) first.combineWith(second).combineWith(third);

} // end of polarMotion method

/****************************************************************************
* Returns the combined polar motion, rotation, and precession transform.
* Specifically, this is the transform from the International Terrestrial
* Reference System to the Geocentric Celestial Reference System
* @return the full ITRS to GCRS transform.
****************************************************************************/
public Rotation terrestrialToCelestial() {

    return (Rotation) polarMotion().combineWith(earthRotation())
                                   .combineWith(precession());

} // end of terrestrialToCelestial method




} // end of EOP class
