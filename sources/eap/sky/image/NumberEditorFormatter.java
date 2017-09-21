package eap.sky.image;

import java.text.*;
import javax.swing.*;
import javax.swing.text.*;

/**************************************************************************
*
**************************************************************************/
public class NumberEditorFormatter extends NumberFormatter{

private final SpinnerNumberModel model;

/**************************************************************************
*
**************************************************************************/
public NumberEditorFormatter(SpinnerNumberModel model, NumberFormat format) {

    super(format);
    this.model = model;
    setValueClass(model.getValue().getClass());

} // end of constructor

/**************************************************************************
*
**************************************************************************/
public void setMinimum(Comparable min) { model.setMinimum(min); }

/**************************************************************************
*
**************************************************************************/
public Comparable getMinimum() { return  model.getMinimum(); }

/**************************************************************************
*
**************************************************************************/
public void setMaximum(Comparable max) {model.setMaximum(max); }

/**************************************************************************
*
**************************************************************************/
public Comparable getMaximum() { return model.getMaximum(); }

} // end of NumberEditorFormatter class