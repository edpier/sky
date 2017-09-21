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

/************************************************************************
*
************************************************************************/
public class BandCollector implements CatalogSource {

CatalogSource source;
Set<Band> bands;

/************************************************************************
*
************************************************************************/
public BandCollector(CatalogSource source) {

    this.source = source;

    bands = new HashSet<Band>();

} // end of constructor

/************************************************************************
*
************************************************************************/
public Star nextStar() throws IOException {

    Star star = source.nextStar();
    if(star == null) return null;

    bands.addAll(star.getPhotometry().getBands());

    return star;

} // end of nextStar method

} // end of BandCollector class
