package com.sdg.etspandroidtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

class ResultReceiver extends BroadcastReceiver {   
	
	public ResultReceiver() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	 public void onReceive(Context context, Intent intent) {
		
	      Bundle bundle = intent.getExtras();
	      if (bundle != null) {
	    	  
	        int resultCode = bundle.getInt(TrackerService.RESULT);
             if (resultCode == -1)
             {
	        	 Messages.showError("ETSP", "Data Sent Successfully", context); 
	        	 //editor=reader.edit();
	        	 //editor.putBoolean(Preferences.result, false);
	        	 //editor.commit();
             }
             /*else
             {
	        	 Messages.showError("ETSP", "Data Sending Failed!", context);
             }*/
	      }
	}
}