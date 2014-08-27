package com.sdg.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.Toast;

import com.sdg.models.Other;
import com.sdg.models.Tracker;
import com.sdg.models.User;
import com.sdg.models.Vehicle;
import com.sdg.models.WildLife;

public class HandleUpdates extends AsyncTask<Void, String, String>{

    private Activity rootAct;
    private Object object;
    private int objectType;
    Long startTime,stopTime;
	
    public HandleUpdates(Activity act,Object object,int objectType) {
        // TODO Auto-generated constructor stub
        rootAct = act;
        this.object=object;
        this.objectType=objectType;
    }
	
	
	@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		startTime = SystemClock.uptimeMillis();
		//------------------------------------
        DataLayer dataLayer = new DataLayer();
        String response="false";
        MethodsList currentObjectType = MethodsList.values()[objectType];
        
        switch (currentObjectType) {
		case user:
			response = dataLayer.updatetUser((User)object);
			break;
		case tracker:
			response = dataLayer.updateTracker((Tracker)object);
			break;
		case animal:
			response = dataLayer.updateWildLife((WildLife)object);
			break;
		case vehicle:
			response = dataLayer.updateVehicle((Vehicle)object);
			break;
		default:
			response = dataLayer.updateOther((Other)object);
			break;
		}

        String []words=response.split(",");
        publishProgress(words[0]);
        return  words[0];
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		if(values[0].equalsIgnoreCase("true"))
		{
	        Toast.makeText(rootAct, 
	        		 "Update Successful!", 
	                 Toast.LENGTH_LONG).show();
	        rootAct.finish();
		}
		else
		{
	        Toast.makeText(rootAct, 
	        		 "Update Failed!", 
	                 Toast.LENGTH_LONG).show();
		}
		
		//------------------------------------
		stopTime = SystemClock.uptimeMillis();
		Long diff =stopTime-startTime;
        Toast.makeText(rootAct, 
        		 "UPDATE elasped: "+diff.toString(), 
                 Toast.LENGTH_LONG).show();
	}
}
