package com.sdg.models;

public class Alert {
	private String alertID;
	private String objectName;
	private String location;
	private String time;
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getAlertID() {
		return alertID;
	}
	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}
}
