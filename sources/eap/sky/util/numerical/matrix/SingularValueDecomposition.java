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

/**************************************************************************
*
**************************************************************************/
public class SingularValueDecomposition {

int nrows;
int ncols;

double[][] u;
double[] w;
double[][] v;

/**************************************************************************
*
**************************************************************************/
public SingularValueDecomposition(Matrix matrix) throws NoConvergenceException {

    /*****************************************
    * copy the input matrix to the "u" array *
    *****************************************/
    u = matrix.copy().getData();

    /*************************
    * remember our dimensions *
    **************************/
    nrows = matrix.getRowCount();
    ncols = matrix.getColumnCount();

    /**************************
    * create the other arrays *
    **************************/
    w = new double[ncols];
    v = new double[ncols][ncols];

    double[] temp = new double[ncols];

    /*******************************************
    * householder reduction to bidiagonal form *
    *******************************************/
    double anorm = 0.0;
    double g = 0.0;
    double scale=0.0;
    for(int i=0; i<ncols; i++) {

        temp[i]=scale*g;
        if(Double.isNaN(temp[i])) {
            throw new NoConvergenceException("NaN or overflow in matrix");
        }

        g=scale=0.0;
        if(i<nrows) {

            for(int k=i; k<nrows; k++) scale += Math.abs(u[k][i]);

            if(scale != 0.0) {

                double sum = 0.0;
                for(int k=i; k<nrows; k++) {
                    u[k][i] /= scale;
                    sum += u[k][i]*u[k][i];
                }

                double f=u[i][i];
                g = -sign(Math.sqrt(sum),f);
                double h = f*g-sum;
                u[i][i] = f-g;


                for(int j=i+1; j<ncols; j++) {
                    double sum2 = 0.0;
                    for (int k=i; k<nrows; k++) sum2 += u[k][i]*u[k][j];

                    f=sum2/h;
                    for(int k=i; k<nrows; k++) u[k][j] += f*u[k][i];
                }

                for(int k=i; k<nrows; k++) u[k][i] *= scale;
            }
        } // end if we are in the square

        w[i]=scale *g;
        g=scale=0.0;
        if(i < nrows && i != ncols-1) {

            for(int k=i+1; k<ncols; k++) scale += Math.abs(u[i][k]);

            if(scale != 0.0) {

                double sum = 0.0;
                for(int k=i+1; k<ncols; k++) {
                    u[i][k] /= scale;
                    sum += u[i][k]*u[i][k];
                }

                double f=u[i][i+1];
                g = -sign(Math.sqrt(sum),f);
                double h=f*g-sum;
                u[i][i+1]=f-g;

                for(int k=i+1; k<ncols; k++) temp[k]=u[i][k]/h;

                for(int j=i+1; j<nrows; j++) {
                    double sum2 = 0.0;
                    for(int k=i+1; k<ncols; k++) sum2 += u[j][k]*u[i][k];
                    for(int k=i+1; k<ncols; k++) u[j][k] += sum2*temp[k];
                }

                for(int k=i+1; k<ncols; k++) u[i][k] *= scale;
            }
        }

        anorm=Math.max(anorm,(Math.abs(w[i])+Math.abs(temp[i])));

    } // end of loop over i

    /*********************************************
    * accumulation of right-hand transformations *
    *********************************************/
    v[ncols-1][ncols-1]=1.0;
    g=temp[ncols-1];
    for (int i=ncols-2; i>=0; i--) {

        if (g != 0.0) {

            for(int j=i+1; j<ncols; j++) {
                v[j][i]=(u[i][j]/u[i][i+1])/g;
            }

            for(int j=i+1; j<ncols; j++) {
                double s = 0.0;
                for(int k=i; k<ncols; k++) s += u[i][k]*v[k][j];
                for(int k=i; k<ncols; k++) v[k][j] += s*v[k][i];
            }
        }

        for(int j=i+1; j<ncols; j++) v[i][j]=v[j][i]=0.0;

        v[i][i]=1.0;
        g=temp[i];

    } // end of loop over i


    /********************************************
    * accumulation of left hand transformations *
    ********************************************/
    for(int i=Math.min(nrows,ncols)-1; i>=0; i--) {

        g=w[i];
        for(int j=i+1; j<ncols; j++) u[i][j]=0.0;

        if (g != 0.0) {
            g=1.0/g;
            for(int j=i+1; j<ncols; j++) {

                double sum = 0.0;
                for (int k=i+1; k<nrows; k++) sum += u[k][i]*u[k][j];

                double f=(sum/u[i][i])*g;

                for(int k=i; k<nrows; k++) u[k][j] += f*u[k][i];
            }

            for(int j=i; j<nrows; j++) u[j][i] *= g;

        } else {
            for(int j=i;j<nrows;j++) u[j][i]=0.0;
        }

        ++u[i][i];
    }

    /***********************************************************
    * diagonalization of the bidiagonal form.
    * Loop over singular values, and over allowed iterations
    ***********************************************************/
    for(int k=ncols-1; k>=0; k--) {
        for(int iteration=1; iteration<=30; iteration++) {

            /*************************
            * see if we should split *
            *************************/
            boolean split=true;
            int split_index = k;

            for( ; split_index>=0; split_index--) {
                if(Math.abs(temp[split_index])+anorm == anorm) {
                    split = false;
                    break;
                }

                /************************************************
                * Note we should never get here if split_index==0
                * because temp[0] should always be zero. This can
                * fail if anorm is NaN, but we have put in a check
                * near the top that is supposed to catch this and
                * throw a NoConvergenceException
                *************************************************/
                if(Math.abs(w[split_index-1])+anorm == anorm) break;
            }

            if(split) {
                double c=0.0;
                double s=1.0;
                for(int i=split_index+1; i<=k+1; i++) {

                    double f=s*temp[i-1];
                    temp[i-1] *= c;

                    /*********************************
                    * check if we are done iterating *
                    *********************************/
                    if ((double)(Math.abs(f)+anorm) == anorm) break;

                    g=w[i-1];
                    double h=pythag(f,g);
                    w[i-1]=h;
                    h=1.0/h;
                    c=g*h;
                    s = -f*h;
                    for(int j=1;j<=nrows;j++) {
                        double y=u[j-1][split_index-1];
                        double z=u[j-1][i-1];
                        u[j-1][split_index-1]=y*c+z*s;
                        u[j-1][i-1]=z*c-y*s;
                    }
                }
            } // end if splitting

            double z=w[k];
            if(split_index == k) {
                if (z < 0.0) {
                    w[k] = -z;
                    for (int j=1;j<=ncols;j++) v[j-1][k] = -v[j-1][k];
                }
                break;
            }

            /************************************
            * check if this has gone on too long *
            *************************************/
            if(iteration == 30) {
                throw new NoConvergenceException();
            }

            double x=w[split_index];
            double y=w[k-1];
            g=temp[k-1];
            double h=temp[k];
            double f=((y-z)*(y+z)+(g-h)*(g+h))/(2.0*h*y);
            g=pythag(f,1.0);
            f=((x-z)*(x+z)+h*((y/(f+sign(g,f)))-h))/x;

            double s = 1.0;
            double c = 1.0;

            for(int j=split_index+1; j<=k; j++) {
                g=temp[j];
                y=w[j];
                h=s*g;
                g=c*g;
                z=pythag(f,h);
                temp[j-1]=z;
                c=f/z;
                s=h/z;
                f=x*c+g*s;
                g = g*c-x*s;
                h=y*s;
                y *= c;
                for(int jj=0; jj<ncols; jj++) {
                    x=v[jj][j-1];
                    z=v[jj][j];
                    v[jj][j-1]=x*c+z*s;
                    v[jj][j  ]=z*c-x*s;
                }
                z=pythag(f,h);
                w[j-1]=z;
                if(z != 0.0) {
                    z=1.0/z;
                    c=f*z;
                    s=h*z;
                }
                f=c*g+s*y;
                x=c*y-s*g;
                for(int jj=0; jj<nrows; jj++) {
                    y=u[jj][j-1];
                    z=u[jj][j];
                    u[jj][j-1]=y*c+z*s;
                    u[jj][j  ]=z*c-y*s;
                }
            } // end of loop over j

            temp[split_index]=0.0;
            temp[k]=f;
            w[k]=x;
        }
    }


} // end of constructor

/**************************************************************************
*
**************************************************************************/
private static double pythag(double a, double b) {

    a = Math.abs(a);
    b = Math.abs(b);

    if(a > b)  {
        double ratio = b/a;
        return a*Math.sqrt(1.0 + ratio*ratio);
    } else {
        if(b == 0.0) return 0.0; // both must be zero
        else {
            double ratio = a/b;
            return b*Math.sqrt(1.0 + ratio*ratio);
        }
    }

} // end of pythag method

/**************************************************************************
*
**************************************************************************/
private static double sign(double a, double b) {

    if(b>0.0) return  Math.abs(a);
    else      return -Math.abs(a);

} // end of sign method

/**************************************************************************
*
**************************************************************************/
public void clipSingularValues(double small) {

    /**********************************
    * find the largest singular value
    * note the w's are all >=0
    **********************************/
    double max = w[0];
    for(int i=1; i< ncols; ++i) if(w[i] > max) max = w[i];

    /*****************************************************
    * scale the tollerance by the largest singular value *
    *****************************************************/
    double limit = small * max;

    /**********************************************
    * set anything smaller than the limit to zero *
    **********************************************/
    for(int i=0; i< ncols; ++i) {
        if(w[i] < limit) w[i] = 0.0;

    }


} // end of clipSingularValues

/**************************************************************************
*
**************************************************************************/
public double[] solve(double[] rhs) {


    double[] temp = new double[ncols];
    double[]    x = new double[ncols];

    for(int j=0; j<ncols; ++j) {
        double sum = 0.0;
        if(w[j] != 0.0) {
            for(int i=0; i<nrows; ++i) sum += u[i][j] * rhs[i];
            sum /= w[j];
        }

        temp[j] = sum;

    } // end of loop over rows

    for(int j=0; j<ncols; ++j) {
        double sum = 0.0;
        for(int jj=0; jj<ncols; ++jj) sum += v[j][jj]*temp[jj];
        x[j] = sum;
    }



    return x;

} // end of solve method

} // end of SingularValueDecomposition class
