package com.sdg.etspandroidtracker;

import java.util.Calendar;

import com.sdg.util.Preferences;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class BootReceiver extends BroadcastReceiver{

	
	private SharedPreferences reader=null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
	    if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
	    	reader= context.getSharedPreferences(Preferences.MyPREFERENCES, Context.MODE_PRIVATE); 
	    	 boolean serviceOnBoolean = reader.getBoolean(Preferences.serviceOn, false);
				String sensors = reader.getString(Preferences.Sensors, "");
				String interval = reader.getString(Preferences.Interval, "5");
				String mode = reader.getString(Preferences.Mode, "1");
	    	 if(serviceOnBoolean)
	    	 {
		    	 Intent serviceIntent = new Intent();
		    	 serviceIntent.setComponent(new ComponentName("com.sdg.etspandroidtracker","com.sdg.etspandroidtracker.TrackerService"));
				 serviceIntent.putExtra(TrackerService.MODE, mode);
				 serviceIntent.putExtra(TrackerService.INTERVAL, interval);
				 serviceIntent.putExtra(TrackerService.SENSORS, sensors);
					
				 Calendar cal = Calendar.getInstance();
				 PendingIntent pintent = PendingIntent.getBroadcast(context, 1111, serviceIntent, 0);
				 AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				 alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), Long.parseLong(interval)*1000, pintent);
	    	 }
	    }
	}

}
