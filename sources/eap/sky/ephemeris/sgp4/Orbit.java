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
public class Orbit {

double eccentricity;
double inclination;
double mean_motion;
double mean_anomaly;
double arg_perigee;
double ascending_node;

/*****************************************************************************
*
*****************************************************************************/
public double getEccentricity() { return eccentricity; }

/*****************************************************************************
*
*****************************************************************************/
public double getInclination() { return inclination; }

/*****************************************************************************
*
*****************************************************************************/
public double getMeanMotion() { return mean_motion; }

/*****************************************************************************
*
*****************************************************************************/
public double getMeanAnomaly() { return mean_anomaly; }

/*****************************************************************************
*
*****************************************************************************/
public double getArgumentOfPerigee() { return arg_perigee; }

/*****************************************************************************
*
*****************************************************************************/
public double getAscendingNode() { return ascending_node; }

/*****************************************************************************
*
*****************************************************************************/
public void setEccentricity(double eccentricity) {

    this.eccentricity = eccentricity;
}

/*****************************************************************************
*
*****************************************************************************/
public void setInclination(double inclination) {

    this.inclination = inclination;
}

/*****************************************************************************
*
*****************************************************************************/
public void setMeanMotion(double mean_motion) {

    this.mean_motion = mean_motion;
}

/*****************************************************************************
*
*****************************************************************************/
public void setMeanAnomaly(double mean_anomaly) {

    this.mean_anomaly = mean_anomaly;
}

/*****************************************************************************
*
*****************************************************************************/
public void setArgumentOfPerigee(double arg_perigee) {

    this.arg_perigee = arg_perigee;
}

/*****************************************************************************
*
*****************************************************************************/
public void setAscendingNode(double ascending_node) {

    this.ascending_node = ascending_node;
}

/*****************************************************************************
* @return The orbital period in minutes
*****************************************************************************/
public double getPeriod() { return 2.0*Math.PI/mean_motion; }


/*****************************************************************************
*
*****************************************************************************/
public void fixNegativeInclination() {

    if(inclination>=0) return;

    inclination = -inclination;
    ascending_node += Math.PI;
    arg_perigee    -= Math.PI;

} // end of fixNegativeInclination method

} // end of DeepVariables class