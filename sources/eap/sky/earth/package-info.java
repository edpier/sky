/******************************************************************************
* The orientation of the Earth, including the UT1 time system.
* The transform between coordinates fixed with respect to the Earth and
* celestial coordinates is just a simple rotation. By convention this rotation
* is broken into three components. First there is polar motion, which is
* the orientation of the Earth's rotation axis moves with respect to its crust.
* Second, the Earth rotation angle specifies the daily spin of the Earth on 
* its axis. This is the largest component of the Earth's motion. It is
* directly related to UT1 time. Finally, precession gives the orientation of
* the Earth's rotation axis with respect to celestial coordinates. Precession
* is due to tidal forces on the Earth from the Moon, Sun and other solar
* system bodies. Sometimes precession is itself divided into subcomponents
* called "precession" and "nutation", however here the combined effects
* are just called "precession".
* <p>
* Polar motion and Earth rotation are due to complicated mass motions 
* inside the Earth as well as better understood celestial motions.
* Therefore they can only be predicted a relatively short
* time into the future. The US Naval Observatory publishes tables of
* past measurements and future predictions of both.
* The {@link eap.sky.earth.EOPTable} class represents one of these tables.
* "EOP" stands for "Earth Orientation Parameters".
* The {@link eap.sky.earth.EOP} class represents a single set of values. It is
* a subclass of {@link eap.sky.time.PreciseDate}, because it also represents
* an instant in {@link eap.sky.earth.UT1System} time.
* <p>
* A {@link eap.sky.earth.PrecessionModel} can predict precession farther
* into the future. The current state of the art is
* {@link eap.sky.earth.IAU2000APrecession}. This is a complicated model with
* numerous harmonic terms from all of the celestial bodies.
* <p>
* The {@link eap.sky.earth.Observatory} class specifies the location of the
* observer on the Earth. It also gives the direction of the local zenith,
* the local {@link eap.sky.earth.Horizon} and 
* {@link eap.sky.time.LocalTimeSystem}.
* <p>
* The {@link eap.sky.earth.atmosphere} package deals with the Earth's
* atmosphere - meteorological conditions and atmospheric refraction.
* The {@link eap.sky.earth.gravity} contains classes which describe the
* shape of the Earth and its gravitational field.
******************************************************************************/
package eap.sky.earth;