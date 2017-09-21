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

package eap.sky.time.cycles;

import eap.sky.time.*;

import java.util.*;
import java.text.*;

/****************************************************************************
*
****************************************************************************/
public class Lunation {

private static final DateFormat month_format = new SimpleDateFormat("MMM");
private static final  DateFormat year_format = new SimpleDateFormat("yyyy");

PhaseCalculator calc;
double split_phase;

Night first_night;
Night  last_night;
Night center_night;

String date_string;

/************************************************************************
*
************************************************************************/
public Lunation(Night night) {

    this(night, -1.0);

} // end of constructor

/************************************************************************
*
************************************************************************/
public Lunation(Night night, double split_phase) {

    this(night, night.getRiseSet().createPhaseCalculator(), split_phase);

} // end of constructor

/*************************************************************************
*
*************************************************************************/
public Lunation(Night night, PhaseCalculator calc, double split_phase) {

    this.calc = calc;
    this.split_phase = split_phase;

    findFirstNight(night);
    findLastNight();

} // end of constructor

/*************************************************************************
*
*************************************************************************/
private Lunation(PhaseCalculator calc, Night night, double split_phase,
                 boolean first) {

    this.calc = calc;
    this.split_phase = split_phase;

    if(first) {
        this.first_night = night;
        findLastNight();
    } else {
        this.last_night = night;
        findFirstNight(night.plus(-27));
    }

} // end of private constructor

/*************************************************************************
*
*************************************************************************/
public Lunation findLunationContaining(Night night) {

    if(contains(night)) return this;
    else                return new Lunation(night, calc, split_phase);

} // end of findLunation method

/*************************************************************************
*
*************************************************************************/
private double phase(Night night) {

    return Math.abs(calc.phase(night) - split_phase);

//     double phase = calc.phase(night) - split_phase -1.0;
//     while(phase < -1.0) phase += 2.0;
//     while(phase > 1.0) phase -= 2.0;
//
//     return phase;

} // end of phase method

/*************************************************************************
*
*************************************************************************/
private void findFirstNight(Night night) {

    double last_phase = phase(night.nextNight());
    double      phase = phase(night);
    boolean decreasing = phase < last_phase;

    last_phase = phase;

    night = night.lastNight();
    while(true) {

        phase = phase(night);
  //  System.out.println(night+" phase="+phase+" decreasing? "+decreasing);
        /*************************
        * check what's happening *
        *************************/
        if(decreasing) {
            if(phase > last_phase) {
                first_night = night.nextNight();
                return;
            }
        } else if(phase < last_phase) decreasing = true;

        /*********************
        * remember the phase *
        *********************/
        last_phase = phase;
        night = night.lastNight();

    } // end of loop over dates

} // end of findFirstNight

/*************************************************************************
*
*************************************************************************/
private void findLastNight() {

//System.out.println("finding last");

    Night[] nights = new Night[4];
    double[] phases = new double[nights.length];

    int min = 0;
    for(int i=0; i< nights.length; ++i) {
        Night night = first_night.plus(28+i);

        nights[i] = night;
        phases[i] = phase(night);

    //    System.out.println(night+" phase="+phases[i]);

        if(phases[i] < phases[min]) min = i;

    }

    last_night = nights[min-1];

} // end of findLastNight method

/*************************************************************************
*
*************************************************************************/
public PhaseCalculator getPhaseCalculator() { return calc; }

/*************************************************************************
*
*************************************************************************/
public Night getFirstNight() { return first_night; }

/*************************************************************************
*
*************************************************************************/
public Night getLastNight() { return last_night; }

/*************************************************************************
*
*************************************************************************/
public Night getCenterNight() {

    if(center_night == null) {
        center_night = first_night.plus(last_night.daysAfter(first_night)/2);
    }

    return center_night;

} // end of getCenterNight method

/*************************************************************************
*
*************************************************************************/
public boolean contains(Night night) {

    return first_night.compareTo(night) <= 0 &&
            last_night.compareTo(night) >= 0;

} // end of contains method

/*************************************************************************
*
*************************************************************************/
public int nightOfLunation(Night night) {

    return night.daysAfter(first_night);

} // end of nightOfLunation method

/*************************************************************************
*
*************************************************************************/
public int nightFromEnd(Night night) {

    return last_night.daysAfter(night);


} // end of nightFromEnd method

/*************************************************************************
*
*************************************************************************/
public int getLength() { return last_night.daysAfter(first_night)+1; }

/*************************************************************************
*
*************************************************************************/
public Lunation plus(int offset) {

    Lunation lunation = this;
    if(offset>0) {
        for(int i=0; i< offset; ++i) {
            lunation=lunation.nextLunation();
        }
    } else if(offset<0) {
        for(int i=0; i< -offset; ++i) {
            lunation = lunation.lastLunation();
        }
    }

    return lunation;

} // end of plus method

/*************************************************************************
*
*************************************************************************/
public Lunation nextLunation() {
    return new Lunation(calc, last_night.nextNight(), split_phase, true);

} // end of nextLunation method

/*************************************************************************
*
*************************************************************************/
public Lunation lastLunation() {
    return new Lunation(calc, first_night.lastNight(), split_phase, false);

} // end of nextLunation method

/*************************************************************************
*
*************************************************************************/
public String getDateString() {

    if(date_string == null) {

        JulianDate jd = new JulianDate(UTCSystem.getInstance());

        jd.setModifiedJulianDate(first_night.getMJDAtStart());
        String month1 = month_format.format(jd.toDate());

        jd.setModifiedJulianDate(last_night.getMJDAtStart());
        Date date = jd.toDate();
        String month2 = month_format.format(date);
        String year    = year_format.format(date);

        if(month1.equals(month2)) return month1+" "+year;
        else                      return month1+" - "+month2+" "+year;


    } // end if we have to construct the date string

    return date_string;

} // end of getDateString method

/*************************************************************************
*
*************************************************************************/
public boolean equals(Object o) {

    Lunation lunation = (Lunation)o;

    return getFirstNight().equals(lunation.getFirstNight()) &&
           split_phase == lunation.split_phase;

} // end of equals method


/*************************************************************************
*
*************************************************************************/
public int hashCode() {

    return getFirstNight().hashCode();

} // end of hashCode method


/*************************************************************************
*
*************************************************************************/
public String toString() {

    return "Lunation "+first_night+" to "+last_night;

} // end of toString method


} // end of Lunation class
