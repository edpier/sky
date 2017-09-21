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

package eap.sky.test;

import eap.sky.earth.*;
import eap.sky.ephemeris.*;
import eap.sky.ephemeris.cached.*;
import eap.sky.ephemeris.sgp4.*;
import eap.sky.time.*;
import eap.sky.time.barycenter.*;
import eap.sky.util.*;


/***************************************************************************
*
***************************************************************************/
public class TestTLE {

/***************************************************************************
*
***************************************************************************/
public static void testGCRFtoMEME() throws Exception {

    Test.initTime();

    /*******
    * time *
    *******/
    PreciseDate time = UTCSystem.getInstance()
                                .createFormat()
                                .parsePreciseDate("2013-03-15 00:00:00 UTC");

    /************
    * constants *
    ************/
    double ZETA_1 = 2306.2181;
    double ZETA_2 =    0.30188;
    double ZETA_3 =    0.017998;

    double THETA_1 = 2004.3109;
    double THETA_2 =   -0.42665;
    double THETA_3 =   -0.041833;

    double Z_1 = 2306.2181;
    double Z_2 =    1.09468;
    double Z_3 =    0.018203;

    TimeSystem TT = TTSystem.getInstance();
    PreciseDate epoch = TT.createFormat()
                          .parsePreciseDate("2000-01-01 12:00:00 TT");

    /***********************************
    * Julian centuries since 2000.0 TT *
    ***********************************/
    double t = time.secondsAfter(epoch)/(36525.0*86400.0);

  //  System.out.println("julian centuries="+t);

    Angle zeta  = Angle.createFromArcsec((( ZETA_3*t +  ZETA_2)*t +  ZETA_1)*t);
    Angle theta = Angle.createFromArcsec(((THETA_3*t + THETA_2)*t + THETA_1)*t);
    Angle z     = Angle.createFromArcsec(((    Z_3*t +     Z_2)*t +     Z_1)*t);

    System.out.println("z="+z.getArcsec()+
                       " theta="+theta.getArcsec()+
                       " zeta="+zeta.getArcsec());

    Rotation rot1 = new Rotation(z.negative(),    Direction.Z_AXIS);
    Rotation rot2 = new Rotation(theta,           Direction.Y_AXIS);
    Rotation rot3 = new Rotation(zeta.negative(), Direction.Z_AXIS);

    Transform trans = rot3.combineWith(rot2).combineWith(rot1);

    ThreeVector vec = new ThreeVector(0.0, 0.0, 1.0);
    Direction dir = vec.getDirection();
    dir = trans.transform(dir);

    vec = new ThreeVector(dir, vec.getLength());
    System.out.println(vec.getX()+" "+vec.getY()+" "+vec.getZ());



} // end of testGCRFtoMEME method

/***************************************************************************
*
***************************************************************************/
public static void testMEMEtoTEME() throws Exception {

    Test.initTime();

    /*******
    * time *
    *******/
    PreciseDate time = UTCSystem.getInstance()
                                .createFormat()
                                .parsePreciseDate("2013-03-15 00:00:00 UTC");

    /************
    * constants *
    ************/
    TimeSystem TT = TTSystem.getInstance();
    PreciseDate epoch = TT.createFormat()
                          .parsePreciseDate("2000-01-01 12:00:00 TT");

    // Coefficients for the Mean Obliquity of the Ecliptic.
    double MOE_0 = 84381.448;
    double MOE_1 =   -46.8150;
    double MOE_2 =    -0.00059;
    double MOE_3 =     0.001813;


    /***********************************
    * Julian centuries since 2000.0 TT *
    ***********************************/
    double t = time.secondsAfter(epoch)/(36525.0*86400.0);

    /**********************************************
    * compute nutation using IAU 1980 conventions *
    **********************************************/
    Nutation1980 nutation = new Nutation1980();
    NutationValues nut = nutation.compute(t);

    Angle epsilon = nut.getEpsilon();
    Angle psi     = nut.getPsi();

    /******************************************
    * mean and true obliquity of the ecliptic *
    ******************************************/
    Angle moe = Angle.createFromArcsec(((MOE_3*t + MOE_2)*t + MOE_1)*t + MOE_0);
    Angle toe = moe.plus(epsilon);


        System.out.println("moe="+moe.getArcsec()+
                      " psi="+psi.getArcsec()+
                      " epsilon="+psi.getArcsec());

    /*************************
    * assemble the rotations *
    *************************/
    Rotation rot1 = new Rotation(toe.negative(), Direction.X_AXIS);
    Rotation rot2 = new Rotation(psi.negative(), Direction.Z_AXIS);
    Rotation rot3 = new Rotation(moe, Direction.X_AXIS);

    Transform trans = rot3.combineWith(rot2).combineWith(rot1);

    //Transform trans = rot1.combineWith(rot2.combineWith(rot3));
   // Transform trans = rot1.combineWith(rot2.combineWith(rot3));

    ThreeVector vec = new ThreeVector(1.0, 2.0, 3.0);
    Direction dir = vec.getDirection();
    dir = trans.transform(dir);

    vec = new ThreeVector(dir, vec.getLength());
    System.out.println(vec.getX()+" "+vec.getY()+" "+vec.getZ());

} // end of testMEMEtoTEME method

/***************************************************************************
*
***************************************************************************/
public static void testNutation1980() throws Exception {

    Test.initTime();

    /*******
    * time *
    *******/
    PreciseDate time = UTCSystem.getInstance()
                                .createFormat()
                                .parsePreciseDate("2013-03-15 00:00:00 UTC");

    /************
    * constants *
    ************/
    TimeSystem TT = TTSystem.getInstance();
    PreciseDate epoch = TT.createFormat()
                          .parsePreciseDate("2000-01-01 12:00:00 TT");

    /***********************************
    * Julian centuries since 2000.0 TT *
    ***********************************/
    double t = time.secondsAfter(epoch)/(36525.0*86400.0);

    /**********************************************
    * compute nutation using IAU 1980 conventions *
    **********************************************/
    Nutation1980 nutation = new Nutation1980();
    NutationValues nut = nutation.compute(t);

    TEMEFrame teme = new TEMEFrame(false, "yikes!");
    teme.computeNutationElements(time.secondsAfter(epoch));


} // end of testNutation1980 method


/***************************************************************************
*
***************************************************************************/
public static void testRotation() {

    Rotation rot1 = new Rotation(Angle.createFromRadians(1.0).negative(), Direction.Z_AXIS);
    Rotation rot2 = new Rotation(Angle.createFromRadians(0.5).negative(), Direction.Y_AXIS);

    Transform rot = rot2.combineWith(rot1);

    Direction dir = Direction.X_AXIS;
    Direction v1 = rot.transform(dir);

    System.out.println("x="+v1.getX()+" y="+v1.getY()+" z="+v1.getZ());


} // end of testRotation method

/***************************************************************************
*
***************************************************************************/
public static void testAdeptPrecession() throws Exception {

    Test.initTime();

    /*******
    * time *
    *******/
    PreciseDate time = UTCSystem.getInstance()
                                .createFormat()
                                .parsePreciseDate("2013-03-15 00:00:00 UTC");

    /************
    * constants *
    ************/
    TimeSystem TT = TTSystem.getInstance();
    PreciseDate epoch = TT.createFormat()
                          .parsePreciseDate("2000-01-01 12:00:00 TT");

    /***********************************
    * Julian centuries after the epoch *
    ***********************************/
    double t = time.secondsAfter(epoch)/(36525.0*86400.0);

    double t0   = 0.0;
    double t0_2 = t0 * t0;

    double t1   = t;
    double t1_2 = t1 * t1;
    double t1_3 = t1_2 * t1;

    // 'zeta', 'zee', 'theta' below correspond to Lieske's
    //  "zeta-sub-a", "z-sub-a", and "theta-sub-a"

    Angle zeta=Angle.createFromArcsec(
                  (2306.2181  + 1.39656 *t0 - 0.000139*t0_2)*t1
                + (   0.30188 - 0.000344*t0                )*t1_2
                +                                  0.017998*t1_3);


    Angle zee = Angle.createFromArcsec(
                 (2306.2181  + 1.39656*t0 - 0.000139*t0_2)*t1
                + (  1.09468 + 0.000066*t0               )*t1_2
                +                                 0.018203*t1_3);

    Angle theta = Angle.createFromArcsec(
                   (2004.3109  - 0.85330*t0 - 0.000217*t0_2)*t1
                   + (-0.42665 - 0.000217*t0               )*t1_2
                                                  - 0.041833*t1_3);

    System.out.println("t1="+t1+" t1_2="+t1_2+" t1_3="+t1_3);

    System.out.println("t="+t);
    System.out.println("zee="+(zee.getArcsec())+
                       " theta="+theta.getArcsec()+
                       " zeta="+zeta.getArcsec());/*

    // Convert from arcseconds to radians

    zeta  *= C::arcsec;
    zee   *= C::arcsec;
    theta *= C::arcsec;

    // Precalculate trigometric terms

    double cosZeta = cos (zeta);
    double sinZeta = sin (zeta);
    double cosTheta = cos (theta);
    double sinTheta = sin (theta);
    double cosZ = cos (zee);
    double sinZ = sin (zee);

    // Create precession rotation matrix

    this->_precessXX =  cosZeta * cosTheta * cosZ - sinZeta * sinZ;
    this->_precessYX = -sinZeta * cosTheta * cosZ - cosZeta * sinZ;
    this->_precessZX = -sinTheta * cosZ;
    this->_precessXY =  cosZeta * cosTheta * sinZ + sinZeta * cosZ;
    this->_precessYY = -sinZeta * cosTheta * sinZ + cosZeta * cosZ;
    this->_precessZY = -sinTheta * sinZ;
    this->_precessXZ =  cosZeta * sinTheta;
    this->_precessYZ = -sinZeta * sinTheta;
    this->_precessZZ =  cosTheta;


    // Perform rotation with precession matrix

    double x, y, z;
    double xp, yp, zp;

    stateVec.getPosition (x, y, z);
    xp = this->_precessXX * x + this->_precessYX * y + this->_precessZX * z;
    yp = this->_precessXY * x + this->_precessYY * y + this->_precessZY * z;
    zp = this->_precessXZ * x + this->_precessYZ * y + this->_precessZZ * z;
    stateVec.setPosition (xp, yp, zp);

    stateVec.getVelocity (x, y, z);
    xp = this->_precessXX * x + this->_precessYX * y + this->_precessZX * z;
    yp = this->_precessXY * x + this->_precessYY * y + this->_precessZY * z;
    zp = this->_precessXZ * x + this->_precessYZ * y + this->_precessZZ * z;
    stateVec.setVelocity (xp, yp, zp);*/

}

/***************************************************************************
*
***************************************************************************/
public static void testAdeptNutation() throws Exception {

    Test.initTime();

    /*******
    * time *
    *******/
    PreciseDate time = UTCSystem.getInstance()
                                .createFormat()
                                .parsePreciseDate("2013-03-15 00:00:00 UTC");

    /************
    * constants *
    ************/
    TimeSystem TT = TTSystem.getInstance();
    PreciseDate epoch = TT.createFormat()
                          .parsePreciseDate("2000-01-01 12:00:00 TT");

    /***********************************
    * Julian centuries after the epoch *
    ***********************************/
    double t  = time.secondsAfter(epoch)/(36525.0*86400.0);
    double t2 = t * t;
    double t3 = t2 * t;

    //  Compute mean obliquity in radians (aka moe)
    Angle mean_obliquity = Angle.createFromArcsec(84381.448
                                                   - 46.815   *t
                                                    - 0.00059 *t2
                                                    + 0.001813*t3);

    // Compute the arguments of the nutation series in radians
    double [] a = new double[5];

    a[0] = 2.3555483935439407 + t * (8328.691422883896
                              + t * (1.517951635553957e-4
                              + 3.1028075591010306e-7 * t));

    a[1] = 6.240035939326023 + t * (628.3019560241842
                             + t * (-2.7973749400020225e-6
                             - 5.817764173314431e-8 * t));

    a[2] = 1.6279019339719611 + t * (8433.466158318453
                              + t * (-6.427174970469119e-5
                              + 5.332950492204896e-8 * t));

    a[3] = 5.198469513579922 + t * (7771.377146170642
                             + t * (-3.340851076525812e-5
                             + 9.211459941081184e-8 * t));

    a[4] = 2.1824386243609943 + t * (-33.75704593375351
                              + t * (3.614285992671591e-5
                              + 3.878509448876288e-8 * t));

    double ap, dp, de;

    ap  = a[4];                                     // Term 1
    dp  = (-171996 - 174.2*t)*Math.sin(ap);
    de  = (92025   +   8.9*t)*Math.cos(ap);

    ap  = 2.0 * a[4];                               // Term 2
    dp += (2062 + 0.2 * t) * Math.sin(ap);
    de += (-895 + 0.5 * t) * Math.cos(ap);

    ap  = -2.0 * a[0] + 2.0 * a[2] + a[4];          // Term 3
    dp += 46 * Math.sin(ap);
    de -= 24 * Math.cos(ap);

    ap  = 2.0 * a[0] - 2.0 * a[2];                  // Term 4
    dp += 11 * Math.sin(ap);

    // Convert longitude and obliquity of the nutation to radians

    Angle psi     = Angle.createFromArcsec(dp*1e-4);
    Angle epsilon = Angle.createFromArcsec(de*1e-4);

    System.out.println("moe="+mean_obliquity.getArcsec()+
                      " psi="+psi.getArcsec()+
                      " epsilon="+psi.getArcsec());

    dp *= 1e-4/3600.0;
    dp = Math.toRadians(dp);

    de *= 1e-4/3600.0;
    de = Math.toRadians(de);

    double trueMeanXZ =  dp * mean_obliquity.getSin();
    double trueMeanYZ =  de;
    double trueMeanZX = -dp * mean_obliquity.getSin();
    double trueMeanZY = -de;


    double x=1.0, y=2.0, z=3.0;
    double xm, ym, zm;

    xm =            x +                trueMeanZX*z;
    ym =                           y + trueMeanZY*z;
    zm = trueMeanXZ*x + trueMeanYZ*y +            z;

    double[][] matrix = new double[3][3];
    matrix[0][0] = 1.0;
    matrix[0][1] = 0.0;
    matrix[0][2] = trueMeanZX;

    matrix[1][0] = 0.0;
    matrix[1][1] = 1.0;
    matrix[1][2] = trueMeanZY;

    matrix[2][0] = trueMeanXZ;
    matrix[2][1] = trueMeanYZ;
    matrix[2][2] = 1.0;

    System.out.println("adept matrix:");
    for(int j=0; j<3; ++j) {
        for(int i=0; i<3; ++i) {
            System.out.print(matrix[j][i]+" ");
        }
        System.out.println();
    }
    System.out.println();

    System.out.println("adept: x="+xm+" y="+ym+" z="+zm);

    System.out.println("pre: "+(x*x+y*y+z*z)+
                       " post: "+(xm*xm+ym*ym+zm*zm));

   // stateVec.setPosition (xm, ym, zm);

    /************************************
    * construct rotation the OREkit way *
    ************************************/
    Angle toe = mean_obliquity.plus(epsilon);

    System.out.println("moe="+mean_obliquity);
    System.out.println("toe="+toe);

    Rotation rot1 = new Rotation(toe.negative(), Direction.X_AXIS);
    Rotation rot2 = new Rotation(psi.negative(), Direction.Z_AXIS);
    Rotation rot3 = new Rotation(mean_obliquity, Direction.X_AXIS);

    Transform trans = rot3.combineWith(rot2).combineWith(rot1);

    /******************
    * dump the matrix *
    ******************/
    Rotation rot = (Rotation)trans;
    double[][] mat = rot.getMatrix();

    System.out.println("orekit matrix:");
    for(int j=0; j<3; ++j) {
        for(int i=0; i<3; ++i) {
            System.out.print(mat[j][i]+" ");
        }
        System.out.println();
    }
    System.out.println();

    /**************************************
    * transform using the rotation matrix *
    **************************************/
    ThreeVector vec = new ThreeVector(1.0, 2.0, 3.0);
    Direction dir = vec.getDirection();
    dir = trans.transform(dir);

    vec = new ThreeVector(dir, vec.getLength());
    System.out.println(vec.getX()+" "+vec.getY()+" "+vec.getZ());

    System.out.println("arcsec difference="+new ThreeVector(xm, ym, zm).getDirection()
                                                  .angleBetween(vec.getDirection()).getArcsec());

    ThreeVector orekit = new ThreeVector(0.9998005216159433,
                                         2.0001381177748545,
                                         2.9999744043575696);

    System.out.println("vs orekit: "+orekit.getDirection()
                                           .angleBetween(vec.getDirection()).getArcsec());

} // end of testAdeptNutation method

/***************************************************************************
*
***************************************************************************/
public static void testSGP4Ephemeris() throws Exception {

    TDBSystem.setDefaultTDBModel(ZeroTDBModel.getInstance());
    Test.initTime();

    /**************
    * Observatory *
    **************/
    Observatory observatory = Test.initObservatory(204.530566, 19.822991, 4213.6);

    /********************
    * TLE for Galaxy 15 *
    ********************/
    String card1 = "1 28884U 05041A   13077.43391420  .00000064  00000-0  00000+0 0  4730";
    String card2 = "2 28884 000.0160 008.3641 0001298 006.2832 184.6988 01.00273091 27200";
    TLE tle = new TLE(card1, card2);



    Precession1980 precession = new Precession1980(TTSystem.getInstance());
    Nutation nutation = new eap.sky.ephemeris.sgp4.Nutation1980();
    NutationTransform nut_trans = new NutationRotation();



    Ephemeris ephemeris = new SGP4Ephemeris(UT1System.getInstance(),
                                            tle, precession, nutation,
                                            nut_trans);


    PreciseDate time0 = UTCSystem.getInstance()
                                .createFormat()
                                .parsePreciseDate("2013-03-15 00:00:00 UTC");

    long before = System.currentTimeMillis();
    for(int j=0; j<30; ++j) {
        PreciseDate time = time0.copy();

        for(int i=0; i<30; ++i, time.increment(600.0)) {
            PreciseDate tdb = TDBSystem.getInstance().convertDate(time);
            EOP eop   = (EOP)(UT1System.getInstance().convertDate(time));


            Direction dir = ephemeris.position(-1, tdb, eop, observatory)
                                    .getDirection();
            long after = System.currentTimeMillis();



        // System.out.println(time+" "+dir.getLongitude()+" "+dir.getLatitude());

        } // end of loop over times
    } // end of loop over observatories

    long after = System.currentTimeMillis();
    System.out.println((after-before)+" ms");

} // end of testSGP4Ephemeris method

/***************************************************************************
*
***************************************************************************/
public static void testCachedSGP4Ephemeris() throws Exception {

    TDBSystem.setDefaultTDBModel(ZeroTDBModel.getInstance());
    Test.initTime();

    /**************
    * Observatory *
    **************/
    Observatory observatory = Test.initObservatory(204.530566, 19.822991, 4213.6);

    /********************
    * TLE for Galaxy 15 *
    ********************/
    String card1 = "1 28884U 05041A   13077.43391420  .00000064  00000-0  00000+0 0  4730";
    String card2 = "2 28884 000.0160 008.3641 0001298 006.2832 184.6988 01.00273091 27200";
    TLE tle = new TLE(card1, card2);

    Precession1980 precession = new Precession1980(TTSystem.getInstance());
    Nutation nutation = new eap.sky.ephemeris.sgp4.Nutation1980();
    NutationTransform nut_trans = new NutationRotation();



    Ephemeris ephemeris = new SGP4Ephemeris(UT1System.getInstance(),
                                            tle, precession, nutation,
                                            nut_trans);

    CachedEphemeris cache = new CachedEphemeris(UT1System.getInstance());

    cache.addInterpolationTable(new NoInterpolation(ephemeris, Ephemeris.EARTH,
                                                    VectorType.POSITION));

    cache.addInterpolationTable(new HashInterpolationTable(ephemeris, -1,
                                                           VectorType.POSITION,
                                                           new PolarMethod(),
                                                           1000.0));

    ephemeris = cache;

    long before = System.currentTimeMillis();

    PreciseDate time0 = UTCSystem.getInstance()
                                .createFormat()
                                .parsePreciseDate("2013-03-15 00:00:00 UTC");

    for(int j=0; j<30; ++j) {
        PreciseDate time = time0.copy();


        for(int i=0; i<30; ++i, time.increment(600.0)) {
            PreciseDate tdb = TDBSystem.getInstance().convertDate(time);
            EOP eop   = (EOP)(UT1System.getInstance().convertDate(time));


            Direction dir = ephemeris.position(-1, tdb, eop, observatory)
                                    .getDirection();




        // System.out.println(time+" "+dir.getLongitude()+" "+dir.getLatitude());

        } // end of loop over times
    } // end of loop over observatories

    long after = System.currentTimeMillis();
    System.out.println((after-before)+" ms");

} // end of testCachedSGP4Ephemeris method

/***************************************************************************
*
***************************************************************************/
public static void testCachedSGP4Ephemeris2() throws Exception {

    TDBSystem.setDefaultTDBModel(ZeroTDBModel.getInstance());
    Test.initTime();

    /**************
    * Observatory *
    **************/
    Observatory observatory = Test.initObservatory(204.530566, 19.822991, 4213.6);

    /********************
    * TLE for Galaxy 15 *
    ********************/
    String card1 = "1 28884U 05041A   13077.43391420  .00000064  00000-0  00000+0 0  4730";
    String card2 = "2 28884 000.0160 008.3641 0001298 006.2832 184.6988 01.00273091 27200";
    TLE tle = new TLE(card1, card2);

    Precession1980 precession = new Precession1980(TTSystem.getInstance());
    Nutation nutation = new eap.sky.ephemeris.sgp4.Nutation1980();
    NutationTransform nut_trans = new NutationRotation();



    Ephemeris ephemeris = new SGP4Ephemeris(UT1System.getInstance(),
                                            tle, precession, nutation,
                                            nut_trans);

    CachedEphemeris cache = new CachedEphemeris(UT1System.getInstance());

    cache.addInterpolationTable(new NoInterpolation(ephemeris, Ephemeris.EARTH,
                                                    VectorType.POSITION));

    cache.addInterpolationTable(new GridInterpolationTable(ephemeris, -1,
                                                           VectorType.POSITION,
                                                           new PolarMethod(),
                                                           3600.0, 10));

    ephemeris = cache;

    long before = System.currentTimeMillis();

    PreciseDate time0 = UTCSystem.getInstance()
                                .createFormat()
                                .parsePreciseDate("2013-03-15 00:00:00 UTC");

    for(int j=0; j<30; ++j) {
        PreciseDate time = time0.copy();


        for(int i=0; i<30; ++i, time.increment(600.0)) {
            PreciseDate tdb = TDBSystem.getInstance().convertDate(time);
            EOP eop   = (EOP)(UT1System.getInstance().convertDate(time));

            Direction dir = ephemeris.position(-1, tdb, eop, observatory)
                                    .getDirection();




        // System.out.println(time+" "+dir.getLongitude()+" "+dir.getLatitude());

        } // end of loop over times
    } // end of loop over observatories

    long after = System.currentTimeMillis();
    System.out.println((after-before)+" ms");

} // end of testCachedSGP4Ephemeris method

/***************************************************************************
*
***************************************************************************/
public static void testGridAccuracy() throws Exception {

    TDBSystem.setDefaultTDBModel(ZeroTDBModel.getInstance());
    Test.initTime();

    /**************
    * Observatory *
    **************/
    Observatory observatory = Test.initObservatory(204.530566, 19.822991, 4213.6);

    /********************
    * TLE for Galaxy 15 *
    ********************/
    String card1 = "1 28884U 05041A   13077.43391420  .00000064  00000-0  00000+0 0  4730";
    String card2 = "2 28884 000.0160 008.3641 0001298 006.2832 184.6988 01.00273091 27200";
    TLE tle = new TLE(card1, card2);

    Precession1980 precession = new Precession1980(TTSystem.getInstance());
    Nutation nutation = new eap.sky.ephemeris.sgp4.Nutation1980();
    NutationTransform nut_trans = new NutationRotation();



    Ephemeris ephemeris = new SGP4Ephemeris(UT1System.getInstance(),
                                            tle, precession, nutation,
                                            nut_trans);

    CachedEphemeris cache = new CachedEphemeris(UT1System.getInstance());

    cache.addInterpolationTable(new NoInterpolation(ephemeris, Ephemeris.EARTH,
                                                    VectorType.POSITION));

    double width = 600.0;

    cache.addInterpolationTable(new GridInterpolationTable(ephemeris, -1,
                                                           VectorType.POSITION,
                                                           new PolarMethod(),
                                                           width, 10));



    long before = System.currentTimeMillis();

    PreciseDate time0 = UTCSystem.getInstance()
                                .createFormat()
                                .parsePreciseDate("2013-03-15 00:00:00 UTC");


    PreciseDate time = time0.copy();

    double step = width*0.01;
    for(int i=0; i<101; ++i, time.increment(step)) {
        PreciseDate tdb = TDBSystem.getInstance().convertDate(time);
        EOP eop   = (EOP)(UT1System.getInstance().convertDate(time));

        ThreeVector interp =     cache.barycentricPosition(-1, tdb);
        ThreeVector real   = ephemeris.barycentricPosition(-1, tdb);

        System.out.println(i+" "+Math.sqrt(interp.distanceSquared(real)));

    } // end of loop over times


    long after = System.currentTimeMillis();
    System.out.println((after-before)+" ms");

} // end of testGridAccuracy method

/***************************************************************************
*
***************************************************************************/
public static void testGridAccuracy2() throws Exception {

    TDBSystem.setDefaultTDBModel(ZeroTDBModel.getInstance());
    Test.initTime();

    /**************
    * Observatory *
    **************/
    Observatory observatory = Test.initObservatory(204.530566, 19.822991, 4213.6);

    /********************
    * TLE for Galaxy 15 *
    ********************/
    String card1 = "1 28884U 05041A   13077.43391420  .00000064  00000-0  00000+0 0  4730";
    String card2 = "2 28884 000.0160 008.3641 0001298 006.2832 184.6988 01.00273091 27200";
    TLE tle = new TLE(card1, card2);

    Precession1980 precession = new Precession1980(TTSystem.getInstance());
    Nutation nutation = new eap.sky.ephemeris.sgp4.Nutation1980();
    NutationTransform nut_trans = new NutationRotation();



    Ephemeris ephemeris = new SGP4Ephemeris(UT1System.getInstance(),
                                            tle, precession, nutation,
                                            nut_trans);

    for(int i=1; i<100; ++i) {

        double width = i*600.0;

        CachedEphemeris cache = new CachedEphemeris(UT1System.getInstance());

        cache.addInterpolationTable(new NoInterpolation(ephemeris, Ephemeris.EARTH,
                                                        VectorType.POSITION));

        cache.addInterpolationTable(new GridInterpolationTable(ephemeris, -1,
                                                            VectorType.POSITION,
                                                            new PolarMethod(),
                                                            width, 10));


        PreciseDate time0 = UTCSystem.getInstance()
                                     .createFormat()
                                     .parsePreciseDate("2013-03-15 00:00:00 UTC");

        PreciseDate tdb0 = TDBSystem.getInstance().convertDate(time0);
        cache.barycentricPosition(-1, tdb0);

        PreciseDate tdb1 = tdb0.copy();
        tdb1.increment(width*0.5);

        ThreeVector interp =     cache.barycentricPosition(-1, tdb1);
        ThreeVector real   = ephemeris.barycentricPosition(-1, tdb1);

        System.out.println(width+" "+Math.sqrt(interp.distanceSquared(real)));

    } // end of loop over times

} // end of testGridAccuracy2 method

/***************************************************************************
*
***************************************************************************/
public static void testObservatoryPosition() throws Exception {

    Test.initTime();

    /**************
    * Observatory *
    **************/
    Observatory observatory = Test.initObservatory(204.530566, 19.822991, 4213.6);


    PreciseDate time = UTCSystem.getInstance()
                                .createFormat()
                                .parsePreciseDate("2013-03-15 00:00:00 UTC");

    for(int i=0; i<25; ++i, time.increment(3600.0)) {

        EOP eop   = (EOP)(UT1System.getInstance().convertDate(time));

        ThreeVector pos = observatory.celestialPosition(eop);
        Direction dir = pos.getDirection();

        System.out.println(time+" "+dir.getLongitude()+" "+dir.getLatitude());

    } // end of loop over times

} // end of testObservatoryPosition method

/***************************************************************************
*
***************************************************************************/
public static void testSatellitePosition() throws Exception {

    Test.initTime();

    /********************
    * TLE for Galaxy 15 *
    ********************/
    String card1 = "1 28884U 05041A   13077.43391420  .00000064  00000-0  00000+0 0  4730";
    String card2 = "2 28884 000.0160 008.3641 0001298 006.2832 184.6988 01.00273091 27200";
    TLE tle = new TLE(card1, card2);

    Precession1980 precession = new Precession1980(TTSystem.getInstance());
    Nutation nutation = new eap.sky.ephemeris.sgp4.Nutation1980();
    NutationTransform nut_trans = new NutationRotation();

    Ephemeris ephemeris = new SGP4Ephemeris(UT1System.getInstance(),
                                            tle, precession, nutation,
                                            nut_trans);

    PreciseDate time = UTCSystem.getInstance()
                                .createFormat()
                                .parsePreciseDate("2013-03-15 00:00:00 UTC");

    for(int i=0; i<25; ++i, time.increment(3600.0)) {
        PreciseDate tdb = TDBSystem.getInstance().convertDate(time);

        ThreeVector pos = ephemeris.barycentricPosition(-1, tdb);
        Direction dir = pos.getDirection();

        System.out.println(time+" "+pos.getLength());

    } // end of loop over times

} // end of testSatellitePosition method

/***************************************************************************
*
***************************************************************************/
public static void main(String[] args) throws Exception {


    //testRotation();
//     testGCRFtoMEME();
//     testAdeptPrecession();

//     testMEMEtoTEME();
//     System.out.println("-----------------------------");
//     testAdeptNutation();

    // testSGP4Ephemeris();
    //testObservatoryPosition();
    //testSatellitePosition();

//     testSGP4Ephemeris();
//     testCachedSGP4Ephemeris();
//     testCachedSGP4Ephemeris2();

    testGridAccuracy2();

} // end of main method

} // end of TestTLE class
