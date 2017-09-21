// Copyright 2013 Edward Alan Pier
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

package eap.sky.ephemeris.sgp4;

/*****************************************************************************
*
*****************************************************************************/
public class DeepSpace {

private static final double TWOPI = 2.0*Math.PI;

// deep common stuff

double s1, s2, s3, s4, s5, s6, s7;
double ss1, ss2, ss3, ss4, ss5, ss6, ss7;
double z1, z2, z3;

double z11, z12, z13;
double z21, z22, z23;
double z31, z32, z33;

double sz1, sz2, sz3;

double sz11, sz12, sz13;
double sz21, sz22, sz23;
double sz31, sz32, sz33;

double cosim, sinim;

private double gsto;

protected int irez;
protected double d2201;
protected double d2211;
protected double d3210;
protected double d3222;
protected double d4410;
protected double d4422;
protected double d5220;
protected double d5232;
protected double d5421;
protected double d5433;
protected double dedt;
protected double del1;
protected double del2;
protected double del3;
protected double didt;
protected double dmdt;
protected double dnodt;
protected double domdt;
protected double e3;
protected double ee2;
protected double peo;
protected double pgho;
protected double pho;
protected double pinco;
protected double plo;
protected double se2;
protected double se3;
protected double sgh2;
protected double sgh3;
protected double sgh4;
protected double sh2;
protected double sh3;
protected double si2;
protected double si3;
protected double sl2;
protected double sl3;
protected double sl4;

protected double xfact;
protected double xgh2;
protected double xgh3;
protected double xgh4;
protected double xh2;
protected double xh3;
protected double xi2;
protected double xi3;
protected double xl2;
protected double xl3;
protected double xl4;
protected double xlamo;
protected double zmol;
protected double zmos;

/**************************************************************************
*
**************************************************************************/
public DeepSpace() {

} // end of constructor

/************************************************************************
*
************************************************************************/
public void init(double epoch, SGP4Propagator nevalues) {

    init1(epoch, nevalues.orb0);

    /**********************************************
    * GST at epoch (really earth rotation angle?) *
    **********************************************/
    double gsto = gstime(epoch + 2433281.5);

    init2(nevalues.orb0, gsto,
          nevalues.mdot,
          nevalues.omegadot,
          nevalues.omegadot + nevalues.argpdot);

} // end of init method

/************************************************************************
* This procedure provides deep space common items used by both the secular
* and periodics subroutines.
* Formerly known as dscom
*
* @author david vallado  719-573-2600    1 mar 2001
**************************************************************************/
public void init1(double epoch, Orbit orb0) {

    /* -------------------------- constants ------------------------- */
    final double zes = 0.01675;
    final double zel = 0.05490;
    final double c1ss = 2.9864797e-6;
    final double c1l = 4.7968065e-7;
    final double zsinis = 0.39785416;
    final double zcosis = 0.91744867;
    final double zcosgs = 0.1945905;
    final double zsings = -0.98088458;

    double argpp  = orb0.getArgumentOfPerigee();
    double inclp  = orb0.getInclination();
    double omegap = orb0.getAscendingNode();

    double nm = orb0.getMeanMotion();
    double em = orb0.getEccentricity();

    double snodm = Math.sin(omegap);
    double cnodm = Math.cos(omegap);

    double sinomm = Math.sin(argpp);
    double cosomm = Math.cos(argpp);

    sinim = Math.sin(inclp);
    cosim = Math.cos(inclp);

    double emsq = em * em;
    double betasq = 1.0 - emsq;
    double rtemsq = Math.sqrt(betasq);

    /* ----------------- initialize lunar solar terms --------------- */
    peo = 0.0;
    pinco = 0.0;
    plo = 0.0;
    pgho = 0.0;
    pho = 0.0;

    double day = epoch + 18261.5;
    double xnodce = modTwoPi(4.5236020 - 9.2422029e-4 * day);
    double stem = Math.sin(xnodce);
    double ctem = Math.cos(xnodce);

    double zcosil = 0.91375164 - 0.03568096*ctem;
    double zsinil = Math.sqrt(1.0 - zcosil*zcosil);
    double zsinhl = 0.089683511*stem/zsinil;
    double zcoshl = Math.sqrt(1.0 - zsinhl*zsinhl);
    double gam = 5.8351514 + 0.0019443680*day;
    double zx = 0.39785416*stem/zsinil;
    double zy = zcoshl * ctem + 0.91744867* zsinhl*stem;
    zx = Math.atan2(zx, zy);
    zx = gam + zx - xnodce;
    double zcosgl = Math.cos(zx);
    double zsingl = Math.sin(zx);

    /* ------------------------- do solar terms --------------------- */
    double zcosg = zcosgs;
    double zsing = zsings;
    double zcosi = zcosis;
    double zsini = zsinis;
    double zcosh = cnodm;
    double zsinh = snodm;
    double cc = c1ss;
    double xnoi = 1.0/nm;

    for (int lsflg = 1; lsflg <= 2; lsflg++) {

        double a1  =  zcosg*zcosh + zsing*zcosi*zsinh;
        double a3  = -zsing*zcosh + zcosg*zcosi*zsinh;
        double a7  = -zcosg*zsinh + zsing*zcosi*zcosh;
        double a8  =  zsing*zsini;
        double a9  =  zsing*zsinh + zcosg*zcosi*zcosh;
        double a10 =  zcosg*zsini;
        double a2  =  cosim*a7 + sinim*a8;
        double a4  =  cosim*a9 + sinim*a10;
        double a5  = -sinim*a7 + cosim*a8;
        double a6  = -sinim*a9 + cosim*a10;

        double x1 =  a1*cosomm + a2*sinomm;
        double x2 =  a3*cosomm + a4*sinomm;
        double x3 = -a1*sinomm + a2*cosomm;
        double x4 = -a3*sinomm + a4*cosomm;
        double x5 =  a5*sinomm;
        double x6 =  a6*sinomm;
        double x7 =  a5*cosomm;
        double x8 =  a6*cosomm;

        z31 = 12.0*x1*x1 - 3.0*x3*x3;
        z32 = 24.0*x1*x2 - 6.0*x3*x4;
        z33 = 12.0*x2*x2 - 3.0*x4*x4;

        z1 = 3.0*(a1*a1 + a2*a2) + z31*emsq;
        z2 = 6.0*(a1*a3 + a2*a4) + z32*emsq;
        z3 = 3.0*(a3*a3 + a4*a4) + z33*emsq;

        z11 = -6.0*a1*a5 + emsq*(-24.0*x1*x7 - 6.0*x3*x5);
        z12 = -6.0*(a1*a6 + a3*a5) +
              emsq*(-24.0*(x2*x7 + x1*x8) - 6.0 * (x3*x6 + x4*x5));

        z13 = -6.0*a3*a6 + emsq*(-24.0*x2*x8 - 6.0*x4*x6);
        z21 = 6.0*a2*a5 + emsq*(24.0*x1*x5 - 6.0*x3*x7);
        z22 = 6.0*(a4*a5 + a2*a6) +
              emsq*(24.0*(x2*x5 + x1*x6) - 6.0*(x4*x7 + x3*x8));

        z23 = 6.0*a4*a6 + emsq*(24.0*x2*x6 - 6.0*x4*x8);

        z1 = z1 + z1 + betasq*z31;
        z2 = z2 + z2 + betasq*z32;
        z3 = z3 + z3 + betasq*z33;

        s3 = cc*xnoi;
        s2 = -0.5*s3/rtemsq;
        s4 = s3*rtemsq;
        s1 = -15.0*em*s4;
        s5 = x1*x3 + x2*x4;
        s6 = x2*x3 + x1*x4;
        s7 = x2*x4 - x1*x3;

        /* ----------------------- do lunar terms ------------------- */
        if (lsflg == 1) {
            ss1 = s1;
            ss2 = s2;
            ss3 = s3;
            ss4 = s4;
            ss5 = s5;
            ss6 = s6;
            ss7 = s7;
            sz1 = z1;
            sz2 = z2;
            sz3 = z3;
            sz11 = z11;
            sz12 = z12;
            sz13 = z13;
            sz21 = z21;
            sz22 = z22;
            sz23 = z23;
            sz31 = z31;
            sz32 = z32;
            sz33 = z33;

            zcosg = zcosgl;
            zsing = zsingl;

            zcosi = zcosil;
            zsini = zsinil;

            zcosh = zcoshl*cnodm  + zsinhl*snodm;
            zsinh =  snodm*zcoshl -  cnodm*zsinhl;

            cc = c1l;

        } // end if we include lunar/solar terms
    } // end of loop over whether we include lunar/solar terms or not

    zmol = modTwoPi(4.7199672 + 0.22997150 *day - gam);
    zmos = modTwoPi(6.2565837 + 0.017201977*day);

    /*******************
    * init solar terms *
    *******************/
    se2 =    2.0*ss1*ss6;
    se3 =    2.0*ss1*ss7;
    si2 =    2.0*ss2*sz12;
    si3 =    2.0*ss2*(sz13 - sz11);
    sl2 =   -2.0*ss3*sz2;
    sl3 =   -2.0*ss3*(sz3 - sz1);
    sl4 =   -2.0*ss3*(-21.0 - 9.0*emsq)*zes;
    sgh2 =   2.0*ss4*sz32;
    sgh3 =   2.0*ss4*(sz33 - sz31);
    sgh4 = -18.0*ss4*zes;
    sh2 =   -2.0*ss2*sz22;
    sh3 =   -2.0*ss2*(sz23 - sz21);

    /*******************
    * init lunar terms *
    *******************/
    ee2 =    2.0*s1*s6;
    e3 =     2.0*s1*s7;
    xi2 =    2.0*s2*z12;
    xi3 =    2.0*s2*(z13 - z11);
    xl2 =   -2.0*s3*z2;
    xl3 =   -2.0*s3*(z3 - z1);
    xl4 =   -2.0*s3*(-21.0 - 9.0*emsq)*zel;
    xgh2 =   2.0*s4*z32;
    xgh3 =   2.0*s4*(z33 - z31);
    xgh4 = -18.0*s4*zel;
    xh2 =   -2.0*s2*z22;
    xh3 =   -2.0*s2*(z23 - z21);

} // end of init1 method


/**********************************************************************
* Provides deep space contributions to mean motion dot due
* to geopotential resonance with half day and one day orbits.
*
*  author        : david vallado                  719-573-2600    1 mar 2001
*
************************************************************************/
public void init2(Orbit orb0, double gsto, double mdot,
                  double omegadot, double xpidot) {

    this.gsto = gsto;

    double argpo  = orb0.getArgumentOfPerigee();
    double mo     = orb0.getMeanAnomaly();
    double no     = orb0.getMeanMotion();
    double omegao = orb0.getAscendingNode();
    double ecco   = orb0.getEccentricity();
    double inclo  = orb0.getInclination();

    double eccsq = ecco*ecco;
    double emsq = eccsq;

    double em = ecco;

    /* --------------------- local variables ------------------------ */

    final double q22    = 1.7891679e-6;
    final double q31    = 2.1460748e-6;
    final double q33    = 2.2123015e-7;
    final double root22 = 1.7891679e-6;
    final double root44 = 7.3636953e-9;
    final double root54 = 2.1765803e-9;
    final double rptim  = 4.37526908801129966e-3;
    final double root32 = 3.7393792e-7;
    final double root52 = 1.1428639e-7;
    final double x2o3   = 2.0/3.0;
    final double xke    = 7.43669161331734132e-2;
    final double znl    = 1.5835218e-4;
    final double zns    = 1.19459e-5;

    /* -------------------- deep space initialization ------------ */
    irez = 0;
    if ((no < 0.0052359877) && (no > 0.0034906585)) {
        irez = 1;
    }

    if ((no >= 8.26e-3) && (no <= 9.24e-3) && (em >= 0.5)) {
        irez = 2;
    }

    /* ------------------------ do solar terms ------------------- */
    double ses  =  zns*ss1*ss5;
    double sis  =  zns*ss2*(sz11 + sz13);
    double sls  = -zns*ss3*(sz1 + sz3 - 14.0 - 6.0 * emsq);
    double sghs =  zns*ss4*(sz31 + sz33 - 6.0);
    double shs  = -zns*ss2*(sz21 + sz23);

    if(inclo < 5.2359877e-2) shs = 0.0;
    if(sinim != 0.0)  shs = shs/sinim;
    double sgs = sghs - cosim*shs;

    /* ------------------------- do lunar terms ------------------ */
    dedt        = ses + znl*s1*s5;
    didt        = sis + znl*s2*(z11 + z13);
    dmdt        = sls - znl*s3*(z1 + z3 - 14.0 - 6.0 * emsq);
    double sghl =       znl*s4*(z31 + z33 - 6.0);
    double shll =      -znl*s2*(z21 + z23);
    if(inclo < 5.2359877e-2) shll = 0.0;

    domdt = sgs + sghl;
    dnodt = shs;

    if (sinim != 0.0) {
        domdt = domdt - cosim/sinim*shll;
        dnodt = dnodt + shll /sinim;
    }

    /* ----------- calculate deep space resonance effects -------- */
    double theta = modTwoPi(gsto); // is this even necessary?

    /* -------------- initialize the resonance terms ------------- */

    double aonv = 0.0;
    if(irez != 0) aonv = Math.pow(no / xke, x2o3);

    /* ---------- geopotential resonance for 12 hour orbits ------ */
    if(irez == 2) {
        double cosisq = cosim * cosim;
        double emo = em;
        em = ecco;
        double emsqo = emsq;
        emsq = eccsq;
        double eoc = em * emsq;
        double g201 = -0.306 - (em - 0.64) * 0.440;

        double g200, g211;
        double g300, g310, g322;
        double       g410, g422;
        double g520,       g521, g532, g533;
        if (em <= 0.65) {
            g211 = 3.616 - 13.2470 * em + 16.2900 * emsq;
            g310 = -19.302 + 117.3900 * em - 228.4190 * emsq + 156.5910
                    * eoc;
            g322 = -18.9068 + 109.7927 * em - 214.6334 * emsq + 146.5816
                    * eoc;
            g410 = -41.122 + 242.6940 * em - 471.0940 * emsq + 313.9530
                    * eoc;
            g422 = -146.407 + 841.8800 * em - 1629.014 * emsq + 1083.4350
                    * eoc;
            g520 = -532.114 + 3017.977 * em - 5740.032 * emsq + 3708.2760
                    * eoc;
        } else {
            g211 = -72.099 + 331.819 * em - 508.738 * emsq + 266.724 * eoc;
            g310 = -346.844 + 1582.851 * em - 2415.925 * emsq + 1246.113
                    * eoc;
            g322 = -342.585 + 1554.908 * em - 2366.899 * emsq + 1215.972
                    * eoc;
            g410 = -1052.797 + 4758.686 * em - 7193.992 * emsq + 3651.957
                    * eoc;
            g422 = -3581.690 + 16178.110 * em - 24462.770 * emsq
                    + 12422.520 * eoc;

            if (em > 0.715) {
                g520 = -5149.66 + 29936.92 * em - 54087.36 * emsq
                        + 31324.56 * eoc;
            } else {
                g520 = 1464.74 - 4664.75 * em + 3763.64 * emsq;
            }
        }
        if (em < 0.7) {
            g533 = -919.22770 + 4988.6100 * em - 9064.7700 * emsq + 5542.21
                    * eoc;
            g521 = -822.71072 + 4568.6173 * em - 8491.4146 * emsq
                    + 5337.524 * eoc;
            g532 = -853.66600 + 4690.2500 * em - 8624.7700 * emsq + 5341.4
                    * eoc;
        } else {
            g533 = -37995.780 + 161616.52 * em - 229838.20 * emsq
                    + 109377.94 * eoc;
            g521 = -51752.104 + 218913.95 * em - 309468.16 * emsq
                    + 146349.42 * eoc;
            g532 = -40023.880 + 170470.89 * em - 242699.48 * emsq
                    + 115605.82 * eoc;
        }

        double sini2 = sinim * sinim;
        double f220 = 0.75 * (1.0 + 2.0 * cosim + cosisq);
        double f221 = 1.5 * sini2;
        double f321 = 1.875 * sinim * (1.0 - 2.0 * cosim - 3.0 * cosisq);
        double f322 = -1.875 * sinim * (1.0 + 2.0 * cosim - 3.0 * cosisq);
        double f441 = 35.0 * sini2 * f220;
        double f442 = 39.3750 * sini2 * sini2;
        double f522 = 9.84375
                * sinim
                * (sini2 * (1.0 - 2.0 * cosim - 5.0 * cosisq) + 0.33333333 * (-2.0
                        + 4.0 * cosim + 6.0 * cosisq));
        double f523 = sinim*(4.92187512*sini2*(-2.0 - 4.0*cosim + 10.0*cosisq) +
                             6.56250012*(1.0 + 2.0*cosim - 3.0*cosisq));
        double f542 = 29.53125
                * sinim
                * (2.0 - 8.0 * cosim + cosisq
                        * (-12.0 + 8.0 * cosim + 10.0 * cosisq));
        double f543 = 29.53125
                * sinim
                * (-2.0 - 8.0 * cosim + cosisq
                        * (12.0 + 8.0 * cosim - 10.0 * cosisq));
        double xno2 = no*no;
        double ainv2 = aonv * aonv;
        double temp1 = 3.0 * xno2 * ainv2;
        double temp = temp1 * root22;
        d2201 = temp * f220 * g201;
        d2211 = temp * f221 * g211;
        temp1 = temp1 * aonv;
        temp = temp1 * root32;
        d3210 = temp * f321 * g310;
        d3222 = temp * f322 * g322;
        temp1 = temp1 * aonv;
        temp = 2.0 * temp1 * root44;
        d4410 = temp * f441 * g410;
        d4422 = temp * f442 * g422;
        temp1 = temp1 * aonv;
        temp = temp1 * root52;
        d5220 = temp * f522 * g520;
        d5232 = temp * f523 * g532;
        temp = 2.0 * temp1 * root54;
        d5421 = temp * f542 * g521;
        d5433 = temp * f543 * g533;
        xlamo = modTwoPi(mo + omegao + omegao - theta - theta);
        xfact = mdot + dmdt + 2.0
                * (omegadot + dnodt - rptim) - no;
        em = emo;
        emsq = emsqo;
    }

    /* ---------------- synchronous resonance terms -------------- */
    if (irez == 1) {
        double g200 = 1.0 + emsq * (-2.5 + 0.8125 * emsq);
        double g310 = 1.0 + 2.0 * emsq;
        double g300 = 1.0 + emsq * (-6.0 + 6.60937 * emsq);
        double f220 = 0.75 * (1.0 + cosim) * (1.0 + cosim);
        double f311 = 0.9375 * sinim * sinim * (1.0 + 3.0 * cosim) - 0.75
                * (1.0 + cosim);
        double f330 = 1.0 + cosim;
        f330 = 1.875 * f330 * f330 * f330;
        del1 = 3.0 * no*no * aonv * aonv;
        del2 = 2.0 * del1 * f220 * g200
                * q22;
        del3 = 3.0 * del1 * f330 * g300
                * q33 * aonv;
        del1 = del1 * f311 * g310 * q31
                * aonv;
        xlamo = modTwoPi(mo + omegao + argpo - theta);
        xfact = mdot + xpidot - rptim
                + dmdt + domdt
                + dnodt - no;
    }

    /* ------------ for sgp4, initialize the integrator ---------- */
// at one time the resonance integration was done incrementally, so this
// was necessary, but that seems to have been removed before I got
// to the code. At some point we should put incremental integration
// back in, but we won't worry about it for now.
//     if (irez != 0) {
//         xli = xlamo;
//         xni = no;
//        // atime = 0.0;
//
//     }

} // end of init2 method

/***************************************************************************
* This Method provides deep space contributions to mean elements for
* perturbing third body. These effects have been averaged over one
* revolution of the sun and moon. For earth resonance effects, the
* effects have been averaged over no revolutions of the satellite.
* (mean motion)
*
*  @author David Vallado 719-573-2600
* 1 mar 2001
*
----------------------------------------------------------------------------*/
public void dspace(double argpo, double argpdot, double t, double no,
                   Orbit input, Orbit output) {

    double em     = input.getEccentricity();
    double inclm  = input.getInclination();
    double nm     = input.getMeanMotion();
    double mm     = input.getMeanAnomaly();
    double argpm  = input.getArgumentOfPerigee();
    double omegam = input.getAscendingNode();

    double xldot = 0.0; // need to check on this one
    double xndt=0.0, xnddt = 0.0;

    final double fasx2 = 0.13130908;
    final double fasx4 = 2.8843198;
    final double fasx6 = 0.37448087;
    final double g22 = 5.7686396;
    final double g32 = 0.95240898;
    final double g44 = 1.8014998;
    final double g52 = 1.0508330;
    final double g54 = 4.4108898;
    final double rptim = 4.37526908801129966e-3;


    /* ----------- calculate deep space resonance effects ----------- */
    double theta = modTwoPi(gsto + t*rptim);
    em += dedt * t;

    inclm  +=  didt * t;
    argpm  += domdt * t;
    omegam += dnodt * t;
    mm     +=  dmdt * t;

    /*********************************************************
    * resonances.
    * We do a numerical integration here. It looks like at one
    * point the original code was set up to do the integral
    * incrementally from the point where it last left off.
    * That seems to have been turned off by the time we got to
    * the code, allowing use to rip out quite a bit of weird
    * machinery devoted to it. But for efficiency's sake it
    * would be nice to put that back in at some point.
    *********************************************************/
    if (irez != 0) {
        /***************************
        * figure out the step size *
        ***************************/
        final double step = 720.0;
        final double step2 = 0.5*step*step;

        double delt;
        if (t > 0.0) delt =  step;
        else         delt = -step;

        double xni = no;
        double xli = xlamo;

        double atime = 0.0;
        while(true) {

            /**************************
            * compute the derivatives *
            **************************/
            if (irez != 2) {
                /* ----------- near - synchronous resonance terms ------- */
                xndt = del1*Math.sin(     xli - fasx2) +
                       del2*Math.sin(2.0*(xli - fasx4)) +
                       del3*Math.sin(3.0*(xli - fasx6));

                xldot = xni + xfact;

                xnddt =      del1*Math.cos(       xli - fasx2) +
                         2.0*del2*Math.cos(2.0 * (xli - fasx4)) +
                         3.0*del3*Math.cos(3.0 * (xli - fasx6));

                xnddt *= xldot;

            } else {
                /* --------- near - half-day resonance terms -------- */
                double xomi = argpo + argpdot * atime;
                double x2omi = xomi + xomi;
                double x2li = xli + xli;

                xndt = d2201*Math.sin(x2omi + xli - g22)
                     + d2211*Math.sin(xli - g22)
                     + d3210*Math.sin(xomi + xli - g32)
                     + d3222*Math.sin(-xomi + xli - g32)
                     + d4410*Math.sin(x2omi + x2li - g44)
                     + d4422*Math.sin(x2li - g44)
                     + d5220*Math.sin(xomi + xli - g52)
                     + d5232*Math.sin(-xomi + xli - g52)
                     + d5421*Math.sin(xomi + x2li - g54)
                     + d5433*Math.sin(-xomi + x2li - g54);

                xldot = xni + xfact;

                xnddt = d2201*Math.cos(x2omi + xli - g22)
                      + d2211*Math.cos(xli - g22)
                      + d3210*Math.cos(xomi + xli - g32)
                      + d3222*Math.cos(-xomi + xli - g32)
                      + d5220*Math.cos(xomi + xli - g52)
                      + d5232*Math.cos(-xomi + xli - g52)
                 + 2.0*(d4410*Math.cos(x2omi + x2li - g44) + d4422
                             *Math.cos(x2li - g44) + d5421
                             *Math.cos(xomi + x2li - g54) + d5433
                             *Math.cos(-xomi + x2li - g54));

                xnddt *= xldot;
            }

            if (Math.abs(t - atime) < step) {
                /*************************************
                * no more full steps so we are done.
                * ft is the overshoot
                *************************************/
                double ft = t - atime;

                xni +=  xndt*ft + xnddt*ft*ft*0.5;
                xli += xldot*ft +  xndt*ft*ft*0.5;

                break;

            } else {
                xli += xldot*delt +  xndt*step2;
                xni +=  xndt*delt + xnddt*step2;

                atime += delt;
            }
        } // end of loop

        /********************************
        * add in the last remaining bit *
        ********************************/
        nm = xni;

        if(irez != 1) mm = xli - 2.0*omegam +     2.0*theta;
        else          mm = xli -     omegam - argpm + theta;

    } // end if we have resonances

    /************************
    * set the return values *
    ************************/
    output.setEccentricity(em);
    output.setInclination(inclm);
    output.setMeanMotion(nm);
    output.setMeanAnomaly(mm);
    output.setArgumentOfPerigee(argpm);
    output.setAscendingNode(omegam);

} // end of dspace method

/**************************************************************************
* Provides deep space long period periodic contributions
* to the mean elements.  By design, these periodics are zero at epoch.
* This used to be dscom, but it's doubly a recurring function.
*
* @author David Vallado 719-573-2600    1 mar 2001
*
***************************************************************************/
public void dpper(double t, Orbit input, Orbit output) {

    double ep     = input.getEccentricity();
    double xincp  = input.getInclination();
    double argpp  = input.getArgumentOfPerigee();
    double omegap = input.getAscendingNode();
    double mp     = input.getMeanAnomaly();
    double np     = input.getMeanMotion(); // not used here

    double zns = 1.19459e-5;
    double zes = 0.01675;
    double znl = 1.5835218e-4;
    double zel = 0.05490;

    /* --------------- calculate time varying periodics ----------- */
    double zm = zmos + zns * t;

    double zf = zm + 2.0 * zes * Math.sin(zm);
    double sinzf = Math.sin(zf);
    double f2 = 0.5 * sinzf * sinzf - 0.25;
    double f3 = -0.5 * sinzf * Math.cos(zf);
    double ses = se2 * f2 + se3 * f3;
    double sis = si2 * f2 + si3 * f3;
    double sls = sl2 * f2 + sl3 * f3 + sl4 * sinzf;
    double sghs = sgh2 * f2 + sgh3 * f3 + sgh4 * sinzf;
    double shs = sh2 * f2 + sh3 * f3;

    zm = zmol + znl * t;

    zf = zm + 2.0 * zel * Math.sin(zm);
    sinzf = Math.sin(zf);
    f2 = 0.5 * sinzf * sinzf - 0.25;
    f3 = -0.5 * sinzf * Math.cos(zf);

    double sel = ee2 * f2 + e3 * f3;
    double sil = xi2 * f2 + xi3 * f3;
    double sll = xl2 * f2 + xl3 * f3 + xl4 * sinzf;
    double sghl = xgh2 * f2 + xgh3 * f3 + xgh4 * sinzf;
    double shll = xh2 * f2 + xh3 * f3;
    double pe = ses + sel;
    double pinc = sis + sil;
    double pl = sls + sll;
    double pgh = sghs + sghl;
    double ph = shs + shll;


    /*********************************************************
    * Set this up for lyddane fix per psc.
    * These are routine calls (init = 0)
    * do before the inclination is changed
    * 0.2 rad = 11.45916 deg
    * sgp4fix for lyddane choice
    * af80 version checked original inclination
    * add next three lines to set up original inclination
    * ildm = 'y';
    * if (inclo >= 0.2)
    * ildm = 'n';
    *******************************************************/
    pe   -= peo;
    pinc -= pinco;
    pl   -= plo;
    pgh  -= pgho;
    ph   -= pho;

    xincp += pinc;
    ep    += pe;

    double sinip = Math.sin(xincp);
    double cosip = Math.cos(xincp);

    /********************************************************************
    * Apply periodics directly
    * could change this for the other side (pi - 0.2, but it affects
    * other tests
    * sgp4fix for lyddane choice
    * use this for original af80 approach and original inclination
    * we believe this is technically correct
    * if (ildm = 'n')
    * use this for perturbed inclination value per gsfc version
    * if (inclp >= 0.2)
    ********************************************************************/
    if (xincp >= 0.2) { // JMC

        ph  /= sinip;
        pgh -= cosip * ph;
        argpp  += pgh;
        omegap += ph;
        mp     += pl;

    } else {
        /********************************************
        * apply periodics with lyddane modification *
        ********************************************/
        double sinop = Math.sin(omegap);
        double cosop = Math.cos(omegap);

        double alfdp = sinip * sinop;
        double betdp = sinip * cosop;

        double dalf =  ph * cosop + pinc * cosip * sinop;
        double dbet = -ph * sinop + pinc * cosip * cosop;

        alfdp = alfdp + dalf;
        betdp = betdp + dbet;

        omegap = modTwoPi(omegap);

        double xls = mp + argpp + cosip*omegap + pl + pgh - pinc*omegap*sinip;

        double xnoh = omegap;
        omegap = Math.atan2(alfdp, betdp);

        if(Math.abs(xnoh - omegap) > Math.PI) {
            if(omegap < xnoh) omegap += TWOPI;
            else              omegap -= TWOPI;
        }

        mp = mp + pl;
        argpp = xls - mp - cosip * omegap;

    } // end if we are using Lyddane modification

    /************************
    * set the return values *
    ************************/
    output.setEccentricity(ep);
    output.setInclination(xincp);
    output.setMeanAnomaly(mp);
    output.setArgumentOfPerigee(argpp);
    output.setAscendingNode(omegap);
    output.setMeanMotion(np); // not actually used here


} // end of dpper method


/***************************************************************************
*
***************************************************************************/
private static double gstime(double jdut1) {

    double tut1 = (jdut1 - 2451545.0)/36525.0;
    double temp = -6.2e-6*tut1*tut1*tut1 + 0.093104*tut1*tut1
            + (876600.0*3600 + 8640184.812866)*tut1 + 67310.54841; // sec

    temp = modTwoPi(Math.toRadians(temp)/240.0); // 360/86400 = 1/240, to
    // deg, to rad

    /* ------------------------ check quadrants --------------------- */
    if (temp < 0.0) temp += TWOPI;

    return temp;

} // end of gstime method

/*************************************************************************
*
*************************************************************************/
private static double modTwoPi(double x) {

    return x - (int)(x/TWOPI)*TWOPI;

} // end of modTwoPi method

} // end of DeepSpaceType class
