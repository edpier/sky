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
public class MemorySorter implements CatalogSource {

List<Star> stars;
Iterator it;

/********************************************************************
*
********************************************************************/
public MemorySorter(CatalogSource source, Band band) throws IOException {

    stars = new ArrayList<Star>();

    /*******************************
    * read the stars into the list *
    *******************************/
    System.out.println("reading...");
    Star star;
    while((star = source.nextStar()) != null) {
  //  System.out.println(star);
        stars.add(star);
        if(stars.size() % 100000 == 0) System.out.println("    "+stars.size());
    }

    /*****************************
    * sort the list by magnitude *
    *****************************/
    System.out.println("sorting...");
    Collections.sort(stars, new MagComparator(band) );

    System.out.println("Done sorting");

    it = stars.iterator();

} // end of costructor

/********************************************************************
*
********************************************************************/
public Star nextStar() {

    if(it.hasNext()) return (Star)it.next();
    else {
        stars = null;
        it = null;
        return null;
    }

} // end of nextStar method


} // end of MemorySorter
