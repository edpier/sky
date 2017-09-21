// Copyright 2013 Edward Alan Pier
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

package eap.sky.time.gti;

import eap.sky.time.*;

import java.util.*;

/***********************************************************************
*
***********************************************************************/
public class GTIList  {

// private static final SortedSet<GTI> EMPTY = Collections.unmodifiableSortedSet(
//                                                             new TreeSet());

SortedSet<GTI> gtis;
SortedSet<GTI> read_only_gtis;

/***********************************************************************
*
***********************************************************************/
public GTIList() {

    gtis = new TreeSet<GTI>();
    read_only_gtis = Collections.unmodifiableSortedSet(gtis);

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public GTIList copy() {

    GTIList list = new GTIList();
    list.gtis.addAll(gtis);

    return list;

} // end of copy method


/***********************************************************************
*
***********************************************************************/
public GTIList(GTIList list) {

    gtis = new TreeSet<GTI>(list.gtis);
    read_only_gtis = Collections.unmodifiableSortedSet(gtis);
}

/***********************************************************************
*
***********************************************************************/
public boolean equals(Object o) {

    GTIList list = (GTIList)o;

    if(gtis.size() != list.gtis.size() ) return false;

    Iterator it1 = gtis.iterator();
    Iterator it2 = list.gtis.iterator();

    while(it1.hasNext() ) {
        GTI gti1 = (GTI)it1.next();
        GTI gti2 = (GTI)it2.next();

        if(!gti1.equals(gti2)) return false;

    } // end of loop over GTIs

    return true;

} // end of equals method

/***********************************************************************
* Returns a new list of the GTIs in this GTI list
***********************************************************************/
public List getGTIs() { return new ArrayList<GTI>(gtis); }

/***********************************************************************
*
***********************************************************************/
public PreciseDate getStart() {

    if(gtis.size() == 0) return null;

    GTI first = (GTI)gtis.first();

    return first.getStart();

} // end of getStart method


/***********************************************************************
* Finds the first GTI which contains the given time or comes after it.
* if there is no such GTI, then return null.
***********************************************************************/
public GTI find(PreciseDate time) {

    PreciseDate stop = (PreciseDate)time.clone();
    stop.increment(1.0);
    GTI probe = new GTI(time, stop);

    /**********************************************
    * first check if there is a GTI which starts
    * before the given time, but ends after it
    **********************************************/
    SortedSet head = gtis.headSet(probe);
    if(head.size() > 0) {
        GTI last = (GTI)head.last();
        if(last.contains(time)) return last;
    }

    /*******************************
    * if we get here, then the answer
    * is not in the head set
    ***********************************/
    SortedSet tail = gtis.tailSet(probe);

    if(tail.size() == 0) return null;
    else                 return (GTI)tail.first();

} // end of find method

/***********************************************************************
*
***********************************************************************/
public boolean contains(PreciseDate time) {

    GTI gti = find(time);

    if(gti==null) return false;
    else          return gti.contains(time);

} // end of contains method

/***********************************************************************
*
***********************************************************************/
public int size() { return gtis.size(); }

/***********************************************************************
*
***********************************************************************/
public Iterator<GTI> iterator() { return read_only_gtis.iterator(); }

/***********************************************************************
*
***********************************************************************/
private SortedSet<GTI> findOverlaps(GTI gti) {

    /*******************************
    * special case of an empty set *
    *******************************/
    if(gtis.size() == 0) return gtis;

    /************************************
    * split the set into the ones before
    * and the ones after
    *************************************/
    SortedSet<GTI> head = gtis.headSet(gti);
    SortedSet<GTI> tail = gtis.tailSet(gti);

  //  Otis.out.println("head size="+head.size()+" tail size="+tail.size());

    GTI first = null;
    GTI last  = null;

    /***********************************************
    * see if the first element was in the head set *
    ***********************************************/
    if(head.size() >0) {
        GTI before = (GTI)head.last();
        if(before.overlaps(gti)) first = before;
    }

    /********************************************
    * look for the last element in the tail set *
    ********************************************/
    for(Iterator it = tail.iterator(); it.hasNext(); ) {
        GTI after = (GTI)it.next();

        if(!after.overlaps(gti) ) {
            /******************************************************
            * we have found the first element after the given GTI *
            ******************************************************/
            last = after;
            break;
        }
    }

    if(first == null) {
        /**************************************************
        * we don't include any elements form the head set *
        **************************************************/
        if(last==null) {
            /***************************************************
            * there was no end to the tail set so we include
            * everything. Note this covers the case where the
            * tail set is empty just as well
            ***************************************************/
            return tail;

        } else {
            /*******************************************
            * we need some subset of the tail set,
            * so we need to mark the first element
            * and the first in the tail set, so that
            * we can extract a subset later
            *******************************************/
            first = (GTI)tail.first();
        }
    }

    /***************************************************
    * we couldn't find a last element so  the subset
    * goes to the end. Note that if we get here
    * we have found a first element somewhere
    **************************************************/
    if(last == null) return gtis.tailSet(first);

    return gtis.subSet(first, last);


} // end of getOverlaps method

/***********************************************************************
*
***********************************************************************/
public void add(GTI gti) {

    /***************************
    * get the overlapping GTIS *
    ***************************/
    SortedSet overlaps = findOverlaps(gti);

    /**********************************************
    * if nothing overlaps, then just add this GTI *
    **********************************************/
    if(overlaps.size() == 0) {
        gtis.add(gti);
        return;
    }

    /*************************************************
    * find the widest enclosing start and stop times *
    *************************************************/
    GTI first = (GTI)overlaps.first();
    GTI last  = (GTI)overlaps.last();

    if(gti.compareStartTo(first) < 0) first = gti;
    if(gti.compareStopTo(last)   > 0) last  = gti;

    /******************************************
    * clobber all the overlapping GTIs
    * and insert one which consolidates them
    ******************************************/
    overlaps.clear();
    gtis.add(new GTI(first, last));

} // end of add single GTI method

/***********************************************************************
*
***********************************************************************/
public GTIList or(GTIList list) {

    /************************************
    * make a fresh copy of the GTI list *
    ************************************/
    list = new GTIList(list);

    /********************************
    * add all the GTIs in this list *
    ********************************/
    for(Iterator it = gtis.iterator(); it.hasNext(); ) {
        GTI gti = (GTI)it.next();

        list.add(gti);
    }

    return list;

} // end of orWith GTIList method

/***********************************************************************
*
***********************************************************************/
public GTIList and(GTIList list) {

    GTIList and = new GTIList();

    for(Iterator it = gtis.iterator(); it.hasNext(); ) {
        GTI gti = (GTI)it.next();

        SortedSet<GTI> overlaps = list.findOverlaps(gti);

        if(overlaps.size() == 1) {
            /************************************
            * a single GTI which could overlap
            * on both ends
            ************************************/
            GTI first = (GTI)overlaps.first();

            GTI from;
            if(gti.compareStartTo(first) < 0) from = first;
            else                              from = gti;

            GTI to;
            if(gti.compareStopTo(first) > 0) to = first;
            else                             to = gti;

            if(!from.abuts(to)) and.gtis.add(new GTI(from, to));

        } else if(overlaps.size() > 1) {
            /***********************************
            * multiple overlaps. We treat the
            * first and last specially and
            * copy over the interior ones
            ***********************************/
            GTI first = (GTI)overlaps.first();

            GTI from;
            if(gti.compareStartTo(first) < 0) from = first;
            else                              from = gti;

            and.gtis.add(new GTI(from, first));

            /***********************
            * and now the last one *
            ***********************/
            GTI last = (GTI)overlaps.last();
            GTI to;
            if(gti.compareStopTo(last) > 0) to = last;
            else                            to = gti;

            and.gtis.add(new GTI(last, to));

            /***************************
            * now copy over the middle *
            ***************************/
            Iterator<GTI> it2 = overlaps.subSet(first, last).iterator();
            it2.next();
            while(it2.hasNext()) {
                and.gtis.add(it2.next());
            }

        } // end if there are multple overlaps

    } // end of loop over GTIs.

    return and;

} // end of and method

/***********************************************************************
*
***********************************************************************/
public void dump() {

    for(Iterator it = gtis.iterator(); it.hasNext(); ) {
        GTI gti = (GTI)it.next();
        System.out.println(gti);

    }

} // end of dump method

} // end of GTIList class
