package com.sdg.util;

import com.sdg.models.Other;
import com.sdg.models.Tracker;
import com.sdg.models.User;
import com.sdg.models.Vehicle;
import com.sdg.models.WildLife;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

public class HandleInsertion extends AsyncTask<Void, String, String>{

    private Activity rootAct;
    private String result;
    private Object object;
    private int objectType;
    Long startTime,stopTime;
	
    public HandleInsertion(Activity act,Object object,int objectType) {
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
			response = dataLayer.insertUser((User)object);
			break;
		case tracker:
			response = dataLayer.insertTracker((Tracker)object);
			break;
		case animal:
			response = dataLayer.insertWildLife((WildLife)object);
			break;
		case vehicle:
			response = dataLayer.insertVehicle((Vehicle)object);
			break;
		default:
			response = dataLayer.insertOther((Other)object);
			break;
		}

        //String []words=response.split(",");
        //publishProgress(words[0]);
        //return  words[0];
        publishProgress(response);
        return response;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		rootAct.finish();
		if(values[0].equalsIgnoreCase("true"))
		{
	        Toast.makeText(rootAct, 
	        		 "Insertion Successful!", 
	                 Toast.LENGTH_LONG).show();
	        rootAct.finish();
		}
		else
		{
	        Toast.makeText(rootAct, 
	        		 "Insertion Failed!", 
	                 Toast.LENGTH_LONG).show();
		}
		
		//------------------------------------
		stopTime = SystemClock.uptimeMillis();
		Long diff =stopTime-startTime;
        Toast.makeText(rootAct, 
        		 "INSERT elasped: "+diff.toString(), 
                 Toast.LENGTH_LONG).show();
	}
}
