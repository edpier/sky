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

import java.util.*;
import java.io.*;

/********************************************************************
*
********************************************************************/
public class DiskOutputCell extends CascadeOutputCell {

private static int instances =0;
private static int nclosed = 0;

int count;
DataOutputStream out;

boolean keep_open;

/*********************************************************************
*
*********************************************************************/
public DiskOutputCell(CatalogGenerator generator, Cell cell) {

    super(generator, cell);

    count = 0;

    ++instances;
    System.out.println("disk instances="+instances+
                       " open="+(instances-nclosed));

} // end of constructor

/*********************************************************************
*
*********************************************************************/
protected List<Star> getList() throws IOException {

    if(out != null) out.flush();

    NativeSource source = new NativeSource(getFile(),
                                           generator.getStarFormat());

    List<Star> list = new ArrayList<Star>(count);
    Star star;
    while((star = source.nextStar()) != null) {
        list.add(star);
    }

    return list;

} // end of getList method

/*********************************************************************
*
*********************************************************************/
protected void addToList(Star star) throws IOException {

    ++count;

    if(out == null) {
        out = new DataOutputStream(
            new FileOutputStream(getFile(), !keep_open));

    }

    generator.getStarFormat().write(star, out);

    if(!keep_open) {
        out.close();
        out = null;
    }

} // end of addToList method

/*********************************************************************
*
*********************************************************************/
protected int getListSize() { return count; }

/*********************************************************************
*
*********************************************************************/
protected void save() throws IOException {

    if(keep_open) out.close();
    out = null;

    ++nclosed;

} // end of close method


/*********************************************************************
*
*********************************************************************/
protected void abortSave() throws IOException {

    getFile().delete();

    out = null;
    ++nclosed;

} // end of abortSave method
    
} // end of DiskOutputCell class
