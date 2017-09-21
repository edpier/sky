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

package eap.sky.time.clock;

import eap.sky.time.*;

import java.util.*;
import java.text.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;

/***************************************************************************
* A GUI component which can control an {@link AdjustableClock}.
***************************************************************************/
public class ClockControler extends JPanel implements ChangeListener,
                                                      ActionListener {

AdjustableClock clock;

int direction;

JLabel rate_label;
JSlider slider;
JLabel time_label;

JTextField type_in;

PreciseDateFormat format;

Animation animation;

/***************************************************************************
* Create a new controller which will control the given clock.
***************************************************************************/
public ClockControler(AdjustableClock clock) {

    this.clock = clock;
    direction = 1;
    
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
    /*************
    * rate label *
    *************/
    rate_label = new JLabel("1x");
    add(rate_label);

    
    /*********
    * slider *
    *********/
    slider = new JSlider(0, 5000);
    slider.addChangeListener(this);
    slider.setValue(1);
    add(slider);

    /**********
    * buttons *
    **********/
    JPanel buttons = new JPanel();
    buttons.setLayout(new GridLayout(1, 3));
    add(buttons);

    JButton reverse = new JButton("Reverse");
    reverse.addActionListener(this);
    buttons.add(reverse);

//     JButton stop = new JButton("Stop");
//     stop.addActionListener(this);
//     buttons.add(stop);

    JButton real = new JButton("Real Time");
    real.addActionListener(this);
    buttons.add(real);

    JButton now = new JButton("Now");
    now.addActionListener(this);
    buttons.add(now);

    JButton reset = new JButton("Set");
    reset.addActionListener(this);
    buttons.add(reset);


    /****************
    * type-in field *
    ****************/
    //format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    format = UTCSystem.getInstance().createFormat();

    type_in = new JTextField();
    type_in.setEditable(false);
    type_in.addActionListener(this);
    type_in.setActionCommand("Type In");
    add(type_in);

    /**********************
    * start the animation *
    **********************/
    animation = new Animation();
    animation.start();



} //end of constructor

/***************************************************************************
* Respond to button clicks.
***************************************************************************/
public void actionPerformed(ActionEvent e) {

    String command = e.getActionCommand();
//    if(     command.equals("Stop")     ) slider.setValue(0);
    if(command.equals("Real Time")) {
        /***************************
        * change the rate to unity *
        ***************************/
        direction = 1;
        slider.setValue(1);
        applyRate(); // needed to handle rate = -1x

    } else if(command.equals("Reverse")) {
        /***************************
        * reverse the flow of time *
        ***************************/
        direction = -direction;
        applyRate();

   } else if(command.equals("Now")    ) {
        /**************************
        * start setting the clock *
        **************************/
        clock.now();

    } else if(command.equals("Set")    ) {
        /**************************
        * start setting the clock *
        **************************/
        if(type_in.isEditable()) setFromTypeIn();
        else                     type_in.setEditable(true);

    } else if(command.equals("Type In") ){
        /************************
        * hit return in type in *
        ************************/
        setFromTypeIn();
    }


} // end of ActionPerformed method

/***************************************************************************
* Set the clock value to the one typed in.
***************************************************************************/
private void setFromTypeIn() {

    if(!type_in.isEditable()) return;

    /****************************************
    * get the contents of the type-in field *
    ****************************************/
    String text = type_in.getText().trim();

    /*****************
    * parse the time *
    *****************/
    double time;
    if(text.equals("now") || text.length()==0) {
        /***************
        * current time *
        ***************/
        clock.now();

    } else {
        /************************
        * parse the date string *
        ************************/
        try {
            clock.setTime(format.parsePreciseDate(text));
        } catch(ParseException ex) {System.out.println(ex); return; }
    }


    /*************************************
    * make the type in window uneditable *
    *************************************/
    type_in.setEditable(false);

} // end of setFromTypeIn method

/***************************************************************************
* Respond to slider movements.
***************************************************************************/
public void stateChanged(ChangeEvent e) {

    applyRate();

} // end of stateChanged method

/***************************************************************************
* Adjust the clock rate according to the slider position.
***************************************************************************/
protected void applyRate() {

    int rate = direction * slider.getValue();

    rate_label.setText(rate+"x");
    clock.setRate(rate);

} // end of ApplyRate method

/***************************************************************************
* Stops the animation thread.
***************************************************************************/
protected void finalize() {

    animation.quit();

} // end of finalize method




/***************************************************************************
* Inner class for animating the time display
***************************************************************************/
private class Animation extends Thread {

boolean keep_going = true;
Calendar calendar;


/***************************************************************************
* Create a new thread.
***************************************************************************/
public Animation() {

    calendar = Calendar.getInstance();

} // end of constructor

/***************************************************************************
* Stop the thread.
***************************************************************************/
public void quit() { keep_going = false; }

/***************************************************************************
* This is what the thread does.
***************************************************************************/
public void run() {

    while(keep_going) {

        try { sleep(100); }
        catch(InterruptedException e) {}
        
        String time = format.format(clock.currentTime());
        if(!type_in.isEditable()) type_in.setText(time);

    } // end of infinite loop


} // end of run method

} // end of Animation thread inner class

} // end of ClockControler class
