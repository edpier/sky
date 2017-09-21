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

import eap.sky.util.*;
import eap.sky.util.coordinates.*;

import java.awt.*;

/************************************************************************
*
************************************************************************/
public class SkyLine extends CompositeItem {

/************************************************************************
*
************************************************************************/
public SkyLine(AzAlt az_alt) {

    /**********
    * horizon *
    **********/
    CurveItem line = new CurveItem(new LatitudeLine(0, 0, 360), az_alt);
    line.setStroke(new BasicStroke(5));
    add(line);

    /**********
    * N/S/E/W *
    **********/
    addLabel("N",   0, az_alt);
    addLabel("E",  90, az_alt);
    addLabel("S", 180, az_alt);
    addLabel("W", 270, az_alt);


} // end of constructor


/************************************************************************
*
************************************************************************/
private void addLabel(String text, double az, AzAlt az_alt) {

    Font font = new Font("sans-serif", Font.PLAIN, 12);

//     Font font2 = font.deriveFont(Font.BOLD);
// 
//     LabelItem background = new SkyLabel(new Direction(az, 0), text, az_alt);
//     background.setFont(font2);
//     background.setColor(Color.white);
//     add(background);

    LabelItem label = new SkyLabel(new Direction(az, 0), text, az_alt);
  //  label.setFont(font);
    label.setBackground(Color.white);
    label.setAnchor(0.5, 0.5);

    add(label);

} // end of addlabel method


} // end of CompositeItem class