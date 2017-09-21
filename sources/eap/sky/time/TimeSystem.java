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

/******************************************************************************
* Represents a particular way of telling time. This is an abstract class.
* Subclasses implement individual time systems. All time systems in this
* package implement a static getInstance() method, which returns an instance of
* that time system.
******************************************************************************/
public abstract class TimeSystem implements Serializable {

private String name;
private String abbreviation;

private Map<TimeSystem, Conversion> conversions;


/******************************************************************************
* Create a new time system with the given name.
* @param name The full name of the time system (e.g. "International Atomic Time")
* @param abbreviation The standard abbreviation for the time system (e.g. "TAI")
******************************************************************************/
protected TimeSystem(String name, String abbreviation) {

    this.name = name;
    this.abbreviation = abbreviation;

    /****************************************
    * create the cache for conversion paths *
    ****************************************/
    conversions = new HashMap<TimeSystem, Conversion>();

} // end of constructor

/*****************************************************************************
* Returns the full name of the system (e.g. "International Atomic Time")
* @return the name of the system.
*****************************************************************************/
public String getName() { return name; }

/*****************************************************************************
* Returns the standard abbreviation for the system (e.g. "TAI").
* @return the abbreviation for the system.
*****************************************************************************/
public String getAbbreviation() { return abbreviation; }

/****************************************************************************
*
****************************************************************************/
private void writeObject(ObjectOutputStream out) throws IOException {

    boolean named = (name != null && abbreviation != null);
    out.writeBoolean(named);
    if(named) {
        out.writeUTF(name);
        out.writeUTF(abbreviation);
    }

} // end of writeObject method

/****************************************************************************
*
****************************************************************************/
private void readObject(ObjectInputStream in) throws IOException,
                                           ClassNotFoundException  {
    boolean named = in.readBoolean();
    if(named) {
        name         = in.readUTF();
        abbreviation = in.readUTF();
    } else {
        name = null;
        abbreviation = null;
    }

    conversions = new HashMap<TimeSystem, Conversion>();

} // end of writeObject method

/*****************************************************************************
* Creates a new PreciseDate object for representing an instant in this
* time system. Subclasses should override this if they require a subclass of
* PreciseDate to represent an instant in time.
* @return a new date object.
*****************************************************************************/
public PreciseDate createDate() {

    return new PreciseDate(this);

} // end of createDate method


/****************************************************************************
* Forces a date to be in this time system.
* @param date a date
* @return the date argument if it is in this time system, or a new date
*         object in this time system which refers to the same instant in time.
*         If the argument is null, this method returns null.
****************************************************************************/
public PreciseDate convertDate(PreciseDate date) {

    if(date == null) return null;
    if(date.getTimeSystem().equals(this)) return date;

    PreciseDate converted = createDate();
    converted.setTime(date);

    return converted;

} // end of convertDate method

/******************************************************************************
*
******************************************************************************/
public PreciseDateFormat createFormat() { return new PreciseDateFormat(this); }


/*****************************************************************************
* Returns an array of all the conversions from this time system to
* another which this time system implements. Each time system may define a
* number of conversions to and from that time system. In general every conversion
* from this system should have a corresponding conversion to this system.
* A given conversion should only be defined by one time system. For instance if
* UTCSystem defines a conversion from UTC to TAI, TAISystem should not also
* define UTC to TAI. You don't have to define every possible conversion.
* The {@link #getConversionTo(TimeSystem)} method is able to
* combine conversions (e.g. UT1 to UTC to TAI) if needed.
* @return an array of time conversions for which
*         {@link Conversion#getFromSystem()}.equals(this) == true}
* @see #getConversionsTo()
* @see CompositeConversion
*****************************************************************************/
protected abstract Conversion[] getConversionsFrom();


/*****************************************************************************
* Returns an array of all the conversions to this time system from
* another which this time system implements.
* @see #getConversionsFrom()
* @return an array of time conversions for which
*         {@link Conversion#getToSystem()}.equals(this) == true}
*****************************************************************************/
protected abstract Conversion[] getConversionsTo();

/*****************************************************************************
* Returns the conversion from one time system to another. This method searches
* all the conversions defined by various time systems by their
* {@link #getConversionsFrom()} and {@link #getConversionsTo()} methods and
* attempts to find some combination of these conversions to get from
* this system
* to the given one. Once a conversion is found, it is cached to optimize for
* multiple conversions between two time systems. This method is called by
* {@link PreciseDate#setTime(PreciseDate)}, and the general user probably
* does not need to call it directly.
* @param to The time system to convert to.
* @throws NoSuchConversionException if there is no way to convert from one
*         system to another, even by converting to intermediate systems first.
* @return The conversion from this system to the given one.
*****************************************************************************/
public Conversion getConversionTo(TimeSystem to) {

    Conversion conv = (Conversion)conversions.get(to);
    if(conv == null) {
        conv = findConversionTo(to);
        conversions.put(to, conv);
    }

    return conv;

} // end of getConversionTo method

/****************************************************************************
* Searches available conversions to see if they can be combined to give
* the desired conversion. This method does not cache the found conversions.
* @param to The time system to convert to.
* @throws NoSuchConversionException if there is no way to convert from one
*         system to another, even by converting intermediate systems first.
* @return The conversion from this system to the given one.
* @see #getConversionTo(TimeSystem)
****************************************************************************/
protected Conversion findConversionTo(TimeSystem to) {

//System.out.println("finding conversion "+this+" -> "+to);

    /**************************************************
    * See if a conversion from this system will do it *
    **************************************************/
    Conversion[] prefixes = getConversionsFrom();
    for(int i=0; i< prefixes.length; ++i) {
        Conversion prefix = prefixes[i];

     //   System.out.println("prefix="+prefix);

        if(prefix.getToSystem().equals(to)) return prefix;

    }

    /*****************************************************
    * See if a conversion to the other system will do it *
    *****************************************************/
    Conversion[] suffixes = to.getConversionsTo();
    for(int i=0; i< suffixes.length; ++i) {
        Conversion suffix = suffixes[i];

     //   System.out.println("    trying suffix "+suffix);

        if(suffix.getFromSystem().equals(this)) return suffix;

    }

    /*****************************************************
    * see if a combination of two conversions will do it *
    *****************************************************/
    for(int i=0; i< prefixes.length; ++i) {
        Conversion prefix = prefixes[i];

        for(int j=0; j< suffixes.length; ++j) {
            Conversion suffix = suffixes[j];

          //  System.out.println("prefix="+prefix+" suffix="+suffix);

            if(prefix.getToSystem().equals(suffix.getFromSystem())) {
                /*************
                * we got one *
                *************/
                return new CompositeConversion(prefix, suffix);
            }
        } // end of loop over suffixes
    } // end of loop over prefixes

    /************************************************************
    * see if we can find a conversion to bridge the gap between
    * a prefix and a suffix
    ************************************************************/
    Conversion best_bridge = null;
    Conversion best_prefix = null;
    Conversion best_suffix = null;

    /********************************************
    * first try to bridge the end of the prefix
    * to the final system
    ********************************************/
    for(int i=0; i< prefixes.length; ++i) {
        Conversion prefix = prefixes[i];

        /*****************************************************
        * get a conversion which bridges the prefix and "to" *
        *****************************************************/
        Conversion bridge = null;
        try {
            bridge = prefix.getToSystem().findConversionTo(to);
        } catch(NoSuchConversionException e) { continue; }

        /*****************************************
        * see if this is the best bridge yet *
        *************************************/
        if(best_bridge == null ||
        bridge.getSteps() < best_bridge.getSteps() ) {
            /*****************************
            * this is the best batch yet *
            *****************************/
            best_bridge = bridge;
            best_prefix = prefix;
            best_suffix = null;
        }

    } // end of loop over prefixes

    /***************************************************
    * then try to bridge this system to the beginning
    * of the suffix
    ***************************************************/
    for(int i=0; i< suffixes.length; ++i) {
        Conversion suffix = suffixes[i];

        /*****************************************************
        * get a conversion which bridges the prefix and "to" *
        *****************************************************/
        Conversion bridge = null;
        try {
            bridge = this.findConversionTo(suffix.getFromSystem());
        } catch(NoSuchConversionException e) { continue; }

        /*****************************************
        * see if this is the best bridge yet *
        *************************************/
        if(best_bridge == null ||
        bridge.getSteps() < best_bridge.getSteps() ) {
            /*****************************
            * this is the best batch yet *
            *****************************/
            best_bridge = bridge;
            best_prefix = null;
            best_suffix = suffix;
        }

    } // end of loop over prefixes

    /*****************************
    * check if we found anything *
    *****************************/
    if(best_bridge == null) {
        /****************************
        * can't get there from here *
        ****************************/
        throw new NoSuchConversionException(this, to);
    }

    /******************************
    * combine everything together *
    ******************************/
    if(best_prefix==null) {
        /*****************
        * bridge, suffix *
        *****************/
        return new CompositeConversion(best_bridge, best_suffix);
    } else if(best_suffix == null) {
        /****************
        * prefix, bridge *
        *****************/
        return new CompositeConversion(best_prefix, best_bridge);
    } else {
        /*************************
        * prefix, bridge, suffix *
        *************************/
        return new CompositeConversion(best_prefix,
               new CompositeConversion(best_bridge, best_suffix));
    }


} // end of findConversionTo method


/*****************************************************************************
* Checks if two time systems are the same. This method returns true if the
* two objects are identical. So it is appropriate for time systems which
* can only have one object in existence (i.e. their constructors are not
* public, and they have a getInstance() method which always returns the same
* object. However, subclasses are not required to behave this way. If a
* subclass can have more than one instance, than this method needs to be
* overridden. You should always use this method instead of the == operator
* to check the equality of two time systems.
* @return this == o
*****************************************************************************/
public boolean equals(Object o) {

    return this == o;

} // end of equals method

/*****************************************************************************
* Returns the standard abreviation for the time system name.
*****************************************************************************/
public String toString() { return abbreviation; }



} // end of TimeSyatem class
