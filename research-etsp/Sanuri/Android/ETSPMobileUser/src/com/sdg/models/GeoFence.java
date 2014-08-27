package com.sdg.models;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class GeoFence {

	private String ID;
	private ArrayList<LatLng> geoFence=new ArrayList<LatLng>();
	private boolean inside;
	private ArrayList<String> objects=new ArrayList<String>();

	public ArrayList<LatLng> getGeoFence() {
		return geoFence;
	}

	public void setGeoFence(ArrayList<LatLng> geoFence) {
		this.geoFence = geoFence;
	}

	public boolean isInside() {
		return inside;
	}

	public void setInside(boolean inside) {
		this.inside = inside;
	}

	public ArrayList<String> getObjects() {
		return objects;
	}

	public void setObjects(ArrayList<String> objects) {
		this.objects = objects;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}
}
