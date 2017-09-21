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

package eap.sky.image;

import java.util.*;

/*********************************************************************
*
*********************************************************************/
public class NearestNeighbors extends PairList {

double max_dist2;

/*********************************************************************
*
*********************************************************************/
public NearestNeighbors(DetectionList dets1, DetectionList dets2,
                        double max_dist) {

    super(dets1, dets2);
    this.max_dist2 = max_dist*max_dist;

    update();

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public void setMaxDist(double max_dist) {

    this.max_dist2 = max_dist*max_dist;
    update();
    fireChangeEvent();

} // end of setMaxDist method

/*********************************************************************
*
*********************************************************************/
public void update() {

    clear();

    /**********************************
    * load the lists into sorted sets *
    **********************************/
    SortedSet<Detection> list1 = new TreeSet<Detection>(dets1.getDetections());
    SortedSet<Detection> list2 = new TreeSet<Detection>(dets2.getDetections());

    /***************************************
    * loop until we empty one of the lists *
    ***************************************/
    while(list1.size()>0 && list2.size() >0) {

        /**************************************************
        * get the brightest detection from the first list *
        **************************************************/
        Detection det1 = list1.first();

        /***********************************************
        * find the closest detection in the other list *
        ***********************************************/
        Detection closest = null;
        double closest_dist2=Double.MAX_VALUE;
        for(Detection det2 : list2) {

            /************************************
            * compute the distance between the
            * detections
            ************************************/
            double dist2 = det1.distanceSquared(det2);

            /************************************
            * make sure the distance is within
            * the realm of possibility
            ************************************/
            if(dist2 >max_dist2) continue;

            /*******************************
            * check if we have a detection *
            *******************************/
            if(closest == null || dist2 < closest_dist2) {
                closest = det2;
                closest_dist2 = dist2;
            }
        } // end of loop over other detections

        /*********************************
        * make a pair if we have a match *
        *********************************/
        if(closest != null) {
            add(new DetectionPair(det1, closest));
            list2.remove(closest);
        }

        /***********************************
        * one way or another we are done
        * with the first detection
        ***********************************/
        list1.remove(det1);

    } // end of loop while at least one list has something in it

} // end of update method

} // end of NearestNeighbors class