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

/*****************************************************************************
*
*****************************************************************************/
public class ElsetParseException extends Exception {

/*****************************************************************************
*
*****************************************************************************/
public ElsetParseException(String message) {

    super(message);

} // end of plain constructor

/*****************************************************************************
*
*****************************************************************************/
public ElsetParseException(int card, String message) {

    super("Card "+card+" "+message);

} // end of plain constructor


/*****************************************************************************
*
*****************************************************************************/
public ElsetParseException(int card, int id, String message) {

    super("Card "+card+" ID "+id+" "+message);

} // end of plain constructor

/*****************************************************************************
*
*****************************************************************************/
public ElsetParseException(int card, String message, Exception cause) {

    super("Card "+card+" "+message);
    initCause(cause);

} // end of plain constructor

/*****************************************************************************
*
*****************************************************************************/
public ElsetParseException(int card, int id, String message, Exception cause) {

    super("Card "+card+" ID "+id+" "+message);
    initCause(cause);

} // end of plain constructor

} // end of ElsetParseException class