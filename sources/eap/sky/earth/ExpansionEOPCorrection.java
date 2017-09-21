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

/***************************************************************************
* Diurnal and semidiurnal corrections to EOP values which uses
* {@link DiurnalExpansion} objects for the corrections to UT1 and
* polar motion. This is the most common form for this calculation.
***************************************************************************/
public class ExpansionEOPCorrection extends EOPCorrection {

private DiurnalExpansion x_expansion;
private DiurnalExpansion y_expansion;
private DiurnalExpansion t_expansion;

/***************************************************************************
* Create a new object.
* @param arg_weight The harmonics shared by the diurnal expansions
*        for time and polar motion.
* @param sin_phase The sine of the phase lag shared by the diurnal expansions
*        for time and polar motion.
* @param cos_phase The cosine of the phase lag shared by the diurnal expansions
*        for time and polar motion.
* @param x_sin_weight The sine amplitude weights for the polar motion
*        x correction.
* @param x_cos_weight The cosine amplitude weights for the polar motion
*        x correction.
* @param y_sin_weight The sine amplitude weights for the polar motion
*        y correction.
* @param y_cos_weight The cosine amplitude weights for the polar motion
*        y correction.
* @param t_sin_weight The   sine amplitude weights for the UT1 correction.
* @param t_cos_weight The cosine amplitude weights for the UT1 correction.
* @see DiurnalExpansion
***************************************************************************/
public ExpansionEOPCorrection(int[][] arg_weight,
                              double[] sin_phase, double[] cos_phase,
                              double[] x_sin_weight, double[] x_cos_weight,
                              double[] y_sin_weight, double[] y_cos_weight,
                              double[] t_sin_weight, double[] t_cos_weight) {

    x_expansion = new DiurnalExpansion(arg_weight,
                                       sin_phase, cos_phase,
                                       x_sin_weight, x_cos_weight);

    y_expansion = new DiurnalExpansion(arg_weight,
                                       sin_phase, cos_phase,
                                       y_sin_weight, y_cos_weight);

    t_expansion = new DiurnalExpansion(arg_weight,
                                       sin_phase, cos_phase,
                                       t_sin_weight, t_cos_weight);

} // end of constructor





/***************************************************************************
*
***************************************************************************/
public void correct(EOP eop) {

    TidalArguments args = eop.getTidalArguments();

    double delta_x = x_expansion.evaluate(args);
    double delta_y = y_expansion.evaluate(args);
    double delta_t = t_expansion.evaluate(args); // note different units

//    System.out.println( "delta_x = "+delta_x+
//                       " delta_y = "+delta_y+
//                       " delta_t = "+delta_t);

    apply(eop, delta_t, delta_x, delta_y);

} // end of correct method



} // end of ExpansionEOPCorrection class
