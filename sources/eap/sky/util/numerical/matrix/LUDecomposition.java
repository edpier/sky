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

/***************************************************************************
*
***************************************************************************/
public class LUDecomposition extends Matrix {

int n;
int[] index;

/***************************************************************************
*
***************************************************************************/
public LUDecomposition(Matrix matrix) throws SingularMatrixException {

    super(matrix.getRowCount(), matrix.getColumnCount());
    if(nrows != ncols) {
        throw new IllegalArgumentException("Matrix not square");
    }
    matrix.copyDataTo(this);

    n = nrows;
    index = new int[n];

    /****************************
    * perform the decomposition *
    ****************************/
    double[] vv = new double[nrows];

    for(int i=0;i<n;i++) {
        double big=0.0;
        for(int j=0;j<n;j++) {
            double abs = Math.abs(data[i][j]);
            if(abs > big) big=abs;
        }
        if(big==0.0) throw new SingularMatrixException();

        vv[i]=1.0/big;
    }


    for(int j=0;j<n;j++) {
        for(int i=0;i<j;i++) {
            double sum=data[i][j];
            for(int k=0;k<i;k++) {
                sum -= data[i][k]*data[k][j];
            }

            data[i][j]=sum;
        }

        double big=0.0;
        int imax = -1;
        for(int i=j;i<n;i++) {

            double sum=data[i][j];
            for (int k=0;k<j;k++) {
                sum -= data[i][k]*data[k][j];
            }
            data[i][j]=sum;

            double dum = vv[i]*Math.abs(sum);
            if(dum >= big) {
                big=dum;
                imax=i;
            }
        }

        if(j != imax) {
            // could probably just swap pointers
            for(int k=0;k<n;k++) {
                double dum=data[imax][k];
                data[imax][k]=data[j][k];
                data[j][k]=dum;
            }
            vv[imax]=vv[j];
        }
        index[j]=imax;

        if(data[j][j] == 0.0) {
            throw new SingularMatrixException();
        }
        double dum=1.0/(data[j][j]);
        for(int i=j+1;i<n;i++) {
            data[i][j]=data[i][j]*dum;
        }
    }


} // end of constructor

/************************************************************************
*
************************************************************************/
public double[] solve(double[] rhs) {

    if(rhs.length != n) {
        throw new IllegalArgumentException("RHS dimension does not match");
    }

    double[] b = new double[n];
    System.arraycopy(rhs, 0, b, 0, n);

    int ii=-1;
    for(int i=0;i<n;i++) {
        int ip=index[i];
        double sum=b[ip];
        b[ip]=b[i];
        if(ii>-1) {
            for(int j=ii;j<=i-1;j++) {
                sum -= data[i][j]*b[j];
            }
        } else if(sum>0.0) {
            ii=i;
        }
        b[i]=sum;
    }

    for(int i=n-1;i>=0;i--) {
        double sum=b[i];
        for (int j=i+1;j<n;j++) {
            sum -= data[i][j]*b[j];
        }
        b[i]=sum/data[i][i];
    }

    return b;

} // end of solve method

} // end of LUDecomposition class
