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
import eap.sky.stars.archive.*;

import java.util.*;
import java.io.*;

/**********************************************************************
*
**********************************************************************/
public abstract class CatalogGenerator {

File dir;
int max_stars;
BandMap bands;
Band band;
StarFormat format;

Properties props;

Cell root;
CellPropagator propagator;

CellInfoTable cell_info;



/************************************************************************
*
************************************************************************/
public CatalogGenerator(File dir, int max_stars,
                        BandMap bands, Band band, StarFormat format,
                        String name, String version,
                        CellPropagator propagator, Cell root) {
    this.dir = dir;
    this.max_stars = max_stars;
    this.bands = bands;
    this.band = band;
    this.format = format;
    this.propagator = propagator;
    this.root = root;

    /*************
    * properties *
    *************/
    props = new Properties();
    props.setProperty(CatalogProperties.NAME_KEY, name);
    props.setProperty(CatalogProperties.CELL_KEY, root.getClass().getName());
    props.setProperty(CatalogProperties.VERSION_KEY, version);
    props.setProperty(CatalogProperties.FORMAT_KEY, format.getClass().getName());
    props.setProperty(CatalogProperties.SORT_BAND_KEY, bands.getName(band));

} // end of constructor

/************************************************************************
*
************************************************************************/
public void generate() throws IOException {

    /***********************
    * write the bands file *
    ***********************/
    bands.write(new File(dir, "bands"));

    /*************
    * properties *
    *************/
    OutputStream out = new FileOutputStream(new File(dir, "properties"));
    props.store(out, "Catalog metadata");
    out.close();

    /********
    * stars *
    ********/
    cell_info = new CellInfoTable();
    binStars();

    /******************
    * cell info table *
    ******************/
    System.out.println("writing cell info table");
//     CellInfoTable info = new CellInfoTable();
//     info.add(new DirectoryArchive(dir), format, band, root);
    cell_info.write(new File(dir, "cell_info"));

} // end of generate method

/************************************************************************
*
************************************************************************/
protected abstract void binStars() throws IOException;

/************************************************************************
*
************************************************************************/
public int getStarsPerCell() { return max_stars; }

/************************************************************************
*
************************************************************************/
public File getDirectory() { return dir; }

/************************************************************************
*
************************************************************************/
public StarFormat getStarFormat() { return format; }

/************************************************************************
*
************************************************************************/
public CellPropagator getCellPropagator() { return propagator; }

/************************************************************************
*
************************************************************************/
public CellInfoTable getCellInfoTable() { return cell_info; }

/************************************************************************
*
************************************************************************/
public Band getSortBand() { return band; }

} // end of CatalogGenerator class
