package com.sdg.models;

import java.util.ArrayList;

public class Tracker {
	public String getImeiNumber() {
		return imeiNumber;
	}
	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}
	public String getTrackerType() {
		return trackerType;
	}
	public void setTrackerType(String trackerType) {
		this.trackerType = trackerType;
	}
	public Integer getMode() {
		return mode;
	}
	public void setMode(Integer mode) {
		this.mode = mode;
	}
	public ArrayList<String> getData() {
		return data;
	}
	public void setData(ArrayList<String> data) {
		this.data = data;
	}
	public String getTrackerContactNumber() {
		return trackerContactNumber;
	}
	public void setTrackerContactNumber(String trackerContactNumber) {
		this.trackerContactNumber = trackerContactNumber;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String id;
	private String imeiNumber;
	private String trackerType;
	private Integer mode;
	private String trackerContactNumber;
	private ArrayList<String> data;
}
