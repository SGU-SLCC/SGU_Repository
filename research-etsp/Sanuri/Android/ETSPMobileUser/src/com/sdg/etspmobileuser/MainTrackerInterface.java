
package com.sdg.etspmobileuser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sdg.models.NavDrawerItem;
import com.sdg.models.Object;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.Messages;
import com.sdg.util.MethodsList;
import com.sdg.util.NavDrawerListAdapter;
import com.sdg.util.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainTrackerInterface  extends FragmentActivity{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DataLayer dataLayer;
	private SharedPreferences sharedPreferences=null;
	private Editor editor=null;
	private String filter="all";
	private LinearLayout linearLayoutMap;
    private CharSequence actionBarTitle;
    private ArrayList<Object> objectsList;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    public Fragment currentstatus;
    private GetResponse asyncRate;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tracker_interface);
        dataLayer = new DataLayer();
        
        //set map
        linearLayoutMap=(LinearLayout)findViewById(R.id.frame_container);
        linearLayoutMap.setVisibility(View.VISIBLE);
		android.support.v4.app.FragmentManager frg=getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction trans=frg.beginTransaction();
		currentstatus=new TrackObjects();
		trans.replace(R.id.frame_container, currentstatus);
		trans.commit();
		
		//set slider menu
		actionBarTitle = "Objects List";
	    sharedPreferences= getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE);
	    String company=sharedPreferences.getString(Preferences.COMPANY, "");
        //get objects to add to menu
	    
	    
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
		
		
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
 
        navDrawerItems = new ArrayList<NavDrawerItem>();
 
        for (Object object : objectsList) 
        {
        	int icon;
            if(object.getProfile().equalsIgnoreCase("animal"))
            {
            	icon=R.drawable.animal;
            }
            else if(object.getProfile().equalsIgnoreCase("vehicle"))
            {
            	icon=R.drawable.vehicle;            
            }
            else if(object.getProfile().equalsIgnoreCase("person"))
            {
            	icon=R.drawable.person;
            }
            else
            {
            	icon=R.drawable.custom;
            }
        	
        	navDrawerItems.add(new NavDrawerItem(object.getObjectID()+":"+object.getObjectName(), icon));
		}

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
 
        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),navDrawerItems);
        mDrawerList.setAdapter(adapter);
 
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setTitle(actionBarTitle);
 
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.objectslist, //nav menu toggle icon
                R.string.empty, // nav drawer open - description for accessibility
                R.string.empty // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(actionBarTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(actionBarTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
 
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            //displayView(0);
        }
    }
    
	//Slide menu item click listener
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            // display view for selected nav drawer item
            displayView(position);
            RelativeLayout itemLayout=(RelativeLayout)view;
            String itemName= ((TextView)itemLayout.findViewById(R.id.textViewMenuItem)).getText().toString();
            String[] words= itemName.split(":");
            String objectID=words[0];
            editor=sharedPreferences.edit();
            editor.putBoolean(Preferences.OBJECT_SELECTED, true);
            editor.putString(Preferences.OBJECTID, objectID);
            editor.commit();
        }
    }
   
 
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
        case R.id.itemVehicles:
        	editor=sharedPreferences.edit();
        	editor.putString(Preferences.FILTER, "vehicle");
        	editor.commit();
            return true;
        case R.id.itemPeople:
        	editor=sharedPreferences.edit();
        	editor.putString(Preferences.FILTER, "person");
        	editor.commit();
        	return true;
        case R.id.itemAnimals:
        	editor=sharedPreferences.edit();
        	editor.putString(Preferences.FILTER, "animal");
        	editor.commit();
        	return true;
        case R.id.itemCustom:
        	editor=sharedPreferences.edit();
        	editor.putString(Preferences.FILTER, "custom");
        	editor.commit();
        	return true;
        case R.id.itemAll:
        	editor=sharedPreferences.edit();
        	editor.putString(Preferences.FILTER, "all");
        	editor.commit();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
 
   
    //Called when invalidateOptionsMenu() is triggered
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
 
    //Zoom in on the selected object
    private void displayView(int position) {

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);
    }
 
    @Override
    public void setTitle(CharSequence title) {
    	actionBarTitle = title;
        getActionBar().setTitle(actionBarTitle);
    }
 
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(sharedPreferences.getBoolean(Preferences.OBJECT_SELECTED, false))
		{
	        editor=sharedPreferences.edit();
	        editor.putBoolean(Preferences.OBJECT_SELECTED, false);
	        editor.putBoolean(Preferences.FIRSTGO, true);
	        editor.commit();
		}
		else if(sharedPreferences.getBoolean(Preferences.DRAWGEOFENCE, false)){
	        editor=sharedPreferences.edit();
	        editor.putBoolean(Preferences.DRAWGEOFENCE, false);
	        editor.commit();
			this.getActionBar().show();
			((TrackObjects)currentstatus).buttonAddGeofence.setEnabled(true);
		}
		else if(sharedPreferences.getBoolean(Preferences.SHOWREMOVEBUTTONS, false))
		{
			editor=sharedPreferences.edit();
			editor.putBoolean(Preferences.SHOWREMOVEBUTTONS, false);
			editor.commit();
			((TrackObjects)currentstatus).buttonAddGeofence.setEnabled(true);
		}
		else
		{
			super.onBackPressed();
		}
	}
    

//---------------------When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...


	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

 
}