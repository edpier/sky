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

/***************************************************************************
* Water vapor represented as relative humidity. Relative humidity is the
* amount of water vapor in the air divided by
* hold. It is commonly writen as a percentage. Here we always use a
* fractional value between zero and one.
***************************************************************************/
public class RelativeHumidity extends WaterVapor {

private double humidity;

/***************************************************************************
* @param humidity The relative humidity expressed as a fraction between
* zero and one.
* @param ice true if the ground is covered with ice or snow. This is used to
* calculate the saturation water vapor pressure.
* @see #saturation(double)
***************************************************************************/
public RelativeHumidity(double humidity, boolean ice) {

    super(ice);

    this.humidity = humidity;

}

/***************************************************************************
* Return the relative humidity as a value between 0 and 1.
* @return The relative humidity.
***************************************************************************/
public double getRelativeHumidity(Weather weather) { return humidity; }

/***************************************************************************
*
***************************************************************************/
public WaterVapor interpolate(WaterVapor water_vapor, double hat) {

    RelativeHumidity next = (RelativeHumidity)water_vapor;

    double value = humidity * (1.0-hat) + next.humidity * hat;

    return new RelativeHumidity(value, ice && water_vapor.ice);
}

/***************************************************************************
*
***************************************************************************/
public double molarFraction(double pressure, double celsius) {

    return humidity * enhancement(pressure, celsius) *
           saturation(celsius)/pressure;
}


/***************************************************************************
*
***************************************************************************/
public String toString() { return 100.0*humidity+"%"; }


} // end of getFraction method
