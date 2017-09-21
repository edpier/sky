// Copyright 2013 Edward Alan Pier
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

package eap.sky.ephemeris.sgp4;

import eap.sky.time.*;

import java.text.*;
import java.util.*;


/******************************************************************************
*
*******************************************************************************/
public class TLE {

UTCSystem UTC;

/** default card condition */
private static final String BLANKCARD = "                                                                     ";

/** Argument of Perigee */
private double argPerigee;

/** SGP4 drag term */
private double bstar;

private static final DecimalFormat df1 = new DecimalFormat("0");
private static final DecimalFormat df2dot8 = new DecimalFormat("00.00000000", new DecimalFormatSymbols(Locale.US));
private static final DecimalFormat df3dot4 = new DecimalFormat("000.0000", new DecimalFormatSymbols(Locale.US));
private static final DecimalFormat df3dot8 = new DecimalFormat("000.00000000", new DecimalFormatSymbols(Locale.US));
private static final DecimalFormat df4 = new DecimalFormat("0000");
private static final DecimalFormat df5 = new DecimalFormat("00000");
private static final DecimalFormat dfdot7 = new DecimalFormat(".0000000", new DecimalFormatSymbols(Locale.US));
private static final DecimalFormat dfdot8 = new DecimalFormat(".00000000", new DecimalFormatSymbols(Locale.US));

/** orbit shape [0.0, < 1.0] */
private double eccentricity;

/** number of prior elset updates [0,9999] */
private int elsetNum;

/** orbit model to use [0 = SGP(nd/2,ndd/6), 2 = SGP4(Bstar)] */
private int ephemerisType;

private double epochDay; // epoch day [0.0,366.0]
private int epochYr; // epoch year [0,99]
PreciseDate epoch;

/** orbital inclination (radians) [0.0,180.0] */
private double inclination;

/** International designator */
private String intDesig;

/** is the data on the 2 lines valid? */
private boolean isValid;

/** Mean Anomaly (radians) */
private double meanAnomaly;

/** revolutions per day */
private double meanMotion;

private double nDot;

private double nDotDot;

/** revolutions since launch (deployment...) */
private int revNum;

/** right ascension of the ascending node (radians) [0.0,360.0] */
private double ascending_node;

/** Classification: U, C, S, or T */
private String satClass;

/** SSC# [1,99999] */
private int satID;


/*****************************************************************************
* Default constructor.
*****************************************************************************/
public TLE(UTCSystem UTC) {

    this.UTC = UTC;

} // end of default constructoor

/*****************************************************************************
* Default constructor.
*****************************************************************************/
public TLE() {

    this(UTCSystem.getInstance());

} // end of default constructoor

/*****************************************************************************
* This constructor takes the two lines of the standard
* 2-line elset format as inputs. The ends of the cards are checked for
* additional columns containing average RCS and TOES values. Only the
* standard columns are preserved in card1 and card2. Then lengths of card1
* and card2 are truncated or extended to 69 characters
* @param card1 the String containing line 1 of the elset (plus possible
*        avgRCS value)
* @param card2 the String containing line 2 of the elset (plus possible
*        TOES value)
*****************************************************************************/
public TLE(String card1, String card2, UTCSystem UTC) throws ElsetParseException {

    this.UTC = UTC;
    set(card1, card2);

} // end of constructor from cards.

/*****************************************************************************
* This constructor takes the two lines of the standard
* 2-line elset format as inputs. The ends of the cards are checked for
* additional columns containing average RCS and TOES values. Only the
* standard columns are preserved in card1 and card2. Then lengths of card1
* and card2 are truncated or extended to 69 characters
* @param card1 the String containing line 1 of the elset (plus possible
*        avgRCS value)
* @param card2 the String containing line 2 of the elset (plus possible
*        TOES value)
*****************************************************************************/
public TLE(String card1, String card2) throws ElsetParseException {

    this(card1, card2, UTCSystem.getInstance());

} // end of constructor from cards.

/*************************************************************************
*
*************************************************************************/
private void validateChecksum(int num, String card) throws ElsetParseException{

    String s = card.substring(68, 69);

    /*****************************
    * see if there is a checksum *
    *****************************/
    if (card.length() <= 68 || s.equals(" ")) {
        /**************
        * no checksum *
        **************/
        return;
    }

    /**********************
    * verify the checksum *
    **********************/
    int correct = checkSum(card);

    int parsed;
    try { parsed = Integer.parseInt(s); }
    catch(NumberFormatException e) {
        throw new ElsetParseException(num, satID, "Could not parse checksum: "+s, e);
    }

    if (parsed != correct) {
        String message = "Bad checksum "+parsed+". Should be "+correct;
        throw new ElsetParseException(num, satID, message);
    }

} // end of validateChecksum method

/****************************************************************************
* Parses and validates the first elset card.
****************************************************************************/
private void parseCard1(String card) throws ElsetParseException {

    if (card == null) {
        throw new NullPointerException();
    }

    if (card.length() < 68) {
        throw new ElsetParseException(1, "Length "+card.length()+" is less than 68");
    }

    if (!"1".equals(card.substring(0, 1))) {
        throw new ElsetParseException("This is not card 1");
    }

    /**********************
    * satellite ID number *
    **********************/
    try {
        setSatID(Integer.parseInt(card.substring(2, 7).replace(' ','0')));

    } catch(NumberFormatException e) {
        String message = "Error parsing satID: "+card.substring(2, 7);
        throw new ElsetParseException(1, message, e);
    } catch(IllegalArgumentException e) {
        throw new ElsetParseException(1, "Invalid satellite ID", e);
    }

    try {

        /******************
        * satellite class *
        ******************/
        satClass = card.substring(7, 8);
        if(!"U".equals(satClass) &&
        !"C".equals(satClass) &&
        !"S".equals(satClass) &&
        !"T".equals(satClass)) {
            throw new ElsetParseException(1, satID, "Invalid class: "+satClass);
        }

        /***************************
        * International designator *
        ***************************/
        intDesig = card.substring(9, 17);

        /*************
        * epoch year *
        *************/
        try {
            setEpochYear(Integer.parseInt(card.substring(18, 20)));

        } catch (NumberFormatException e) {
            String message = "Error parsing epochYr";
            throw new ElsetParseException(1, satID, message, e);
        }

        /************
        * epoch day *
        ************/
        try {
            epochDay = Double.parseDouble(card.substring(20, 32));
            if (epochDay < 0.0 || epochDay > 367.0) {
                String message = "Epoch Day out of range: "+epochDay;
                throw new ElsetParseException(1, satID, message);
            }
        } catch (NumberFormatException e) {
            String message = "Error parsing epochDay: "+
                            card.substring(20, 32);
            throw new ElsetParseException(1, satID, message, e);
        }

        /*****************************************
        * construct the PreciseDate of the epoch *
        *****************************************/
        try {
            epoch = UTC.createFormat()
                       .parsePreciseDate(epochYr+"-01-01 00:00:00 UTC");

            epoch.increment(86400.0*epochDay);

        } catch(ParseException e) {
            /***************************
            * this should never happen *
            ***************************/
            throw (IllegalStateException)(new IllegalStateException().initCause(e));
        }

        /********
        * N Dot *
        ********/
        try { setNdot(Double.parseDouble(card.substring(33, 43))); }
        catch (NumberFormatException e) {
            String message = "Could not parse Ndot: "+
                            card.substring(33, 43);

            throw new ElsetParseException(1, satID, message, e);
        }

        /***************
        * N double dot *
        ***************/
        try {
            String tmpStr;
            if(card.charAt(50) == ' ') {
                tmpStr = card.substring(44, 45) + "." + card.substring(45, 50)
                        + "E+" + card.substring(51, 52);
            } else {
                tmpStr = card.substring(44, 45) + "." + card.substring(45, 50)
                        + "E" + card.substring(50, 52);
            }

            setNdotdot(Double.parseDouble(tmpStr));

        } catch (NumberFormatException e) {

            String message = "Card 1 Error parsing N double dot: "+
                            card.substring(44, 52);

            throw new ElsetParseException(1, satID, message, e);
        }

        /********
        * BSTAR *
        ********/
        try {
            String expString = card.substring(59, 61);
            if (" 0".equalsIgnoreCase(expString)) {
                expString = "00";
            }

            setBstar(Double.parseDouble(card.substring(53, 54)+"."+
                                        card.substring(54, 59)+"E"+expString));

        } catch (NumberFormatException e) {
            String message = "Could not parse BSTAR: "+
                            card.substring(53, 61);
            throw new ElsetParseException(1, satID, message, e);
        }

        /*****************
        * Ephemeris type *
        *****************/
        try {
            if (" ".equals(card.substring(62, 63))) {
                throw new ElsetParseException(1, satID, "No Ephemeris Type");
            } else {
                setEphemerisType(Integer.parseInt(card.substring(62, 63)));
            }

        } catch (NumberFormatException e) {
            String message = "Error parsing ephemeris type";
            throw new ElsetParseException(1, satID, message, e);
        }

        /***************
        * Elset number *
        ***************/
        StringTokenizer st = new StringTokenizer(card.substring(65, 68), " ");
        try {
            setElsetNum(Integer.parseInt((String) st.nextElement()));

        } catch (NumberFormatException ex) {
            String message = "Could not parse elset number: "+
                            card.substring(65, 68);

            throw new ElsetParseException(1, satID, message);
        }
    } catch(IllegalArgumentException e) {
        throw new ElsetParseException(1, satID, "Parsed illegal value", e);
    }

    /***********
    * checksum *
    ***********/
    validateChecksum(1, card);

} // end of parseCard1 method

/***************************************************************************
* This method validates the data fields and checksum, if available, on the
* second elset card.
*
* @return boolean = true if card contains valid data for the second elset
*         line
***************************************************************************/
public boolean parseCard2(String card) throws ElsetParseException {

    if (card == null) {
        throw new NullPointerException();
    }

    if (card.length() < 68) {
        throw new ElsetParseException(2, satID, "Shorter than 68 characters");
    }

    if (!"2".equals(card.substring(0, 1))) {
        throw new ElsetParseException(2, satID, "This is not card 2");
    }

    try {
        /**************
        * inclination *
        **************/
        try {
            setInclination(Double.parseDouble(card.substring(8, 17)));

        } catch (NumberFormatException e) {
            String message = "Card 2 Error parsing inclination: ";
            throw new ElsetParseException(2, satID, message, e);
        }

        /************************************
        * Right Ascension of ascending node *
        ************************************/
        try {
            setAscendingNode(Double.parseDouble(card.substring(17, 26)));

        } catch (NumberFormatException e) {
            String message = "Error parsing Right Acension: "+
                            card.substring(17, 26);
            throw new ElsetParseException(2, satID, message, e);
        }

        /**********************
        * argument of perigee *
        **********************/
        try {
            setArgPerigee(Double.parseDouble(card.substring(34, 42)));

        } catch (NumberFormatException e) {
            String message = "Card 2 Error parsing argPerigee: "+
                            card.substring(34, 42);

            throw new ElsetParseException(2, satID, message, e);
        }

        /***************
        * eccentricity *
        ***************/
        try {
            setEccentricity(Double.parseDouble("0."+card.substring(26, 33)));

        } catch(NumberFormatException e) {
            String message = "Card 2 Could not parse eccentricity: "+
                            card.substring(26, 33);
            throw new ElsetParseException(2, satID, message, e);
        }

        /***************
        * mean anomaly *
        ***************/
        try {
            setMeanAnom(Double.parseDouble(card.substring(43, 51)));

        } catch (NumberFormatException e) {
            String message = "Error parsing Mean Anomaly: "+card.substring(43, 51);
            throw new ElsetParseException(2, satID, message, e);
        }

        /**************
        * mean motion *
        **************/
        try {
            setMeanMotion(Double.parseDouble(card.substring(52, 63)));

        } catch (NumberFormatException e) {
            String message = "Error parsing Mean Motion: "+
                            card.substring(52, 63);
            throw new ElsetParseException(2, satID, message, e);
        }

        /*******************
        * revolution count *
        *******************/
        StringTokenizer st = new StringTokenizer(card.substring(63, 68), " ");
        try {
            setRevNum(Integer.parseInt((String) st.nextElement()));

        } catch (NumberFormatException e) {
            String message = "Error parsing revision number: "+
                            card.substring(63, 68);
            throw new ElsetParseException(2, satID, message, e);
        }
    } catch(IllegalArgumentException e) {
        throw new ElsetParseException(2, satID, "Parsed illegal value", e);
    }


    /***********
    * checksum *
    ***********/
    // FIXME need to determine if this is ignored or not
    validateChecksum(2, card);

    return true;
}

/**********************************************************************
* Computes the checksum of a card of a a 2-line elset. The
* first 68 characters are are summed byte to get the checksum.
* @return the int containing the checksum [0,9]
* @param card the String containing the card image
**********************************************************************/
private int checkSum(String card) {

    int checksum = 0;

    for (int i = 0; i < 68; i++) {
        switch (card.charAt(i)) {
            case '1':
            /* falls through */
            case '-':
                checksum++;
                break;
            case '2':
                checksum += 2;
                break;
            case '3':
                checksum += 3;
                break;
            case '4':
                checksum += 4;
                break;
            case '5':
                checksum += 5;
                break;
            case '6':
                checksum += 6;
                break;
            case '7':
                checksum += 7;
                break;
            case '8':
                checksum += 8;
                break;
            case '9':
                checksum += 9;
                break;
            default:
                break;
        }
    }

    return checksum % 10;
}

/***************************************************************************
* This method analyzes the supplied value val and breaks it into sign,
* mantissa, and exponent parts, each of which is constructed as an integer.
* The mantissa is constrained to be either 00000 or be in the range
* [10000,99999] (5 significant digits), representing the values 0.10000 to
* 0.99999. The exponent must lie in the range [-9,9] (single digit). The
* returned mantissa and exponent are "clamped" at the limit values 0000 and
* 0 or 9999 and 9 if the constraints can't be achieved.
*
* @return boolean true if the formatting could be done within the
*         constraints
* @param val the double value to format
* @param parts the int array containing the sign, mantissa, and exponent
****************************************************************************/
private boolean formatInt(double val, int[] parts) {

    double absVal = Math.abs(val);
    int exp = 0;

    // determine sign of value
    parts[0] = (val < 0.0) ? -1 : 1;

    if (absVal <= 1.0E-10) {
        // too small in magnitude to fit format constraints
        parts[1] = 0;
        parts[2] = 0;
        return false;
    }

    if (absVal >= 1.0E9) {
        // too large in magnitude to fit format constraints
        parts[1] = 99999;
        parts[2] = 9;
        return false;
    }

    exp = 0; // start with representation absVal * 10^0
    while (absVal > 1.0) { // reduce absVal and increase exponent parts[2]
        absVal /= 10.0;
        exp++;
    }
    while (absVal < 0.1) { // increase absVal and reduce exponent parts[2]
        absVal *= 10.0;
        exp--;
    }
    parts[1] = (int) (absVal * 1.0E5 + 0.5);
    // save first 5 significant digits as a integer
    parts[2] = exp;

    return true;
}

/**********************************************************************
* This method returns the orbital argument of perigee as a double.
* @return the double containing the argument of perigee in degrees.
**********************************************************************/
public double getArgumentOfPerigee() {
    return argPerigee;
}

/*************************************************************************
* This method returns the orbital argument of perigee as a string.
*
* @return the String containing the argument of perigee (NNN.NNNN) in
*         degrees [0,360]
*************************************************************************/
private String getArgPerigeeString() {
    DecimalFormat df4 = new DecimalFormat("000.0000");
    String argPerStr = df4.format(argPerigee);
    return argPerStr;
}

/**************************************************************************
* This method returns the orbital drag term as a string.
*
* @return the String containing the drag term (S.NNNNNESE) in 1/earth-radii
****************************************************************************/
private String getBstarString() {

    String card1 = getCard1();
    return card1.substring(53, 54) + "." + card1.substring(54, 59) + "E"
            + card1.substring(59, 61);
}

/*************************************************************************
* This method returns the orbital drag term as a double.
*
* @return the double containing the drag term (1/earth-radii)
************************************************************************/
public double getBstar() {
    return bstar;
}

/**************************************************************************
* This method returns the first line of the elset.
*
* @return the String containing card1
**************************************************************************/
public String getCard1() {

    StringBuilder card1sb = new StringBuilder("1 00001U 00000000 70001.00000000 +.00000000 +00000+0 +00000+0 0 00009");

    // Sat ID
    String satIDstr = df5.format(satID);
    card1sb.replace(2, 7, satIDstr);

    /*****************
    * classification *
    *****************/
    card1sb.replace(7, 8, satClass);

    // International Designator
    int idLen = intDesig.length();
    if (idLen > 0) {
        if (idLen < 8) {
            intDesig += BLANKCARD.substring(0, 8 - idLen);
        }
        card1sb.replace(9, 17, intDesig.substring(0, 8));
    }

    DecimalFormat df = new DecimalFormat(" 00");
    int tempYr = epochYr;
    if (tempYr > 1900) {
        tempYr -= 1900;
        if (tempYr > 100) {
            tempYr -= 100;
        }
    }
    String yrStr = df.format(tempYr);
    card1sb.replace(17, 20, yrStr);

    // Epoch Day
    String dayStr = df3dot8.format(epochDay);
    card1sb.replace(20, 32, dayStr);

    // N dot
    String nDotStr = dfdot8.format(nDot);
    if (nDotStr.charAt(0) == '-') {
        card1sb.replace(33, 43, nDotStr);
    } else {
        card1sb.replace(33, 43, " " + nDotStr);
    }

    // N dot dot
    int[] parts = new int[3]; // sign, mantissa, and power of 10
    String signStr;

    formatInt(nDotDot, parts); // represent nDotdot as +/-.NNNNNE+/-N
    String mantissa = df5.format(parts[1]);
    signStr = (parts[0] < 0) ? "-" : " ";
    String exp = df1.format(parts[2]);
    if (exp.charAt(0) == '-') {
        card1sb.replace(44, 52, signStr + mantissa + exp);
    } else {
            /** FIX: "0" in the exponent has a zero sign to get the checksums correct  */
            if (exp.charAt(0) == '0') card1sb.replace(44, 52, signStr + mantissa + "-" + exp);
            else card1sb.replace(44, 52, signStr + mantissa + "+" + exp);
            // card1sb.replace(44, 52, signStr + mantissa + "+" + exp);
    }

    // B star
    formatInt(bstar, parts); // represent Bstar as +/-.NNNNNE+/-N
    mantissa = df5.format(parts[1]);
    signStr = (parts[0] < 0) ? "-" : " ";
    exp = df1.format(parts[2]);
    if (exp.charAt(0) == '-') {
        card1sb.replace(53, 61, signStr + mantissa + exp);
    } else {
            /** FIX: "0" in the exponent has a zero sign to get the checksums correct  */
            if (exp.charAt(0) == '0') card1sb.replace(53, 61, signStr + mantissa + "-" + exp);
            else card1sb.replace(53, 61, signStr + mantissa + "+" + exp);
            // card1sb.replace(53, 61, signStr + mantissa + "+" + exp);
    }

    // Ephemeris type
    String ephTypeStr = df1.format(ephemerisType);
    card1sb.replace(62, 63, ephTypeStr);

    // Elset num
    String elnoStr = df4.format(elsetNum);
    card1sb.replace(64, 68, elnoStr);

    card1sb.replace(68, 69, Integer.toString(checkSum(card1sb.substring(0,
            68))));


    return card1sb.toString();

} // end of getCard1 method

/**************************************************************************
* This method returns the second line of the elset.
* @return the String containing card2
**************************************************************************/
public String getCard2() {

    StringBuilder card2sb = new StringBuilder("2 00001  60.0000  90.0000 0010000  90.0000  90.0000 15.00000000    19");
    String satIDstr = df5.format(satID);
    card2sb.replace(2, 7, satIDstr);

    String incStr = df3dot4.format(inclination);
    card2sb.replace(8, 16, incStr);

    String rtAscStr = df3dot4.format(ascending_node);
    card2sb.replace(17, 25, rtAscStr);

    String eccStr = dfdot7.format(eccentricity);
    card2sb.replace(26, 33, eccStr.substring(1, 8));

    String argPerStr = df3dot4.format(argPerigee);
    card2sb.replace(34, 42, argPerStr);

    String meanAnomStr = df3dot4.format(meanAnomaly);
    card2sb.replace(43, 51, meanAnomStr);

    String meanMotionStr = df2dot8.format(meanMotion);
    card2sb.replace(52, 63, meanMotionStr);

    String revnumStr = df5.format(revNum);
    card2sb.replace(63, 68, revnumStr);

    // Compute the checksum
    card2sb.replace(68, 69, ""+checkSum(card2sb.substring(0,68)));

    return new String(card2sb);

} // end of getCard2 method

/************************************************************************
* This method returns the orbital eccentricity as a string.
*
* @return the String containing the eccentricity (0.NNNNNNN) [0, <1]
************************************************************************/
private String getEccentricityString() {
    DecimalFormat dfe = new DecimalFormat(".0000000");
    return "0" + dfe.format(eccentricity);
}

/************************************************************************
* This method returns the orbital eccentricity.
*
* @return the double containing the eccentricity [0.0, <1.0]
************************************************************************/
public double getEccentricity() {
    return eccentricity;
}

/************************************************************************
* This method returns the elset number.
* @return the int containing the elset number [0,9999]
************************************************************************/
public int getElsetNum() {
    return elsetNum;
}

/***********************************************************************
* This method returns the elset number as a string.
*
* @return the String containing the elset number (NNNN) [0,9999]
***********************************************************************/
private String getElsetNumString() {
    return df4.format(elsetNum);
}

/**************************************************************************
* This method returns the ephemeris type (orbit model). 0 = SGP 2 = SGP4
*
* @return the int containing the ephemeris type (N) [0(SGP) or 2(SGP4)]
**************************************************************************/
public int getEphemerisType() {
    return ephemerisType;
}

/***************************************************************************
* This method returns the ephemeris type (orbit model) as a string. 0 = SGP
* 2 = SGP4
*
* @return the String containing the ephemeris type (N) [0(SGP) or 2(SGP4)]
***************************************************************************/
private String getEphemerisTypeString() {
    return Integer.toString(ephemerisType);
}

/***************************************************************************
* This method returns the epoch day of the year.
* @return The day of the epoch year [0,365.99999999]
***************************************************************************/
public double getEpochDay() {
    return epochDay;
}

/*************************************************************************
* This method returns the epoch day of the year as a string.
*
* @return the String containing the day of the epoch year (DDD.DDDDDDD)
*         [0,365.99999999]
*************************************************************************/
private String getEpochDayString() {
    return df3dot8.format(epochDay);
}

/*************************************************************************
* Returns the epoch year.
* @return The four digit epoch year.
*************************************************************************/
public int getEpochYear() { return epochYr; }

/*************************************************************************
*
*************************************************************************/
public PreciseDate getEpoch() { return epoch; }


/*************************************************************************
* This method returns the orbital inclination.
* @return the double containing the inclination in degrees
*************************************************************************/
public double getInclination() {
    return inclination;
}

/**************************************************************************
* Returns the satellite international designator.
* @return the String containing the international designator
**************************************************************************/
public String getIntDesig() {
    return intDesig;
}

/************************************************************************
* Returns the orbital mean anomaly.
* @return The mean anomaly in radians
************************************************************************/
public double getMeanAnomaly() {
    return meanAnomaly;
}


/**************************************************************************
* This method returns the orbital mean motion.
*
* @return the String containing the mean motion in revs/day [ < 17.0]
**************************************************************************/
public double getMeanMotion() {
    return meanMotion;
}

/**************************************************************************
* This method returns the orbital first-order drag term as a string.
* This is not used by SGP4, but is maintained in the TLEs for backward
* compatibility with the older SGP model. It has essentially been replaced
* by B*.
* @return The mean motion rate (S.NNNNNNNN) in revs/day^2
*************************************************************************/
public double getNdot() {
    return nDot;
}

/***************************************************************************
* This method returns the orbital second-order drag term.
* This is not actually used by SGP4, but is maintained in the TLEs
* for backward compatibility with the older SGP model. It has been
* essentially replaced by B*.
* @return The mean motion acceleration in revs/day^2
***************************************************************************/
public double getNdotdot() {
    return nDotDot;
}


/*************************************************************************
* This method computes and returns the orbital period, in minutes/rev.
* @return The orbital period (minutes)
*************************************************************************/
public double getPeriod() {

    double mm = getMeanMotion(); // revs/day
    if (mm > 0.0) return 1440.0 / mm;
    else          return 0.0;
}

/*************************************************************************
* Returns the revolution number.
* @return The revolution number [0,99999]
*************************************************************************/
public int getRevNum() {
    return revNum;
}

/**************************************************************************
* Returns the right ascension of the ascending node.
* @return the double containing the right ascension in degrees
**************************************************************************/
public double getAscendingNode() {
    return ascending_node;
}

/**************************************************************************
* This method returns the current,if any, satellite SSC#.
*
* @return the int containing the sat ID
**************************************************************************/
public int getSatID() {
    return satID;
}

/*************************************************************************
* Returns the current,if any, satellite security classification.
* @return The security class (U, C, S, or T)
*************************************************************************/
public String getSecClass() {
    return satClass;
}

/*************************************************************************
*
*************************************************************************/
public Orbit getOrbitAtEpoch() {

    Orbit orb = new Orbit();

    orb.setEccentricity(getEccentricity());
    orb.setInclination(Math.toRadians(getInclination()));
    orb.setMeanMotion(getMeanMotion()/229.1831180523293); // to rad/min
    orb.setMeanAnomaly(Math.toRadians(getMeanAnomaly()));
    orb.setArgumentOfPerigee(Math.toRadians(getArgumentOfPerigee()));
    orb.setAscendingNode(Math.toRadians(getAscendingNode()));

   return orb;

} // end of getOrbitAtEpoch method

/****************************************************************************
* This method returns true if the current 2-line elset contains valid data.
*
* @return boolean
****************************************************************************/
public boolean isValid() {
    return isValid;
}

/**************************************************************************
* This method replaces the current,if any, lines of the elset.
*
* @param card1 the String containing the replacement for line 1 of the
*        elset
* @param card2 the String containing the replacement for line 2 of the
*        elset
**************************************************************************/
public void set(String card1, String card2) throws ElsetParseException {
    int slen = card1.length();
    String line1;
    String line2;
    if (slen <= 69) {
        line1 = card1 + BLANKCARD.substring(0, 69 - slen);
    } else {
        line1 = card1.substring(0, 70);
    }

    slen = card2.length();
    if (slen <= 69) {
        line2 = card2 + BLANKCARD.substring(0, 69 - slen);
    } else {
        line2 = card2.substring(0, 70);
    }

    isValid = false;

    /*************************************
    * Check if the first 5 characters in
    * both elset lines are the same
    *************************************/
    if(!line1.regionMatches(2, line2, 2, 5)) {
        throw new ElsetParseException("The first 5 characters of "+
                                      "each line do not match");
    }

    parseCard1(line1);
    parseCard2(line2);
    isValid = true;

} // end of set method

/***************************************************************************
* Sets the argument of perigee.
* @param argPer The argument of perigee.
* @throws IllegalArgumentException if the value is not between zero
* and 2*pi (inclusivge).
***************************************************************************/
public void setArgPerigee(double argPer) throws IllegalArgumentException {

    if(argPer < 0.0 || argPer > 360.0) {
        throw new IllegalArgumentException("Argument of perigee "+argPer+
                                           " not between 0 and 360");
    }

    this.argPerigee = argPer;

} // end of setArgPerigee method

/**************************************************************************
* This method sets the Bstar field on card 1 to the value supplied. The
* supplied value must be valid or false is returned.
*
* @param Bstar the double containing the Bstar value to use
**************************************************************************/
public void setBstar(double Bstar) {
    bstar = Bstar;
}

/***************************************************************************
* Sets the orbital eccentricity.
* @throws IllegalArgumentException if the eccentricity is not between 0 and 1
* (exclusive).
****************************************************************************/
public void setEccentricity(double ecc) throws IllegalArgumentException {

    // shouldn't zero eccentricity be allowed??
    if(ecc <= 0.0 || ecc >= 1.0) {
        throw new IllegalArgumentException("Eccentricity "+ecc+
                                           " not between 0 and 1");
    }

    this.eccentricity = ecc;

} // end of setEccentricity method

/******************************************************************************
* This method sets the element number on card 1 to the value supplied. The
* supplied value must be valid ([0,999]) or false is returned.
*
* @return boolean true if the value provided for element number is valid
* @param elno the int containing the element to use [0,999]
*****************************************************************************/
public void setElsetNum(int elno) throws IllegalArgumentException {

    if(elno <0 || elno >999) {
        throw new IllegalArgumentException("Element number "+elno+
                                           " not between 0 and 999");
    }

    this.elsetNum = elno;

} // end of setElsetNum method

/*****************************************************************************
* This method sets the ephemeris type on card 1 to the value supplied. The
* supplied value must be valid ([0 or 2]) or false is returned.
*
* @return boolean true if the value provided for ephemeris type is valid
* @param ephType the int containing the ephemeris type to use [0 or 2]
*****************************************************************************/
public void setEphemerisType(int ephType) throws IllegalArgumentException {

    if(ephType != 0 && ephType != 2) {
        throw new IllegalArgumentException("Ephemeris type "+ephType+
                                           " not 0 or 2");
    }

    this.ephemerisType = ephType;

} // end of setEphemerisType method

/******************************************************************************
* This method sets the epoch day on card 1 to the value supplied. The
* supplied value must be valid ([00.0,366.99999999]) or false is returned.
*
* @return boolean true if the value provided for epoch day-of-the-year is
*         valid
* @param epochDay the double containing the epoch day to use
*        [00.0,366.99999999]
******************************************************************************/
public void setEpochDay(double epochDay) throws IllegalArgumentException {

    if(epochDay < 0.0 || epochDay >= 367.0) {
        throw new IllegalArgumentException("Epoch day "+epochDay+
                                           " not in range [0, 367)");
    }

    this.epochDay = epochDay;

} // end of setEpochDay method

/**************************************************************************
* This method sets the epoch year on card 1 to the value supplied. The
* supplied value must be valid ([00,99]). if less than 100, the value is
* converted to the correct 4 digit year.
*
* @return boolean true if the value provided for epoch year is valid
* @param epochYr the int containing the epoch year to use [00,99]
**************************************************************************/
public void setEpochYear(int year) {

    if(year < 0) {
        throw new IllegalArgumentException("Negative epoch year "+year);
    }

    if(year >= 3000) {
        throw new IllegalArgumentException("Epoch year "+year+
                                           " greater than 3000");
    }

    /******************
    * y2k adjustments *
    ******************/
    if(year < 100) {
        if(year < 50) year += 2000;
        else          year += 1900;
    }

    this.epochYr = year;

} // end of setEpochYear method

/*****************************************************************************
* This method sets the inclination on card 2 to the value supplied. The
* supplied value must be valid ([0.0,180.0] degrees) or false is returned.
*
* @return boolean true if the value provided for inclination is valid
* @param inclin the double containing the inclination to use in radians
*****************************************************************************/
public void setInclination(double inclin) throws IllegalArgumentException {

    if(inclin <0.0 || inclin >180) {
        throw new IllegalArgumentException("Inclination "+inclin+
                                           " not between 0 and 180");
    }

    this.inclination = inclin;

} // end of setInclination method

/**************************************************************************
* This method replaces the international designator on card 1 with the
* string provided (up to 8 characters).
**************************************************************************/
public void setIntDes(String intDes) throws NullPointerException {

    if(intDes == null) {
        throw new NullPointerException("International designator is null");
    }

    /***********************
    * clip to 8 characters *
    ***********************/
    if(intDes.length()>8) intDes = intDes.substring(0, 8);

    this.intDesig = intDes;

} // end of setIntDes method

/******************************************************************************
* This method sets the elset mean anomaly on card 2 to the value supplied.
* The supplied value must be valid ([0.0,360.0]) or false is returned.
*
* @return boolean true if the value provided for mean anomaly is valid
* @param meanAnom the double containing the mean anomaly to use radians
******************************************************************************/
public void setMeanAnom(double meanAnom) throws IllegalArgumentException {

    if(meanAnom <0.0 || meanAnom > 360.0) {
        throw new IllegalArgumentException("Mean anomaly "+meanAnom+
                                           " is not between 0 and 360");
    }

    this.meanAnomaly = meanAnom;

} // end of setMeanAnom method

/***************************************************************************
* This method sets the elset mean motion on card 2 to the value supplied
* and updates the card2 checksum. The supplied value must be valid
* ([0.0,17.0]) or false is returned.
*
* @return boolean true if the value provided for mean motion is valid
* @param meanMotion the double containing the mean motion to use [0.0,17.0]
*        (rev/day)
***************************************************************************/
public void setMeanMotion(double meanMotion) throws IllegalArgumentException {

    if(meanMotion <= 0.0 || meanMotion >17.0) {
        throw new IllegalArgumentException("Mean motion "+meanMotion+
                                           " is not between 0 and 17");
    }

    this.meanMotion = meanMotion;

} // end of setmeanMotion method


/***************************************************************************
* This method sets the n dot (dn/dt) field on card 1 to the value supplied
* and updates the checksum. The supplied value must be valid or false is
* returned.
*
* @return boolean true if the value provided for mean motion derivative is
*         valid
* @param nDot the double containing the n-dot to use (|nDot| < 1.0
*        rev/day/day)
**************************************************************************/
public void setNdot(double nDot) throws IllegalArgumentException {

    if(nDot >=1.0) {
        throw new IllegalArgumentException("nDot "+nDot+
                                           " is greater than 1.0");
    }

    this.nDot = nDot;

} // end of setNdot method

/***************************************************************************
* This method sets the n-dotdot ((dn/dt)/dt) field on card 1 to the value
* supplied and updates the card 1 checksum. The supplied value must be
* valid or false is returned.
*
* @return boolean true if the value provided for mean motion second
*         derivative is valid
* @param nDotdot the double containing the n-dotdot value to use (|nDot| <
*        1.0 rev/day^3)
****************************************************************************/
public boolean setNdotdot(double nDotdot) {

    // FIXME range for nDotdot
    nDotDot = nDotdot;
    return true;

}

/***************************************************************************
* This method sets the revolution count on card 2 to the value supplied and
* updates the card 2 checksum. The supplied value must be valid ([0,99999])
* or false is returned.
*
* @return boolean true if the value provided for rev number is valid
* @param revnum the int containing the rev number to use [0,99999]
***************************************************************************/
public void setRevNum(int revnum) throws IllegalArgumentException {

    if(revnum < 0 || revnum >= 100000) {
        throw new IllegalArgumentException("Revolution count "+revnum+
                                           " is not between 0 and 99999");
    }

    this.revNum = revnum;

} // end of setRevNum method

/***************************************************************************
* This method sets the right ascension of the ascending node on card 2 to
* the value supplied. The supplied value must be valid ([0.0,2Pi]
* radians) or false is returned.
*
* @return boolean true if the value provided for right ascension is valid
* @param rtAsc the double containing the right ascension to use in radians
***************************************************************************/
public void setAscendingNode(double rtAsc) throws IllegalArgumentException {

    if(rtAsc < 0.0 || rtAsc > 360.0) {
        throw new IllegalArgumentException("Right ascension of ascending node"+
                                           rtAsc+
                                           " is not between 0 an 360");
    }

    this.ascending_node = rtAsc;

} // end of setRtAsc method

/***************************************************************************
* This method sets the satellite SSC# on one or both cards to the value
* supplied and updates the checksums on both cards. The supplied value must
* be valid ([1,99999]) or false is returned.
*
* @return boolean true if the value provided for satID is valid
* @param satID the int containing the sat ID to use
***************************************************************************/
public void setSatID(int satID) throws IllegalArgumentException {

    if(satID <= 0 || satID >= 100000) {
        throw new IllegalArgumentException("SSC number "+satID+
                                           " is not between 1 and 99999");
    }

    this.satID = satID;
}

/***************************************************************************
* This method replaces the security classification on card 1 with the
* classification provided and updates the card 1 checksum.
*
* @return boolean true if a valid classification was provided
* @param secClass the String containing the new classification (U, C, S, or
*        T)
***************************************************************************/
public void setSecClass(String secClass) throws IllegalArgumentException {

    if ("U".equals(secClass) ||
        "C".equals(secClass) ||
        "S".equals(secClass) ||
        "T".equals(secClass)) {
        this.satClass = secClass;

    } else {
        throw new IllegalArgumentException("Invalid security classification "+
                                           secClass+" Should be U, C, S, or T");
    }

} // end of setSecClass method

} // end of Elset class