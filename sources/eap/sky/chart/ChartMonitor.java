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

import javax.swing.*;
import java.awt.*;

/************************************************************************
*
************************************************************************/
public class ChartMonitor extends JPanel {

private static final Color[] blinker_colors = {Color.white, Color.black};

JLabel sleep_label;
JLabel paint_label;
JLabel  median_update_label;
JLabel current_update_label;
JPanel blinker;
int blinker_state;

/************************************************************************
*
************************************************************************/
public ChartMonitor() {

    setLayout(new GridLayout(5,1));

    /********
    * paint *
    ********/
    paint_label = new JLabel("0000000");

    JPanel panel = new JPanel();
    panel.add(new JLabel("Paint Time"));
    panel.add(paint_label);
    add(panel);

    /********
    * update *
    ********/
    median_update_label = new JLabel("0000000");

    panel = new JPanel();
    panel.add(new JLabel("Update Median"));
    panel.add(median_update_label);
    add(panel);

    /********
    * update *
    ********/
    current_update_label = new JLabel("0000000");

    panel = new JPanel();
    panel.add(new JLabel("Update Time"));
    panel.add(current_update_label);
    add(panel);

    /********
    * sleep *
    ********/
    sleep_label = new JLabel("0000000");

    panel = new JPanel();
    panel.add(new JLabel("Sleep Time"));
    panel.add(sleep_label);
    add(panel);

    /************
    * blinker *
    **********/
    blinker = new JPanel();
    blinker.setOpaque(true);
    blinker_state = 0;

    add(blinker);



} // end of constructor

/************************************************************************
*
************************************************************************/
public void update(long sleep_time, long paint_time,
                   long median_update_time, long current_update_time) {

    sleep_label.setText(sleep_time+"");
    paint_label.setText(paint_time+"");
    median_update_label.setText(median_update_time+"");
    current_update_label.setText(current_update_time+"");

    blinker_state = 1-blinker_state;
    blinker.setBackground(blinker_colors[blinker_state]);


    repaint();

} // end of update monitor

/************************************************************************
*
************************************************************************/
public static ChartMonitor popUp() {

    ChartMonitor monitor = new ChartMonitor();

    JFrame frame = new JFrame();
    frame.getContentPane().add(monitor);
    frame.pack();
    frame.setVisible(true);

    return monitor;

} // end of popUp method

} // end of ChartMonitor class
