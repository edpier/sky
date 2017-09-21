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

/************************************************************************
* A cell propagator which creates memory cells while there is
* memory available. After this, it creates disk cells.
* @see MemoryOutputCell
* @see DiskOutputCell
************************************************************************/
public class MemoryPropagator extends CellPropagator {

/***********************************************************************
*
***********************************************************************/
public OutputCell createRoot(CatalogGenerator generator, Cell root) {

    return new MemoryOutputCell(generator, root);


} // end of createRoot method

/***********************************************************************
*
***********************************************************************/
public OutputCell createChild(OutputCell parent, Cell cell) {

    /**********************************************************
    * find out how much memory we have left.
    * if we are running out, then go to a disk-based scheme
    **********************************************************/
    Runtime runtime = Runtime.getRuntime();
    long free = runtime.freeMemory();
    long max = runtime.maxMemory();

    long overhead =10000000l;
    if(max != Long.MAX_VALUE) {
        free += (max - runtime.totalMemory());

        long fraction = max/5;
        if(overhead < fraction) overhead = fraction;
    }

    CatalogGenerator generator = parent.getCatalogGenerator();

    if(free < overhead) {
        /******************************************
        * this flavor writes directly to the disk.
        * it uses very little memory, but is slow
        ******************************************/
        return new DiskOutputCell(generator, cell);

    } else {
        /*********************************************************
        * this flavor holds the stars in memory
        * so it is faster, but can use a prohibitively large
        * amout of memory if you are processing a large catalog
        *********************************************************/
        return new MemoryOutputCell(generator, cell);
    }

} // end of createChild method

} // end of MemoryPropagator class