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

/****************************************************************************
*
****************************************************************************/
public class CachedDate extends PreciseDate {

PreciseDate original;

/****************************************************************************
*
****************************************************************************/
private static CachedTimeSystem getCachedTimeSystem(PreciseDate date) {

    if(date instanceof CachedDate) {
        return (CachedTimeSystem)date.getTimeSystem();
    } else {
        return new CachedTimeSystem(date);
    }
}

/****************************************************************************
*
****************************************************************************/
public static PreciseDate uncache(PreciseDate time, TimeSystem system) {

    if(time instanceof CachedDate)               return system.convertDate(time);
    else if(time.getTimeSystem().equals(system)) return time.copy();
    else                                         return system.convertDate(time);

} // end of uncache static method

/****************************************************************************
*
****************************************************************************/
public static PreciseDate uncache(PreciseDate time) {

    if(time instanceof CachedDate) {
        CachedDate cache = (CachedDate)time;
        return cache.original.copy();

    } else {
        return time;
    }

} // end of uncache static method

/****************************************************************************
*
****************************************************************************/
public CachedDate(PreciseDate date) {

  //  super(new CachedTimeSystem(date));
    super(getCachedTimeSystem(date));

    original = date;


} // end of constructor

/****************************************************************************
*
****************************************************************************/
private void writeObject(ObjectOutputStream out) throws IOException {

    out.defaultWriteObject();
    out.writeObject(original);

} // end of writeObject method

/****************************************************************************
*
****************************************************************************/
private void readObject(ObjectInputStream out) throws IOException,
                                           ClassNotFoundException  {

    out.defaultReadObject();
    original = (PreciseDate)out.readObject();

} // end of writeObject method

/****************************************************************************
*
****************************************************************************/
public PreciseDate copy() { return this; }

/****************************************************************************
*
****************************************************************************/
protected int compareTimes(PreciseDate date) {

    /**********************************************
    * we are comparing two cached dates
    * try to find a cached time system in common
    **********************************************/
    CachedDate cached = (CachedDate)date;
    Set<TimeSystem> this_systems = ((CachedTimeSystem)system)
                                            .getCachedTimeSystems();
    Set<TimeSystem> date_systems = ((CachedTimeSystem)date.system).
                                            getCachedTimeSystems();

    Set<TimeSystem> common = new HashSet<TimeSystem>(this_systems);
    common.retainAll(date_systems);

    if(common.size() >0) {
        /******************************************************
        * there is at least one time system in common
        * so arbitrarily pick one and compare in terms of it
        ******************************************************/
        TimeSystem sys = (TimeSystem)common.iterator().next();
        return sys.convertDate(this).compareTo(sys.convertDate(date));

    } else {
        /************************************************
        * there is nothing in common, so try everything *
        ************************************************/
        Set<TimeSystem> all_systems = new HashSet<TimeSystem>(this_systems);
        all_systems.addAll(date_systems);
        for(Iterator it = all_systems.iterator(); it.hasNext(); ) {
            TimeSystem sys = (TimeSystem)it.next();

            try {
                return sys.convertDate(this).compareTo(
                    sys.convertDate(date));

            } catch(NoSuchConversionException e) {}
        }

        /********************************
        * if we get here, it's hopeless *
        ********************************/
        throw new NoSuchConversionException(system, date.system);
    }

} // end of compareTimes method

/****************************************************************************
*
****************************************************************************/
public double secondsAfter(PreciseDate date) {

    if(date instanceof CachedDate) {
        /*******************************************************************
        * comparing two cached dates, so we'll convert both to TAI
        * Different time systems can have different length seconds
        * e.g. UT1, so we could get a different answer depending on the
        * previous history of the cache. So we need to specify a
        * particular system
        ******************************************************************/
        TAISystem TAI = TAISystem.getInstance();

        PreciseDate tai1 = TAI.convertDate(this);
        PreciseDate tai2 = TAI.convertDate(date);

        return tai1.secondsAfter(tai2);

    } else {
        /*********************************************************
        * comparing a cached date to a regular date.
        * we can toss this up to the superclass, but for
        * efficiency we want to switch the order, since
        * the superclass first trys to convert the argument to
        * this time system, and that will fail
        *********************************************************/
        return -date.secondsAfter(this);

    }



} // end of compare


/****************************************************************************
*
****************************************************************************/
public long getTime() {

    throw new UnsupportedOperationException();

} // end of getTime method

/****************************************************************************
*
****************************************************************************/
public int getNanoseconds() {

    throw new UnsupportedOperationException();

} // end of getNanoseconds method

/****************************************************************************
*
****************************************************************************/
public void setTime(long millisec) {

    throw new UnsupportedOperationException();

} // end of setTime method

/****************************************************************************
*
****************************************************************************/
public void setTime(long millisec, int nanosec) {

    throw new UnsupportedOperationException();

} // end of setTime method

/****************************************************************************
*
****************************************************************************/
public void increment(double seconds) {

    throw new UnsupportedOperationException();

} // end of increment method

/****************************************************************************
*
****************************************************************************/
protected void copyContentsFrom(PreciseDate date) {

    throw new UnsupportedOperationException();

} // end of copyContentsFrom method

/****************************************************************************
*
****************************************************************************/
public String toString() { return "Cached: "+original; }

} // end of CachedDate class
