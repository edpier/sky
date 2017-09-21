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

package eap.sky.util.plane;

import eap.sky.util.*;

import java.awt.geom.*;

/*************************************************************************
* Represents a transform expressed as a pair of two dimensional
* polynomials. The polynomial transform takes no {@link TransformParameter}s
*************************************************************************/
public class PolynomialTransform extends ParameterlessTransform {

int nx;
int ny;

double[][] poly_x;
double[][] poly_y;

PlaneTransform inverse;

/*************************************************************************
* Create a transform given its polynomial coeficients.
* The transform is specified as<br>
* x' = &Sigma;<sub>i,j</sub> poly_x[j][i] x<sup>i</sup> y<sup>j</sup><br>
* y' = &Sigma;<sub>i,j</sub> poly_y[j][i] x<sup>i</sup> y<sup>j</sup>.
*************************************************************************/
public PolynomialTransform(double[][] poly_x, double[][] poly_y) {

    this.poly_x = poly_x;
    this.poly_y = poly_y;

    this.ny = poly_x.length;
    this.nx = poly_x[0].length;

} // end of constructor

/*************************************************************************
*
*************************************************************************/
public Mapping getMapping() {

    if(nx == 2 && ny == 2 && poly_x[1][1] ==0.0 && poly_y[1][1] ==0.0) {
        /******************************
        * this is an affine transform *
        ******************************/
        return new AffineMapping(new AffineTransform(poly_x[0][1], poly_y[0][1],
                                                     poly_x[1][0], poly_y[1][0],
                                                     poly_x[0][0], poly_y[0][0]));
    } else {
        /******************************
        * the transform is non-linear *
        ******************************/
        return super.getMapping();
    }

} // end of getMapping method

/*************************************************************************
* Apply the transform.
*************************************************************************/
public void transform(Point2D point, Point2D result) {

    /****************************************
    * pull the coordinates out of the point *
    ****************************************/
    double x = point.getX();
    double y = point.getY();

    /******************************
    * calculate powers of x and y *
    ******************************/
    double[] xn = powers(x, nx);
    double[] yn = powers(y, ny);

    /*********************
    * sum the polynomial *
    *********************/
    double new_x = 0.0;
    double new_y = 0.0;
    for(int j=0; j<ny; ++j) {
        for(int i=0; i<nx; ++i) {

            double term = yn[j]*xn[i];

            new_x += poly_x[j][i] * term;
            new_y += poly_y[j][i] * term;
        }
    }


    result.setLocation(new_x, new_y);

} // end of transform method


/*************************************************************************
* Calculate all the powers of x up to x<sup>n-1</sup>
* @param x The number to raise to various powers.
* @param n The highest exponent to calculate.
* @return The array {1.0, x, x<sup>2</sup>, ... x<sup>n-1</sup>}
*************************************************************************/
private static double[] powers(double x, int n) {

    /************************
    * calculate powers of x *
    ************************/
    double[] xn = new double[n];
    xn[0] = 1.0;
    xn[1] = x;
    for(int i=2; i<n; ++i) {
        xn[i] = xn[i-1] * x;
    }

    return xn;

} // end of powers method

/*************************************************************************
* Invert this transform. If this tranform is linear, then we can
* invert this analytically. Currently we only handle this case. In the future
* we will need to create a transform class which uses a numerical
* polynomial root-finding algorithm.
* @return the inverse transform, or null if this transform is not linear.
*************************************************************************/
public PlaneTransform invert() {

    if(inverse == null) {

        if(nx == 2 && ny == 2 && poly_x[1][1] ==0.0 && poly_y[1][1] ==0.0) {
            /**************************************************
            * this is a linear transform, so we can invert it *
            **************************************************/
            AffineTransform trans = new AffineTransform(poly_x[0][1],
                                                        poly_y[0][1],
                                                        poly_x[1][0],
                                                        poly_y[1][0],
                                                        poly_x[0][0],
                                                        poly_y[0][0]);

            try { trans = trans.createInverse(); }
            catch(NoninvertibleTransformException e) { return null; }

            double[][] inverse_x = {{trans.getTranslateX(), trans.getScaleX()},
                                    {trans.getShearX(),     0.0}};

            double[][] inverse_y = {{trans.getTranslateY(), trans.getShearY()},
                                    {trans.getScaleY(),     0.0}};

            inverse =  new PolynomialTransform(inverse_x, inverse_y);

        } else {
            /***********************************************
            * we need to invert the polynomial numerically *
            ***********************************************/
            inverse = new InversePolynomialTransform(poly_x, poly_y);

        }

    } // end if we have to create the inverse

    return inverse;

} // end of invert method

} // end of PolynomialTransform class
