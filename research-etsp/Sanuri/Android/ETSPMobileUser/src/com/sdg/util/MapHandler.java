package com.sdg.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sdg.etspmobileuser.MainTrackerInterface;
import com.sdg.etspmobileuser.R;
import com.sdg.models.GeoFence;
import com.sdg.models.Object;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

public class MapHandler extends AsyncTask<Void, String, String>{

	private Context context;
    private Activity rootAct;
    private ArrayList<Object> objectsList=new ArrayList<Object>();
    private String method;
    private String filter;
    private String company;
    private GoogleMap googleMap;
    private SharedPreferences sharedPreferences=null;
    private Editor editor=null;
    private Marker TP=null;
    private List<GeoFence> geofences=new ArrayList<GeoFence>();
    DataLayer dataLayer = new DataLayer();
    private ImageView imageViewRemove;
    private int fenceCount;
    private Intent serviceIntent;
    double n;
	
    public MapHandler(Context context,Activity activity,ArrayList<Object> objectsList,String filter,String company,GoogleMap googleMap,double n) {
        // TODO Auto-generated constructor stub
    	this.context=context;
        rootAct = activity;
        this.objectsList=objectsList;
        this.filter=filter;
        this.company=company;
        this.googleMap=googleMap;
        sharedPreferences= activity.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE);
        this.n=n;
    }
	
	@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		    String response = dataLayer.getLocationData(filter, company);
		    publishProgress(response);
		    return response;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		googleMap.clear();
		LatLng point;			
		String [] numbers=values[0].split(":");
		for (String objectData : numbers) {
			String []dataList=objectData.split(" ");
			for (Object object : objectsList) {
				if(object.getObjectID()==dataList[0])
				{
					
					//mark the object
					point=new LatLng(Double.parseDouble(dataList[1]), Double.parseDouble(dataList[2]));
					mark(object,point,googleMap);
					
					//if user has selected this object, move camera with the object
					Boolean objectSelected=sharedPreferences.getBoolean(Preferences.OBJECT_SELECTED, false);
					if(objectSelected)
					{
						String objectID=sharedPreferences.getString(Preferences.OBJECTID, "");
						if(objectID==object.getObjectID())
						{
							moveToCurrentLocation(point);
							zoom(point);
						}
					}
					else
					{
						if(sharedPreferences.getBoolean(Preferences.FIRSTGO, false))
						{
							moveToCenter();
					        editor=sharedPreferences.edit();
					        editor.putBoolean(Preferences.FIRSTGO, false);
					        editor.commit();
						}
					}
				}
			}
		}
		
		
		for (int i = 0; i < fenceCount; i++) {
			
		}
		
		//draw geo fences once objects are all displayed
		getGeofences();
		int count=0;
		fenceCount=0;
		LatLng previous;
		for (GeoFence fence : geofences) {
			previous=fence.getGeoFence().get(0);
			for (LatLng geoPoint : fence.getGeoFence()) {
				if(count==1)
				{
					 Polyline line = googleMap.addPolyline(new PolylineOptions()
				     .add(previous, geoPoint)
				     .width(2)
				     .color(Color.BLUE));
				}
				count=1;
				previous=geoPoint;
			}
			
			//slows process a lot
			/*
			if(sharedPreferences.getBoolean(Preferences.SHOWREMOVEBUTTONS, false))//remove geofence mode.So, show "remove" buttons
			{
				Point currentPoint = googleMap.getProjection().toScreenLocation(fence.getGeoFence().get(0)); // attach remove button to starting point of the geofence for ease
				imageViewRemove=new ImageView(context);
				imageViewRemove.setId(fenceCount);
				imageViewRemove.setLayoutParams(new LayoutParams(40, 40));
				imageViewRemove.setBackgroundResource(R.drawable.remove);
				imageViewRemove.setX(currentPoint.x);
				imageViewRemove.setY(currentPoint.y);
				imageViewRemove.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						final View view=v;
						new AlertDialog.Builder(context)
						.setTitle("ETSP")
						.setMessage("Delete Geo-fence Permenantly?")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

						    public void onClick(DialogInterface dialog, int whichButton) {
						    		geofences.remove(view.getId());
						    }})
							 .setNegativeButton(android.R.string.no, null).show();
					}
				});
			}*/
			fenceCount++;
		}
		
		if(geofences.size()==1 && !sharedPreferences.getBoolean(Preferences.ALERTSSTARTED, false) ){
			startAlertService();
			editor=sharedPreferences.edit();
			editor.putBoolean(Preferences.ALERTSSTARTED, true);
			editor.commit();
		}
		else if(geofences.size()==0 && sharedPreferences.getBoolean(Preferences.ALERTSSTARTED, false)){
			stopAlertService();
			editor=sharedPreferences.edit();
			editor.putBoolean(Preferences.ALERTSSTARTED, false);
			editor.commit();
		}
	}
	
	private void startAlertService() {
		// TODO Auto-generated method stub
		serviceIntent = new Intent();
		serviceIntent.setComponent(new ComponentName("com.sdg.etspmobileuser","com.sdg.etspmobileuser.AlertService"));	
		Calendar cal = Calendar.getInstance();
		PendingIntent pintent = PendingIntent.getService(context, 1111, serviceIntent, 0);
		AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10*1000, pintent); // Every 10 seconds
		
    	Toast.makeText(context,
		         "Monitoring objects started!",
		         Toast.LENGTH_LONG).show();	
	}
	
	
	 private void stopAlertService() {
		// TODO Auto-generated method stub
		AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		serviceIntent = new Intent();
	    serviceIntent.setComponent(new ComponentName("com.sdg.etspmobileuser","com.sdg.etspmobileuser.AlertService"));	    
	    PendingIntent pending = PendingIntent.getService(context, 1111, serviceIntent,PendingIntent.FLAG_CANCEL_CURRENT);
	    service.cancel(pending);
   	Toast.makeText(context,
		         "Monitoring objects stopped!",
		         Toast.LENGTH_LONG).show();	
	}

	private void getGeofences() {
		// TODO Auto-generated method stub
		geofences.clear();
		StoreGeoFence store=new StoreGeoFence(context);
		Cursor cursor = store.GetData();
		GeoFence g;
		if (cursor.moveToFirst()){
			   do{
				   g=new GeoFence();
				   g.setID(cursor.getString(cursor.getColumnIndex(StoreGeoFence.FeedEntry.COLUMN_NAME_FENCE_ID)));
				   g.setInside(cursor.getInt(cursor.getColumnIndex(StoreGeoFence.FeedEntry.COLUMN_NAME_ISINSIDE))!=0);
				   ArrayList<LatLng> coordinates=new ArrayList<LatLng>();
				   String path=cursor.getString(cursor.getColumnIndex(StoreGeoFence.FeedEntry.COLUMN_NAME_PATH));
				   String []points=path.split(":");
				   for (String singlePoint: points) {
					   if(!singlePoint.equals("")){
						   String []words=singlePoint.split(" ");
						   coordinates.add(new LatLng(Double.parseDouble(words[0]), Double.parseDouble(words[1])));
					   }					
				   }
				   g.setGeoFence(coordinates);
				   String objectsList=cursor.getString(cursor.getColumnIndex(StoreGeoFence.FeedEntry.COLUMN_NAME_OBJECTS));
				   ArrayList<String> objectsSet=new ArrayList<String>();
				   String []objects=objectsList.split(":");
				   for (String object : objects) {
					if(!object.equals("")){
						objectsSet.add(object);
					}
				   }
				   g.setObjects(objectsSet);
				   geofences.add(g);
			   }while(cursor.moveToNext());
			}
			cursor.close();
	}

	private void zoom(LatLng point) {
		// TODO Auto-generated method stub
		CameraPosition INIT = new CameraPosition.Builder()
		.target(new LatLng(point.longitude, point.latitude))
		.zoom( 10F )
		.bearing( 300F) // orientation
		.tilt( 50F) // viewing angle
		.build();
		googleMap.animateCamera( CameraUpdateFactory.newCameraPosition(INIT) );
	}

	private void moveToCenter() {
		// TODO Auto-generated method stub
		//LatLng point=new LatLng(0,0);
		//googleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
		CameraPosition INIT = new CameraPosition.Builder()
		.target(new LatLng(0, 0))
		.zoom( 0 )
		.bearing( 0) // orientation
		.tilt( 0) // viewing angle
		.build();
		googleMap.animateCamera( CameraUpdateFactory.newCameraPosition(INIT) );
	}

	private void mark(Object object,LatLng point,GoogleMap googleMap) {
		// TODO Auto-generated method stub
        if(object.getProfile().equalsIgnoreCase("animal"))
        {
        	TP = googleMap.addMarker(new MarkerOptions().position(point).title(object.getObjectName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.animal)));
        }
        else if(object.getProfile().equalsIgnoreCase("vehicle"))
        {
        	TP = googleMap.addMarker(new MarkerOptions().position(point).title(object.getObjectName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.vehicle)));
        }
        else if(object.getProfile().equalsIgnoreCase("person"))
        {
        	TP = googleMap.addMarker(new MarkerOptions().position(point).title(object.getObjectName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
        }
        else
        {
        	TP = googleMap.addMarker(new MarkerOptions().position(point).title(object.getObjectName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom)));
        }
	}

	
    private void moveToCurrentLocation(LatLng currentLocation)
    {   
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        if(TP!=null)
        {
        	TP.showInfoWindow();
        }
    }
	
}
