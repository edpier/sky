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

package eap.sky.util.numerical;

/*************************************************************************
*
*************************************************************************/
public class State {

double x;
double[] y;

double[] error;

/***************************************************************************
*
***************************************************************************/
public State(double x, double[] y) { this(x, y, null); }


/***************************************************************************
*
***************************************************************************/
public State(double x, double[] y, double[] error) {
    this.x = x;
    this.y = y;
    this.error = error;
}

/***************************************************************************
*
***************************************************************************/
public int getDimension() { return y.length; }

/***************************************************************************
*
***************************************************************************/
public double getX() { return x; }

/***************************************************************************
*
***************************************************************************/
public double getY(int index) { return y[index]; }

/***************************************************************************
*
***************************************************************************/
public void getY(double[] copy) {
    System.arraycopy(y,0, copy,0, y.length);
}

/***************************************************************************
*
***************************************************************************/
public double getError(int index) { return error[index]; }

/***************************************************************************
*
***************************************************************************/
public String toString() {

    return x + " "+y[0];
}

} // end of State class
