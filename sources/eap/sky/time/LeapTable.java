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
* Implements a list of leap seconds for converting between TAI and UTC
* (see {@link UTCSystem}).
* <p>
* Leap seconds can be either negative or positive. For a negative leap second,
* 23:59:58 is followed immediately by 00:00:00. For a positive leap second,
* 23:59:59 is followed by 23:59:60, and then 00:00:00.
* So far, all leap seconds have been positive, because the Earth has taken
* more than
* 86400 seconds to rotate since the inception of UTC. Tides from the Sun
* and Moon slow the Earth's rotation, so it is likely that leap seconds will
* always be positive. However, movement in the Earth's core can cause temporary
* speed-ups over a decadal time scale, so negative leap seconds are not
* impossible.
* <p>
* This class does not check that leap seconds occur in the last minute of a month,
* as specified in the standard.
* <p>
* Note that you should not hard-code a list of leap seconds, since
* a new leap second would render such code obsolete. Instead you should maintain
* The list of leapseconds externally, so that it may be updated in a reliable
* way. This package includes subclasses for reading leap second
* lists from a file or over a network.
* <p>
* This class is thread safe to the extent that multiple threads may access
* the list
* at the same time. By default it is not safe to add a leap
* second while you are accessing the list. However,
* {@link #LeapTable(boolean)} provides an option to synchronize the
* underlying storage and prevent a
* {@link ConcurrentModificationException}. However,
* a new leap second is not added to the internal UTC and TAI lists
* simultaneously.
* This is not a problem as long as the access is occuring for times
* earlier than the new leap second. If this is not the case,
* then you should do your own synchronization.
* @see UTCSystem
* @see UTCDate
****************************************************************************/
public class LeapTable implements Serializable {

private static final TimeSystem TAI = TAISystem.getInstance();


LeapList plus_utc_list;
LeapList plus_tai_list;

LeapList minus_utc_list;
LeapList minus_tai_list;

UTCSystem system;

/***************************************************************************
* Creates a new empty leap second list. The underlying storage classes
* will be unsynchronized, so if you access the list while adding a leap
* second, you could get a {@link ConcurrentModificationException}
***************************************************************************/
public LeapTable() {
    this(false);
}


/***************************************************************************
* Creates a new empty, possibly synchronized leap second list.
* @param synched If true the underlying storage classes will be synchronized.
***************************************************************************/
public LeapTable(boolean synched) {

    system = new UTCSystem(this);

    plus_utc_list = new LeapList(synched);
    plus_tai_list = new LeapList(synched);

    minus_utc_list = new LeapList(synched);
    minus_tai_list = new LeapList(synched);

} // end of constructor

/*****************************************************************************
* Checks if a date is UTC, TAI or something else. This is a utility method
* for internal type checking.
* @param date the date to check
* @return true if the given date is in UTC and uses this leaptable, or false
*         if the given date is TAI.
* @throws IllegalArgumentException if the date is not UTC or TAI or if it uses
*         a different leap second table.
*****************************************************************************/
private boolean isUTC(PreciseDate date) {

    if(date instanceof UTCDate) {
        /********************************************
        * it's UTC, but does it use this leaptable? *
        ********************************************/
        if(!date.getTimeSystem().equals(system)) {

            throw new IllegalArgumentException(date+
                                               " uses a different LeapTable");
        }
        return true;

    } else {
        /************************************************
        * if it's not UTC, then we require it to be TAI *
        ************************************************/
        if(!date.getTimeSystem().equals(TAI)) {
            throw new IllegalArgumentException(date+" is not UTC or TAI");
        }
        return false;
    }

} // end of isUTC method


/*****************************************************************************
* Adds a positive leap second to the list. This is the same as calling
* addLeapSecond(date, true).
* @param date the start of the leap second expressed in UTC or TAI
* @throws IllegalArgumentException if leapsec argument is not an integral
*         number of seconds, or if the date does not come after all the
*         dates already in the list, or if the date is in UTC and
*         {@link UTCDate#isLeapSecond()} returns false.
* @see #addLeapSecond(PreciseDate, boolean)
*****************************************************************************/
public void addLeapSecond(PreciseDate date) {

    addLeapSecond(date, true);

}

/*****************************************************************************
* Add a positive or negative leap second to the list.
* For a positive leap second this should refer to the start of the
* leap second itself (i.e. 23:59:60.0 UTC). For negative leap seconds, it
* should refer to the time immediately following the leapsecond,
* (i.e. 00:00:00.0 UTC).
* Note that it is possible to specify the skipped second using a
* {@link UTCDate}, but that this date would be invalid, and there would be
* no corresponding date in TAI.
* @param leapsec the date of the leap second expressed in UTC or TAI.
* @param plus if true the leap second will be treated as positive,
*        otherwise it will be treated as negative
* @throws IllegalArgumentException if leapsec argument is not an integral
*         number of seconds, or if the date does not come after all the
*         dates already in the list, or if the date is in UTC, and
*         {@link UTCDate#isLeapSecond()} is not consistent with the plus
*         argument.
*****************************************************************************/
public synchronized void addLeapSecond(PreciseDate leapsec, boolean plus) {


    /*********************************************************
    * make sure the time points to the beginning of a second *
    *********************************************************/
    if(leapsec.getNanoseconds() != 0 ||
    leapsec.getMilliseconds() % 1000 != 0 ) {

        throw new IllegalArgumentException(leapsec+
                                    " is not the beginning of a second ");
    }

    /******************************************************
    * determine whether to add to the plus or minus lists *
    ******************************************************/
    LeapList utc_list = null;
    LeapList tai_list = null;
    if(plus) {
        /*******
        * plus *
        *******/
        utc_list = plus_utc_list;
        tai_list = plus_tai_list;
    } else {
        /********
        * minus *
        ********/
        utc_list = minus_utc_list;
        tai_list = minus_tai_list;
    }

    /**************************************
    * treat UTC and TAI dates differently *
    **************************************/
    if(isUTC(leapsec)) {
        /******************************************
        * leapsec specified in UTC
        * make sure it's using the same leaptable *
        ******************************************/
        UTCDate utc = (UTCDate)leapsec;

        /*******************************************************
        * for positive leap seconds, the leapsec flag must be
        * set in the UTCDate
        *******************************************************/
        if(plus && !utc.isLeapSecond()) {
            throw new IllegalArgumentException(utc+" is not a leap second ");
        } else if(!plus && utc.isLeapSecond()) {
            throw new IllegalArgumentException(utc+" is a positive leap second");
        }

        /*****************************************************
        * add the utc date to the leapsecond list
        * note we have to do this before converting to TAI
        *****************************************************/
        utc_list.add(utc);

        /**********************************
        * find the corresponding TAI date
        * and add it tot he list
        **********************************/
        PreciseDate tai = TAI.createDate();
        tai.setTime(utc);

        tai_list.add(tai);

    } else {
        /*****************************************************
        * The leapsec is in TAI, so add it to the TAI list
        * add the TAI leap second to the list.
        * Note we have to do this before converting to UTC
        ****************************************************/
        tai_list.add(leapsec);

        /**********************************
        * find the corresponding TAI date
        * and add it to the list
        **********************************/
        PreciseDate utc = system.createDate();
        utc.setTime(leapsec);

        utc_list.add(utc);

    } // end if it's a TAI leapsec


} // end of addLeapSecond method in UTC


/***************************************************************************
* Returns the number of positive leap seconds on or before the given date.
* @param date A date expressed in TAI or UTC
***************************************************************************/
public int positiveLeapSecondsOnOrBefore(PreciseDate date) {

    if(isUTC(date)) return plus_utc_list.findNextLeapsecAfter(date);
    else            return plus_tai_list.findNextLeapsecAfter(date);


} // end of nagativeLeapSecondsOnOrBefore method for UTC

/***************************************************************************
* Returns the number of negative leap seconds on or before the given date.
* @param date A date expressed in TAI or UTC
***************************************************************************/
public int negativeLeapSecondsOnOrBefore(PreciseDate date) {

    if(isUTC(date)) return minus_utc_list.findNextLeapsecAfter(date);
    else            return minus_tai_list.findNextLeapsecAfter(date);

} // end of nagativeLeapSecondsOnOrBefore method for UTC


/*****************************************************************************
* Returns {@link #positiveLeapSecondsOnOrBefore(PreciseDate)} minus
*         {@link #negativeLeapSecondsOnOrBefore(PreciseDate)}. This
* value may be used to convert between UTC and TAI.
* @param date A date expressed in TAI or UTC
*****************************************************************************/
public int leapSecondsOnOrBefore(PreciseDate date) {

    return positiveLeapSecondsOnOrBefore(date) -
           negativeLeapSecondsOnOrBefore(date);


} // end of leapSecondsOnOrBefore method

/*****************************************************************************
* Determines if the given instant occurs during a positive leap second.
* @param tai An instant in TAI format.
* @return true if the given instant occurs during a positive leapsec, and
*         false otherwise
*****************************************************************************/
public boolean isLeapSecond(PreciseDate tai) {

    if(isUTC(tai)) throw new IllegalArgumentException(tai+" is not in TAI");

    return plus_tai_list.contains(tai);

} // end of isLeapSecond method

/***************************************************************************
* Checks if the given date is a valid UTC date, given the leapseconds in
* this table. This method is called by {@link UTCDate#isValid()}.
* @param utc A UTC date.
* @return false if the date occurs during a negative leap second, or if
*         {@link UTCDate#isLeapSecond()} returns true, but the date does not
*         occur during one of the positive leap seconds in this table.
***************************************************************************/
public boolean isValid(UTCDate utc) {

    /***************************************
    * make sure it uses the same leaptable *
    ***************************************/
    isUTC(utc);

    if(utc.isLeapSecond() ) {
        /*********************************************************
        * the date is marked as a positive leap second, so
        * check if it matches any of the positive dates in this
        * table
        *********************************************************/
//         System.out.println("is leap second");
//         System.out.println(utc);
//         System.out.println(plus_utc_list.get(plus_utc_list.list.size()-1));
        if(! plus_utc_list.contains(utc)) return false;
    }

          ///  System.out.println("about to check negative");

    /*************************************************************
    * make sure the leap second is not in the list of negative
    * leap seconds, since these seconds are skipped over.
    * Note the negative leapsec list holds the times immediately
    * following the negative leap second.
    ************************************************************/
    UTCDate following = (UTCDate)system.createDate();
    following.setTime(utc.getMilliseconds()+1000, 0, false);
    return !minus_utc_list.contains(following);

} // end of isValid method



/***************************************************************************
* A utility class for maintaining a list of leap seconds.
* It is optmized for multiple searches for similar values.
***************************************************************************/
private class LeapList implements Serializable {

int index;
List<PreciseDate> list;

/**************************************************************************
* Create a new empty list
**************************************************************************/
public LeapList(boolean synched) {
    list = new ArrayList<PreciseDate>();
    if(synched) list = Collections.synchronizedList(list);
    index = 0;
}

/**************************************************************************
* add a leap second to the list.
* @throws IllegalArgumentException if the leap second does not come
* after all the leap seconds already in the list
**************************************************************************/
public void add(PreciseDate leapsec) {

    /******************************************************************
    * compare the new leap second to the last one already in the list *
    ******************************************************************/
    if(list.size()>0) {
        PreciseDate last = (PreciseDate)list.get(list.size()-1);

        /*************************************************
        * make sure the dates are sorted chronologically *
        *************************************************/
        if(leapsec.compareTo(last) <= 0) {
            throw new IllegalArgumentException(leapsec+
                                               " does not come after "+
                                               last);
        }
    }


    /**********************************
    * add the leap second to the list *
    **********************************/
    list.add(leapsec);



} // end of add method

/**************************************************************************
* Returns the index of the leap second in the list which occurs
* immediately after the given instant. If there is no such element, then
* this method returns the number of elements in the list.
* Note that this gives a convenient way to count the number of leap seconds
* on or before a given time.
* This method is otimized for sucessive searches for similar values.
* @param date The date to search for. This should use the same time system
*        as the leap seconds in this list.
* @return The index of the following leap second.
**************************************************************************/
public int findNextLeapsecAfter(PreciseDate date) {

    if(list.size() == 0 ) return 0;

    /*********************************************
    * get the saved index. We do this to allow
    * muli-threaded use of this method.
    ********************************************/
    int index = this.index;

    /*********************************************************************
    * find the index of the first leap second in the list which occurs
    * after the given date
    ********************************************************************/
    while(index >0 &&
          date.compareTo((PreciseDate)list.get(index-1)) < 0 ) --index;

    while(index < list.size() &&
          date.compareTo((PreciseDate)list.get(index)) >= 0 ) ++index;
// System.out.println();
//           System.out.println(new Date(date.getMilliseconds()));
//           System.out.println(date);
// 
//           PreciseDate last = (PreciseDate)list.get(list.size()-1);
//           System.out.println(list.get(list.size()-1));
//           System.out.println(new Date(last.getMilliseconds()));
//           System.out.println("index="+index+" size="+list.size());

    /****************************************************
    * save the search index to optimize the next search *
    ****************************************************/
    this.index = index;

   // System.out.println("index="+index);

    return index;

} // end if findNextLeapsecAfter method

/**************************************************************************
* Returns the leap second at a given index.
**************************************************************************/
public PreciseDate get(int index) { return (PreciseDate)list.get(index); }

/****************************************************************************
* returns true if the given instant occurs duraing a leap second in this
* list. Note that the instant does not need to be an integral second.
****************************************************************************/
private boolean contains(PreciseDate date) {

    /******************************************************************
    * Find the index of our best candidate for a leap second matching
    * the argument. If this is -1, then there is no such candiate
    *****************************************************************/
    int index = findNextLeapsecAfter(date) -1;
    if(index == -1) return false;

    /***********************************************
    * compare the candiate leapsec to the argument
    * to see if they fall within the same second
    ***********************************************/
    PreciseDate leapsec = get(index);
    long diff = date.getMilliseconds() - leapsec.getMilliseconds();

    return diff < 1000 && diff >= 0;

} // end of contains method

} // end of LeapList inner class





} // end of LeapTable class
