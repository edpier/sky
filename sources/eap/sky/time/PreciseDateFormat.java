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

/***************************************************************************
* Note this does not handle leapseconds or daylight time properly.
* The latter is not a problem here in Hawaii.
***************************************************************************/
public class PreciseDateFormat extends Format {

TimeSystem system;
DateFormat format;
DecimalFormat fraction_format;

/****************************************************************************
*
****************************************************************************/
public PreciseDateFormat(TimeSystem system) {

    this.system = system;

    format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    format.setTimeZone(TimeZone.getTimeZone("GMT"));

    fraction_format = new DecimalFormat(".#########");

} // end of constructor

/****************************************************************************
*
****************************************************************************/
public void setDecimals(int decimals, boolean optional) {

    if(decimals <= 0) {
        /**************
        * no decimals *
        **************/
        fraction_format = null;

    } else {
        /******************************
        * construct the format string *
        ******************************/
        StringBuilder s = new StringBuilder();
        s.append(".");
        if(optional) for(int i=0; i<decimals; ++i) s.append("#");
        else         for(int i=0; i<decimals; ++i) s.append("0");

        fraction_format = new DecimalFormat(s.toString());
    }


   // fraction_format.setDecimalSeparatorAlwaysShown(decimals != 0);

} // end of setDecimals method

/****************************************************************************
*
****************************************************************************/
protected String formatWholeSeconds(PreciseDate date) {

    long milli = date.getMilliseconds();
    return format.format(new Date(milli));


} // end of formatWholeSeconds method

/****************************************************************************
*
****************************************************************************/
public StringBuffer format(Object o, StringBuffer buffer, FieldPosition pos) {

    PreciseDate date = system.convertDate((PreciseDate)o);

    long milli = date.getMilliseconds();
    int nano = date.getNanoseconds();

    /***************************************
    * Format the whole seconds.
    * The standard Java DateFormat ignores
    * fractions of a second.
    ***************************************/
    buffer.append(formatWholeSeconds(date));

    /********************************
    * format the fractional seconds 
    ********************************/
    if(fraction_format != null) {
        milli = milli%1000;

        double fraction = milli/1000.0 + nano/1000000000.0;
        String formatted_fraction = fraction_format.format(fraction);
        if(!formatted_fraction.equals(".0")) {
            buffer.append(fraction_format.format(fraction));
        }
    }

    /*************************************
    * append the name of the time system *
    *************************************/
    buffer.append(" ");
    buffer.append(system.getAbbreviation());

    return buffer;

} // end of format method

/****************************************************************************
*
****************************************************************************/
public PreciseDate parsePreciseDate(String source) throws ParseException {

    return (PreciseDate)parseObject(source);

} // end of parsePreciseDate method

/****************************************************************************
*
****************************************************************************/
protected PreciseDate parseWholeSeconds(String source, ParsePosition pos) {

    Date date = (Date)format.parseObject(source, pos);
    if(date == null) return null;

    /************************
    * put together the date *
    ************************/
    PreciseDate precise = system.createDate();
    precise.setTime(date.getTime(), 0);

    return precise;


} // end of parseWholeSeconds

/****************************************************************************
*
****************************************************************************/
public Object parseObject(String source, ParsePosition pos) {

    int index0 = pos.getIndex();

    /*****************************
    * parse the bulk of the date *
    *****************************/
    PreciseDate precise = parseWholeSeconds(source, pos);
    if(pos.getIndex() == index0) {
        return null;
    }

    /*********************
    * parse the fraction *
    *********************/
    double fraction = 0.0;
    String fraction_string = source.substring(pos.getIndex());

    if(fraction_string.startsWith(".") ) {

        if(!Character.isDigit(fraction_string.charAt(1))) {
            /**********************
            * naked decimal point *
            **********************/
            pos.setIndex(pos.getIndex()+1);

        } else {
            /******************************************************
            * there should be a fraction here, so try to parse it *
            ******************************************************/
            int index = pos.getIndex();
            fraction = ((Number)fraction_format.parseObject(source, pos))
                                               .doubleValue();

            /*******************
            * check for errors *
            *******************/
            if(pos.getIndex() == index) {

                pos.setIndex(index0);
                return null;
            }
        } // end if there is a fraction to parse
    }

    /**********************
    * add in the fraction *
    **********************/
    precise.increment(fraction);

    /*************************************
    * parse the time system abbreviation *
    *************************************/
    String abbr = system.getAbbreviation();
    if(!source.substring(pos.getIndex()).trim().startsWith(abbr)) {
        pos.setErrorIndex(pos.getIndex());
        pos.setIndex(index0);
        return null;
    }

    pos.setIndex(pos.getIndex() + 1 + abbr.length());


    return precise;

} // end of parseObject method

} // end of PreciseDateFormat class
