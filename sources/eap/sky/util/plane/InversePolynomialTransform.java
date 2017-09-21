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
*
*************************************************************************/
public class InversePolynomialTransform extends ParameterlessTransform {

int nx;
int ny;

double[][] poly_x;
double[][] poly_y;

AffineTransform linear;

PolynomialTransform inverse;

/*************************************************************************
*
*************************************************************************/
protected InversePolynomialTransform(PolynomialTransform inverse) {

    this(inverse.poly_x, inverse.poly_y);

    this.inverse = inverse;

} // end of protected constructor

/*************************************************************************
*
*************************************************************************/
public InversePolynomialTransform(double[][] poly_x, double[][] poly_y)
                                            throws IllegalArgumentException {

    this.poly_x = poly_x;
    this.poly_y = poly_y;

    this.ny = poly_x.length;
    this.nx = poly_x[0].length;

    linear = new AffineTransform(poly_x[0][1], poly_y[0][1],
                                 poly_x[1][0], poly_y[1][0],
                                 poly_x[0][0], poly_y[0][0]);

    try { linear = linear.createInverse(); }
    catch(NoninvertibleTransformException e) {
        throw (IllegalArgumentException)
           new IllegalArgumentException("Linear terms cannot be inverted")
           .initCause(e);
    }

} // end of constructor


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
*
*************************************************************************/
// public Mapping getMapping() {
//
//     // this is really cheesy
//     return ((ParameterlessTransform)invert()).getMapping().invert();
//
// } // end of getMapping method

/*************************************************************************
*
*************************************************************************/
public PlaneTransform invert() {

    if(inverse == null) inverse = new PolynomialTransform(poly_x, poly_y);

    return inverse;

} // end of invert method


/*************************************************************************
* Apply the transform.
*************************************************************************/
public void transform(Point2D point, Point2D result) {


    Point2D base = linear.transform(point, null);
    result.setLocation(base.getX(), base.getY());

  //  System.out.println("base="+base);

    Point2D adjustment = new Point2D.Double(0,0);

    for(int iteration=0; iteration < 3; ++iteration) {



        /****************************************
        * pull the coordinates out of the point *
        ****************************************/
        double x = result.getX();
        double y = result.getY();

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

                if(i+j<2) continue;

                double term = yn[j]*xn[i];

                new_x += poly_x[j][i] * term;
                new_y += poly_y[j][i] * term;
            }
        }

        adjustment.setLocation(new_x, new_y);
        linear.deltaTransform(adjustment, adjustment);

        result.setLocation(base.getX()-adjustment.getX(),
                           base.getY()-adjustment.getY() );

//         System.out.println("    "+iteration+" "+result+
//                            " new_x="+new_x+" new_y="+new_y);
      //  System.out.println("    adjustment="+adjustment);



    } // end of loop over iterations


} // end of transform method

} // end of InversePolynomialTransform class
