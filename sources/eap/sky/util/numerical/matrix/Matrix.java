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

/************************************************************************
*
************************************************************************/
public class Matrix {

int nrows;
int ncols;

double[][] data;

/************************************************************************
*
************************************************************************/
public Matrix(int nrows, int ncols) {

    this.nrows = nrows;
    this.ncols = ncols;

    data = new double[nrows][ncols];

} // end of Matrix class


/************************************************************************
*
************************************************************************/
public Matrix(double[][] data) {

    this.nrows = data.length;
    this.ncols = data[0].length;

    this.data = data;

} // end of constructor from an array

/************************************************************************
*
************************************************************************/
public int getColumnCount() { return ncols; }

/************************************************************************
*
************************************************************************/
public int getRowCount() { return nrows; }

/************************************************************************
*
************************************************************************/
public double[][] getData() { return data; }


/************************************************************************
*
************************************************************************/
public Matrix copy() {

    Matrix copy = new Matrix(nrows, ncols);
    copyDataTo(copy);

    return copy;

} // end of copy method


/************************************************************************
*
************************************************************************/
protected void copyDataTo(Matrix copy) {

    for(int row=0; row<nrows; ++row) {
        System.arraycopy(data[row],0, copy.data[row], 0, ncols);
    }

} // end of copy method

/************************************************************************
*
************************************************************************/
public double[] multiply(double[] column) {

    double[] result = new double[nrows];

    for(int j=0; j<nrows; ++j) {

        double sum = 0.0;
        for(int i=0; i< ncols; ++i) {
            sum += data[j][i] * column[i];
        }

        result[j] = sum;

    } // end of loop over rows

    return result;

} // end of multiply method


/************************************************************************
*
************************************************************************/
public void invert(double[][] rhs) {

    if(nrows != ncols) {
        throw new IllegalStateException("Matrix is not square");
    }

    if(rhs.length != nrows) {
        throw new IllegalArgumentException("Dimension of RHS does not match matrix");
    }

    int nrhs = rhs[0].length;

    int[] indxc= new int[nrows];
    int[] indxr= new int[nrows];
    int[] ipiv = new int[nrows];

    for(int j=0; j<ipiv.length; j++) ipiv[j]=-1;

    int irow = -1;
    int icol = -1;
    for(int i=0;i<nrows;i++) {
        double big=0.0;
        for (int j=0;j<nrows;j++) {
            if (ipiv[j] != 0) {
                for (int k=0;k<nrows;k++) {
                    if (ipiv[k] == -1) {
                        if (Math.abs(data[j][k]) >= big) {
                                big=Math.abs(data[j][k]);
                                irow=j;
                                icol=k;
                        }
                    }
                }
            }
        }

        ++(ipiv[icol]);
        if(irow != icol) {
            for(int l=0;l<nrows;l++) {
                double temp = data[irow][l];
                data[irow][l] = data[icol][l];
                data[icol][l] = temp;
            }

            for(int l=0;l<nrhs;l++) {
                double temp = rhs[irow][l];
                rhs[irow][l] = rhs[icol][l];
                rhs[icol][l] = temp;
            }
        }
        indxr[i]=irow;
        indxc[i]=icol;
        if(data[icol][icol] == 0.0) {
            throw new IllegalStateException("Singular Matrix");
        }

        double pivinv=1.0/data[icol][icol];
        data[icol][icol]=1.0;

        for(int l=0;l<nrows;l++) data[icol][l] *= pivinv;
        for(int l=0;l<nrhs;l++) rhs[icol][l] *= pivinv;
        for(int ll=0;ll<nrows;ll++) {
            if(ll != icol) {
                double dum=data[ll][icol];
                data[ll][icol]=0.0;
                for (int l=0;l<nrows;l++) {
                    data[ll][l] -= data[icol][l]*dum;
                }

                for (int l=0;l<nrhs;l++) {
                    rhs[ll][l] -= rhs[icol][l]*dum;
                }
            }
        }
    }

    /**********************
    * undo all the pivots *
    **********************/
    for(int l=nrows-1;l>=0;l--) {
        if(indxr[l] != indxc[l]) {
            for(int k=0;k<nrows;k++) {
                /*******
                * swap *
                *******/
                double temp = data[k][indxr[l]];
                data[k][indxr[l]] = data[k][indxc[l]];
                data[k][indxc[l]] = temp;
            }
        }
    }


} // end of invert method

} // end of Matrix class
