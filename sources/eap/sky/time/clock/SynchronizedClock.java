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

package eap.sky.time.clock;

import eap.sky.time.*;

/****************************************************************************
* A clock which is synchronized to another.
****************************************************************************/
public class SynchronizedClock implements Clock {

long before;
long after;

Clock local_clock;

double accuracy;
double offset;

boolean synched;

PreciseDate last_time;

/****************************************************************************
*
****************************************************************************/
public SynchronizedClock() {

    local_clock = new SystemClock();

    offset = 0.0;
    accuracy = 1.0;

} // end of lax constructor

/****************************************************************************
*
****************************************************************************/
public void beforeSync() {

    before = System.nanoTime();

} // end of beforeSync method

/****************************************************************************
*
****************************************************************************/
public void afterSync() {

    after = System.nanoTime();

} // end of beforeSync method


/****************************************************************************
*
****************************************************************************/
public void syncTo(PreciseDate remote) {

    PreciseDate local = local_clock.currentTime();

    /***************************************
    * set the accuracy to half the latency *
    ***************************************/
    double accuracy = (double)(after-before)*5e-10;

    /***************************************************
    * Convert the mean of the bracketed times
    * to a date.
    ***************************************************/
    local.increment(-accuracy);

    /*************************************************************
    * calculate the offset
    ************************************************************/
    double measured_offset = remote.secondsAfter(local);

    /**************************
    * apply the Kalman filter *
    **************************/
    double predicted_offset   = this.offset;
    double predicted_accuracy = this.accuracy;

    double gain = predicted_accuracy/(predicted_accuracy + accuracy);

    this.offset = predicted_offset + gain*(measured_offset - predicted_offset);
    this.accuracy = (1.0-gain)*predicted_accuracy;

} // end of syncTo method

/****************************************************************************
*
****************************************************************************/
public PreciseDate currentTime() {

    /***********************************************************
    * get the current time according to the local system clock
    ***********************************************************/
    PreciseDate time = UTCSystem.getInstance().createDate();
    time.setTime(System.currentTimeMillis(), 0);

    /***********************************************
    * apply the offset to sync to the source clock *
    ***********************************************/
    time.increment(offset);

    /****************************************
    * make sure time doesn't jump backwards
    * because the offset is varying
    ****************************************/
    if(last_time != null && time.compareTo(last_time) <= 0) {
        return last_time.copy();
    }

    last_time = time.copy();
    return time;

} // end of currentTime method

/***************************************************************************
* Returns the offset between the source clock and the local system clock.
* A positive value means the local clock reads a time earlier than the
* source clock.
* @return The source clock time minus the system clock time in seconds.
***************************************************************************/
public double getOffset() { return offset; }

/****************************************************************************
* Returns an estimate of the accuracy of the synchronization.
* This is actually half of the time required to make a call to the source clock,
* which is usually dominated by network latency on remote systems.
* This value may be a large overestimate on remote machines, but could be
* a small underestimate on local machines.
* @return An estimate of the synchronization accuracy in seconds
****************************************************************************/
public double getAccuracy() { return accuracy; }

} // end of SynchedClock class