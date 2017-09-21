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

/*******************************************************************
*
*******************************************************************/
public class NativeSaver {

CatalogSource source;
File file;
StarFormat format;

/*******************************************************************
*
*******************************************************************/
public NativeSaver(CatalogSource source, File file, StarFormat format) {

    this.source = source;
    this.file = file;
    this.format = format;

} // end of constructor

/*******************************************************************
*
*******************************************************************/
public void save() throws IOException {

    DataOutputStream out = new DataOutputStream(
                           new BufferedOutputStream(
                           new FileOutputStream(file)));

   Star star;
   while((star = source.nextStar()) != null) {
       format.write(star, out);
   }

   out.close();


} // end of save method

} // end of NativeSaver class
