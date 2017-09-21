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

import java.lang.ref.*;
import java.util.*;
import java.io.*;

/*******************************************************************
*
*******************************************************************/
public class HybridOutputCell extends CascadeOutputCell {

SoftReference<StarCache> cache_ref;
boolean dumped;
int count;

/*******************************************************************
*
*******************************************************************/
public HybridOutputCell(CatalogGenerator generator, Cell cell) {

    super(generator, cell);

    dumped = false;
    cache_ref = new SoftReference<StarCache>(new StarCache(this));


} // end of constructor

/*******************************************************************
*
*******************************************************************/
public synchronized void markDumped() {

   // System.out.println("marking dumped for "+getName());
    dumped = true;
    notifyAll();

} // end of markDumped method

/*******************************************************************
*
*******************************************************************/
private StarCache getStarCache() {

    if(dumped) return null;

    StarCache cache = (StarCache)cache_ref.get();
    if(cache != null) return cache;

    /**********************************************
    * if we get here the cache has been cleared
    * wait until it has been finalized
    **********************************************/
    synchronized(this) {
       // Runtime.getRuntime().runFinalization();
        while(!dumped) {
            System.out.println("waiting until dumped "+getName());
            try { wait(); }
            catch(InterruptedException e) {}
        }

    } // end of synchronized block

    return null;



} // end of getStarCache method

/*********************************************************************
*
*********************************************************************/
protected List<Star> getList() throws IOException {

    StarCache cache = getStarCache();
    if(cache != null) return cache.getStars();
    else {
        NativeSource source = new NativeSource(getFile(),
                                            generator.getStarFormat());

        List<Star> list = new ArrayList<Star>(count);
        Star star;
        while((star = source.nextStar()) != null) {
            list.add(star);
        }

        return list;
    } // end if the cache has been dumped

} // end of getList method

/*********************************************************************
*
*********************************************************************/
protected void addToList(Star star) throws IOException {

    ++count;

    StarCache cache = getStarCache();
    if(cache != null) cache.addStar(star);
    else {

        DataOutputStream out = new DataOutputStream(
                               new FileOutputStream(getFile(), true));

        generator.getStarFormat().write(star, out);
        out.close();

    } // end if the cache has been dumped

} // end of addToList method

/*********************************************************************
*
*********************************************************************/
protected int getListSize() { return count; }

/*********************************************************************
*
*********************************************************************/
protected void save() throws IOException {

    StarCache cache = getStarCache();
    if(cache != null) {
        cache_ref.clear();
    }

} // end of close method

/*********************************************************************
*
*********************************************************************/
protected void abortSave() throws IOException {

    cache_ref.clear(); // ?? is this the right thing to do???
    getFile().delete();

} // end of close method


} // end of HybridOutputCell class
