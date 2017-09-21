/******************************************************************************
* Tools for calculating the positions of Solar System bodies.
* The central class in this package is {@link eap.sky.ephemeris.Ephemeris}.
* It provides the general framework for a model of solar system motion.
* The current state of the art high accuracy ephemeris is 
* {@link eap.sky.ephemeris.JPLDE405Ephemeris}, which is a precomputed 
* ephemeris stored in a set of files.
* <p>
* Each instance of {@link eap.sky.util.coordinates.AzAlt} has an associated
* ephemeris, which it uses to compute
* {@link eap.sky.ephemeris.Aberration} and
* {@link eap.sky.ephemeris.GravitationalDeflection}.
* If you want to neglect both of these effects, you can use a
* {@link eap.sky.ephemeris.NoEphemeris}.
******************************************************************************/
package eap.sky.ephemeris;