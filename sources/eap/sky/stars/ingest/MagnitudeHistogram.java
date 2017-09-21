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

package eap.sky.stars.ingest;

import eap.sky.stars.*;

import java.io.*;

/*************************************************************************
*
*************************************************************************/
public class MagnitudeHistogram {



int low;

int[] bins;
int highest;

float mag0;
float scale;

Band band;

/*************************************************************************
*
*************************************************************************/
private MagnitudeHistogram() {

} // end of private empty constructor

/*************************************************************************
*
*************************************************************************/
public MagnitudeHistogram(Band band) {

    this.band = band;

    mag0 = -10.f;
    scale = 1000.f;

    bins = new int[findBin(30.f)+1];

    highest = -1;

} // end of constructor





/*************************************************************************
*
*************************************************************************/
public Band getBand() { return band; }

/*************************************************************************
*
*************************************************************************/
public int findBin(float mag) {

    return (int)Math.floor((mag - mag0)*scale);

} // end of findBin method

/*************************************************************************
*
*************************************************************************/
public int getBinCount() { return bins.length; }

/*************************************************************************
*
*************************************************************************/
public int getCount(int index) { return bins[index]; }

/*************************************************************************
*
*************************************************************************/
public int getTotalCount() {

    int sum = 0;
    for(int i=0; i< getBinCount(); ++i) {

        sum += getCount(i);
    }

    return sum;

} // end of getTotal method

/*************************************************************************
*
*************************************************************************/
public float getMinMagnitude(int index) {

    return index/scale+mag0;

} // end of getMinMagnitude method

/*************************************************************************
*
*************************************************************************/
public float getBinWidth() { return 1.f/scale; }

/*************************************************************************
*
*************************************************************************/
private void add(float mag) {

    int index = findBin(mag);

    if(index >= bins.length) {
        System.out.println("enlarging to "+(index+1));
        int[] bins2 = new int[index+1];
        System.arraycopy(bins, 0, bins2, 0, bins.length);
        bins = bins2;
        System.out.println("done enlarging");


      //  System.exit(0);
    }

    if(index < 0) {
        ++low;
        return;
    }

    ++bins[index];

  //  System.out.println("    "+index+" "+bins[index]);

    if(index > highest) highest = index;

} // end of add method

/*************************************************************************
*
*************************************************************************/
public void add(Star star) {

    add(star.getMagnitude(band));

} // end of add method

/*************************************************************************
*
*************************************************************************/
public void dump() {

    System.out.println("low="+low);

    for(int i=0; i<= highest; ++i) {
        System.out.println(i+" "+getMinMagnitude(i)+" "+bins[i]);
    }

} // end of dump method

/*************************************************************************
*
*************************************************************************/
public void write(File file) throws IOException {

    DataOutputStream out = new DataOutputStream(
                           new BufferedOutputStream(
                           new FileOutputStream(file)));
    write(out);
    out.close();

} // end of write to a file method

/*************************************************************************
*
*************************************************************************/
public void write(DataOutput out) throws IOException {

    band.write(out);

    out.writeFloat(mag0);
    out.writeFloat(scale);

    out.writeInt(low);

    out.writeInt(highest+1);

    for(int i=0; i<=highest; ++i) {
        out.writeInt(bins[i]);
    }

} // end of write method

/*************************************************************************
*
*************************************************************************/
public static MagnitudeHistogram read(File file) throws IOException {

    DataInputStream in = new DataInputStream(
                         new BufferedInputStream(
                         new FileInputStream(file)));

    MagnitudeHistogram hist = read(in);
    in.close();

    return hist;

} // end of read from file method

/*************************************************************************
*
*************************************************************************/
public static MagnitudeHistogram read(DataInput in) throws IOException {

    MagnitudeHistogram hist = new MagnitudeHistogram();

    hist.band = Band.read(in);

    hist.mag0 = in.readFloat();
    hist.scale = in.readFloat();

    hist.low = in.readInt();

    hist.highest = in.readInt()-1;
    hist.bins = new int[hist.highest+1];
    for(int i=0; i< hist.bins.length; ++i) {

        hist.bins[i] = in.readInt();

    } // end of loop over bins

    return hist;


} // end of read method


} // end of MagnitudeHistogram class
