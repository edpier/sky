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

import eap.sky.time.*;
import eap.sky.util.*;

import java.text.*;

/************************************************************************
*
************************************************************************/
public class SGP4Propagator {

private static final double TWOPI = 2.0 * Math.PI;

double bstar;
Orbit orb0;

protected boolean deep_space;

protected double aycof;
protected double con41;
protected double cc1;
protected double cc4;
protected double cc5;
protected double delmo;
protected double eta;
protected double argpdot;
protected double omgcof;
protected double sinmao;
protected double x1mth2;
protected double x7thm1;
protected double mdot;
protected double omegadot;
protected double xlcof;
protected double xmcof;
protected double omegacf;

// extra terms if not "simple"
protected boolean simple;

protected double d2;
protected double d3;
protected double d4;

protected double t2cof;
protected double t3cof;
protected double t4cof;
protected double t5cof;

protected DeepSpace dsvalues;

/*************************************************************************
*
*************************************************************************/
public SGP4Propagator(TLE tle) throws OrbitDecayedException {

    /*********************
    * load up the satrec *
    *********************/
    bstar = tle.getBstar();
    orb0 = tle.getOrbitAtEpoch();

    final double radiusearthkm = 6378.135;
    final double ss = 78.0 / radiusearthkm + 1.0;
    final double qzms2t = Math.pow(((120.0 - 78.0) / radiusearthkm), 4);
    final double j2 = 1.082616e-3;
    final double j3oj2 = -2.53881e-6 / j2;
    final double x2o3 = 2.0/3.0;
    final double j4 = -1.65597e-6;

    /* -------------------- wgs-72 earth constants ----------------- */
    final double xke = 7.43669161331734132e-2;

    /* ------------- calculate auxillary epoch quantities ---------- */
    double omeosq = 1.0 - orb0.getEccentricity() * orb0.getEccentricity();
    double rteosq = Math.sqrt(omeosq);
    double cosio  = Math.cos(orb0.getInclination());
    double cosio2 = cosio*cosio;

    /***************************
    * un-kozai the mean motion *
    ***************************/
    double no = orb0.getMeanMotion();

    double ak = Math.pow(xke/no, x2o3);
    double d1 = 0.75*j2*(3.0*cosio2 - 1.0)/(rteosq*omeosq);
    double del = d1/(ak*ak);

    double adel = ak*(1.0 - del*del
                          - del*(1.0/3.0 + 134.0*del*del/81.0));

    del = d1/(adel*adel);
    orb0.setMeanMotion(no/(1.0 + del));



    double ao = Math.pow(xke/orb0.getMeanMotion(), x2o3);
    double sinio = Math.sin(orb0.getInclination());
    double po = ao * omeosq;
    double con42 = 1.0 - 5.0*cosio2;

    con41 = -con42 - cosio2 - cosio2;

    double einv = 1.0/orb0.getEccentricity();
    double posq = po * po;
    double rp = ao * (1.0 - orb0.getEccentricity());

    if (rp < 1.0){
        throw new OrbitDecayedException("Epoch elts sub-orbital");
    }

    // in the original code none of the init seemed to
    // happen if this failed -ED
    if(!(omeosq >= 0.0 || orb0.getMeanMotion() >= 0.0)) {
        throw new OrbitDecayedException("ascending node="+omeosq+
                                        " mean motion="+orb0.getMeanMotion());
    }

    /**********************************************************
    * If the epoch perigee height is less than 220 km, some
    * some the equations are simplified.
    * See Spacetrack Report #3 page 12.
    **********************************************************/
    simple = (rp < (220.0/radiusearthkm + 1.0));

    double sfour = ss;
    double qzms24 = qzms2t;
    double perige = (rp - 1.0)*radiusearthkm;

    /******************************************************
    * for perigees below 156 km, s and qoms2t are altered *
    ******************************************************/
    if (perige < 156.0) {
        sfour = perige - 78.0;
        if (perige < 98.0)
            sfour = 20.0;
        qzms24 = Math.pow(((120.0 - sfour)/radiusearthkm), 4.0);
        sfour = sfour/radiusearthkm + 1.0;
    }

    double pinvsq = 1.0/posq;

    double tsi = 1.0/(ao - sfour);
    eta = ao * orb0.getEccentricity()*tsi;
    double etasq = eta*eta;
    double eeta = orb0.getEccentricity()*eta;
    double psisq = Math.abs(1.0 - etasq);
    double coef = qzms24 * Math.pow(tsi, 4.0);
    double coef1 = coef / Math.pow(psisq, 3.5);

    double cc2 = coef1*orb0.getMeanMotion()*
                 (ao*(1.0 + 1.5*etasq + eeta*(4.0 + etasq)) +
                  0.375*j2*tsi/psisq*con41*(8.0 + 3.0*etasq*(8.0 + etasq)));

    cc1 = bstar*cc2;

    double cc3 = 0.0;
    if (orb0.getEccentricity() > 1.0e-4) {
        cc3 = -2.0*coef*tsi*j3oj2*orb0.getMeanMotion()*
               sinio/orb0.getEccentricity();
    }

    x1mth2 = 1.0 - cosio2;
    cc4 = 2.0*orb0.getMeanMotion()*coef1*ao*omeosq*
          (eta*(2.0 + 0.5 * etasq) +
           orb0.getEccentricity()*(0.5 + 2.0 * etasq) -
           j2*tsi/(ao*psisq)*(-3.0*con41*(1.0 - 2.0*eeta + etasq*(1.5 - 0.5*eeta)) + 0.75*x1mth2*(2.0*etasq -
                 eeta*(1.0 + etasq))*Math.cos(2.0*orb0.getArgumentOfPerigee())));

    cc5 = 2.0*coef1*ao*omeosq*(1.0 + 2.75*(etasq + eeta) + eeta*etasq);

    double cosio4 = cosio2*cosio2;
    double temp1 = 1.5*j2*pinvsq*orb0.getMeanMotion();
    double temp2 = 0.5*temp1*j2*pinvsq;
    double temp3 = -0.46875*j4*pinvsq*pinvsq*orb0.getMeanMotion();

    mdot = orb0.getMeanMotion()
           +0.5   *temp1*rteosq*con41
           +0.0625*temp2*rteosq*(13.0 - 78.0*cosio2 + 137.0*cosio4);

    argpdot =    -0.5*temp1*con42 +
               0.0625*temp2*(7.0 - 114.0*cosio2 + 395.0*cosio4) +
                      temp3*(3.0 -  36.0*cosio2 +  49.0*cosio4);

    double xhdot1 = -temp1 * cosio;

    omegadot = xhdot1 + (0.5*temp2*(4.0 - 19.0 * cosio2) +
                         2.0*temp3*(3.0 -  7.0 * cosio2))*cosio;

    double xpidot = argpdot + omegadot;

    omgcof = bstar*cc3*Math.cos(orb0.getArgumentOfPerigee());

    if (orb0.getEccentricity() > 1.0e-4) xmcof = -x2o3*coef*bstar/eeta;
    else                                 xmcof = 0;

    omegacf = 3.5*omeosq*xhdot1*cc1;
    t2cof   = 1.5*cc1;

    xlcof = -0.25*j3oj2*sinio*(3.0 + 5.0*cosio)/(1.0 + cosio);
    aycof = -0.5 *j3oj2*sinio;

    delmo  = Math.pow((1.0 + eta*Math.cos(orb0.getMeanAnomaly())), 3);
    sinmao = Math.sin(orb0.getMeanAnomaly());
    x7thm1 = 7.0*cosio2 - 1.0;

    /********************************
    * checking if we are deep space *
    ********************************/
    deep_space = (tle.getPeriod() >= 225.0);

    /***************************************
    * the equations are also simplified if
    * we are a deep space orbit
    ***************************************/
    if(deep_space) simple = true;

    if(!isSimple()) {
        /***********************************
        * additional terms if we need them *
        ***********************************/
        double cc1sq = cc1*cc1;
        d2 = 4.0*ao*tsi*cc1sq;

        double temp = d2*tsi*cc1/3.0;
        d3 = (17.0*ao + sfour)*temp;
        d4 = 0.5*temp*ao*tsi*(221.0*ao + 31.0*sfour)*cc1;

        t3cof = d2 + 2.0*cc1sq;
        t4cof = 0.25*(3.0*d3 + cc1*(12.0*d2 + 10.0*cc1sq));

        t5cof = 0.2*(3.0*d4 + 12.0*cc1*d3 + 6.0*d2*d2 +
                     15.0*cc1sq*(2.0*d2 + cc1sq));

    } // end if not simple

    /******************
    * deep space init *
    ******************/
    if(deep_space) {

        dsvalues = new DeepSpace();


        try {
            /****************************
            * compute the whole days of
            * the TLE epoch since 1950
            ***************************/
            PreciseDate epoch = tle.getEpoch();

            PreciseDate time1950
                      = epoch.getTimeSystem()
                             .createFormat()
                             .parsePreciseDate("1950-01-01 00:00:00 UTC");

            double days_since_1950
                       = Math.round(epoch.secondsAfter(time1950)/86400.0);

            dsvalues.init(days_since_1950, this);

        } catch(ParseException e) {
            /***************************
            * this should never happen *
            ***************************/
            throw (IllegalStateException)
              (new IllegalStateException().initCause(e));
        }

    } // end if this is a deep space orbit

} // end of init method


/*************************************************************************
*
*************************************************************************/
public boolean isDeepSpace() { return deep_space; }

/*************************************************************************
*
*************************************************************************/
public boolean isSimple() { return simple; }

/****************************************************************************
*
*****************************************************************************/
public MotionState propagate(double time) throws OrbitDecayedException {

    /* -------------------- wgs-72 earth constants ----------------- */
    /* ------------------ set mathematical constants --------------- */
    double x2o3  = 2.0/3.0;
    double xke   = 7.43669161331734132e-2;
    double j2    = 1.082616e-3;
    double j3    = -2.53881e-6;
    double j3oj2 = j3/j2;

    /* ------- update for secular gravity and atmospheric drag ----- */
    double xmdf    = orb0.getMeanAnomaly()       + time*mdot;
    double argpdf  = orb0.getArgumentOfPerigee() + time*argpdot;
    double omegadf = orb0.getAscendingNode()     + time*omegadot;

    double t2 = time*time;

    Orbit orb = new Orbit();
    orb.setArgumentOfPerigee(argpdf);
    orb.setMeanAnomaly(xmdf);
    orb.setAscendingNode(omegadf + omegacf*t2);


    double tempa = 1.0 - cc1*time;
    double tempe = bstar*cc4*time;
    double templ = t2cof*t2;

    if(!isSimple()) {
        /*************************
        * apply additional terms *
        *************************/
        double delomg = omgcof*time;

        double delm = xmcof*(Math.pow((1.0 + eta*Math.cos(xmdf)), 3)
                             - delmo);

        double temp = delomg + delm;
        orb.setMeanAnomaly(xmdf + temp);
        orb.setArgumentOfPerigee(argpdf - temp);

        double t3 = t2*time;
        double t4 = t3*time;
        tempa = tempa - d2*t2 - d3*t3 - d4*t4;
        tempe = tempe + bstar*cc5*(Math.sin(orb.getMeanAnomaly()) - sinmao);
        templ = templ + t3cof*t3 + t4*(t4cof + time* t5cof);

    } // end if applying additional terms

    orb.setMeanMotion(  orb0.getMeanMotion());
    orb.setEccentricity(orb0.getEccentricity());
    orb.setInclination( orb0.getInclination());

    if(isDeepSpace()) {
        /*************
        * deep space *
        *************/
        dsvalues.dspace(orb0.getArgumentOfPerigee(), argpdot, time,
                        orb0.getMeanMotion(), orb, orb);



    } // end if deep space

    /*************************************
    * make sure the mean motion is valid *
    *************************************/
    if (orb.getMeanMotion() <= 0.0) {
        throw new OrbitDecayedException("Mean motion "+
                                        orb.getMeanMotion()+
                                        " is less than zero");
    } // end if the mean motion is bad

    double am = Math.pow((xke/orb.getMeanMotion()), x2o3) * tempa*tempa;
    orb.setMeanMotion(xke/Math.pow(am, 1.5));

    if(am < 0.95) {
        throw new OrbitDecayedException("Bad value am="+am);
    }


    /********************************************
    * subtract drag effects on the eccentricity *
    ********************************************/
    { // block of local variables
        double e = orb.getEccentricity();
        e -= tempe;

        /*********************************************
        * Check for eccentricity being out of bounds *
        *********************************************/
        if (e >= 1.0 ||  e < -0.001 ) {
            throw new OrbitDecayedException("Eccentricity "+e+
                                            " out of bounds");
        }

        /***********************************************
        * If the eccentricity is less than zero,
        * try and correct by making it a small value
        ***********************************************/
        if(e<0.0) e = 1.0e-6;
        orb.setEccentricity(e);

    } // end of block of local variables

    /**********************
    * adjust mean anomaly *
    **********************/
    { // start of block of local variables
        double m     = orb.getMeanAnomaly();
        double argp  = orb.getArgumentOfPerigee();
        double omega = orb.getAscendingNode();

        m += orb0.getMeanMotion() * templ;
        double xlm = m + argp + omega;

        omega = modTwoPi(omega);
        argp  = modTwoPi(argp);

        xlm = modTwoPi(xlm);
        m   = modTwoPi(xlm - argp - omega);

        orb.setMeanAnomaly(m);
        orb.setArgumentOfPerigee(argp);
        orb.setAscendingNode(omega);

    } // end of block of local variables

    /* ----------------- compute extra mean quantities ------------- */
    double sinim = Math.sin(orb.getInclination());
    double cosim = Math.cos(orb.getInclination());

    /****************************
    * add lunar-solar periodics *
    ****************************/
    double sinip = sinim;
    double cosip = cosim;

    if(isDeepSpace()) {

        dsvalues.dpper(time, orb, orb);
        orb.fixNegativeInclination();

        // Another eccentricity check
        if (orb.getEccentricity() < 0.0 || orb.getEccentricity() > 1.0) {
            throw new OrbitDecayedException("Eccentricity "+
                                            orb.getEccentricity()+
                                            " out of bounds");
        }

        /****************************************************
        * if we are not deep space these remain
        * initialized to the corresponding values at epoch
        ****************************************************/
        sinip = Math.sin(orb.getInclination());
        cosip = Math.cos(orb.getInclination());

        aycof = -0.5 *j3oj2*sinip;
        xlcof = -0.25*j3oj2*sinip*(3.0 + 5.0*cosip)/(1.0 + cosip);

    } // end if deep space

    double axnl = orb.getEccentricity()*Math.cos(orb.getArgumentOfPerigee());
    double amep2 = 1.0/(am*(1.0 - orb.getEccentricity()*orb.getEccentricity()));

    double aynl = orb.getEccentricity()*Math.sin(orb.getArgumentOfPerigee()) +
                  amep2*aycof;

    double xl = orb.getMeanAnomaly() +
                orb.getArgumentOfPerigee() +
                orb.getAscendingNode() + amep2*xlcof*axnl;

    /**************************
    * solve Kepler's equation *
    **************************/
    double u = modTwoPi(xl - orb.getAscendingNode());
    double eo1 = u;

    double sineo1 = 0.0;
    double coseo1 = 0.0;
    for(int iteration=1; iteration <= 10; ++iteration) {

        /************************
        * compute the increment *
        ************************/
        sineo1 = Math.sin(eo1);
        coseo1 = Math.cos(eo1);

        double delta = (u   - aynl*coseo1 + axnl*sineo1 - eo1)/
                       (    - axnl*coseo1 - aynl*sineo1 + 1.0);

        /**********************
        * limit the increment *
        **********************/
        if(delta >=  0.95) delta =  0.95; // PSC - crude fix
        if(delta <= -0.95) delta = -0.95;

        /**********************
        * apply the increment *
        **********************/
        eo1 += delta;

        /***************************
        * see if we have converged *
        ***************************/
        if(Math.abs(delta) < 1e-12) break;

    } // end of loop over iterations

    /* ------------- short period preliminary quantities ----------- */
    double ecose = axnl*coseo1 + aynl*sineo1;
    double esine = axnl*sineo1 - aynl*coseo1;
    double el2   = axnl*axnl   + aynl*aynl;
    double pl = am*(1.0 - el2);

    if(pl < 0.0) {
        /************************
        * the orbit has decayed *
        ************************/
        throw new OrbitDecayedException("ERROR pl="+ pl);
    }

    double rl = am*(1.0 - ecose);
    double rdotl  = Math.sqrt(am)*esine/rl;
    double rvdotl = Math.sqrt(pl)/rl;
    double betal  = Math.sqrt(1.0 - el2);

    double e_over_beta = esine/(1.0 + betal);
    double sinu = am/rl*(sineo1 - aynl - axnl*e_over_beta);
    double cosu = am/rl*(coseo1 - axnl + aynl*e_over_beta);

    double su = Math.atan2(sinu, cosu);
    double sin2u = 2.0*cosu*sinu;
    double cos2u = 1.0 - 2.0*sinu*sinu;

    /* -------------- update for short period periodics ------------ */
    if (isDeepSpace()) {
        double cosisq = cosip * cosip;
        con41  =   3.0*cosisq - 1.0;
        x1mth2 = 1.0 - cosisq;
        x7thm1 =   7.0*cosisq - 1.0;
    }

    double one_over_pl = 1.0/pl;
    double temp1 = 0.5*j2*one_over_pl;
    double temp2 =  temp1*one_over_pl;

    double mrt = rl*(1.0 - 1.5*temp2*betal*con41) +
                 0.5*temp1*x1mth2*cos2u;

    su -= 0.25*temp2*x7thm1*sin2u;

    double xnode = orb.getAscendingNode() + 1.5*temp2*cosip*sin2u;
    double xinc  = orb.getInclination()   + 1.5*temp2*cosip*sinip*cos2u;

    double mvt   = rdotl  - orb.getMeanMotion()*temp1*x1mth2*sin2u/xke;
    double rvdot = rvdotl + orb.getMeanMotion()*temp1*(x1mth2*cos2u + 1.5*con41)/xke;

    /* --------------------- orientation vectors ------------------- */
    double sinsu = Math.sin(su);
    double cossu = Math.cos(su);
    double snod = Math.sin(xnode);
    double cnod = Math.cos(xnode);
    double sini = Math.sin(xinc);
    double cosi = Math.cos(xinc);
    double xmx = -snod * cosi;
    double xmy = cnod * cosi;

    double ux = xmx  * sinsu + cnod * cossu;
    double uy = xmy  * sinsu + snod * cossu;
    double uz = sini * sinsu;

    double vx = xmx  * cossu - cnod * sinsu;
    double vy = xmy  * cossu - snod * sinsu;
    double vz = sini * cossu;

    double scale = 6378.135*1e3;

    return new MotionState(new ThreeVector(mrt*ux*scale, mrt*uy*scale, mrt*uz*scale),
                           new ThreeVector(scale*(mvt*ux + rvdot*vx),
                                           scale*(mvt*uy + rvdot*vy),
                                           scale*(mvt*uz + rvdot*vz) )       );
} // end of propagate method


/*************************************************************************
*
*************************************************************************/
private double modTwoPi(double x) {

    return x - (int)(x/TWOPI)*TWOPI;

} // end of modTwoPi method

} // end of SGP4Propagator class
