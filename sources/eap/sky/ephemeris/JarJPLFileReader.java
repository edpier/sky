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

package eap.sky.ephemeris;

import java.io.*;
import java.rmi.*;

/***************************************************************************
*
***************************************************************************/
public class JarJPLFileReader implements JPLFileReader {

/***************************************************************************
*
***************************************************************************/
public JPLFile readFile(String year) throws IOException {

    String name ="ascp"+year+".405";

    ClassLoader loader = JarJPLFileReader.class.getClassLoader();

    java.net.URL url = loader.getResource("eap/sky/ephemeris/jplde405/"+name);
    if(url==null) {
        throw new IOException("No ephemeris file for "+year);
    }

    /****************
    * read the file *
    ****************/
    JPLFile file = new JPLFile();
    file.read(url.openStream());

    return file;

} // end of readFile method

} // end of JPLFileReader interface
