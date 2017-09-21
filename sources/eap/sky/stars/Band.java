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

/************************************************************************
* Represents a particular filter in which the magnitude of a star may
* be measured. The intent is for each filter systems (e.g. Johnson B, V)
* implement a subclass of this one, and each filter to be represented
* by a static final instance of that subclass. Each subclass should have
* a private or protected constructor. This way we may safely compare
* bands with "==".
************************************************************************/
public abstract class Band {

/** Planck's constant in MKS units **/
public static final double PLANCK = 6.626068e-34;

/** Speed of light in MKS units **/
public static final double LIGHT  = 2.99792458e8;

/** for converting between mag and flux */
private static final double EXPONENT = -0.4 * Math.log(10.0);

private static int instances = 0;

String name;
int hash;

double wavelength;
double width;
double zero_point;

/************************************************************************
* Construct a new Band.
* @param name The commonly recognized name of the band. Band names do not
* have to be unique, but you avoid confusion if they are.
* @param wavelength The center wavelength of the filter in meters.
* This should be set to NaN is unknown.
* @param width The equivalent width of the filter in meters.
* This should be set to NaN if unknown.
* @param zero_point The flux in photons/m^2/s corresponding to a magnitude
* of zero. This should be set to NaN if unknown.
************************************************************************/
protected Band(String name, double wavelength, double width, double zero_point) {

    this.name = name;
    this.wavelength = wavelength;
    this.width = width;
    this.zero_point = zero_point;

    hash = (instances++);

} // end of constructor

/************************************************************************
* Construct a new Band with unknown properties.
* @param name The commonly recognized name of the band. Band names do not
* have to be unique, but you avoid confusion if they are.
************************************************************************/
protected Band(String name) {

    this(name, Double.NaN, Double.NaN, Double.NaN);

} // end of constructor

/************************************************************************
*
************************************************************************/
public int hashCode() { return hash; }

/************************************************************************
*
************************************************************************/
public boolean equals(Object o) {

    Band band = (Band)o;
    return hash == band.hash;

} // end of equals method

/************************************************************************
* returns the name of the band specified in the constructor
************************************************************************/
public String getName() { return name; }

/************************************************************************
* returns the name of the band specified in the constructor
************************************************************************/
public String toString() { return name; }

/************************************************************************
* @return The center wavelength in meters
************************************************************************/
public double getWavelength() { return wavelength; }

/************************************************************************
* @return The equivalent width in meters
************************************************************************/
public double getWidth() { return width; }

/************************************************************************
* Compute the flux corresponding to a given magnitude in this band.
* @param mag The magnitude to be converted.
* @return The flux for a given magnitude in this band in photons/m^2.
************************************************************************/
public double toFlux(double mag) {

    return zero_point * Math.exp(EXPONENT * mag);

} // end of toFlux method

/************************************************************************
*
************************************************************************/
public void write(File file) throws IOException {

    DataOutputStream out = new DataOutputStream(
                           new FileOutputStream(file));

    write(out);
    out.close();

} // end of write to a file method

/************************************************************************
* Encode this band to a data stream.
* @param out The data stream
************************************************************************/
public void write(DataOutput out) throws IOException {

    out.writeUTF(getClass().getName());
    out.writeUTF(name);

} // end of write method

/************************************************************************
*
************************************************************************/
public static Band read(File file) throws IOException {

    DataInputStream in = new DataInputStream(
                         new FileInputStream(file));
    Band band = read(in);
    in.close();

    return band;

} // end of read from a file method

/************************************************************************
* Decode a band from a data stream.
* @param in The data stream
************************************************************************/
public static Band read(DataInput in) throws IOException {

    /******************
    * read the values *
    ******************/
    String class_name = in.readUTF();
    if(class_name.startsWith("eap.sky.stars.catalog")) {
        /********************************
        * backward compatibility kludge *
        ********************************/
        class_name = "eap.sky.stars"+class_name.substring(21);
    }
    String name       = in.readUTF();

    /**********************************
    * find the corresponding instance *
    **********************************/
    try {
        Class c = Class.forName(class_name);

        Field[] fields = c.getFields();
        for(int i=0; i<fields.length; ++i) {
            Field field = fields[i];

            Band band = (Band)field.get(null);
            if(band.name.equals(name)) return band;
        }

    } catch(Exception e) {
        throw (IOException)new IOException("Could not read Band").initCause(e);
    }

    throw new IOException("Unknown band "+name+" for class "+class_name);

} // end of read method

} // end of Band class
