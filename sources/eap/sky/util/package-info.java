/******************************************************************************
* Mathematical infrastructure for spherical and planar coordinates.
* The cornerstone of this package is the {@link eap.sky.util.Direction} class,
* which represents a point on a unit sphere. Internally it represents this
* point as either a Cartesian unit vector, or a longitude, latitude pair.
* It converts between the two as needed and caches the results for efficiency.
* Direction objects are immutable, so you never need to worry about copying
* when passing them as method arguments.
* <p>
* The {@link eap.sky.util.ThreeVector} class is an arbitrary length vector
* in three dimensional space. It is represented internally as a set of
* Cartesian coordinates or as a Direction and a length. It converts between
* the two automatically and caches the result for efficiency. ThreeVector
* objects are immutable.
* <p>
* The {@link eap.sky.util.Transform} class can convert between one spherical
* coordinate system and another. Examples of Transforms are a rigid
* {@link eap.sky.util.Rotation}, or a deforming {@link eap.sky.util.Deflection}.
* You can chain Transforms together with a {@link eap.sky.util.CompositeTransform}.
* The basic function of positional astronomy
* is determining the correct transform from one coordinate system to another.
* The {@link eap.sky.util.coordinates} package contains instances of a number
* of useful coordinate systems.
* <p>
* The {@link eap.sky.util.Projection} class projects spherical coordinates
* onto a plane and unprojects from a plane back to a sphere.
* The {@link eap.sky.util.Mapping} class converts from one planar coordinate
* system to another. The {@link eap.sky.util.plane} package contains classes
* for describing camera focal planes and the associated coordinate transforms.
* <p>
* The {@link eap.sky.util.numerical} package contains tools for numerical
* computation, such as quadratures and differential equation solvers.
* The {@link eap.sky.util.polyhedron} contains classes which describe 
* complex polyhedra, which can be used for tessellations of a sphere.
******************************************************************************/
package eap.sky.util;