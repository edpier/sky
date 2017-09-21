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

package eap.sky.earth.atmosphere.monitors;

import eap.sky.time.*;
import eap.sky.time.clock.*;
import eap.sky.util.numerical.smoothing.*;
import eap.sky.earth.atmosphere.*;

/************************************************************************
*
************************************************************************/
public class SmoothedCO2Monitor implements CO2Monitor {

CO2Monitor monitor;
long interval;
Clock clock;

Smoother smoother;
double last_co2;
double co2;

PreciseDate last_poll_time;
PollingThread thread;
WatchdogThread watchdog;

/************************************************************************
*
************************************************************************/
public SmoothedCO2Monitor(CO2Monitor monitor, long interval, Clock clock,
                          double last_co2, Smoother smoother) {

    this.monitor = monitor;
    this.interval = interval;
    this.clock = clock;
    this.last_co2 = last_co2;
    this.smoother = smoother;


    thread = new PollingThread();
    thread.start();

    watchdog = new WatchdogThread();
    watchdog.start();

} // end of constructor

/**************************************************************************
* Report the CO<sub>2</sub> fraction
* @return the CO<sub>2</sub> fraction specified in the constructor in
* micromoles per mole (ppm).
**************************************************************************/
public double reportCO2Fraction(PreciseDate time) {

    double co2;
    synchronized(smoother) { co2 = smoother.getSmoothedValue(); }

    if(Double.isNaN(co2)) co2 = last_co2;
    else             last_co2 = co2;

    return co2;

} // end of reportCO2Fraction method

/***************************************************************************
*
***************************************************************************/
private long timeSinceLastPoll() {

    /****************************************************
    * Note the last poll time is only null for a
    * brief instant after the polling thread is started.
    *****************************************************/
    if(last_poll_time == null) return 0l;

    PreciseDate now = clock.currentTime();
    double seconds = now.secondsAfter(last_poll_time);

    return Math.round(1000.0*seconds);

} // end of timeSincelastPoll method

/***************************************************************************
*
***************************************************************************/
private class PollingThread extends Thread {

boolean keep_going;

/***************************************************************************
*
***************************************************************************/
public PollingThread() {

    super("CO2Poller");

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public void quit() {

    keep_going = false;
    interrupt();

} // end of quit method


/***************************************************************************
*
***************************************************************************/
public void run() {

    keep_going = true;
    while(keep_going) {

        /***************************
        * poll the weather station *
        ***************************/
        last_poll_time = clock.currentTime();
        double co2 = monitor.reportCO2Fraction(last_poll_time);

        if(!keep_going) break;

        /****************************
        * smooth the weather values *
        ****************************/
        synchronized(smoother) { smoother.addValue(co2); }



        /*****************************************
        * sleep until it's time for another poll *
        *****************************************/
        long sleep = interval - timeSinceLastPoll();
        if(sleep < 0l) sleep = 0l;

        try { sleep(sleep); }
        catch(InterruptedException e) {}

    } // end of loop

} // end of run method

} // end of PollingThread class

/***************************************************************************
*
***************************************************************************/
private class WatchdogThread extends Thread {

boolean keep_going;

/***************************************************************************
*
***************************************************************************/
public WatchdogThread() {

    super("CO2PollingWatchdog");

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public void run() {

    long restart_interval = interval*2;

    keep_going = true;
    while(keep_going) {

        /********************
        * sleep for a while *
        ********************/
        try { sleep(restart_interval); }
        catch(InterruptedException e) { return; }

        /****************************************
        * check if the polling thread got stuck *
        ****************************************/
        if(timeSinceLastPoll() > restart_interval) {
            /**********
            * restart *
            **********/
            thread.quit();
            thread = new PollingThread();
            thread.start();

        } // end if we need to restart

    } // end of loop

} // end of run method

} // end of WatchdogThread inner class

} // end of SmoothedCO2Monitor class
