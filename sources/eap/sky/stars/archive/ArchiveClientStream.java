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
import java.net.*;

/***********************************************************************
*
***********************************************************************/
public class ArchiveClientStream extends InputStream {

String host;
int port;

DataOutputStream out;
DataInputStream in;
int remaining;

/***********************************************************************
*
***********************************************************************/
public ArchiveClientStream(String host, int port) {

    this.host = host;
    this.port = port;

    try { connect(); }
    catch(IOException e) { e.printStackTrace(); }

    remaining = 0;

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public void open(String archive_name, String file_name) {

  //  for(int trial = 1; trial<= 2; ++trial) {
    try {
        ensureConnection();
        out.writeUTF(archive_name);
        out.writeUTF(file_name);

        remaining = in.readInt();
    } catch(IOException e) {

    }

} // end of open method

/***********************************************************************
*
***********************************************************************/
private void ensureConnection() throws IOException {

    if(in == null || out == null) connect();

} // end of ensureConnection method

/***********************************************************************
*
***********************************************************************/
private void connect() throws IOException {

    Socket socket = new Socket(host, port);

    in  = new  DataInputStream(socket.getInputStream() );
    out = new DataOutputStream(socket.getOutputStream());

} // end of connect method

/***********************************************************************
*
***********************************************************************/
public int available() throws IOException {

    int available = in.available();
    if(available < remaining) return available;
    else                      return remaining;

} /// end of available

/***********************************************************************
*
***********************************************************************/
public void close() throws IOException { in.skip(remaining); }

/***********************************************************************
*
***********************************************************************/
public int read() throws IOException {

    if(remaining <= 0) return -1;
    else {
        --remaining;
        return in.read();
    }

} // end of read method


} // end of ArchiveClientStream class