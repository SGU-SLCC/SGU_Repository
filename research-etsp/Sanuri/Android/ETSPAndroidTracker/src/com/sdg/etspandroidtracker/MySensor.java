package com.sdg.etspandroidtracker;

public class MySensor {
	 String name = null;
	 boolean selected = false;
	 
	 //Constructor
	 public MySensor(String name, boolean selected) {
	  super();
	  this.name = name;
	  this.selected = selected;
	 }
	 
	 //Get sensor name
	 public String getName() {
	  return name;
	 }
	 
	 //set sensor name
	 public void setName(String name) {
	  this.name = name;
	 }
	 
	 //get selected status
	 public boolean isSelected() {
	  return selected;
	 }
	 
	 //set selected status
	 public void setSelected(boolean selected) {
	  this.selected = selected;
	 }
}
