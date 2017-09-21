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

import java.lang.reflect.*;

/***********************************************************************
* A Cell propagator which only creates cells of a particular class.
* The class is specified using a Class object.
***********************************************************************/
public class UniformPropagator extends CellPropagator {

Constructor constructor;

/***********************************************************************
*
***********************************************************************/
public UniformPropagator(Class<?> c) {

    if(!OutputCell.class.isAssignableFrom(c)) {
        throw new IllegalArgumentException(c.getName()+
                                           "is not an OutputCell class");
    }

    Class[] template = {CatalogGenerator.class, Cell.class};
    try { constructor = c.getConstructor(template); }
    catch(Exception e) {
        throw (IllegalArgumentException)
           new IllegalArgumentException("Could not get constructor")
           .initCause(e);
    }


} // end of constructor

/***********************************************************************
*
***********************************************************************/
public OutputCell createRoot(CatalogGenerator generator, Cell cell) {

    Object[] args = {generator, cell};
    try { return (OutputCell)constructor.newInstance(args); }
    catch(Exception e) {
        throw (IllegalArgumentException)
           new IllegalArgumentException("Unexpected exception")
           .initCause(e);
    }

} // end of createRoot method

/***********************************************************************
*
***********************************************************************/
public OutputCell createChild(OutputCell parent, Cell cell) {

    CatalogGenerator generator = parent.getCatalogGenerator();

    Object[] args = {generator, cell};
    try { return (OutputCell)constructor.newInstance(args); }
    catch(Exception e) {
        throw (IllegalArgumentException)
           new IllegalArgumentException("Unexpected exception")
           .initCause(e);
    }

} // end of createChild methd

} // end of UniformPropagator class
