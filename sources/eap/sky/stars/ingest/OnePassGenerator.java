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
*
**********************************************************************/
public class OnePassGenerator extends CatalogGenerator {

CatalogSource source;
OutputCell root;


/************************************************************************
*
************************************************************************/
public OnePassGenerator(CatalogSource source, File dir, int max_stars,
                        BandMap bands, Band band, StarFormat format,
                        String name, String version, Cell root,
                        CellPropagator propagator) {

    super(dir, max_stars, bands, band, format, name, version, propagator, root);

    this.source = source;
    this.root = propagator.createRoot(this, root);

} // end of constructor


/************************************************************************
*
************************************************************************/
protected void binStars() throws IOException {

    /*********************
    * read all the stars *
    *********************/
    int count=0;
    Star star;
    while((star=source.nextStar()) != null) {

        root.addStar(star);
        ++count;

        if(count % 100000 == 0) {
            System.out.println("read "+count+
                               " stars to mag "+star.getMagnitude(band));
        }

    } // end of loop over stars;

    /************************************
    * write out all the remaining cells *
    ************************************/
    root.closeAll();

} // end of binStars method


} // end of CatalogGenerator class
