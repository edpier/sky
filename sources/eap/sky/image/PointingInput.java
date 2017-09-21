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

import eap.sky.time.*;
import eap.sky.util.*;
import eap.sky.util.coordinates.*;
import eap.sky.util.plane.*;

import java.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;

/******************************************************************************
*
******************************************************************************/
public class PointingInput implements ActionListener, ChangeListener {

ImageDisplay display;
BufferedImage image;
StarDetectionList stars;
DetectionOverlay overlay;
ImageParams params;
AzAlt az_alt;
PreciseDate time;

JSpinner  ra_field;
JSpinner dec_field;
JSpinner  pa_field;

JRadioButton hms_button;
JRadioButton deg_button;

/******************************************************************************
*
******************************************************************************/
public PointingInput(StarDetectionList stars,
                     ImageParams params) {

    this.display = display;
    this.stars = stars;
    this.overlay = overlay;
    this.params = params;

    this.az_alt = params.getAzAlt();
    this.time = params.getTime();

    NumberFormat ra_format = SexigesimalFormat.HMSF;
    NumberFormat dec_format = SexigesimalFormat.DMSF;

    Direction dir = params.getDirection();
    double ra  = dir.getLongitude();
    double dec = dir.getLatitude();
    double pa  = params.getPA().getDegrees();

    /*****
    * RA *
    *****/
    ra_field = new JSpinner(new SpinnerNumberModel(new Double(ra),
                                                   new Double(0.0),
                                                   new Double(360.0),
                                                   new Double(1.5/3600.0)));

    ra_field.setEditor(new NumberEditor(ra_field, ra_format));
    ra_field.addChangeListener(this);

    /******
    * Dec *
    ******/
    dec_field = new JSpinner(new SpinnerNumberModel(new Double(dec),
                                                    new Double(-90.0),
                                                    new Double(90.0),
                                                    new Double(1.0/3600.0)));

    dec_field.setEditor(new NumberEditor(dec_field, dec_format));
    dec_field.addChangeListener(this);

    /*****
    * PA *
    *****/
    pa_field = new JSpinner(new SpinnerNumberModel(new Double(pa),
                                                   new Double(-10.0),
                                                   new Double(370.0),
                                                   new Double(0.01)));

    pa_field.setEditor(new JSpinner.NumberEditor(pa_field, "0.0000"));
    pa_field.addChangeListener(this);

    /**********
    * buttons *
    **********/
    hms_button = new JRadioButton("HMS");
    deg_button = new JRadioButton("Deg");

    ButtonGroup group = new ButtonGroup();
    group.add(hms_button);
    group.add(deg_button);

    hms_button.setSelected(true);

    hms_button.addActionListener(this);
    deg_button.addActionListener(this);

} // end of constructor

/******************************************************************************
*
******************************************************************************/
public JComponent getRAComponent() { return ra_field; }

/******************************************************************************
*
******************************************************************************/
public JComponent getDecComponent() { return dec_field; }

/******************************************************************************
*
******************************************************************************/
public JComponent getPAComponent() { return pa_field; }

/******************************************************************************
*
******************************************************************************/
public JRadioButton getSexigesimalButton() { return hms_button; }

/******************************************************************************
*
******************************************************************************/
public JRadioButton getDecimalButton() { return deg_button; }

/******************************************************************************
*
******************************************************************************/
public void setBase(Base base) {

    NumberFormat ra_format;
    NumberFormat dec_format;

    Double  ra_step;
    Double dec_step;

    if(base == Base.SEXIGESIMAL) {
        /**************
        * sexigesimal *
        **************/
         ra_format = new SexigesimalFormat(15.0, 'h', 'm', 's', 4);
        dec_format = new SexigesimalFormat(1.0, 'd', '\'', '"', 3);

         ra_step = new Double(1.5/3600.0);
        dec_step = new Double(1.0/3600.0);

    } else if(base == Base.DECIMAL) {
        /******************
        * decimal degrees *
        ******************/
         ra_format = new DecimalFormat("0.00000");
        dec_format = ra_format;

         ra_step = new Double(0.0001);
        dec_step = new Double(0.0001);

    } else {
        throw new IllegalArgumentException("Unknown base "+base);
    }

    /*****************
    * set the format *
    *****************/
    NumberEditor  ra_editor = (NumberEditor) ra_field.getEditor();
    NumberEditor dec_editor = (NumberEditor)dec_field.getEditor();

     ra_editor.setNumberFormat( ra_format);
    dec_editor.setNumberFormat(dec_format);

    /********************
    * set the step size *
    ********************/
    SpinnerNumberModel  ra_model = (SpinnerNumberModel) ra_field.getModel();
    SpinnerNumberModel dec_model = (SpinnerNumberModel)dec_field.getModel();

     ra_model.setStepSize( ra_step);
    dec_model.setStepSize(dec_step);

} // end of setBase method

/******************************************************************************
*
******************************************************************************/
public double getRA() throws ParseException {

    return ((Number)(ra_field.getValue())).doubleValue();

} // end of getRA method

/******************************************************************************
*
******************************************************************************/
public double getDec() throws ParseException {

    return ((Number)(dec_field.getValue())).doubleValue();

} // end of getDec method

/******************************************************************************
*
******************************************************************************/
public double getPA() throws ParseException {

    return ((Number)(pa_field.getValue())).doubleValue();

} // end of getPA method

/******************************************************************************
*
******************************************************************************/
public void setRADec(Direction dir) {

     ra_field.setValue(new Double(dir.getLongitude()));
    dec_field.setValue(new Double(dir.getLatitude()));

    update();

} // end of setRADec method


/******************************************************************************
*
******************************************************************************/
public void setPointing(Direction dir, double pa) {

     ra_field.setValue(new Double(dir.getLongitude()));
    dec_field.setValue(new Double(dir.getLatitude()));
     pa_field.setValue(new Double(pa));

    update();

} // end of setRADec method


/******************************************************************************
*
******************************************************************************/
public void setPointing(Direction dir, double pa, PreciseDate time) {

     ra_field.setValue(new Double(dir.getLongitude()));
    dec_field.setValue(new Double(dir.getLatitude()));
     pa_field.setValue(new Double(pa));

    this.time = time.copy();

    update();

} // end of setRADec method

/******************************************************************************
*
******************************************************************************/
private void update() {

    try {
        double ra  = getRA();
        double dec = getDec();
        double pa  = getPA();

        params.set(new Direction(ra, dec), new Angle(pa), time, az_alt);
        stars.set(params);

    } catch(Exception ex) {
        ex.printStackTrace();
    }
} // end of update method

/******************************************************************************
*
******************************************************************************/
public void actionPerformed(ActionEvent e) {

    Object source = e.getSource();
    if(source != hms_button && source != deg_button) return;

    if(     source == hms_button && hms_button.isSelected()) setBase(Base.SEXIGESIMAL);
    else if(source == deg_button && deg_button.isSelected()) setBase(Base.DECIMAL);

} // end of actionPerformed method


/******************************************************************************
*
******************************************************************************/
public void stateChanged(ChangeEvent e) {

    update();

} // end of stateChanged method

/******************************************************************************
*
******************************************************************************/
public enum Base { DECIMAL, SEXIGESIMAL};

} // end of PointingInput class