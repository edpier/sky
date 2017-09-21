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

import java.io.*;
import java.util.*;
import javax.swing.event.*;

/*********************************************************************
*
*********************************************************************/
public class DetectionList implements Serializable {

private Collection<ChangeListener> listeners;
private List<Detection> all_detections;
private List<Detection> detections;

double mag_limit;

/*********************************************************************
*
*********************************************************************/
public DetectionList() {

    listeners = new HashSet<ChangeListener>();

    all_detections = new ArrayList<Detection>();
        detections = new ArrayList<Detection>();

    mag_limit = Double.MAX_VALUE;

} // end of constructor

/****************************************************************************
*
****************************************************************************/
private void writeObject(ObjectOutputStream out) throws IOException {

    out.writeObject(all_detections);
    out.writeDouble(mag_limit);

} // end of writeObject method

/****************************************************************************
*
****************************************************************************/
private void readObject(ObjectInputStream in) throws IOException,
                                           ClassNotFoundException  {

    all_detections = (List<Detection>)in.readObject();
    mag_limit      = in.readDouble();

    listeners  = new HashSet<ChangeListener>();
    detections = new ArrayList<Detection>();
    updateMagnitudeSelection();

} // end of readObject method

/*********************************************************************
*
*********************************************************************/
public void addChangeListener(ChangeListener l ) {

    listeners.add(l);

} // end of addChangeListener method

/***********************************************************************
*
***********************************************************************/
public void removeChangeListener(ChangeListener l) {

    listeners.remove(l);

} // end of removeChageListener method

/*********************************************************************
*
*********************************************************************/
public void clear() {

    all_detections.clear();
    detections.clear();

} // end of clear method

/*********************************************************************
*
*********************************************************************/
public void setLimitingMagnitude(double limit) {

    this.mag_limit = limit;
    updateMagnitudeSelection();

} // end of setMagnitudeLimit method

/*********************************************************************
*
*********************************************************************/
public void add(Detection det) { all_detections.add(det); }

/*********************************************************************
*
*********************************************************************/
public void updateMagnitudeSelection() {

    detections.clear();
    for(Detection det : all_detections) {

        /********************************
        * skip the star if it's too dim *
        ********************************/
        if(det.getMagnitude() <= mag_limit) {

            detections.add(det);
        }

    } // end of loop over detections

    fireChangeEvent();

} // end of selectByMagnitude method

/*********************************************************************
*
*********************************************************************/
public Collection<Detection> getDetections() {

    return Collections.unmodifiableCollection(detections);

} // end of getDetections method

/***********************************************************************
*
***********************************************************************/
private void fireChangeEvent() {

    ChangeEvent e = new ChangeEvent(this);

    for(ChangeListener l : listeners) {
        l.stateChanged(e);
    }

} // end of fireChangeEvent method

} // end of DetectionList class