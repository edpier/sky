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

/*********************************************************************
*
*********************************************************************/
public class URLArchive extends Archive {

URL base;

/*********************************************************************
*
*********************************************************************/
public URLArchive(URL base) {

    this.base = base;

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public boolean equals(Object o) {

    if(!(o instanceof URLArchive)) return false;

    URLArchive archive = (URLArchive)o;
    return base.equals(archive.base);

} // end of equals method

/**********************************************************************
*
**********************************************************************/
protected URL makeURL(String name) throws MalformedURLException  {

    return new URL(base, name);

} // end of makeURL method

/**********************************************************************
*
**********************************************************************/
public InputStream getInputStream(String name) throws IOException {

    URL url = makeURL(name);
    URLConnection connection = url.openConnection();
    if(connection instanceof HttpURLConnection) {

        int code = ((HttpURLConnection)connection).getResponseCode();
        if(code == HttpURLConnection.HTTP_NOT_FOUND) return null;

    }

    return connection.getInputStream();

} // end of getInputStream method

} // end of DirectoryArchive class
