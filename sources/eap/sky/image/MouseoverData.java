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

import java.text.*;

/********************************************************************
*
********************************************************************/
public class MouseoverData {
    
    
    
double x;
double y;
double value;

boolean have_point;
boolean have_value;

private static final DecimalFormat pixel_format = new DecimalFormat("0");
private static final DecimalFormat value_format = new DecimalFormat("0.1");

/********************************************************************
*
********************************************************************/
public MouseoverData() {
    
    have_point = false;
    have_value = false;
    
} // end of constructor
   
/********************************************************************
*
********************************************************************/
public MouseoverData(double x, double y) {
    
    this.x = x; 
    this.y = y;
    
    have_point = true;
    have_value = false;
    
} // end of constructor
       
/********************************************************************
*
********************************************************************/
public MouseoverData(double x, double y, double value) {
    
    this.x = x; 
    this.y = y;
    this.value = value;
    
    have_point = true;
    have_value = true;

} // end of constructor
         
/********************************************************************
*
********************************************************************/
public MouseoverData(MouseoverData data) {
    
    this.have_point = data.have_point;
    this.have_value = data.have_value;
    
    this.x = data.x;
    this.y = data.y;
    
    this.value = data.value;
    
} // end of copy constructor


/********************************************************************
*
********************************************************************/
public boolean hasPoint() { return have_point; }
       
/********************************************************************
*
********************************************************************/
public boolean hasValue() { return have_value; }
       
/********************************************************************
*
********************************************************************/
public double getX() { return x; }
       
/********************************************************************
*
********************************************************************/
public double getY() { return y; }
       
/********************************************************************
*
********************************************************************/
public double getValue() { return value; }
       
/********************************************************************
*
********************************************************************/
public String toString() {
    
    if(!have_point) return "";
    
   // System.out.println("pixel_format="+pixel_format);
    if(!have_value) {
        return "("+pixel_format.format(x)+", "+
                   pixel_format.format(y)+")";   
    }
     
    
    return "("+pixel_format.format(x)+", "+
               pixel_format.format(y)+") "+
               value_format.format(value);
    
} // end of toString method

} // end of MouseoverData class