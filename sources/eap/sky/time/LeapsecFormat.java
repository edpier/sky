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

import java.util.*;
import java.text.*;

/*************************************************************************
*
*************************************************************************/
public class LeapsecFormat extends PreciseDateFormat {


/****************************************************************************
*
****************************************************************************/
public LeapsecFormat(TimeSystem system) {

    super(system);

} // end of constructor

/****************************************************************************
*
****************************************************************************/
protected String formatWholeSeconds(PreciseDate date) {

    UTCDate utc = (UTCDate)date;

    String string = super.formatWholeSeconds(date);
    if(utc.isLeapSecond()) {
        if(!string.endsWith("59")) {
            throw new IllegalArgumentException("Leap second is not the last "+
                                               "second of a minute");
        }

        string = string.substring(0, string.length()-2)+"60";

    } // end if this is a leapsecond

    return string;

} // end of formatWholeSeconds method

/****************************************************************************
*
****************************************************************************/
protected PreciseDate parseWholeSeconds(String source, ParsePosition pos) {

    /************************
    * find the second colon *
    ************************/
    int colon = source.indexOf(":", pos.getIndex());
    colon = source.indexOf(":", colon);

    String sec = source.substring(pos.getIndex(), colon+3);
    boolean leapsec = sec.equals("60");
    if(leapsec) {
        /************************
        * this is a leap second *
        ************************/
        source = source.substring(0, colon+1) + "59"+
                 source.substring(colon+3);

    }

    Date date = (Date)format.parseObject(source, pos);
    if(date == null) return null;

    /************************
    * put together the date *
    ************************/
    UTCDate utc = (UTCDate)system.createDate();
    utc.setTime(date.getTime(), 0, leapsec);

    return utc;


} // end of parseWholeSeconds

} // end of LeapsecFormat class