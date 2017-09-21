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

package eap.sky.util.numerical.matrix;

import eap.sky.util.numerical.*;

import java.awt.geom.*;

/***********************************************************************
*
***********************************************************************/
public class AffineFit {

LeastSquaresFit fit_x;
LeastSquaresFit fit_y;

/***********************************************************************
*
***********************************************************************/
public AffineFit(int npoints) {

    fit_x = new LeastSquaresFit(npoints, 3);
    fit_y = new LeastSquaresFit(npoints, 3);

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public void add(Point2D point1, Point2D point2) {

    add(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    
} // end of add points method

/***********************************************************************
*
***********************************************************************/
public void add(double x1, double y1, double x2, double y2) {

    fit_x.addFunctionValue(x1);
    fit_x.addFunctionValue(y1);
    fit_x.addFunctionValue(1.0);
    fit_x.addMeasuredValue(x2);
    
    fit_y.addFunctionValue(x1);
    fit_y.addFunctionValue(y1);
    fit_y.addFunctionValue(1.0);
    fit_y.addMeasuredValue(y2);    

} // end of add method

/***********************************************************************
*
***********************************************************************/
public AffineTransform fit() throws NoConvergenceException {

    double[] x = fit_x.fit();
    double[] y = fit_y.fit();
    
    return new AffineTransform(x[0], y[0], x[1], y[1], x[2], y[2]);


} // end of fit method

} // end of AffineFit class
