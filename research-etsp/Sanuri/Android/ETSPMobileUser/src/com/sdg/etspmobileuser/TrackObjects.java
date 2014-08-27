package com.sdg.etspmobileuser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sdg.models.CheckListData;
import com.sdg.models.GeoFence;
import com.sdg.models.Object;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.MapHandler;
import com.sdg.util.MethodsList;
import com.sdg.util.Preferences;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.LinearGradient;
import android.graphics.drawable.ColorDrawable;

public class TrackObjects extends Fragment implements OnClickListener{
	   
	   private GoogleMap googleMap;
	   private ArrayList<Object> objectsList;
	   private String filter="all";
	   private String previousFilter="all";
	   private DataLayer dataLayer;
	   private SharedPreferences sharedPreferences=null;
	   private Editor editor=null;
	   private RelativeLayout mapLayout;
	   private LinearLayout drawLayout;
	   private Button buttonToggleView;
	   public Button buttonAddGeofence;
	   public Button buttonRemoveGeofence;
	   double n=0;
	   private List<GeoFence> geoFences=new ArrayList<GeoFence>();
	   private GetResponse asyncRate;
	   
		public TrackObjects() {
			// TODO Auto-generated constructor stub
			super();
		}
		
	   
	   @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	        View rootView = inflater.inflate(R.layout.track_objects, container, false);
	        dataLayer=new DataLayer();
		      sharedPreferences= getActivity().getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE);
		      mapLayout=(RelativeLayout)rootView.findViewById(R.id.mapLayout);
		      //mapLayout.setOnTouchListener(this);
		      drawLayout=(LinearLayout)rootView.findViewById(R.id.drawLayout);
		      //drawLayout.setOnTouchListener(this);
		      buttonToggleView= (Button)rootView.findViewById(R.id.buttonToggleView);
		      buttonToggleView.setOnClickListener(this);
		      buttonAddGeofence= (Button)rootView.findViewById(R.id.buttonAddGeoFence);
		      buttonAddGeofence.setOnClickListener(this);
		     buttonRemoveGeofence = (Button)rootView.findViewById(R.id.buttonRemoveGeoFence);
		     buttonRemoveGeofence.setOnClickListener(this);
		      
		      editor=sharedPreferences.edit();
		      editor.putBoolean(Preferences.OBJECT_SELECTED, false);
		      editor.commit();
		      
		      
		      final String company=sharedPreferences.getString(Preferences.COMPANY, "");
		      
		      try { 
		            if (googleMap == null) {
		            	SupportMapFragment f=(SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
		            	googleMap = f.getMap();
		            }
		         googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		         googleMap.setMyLocationEnabled(true);
		         
		         UiSettings mapSettings;
		         mapSettings = googleMap.getUiSettings();
		         mapSettings.setZoomControlsEnabled(true);
		         //mapSettings.setAllGesturesEnabled(true);
		         mapSettings.setMyLocationButtonEnabled(true);
		         
		         getObjects(company);
				
		         googleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
					
					@Override
					public void onMapLoaded() {
						// TODO Auto-generated method stub					
						
				        ScheduledExecutorService scheduler =
				        	    Executors.newSingleThreadScheduledExecutor();

				        	scheduler.scheduleAtFixedRate
				        	      (new Runnable() {
				        	         public void run() {
				        	        	 
				        	             filter=sharedPreferences.getString(Preferences.FILTER, "all");
				        	             if(!filter.equalsIgnoreCase(previousFilter))
				        	             {
				        	             	dataLayer=new DataLayer();
				        	             	getObjects(company);
				        	             }
				        	             previousFilter=filter;
				     			 		MapHandler asyncMap  = new MapHandler(getActivity().getBaseContext(),getActivity(), objectsList, filter, company, googleMap,n);
				    			 		asyncMap.execute(); 
				    			 		n+=0.01;
				        	         }
				        	      }, 0, 1000, TimeUnit.MILLISECONDS);

					}
				});

		         googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {
					
					@Override
					public void onMapLongClick(LatLng point) {
						// TODO Auto-generated method stub
						//startingPoint=point;
						if(sharedPreferences.getBoolean(Preferences.DRAWGEOFENCE, false)){
							editor=sharedPreferences.edit();
							editor.putBoolean(Preferences.STARTDRAWING, true);
							editor.commit();
							DrawGeofenceCanvas drawGeoFence=new DrawGeofenceCanvas(getActivity(),googleMap);
							drawGeoFence.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
							drawGeoFence.requestWindowFeature(Window.FEATURE_NO_TITLE);
							drawGeoFence.setCanceledOnTouchOutside(false);//make dialog not dissapear when screen touched
							drawGeoFence.show();
							//drawLayout.bringToFront();
					        Toast.makeText(getActivity(), 
					        		 "Start Drawing!", 
					                 Toast.LENGTH_LONG).show();
						}
					}
				});
		         
		      } catch (Exception e) {
		         e.printStackTrace();
		      }

	        return rootView;
	}


	private void getObjects(String company) {
		// TODO Auto-generated method stub
        //get object information to display them on map
		try {
		asyncRate = new GetResponse(filter,company,MethodsList.getObjectsWithDetails.ordinal());
       String response = asyncRate.execute().get(); 
			JSONObject obj = new JSONObject(response);
			JSONArray jsonArray = obj.getJSONArray("rows");		
		    for(int i=0;i<jsonArray.length(); i++){
		        JSONObject jsonas = jsonArray.getJSONObject(i);
		        Object data=new Object();
		        data.setObjectID(jsonas.getString("id"));
		        data.setObjectName(jsonas.getString("name"));
		        data.setProfile(filter);
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
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.buttonToggleView:
			toggleView();
			break;
		case R.id.buttonAddGeoFence:
			AddGeoFence addGeoFence=new AddGeoFence(getActivity(),getActivity());
			addGeoFence.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			addGeoFence.show();
			break;
		case R.id.buttonRemoveGeoFence:
			editor=sharedPreferences.edit();
			editor.putBoolean(Preferences.SHOWREMOVEBUTTONS, true);
			editor.commit();
			buttonAddGeofence.setEnabled(false);
			break;
		default:
			break;	
		}
	}
	
	public void toggleView(){
		googleMap.setMapType( googleMap.getMapType() ==
	             GoogleMap.MAP_TYPE_NORMAL ?
	             GoogleMap.MAP_TYPE_SATELLITE :
	             GoogleMap.MAP_TYPE_NORMAL);
	}

	   
}



