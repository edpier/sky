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
import java.util.*;

/*************************************************************************
*
*************************************************************************/
public class LocalTimeSystem extends TimeSystem {

private static LocalTimeSystem instance;

UTCSystem UTC;
TimeZone zone;

Conversion[] toUTC;
Conversion[] fromUTC;


/*************************************************************************
*
*************************************************************************/
public LocalTimeSystem(UTCSystem UTC, TimeZone zone) {

    super(zone.getDisplayName(false, zone.LONG),
          zone.getDisplayName(false, zone.SHORT) );



    this.UTC = UTC;
    this.zone = zone;

    toUTC = new Conversion[1];
    toUTC[0] = new toUTC();

    fromUTC = new Conversion[1];
    fromUTC[0] = new fromUTC();

} // end of constructor


/****************************************************************************
*
****************************************************************************/
private void writeObject(ObjectOutputStream out) throws IOException {

    out.writeObject(UTC);
    out.writeObject(zone);

} // end of writeObject method

/****************************************************************************
*
****************************************************************************/
private void readObject(ObjectInputStream in) throws IOException,
                                           ClassNotFoundException  {

    UTC = (UTCSystem)in.readObject();
    zone = (TimeZone)in.readObject();

    toUTC = new Conversion[1];
    toUTC[0] = new toUTC();

    fromUTC = new Conversion[1];
    fromUTC[0] = new fromUTC();

} // end of writeObject method

/*************************************************************************
*
*************************************************************************/
public static LocalTimeSystem getInstance() {

    if(instance == null) {
        instance =  new LocalTimeSystem(UTCSystem.getInstance(),
                                        TimeZone.getDefault());
    }

    return instance;

} // end of getInstance method

/*************************************************************************
*
*************************************************************************/
public UTCSystem getUTCSystem() { return UTC; }

/*************************************************************************
*
*************************************************************************/
public String getDaylightName() {
    return zone.getDisplayName(true, zone.LONG);
}

/*************************************************************************
*
*************************************************************************/
public String getDaylightAbbreviation() {
    return zone.getDisplayName(true, zone.SHORT);
}

/*************************************************************************
*
*************************************************************************/
public TimeZone getTimeZone() { return zone; }

/*************************************************************************
*
*************************************************************************/
public PreciseDateFormat createFormat() { return new LeapsecFormat(this); }

/**************************************************************************
*
**************************************************************************/
public PreciseDate createDate() {

    return new LocalDate(this);

} // end of createDate method

/*************************************************************************
* Returns true if the object is a LocalTimeSystem based on the same UTCSystem
* and time zone.
*************************************************************************/
public boolean equals(Object o) {

    if(!(o instanceof LocalTimeSystem))  return false;

    LocalTimeSystem local = (LocalTimeSystem)o;
    return UTC.equals(local.UTC) && zone.equals(local.zone);

} // end of equals method

/*************************************************************************
*
*************************************************************************/
protected Conversion[] getConversionsFrom() { return toUTC; }


/*************************************************************************
*
*************************************************************************/
protected Conversion[] getConversionsTo() { return fromUTC; }

/*************************************************************************
*
*************************************************************************/
private class toUTC extends Conversion {

/*************************************************************************
*
*************************************************************************/
public toUTC() { super(LocalTimeSystem.this, UTC); }

/*************************************************************************
*
*************************************************************************/
public void convert(PreciseDate from, PreciseDate to) {

    LocalDate local = (LocalDate)from;
    UTCDate utc     =   (UTCDate)to;

    boolean daylight = local.isDaylightSavingsTime();

    int offset = zone.getRawOffset();
    if(daylight) offset += zone.getDSTSavings();

    utc.setTime(local.getMilliseconds() - offset,
                local.getNanoseconds(),
                local.isLeapSecond());

} // end of convert method

} // end of toUTC inner class

/*************************************************************************
*
*************************************************************************/
private class fromUTC extends Conversion {

/*************************************************************************
*
*************************************************************************/
public fromUTC() { super(UTC, LocalTimeSystem.this); }

/*************************************************************************
*
*************************************************************************/
public void convert(PreciseDate from, PreciseDate to) {

//System.out.println("converting UTC to local");

    UTCDate utc     =   (UTCDate)from;
    LocalDate local = (LocalDate)to;


    Date date = new Date(utc.getMilliseconds());
    boolean daylight = zone.inDaylightTime(date);

    int offset = zone.getRawOffset();
    if(daylight) offset += zone.getDSTSavings();

  //  System.out.println("setting");

    local.setTime(utc.getMilliseconds() + offset,
                  utc.getNanoseconds(),
                  ((UTCDate)utc).isLeapSecond(),
                  daylight);

 // System.out.println("done converting");

} // end of convert method

} // end of toUTC inner class

} // end of LocalTime class
