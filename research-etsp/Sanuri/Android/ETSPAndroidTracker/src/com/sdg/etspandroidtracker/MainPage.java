package com.sdg.etspandroidtracker;



import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sdg.util.Preferences;

import android.os.Bundle;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;

public class MainPage extends FragmentActivity {

	Button setup;
	Switch details;
	LinearLayout summary;
    Intent serviceIntent=null;
    
	private SharedPreferences reader=null;
	private Editor editor=null;
	
	private String mode,sensors,interval;
	private ResultReceiver resultReceiver=new ResultReceiver();
	private ResultReceiver bootReceiver=new ResultReceiver();
	final Context context=this;	
	
	

	
	private void GetDetails() {
		// TODO Auto-generated method stub
		sensors = reader.getString(Preferences.Sensors, "");
		interval = reader.getString(Preferences.Interval, "5");
		mode = reader.getString(Preferences.Mode, "1");
	}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        details=(Switch)findViewById(R.id.switchTracking);
        reader= this.getSharedPreferences(Preferences.MyPREFERENCES, Context.MODE_PRIVATE); 
        editor = reader.edit();
        
        final Context context=this;
        details.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				if(isChecked)
				{
					startService();
		            editor.putBoolean(Preferences.serviceOn, true); 
		            editor.commit();
		        	Toast.makeText(getApplicationContext(),
	        		         "Tracking started.",
	        		         Toast.LENGTH_LONG).show();
				}
				else		
				{
					summary.setVisibility(View.GONE);	
					//Kill service
					stopService();
		            editor.putBoolean(Preferences.serviceOn, false); 
		            editor.commit();
		        	Toast.makeText(getApplicationContext(),
		        		         "Tracking Stopped.",
		        		         Toast.LENGTH_LONG).show();
				}
			}
		});
        summary=(LinearLayout)findViewById(R.id.summarylayout);
        setup=(Button)findViewById(R.id.buttonSetup);
        setup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent OpenSetup=new Intent("com.sdg.etspandroidtracker.SETUP");
				startActivity(OpenSetup);		
				finish();
			}
		});
        
        boolean serviceOnBoolean = reader.getBoolean(Preferences.serviceOn, false);
        if(serviceOnBoolean){
        	details.setChecked(true);
			GetDetails();
			//startService();
        }
       
    }
       
    public static PendingIntent getSyncPendingIntent(Context context)
    {
		Intent i = new Intent();
	    i.setComponent(new ComponentName("com.sdg.etspandroidtracker","com.sdg.etspandroidtracker.TrackerService"));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        return pi;  
    }
    
    
    protected void stopService() {
		// TODO Auto-generated method stub
    	
    		AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    		serviceIntent = new Intent();
    	    serviceIntent.setComponent(new ComponentName("com.sdg.etspandroidtracker","com.sdg.etspandroidtracker.TrackerService"));	    
    	    PendingIntent pending = PendingIntent.getService(context, 1111, serviceIntent,PendingIntent.FLAG_CANCEL_CURRENT);
    	    service.cancel(pending);
	}


	private void startService(){
		if(CheckNetwork.isNetworkAvailable(this))
		{
			summary.setVisibility(View.VISIBLE);
			GetDetails();
			serviceIntent = new Intent();
			serviceIntent.setComponent(new ComponentName("com.sdg.etspandroidtracker","com.sdg.etspandroidtracker.TrackerService"));
			serviceIntent.putExtra(TrackerService.MODE, mode);
			serviceIntent.putExtra(TrackerService.INTERVAL, interval);
			serviceIntent.putExtra(TrackerService.SENSORS, sensors);
			
			Calendar cal = Calendar.getInstance();
			PendingIntent pintent = PendingIntent.getService(MainPage.this, 1111, serviceIntent, 0);
			AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), Long.parseLong(interval)*1000, pintent);
			
			registerReceiver(bootReceiver, new IntentFilter("com.sdg.etspandroidtracker.TSERVICE"));
		}
		else
		{
			stopService();
			
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.alert)
			        .setContentTitle("ETSP Error")
			        .setContentText("No Internet Connection!");
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(this, MainPage.class);

			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainPage.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(0, mBuilder.build());
			
	    	Toast.makeText(getApplicationContext(),
			         "No Internet Connection!",
			         Toast.LENGTH_LONG).show();		
	    	details.setChecked(false);
		}
    }
    
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
	    unregisterReceiver(resultReceiver);
		super.onPause();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();	
		
        final boolean first = reader.getBoolean(Preferences.firstTime, true);
        if(first){
			Intent registerIntent=new Intent("com.sdg.etspandroidtracker.REGISTER");
			startActivity(registerIntent);	
			finish();
        }
        else{        
        	//Handle fragment
	        summary.setVisibility(View.GONE);	
			android.support.v4.app.FragmentManager frg=getSupportFragmentManager();
			android.support.v4.app.FragmentTransaction trans=frg.beginTransaction();
			Fragment currentstatus=new Details();
			trans.replace(R.id.summarylayout, currentstatus);
			trans.commit();
	        
	        boolean serviceOnBoolean = reader.getBoolean(Preferences.serviceOn, false);
	        if(serviceOnBoolean){
	        	details.setChecked(true);
	        	summary.setVisibility(View.VISIBLE);
				GetDetails();
	        }
        }	
		registerReceiver(resultReceiver, new IntentFilter(TrackerService.NOTIFICATION));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Settings");
        boolean loggedIn = reader.getBoolean(Preferences.loggedin, false);
		if(loggedIn)
		{
			menu.add(0, 1, 0, "Sign Out");
		}
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int n=item.getItemId();
		switch (item.getItemId()) {
        case 0:
			Intent PasswordSettings=new Intent("com.sdg.etspandroidtracker.PASSWORDSETTINGS");
			startActivity(PasswordSettings);
			finish();
			break;
        case 1:
			editor=reader.edit();
			editor.putBoolean(Preferences.loggedin, false);
			editor.commit();
			Intent login=new Intent("com.sdg.etspandroidtracker.LOGIN");
			startActivity(login);
			finish();
			break;
		}
        return super.onOptionsItemSelected(item);
	}
	

	
}
