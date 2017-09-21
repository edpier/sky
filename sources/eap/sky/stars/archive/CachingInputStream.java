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

package eap.sky.stars.archive;

import java.io.*;

/**********************************************************************
*
**********************************************************************/
public class CachingInputStream extends InputStream {

InputStream in;
OutputStream out;

byte[] buffer;
int size;
int index;

/**********************************************************************
*
**********************************************************************/
public CachingInputStream(InputStream in, File file) throws IOException {

    this(in, new FileOutputStream(file));

    System.out.println("caching "+file.getName());

} // end of constructor from a file

/**********************************************************************
*
**********************************************************************/
public CachingInputStream(InputStream in, OutputStream out) {

    this.in = in;
    this.out = out;

    buffer = new byte[2048];
    size = 0;
    index = 0;

} // end of constructor

/**********************************************************************
*
**********************************************************************/
private boolean readBuffer() throws IOException {

    index = 0;
    size = in.read(buffer);

  //  System.out.println("read buffer size="+size);

    if(size == -1) {
        out.close();
        return true;
    } else {
        out.write(buffer, 0, size);
        return false;
    }

} // end of readBuffer method

/**********************************************************************
*
**********************************************************************/
public int read() throws IOException {

    if(index >=size) {
        boolean eof = readBuffer();
        if(eof) return -1;
    }

    return ((int)(buffer[index++]))&0xff;

} // end of read method

/**********************************************************************
*
**********************************************************************/
public int available() {

    return size-index;

} // end of available method

/**********************************************************************
*
**********************************************************************/
public int read(byte[] b, int offset, int length) throws IOException {

   // System.out.println("requesting "+length+" from "+offset);

    int available = available();
    if(available <=0) {
        boolean eof = readBuffer();
        if(eof) return -1;
        else available = available();
    }

    if(length > available) length = available;

  //  System.out.println("reading length "+length+" available="+available);

    System.arraycopy(buffer, index, b, offset, length);
    index += length;

    return length;

} // end of read array method

/**********************************************************************
*
**********************************************************************/
public void close() throws IOException {

    while(!readBuffer());
    in.close();

} // end of close method


} // end of CachingInputStream class
