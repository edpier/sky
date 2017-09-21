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

package eap.sky.earth.atmosphere;

/**************************************************************************
* Water vapor represented as dew point.
* The dew point is the temperature at which water will begin to condense.
***************************************************************************/
public class DewPoint extends WaterVapor {

private double dew_point;

/***************************************************************************
* Construct a new dew point object.
* @param dew_point The dew point in Celsius
* @param ice true if the ground is covered with ice or snow. This is used to
* calculate the saturation water vapor pressure.
* @see #saturation(double)
***************************************************************************/
public DewPoint(double dew_point, boolean ice) {

    super(ice);

    this.dew_point = dew_point;

}

/***************************************************************************
* Returns the relative humidity as a value between 0 and 1.
* Note this requires a conversion from the measured dew point, and is
* not currently implemented. This method always throws an
* {@link UnsupportedOperationException }.
* @return The relative humidity.
* @throws UnsupportedOperationException always.
***************************************************************************/
public double getRelativeHumidity(Weather weather) {

    throw new UnsupportedOperationException("Conversion from dew point to"+
                                         " relative humidity not implemented");

}

/***************************************************************************
*
***************************************************************************/
public WaterVapor interpolate(WaterVapor water_vapor, double hat) {

    DewPoint next = (DewPoint)water_vapor;

    double value = dew_point * (1.0-hat) + next.dew_point * hat;

    return new DewPoint(value, ice && water_vapor.ice);
}

/************************************************************************
*
************************************************************************/
public double molarFraction(double pressure, double celsius) {

    /************************************************
    * calculate the saturation water vapor pressure
    * at the dew point
    ************************************************/
    double saturation = saturation(dew_point);

    /****************************************
    * now the molar fraction of water vapor *
    ****************************************/
    double water_fraction = enhancement(pressure, celsius) * saturation/pressure;

    return water_fraction;

} // end of getFraction method


/************************************************************************
* Converts this objct toa string.
* @return A string representation of this object.
************************************************************************/
public String toString() {

    return "DewPoint "+dew_point+" C";
}


} // end of WaterVapor class
