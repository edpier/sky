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

import eap.sky.util.numerical.*;
import eap.sky.util.numerical.matrix.*;

import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;

/*********************************************************************
*
*********************************************************************/
public abstract class PairList {

protected DetectionList dets1;
protected DetectionList dets2;

private List<DetectionPair> pairs;
private Collection<ChangeListener> listeners;



/*********************************************************************
*
*********************************************************************/
public PairList(DetectionList dets1, DetectionList dets2) {

    this.dets1 = dets1;
    this.dets2 = dets2;

    pairs = new ArrayList<DetectionPair>();
    listeners = new HashSet<ChangeListener>();

    Updater updater = new Updater();
    dets1.addChangeListener(updater);
    dets2.addChangeListener(updater);

} // end of constructor


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
public int size() { return pairs.size(); }

/*********************************************************************
*
*********************************************************************/
protected void clear() {

    pairs.clear();

} // end of clear method

/*********************************************************************
*
*********************************************************************/
protected void add(DetectionPair pair) { pairs.add(pair); }

/*********************************************************************
*
*********************************************************************/
public Collection<DetectionPair> getPairs() {

    return Collections.unmodifiableCollection(pairs);

} // end of getPairs method

/*********************************************************************
*
*********************************************************************/
protected abstract void update();

/*********************************************************************
*
*********************************************************************/
public AffineTransform fitRigidTransform() throws NoConvergenceException{

    LeastSquaresFit fit = new LeastSquaresFit(pairs.size()*2, 4);

    /****************
    * load the data *
    ****************/
    for(DetectionPair pair : pairs) {
        Detection p1 = pair.getFirst();
        Detection p2 = pair.getSecond();

        fit.addFunctionValue( p1.getX());
        fit.addFunctionValue(-p1.getY());
        fit.addFunctionValue(1.0);
        fit.addFunctionValue(0.0);

        fit.addMeasuredValue(p2.getX());


        fit.addFunctionValue(p1.getY());
        fit.addFunctionValue(p1.getX());
        fit.addFunctionValue(0.0);
        fit.addFunctionValue(1.0);

        fit.addMeasuredValue(p2.getY());

    } // end of loop over points

    /*************
    * do the fit *
    *************/
    double[] result = fit.fit();


    /*********************************
    * construct the affine transform *
    *********************************/
    return new AffineTransform(result[0], result[1],
                              -result[1], result[0],
                               result[2], result[3]);


} // end of fitRigidTransform method

/*********************************************************************
*
*********************************************************************/
public void write(PrintWriter writer) {

    writer.println("# name1 x1 y1 mag1 name2 x2 y2 mag2");

    for(DetectionPair pair : getPairs()) {
        Detection det1 = pair.getFirst();
        Detection det2 = pair.getSecond();

        writer.println(det1.getName()+" "+det1.getX()+" "+det1.getY()+" "+det1.getMagnitude()+" "+
                       det2.getName()+" "+det2.getX()+" "+det2.getY()+" "+det2.getMagnitude());

    } // end of loop over pairs

    writer.flush();

} // end of write method

/*********************************************************************
*
*********************************************************************/
public void write(File file) throws IOException {

    write(new PrintWriter(file));

} // end of write to File method

/*********************************************************************
*
*********************************************************************/
public void write() throws IOException {

    write(new PrintWriter(System.out));

} // end of write to stdout method

/***********************************************************************
*
***********************************************************************/
protected void fireChangeEvent() {

    ChangeEvent e = new ChangeEvent(this);

    for(ChangeListener l : listeners) {
        l.stateChanged(e);
    }

} // end of fireChangeEvent method

/*********************************************************************
*
*********************************************************************/
private class Updater implements ChangeListener {

/*********************************************************************
*
*********************************************************************/
public void stateChanged(ChangeEvent e) {

    update();
    fireChangeEvent();

} // end of stateChanged method

} // end of Updater inner class

} // end of PairList class