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
public class LocalJPLFileReader implements JPLFileReader {

File dir;

/***************************************************************************
*
***************************************************************************/
public LocalJPLFileReader(File dir) {

    this.dir = dir;

} // end of constructor


/***************************************************************************
*
***************************************************************************/
public JPLFile readFile(String year) throws IOException {

    /***********************
    * try reading the file *
    ***********************/
    String name ="ascp"+year+".405";

    InputStream in = new FileInputStream(new File(dir, name));

    JPLFile file = new JPLFile();
    file.read(in);

    return file;

} // end of readFile method

} // end of JPLFileReader class