package com.sdg.etspmobileuser;

import com.google.android.gms.tagmanager.DataLayer;
import com.sdg.util.MethodsList;
import com.sdg.util.Preferences;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Menu extends Activity implements OnClickListener{

	private Button buttonManageUsers;
	private Button buttonManageObjects;
	private Button buttonManageTrackers;
	private Button buttonTrackObjects;
	private Button buttonStatistics;
	private Button buttonSettings;
	private Intent clicked;
	private SharedPreferences sharedPreferences=null;
	private Editor editor=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		sharedPreferences= getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE);
		buttonManageUsers=(Button)findViewById(R.id.buttonManageUsers);
		buttonManageObjects=(Button)findViewById(R.id.buttonManageObjects);
		buttonTrackObjects=(Button)findViewById(R.id.buttonTrackObjects);
		buttonStatistics=(Button)findViewById(R.id.buttonStatistics);
		buttonSettings=(Button)findViewById(R.id.buttonSettings);
		buttonManageTrackers=(Button)findViewById(R.id.buttonManageTrackingDevices);
		buttonManageUsers.setOnClickListener(this);
		buttonManageObjects.setOnClickListener(this);
		buttonTrackObjects.setOnClickListener(this);
		buttonStatistics.setOnClickListener(this);
		buttonSettings.setOnClickListener(this);
		buttonManageTrackers.setOnClickListener(this);
		boolean loggedIn=sharedPreferences.getBoolean(Preferences.loggedin, false);
		boolean passwordOn = sharedPreferences.getBoolean(Preferences.passwordOn, false);
		boolean fisrtTime = sharedPreferences.getBoolean(Preferences.FIRSTTIME, true);
		if(fisrtTime || (passwordOn && !loggedIn))
		{
			editor=sharedPreferences.edit();
			editor.putBoolean(Preferences.loggedin, true);
			editor.putBoolean(Preferences.FIRSTTIME, false);
			editor.commit();
			clicked = new Intent("com.sdg.etspmobileuser.LOGIN");
			startActivity(clicked);
			finish();
		}
		
		if(!sharedPreferences.getBoolean(Preferences.passwordOn, false))
		{
			ActionBar actionBar = getActionBar();
			actionBar.hide();
		}
		else
		{
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}
		
		if(!com.sdg.util.CheckNetwork.isNetworkAvailable(this))
		{
	    	Toast.makeText(getApplicationContext(),
			         "No Internet Connection!",
			         Toast.LENGTH_LONG).show();	
		}
		checkPrivileges();
	}
	
	

	private void checkPrivileges() {
		// TODO Auto-generated method stub
		String privileges=sharedPreferences.getString(Preferences.PRIVILEGES, "");
		String []privilegesList=privileges.split(",");
		
		for(int i=0;i<privilegesList.length;i++){
			if(!privilegesList[i].equalsIgnoreCase("")){
			com.sdg.util.privileges currentPrivilege = com.sdg.util.privileges.values()[Integer.parseInt(privilegesList[i])];
			switch (currentPrivilege) {
			case ADMIN:
				buttonManageObjects.setEnabled(true);
				buttonManageTrackers.setEnabled(true);
				buttonManageUsers.setEnabled(true);
				buttonSettings.setEnabled(true);
				buttonStatistics.setEnabled(true);
				buttonTrackObjects.setEnabled(true);
				break;
			case USERS:
				buttonManageUsers.setEnabled(true);
				break;
			case MAPS:
				buttonTrackObjects.setEnabled(true);
				break;
			case TRACKINGDEVICES:
				buttonManageTrackers.setEnabled(true);
				break;
			case TRACKINGPROFILES:
				buttonManageObjects.setEnabled(true);
				break;
			case STATISTICS:
				buttonStatistics.setEnabled(true);
				break;
			case SETTINGS:
				buttonSettings.setEnabled(true);
				break;
			default:
				break;
			}
			}
		}
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.buttonManageUsers:
			clicked = new Intent("com.sdg.etspmobileuser.MANAGEUSERS");
			startActivity(clicked);
			break;
		case R.id.buttonManageTrackingDevices:
			clicked = new Intent("com.sdg.etspmobileuser.MANAGETRACKERS");
			startActivity(clicked);
			break;
		case R.id.buttonManageObjects:
			clicked = new Intent("com.sdg.etspmobileuser.OBJECTCATEGORIES");
			startActivity(clicked);
			break;
		case R.id.buttonTrackObjects:
			clicked = new Intent("com.sdg.etspmobileuser.TRACKOBJECTS");
			startActivity(clicked);
			break;
		case R.id.buttonStatistics:
			clicked = new Intent("com.sdg.etspmobileuser.STATISTICS");
			startActivity(clicked);
			break;
		case R.id.buttonSettings:
			clicked = new Intent("com.sdg.etspmobileuser.SETTINGS");
			startActivity(clicked);
			break;
		}
		
	}

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		sharedPreferences= getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE);
		editor=sharedPreferences.edit();
		editor.putString(Preferences.COMPANY, "");
		editor.putBoolean(Preferences.loggedin, false);
		editor.commit();
		clicked = new Intent("com.sdg.etspmobileuser.LOGIN");
		startActivity(clicked);
		finish();
		return super.onOptionsItemSelected(item);
    }
	
}
