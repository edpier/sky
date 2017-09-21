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

package eap.sky.time;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;


/**********************************************************************
*
**********************************************************************/
public class PreciseDateTableRenderer implements TableCellRenderer {

TimeSystem system;
JLabel label;

/**********************************************************************
*
**********************************************************************/
public PreciseDateTableRenderer(TimeSystem system) {

    this.system = system;

    label = new JLabel();

} // end of constructor

/**********************************************************************
*
**********************************************************************/
public Component getTableCellRendererComponent(JTable table, Object value,
                                       boolean isSelected, boolean hasFocus,
                                       int row, int column) {

    PreciseDate date = (PreciseDate)value;

    label.setText(system.convertDate(date).toString());
    return label;

} // end of getTableCellRendererComponent method

} // end of PreciseDateTableRenderer class