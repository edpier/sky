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

/****************************************************************************
* A sum over a set of {@link TidalArguments}.
* Specifically, this is the sum
* <blockquote>
* <font size=+3>&Sigma;</font><sub>i</sub>
                   [S<sub>i</sub> sin (&theta;<sub>i</sub> + &phi;<sub>i</sub>)+
                    C<sub>i</sub> cos(&theta;<sub>i</sub>+ &phi;<sub>i</sub>)],
* </blockquote>
* where
* &theta;<sub>i</sub> = <font size=+3>&Sigma;</font><sub>j</sub>
* A<sub>i,j</sub> f<sub>j</sub>, and f<sub>j</sub> are the fundamental
* tidal arguments as calculated by {@link TidalArguments}.
* <p>
* This class is used to
* calculate diurnal and semidiurnal corrections to the Earth's rotation
* and polar motion.
* @see EOPCorrection
****************************************************************************/
public class DiurnalExpansion extends TidalExpansion {

private double[] sin_phase;
private double[] cos_phase;
private double[] sin_weight;
private double[] cos_weight;
private int[][]  arg_weight;

/***************************************************************************
* Create a new series.
* @param arg_weight An array of harmonics. Each harmonic is specified as
*        an array of integer weights for the fundamental arguments.
*        Specifically, arg_weight[i][j] = A<sub>i,j</sub> above.
* @param sin_phase The sine   of the phase offset for this harmonic.
*        Specifically, sin_phase[i] = sin(&phi;<sub>i</sub>) above.
* @param cos_phase The cosine of the phase offset for this harmonic
*        Specifically, sin_phase[i] = cos(&phi;<sub>i</sub>) above.
* @param sin_weight The amplitude weight of the sine of the harmonic
*        Specifically, sin_weight[i] = S<sub>i</sub> above.
* @param cos_weight The amplitude weight of the cosine of the harmonic.
*        Specifically, cos_weight[i] = C<sub>i</sub> above.
***************************************************************************/
public DiurnalExpansion(int[][] arg_weight,
                        double[] sin_phase,  double[] cos_phase,
                        double[] sin_weight, double[] cos_weight) {

    this.arg_weight = arg_weight;
    this.sin_phase  = sin_phase;
    this.cos_phase  = cos_phase;
    this.sin_weight = sin_weight;
    this.cos_weight = cos_weight;

} // end of constructor

/***************************************************************************
* Evaluate the sum described above.
* @param args The tidal arguments whose harmonics we are summing.
* @return a weighted sum of phase shifted harmonics of the fundamental
* arguments.
***************************************************************************/
public double evaluate(TidalArguments args) {

    /********************************************************
    * calculate a weighted sum of the fundamental arguments *
    ********************************************************/
    double sum = 0.0;
    for(int i=0; i< sin_weight.length; ++i) {

        /**********************************************************
        * calculate the weighted sum of the fundamental arguments *
        **********************************************************/
        double[] angle =args.weightedSum(arg_weight[i]);

        /**************************
        * add in the phase offset *
        **************************/
        double sin_arg = angle[0]*cos_phase[i] + angle[1]*sin_phase[i];
        double cos_arg = angle[1]*cos_phase[i] - angle[0]*sin_phase[i];

    //    System.out.println(i+" sin_arg="+sin_arg+" cos_arg="+cos_arg);
//         System.out.println(i+" sin_weight="+sin_weight[i]+
//                              " cos_weight="+cos_weight[i]);
        sum += sin_weight[i] * sin_arg +
               cos_weight[i] * cos_arg;

    } // end of loop over i

    return sum;


} // end of evaluate method

} // end of DiurnalExpansion class
