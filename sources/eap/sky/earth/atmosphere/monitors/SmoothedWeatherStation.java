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

/*************************************************************************
*
*************************************************************************/
public class SmoothedWeatherStation implements WeatherStation {

WeatherStation station;
long interval;
Clock clock;

double last_temperature;
double last_pressure;
double last_humidity;

Smoother temperature_smoother;
Smoother    pressure_smoother;
Smoother    humidity_smoother;

boolean last_ice;

PreciseDate last_poll_time;
PollingThread thread;
WatchdogThread watchdog;
Object lock;

/*************************************************************************
*
*************************************************************************/
public SmoothedWeatherStation(WeatherStation station, long interval,
                              Clock clock, Weather last_weather,
                              Smoother temperature_smoother,
                              Smoother    pressure_smoother,
                              Smoother    humidity_smoother ) {

    this.station = station;
    this.interval = interval;
    this.clock = clock;

    last_temperature = last_weather.getCelsiusTemperature();
    last_pressure    = last_weather.getPressure();
    last_humidity    = last_weather.getRelativeHumidity();

    last_ice = last_weather.getWaterVapor().isIcy();

    this.temperature_smoother = temperature_smoother;
    this.pressure_smoother    = pressure_smoother;
    this.humidity_smoother    = humidity_smoother;


    lock = new Object();



    thread = new PollingThread();
    thread.start();

    watchdog = new WatchdogThread();
    watchdog.start();

} // end of constructor

/***************************************************************************
*
***************************************************************************/
public Weather reportWeather(PreciseDate time) {

    /**************************
    * get the smoothed values *
    **************************/
    double temperature;
    double pressure;
    double humidity;

    synchronized(lock) {
        temperature = temperature_smoother.getSmoothedValue();
        pressure    = pressure_smoother.getSmoothedValue();
        humidity = humidity_smoother.getSmoothedValue();
    }

    /*****************************
    * replace NaNs with defaults *
    *****************************/
    if(Double.isNaN(temperature)) temperature = last_temperature;
    else                          last_temperature = temperature;


    if(Double.isNaN(pressure)) pressure = last_pressure;
    else                       last_pressure = pressure;

    if(Double.isNaN(humidity)) humidity = last_humidity;
    else                       last_humidity = humidity;


    /*********************************
    * construct the smoothed weather *
    *********************************/
    WaterVapor vapor = new RelativeHumidity(humidity, last_ice);
    return  new Weather(pressure, temperature, vapor);

} // end of reportWeather method

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

    super("WeatherPoller");

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
        Weather weather = station.reportWeather(last_poll_time);

        if(!keep_going) break;

        /****************************
        * smooth the weather values *
        ****************************/
        synchronized(lock) {
            temperature_smoother.addValue(weather.getCelsiusTemperature());
               pressure_smoother.addValue(weather.getPressure()   );

               double humidity = weather.getRelativeHumidity();
               humidity_smoother.addValue(humidity);
               if(!Double.isNaN(humidity)) {
                   last_ice = weather.getWaterVapor().isIcy();
               }
        } // end of synchronized block


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

    super("WeatherPollingWatchdog");

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

} // end of WeatherPoller class
