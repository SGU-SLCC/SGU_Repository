package com.sdg.etspmobileuser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.sdg.models.GeoFence;
import com.sdg.models.Object;
import com.sdg.util.DataBaseAccess;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.MethodsList;
import com.sdg.util.PolygonTest;
import com.sdg.util.Preferences;
import com.sdg.util.StoreGeoFence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class AlertService extends IntentService{

    private PolygonTest check=new PolygonTest();
    private List<GeoFence> geofences=new ArrayList<GeoFence>();
    DataLayer dataLayer = new DataLayer();
    private ArrayList<Object> objectsList=new ArrayList<Object>();
	private SharedPreferences sharedPreferences=null;
	private Editor editor=null;
	private LatLng point;
	private StringBuilder alerts;
	DataBaseAccess dbAccess;
	private GetResponse asyncRate;
	   
	public AlertService() {
		super("MyAlertService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub\
		sharedPreferences= getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE);
		dbAccess=new DataBaseAccess(getApplicationContext());
		String company=sharedPreferences.getString(Preferences.COMPANY, "");

		try {
		asyncRate = new GetResponse("all",company,MethodsList.getObjectsWithDetails.ordinal());
       String response = asyncRate.execute().get(); 
			JSONObject obj = new JSONObject(response);
			JSONArray jsonArray = obj.getJSONArray("rows");		
		    for(int i=0;i<jsonArray.length(); i++){
		        JSONObject jsonas = jsonArray.getJSONObject(i);
		        Object data=new Object();
		        data.setObjectID(jsonas.getString("id"));
		        data.setObjectName(jsonas.getString("name"));
		        data.setProfile("all");
		        data.setDescription(jsonas.getString("description"));
		        objectsList.add(data);
		    }
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Object object : objectsList) {
			getGeofences(object.getObjectID());//prepare list of geofences for this object
			point=dataLayer.getCurrentPosition(object.getObjectID(),company);
			for (GeoFence path : geofences) {
				if(path.isInside()){
					if(check.PointIsInRegion(point.latitude, point.longitude,path.getGeoFence() ))
					{
						  Geocoder geocoder = new Geocoder(this, Locale.getDefault());
						  List<Address> addresses;
						try {
							addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
						
						  StringBuilder sb = new StringBuilder();
						  if (addresses.size() > 0) {
						    Address address = addresses.get(0);
						    sb.append(address.getLocality()).append("\n");
						    sb.append(address.getCountryName());
						  }

						warn(object.getObjectName()+" is in prohibited region!");
						dbAccess.InsertValues(object.getObjectName()+" is in prohibited region!", sb.toString(), getDateTime());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else
				{
					if(!check.PointIsInRegion(point.latitude, point.longitude,path.getGeoFence() ))
					{
						  Geocoder geocoder = new Geocoder(this, Locale.getDefault());
						  List<Address> addresses;
						try {
							addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
					
						  StringBuilder sb = new StringBuilder();
						  if (addresses.size() > 0) {
						    Address address = addresses.get(0);
						    sb.append(address.getLocality()).append("\n");
						    sb.append(address.getCountryName());
						  }
						warn(object.getObjectName()+" is outside the safe region!");
						dbAccess.InsertValues(object.getObjectName()+" is in prohibited region!", sb.toString(), getDateTime());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	
	private String getDateTime() {
		// TODO Auto-generated method stub
		alerts=new StringBuilder();
		Calendar calendar = Calendar.getInstance(); 
		alerts.append(calendar.get(Calendar.DATE));
		alerts.append(" ");
		alerts.append(calendar.getTime());
		alerts.append(":");
		return alerts.toString();
	}

	private void warn(String message) {
		 
		// TODO Auto-generated method stub
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.alert)
		        .setContentTitle("ETSP Error")
		        .setContentText(message);
		Intent resultIntent = new Intent(this, Alerts.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainTrackerInterface.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
	}
	
	private void getGeofences(String objectID) {
		// TODO Auto-generated method stub
		geofences.clear();
		StoreGeoFence store=new StoreGeoFence(getBaseContext());
		Cursor cursor = store.GetGeofencesOfObject(objectID);
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

}
