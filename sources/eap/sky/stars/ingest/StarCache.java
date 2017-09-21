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

/**********************************************************************
* @see HybridOutputCell
**********************************************************************/
public class StarCache {

HybridOutputCell cell;
List<Star> list;

/**********************************************************************
*
**********************************************************************/
public StarCache(HybridOutputCell cell) {

    this.cell = cell;
    list = new ArrayList<Star>(cell.getCatalogGenerator().getStarsPerCell());

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public void addStar(Star star) { list.add(star); }

/**********************************************************************
*
**********************************************************************/
public List<Star> getStars() { return list; }

/**********************************************************************
*
**********************************************************************/
public void dump() throws IOException {

  //  System.out.println("dumping "+cell.getName());

    DataOutputStream out = new DataOutputStream(
                           new FileOutputStream(cell.getFile()));

    StarFormat format = cell.getCatalogGenerator().getStarFormat();

    for(Iterator it = list.iterator(); it.hasNext(); ) {
        Star star = (Star)it.next();

        format.write(star, out);



    } // end of loop over stars

    out.close();

   // System.out.println("done dumping "+cell.getName());
    cell.markDumped();

} // end of dump method


/**********************************************************************
*
**********************************************************************/
public void finalize() throws Throwable {

    System.out.println("Finalizing cache for "+cell.getName()+" "+list.size());

    try { dump(); }
    catch(Exception e) {
        e.printStackTrace();
    }

} // end of finalized method



} // end of StarCache class
