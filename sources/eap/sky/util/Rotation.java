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

package eap.sky.util;

import java.util.*;

/***************************************************************************
* Represents a three dimensional rotation. Rotations may be represented
* in a number of ways, such as Euler angles, 3x3 matrices, and quaternions.
* This class provides methods for specifying or representing the
* rotation in any of these, causing the class to convert to or from its
* internal representation. Note that converting to and from a given
* representation may introduce small numerical errors. This implementation
* uses quaternions for its internal representation.
***************************************************************************/
public class Rotation extends Transform {

/** the Cartesian X axis **/
public static final int X_AXIS = 0;

/** the Cartesian Y axis **/
public static final int Y_AXIS = 1;

/** the Cartesian Z axis **/
public static final int Z_AXIS = 2;

/** Identity rotation **/
public static final Rotation IDENTITY  = new IdentityRotation();

/** The inverse of this rotation **/
private Rotation inverse;

/** Quaternian components **/
protected double[] q;

protected double[][] m;

/***************************************************************************
* Create a rotation with uninitialized (and therefore invalid) quaternion
* components
* This constructor is private, since it leaves the rotation values
* uninitialized
***************************************************************************/
protected Rotation() {

    q = new double[4];
    inverse = null;

} // end of protected constructor

/************************************************************************
* Returns a unit vector pointing along a particular axis
* This is a utility method used by constructors.
* @param axis one of {@link #X_AXIS}, {@link #Y_AXIS}, or {@link #Z_AXIS}
* @return a unit vector
* @throws IllegalArgumentException if the specified axis is not valid.
************************************************************************/
// private static double[] getAxis(int axis) {
//
//     /**************************************************
//     * create and return the corresponding unit vector *
//     **************************************************/
//     if(     axis == X_AXIS) { double[] vec = {1.0, 0.0, 0.0}; return vec; }
//     else if(axis == Y_AXIS) { double[] vec = {0.0, 1.0, 0.0}; return vec; }
//     else if(axis == Z_AXIS) { double[] vec = {0.0, 0.0, 1.0}; return vec; }
//     else throw new IllegalArgumentException("Invalid axis "+axis);
//
// } // end of getAxis static method

/***************************************************************************
* Create a rotation around a given
* cartesian axis by a given angle
* @param angle The angle by which to rotate in degrees
* @param axis one of {@link #X_AXIS}, {@link #Y_AXIS}, or {@link #Z_AXIS}
* @throws IllegalArgumentException if the specified axis is not valid.
***************************************************************************/
// public Rotation(double angle, int axis) {
//
//     this(angle, getAxis(axis));
//
// } // end of constructor from an angle and cartesian axis

/***************************************************************************
* Create the transform between two coordinates systems which are rotated
* with respect to each other by an angle around an axis.  The angle is
* measured from the original coordinates to the transformed coordinates
* counter-clockwise if the pole is facing toward you.
* @param angle The rotation angle
* @param dir the direction of the axis of rotation.
***************************************************************************/
public Rotation(Angle angle, Direction dir) {

    this();

    /************************************
    * sine and cosine of the half angle *
    ************************************/
    Angle half = angle.half();
    double sin = half.getSin();
    double cos = half.getCos();

    double[] vec = dir.getUnitVector();

    /************************
    * quaternion components *
    ************************/
    q[0] = vec[0] * sin;
    q[1] = vec[1] * sin;
    q[2] = vec[2] * sin;
    q[3] = cos;

  //  System.out.println("angle="+angle+" norm2="+norm2());

} // end of constructor from an angle and arbitrary axis

/***************************************************************************
*
***************************************************************************/
public Rotation(double angle, Direction dir) {

    this(new Angle(angle), dir);
}


/***************************************************************************
* Create a rotation which represents the rotation equivalent to the
* combination of two rotations. Note that the order of arguments is
* reversed from the standard definition of quaternion multiplication.
* @param first the first rotation to appply.
* @param second the second rotation to apply.
***************************************************************************/
public Rotation(Rotation first, Rotation second) {

    this();

    /***************************************************************
    * this is a quaternion product with the argument order swapped *
    ***************************************************************/
//     double[] q2 =  first.q;
//     double[] q1 = second.q;

    double[] q2 =  first.q;
    double[] q1 = second.q;

    q[0] =  q1[3]*q2[0] + q1[2]*q2[1] - q1[1]*q2[2] + q1[0]*q2[3];
    q[1] = -q1[2]*q2[0] + q1[3]*q2[1] + q1[0]*q2[2] + q1[1]*q2[3];
    q[2] =  q1[1]*q2[0] - q1[0]*q2[1] + q1[3]*q2[2] + q1[2]*q2[3];
    q[3] = -q1[0]*q2[0] - q1[1]*q2[1] - q1[2]*q2[2] + q1[3]*q2[3];

  //  System.out.println("norm2 of combined = "+norm2());

} // end of merging constructor

/***************************************************************************
* Create the rotation corresponding to the given set of Euler angles.
* @param euler A set of Euler angles.
***************************************************************************/
public Rotation(Euler euler) {

    this();

    /***************************
    * calculate trig functions *
    ***************************/
    double sinphi=Math.sin(Math.toRadians(euler.getPhi()));
    double cosphi=Math.cos(Math.toRadians(euler.getPhi()));

    double sintheta=Math.sin(Math.toRadians(euler.getTheta()));
    double costheta=Math.cos(Math.toRadians(euler.getTheta()));

    double sinpsi=Math.sin(Math.toRadians(euler.getPsi()));
    double cospsi=Math.cos(Math.toRadians(euler.getPsi()));

    /****************************
    * calculate matrix elements *
    ****************************/
    double m00= cospsi*costheta*cosphi - sinpsi*sinphi;
    double m01= cospsi*costheta*sinphi + sinpsi*cosphi;
    double m10=-sinpsi*costheta*cosphi - cospsi*sinphi;
    double m11=-sinpsi*costheta*sinphi + cospsi*cosphi;

    double m02=-cospsi*sintheta;
    double m12= sinpsi*sintheta;
    double m20=sintheta*cosphi;
    double m21=sintheta*sinphi;

    double m22=costheta;

    /**********************************************
    * convert the matrix elements to a quaternion *
    **********************************************/
    setFromMatrixElements(m00, m01, m02,
                          m10, m11, m12,
                          m20, m21, m22);



} // end of constructor from a set of Euler angles

/***************************************************************************
* Create the rotation cooresponding to a given 3x3 matrix.
* @param matrix a 3x3 rotation matrix, with columns cycling fastest.
* @throws IllegalArgumentException if the matrix is not a valid
* rotation matrix.
***************************************************************************/
public Rotation(double[][] matrix) {

    this();

    /****************************
    * check the first dimension *
    ****************************/
    if(matrix.length != 3) {
        throw new IllegalArgumentException("Not a 3x3 matrix");
    }

    /*****************************
    * check the second dimension *
    *****************************/
    for(int i=0; i<3; ++i) {
        if(matrix[i].length != 3) {
            throw new IllegalArgumentException("Not a 3x3 matrix");
        }
    }

    /****************************************************
    * check that the determanent of the matrix is unity *
    ****************************************************/

    /**************************
    * convert to a quaternion *
    **************************/
    setFromMatrixElements(matrix[0][0], matrix[0][1], matrix[0][2],
                          matrix[1][0], matrix[1][1], matrix[1][2],
                          matrix[2][0], matrix[2][1], matrix[2][2]);

    /***************************************************
    * make sure the resulting quaternion is normalized *
    ***************************************************/
    if(Math.abs(norm2()-1) > 1e-13) {
        throw new IllegalArgumentException("Matrix is not a rotation norm2-1="+
                                           Math.abs(norm2()-1));
    }


} // end of constructor from matrix

/***************************************************************************
* Sets this rotation to one corresponding to the given 3x3 rotation
* matrix elements. This is used by constructors.
***************************************************************************/
private void setFromMatrixElements(double m00, double m01, double m02,
                                   double m10, double m11, double m12,
                                   double m20, double m21, double m22) {

    double[] diag_sum = new double[4];
    diag_sum[0]=1+m00-m11-m22;
    diag_sum[1]=1-m00+m11-m22;
    diag_sum[2]=1-m00-m11+m22;
    diag_sum[3]=1+m00+m11+m22;

    int maxi=0;
    for(int i=1;i<4;++i) {
        if(diag_sum[i]>diag_sum[maxi]) maxi=i;
    }

    q[maxi]=0.5*Math.sqrt(diag_sum[maxi]);
    double recip=1./(4.*q[maxi]);

    if(maxi==0) {
        q[1]=recip*(m01+m10);
        q[2]=recip*(m20+m02);
        q[3]=recip*(m12-m21);

    } else if(maxi==1) {
        q[0]=recip*(m01+m10);
        q[2]=recip*(m12+m21);
        q[3]=recip*(m20-m02);

    } else if(maxi==2) {
        q[0]=recip*(m20+m02);
        q[1]=recip*(m12+m21);
        q[3]=recip*(m01-m10);

    } else if(maxi==3) {
        q[0]=recip*(m12-m21);
        q[1]=recip*(m20-m02);
        q[2]=recip*(m01-m10);
    }

//     double[] diag_sum = new double[4];
//     diag_sum[0]=1+m00-m11-m22;
//     diag_sum[1]=1-m00+m11-m22;
//     diag_sum[2]=1-m00-m11+m22;
//     diag_sum[3]=1+m00+m11+m22;
//
//     int maxi=0;
//     for(int i=1;i<4;++i) {
//         if(diag_sum[i]>diag_sum[maxi]) maxi=i;
//     }
//
//     q[maxi]=0.5*Math.sqrt(diag_sum[maxi]);
//     double recip=1./(4.*q[maxi]);
//
//     if(maxi==0) {
//         q[1]=recip*(m01+m10);
//         q[2]=recip*(m20+m02);
//         q[3]=recip*(m21-m12);
//
//     } else if(maxi==1) {
//         q[0]=recip*(m01+m10);
//         q[2]=recip*(m12+m21);
//         q[3]=recip*(m02-m20);
//
//     } else if(maxi==2) {
//         q[0]=recip*(m20+m02);
//         q[1]=recip*(m12+m21);
//         q[3]=recip*(m10-m01);
//
//     } else if(maxi==3) {
//         q[0]=recip*(m21-m12);
//         q[1]=recip*(m02-m20);
//         q[2]=recip*(m10-m01);
//     }


} // end of setFromMatrixElements method

/***************************************************************************
* Returns the square of the norm of the quaternion. This is a private method
* because we restrict the queternion to always be normalized, so users should
* never need to check this. Also, we are hiding the underlying quaternion
* implementation. However, this method is useful for internal error checking.
***************************************************************************/
public double norm2() {

    return q[0]*q[0] + q[1]*q[1] + q[2]*q[2] + q[3]*q[3];

} // end of norm2 method

/***************************************************************************
* Invert the rotation so that it represents a rotation about the same
* axis but with the opposite sign.
* @return a new Rotation representing the inverse of this one.
***************************************************************************/
public Transform invert() {

    /***************************************************
    * return the inverse if we have already created it *
    ***************************************************/
    if(inverse != null) return inverse;

    /******************************
    * create the inverse rotation *
    ******************************/
    inverse = new Rotation();

    inverse.q[0]=-q[0];
    inverse.q[1]=-q[1];
    inverse.q[2]=-q[2];
    inverse.q[3]= q[3];

    return inverse;

} // end of invert method

/***************************************************************************
*
***************************************************************************/
private double[][] getRotationMatrix() {

    if(m == null) {
        m = new double[3][3];

        double q00 = q[0]*q[0];
        double q11 = q[1]*q[1];
        double q22 = q[2]*q[2];
        double q33 = q[3]*q[3];

        m[0][0] =  q00 - q11 - q22 + q33;
        m[1][1] = -q00 + q11 - q22 + q33;
        m[2][2] = -q00 - q11 + q22 + q33;

        /***************
        * off diagonal *
        ***************/
        double q01 = q[0]*q[1];
        double q23 = q[2]*q[3];
        m[0][1] = 2.0*(q01 + q23);
        m[1][0] = 2.0*(q01 - q23);

        double q02 = q[0]*q[2];
        double q13 = q[1]*q[3];
        m[0][2] = 2.0*(q02 - q13);
        m[2][0] = 2.0*(q02 + q13);

        double q12 = q[1]*q[2];
        double q03 = q[0]*q[3];
        m[1][2] = 2.0*(q12 + q03);
        m[2][1] = 2.0*(q12 - q03);

    } // end if we need to create the matrix

    return m;

} // end of getRotationMatrix method

/***************************************************************************
* Apply the rotation to a set of coordinates.
* @param dir the original coordinates
* @return the rotated coordinates or null if the argument is null.
***************************************************************************/
public Direction transform(Direction dir) {

//System.out.println("\nrotation: orig dir = "+dir);

    if(dir==null) return null;

    /***********************************
    * we're actually rotating a vector *
    ***********************************/
    double[] vec = dir.getUnitVector();

    double[][] m = getRotationMatrix();

    double[] rotated = new double[3];

    rotated[0]=vec[0]*m[0][0] + vec[1]*m[0][1] + vec[2]*m[0][2];
    rotated[1]=vec[0]*m[1][0] + vec[1]*m[1][1] + vec[2]*m[1][2];
    rotated[2]=vec[0]*m[2][0] + vec[1]*m[2][1] + vec[2]*m[2][2];

    return new Direction(rotated);
// OK, quaternions are cool and all, but you get better performance by
// converting to a rotation matrix, provided you are doing multiple
// rotations - which we usually are.
// we could make it user selectable which way to do it. -ED 2008-04-03
//     /***************************************************
//     * cast the vector as a purely imaginary quaternion *
//     ***************************************************/
//     Rotation v = new Rotation();
//     v.q[0] = vec[0];
//     v.q[1] = vec[1];
//     v.q[2] = vec[2];
//     v.q[3] = 0.0;
//
//     /************
//     * do q v q* *
//     ************/
//     Rotation vv = (Rotation)invert().combineWith(v).combineWith(this);
// // Rotation vv = (Rotation)this.combineWith(v).combineWith(invert());
//
//     /********************************
//     * recast the result as a vector *
//     ********************************/
//     double[] rotated = new double[3];
//     rotated[0] = vv.q[0];
//     rotated[1] = vv.q[1];
//     rotated[2] = vv.q[2];
//
//     return new Direction(rotated);

} // end of transform method

/***************************************************************************
* Apply the rotation to a set of coordinates.
* @param dir the original coordinates
* @return the rotated coordinates or null if the argument is null.
***************************************************************************/
public ThreeVector transform(ThreeVector vec) {

    if(vec==null) return null;

    /***********************************
    * we're actually rotating a vector *
    ***********************************/
    double x = vec.getX();
    double y = vec.getY();
    double z = vec.getZ();

    double[][] m = getRotationMatrix();

    double xp = x*m[0][0] + y*m[0][1] + z*m[0][2];
    double yp = x*m[1][0] + y*m[1][1] + z*m[1][2];
    double zp = x*m[2][0] + y*m[2][1] + z*m[2][2];

    return new ThreeVector(xp, yp, zp);

} // end of transform method

/***************************************************************************
* Returns the transform equilavent to first applyign this transform and
* then applying the given one. If the given transform is also a rotation,
* then the result is a Rotation. Otherwsie the result is a
* {@link CompositeTransform}.
* @param trans the transform to apply after this one.
* @return the merged transform.
***************************************************************************/
public Transform combineWith(Transform trans) {



    if(trans instanceof Rotation) {
        /*******************************************************
        * combining two rotations, use the merging constructor *
        *******************************************************/
     // System.out.println("combineing rotation with "+trans.getClass().getName());
        return new Rotation(this, (Rotation)trans);

    } else if(trans instanceof CompositeTransform) {
        /*******************************************************
        * see if the first part of the composite is a rotation *
        *******************************************************/
        // don't know if we need this
        // or even if its sufficient
        CompositeTransform composite = (CompositeTransform)trans;
        Transform first = composite.getFirstTransform();
        if(first instanceof Rotation) {
            return new CompositeTransform(this.combineWith(first),
                                         composite.getSecondTransform());
        }
    }

    /************************
    * just make a composite *
    ************************/
    return super.combineWith(trans);


//     /***********************************************
//     * if it's not another rotation, do the default *
//     ***********************************************/
//     if(! (trans instanceof Rotation)) return super.combineWith(trans);
//
//     /***************************************************
//     * if it is a rotation, use the merging constructor *
//     ***************************************************/
//     return new Rotation(this, (Rotation)trans);

} // end of combineWith method

/***************************************************************************
* Returns the 3x3  matrix represenation of this rotation.
* This matrix has indices of [row][column] and is intended to multiply
* a column vector to apply the transform. In other words:
* <pre>
* transformed[0] = rot[0][0]*orig[0] + rot[0][1]*orig[1] + rot[0][2]*orig[2];
* transformed[0] = rot[1][0]*orig[0] + rot[1][1]*orig[1] + rot[1][2]*orig[2];
* transformed[0] = rot[2][0]*orig[0] + rot[2][1]*orig[1] + rot[2][2]*orig[2];
* </pre>
* @return a new 3x3 rotation matrix
***************************************************************************/
public double[][] getMatrix() {

    double[][] matrix = getRotationMatrix();
    double[][] copy = new double[3][3];

    for(int j=0; j< 3; ++j) {
        System.arraycopy(matrix[j], 0, copy[j], 0, 3);
    }

    return copy;

} // end of getMatrix method

//
//     double[][] rot = new double[3][3];
//
//
//     /********************
//     * diagonal elements *
//     ********************/
//     rot[0][0] =  q[0]*q[0] - q[1]*q[1] - q[2]*q[2] + q[3]*q[3];
//     rot[1][1] = -q[0]*q[0] + q[1]*q[1] - q[2]*q[2] + q[3]*q[3];
//     rot[2][2] = -q[0]*q[0] - q[1]*q[1] + q[2]*q[2] + q[3]*q[3];
//
//     /***************
//     * off diagonal *
//     ***************/
// //     rot[0][1] = 2.*(q[0]*q[1] + q[2]*q[3]);
// //     rot[1][0] = 2.*(q[0]*q[1] - q[2]*q[3]);
// //
// //     rot[0][2] = 2.*(q[0]*q[2] - q[1]*q[3]);
// //     rot[2][0] = 2.*(q[0]*q[2] + q[1]*q[3]);
// //
// //     rot[1][2] = 2.*(q[1]*q[2] + q[0]*q[3]);
// //     rot[2][1] = 2.*(q[1]*q[2] - q[0]*q[3]);
//
//     rot[0][1] = 2.*(q[0]*q[1] + q[2]*q[3]);
//     rot[1][0] = 2.*(q[0]*q[1] - q[2]*q[3]);
//
//     rot[0][2] = 2.*(q[0]*q[2] - q[1]*q[3]);
//     rot[2][0] = 2.*(q[0]*q[2] + q[1]*q[3]);
//
//     rot[1][2] = 2.*(q[1]*q[2] + q[0]*q[3]);
//     rot[2][1] = 2.*(q[1]*q[2] - q[0]*q[3]);
//
//     return rot;
//
//
// } // end of getMatrix method


/***************************************************************************
* Returns the Euler angle representation of this rotation
***************************************************************************/
public Euler getEulerAngles() {

    /*****************
    * matrix element *
    *****************/
    double m22=-q[0]*q[0] - q[1]*q[1] + q[2]*q[2] + q[3]*q[3];

    double phi;
    double theta;
    double psi;

    if(Math.abs(m22)<1.0) {
        /**************
        * normal case *
        **************/
//System.out.println("normal case");
        theta=Math.toDegrees(Math.acos(m22));

        /************************************************************
        * more matrix elements - though we've dropped a factor of 2 *
        ************************************************************/
        double m02=q[0]*q[2] - q[1]*q[3];
        double m20=q[0]*q[2] + q[1]*q[3];

        double m12=q[1]*q[2] + q[0]*q[3];
        double m21=q[1]*q[2] - q[0]*q[3];

        phi=Math.toDegrees(Math.atan2(m21,  m20));
        psi=Math.toDegrees(Math.atan2(m12, -m02));

    } else {
        /**************
        * at the pole *
        **************/
      //  System.out.println("at pole");
        if(m22>0.) theta=0.;
        else       theta=180.0;

        double m00=q[0]*q[0] - q[1]*q[1] - q[2]*q[2] + q[3]*q[3];
        double m10=2.*(q[0]*q[1] + q[2]*q[3]);

        if(m22>0.) phi=Math.toDegrees(Math.atan2( m10, m00));
        else       phi=Math.toDegrees(Math.atan2(-m10,-m00));

        psi = 0.;

    }

    return new Euler(phi, theta, psi);

} // end of getEulerAngles method

/**************************************************************************
*
**************************************************************************/
public double[] getQuaternionComponents() {

    double[] array = new double[4];
    System.arraycopy(q, 0, array, 0, 4);

    return array;
}

/***************************************************************************
* Returns the axis of this rotation. If the angle of the rotation is 0,
* then the axis is undefined, and this method returns the Z axis arbitrarily.
* @return a new Direction pointing along the axis of this rotation.
***************************************************************************/
public Direction getAxis() {

    if(q[3] == 1) return new Direction(0.0, 0.0, 0.0);

    /**************************************************************
    * note here we take advantage of the fact that the
    * Direction constructor takes only the first three elements
    * of the array and normalizes them.
    **************************************************************/
    return new Direction(q);

} // end of getAxis method

/***************************************************************************
* Returns the angle of this rotation in degrees.
***************************************************************************/
public double getAngle() {

    return 2.0* Math.toDegrees(Math.acos(q[3]));


} // end of getAngle method

/***************************************************************************
* Prints the quaternion components corresponding to this rotation.
***************************************************************************/
public String toString() {
    return "Quat["+q[0]+", "+q[1]+", "+q[2]+", "+q[3]+"]";
}

} // end of Rotation class
