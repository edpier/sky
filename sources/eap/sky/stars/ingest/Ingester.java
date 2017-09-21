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

/***************************************************************
*
***************************************************************/
public class Ingester {

File dir;
Band band;
BandMap bands;
StarFormat format;
CatalogSource source;
String catalog_name;
String version;

/***************************************************************
* A tool for reading a star catalog and grouping stars into cells.
* @param dir A work directory. The ingest will write scratch files and
* the final output to this directory. If it does not exist,
* the constructor will try to create it.
* @param band The band used for sorting the stars by magnitude.
* @param source The native format catalog of stars.
***************************************************************/
public Ingester(File dir, Band band, CatalogSource source,
                String catalog_name, String version) {

    this.dir = dir;
    this.band = band;
    this.source = source;
    this.catalog_name = catalog_name;
    this.version = version;

    bands = new BandMap();
    bands.add(band);
    format = new StarFormat5(bands);

    if(!dir.exists()) dir.mkdir();

} // end of constructor

/*************************************************************************
*
*************************************************************************/
public File getHistogramFile() { return new File(dir, "histogram"); }

/*************************************************************************
*
*************************************************************************/
public File getUnsortedFile() { return new File(dir, "unsorted"); }

/*************************************************************************
*
*************************************************************************/
public File getBinsDirectory() { return new File(dir, "bins"); }

/*************************************************************************
*
*************************************************************************/
public File getSortedFile() { return new File(dir, "sorted"); }

/*************************************************************************
*
*************************************************************************/
public File getCellsDirectory() { return new File(dir, "cells"); }

/*************************************************************************
*
*************************************************************************/
public void histogram() throws IOException {

    /***************************************************
    * check if the files we would create already exist *
    ***************************************************/
    File unsorted_file = getUnsortedFile();
    File     hist_file = getHistogramFile();

    if(unsorted_file.exists() || hist_file.exists()   ) return;

    /***************
    * histogrammer *
    ***************/
    HistogramSource hist = new HistogramSource(source, band);

    /*************************************************
    * save the unsorted stars in our format, which
    * is probably more compact than the original
    *************************************************/
    NativeSaver saver = new NativeSaver(hist, unsorted_file, format);
    saver.save();
    hist.write(hist_file);

} // end of histogram method


/*************************************************************************
*
*************************************************************************/
public void bin() throws IOException {

    /*********************************************
    * check if the bins directory already exists *
    *********************************************/
    File bins_dir = getBinsDirectory();
    if(bins_dir.exists()) return;

    /****************************************
    * open the files from the previous step *
    ****************************************/
    File hist_file = getHistogramFile();
    File unsorted_file = getUnsortedFile();

    MagnitudeHistogram hist = MagnitudeHistogram.read(hist_file);
    NativeSource source = new NativeSource(unsorted_file, format);

    /*********************
    * 1000 stars per bin *
    *********************/
    int size = hist.getTotalCount()/1000;

    /****************
    * bin the stars *
    ****************/
    bins_dir.mkdir();
    DiskSorter sorter = new DiskSorter(source, bins_dir, format, hist, size);

    sorter.binStars();

} // end of bin method

/*************************************************************************
*
*************************************************************************/
public void sort() throws IOException {

    File sorted_file = getSortedFile();
    if(sorted_file.exists()) return;

    /************************
    * open the binned stars *
    ************************/
    File bins_dir = getBinsDirectory();
    DiskSortedSource source = new DiskSortedSource(bins_dir, band, format);

    /*****************************
    * write out the sorted stars *
    *****************************/
    NativeSaver saver = new NativeSaver(source, sorted_file, format);
    saver.save();

} // end of sort method

/*************************************************************************
*
*************************************************************************/
public void generate() throws IOException {

    File cells_dir = getCellsDirectory();
    if(cells_dir.exists()) return;

    /****************************
    * open the sorted star list *
    ****************************/
    File sorted_file = getSortedFile();
    CatalogSource source = new NativeSource(sorted_file, format);

    /***********************
    * generate the catalog *
    ***********************/
    cells_dir.mkdir();
    CatalogGenerator generator = new DepthFirstGenerator(source,
                                                         cells_dir,
                                                         1000,
                                                         bands,
                                                         band,
                                                         format,
                                                         catalog_name,
                                                         version,
                                                         new HTMRoot());
    generator.generate();

} // end of generate method

/**************************************************************************
*
**************************************************************************/
public void ingest() throws IOException {

    histogram();
    bin();
    sort();
    generate();

} // end of ingest method


} // end of Ingester class