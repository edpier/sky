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

/**************************************************************************
* One of the implementations of Terrestrial Time where the offset
* from TAI is not constant.
**************************************************************************/
public class BIPMTTSystem extends TTSystem {

private TTTable table;

private Conversion[] tt2tai;
private Conversion[] tai2tt;

public BIPMTTSystem(TTTable table) {
    super(table.getAbbreviation());

    this.table = table;

    tt2tai = new Conversion[1];
    tt2tai[0] = new TTtoTAI(this);

    tai2tt = new Conversion[1];
    tai2tt[0] = new TAItoTT(this);


} // end of constructor


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
* Inner class to implement the conversion from TT to TAI.
****************************************************************************/
private class TTtoTAI extends Conversion {

/****************************************************************************
* Creates a new conversion.
****************************************************************************/
public TTtoTAI(BIPMTTSystem tt)  {
    super(tt, TAISystem.getInstance());
}

/**************************************************************************
* Does the conversion.
*************************************************************************/
public void convert(PreciseDate from, PreciseDate to) {

    table.convertTTtoTAI(from, to);

} // end of convert method

} // end if TTtoTAI inner class

/****************************************************************************
* Inner class to implement the conversion from TAI to TT
****************************************************************************/
private class TAItoTT extends Conversion {

/****************************************************************************
* Creates a new conversion.
****************************************************************************/
public TAItoTT(BIPMTTSystem tt) {
    super(TAISystem.getInstance(), tt);
}

/****************************************************************************
* Does the conversion.
****************************************************************************/
public void convert(PreciseDate from, PreciseDate to) {

    table.convertTAItoTT(from,to);

} // end of convert method

} // end if TTtoTAI inner class

} // end of BIPMTTSystem class
