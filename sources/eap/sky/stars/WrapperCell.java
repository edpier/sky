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

package eap.sky.stars;

import eap.sky.util.*;

import java.util.*;
import java.io.*;

/*********************************************************************
*
*********************************************************************/
public abstract class WrapperCell extends Cell {

private Cell cell;

/*********************************************************************
*
*********************************************************************/
public WrapperCell(Cell cell) {

    this.cell = cell;

} // end of constructor


/*********************************************************************
*
*********************************************************************/
public Cell getWrappedCell() { return cell; }

/*********************************************************************
*
*********************************************************************/
protected abstract WrapperCell createChild(Cell cell);

/*********************************************************************
*
*********************************************************************/
public final String getName() { return cell.getName(); }

/*********************************************************************
*
*********************************************************************/
public final void createChildren() {

    for(Cell child : cell.getChildren()) {

        WrapperCell wrapper = createChild(child);
        if(wrapper != null) addChild(wrapper);
    }

} // end of createChildren method

/*********************************************************************
*
*********************************************************************/
public final Angle getRadius() { return cell.getRadius(); }

/*********************************************************************
*
*********************************************************************/
public final Direction getCenter() { return cell.getCenter(); }


/*********************************************************************
*
*********************************************************************/
public final boolean contains(Direction dir) { return cell.contains(dir); }

/*********************************************************************
*
*********************************************************************/
public final ArcPath getBoundary() { return cell.getBoundary(); }

} // end of WrapperCell class
