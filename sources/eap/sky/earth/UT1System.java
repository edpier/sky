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

import java.io.*;

/****************************************************************************
* Represents the Universal Time 1 (UT1) system. UT1 is tied to the rotation
* of the Earth. Historically, time has been measured with respect to the
* rising and setting of the Sun. However, in the modern era of atomic
* clocks, the rotation of the Earth makes for a highly unstable time
* standard. Tidal effects, changes in the angular momentum of the
* atmosphere, seasonal changes in the polar ice caps, movement within
* the Earth's core, and other effects all cause measurable changes in the
* Earth's rotation on a daily basis.
* However, UT1 is still vitally important for determining the orientation
* of the Earth with respect to the sky.
* <p>
* Since 2003-01-01 UT1 has been defined as directly proportional to the
* Earth Rotation Angle
* (see <a href="http://maia.usno.navy.mil/conv2003.html">
* IERS Technical Note 32</a>). Previous to that date, a different definition
* was in effect (see <a href="http://maia.usno.navy.mil/conventions.html">
* IERS Technical Note 21</a>.
* <p>
* There can be multiple instances of this class, each one associated
* with a table of Earth orientation parameters (see {@link EOPTable}).
* Two UT1 time systems are equal if and only if they use the same table
* object. The definition of UT1 depends on the table in use. Currently,
* only the post 2003 definition is supported by this package.
* <p>
* Currently, this class defines a conversion from TAI to UT1, but not
* its inverse. This is because the former conversion is more common,
* and the latter would require inverting an interpolated function.
* The transform is from TAI and not UTC, since this makes it easier to
* account for leap seconds.
* <p>
* We extend the concept of UT1 to encompass all motion of the Earth, incuding
* polar motion and precession/nutation.
* @see EOP
****************************************************************************/
public class UT1System extends TimeSystem {

private static UT1System default_system = null;

private static final Conversion[] EMPTY = {};

EOPTable eop;
PrecessionModel precession;

Conversion[] utc2ut1;

/***************************************************************************
* Create a new instance of this class which uses the given Earth orientation
* parameter table. Typically, the user will call
* {@link #setDefaultEOPTable(EOPTable)} during initialization, and then
* call {@link #getInstance()} instead of using this constructor.
* The only reason to use this constructor directly woul dbe if you want to
* support two different Earth orientation parameter tables simultaneously.
* @param eop The Earth Orientation parameter table.
* @param precession The model used to calculate precession and nutation.
***************************************************************************/
public UT1System(EOPTable eop, PrecessionModel precession) {

    super("Universal Time 1", "UT1");

    this.eop = eop;
    this.precession = precession;

    /***********************************
    * initialize the conversions array *
    ***********************************/
    this.utc2ut1 = new Conversion[1];
    this.utc2ut1[0] = new TAItoUT1();

} // end of constructor


/****************************************************************************
*
****************************************************************************/
private void writeObject(ObjectOutputStream out) throws IOException {

    out.writeObject(eop);
    out.writeObject(precession);

} // end of writeObject method

/****************************************************************************
*
****************************************************************************/
private void readObject(ObjectInputStream in) throws IOException,
                                           ClassNotFoundException  {

    eop        =        (EOPTable)in.readObject();
    precession = (PrecessionModel)in.readObject();

    /***********************************
    * initialize the conversions array *
    ***********************************/
    this.utc2ut1 = new Conversion[1];
    this.utc2ut1[0] = new TAItoUT1();

} // end of writeObject method

/***************************************************************************
* Sets the Earth orientation paramater table to be used by the
* system returned by {@link #getInstance()}.
* @param eop The default Earth orientation parameter table.
***************************************************************************/
public static void setDefaultEOPTable(EOPTable eop) {

    setDefaults(eop, IAU2000APrecession.getInstance());
}

/***************************************************************************
* Sets the Earth orientation paramater table to be used by the
* system returned by {@link #getInstance()}.
* @param eop The default Earth orientation parameter table.
***************************************************************************/
public static void setDefaults(EOPTable eop, PrecessionModel precession) {

    default_system = new UT1System(eop, precession);
}

/***************************************************************************
* Returns a a UT1 time system which uses the Earth orientation parameter
* table specified in the last call to {@link #setDefaultEOPTable(EOPTable)}.
***************************************************************************/
public static UT1System getInstance() {

    if(default_system == null) {
        throw new IllegalStateException("Default UT1 not initialized");
    }

    return default_system;

} // end of getInstance method

/***************************************************************************
* Returns the precession/nutation modelspecified in the constructor.
* @return the precession model.
***************************************************************************/
public PrecessionModel getPrecessionModel() { return precession; }

/***************************************************************************
*
***************************************************************************/
public EOPTable getEOPTable() { return eop; }


/***************************************************************************
* Creates a new {@link EOP} object in this time system.
* @return a new date object
***************************************************************************/
public PreciseDate createDate() {

    return new EOP(this);

} // end of createDate method



/***************************************************************************
* Returns an empty array.
***************************************************************************/
protected  Conversion[] getConversionsFrom() { return EMPTY; }


/***************************************************************************
* Returns a conversion from TAI to UT1.
***************************************************************************/
protected Conversion[] getConversionsTo() { return utc2ut1; }

/***************************************************************************
* Checks if the given object is the same time system.
* @return true if the object is a UT1System which uses the same
*         {@link EOPTable} and precession model as this one.
***************************************************************************/
public boolean equals(Object o) {

    if(! (o instanceof UT1System)) return false;

    UT1System ut1 = (UT1System)o;
    return eop == ut1.eop && precession == ut1.precession;

} // end of equals method

/*************************************************************************
* Defines the conversion from TAI to UT1. This really just a thin
* wrapper around the enclosing object's {@link EOPTable}.
*************************************************************************/
private class TAItoUT1 extends Conversion {

/*************************************************************************
* Creates a new conversion.
*************************************************************************/
public TAItoUT1() { super(TAISystem.getInstance(), UT1System.this); }

/*************************************************************************
* Perform the conversion by calling
* {@link EOPTable#getUT1(PreciseDate, EOP).
*************************************************************************/
public void convert(PreciseDate from, PreciseDate to) {

    eop.getUT1(from, (EOP)to);

} // end of convert method

} // end of UTCtoUT1 inner class

} // end of UT1System class
