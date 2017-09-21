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

/*********************************************************************
*
*********************************************************************/
public class NativeSource implements CatalogSource {

StarFormat format;
DataInput in;

/*********************************************************************
*
*********************************************************************/
public NativeSource(File file, StarFormat format) throws IOException {

    this(file, format, 2048);

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public NativeSource(File file, StarFormat format, int buffer_size) throws IOException {

    this(new DataInputStream(
         new BufferedInputStream(
         new FileInputStream(file), 2048)),
         format);

} // end of file constructor

/*********************************************************************
*
*********************************************************************/
public NativeSource(DataInput in, StarFormat format) {

    this.in = in;
    this.format = format;

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public Star nextStar() throws IOException {

    try { return format.read(in); }
    catch(EOFException e) {
        if(in instanceof DataInputStream) {
            ((DataInputStream)in).close();
        }
        return null;
    }

} // end of nextStar method

} // end of NativeSource class
