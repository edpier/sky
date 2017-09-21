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

package eap.sky.chart;

import eap.sky.time.*;
import eap.sky.time.clock.*;
import eap.sky.time.cycles.*;

import java.lang.reflect.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/***********************************************************************
*
***********************************************************************/
public class TimeInput extends JPanel implements ActionListener {

Clock clock;

JTextField field;
PreciseDateFormat format;

PreciseDate time;

Collection<ActionListener> listeners;

/***********************************************************************
*
***********************************************************************/
public TimeInput(Clock clock, TimeSystem system) {

    this.clock = clock;

    field = new JTextField(20);
    field.addActionListener(this);

    format = new PreciseDateFormat(system);

    listeners = new HashSet<ActionListener>();

    /*********
    * layout *
    *********/
    setLayout(new BorderLayout());
    add(field);

} // end of TimeInput class

/***********************************************************************
*
***********************************************************************/
public void clear() { field.setText(""); }

/***********************************************************************
*
***********************************************************************/
public void addActionListener(ActionListener l) {

    listeners.add(l);

} // end of addActionListener method

/***********************************************************************
*
***********************************************************************/
public void removeActionListener(ActionListener l) {

    listeners.remove(l);

} // end of removeActionListener method

/***********************************************************************
*
***********************************************************************/
public void setEditable(boolean editable) {

    field.setEditable(editable);

} // end of setEditable method

/***********************************************************************
*
***********************************************************************/
public void setTime() {

    setTime(clock.currentTime());

} // end of setTime method

/***********************************************************************
* This method is thread safe.
***********************************************************************/
public void setTime(PreciseDate time) {

    Runnable runnable = new Setter(time, true);

    if(SwingUtilities.isEventDispatchThread()) runnable.run();
    else {
        try { SwingUtilities.invokeAndWait(runnable); }
        catch(InterruptedException e) {}
        catch(InvocationTargetException e) {
            throw (RuntimeException)e.getCause();
        }
    }

} // end of setTime method

/***********************************************************************
*
************************************************************************/
public PreciseDate getTime() {

    return time.copy();

} // end of getTime method

/***********************************************************************
*
***********************************************************************/
public void actionPerformed(ActionEvent e) {

    try { parseTime(); }
    catch(ParseException ex) {}

} // end of actionPerformed method

/***********************************************************************
*
***********************************************************************/
public void parseTime() throws ParseException {

    String text = field.getText().trim();

    try {
        if(text.equalsIgnoreCase("now")) {
            /***************
            * current time *
            ***************/
            setTime();
        } else if(text.endsWith("sunset") ||
                  text.endsWith("sunrise") ||
                  text.endsWith("twilight") ||
                  text.endsWith("morning") ||
                  text.endsWith("evening" )   ) {
            /*********************
            * night-related time *
            *********************/
            StringTokenizer tokens = new StringTokenizer(text);
            Night night = new Night(tokens.nextToken());
            String when = tokens.nextToken();
            if(when.equals("sunset")) setTime(night.getSunset());
            else if(when.equals("sunrise")) setTime(night.getSunrise());
            else {

                /*********************************
                * determine the kind of twilight *
                *********************************/
                Twilight twilight;
                if(     when.equals("ir")   ) twilight = Twilight.NEAR_IR;
                else if(when.equals("civil")) twilight = Twilight.CIVIL;
                else                          twilight = Twilight.ASTRONOMICAL;


                /**********************
                * morning or evening? *
                **********************/
                if(tokens.hasMoreTokens()) when = tokens.nextToken();

                if(when.equals("twilight") || when.equals("evening")) {
                    /**********
                    * evening *
                    **********/
                    setTime(night.getEveningTime(twilight));
                } else if(when.equals("morning")) {
                    /**********
                    * morning *
                    **********/
                    setTime(night.getMorningTime(twilight));
                }


           } // end if twilight


        } else {
            setTime((PreciseDate)format.parseObject(field.getText()));
        }
    } catch(ParseException ex) {
        int index = ex.getErrorOffset();
        field.setCaretPosition(index);
        field.moveCaretPosition(field.getText().length()-1);

        field.setBorder(BorderFactory.createLineBorder(Color.red));
        field.setToolTipText(ex.getMessage());

        throw ex;
    }


} // end of actionPerformed method

/***********************************************************************
*
***********************************************************************/
private void fireEvent() {

    ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                        "TimeChanged");

    for(Iterator it = listeners.iterator(); it.hasNext(); ) {
        ActionListener l = (ActionListener)it.next();

        l.actionPerformed(event);

    } // end of loop over listeners

} // end of fireEvent method

/***********************************************************************
*
***********************************************************************/
private class Setter implements Runnable {

PreciseDate new_time;
boolean fire;

/***********************************************************************
*
***********************************************************************/
public Setter(PreciseDate new_time, boolean fire) {

    this.new_time = new_time;
    this.fire = fire;

} // end of constructor

/***********************************************************************
*
***********************************************************************/
public void run() {

    field.setText(format.format(new_time));
    time = new_time.copy();

    field.setBorder(null);
    field.setToolTipText(null);

    if(fire) fireEvent();

} // end of run method

} // end of Setter inner class

} // end of TimeInput class
