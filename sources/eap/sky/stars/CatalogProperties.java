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

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import eap.sky.stars.archive.*;


/************************************************************************
* A lightweight stub for a star catalog. This class allows you to get
* basic information about a catalog (e.g. to create an entry in a menu),
* without having to instantiate the catalog. 
************************************************************************/
public class CatalogProperties {

public static final String      NAME_KEY = "name";
public static final String      CELL_KEY = "cell";
public static final String   VERSION_KEY = "version";
public static final String    FORMAT_KEY = "format";
public static final String SORT_BAND_KEY = "sort_band";

Archive archive;
Properties properties;

Class root_class;
Class format_class;

/************************************************************************
*
************************************************************************/
public CatalogProperties(Archive archive) throws IOException {

    this.archive = archive;

    /**********************
    * read the properties *
    **********************/
    properties = new Properties();
    InputStream in = archive.getInputStream("properties");
    if(in == null) {
        throw new IOException("No properties file");
    }

    properties.load(in);
    in.close();

} // end of constructor

/************************************************************************
*
************************************************************************/
public Archive getArchive() { return archive; }

/************************************************************************
*
************************************************************************/
public String getProperty(String key) { return properties.getProperty(key); }

/************************************************************************
*
************************************************************************/
public String getName() { return getProperty(NAME_KEY); }

/************************************************************************
*
************************************************************************/
public String getVersion() { return getProperty(VERSION_KEY); }

/************************************************************************
*
************************************************************************/
public String getSortBandName() { return getProperty(SORT_BAND_KEY); }

/************************************************************************
* Creates the root tessellation cell. Note this is not the InputCell,
* which holds the stars.
************************************************************************/
public Cell createRootCell() throws ClassNotFoundException,
                                    InstantiationException,
                                    IllegalAccessException,
                                 InvocationTargetException {

    String class_name = properties.getProperty(CELL_KEY);

    /************************************
    * create the root tessellation cell *
    ************************************/
    Class c = Class.forName(class_name);
    return (Cell)c.newInstance();

} // end of createRootCell method

/********************************************************************
*
********************************************************************/
public StarFormat createStarFormat(BandMap bands) throws ClassNotFoundException,
                                                         InstantiationException,
                                                         IllegalAccessException,
                                                      InvocationTargetException {

    String name = getProperty(FORMAT_KEY);

    /*****************
    * find the class *
    *****************/
    Class<?> c = Class.forName(name);

    /******************************************
    * first try using a constructor with the
    * band map as an argument
    ******************************************/
    try {
        Class[] types = {BandMap.class};
        Constructor constructor = c.getConstructor(types);

        Object[] args = {bands};
        return (StarFormat)constructor.newInstance(args);

    } catch(NoSuchMethodException e) {}

    /*************************************
    * then try a no argument constructor *
    *************************************/
    try {
        Class[] types = {};
        Constructor constructor = c.getConstructor(types);

        Object[] args = {};
        return (StarFormat)constructor.newInstance(args);


    } catch(NoSuchMethodException e) {}

    /**************************************************
    * if we get here, then there are no constructors
    * of our desired type
    **************************************************/
    throw new IllegalStateException("Can't find constructor for "+name);


} // end of createStarFormat method


} // end of StarCatalog class