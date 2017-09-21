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

package eap.sky.earth;

import eap.sky.time.*;

/*****************************************************************************
* The class behind {@link EOPCorrection#Eanes2003}.
*****************************************************************************/
public class  Eanes2003EOPCorrection extends EOPCorrection {

private static EOPCorrection instance;

private static final double[][] sp ={
{0.0298,  0.1408, +0.0805,  0.6002, +0.3025,  0.1517},
{0.0200,  0.0905, +0.0638,  0.3476, +0.1645,  0.0923}};

private static final int[] nj={
2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

private static final int[] mj={
1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

private static final double[] hs={
-1.94, -1.25, -6.64, -1.51, -8.02, -9.47, -50.20, -1.80, -9.54, 1.52, -49.45,
-262.21, 1.70, 3.43, 1.94, 1.37, 7.41, 20.62, 4.14, 3.94, -7.14, 1.37,
-122.03, 1.02, 2.89, -7.30, 368.78, 50.01, -1.08, 2.93, 5.25, 3.95, 20.62,
4.09, 3.42, 1.69, 11.29, 7.23, 1.51, 2.16, 1.38, 1.80, 4.67, 16.01,
19.32, 1.30, -1.02, -4.51, 120.99, 1.13, 22.98, 1.06, -1.90, -2.18, -23.58,
631.92, 1.92, -4.66, -17.86, 4.47, 1.97, 17.20, 294.00, -2.46, -1.02, 79.96,
23.83, 2.59, 4.47, 1.95, 1.17};

private static final double[] phase={
9.0899831, 8.8234208, 12.1189598, 1.4425700, 4.7381090, 4.4715466,
7.7670857, -2.9093042, 0.3862349, -3.1758666, 0.1196725, 3.4152116,
12.8946194, 5.5137686, 6.4441883, -4.2322016, -0.9366625, 8.5427453,
11.8382843, 1.1618945, 5.9693878, -1.2032249, 2.0923141, -1.7847596,
8.0679449, 0.8953321, 4.1908712, 7.4864102, 10.7819493, 0.3137975,
6.2894282, 7.2198478, -0.1610030, 3.1345361, 2.8679737, -4.5128771,
4.9665307, 8.2620698, 11.5576089, 0.6146566, 3.9101957, 20.6617051,
13.2808543, 16.3098310, 8.9289802, 5.0519065, 15.8350306, 8.6624178,
11.9579569, 8.0808832, 4.5771061, 0.7000324, 14.9869335, 11.4831564,
4.3105437, 7.6060827, 3.7290090, 10.6350594, 3.2542086, 12.7336164,
16.0291555, 10.1602590, 6.2831853, 2.4061116, 5.0862033, 8.3817423,
11.6772814, 14.9728205, 4.0298682, 7.3254073, 9.1574019};

private static final double[] freq={
5.18688050, 5.38346657, 5.38439079, 5.41398343, 5.41490765, 5.61149372,
5.61241794, 5.64201057, 5.64293479, 5.83859664, 5.83952086, 5.84044508,
5.84433381, 5.87485066, 6.03795537, 6.06754801, 6.06847223, 6.07236095,
6.07328517, 6.10287781, 6.24878055, 6.26505830, 6.26598252, 6.28318449,
6.28318613, 6.29946388, 6.30038810, 6.30131232, 6.30223654, 6.31759007,
6.33479368, 6.49789839, 6.52841524, 6.52933946, 6.72592553, 6.75644239,
6.76033111, 6.76125533, 6.76217955, 6.98835826, 6.98928248, 11.45675174,
11.48726860, 11.68477889, 11.71529575, 11.73249771, 11.89560406, 11.91188181,
11.91280603, 11.93000800, 11.94332289, 11.96052486, 12.11031632, 12.12363121,
12.13990896, 12.14083318, 12.15803515, 12.33834347, 12.36886033, 12.37274905,
12.37367327, 12.54916865, 12.56637061, 12.58357258, 12.59985198, 12.60077620,
12.60170041, 12.60262463, 12.82880334, 12.82972756, 13.06071921};

private static final double[][] orthow = {
{ -6.77832,-14.86323,  0.47884, -1.45303,  0.16406,  0.42030,
   0.09398, 25.73054, -4.77974,  0.28080,  1.94539, -0.73089},

{ 14.86283, -6.77846,  1.45234,  0.47888, -0.42056,  0.16469,
  15.30276, -4.30615,  0.07564,  2.28321, -0.45717, -1.62010},

{ -1.76335,  1.03364, -0.27553,  0.34569, -0.12343, -0.10146,
  -0.47119,  1.28997, -0.19336,  0.02724,  0.08955,  0.04726}};


private static final int jd1960 = 2437077; // MJD of 1960 ?
private static final double dt = 2.0;
private static final int nmax = 2;

/***************************************************************************
* Construct an new object. This is projected since we only need one of these
* Use {@link EOPCorrection#Eanes2003}
***************************************************************************/
protected Eanes2003EOPCorrection() {}

// /***************************************************************************
// *
// ***************************************************************************/
// public static EOPCorrection getInstance() {
// 
//     if(instance==null) instance = new IERS2003EOPCorrection();
// 
//     return instance;
// }

/****************************************************************************
*
****************************************************************************/
public void correct(EOP eop) {

    /******************************************
    * using the UT1 time - for no good reason *
    ******************************************/
    JulianDate jd = new JulianDate(eop);

    double[][][] anm = new double[2][2][3];
    double[][][] bnm = new double[2][2][3];

    for(int k=0; k<3; ++k) {
        /******************************************
        * get the offset in Julian days from 1960 *
        ******************************************/
        double dt60 = (jd.getNumber() - jd1960) + jd.getFraction() - (k-1)*dt;

//         System.out.println("k="+k+" dt60="+dt60+
//                            " jd="+jd.getJulianDate()+" jd1960="+jd1960);

        for(int j=0; j< nj.length; ++j) {

            int n = nj[j];
            int m = mj[j];

            double pinm = (n+m)%2 * 0.5*Math.PI;

            double alpha = phase[j] + freq[j]*dt60 - pinm;

    //         alpha = Math.IEEEremainder(alpha, 2.0*Math.PI);
    //         if(alpha < 0 ) alpha += 2.0 * Math.PI;


            anm[n-1][m-1][k] +=   hs[j]*Math.cos(alpha);
            bnm[n-1][m-1][k] += - hs[j]*Math.sin(alpha);

        }
    }



    /***********************************
    * orthogonalize the response terms *
    ***********************************/
    for(int m=1; m<=2; ++m) {

        double ap = anm[1][m-1][2] + anm[1][m-1][0];
        double am = anm[1][m-1][2] - anm[1][m-1][0];

        double bp = bnm[1][m-1][2] + bnm[1][m-1][0];
        double bm = bnm[1][m-1][2] - bnm[1][m-1][0];

        double[][] p = new double[3][2];

        p[0][m-1] = sp[m-1][0]*anm[1][m-1][1];
        p[1][m-1] = sp[m-1][1]*anm[1][m-1][1] - sp[m-1][2]*ap;
        p[2][m-1] = sp[m-1][3]*anm[1][m-1][1] - sp[m-1][4]*ap + sp[m-1][5]*bm;

        double[][] q = new double[3][2];

        q[0][m-1] = sp[m-1][0]*bnm[1][m-1][1];
        q[1][m-1] = sp[m-1][1]*bnm[1][m-1][1] - sp[m-1][2]*bp;
        q[2][m-1] = sp[m-1][3]*bnm[1][m-1][1] - sp[m-1][4]*bp - sp[m-1][5]*am;

        anm[1][m-1][0] = p[0][m-1];
        anm[1][m-1][1] = p[1][m-1];
        anm[1][m-1][2] = p[2][m-1];

        bnm[1][m-1][0] = q[0][m-1];
        bnm[1][m-1][1] = q[1][m-1];
        bnm[1][m-1][2] = q[2][m-1];

    }

    /***********************
    * fill partials vector *
    ***********************/
    double[] h = new double[12];
    int index = 0;
    for(int n=2; n<=nmax; ++n) {

        for(int m=1; m<=n; ++m) {
            for(int k=0; k<3; ++k) {

                h[index++] = anm[n-1][m-1][k];
                h[index++] = bnm[n-1][m-1][k];
            }
        }
    }

    /****************************************************
    * apply the orthoweights to get the EOP corrections *
    ****************************************************/
    double[] cor = new double[3];
    for(int k=0; k<3; ++k) {
        cor[k] = 0.0;
        for(int j=0; j<12; ++j) {

            cor[k] += h[j] * orthow[k][j];

        }
    }

    /***************************
    * convert from micro units *
    ***************************/
    for(int i=0; i<cor.length; ++i) cor[i] *= 1e-6;

//     System.out.println("mjd="+jd.getModifiedJulianDate()+
//                        " time correction="+cor[2]);

    /****************************
    * now apply the corrections *
    *****************************/
    apply(eop, cor[2], cor[0], cor[1]);



} // end of correct method



} // end of IERS2003EOPCorrection class
