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

import java.lang.reflect.*;
import java.util.*;
import java.io.*;

/***********************************************************************
* This class maps between {@link Band}s and unique strings.
* Note it is not efficient to use this class to accumulate all the bands
* for a given catalog. Instead collect them in a Set first
***********************************************************************/
public class BandMap {

Map<String, Band> bands;
Map<Band, String> names;

List<Band> list;
Band[] array;

/***********************************************************************
*
***********************************************************************/
public BandMap(Class c) {

    this();

    add(c);

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public BandMap() {

    bands = new LinkedHashMap<String, Band>();
    names = new LinkedHashMap<Band, String>();

} // end of constructor

/***********************************************************************
*
***********************************************************************/
private void updateBandArray() {

    list = Collections.unmodifiableList(new ArrayList<Band>(names.keySet()));

} // end of updateBandArray method

/***********************************************************************
*
***********************************************************************/
public Band[] getBandArray() {

    List<Band> list = new ArrayList<Band>(names.keySet());
    return (Band[])list.toArray(new Band[list.size()]);

} // end of getBandArrayMethod

/***********************************************************************
*
***********************************************************************/
public List<Band> getBands() {

    return list;

} // end of getBands method

/***********************************************************************
*
***********************************************************************/
public Band getBand(String name) { return (Band)bands.get(name); }

/***********************************************************************
*
***********************************************************************/
public String getName(Band band) { return (String)names.get(band); }

/***********************************************************************
*
***********************************************************************/
private void addWithoutChecking(String name, Band band) {

    bands.put(name, band);
    names.put(band, name);

} // end of addWithoutChecking method

/***********************************************************************
*
***********************************************************************/
public void add(String name, Band band) {

    /*******************
    * uniqueness check *
    *******************/
    Band orig = getBand(name);
    if(orig != null && !orig.equals(band) ) {
        throw new IllegalArgumentException(name+" already used for "+orig);
    }

    addWithoutChecking(name, band);
    updateBandArray();

} // end of add method

/***********************************************************************
*
***********************************************************************/
public void add(Band band) {

    /************************************************
    * see if we have an entry for this band already *
    ************************************************/
    if(names.containsKey(band)) return;

    for(int index = 0; ; ++index) {

        String name = band.getName();
        if(index > 0) name = name+index;

        Band orig = getBand(name);
        if(orig == null) {
            /**********************
            * this name is unused *
            **********************/
            addWithoutChecking(name, band);
            updateBandArray();
            return;

        }
    } // end of loop over possible names

} // end of add method

/***********************************************************************
*
***********************************************************************/
public void add(Class c) {

    /******************************
    * make sure it's a band class *
    ******************************/
    if(!Band.class.isAssignableFrom(c)) {
        throw new IllegalArgumentException(c.getName()+
                                           " is not a subclass of "+
                                           Band.class.getName());
    }

    Field[] fields = c.getDeclaredFields();
    for(int i=0; i< fields.length; ++i) {
        Field field = fields[i];
        if(!Modifier.isStatic(field.getModifiers())) continue;
        if(!Modifier.isPublic(field.getModifiers())) continue;
        if(!Band.class.isAssignableFrom(field.getDeclaringClass())) continue;

        try {
            Band band = (Band)field.get(null);
            add(band);
        } catch(IllegalAccessException e) {e.printStackTrace(); }

    } // end of loop over fields

} // end of add method

/***********************************************************************
*
***********************************************************************/
public void write(File file) throws IOException {

    DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
    write(out);

    out.close();

} // end of write method

/***********************************************************************
*
***********************************************************************/
public void write(DataOutput out) throws IOException {

    /****************************
    * write the number of bands *
    ****************************/
    out.writeInt(bands.size());

    for(Iterator it = bands.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry entry = (Map.Entry)it.next();
        String name = (String)entry.getKey();
        Band band   =   (Band)entry.getValue();

        out.writeUTF(name);
        band.write(out);

    } // end of loop over bands

} // end of write method

/***********************************************************************
*
***********************************************************************/
public static BandMap read(File file) throws IOException {

    DataInputStream in = new DataInputStream(new FileInputStream(file));

    BandMap map = read(in);
    in.close();

    return map;

} // end of write method

/***********************************************************************
*
***********************************************************************/
public static BandMap read(DataInput in) throws IOException {

    int nbands = in.readInt();

    BandMap map = new BandMap();
    for(int i=0; i< nbands; ++i) {
        String name = in.readUTF();
        Band band = Band.read(in);

        map.add(name, band);

    } // end of loop over bands

    return map;

} // end of read method


} // end of BandMap class

