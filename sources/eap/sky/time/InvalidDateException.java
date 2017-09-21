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

/****************************************************************************
* Indicates that a given date can never actually occur. For example,
* a {@link UTCDate} which refers to a negative (skipped) leap second, or
* a UTCDate for which {@link UTCDate#isLeapSecond()} returns true, but
* which is not actually a leap second.
* This is a RuntimeException, so it does not need to be explicitly
* declared or caught.
****************************************************************************/
public class InvalidDateException extends IllegalArgumentException {

/*****************************************************************************
* Creates a new exception indicating that the given date is not valid.
* @param date The invalid date
*****************************************************************************/
public InvalidDateException(PreciseDate date) {

    super(date+" is not valid");


} // end of InvalidDateException



} // end of InvalidDateException class
