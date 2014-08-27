package com.sdg.etspandroidtracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GetLocation {

	// Acquire a reference to the system Location Manager
	LocationManager locationManager;
	String latestLocation=null;
	
	public GetLocation(){
		super();
	}
	
	public GetLocation(Context context){
		super();
		locationManager= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
				LocationListener locationListener = new LocationListener() {
				    public void onLocationChanged(Location location) {
				      // Called when a new location is found by the network location provider.
				    	latestLocation=location.getAltitude()+" "+location.getLatitude()+" "+location.getLongitude()+" "+location.getBearing()+" "+location.getSpeed()+" ";
				    }

				    public void onStatusChanged(String provider, int status, Bundle extras) {}

				    public void onProviderEnabled(String provider) {}

				    public void onProviderDisabled(String provider) {}
				  };
				// Register the listener with the Location Manager to receive location updates
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
	

	  public String getLocation(){
		  return latestLocation;
	  }
}
