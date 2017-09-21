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

package eap.sky.stars;

import eap.sky.stars.archive.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;

/************************************************************************
*
************************************************************************/
public class StarCatalog {

CatalogProperties properties;
Archive archive;

BandMap bands;
Band band;

Cell cell;
InputCell root;

boolean no_cell_info;
CellInfoTable cell_info;

StarFormat format;

/************************************************************************
*
************************************************************************/
//protected StarCatalog()  {}

/************************************************************************
*
************************************************************************/
public StarCatalog(Archive archive) throws IOException {

    this(new CatalogProperties(archive));

} // end of constructor from an archive

/************************************************************************
*
************************************************************************/
public StarCatalog(CatalogProperties properties) throws IOException {

    this.properties = properties;
    this.archive    = properties.getArchive();

    InputStream in;

    /********************
    * read the band map *
    ********************/
    in = archive.getInputStream("bands");
    if(in == null) throw new IOException("No bands file");

    bands = BandMap.read(new DataInputStream(in));
    in.close();


    /*********************
    * read the sort band *
    *********************/
    band = null;
    String band_name = properties.getSortBandName();
    if(band_name == null) {
        throw new IOException("Sort band not specified");
    }

    band = bands.getBand(band_name);
    if(band == null) throw new IOException("No such band "+band_name);

    /**************
    * star format *
    **************/
    try { format = properties.createStarFormat(bands); }
    catch(Exception e) {
        throw (IOException)(new IOException("Could not create star format")
                                        .initCause(e));
    }

    /***************************
    * read the cell info table *
    ***************************/
    in = archive.getInputStream("cell_info");
    if(in == null) {
        throw new IOException("No cell_info file");
    }

    cell_info = CellInfoTable.read(in);
    in.close();

    /***********************
    * create the root cell *
    ***********************/
    try {
        /************************************
        * create the root tessellation cell *
        ************************************/
        cell = properties.createRootCell();

        /**************************************
        * get the cell info for the root cell *
        **************************************/
        CellInfo root_info = getCellInfo(cell.getName());
        if(root_info == null) {
            throw new IOException("No Cell info for root cell");
        }

        /*****************************
        * create the root input cell *
        *****************************/
        root = new InputCell(this, cell, root_info);


    } catch(Exception e) {
        /*******************
        * that didn't work *
        *******************/
        throw (IOException)(new IOException("Could not create root cell")
                                        .initCause(e));
    }


} // end of constructor

/********************************************************************
*
********************************************************************/
CatalogProperties getCatalogProperties() { return properties; }

/********************************************************************
*
********************************************************************/
public boolean equals(StarCatalog catalog) {

    return archive.equals(catalog.archive);

} // end of equals method


/************************************************************************
*
************************************************************************/
public String getName() { return properties.getName(); }

/************************************************************************
*
************************************************************************/
public String getVersion() { return properties.getVersion(); }

/************************************************************************
*
************************************************************************/
public Archive getCellArchive() { return archive; }

/************************************************************************
*
************************************************************************/
public BandMap getBandMap() { return bands; }

/************************************************************************
*
************************************************************************/
public Band getSortBand() { return band; }

/************************************************************************
*
************************************************************************/
public InputCell getRootCell() { return root; }

/************************************************************************
*
************************************************************************/
public StarFormat getStarFormat() { return format; }

/************************************************************************
* Get the info for a particular cell.
************************************************************************/
public CellInfoTable getCellInfoTable() { return cell_info; }

/************************************************************************
* Get the info for a particular cell.
************************************************************************/
public CellInfo getCellInfo(String name) { return cell_info.get(name); }


} // end of StarCatalog class
