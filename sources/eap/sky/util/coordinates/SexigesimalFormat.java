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

package eap.sky.util.coordinates;

import java.util.*;
import java.text.*;

/****************************************************************************
* A formatter and parser for sexigesimal (base 60) notation.
* You can use this for the degrees, minutes, seconds, and hours, minutes,
* seconds notations used with Right Ascension and Declination.
****************************************************************************/
public class SexigesimalFormat extends NumberFormat {

/** Degrees, minutes, seconds, and fractional seconds **/
public static final SexigesimalFormat DMSF
                            = new SexigesimalFormat(1.0, 'd', '\'', '"', 6);

/** Hours, minutes, seconds, and fractional seconds **/
public static final SexigesimalFormat HMSF
                            = new SexigesimalFormat(15.0, 'h', 'm', 's', 6);

/** Degrees, minutes, and whole seconds **/
public static final SexigesimalFormat DMS
                            = new SexigesimalFormat(1.0, 'd', '\'', '"', 0);

/** Hours, minutes, and whole seconds **/
public static final SexigesimalFormat HMS
                            = new SexigesimalFormat(15.0, 'h', 'm', 's', 0);

/** Hours, minutes, and whole seconds **/
public static final SexigesimalFormat TIME
                            = new SexigesimalFormat(1.0, ':', ':', '\0', 0,
                                                    false);
private static final int UNDEFINED = -1;
public  static final int WHOLE     =  0;
public  static final int MINUTES   =  1;
public  static final int SECONDS   =  2;

NumberFormat int_format;
NumberFormat fraction_format;

double scale;
char whole_sep;
char minutes_sep;
char seconds_sep;

int decimals;

boolean show_zeros;
boolean default_high;
boolean space_sep;

/****************************************************************************
* Create a new format.
* @param scale The scale factor by which to divide the number before
*        representing it. This typoically used to convert degrees to hours
*        of Right Ascension by setting this to 15.0
* @param whole_sep The character which will follow the whole number. This
*        is typically 'd' for degrees or 'h' for hours.
* @param minutes_sep The character to follow the first 1/60th. This is typically
*        '\'' for minutes of arc, and "m" for minutes of time.
* @param seconds_sep The character to follow the second 1/60th. This is
*        typically '"' for seconds of arc, and "s" for seconds of time.
* @param decimals The number of decimal places to display for fractions of
*        1/3600.
****************************************************************************/
public SexigesimalFormat(double scale, char whole_sep, char minutes_sep,
                                       char seconds_sep, int decimals,
                                       boolean space_sep) {

    this.scale = scale;
    this.whole_sep = whole_sep;
    this.minutes_sep = minutes_sep;
    this.seconds_sep = seconds_sep;
    this.decimals = decimals;
    this.space_sep = space_sep;

    int_format = new DecimalFormat("00");
    fraction_format = new DecimalFormat(".######");
    fraction_format.setMaximumFractionDigits(decimals);
    fraction_format.setMinimumFractionDigits(decimals);

    show_zeros = true;
    default_high = true;

} // end of constructor

/****************************************************************************
* Create a new format.
* @param scale The scale factor by which to divide the number before
*        representing it. This typoically used to convert degrees to hours
*        of Right Ascension by setting this to 15.0
* @param whole_sep The character which will follow the whole number. This
*        is typically 'd' for degrees or 'h' for hours.
* @param minutes_sep The character to follow the first 1/60th. This is typically
*        '\'' for minutes of arc, and "m" for minutes of time.
* @param seconds_sep The character to follow the second 1/60th. This is
*        typically '"' for seconds of arc, and "s" for seconds of time.
* @param decimals The number of decimal places to display for fractions of
*        1/3600.
****************************************************************************/
public SexigesimalFormat(double scale, char whole_sep, char minutes_sep,
                                       char seconds_sep, int decimals) {

    this(scale, whole_sep, minutes_sep, seconds_sep, decimals, true);

} // end of default constructor

/************************************************************************
*
************************************************************************/
public void setShowZeros(boolean show_zeros) {

    this.show_zeros = show_zeros;
}

/************************************************************************
*
************************************************************************/
public void setDefaultHigh(boolean default_high) {

    this.default_high = default_high;

} // end of setDefaulHigh method

/****************************************************************************
* Format a number. This implements the format, but you are more likely to
* use {@link #format(double)}.
* @param number the number to format.
* @param buffer A string buffer to hld the formatted result.
* @param pos This is ignored.
* @return the buffer.
****************************************************************************/
public StringBuffer format(double number, StringBuffer buffer,
                           FieldPosition pos) {

    boolean negative = (number < 0.0);
    number = Math.abs(number/scale);



    int degrees = (int)number;
    number -= degrees;
    number *= 60.0;

    int minutes = (int)number;
    number -= minutes;
    number *= 60.0;

    int seconds = (int)number;
    double fraction = number - seconds;

    /********************************************
    * we need to handle the case where rounding
    * the fraction makes it equal to 1.0
    ********************************************/
    if(fraction_format.format(fraction).startsWith("1")) {
        ++seconds;
        fraction=0.0;

        if(seconds == 60) {
            seconds = 0;
            ++minutes;
            if(minutes == 60) {
                minutes = 0;
                ++degrees;
            }
        }
    }

    /*******
    * sign *
    *******/
    if(negative) buffer.append("-");

    /********
    * whole *
    ********/
    if(show_zeros || degrees != 0) {
        buffer.append(degrees);
        buffer.append(whole_sep);
        if(space_sep) buffer.append(" ");
    }

    /**********
    * minutes *
    **********/
    if(show_zeros || minutes != 0 ||
                    (degrees != 0 &&
                    (seconds != 0 || (fraction != 0 && decimals >0))) ) {
        buffer.append(int_format.format(minutes))
              .append(minutes_sep);
        if(space_sep) buffer.append(" ");
    }

    /**********
    * seconds *
    **********/
    if(show_zeros || seconds != 0
                  || (fraction != 0 && decimals >0)
                  || (degrees == 0 && minutes == 0) ) {
        buffer.append(int_format.format(seconds));
        if(seconds_sep != '\0' ) buffer.append(seconds_sep);
    }

    if(decimals >0 && (show_zeros || fraction != 0.0 || seconds != 0) ) {
        buffer.append(fraction_format.format(fraction));
     //    System.out.println(buffer);
    }


    return buffer;

} // end of format method

/**************************************************************************
*
**************************************************************************/
private static int skipWhiteSpace(String source, int index) {

    while(index< source.length() && Character.isWhitespace(source.charAt(index))) ++index;

    return index;

} // end of skipWhiteSpace method

/****************************************************************************
* Format a long. This just converts the number to a double and calls
* {@link #format(double, StringBuffer, FieldPosition)}.
****************************************************************************/
public StringBuffer format(long number, StringBuffer buffer, FieldPosition pos) {

    return format((double)number, buffer, pos);

} // end of format a long

/*************************************************************************
*
*************************************************************************/
private int skipWhitespace(String source, ParsePosition pos) {

    if(pos.getErrorIndex() != -1) return 0;

    int index = pos.getIndex();

    while(index< source.length() &&
          Character.isWhitespace(source.charAt(index))) ++index;

    int skipped = index - pos.getIndex();
    pos.setIndex(index);

    return skipped;

} // end of skipWhitesapce method


/*************************************************************************
*
*************************************************************************/
private boolean skipCharacter(String source, ParsePosition pos, char c) {

    if(pos.getErrorIndex() != -1) return false;


    int index = pos.getIndex();
    if(index < source.length() && source.charAt(index) == c) {
        ++index;
        pos.setIndex(index);
        return true;

    } else {
        return false;
    }

} // end of skipCharacter

/*************************************************************************
*
*************************************************************************/
private int parseSign(String source, ParsePosition pos) {

    if(pos.getErrorIndex() != -1) return 1;
    int index = pos.getIndex();

    if(index >=source.length()) return 1;

    int sign = 1;
    if(     source.charAt(index) == '+') ++index;
    else if(source.charAt(index) == '-') {
        sign = -1;
        ++index;
    }

    pos.setIndex(index);

    return sign;

} // end of parseSign method

/*************************************************************************
*
*************************************************************************/
private int parseInteger(String source, ParsePosition pos) {

    if(pos.getErrorIndex() != -1) return 0;

    int index = pos.getIndex();

    /***************************************
    * make sure we are starting at a digit *
    ***************************************/
    if(index > source.length() ||
       !Character.isDigit(source.charAt(index))) {
        pos.setErrorIndex(index);
        return 0;
    }

    int start = index;

    /**********************************
    * find all the consecutive digits *
    **********************************/
    while(index < source.length() &&
          Character.isDigit(source.charAt(index))) ++index;

    /**************************
    * parse the integer value *
    **************************/
    int value = Integer.parseInt(source.substring(start, index));

    pos.setIndex(index);

    return value;

} // end of DMSFormat class

/*************************************************************************
* the value after the decimal place.
*************************************************************************/
private double parseDecimal(String source, ParsePosition pos) {

//System.out.println("parsing decimal");

    if(pos.getErrorIndex() != -1) return 0.0;

    int index = pos.getIndex();




    /**********************************
    * find all the consecutive digits *
    **********************************/
    int start = index;
    while(index < source.length() &&
          Character.isDigit(source.charAt(index))) ++index;



    /**************************
    * parse the integer value *
    **************************/
    double value = 0.0;
    if(index>start) {
        value = Double.parseDouble('.'+source.substring(start, index));
    }

    pos.setIndex(index);

    return value;


} // end of parseDecimal method

/**************************************************************************
*
**************************************************************************/
private Piece parsePiece(String source, ParsePosition pos, int last_place) {

    skipWhitespace(source, pos);

    /****************************
    * check if we're at the end *
    ****************************/
    if(pos.getIndex() == source.length() ) return null;


    int value = parseInteger(source, pos);
    int spaces = skipWhitespace(source, pos);

    int place = UNDEFINED;
    if(     last_place < WHOLE   && skipCharacter(source, pos,   whole_sep)) place = WHOLE;
    else if(last_place < MINUTES && skipCharacter(source, pos, minutes_sep)) place = MINUTES;
    else if(last_place < SECONDS && skipCharacter(source, pos, seconds_sep)) place = SECONDS;

    spaces += skipWhitespace(source, pos);

    double fraction = 0.0;
    if(spaces == 0 && skipCharacter(source, pos, '.')) {
        fraction = parseDecimal(source, pos);
    }

  //  System.out.println("place="+place+" value="+value+" fraction="+fraction);

    return new Piece(place, value, fraction);


} // end of parse Piece method



/**************************************************************************
* Parse a number in sexigesimal notation. This implements the parsing,
* but you are more likely to use {@link #parse(String)}, which calls this method.
* @param source a string which we will parse at least part of.
* @param pos The position in the string to start parsing.
* @return A Double containing the parsed value.
**************************************************************************/
public Number parse(String source, ParsePosition pos) {

    /**************************************************
    * get the original index so that we can signal
    * a parse error by putting the parse position
    * back to the beginning
    *************************************************/
    int orig_index = pos.getIndex();

    /*******
    * sign *
    *******/
    skipWhitespace(source, pos);
    int sign = parseSign(source, pos);

//System.out.println("sign="+sign);

    /**************************
    * read up to three pieces *
    **************************/
    List<Piece> pieces = new ArrayList<Piece>();
    int last_place = UNDEFINED;
    for(int i=0; i< 3; ++i) {
        Piece piece = parsePiece(source, pos, last_place);
        if(piece != null) {
            pieces.add(piece);
            last_place = piece.getPlace();
//System.out.println("piece "+piece.getPlace()+" "+piece.getValue());
        }

    }

    /*******************
    * check for errors *
    *******************/
    if(pieces.size() == 0 || pos.getErrorIndex()!= -1) {
        pos.setIndex(orig_index);
        return null;
    }

    /************************************
    * check if any places are undefined *
    ************************************/
    double sum = 0.0;
    boolean any_undefined = false;
    for(int i=0; i< pieces.size(); ++i) {
        Piece piece = (Piece)pieces.get(i);

        if(piece.place == UNDEFINED) {
            if(default_high) {
                piece.place = i;
            } else {
                piece.place = i + 3 - pieces.size();
            }
        }

        sum += piece.getValue();

    } // end of loop over pieces;

    return new Double(sign*scale*sum);

} // end of parse method


/*********************************************************************
*
*********************************************************************/
private class Piece {

int place;
int value;
double fraction;

/*********************************************************************
*
*********************************************************************/
public Piece(int place, int value, double fraction) {

    this.place = place;
    this.value = value;
    this.fraction = fraction;

} // end of constructor
/*********************************************************************
*
*********************************************************************/
public int getPlace() { return place; }

/*********************************************************************
*
*********************************************************************/
public double getValue() {

    if(     place == SECONDS) return (value + fraction)/3600.0;
    else if(place == MINUTES) return (value + fraction)/60.0;
    else if(place == WHOLE  ) return  value + fraction;
    else                      return Double.NaN;

} // end of getValue method



} // end of Piece inner class


} // end of SexigesimalFormat class
