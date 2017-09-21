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

import java.util.*;
import java.text.*;
import java.io.*;

/*********************************************************************
*
*********************************************************************/
public class DiskSorter {

CatalogSource source;
File dir;
Band band;
StarFormat format;
BinLayout layout;

OutputBinFile[] files;

/*********************************************************************
*
*********************************************************************/
public DiskSorter(CatalogSource source, File dir, StarFormat format,
                  MagnitudeHistogram hist, int size) throws IOException {

    this(source, dir, hist.getBand(), format, new BinLayout(hist, size));

} // end of constructor from a histogram

/*********************************************************************
*
*********************************************************************/
public DiskSorter(CatalogSource source, File dir, Band band, StarFormat format,
                  BinLayout layout) throws IOException {

    this.source = source;
    this.dir = dir;
    this.band = band;
    this.format = format;
    this.layout = layout;

    files = new OutputBinFile[layout.getBinCount()];

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public void binStars() throws IOException {

    Star star;
    int count=0;
    while((star=source.nextStar())!=null) {

        addStar(star);

        ++count;
        if(count%(1024*128) ==0) {
            flush();
        }


    } // end of loop over stars

    close();

} // end of binStars method




/*********************************************************************
*
*********************************************************************/
private OutputBinFile getBin(Star star) throws IOException {

    float mag = star.getMagnitude(band);
    int index = layout.getBin(mag);

    OutputBinFile bin = files[index];
    if(bin == null) {
        bin = new OutputBinFile(dir, index, format);
        System.out.println("opening "+bin.getFile());

        files[index] = bin;
    }

    return bin;

} // end of getStream method

/*********************************************************************
*
*********************************************************************/
public void addStar(Star star) throws IOException {

    getBin(star).addStar(star);

} // end of addStar method

/*********************************************************************
*
*********************************************************************/
public void flush() throws IOException {

  //  System.out.println("flushing");
    for(int i=0; i< files.length; ++i) {
        OutputBinFile bin = files[i];
        if(bin == null) continue;

        bin.flush();
    }

    saveCounts();

} // end of flush method

/*********************************************************************
*
*********************************************************************/
private void saveCounts() throws IOException {

    DataOutputStream out = new DataOutputStream(
                           new FileOutputStream(
                           new File(dir, "counts")));

    for(int i=0; i< files.length; ++i) {
        OutputBinFile bin = files[i];
        if(bin == null) continue;

        out.writeUTF(bin.getFile().getName());
        out.writeInt(bin.getCount());

    } // end of loop over bins

    out.close();

} // end of saveCounts method

/*********************************************************************
*
*********************************************************************/
public void close() throws IOException {

    for(int i=0; i< files.length; ++i) {
        OutputBinFile bin = files[i];
        if(bin == null) continue;
        
        bin.close();

    } // end of loop over bins

    saveCounts();

} // end of close method


} // end of MagBinner class
