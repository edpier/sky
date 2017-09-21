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

package eap.sky.time;

import java.io.*;


/*****************************************************************************
* Represents the Universal Time Coordinated (UTC) system.
* This system is defined by ITU-R-TF.460-4 (1986)
* (also known as CCIR Recommendation 460-4 (1986)), issued by the
* <a href="http://www.itu.int">International Telecommunication Union</a>.
* <p>
* UTC is offset from TAI by an integral number of seconds. This offset
* changes with time in order to keep UTC linked to the Earth's rotation.
* The offset is changed by "adding a leap second" to the last minute of
* a month. Leap seconds are unpredictable, and are announced at least
* two weeks in advance by the
* <a href="http://www.ierc.org">International Earth Rotation and Reference
* Systems Service (IERS)</a>. See {@link LeapTable} for more information
* about leap seconds.
* <p>
* This class uses a {@link LeapTable} object to keep track of leap seconds.
* You could have multiple instances of this class with different leap
* second tables.
* Two UTCSystem objects are only equal if they have an identical leap
* second table. However, typical usage is to call
* {@link #setDefaultLeapTable(LeapTable)} during initialization, and then
* use the instance returned by {@link #getInstance()}. This prevents an
* accidental mismatch in leap second tables. Note that initially the default
* leaptable is null.
* @see LeapTable
* @see UTCDate
*****************************************************************************/
public class UTCSystem extends TimeSystem {

private static LeapTable default_leaptable = null;
private static UTCSystem instance = null;

/** The difference between TAI and UTC before all leap seconds **/
private static final int TAI_OFFSET = 10000; // 10 s in milliseconds

private Conversion[] utc2tai;
private Conversion[] tai2utc;

private LeapTable leaptable;

/******************************************************************************
* Creates a new UTCSystem class which uses the given LeapTable to convert
* to and from TAI. Unless you need to maintain multiple leap second tables
* simultaneously, it is probably better practice to call
* {@link #setDefaultLeapTable(LeapTable)} during program initialization,
* and then always use the value returned by {@link #getInstance()}.
* @param leaptable The leap second table to be used by this class
******************************************************************************/
public UTCSystem(LeapTable leaptable) {

    super("Universal Time Coordinated", "UTC");

    this.leaptable = leaptable;

    utc2tai = new Conversion[1];
    utc2tai[0] = new UTCtoTAI();

    tai2utc = new Conversion[1];
    tai2utc[0] = new TAItoUTC();

} // end of constructor

/****************************************************************************
*
****************************************************************************/
private void writeObject(ObjectOutputStream out) throws IOException {

    out.writeObject(leaptable);

} // end of writeObject method

/****************************************************************************
*
****************************************************************************/
private void readObject(ObjectInputStream in) throws IOException,
                                           ClassNotFoundException  {

    this.leaptable = (LeapTable)in.readObject();

    utc2tai = new Conversion[1];
    utc2tai[0] = new UTCtoTAI();

    tai2utc = new Conversion[1];
    tai2utc[0] = new TAItoUTC();

} // end of writeObject method

/******************************************************************************
* Sets the leap second table to be used by the object returned by
* {@link #getInstance()}.
******************************************************************************/
public static void setDefaultLeapTable(LeapTable leaptable) {
    default_leaptable = leaptable;
    instance = new UTCSystem(leaptable);
}

/******************************************************************************
* Returns a UTCSystem which uses the leap second table specified in the last
* call to {@link #setDefaultLeapTable(LeapTable)}.
* @return a UTCSystem instance which uses the default leap second table, or
* null if no default leap second table has been specified.
******************************************************************************/
public static UTCSystem getInstance() {

    if(instance == null) {
        throw new IllegalStateException("Default UTC not initialized");
    }

    return instance;

} // end of getInstance method

/******************************************************************************
*
******************************************************************************/
public PreciseDateFormat createFormat() { return new LeapsecFormat(this); }

/*****************************************************************************
*
*****************************************************************************/
public PreciseDate createDate() {

    return new UTCDate(this);

} // end of createDate method

/*****************************************************************************
* Creates a UTCDate for this time system, which corresponds to the given
* Java {@link java.util.Date}.
* We assume that {@link java.util.Date#getTime()} gives the number
* of miliseconds since
* January 1, 1970 00:00:00.0 UTC, <em>excluding leap seconds</em>.
* Note that the argument will be treated as a generic Java Date, even
* if it is actually a PreciseDate. The created UTCDate will not be marked as
* a leap second, so it can never correspond to 23:59:60.
* @return The UTCDate corresponding to the given Java date.
*****************************************************************************/
public UTCDate convertDate(java.util.Date date) {

    UTCDate utc = (UTCDate)createDate();
    utc.setTime(date.getTime(), 0, false);

    return utc;

} // end of createDate method

/******************************************************************************
* Returns the leap second table for this UTCSystem.
******************************************************************************/
public LeapTable getLeapTable() { return leaptable; }


/******************************************************************************
* Returns the conversion to TAI using this object's leap second table.
******************************************************************************/
protected Conversion[] getConversionsTo() { return tai2utc; }

/******************************************************************************
* Returns the conversion from TAI using this object's leap second table.
******************************************************************************/
protected Conversion[] getConversionsFrom() { return utc2tai; }

/******************************************************************************
* Returns true if the object is a UTCDate which uses the same leap second
* table as this one.
******************************************************************************/
public boolean equals(Object o) {

    if(!(o instanceof UTCSystem))  return false;

    UTCSystem utc = (UTCSystem)o;
    return leaptable == utc.leaptable;

} // end of equals method

/******************************************************************************
* returns a hash code consistent with {@link #equals(Object)}.
******************************************************************************/
public int hashCode() {

    return super.hashCode() + leaptable.hashCode();

}

/******************************************************************************
* Represents a conversion from UTC to TAI using the enclosing class's
* leap second table.
******************************************************************************/
private class UTCtoTAI extends Conversion {

/*****************************************************************************
* Create a new conversion.
* @param utc The enclosing class
*****************************************************************************/
public UTCtoTAI() { super(UTCSystem.this, TAISystem.getInstance()); }

/*****************************************************************************
* Does the conversion.
* @param from the UTC date
* @param to   the TAI date
* @throws InvalidDateException if the {@link UTCDate#isValid()} returns false
*         for the UTC date.
* @throws NoLeapTableException if the time system has no leap second table.
*****************************************************************************/
public void convert(PreciseDate from, PreciseDate tai) {

    if(leaptable == null) throw new NoLeapTableException();

    UTCDate utc = (UTCDate)from;

    /******************************
    * make sure the date is valid *
    ******************************/
    if(!utc.isValid()) throw new InvalidDateException(utc);

    /*****************
    * convert to TAI *
    *****************/
    tai.setTime(utc.getMilliseconds() + TAI_OFFSET +
            1000*leaptable.leapSecondsOnOrBefore(utc),
            utc.getNanoseconds() );

} // end of convert method

} // end of UTCtoTAI inner class

/******************************************************************************
* An inner class for converting from TAI to UTC.
******************************************************************************/
private class TAItoUTC extends Conversion {

/*****************************************************************************
* Create a new conversion.
* @param utc The enclosing class
*****************************************************************************/
public TAItoUTC() { super(TAISystem.getInstance(), UTCSystem.this); }

/*****************************************************************************
* Does the conversion.
* @param from the TAI date
* @param to   the UTC date
* @throws InvalidDateException if the {@link UTCDate#isValid()} returns false
*         for the UTC date.
* @throws NoLeapTableException if the time system has no leap second table.
*****************************************************************************/
public void convert(PreciseDate tai, PreciseDate to) {

    if(leaptable == null) throw new NoLeapTableException();

    UTCDate utc = (UTCDate)to;

    utc.setTime(tai.getMilliseconds() - TAI_OFFSET -
                1000*leaptable.leapSecondsOnOrBefore(tai),
                tai.getNanoseconds(),
                leaptable.isLeapSecond(tai));

} // end of convert method

} // end of UTCtoTAI inner class

} // end of UTCSystem class
