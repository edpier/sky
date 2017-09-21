/******************************************************************************
* Classes for precise time-keeping in various time systems.
* Accurate time keeping is an intricate business full of common
* misunderstandings. Java's built-in {@link java.util.Date} class is
* insufficient for positional astronomy for two reasons. First, it only
* has millisecond precision. Second, it does not handle different timekeeping
* systems and only represents dates in UTC, though even this is not well defined.
* <p>
* The centerpiece of this package is the {@link eap.sky.time.PreciseDate} class.
* PreciseDate is a subclass of Date. It adds nanosecond resolution and contains
* a {@link eap.sky.time.TimeSystem} object which specifies how its time is
* reckoned. Some time systems, such as UTC, use subclasses of PreciseDate
* so, although PreciseDate has a public constructor, you should always use 
* the {@link eap.sky.time.TimeSystem#createDate()} method instead.
* PreciseDates are mutable. You can set the milliseconds and nanoseconds
* directly, or increment the date by some number of seconds. You can also
* set the time from another PreciseDate. If the dates have different time
* systems, conversion happens automatically. Because PreciseDates are
* mutable, you should be careful when passing them as method arguments
* Use {@link eap.sky.time.PreciseDate#copy()} as needed to keep your code
* safe.
* <p>
* A {@link eap.sky.time.CachedDate} is an unmutable PreciseDate which 
* caches copies of itself converted to different time systems. It helps
* make code more efficient by avoiding unnecessary repeated conversions.
* See also {@link eap.sky.util.TransformCache}.
* <p>
* Each timekeeping system is represented by a subclass of TimeSystem. This
* class is responsible for creating the proper kind of PreciseDate objects
* and converting to and from other time systems. Most time systems require
* configuration, such as a table of leap seconds for UTC, or can have more
* than one realization (e.g. different ways of calculating general
* relativistic corrections). By convention each time system has a static
* getInstance() method which returns a default instance of the class.
* Usually, there are other methods for configuring this default object,
* which an application should call on startup.
* <p>
* This library contains implementations of the following time systems:
* <ul>
* <li> {@link eap.sky.time.TAISystem} This is International Atomic Time, as
* kept by a collection of atomic clocks adjusted to sea level. TAI is 
* <em>not</em> tied to the rotation of the Earth, so it is not fixed with
* respect to civil time. Most time systems are referenced to TAI.</li>
* <p>
* <li> {@link eap.sky.time.UTCSystem} is Universal Time Coordinated or civil time.
* This is essentially what is commonly known as Greenwich Mean Time. It is
* tied roughly to the rotation of the earth, but always differs by integral
* numbers of seconds from TAI. UTC adds "leap seconds" as needed at midnight
* New Years Eve.</li>
* <p>
* <li> {@link eap.sky.earth.UT1System} is Universal Time tied directly to
* the rotation of the earth. It is not so much a time system as a measure of
* the Earth rotation angle, because the lengths of its seconds are continually
* varying.
* </li>
* <p>
* <li> {@link eap.sky.time.LocalTimeSystem} is the time on your local clock
* taking time zones into account.</li>
* <p>
* <li> {@link eap.sky.time.TTSystem} is Terrestrial Time. It is a refinement of
* TAI. There are two implementations of TT. {@link eap.sky.time.PlainTTSystem}
* is offset from TAI by a constant 32.184 seconds. 
* {@link eap.sky.time.BIPMTTSystem} is the highest accuracy time system in
* existence. It uses millisecond pulsars to correct the atomic clocks used
* to measure TAI.</li>
* <p>
* <li> {@link eap.sky.time.barycenter.TDBSystem} is the time used for ephemeris
* calculations. It is measured in the solar system barycenter, taking
* relativistic corrections into account.</li>
* </ul>
* <p>
* The {@link eap.sky.time.JulianDate} class provides an alternate notation
* of time commonly used in astronomy. It handles both regular Julian Date (JD)
* and Modified Julian Date (MJD).
* <p>
* See also the {@link eap.sky.time.cycles} package for more time-related classes.
* These handle concepts of regular periods of time like nights, lunations, etc.
* these are useful for observation planning and other things.
******************************************************************************/
package eap.sky.time;