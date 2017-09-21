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
import eap.sky.util.*;

/***************************************************************************
*
***************************************************************************/
public class NutationValues {

double t;
Angle mean_obliquity;
Angle epsilon;
Angle psi;

/***************************************************************************
*
***************************************************************************/
public NutationValues(double t,
                      Angle mean_obliquity,
                      Angle epsilon,
                      Angle psi) {

    this.t              = t;
    this.mean_obliquity = mean_obliquity;
    this.epsilon        = epsilon;
    this.psi            = psi;

} // end of constructor

/***************************************************************************
* @returns the number of Julian centuries since 2012-01-01 12:00:00 TT.
***************************************************************************/
public double getTime() { return t; }

/***************************************************************************
*
***************************************************************************/
public Angle getMeanObliquity() { return mean_obliquity; }

/***************************************************************************
*
***************************************************************************/
public Angle getEpsilon() { return epsilon; }

/***************************************************************************
*
***************************************************************************/
public Angle getPsi() { return psi; }

} // end of NutationValues class

