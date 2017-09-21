package eap.sky.image;

import java.text.*;
import javax.swing.*;
import javax.swing.text.*;

/**************************************************************************
*
**************************************************************************/
public class NumberEditor extends JSpinner.DefaultEditor {

/**************************************************************************
*
**************************************************************************/
public NumberEditor(JSpinner spinner, NumberFormat format) {

    super(spinner);

    /****************************************
    * make sure it's a number spinner model *
    ****************************************/
    if (!(spinner.getModel() instanceof SpinnerNumberModel)) {
        throw new IllegalStateException("Model not a SpinnerNumberModel");
    }

    SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();

    NumberFormatter formatter = new NumberEditorFormatter(model, format);
    DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);

    JFormattedTextField ftf = getTextField();
    ftf.setEditable(true);
    ftf.setFormatterFactory(factory);
    ftf.setHorizontalAlignment(JTextField.RIGHT);

    try {
        String maxString = formatter.valueToString(model.getMinimum());
        String minString = formatter.valueToString(model.getMaximum());
        ftf.setColumns(Math.max(maxString.length(), minString.length()));

    } catch (ParseException e) {}

} // end of constructor


/**************************************************************************
*
**************************************************************************/
public void setNumberFormat(NumberFormat format) {

    JSpinner spinner = getSpinner();
    SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();

    NumberFormatter formatter = new NumberEditorFormatter(model, format);
    DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);

    JFormattedTextField ftf = getTextField();
    ftf.setFormatterFactory(factory);

} // end of setNumberFormat method

} // end of NumberEditor class