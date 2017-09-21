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

/***********************************************************************
*
***********************************************************************/
public class ArchiveClient extends Archive {

String host;
int port;

/***********************************************************************
*
***********************************************************************/
public ArchiveClient(String host, int port) {

    this.host = host;
    this.port = port;

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public boolean equals(Object o) {

    ArchiveClient client = (ArchiveClient)o;

    return host.equals(client.host) && port == client.port;

} // end of equals method

/**********************************************************************
*
**********************************************************************/
public InputStream getInputStream(String name) throws IOException {

    return null;

} // end of getInputStream

} // end of ArchiveServer class