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

/***************************************************************************
* The most common realization of Terrestrial Time, with a constant offset from
* TAI. The offset is {@link TTSystem#TAI_OFFSET}.
***************************************************************************/
public class PlainTTSystem extends TTSystem {

private static final TTSystem instance = new PlainTTSystem();

private static final Conversion[] tt2tai = {new TTtoTAI()};
private static final Conversion[] tai2tt = {new TAItoTT()};

/****************************************************************************
* Create a new instance of TT. This method is private because we only need one
* instance of this class. Use {@link #getInstance() instead.
****************************************************************************/
private PlainTTSystem() {

    super("TT");
}

/****************************************************************************
* Returns the one and only instance of this class.
* @return The instance of this class.
****************************************************************************/
public static final TTSystem getInstance() { return instance; }

/****************************************************************************
* Returns a conversion from TAI to TT.
* @return A conversion from TAI to TT.
****************************************************************************/
public Conversion[] getConversionsTo() { return tai2tt; }

/****************************************************************************
* Returns a conversion from TT to TAI.
* @return A conversion from TT to TAI.
****************************************************************************/
public Conversion[] getConversionsFrom() { return tt2tai; }


/****************************************************************************
* Inner class to implement the conversion from TT to TAI
****************************************************************************/
private static class TTtoTAI extends Conversion {

/****************************************************************************
* Creates a new conversion.
****************************************************************************/
public TTtoTAI()  {
    super(instance, TAISystem.getInstance());
}

/**************************************************************************
* Does the conversion.
*************************************************************************/
public void convert(PreciseDate from, PreciseDate to) {


    to.setTime(from.getMilliseconds(), from.getNanoseconds());
    to.increment(-TAI_OFFSET);

} // end of convert method

} // end if TTtoTAI inner class

/****************************************************************************
* Inner class to implement the conversion from TAI to TT
****************************************************************************/
private static class TAItoTT extends Conversion {

/****************************************************************************
* Creates a new conversion.
****************************************************************************/
public TAItoTT() {
    super(TAISystem.getInstance(), instance);
}

/****************************************************************************
* Does the conversion.
****************************************************************************/
public void convert(PreciseDate from, PreciseDate to) {


    to.setTime(from.getMilliseconds(), from.getNanoseconds());
    to.increment(TAI_OFFSET);

} // end of convert method

} // end if TTtoTAI inner class


} // end of TTSystem class
