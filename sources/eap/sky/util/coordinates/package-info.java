/******************************************************************************
* Coordinate systems, such as 
* {@link eap.sky.util.coordinates.RADec} and 
* {@link eap.sky.util.coordinates.AzAlt}.
* Positional astronomy is all about transforming between different coordinate
* systems. The {@link eap.sky.util.coordinates.Coordinates} class defines a
* particular coordinate system by defining the {@link eap.sky.util.Transform}
* to other coordinate systems. In particular each subclass of Coordinates
* must define the Transform to RA/Dec coordinates for a given 
* {@link eap.sky.time.PreciseDate}. This is sufficient for Coordinates to
* determine the transform to any other type of coordinates.
* <p>
* For the most part this package contains various subclasses of Coordinates.
* It also contains {@link eap.sky.util.coordinates.SexigesimalFormat} which
* formats decimal numbers as degrees, minutes, seconds or hours, minutes, 
* seconds and parses the same.
******************************************************************************/
package eap.sky.util.coordinates;