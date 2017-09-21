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

/***********************************************************************
* A CellPropagator which creates memory and dead end cells.
* It starts out creating memory cells, and then after it reaches a
* maximum number, it switches to dead end cells. The DepthFirstGenerator
* uses this propagator. Other CatalogGenerators probably shouldn't.
* @see DepthFirstGenerator
***********************************************************************/
public class LimitedDepthPropagator extends CellPropagator {

int max_cells;
int ncells;

/***********************************************************************
*
***********************************************************************/
public LimitedDepthPropagator(int max_cells) {

    this.max_cells = max_cells;
    ncells = 0;

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public void reset() { ncells = 0; }

/***********************************************************************
*
***********************************************************************/
public OutputCell createRoot(CatalogGenerator generator, Cell cell) {

    ++ncells;
    return new MemoryOutputCell(generator, cell);

} // end of createRoot method

/***********************************************************************
*
***********************************************************************/
public OutputCell createChild(OutputCell parent, Cell cell) {

    CatalogGenerator generator = parent.getCatalogGenerator();

    if(ncells <= max_cells) {
        ++ncells;
        return new MemoryOutputCell(generator, cell);
    } else {
        ++ncells;
        return new DeadEndCell(generator, cell);
    }

} // end of createChild methd

} // end of UniformPropagator class