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

package eap.sky.earth;

import java.io.*;

/************************************************************************
* Represents an adjustment to the modeled Precession/Nutation
* X and Y parameters.
* @see EOPTable
* @see PrecessionModel
************************************************************************/
public class PrecessionCorrection implements Serializable {

public static final PrecessionCorrection ZERO = new PrecessionCorrection(0,0,0,0);

double dX;
double dY;
double dX_err;
double dY_err;

/************************************************************************
* Create a new precession correction. Note that the values are given in
* milliarcseconds to match the units used in the IERS tables,
* but the precession model return the values in radians, which are the
* units needed to calculate the precession rotation matrix.
* @param dX the correction to the precession X value in milliarcseconds
* @param dY the correction to the precession Y value in milliarcseconds
* @param dX_err the error in dX in milliarcseconds
* @param dY_err the error in dY in milliarcseconds
* @see EOPTable
* @see PrecessionModel
************************************************************************/
public PrecessionCorrection(double dX, double dY,
                             double dX_err, double dY_err) {

    this.dX     = dX;
    this.dY     = dY;
    this.dX_err = dX_err;
    this.dY_err = dY_err;


} // end of constructor

/************************************************************************
* Returns the correction to the precession X value.
* @return the correction to the precession X value in milliarcseconds.
************************************************************************/
public double getXCorrection() { return dX; }

/************************************************************************
* Returns the correction to the precession Y value.
* @return the correction to the precession Y value in milliarcseconds.
************************************************************************/
public double getYCorrection() { return dY; }

/************************************************************************
* Returns the error in the correction to the precession X value.
* @return the error in the correction to the precession X value in
*         milliarcseconds.
************************************************************************/
public double getXCorrectionError() { return dX_err; }

/************************************************************************
* Returns the error in the correction to the precession Y value.
* @return the error in the correction to the precession Y value in
*         milliarcseconds.
************************************************************************/
public double getYCorrectionError() { return dY_err; }

} // end of PolarMotionParameters class
