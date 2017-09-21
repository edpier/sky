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

package eap.sky.time.barycenter;

import eap.sky.time.*;

import java.io.*;

/**************************************************************************
* Represents Barycentry Dynamical Time. This is a time system meant to
* represent the time at the Solar System barycenter. This differs from
* Terrestrial Time because of relativistic effects. The definition of TDB
* specifies that it should differ from TT only by periodic terms. That
* means the actual time at the barycenter is scaled by a constant factor
* so that on average it flows at the same rate as TT. However, this is
* an imprecise definition, because it does not specify the range of time
* over which the average should be calculated. To remedy this,
* The TCB time system was devised in 1991 to represent unscaled time at the
* barycenter. However, TDB is still widely used (e.g. for ephemerides).
* <p>
* TDB is defined in terms of TT and a model for the motion of objects
* in the SolarSystem. Several such models are available
* (see {@link TDBModel}).
**************************************************************************/
public class TDBSystem extends TimeSystem {

private static TDBSystem instance =
    new TDBSystem(FairheadTDBModel.getInstance(), TTSystem.getInstance() );

private TDBModel model;
private TTSystem TT;

private Conversion[] tt2tdb;
private Conversion[] tdb2tt;

/**************************************************************************
* Create a new TDB system which uses the given model of the solar system
* and referred to the given TT system.
* @param model a model of the motion of objects in the solar system
* @param TT The Terrestrial Time system in terms of which this TDB system
*        is defined.
**************************************************************************/
public TDBSystem(TDBModel model, TTSystem TT) {

    super("Barycentric Dynamical Time", "TDB");

    this.model = model;
    this.TT = TT;

    tt2tdb = new Conversion[1];
    tt2tdb[0] = new TTtoTDB();

    tdb2tt = new Conversion[1];
    tdb2tt[0] = new TDBtoTT();

} // end of constructor


/****************************************************************************
*
****************************************************************************/
private void writeObject(ObjectOutputStream out) throws IOException {

    out.writeObject(model);
    out.writeObject(TT);

} // end of writeObject method

/****************************************************************************
*
****************************************************************************/
private void readObject(ObjectInputStream in) throws IOException,
                                           ClassNotFoundException  {
    model = (TDBModel)in.readObject();
    TT    = (TTSystem)in.readObject();

    tt2tdb = new Conversion[1];
    tt2tdb[0] = new TTtoTDB();

    tdb2tt = new Conversion[1];
    tdb2tt[0] = new TDBtoTT();

} // end of writeObject method

/*************************************************************************
* Returns the default instance. Initially, this instance uses
* the {@link FairheadTDBModel} and the default TT system, but this may
* be changed by the user.
* @return The default istance of this class
* @see #setDefaultTDBModel
* @see #setDefaultTTSystem
*************************************************************************/
public static TDBSystem getInstance() { return instance; }

/**********************************************************************
* Sets the TDB model used by the default instance
* @param model The TDB model to be used subsequently
* @see #getInstance()
**********************************************************************/
public static void setDefaultTDBModel(TDBModel model) {

    instance = new TDBSystem(model, instance.getTTSystem());

} // end of setDefaultTDBModel method

/**********************************************************************
* Sets the TT system to be used by the default instance
* @param TT The TT system to be used subsequently
* @see #getInstance()
**********************************************************************/
public static void setDefaultTTSystem(TTSystem TT) {

    instance = new TDBSystem(instance.getTDBModel(), TT);

} // end of setDefaultTDBModel method

/*************************************************************************
* Returns the TT system to which this TDB system refers
*************************************************************************/
public TTSystem getTTSystem() { return TT; }

/*************************************************************************
* Returns the model of the Solar System used by this TDB system
*************************************************************************/
public TDBModel getTDBModel() { return model; }

/*************************************************************************
* returns a conversion from TT
*************************************************************************/
protected Conversion[] getConversionsFrom() {

    return tdb2tt;
}


/*************************************************************************
* Returns a conversion to TT
*************************************************************************/
protected Conversion[] getConversionsTo() {

    return tt2tdb;

}

/*************************************************************************
* Checks if two time systems are the same.
* @param o An object to which we will compare
* @return true if the argument is a TDBSystem which uses the same
*         TDB model and TT system.
*************************************************************************/
public boolean equals(Object o) {

    if(! (o instanceof TDBSystem)) return false;

    TDBSystem system = (TDBSystem)o;

    return model.equals(system.model) && TT.equals(system.TT);

} // end of equals method

/*************************************************************************
* Inner class implementing the conversion to TT
*************************************************************************/
private class TDBtoTT extends Conversion {

/***********************************************************
* Create a new conversion
* @param TDB the enclosing class
***********************************************************/
public TDBtoTT() { super(TDBSystem.this, TDBSystem.this.TT); }

/*************************************************************************
* Do the actual conversion.
* @param from a TDB time
* @param to a TT time
*************************************************************************/
public void convert(PreciseDate from, PreciseDate to) {

    double offset = ((TDBSystem)getFromSystem()).model.getTDBminusTT(from);

    to.setTime(from.getMilliseconds(), from.getNanoseconds());
    to.increment(offset);

} // end of convert method

} // end of TDBtoTT inner class


/*************************************************************************
* Inner class implementing the conversion from TT
*************************************************************************/
private class TTtoTDB extends Conversion {

/***********************************************************
* Create a new conversion
* @param TDB the enclosing class
***********************************************************/
public TTtoTDB() { super(TDBSystem.this.TT, TDBSystem.this); }

/*************************************************************************
* Do the actual conversion.
* @param tt a TT time
* @param tdb a TDB time
*************************************************************************/
public void convert(PreciseDate tt, PreciseDate tdb) {

  //  long start = System.currentTimeMillis();

   // System.out.println("Converting with "+model);

    /********************
    * get the TDB model *
    ********************/
    TDBModel model = ((TDBSystem)getToSystem()).model;

    /*****************************************************
    * the offsets are calculated in TDB, so we have to
    * do this iteratively. TDB and TT are so close that
    * this iteration should converge rapidly.
    * Note it's OK to compare doubles, since the same
    * TDB date should return exactly the same offset.
    ******************************************************/
    double offset = 0.0;
    double last_offset=1.0;
    int iteration=0;
    while(last_offset != offset && iteration < 5) {

   // System.out.println("    offset="+offset);

        /****************************************
        * set the TDB time to an offset from TT *
        ****************************************/
        tdb.setTime(tt.getMilliseconds(), tt.getNanoseconds());
        tdb.increment(-offset);

        /**********************************
        * update our guess for the offset *
        **********************************/
        last_offset = offset;
        offset = model.getTDBminusTT(tdb);

        ++iteration;

    } // end of iteration

//     long end = System.currentTimeMillis();
//     System.out.println("TT -> TDB took "+(end-start)+" in "+iteration);

} // end of convert method

} // end of TTtoTDB inner class

} // end of TDBSystem
