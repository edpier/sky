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
public class DetectionPair {

Detection first;
Detection second;

/*********************************************************************
*
*********************************************************************/
public DetectionPair(Detection first, Detection second) {

    this.first  = first;
    this.second = second;

} // end of constructor

/*********************************************************************
*
*********************************************************************/
public Detection getFirst() { return first; }

/*********************************************************************
*
*********************************************************************/
public Detection getSecond() { return second; }

/*********************************************************************
*
*********************************************************************/
public double getDeltaX() {

    return second.getX() - first.getX();

} // end of getDeltaX method

/*********************************************************************
*
*********************************************************************/
public double getDeltaY() {

    return second.getY() - first.getY();

} // end of getDeltaY method


/*********************************************************************
*
*********************************************************************/
public double getMeanX() {

    return 0.5*(second.getX() + first.getX());

} // end of getMeanX method

/*********************************************************************
*
*********************************************************************/
public double getMeanY() {

    return 0.5*(second.getY() + first.getY());

} // end of getMeanY method

/*********************************************************************
*
*********************************************************************/
Collection<DetectionPair> matchNearestNeighbors(Collection<Detection> dets1,
                                                Collection<Detection> dets2,
                                                double max_dist) {

    List<DetectionPair> pairs = new ArrayList<DetectionPair>();

    double max_dist2 = max_dist*max_dist;

    /**********************************
    * load the lists into sorted sets *
    **********************************/
    SortedSet<Detection> list1 = new TreeSet<Detection>(dets1);
    SortedSet<Detection> list2 = new TreeSet<Detection>(dets2);

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
            pairs.add(new DetectionPair(det1, closest));
            list2.remove(closest);
        }

        /***********************************
        * one way or another we are done
        * with the first detection
        ***********************************/
        list1.remove(det1);

    } // end of loop while at least one list has something in it

    return pairs;

} // end of matchNearestNeighbors method

} // end of DetectionPair class