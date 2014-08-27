package com.sdg.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.Toast;

import com.sdg.models.User;

public class HandleDeletion extends AsyncTask<Void, String, String>{

    private String ID;
    private int category;
    private Activity rootAct;
    Long startTime,stopTime;
	
    public HandleDeletion(Activity rootAct,String ID,int category) {
        // TODO Auto-generated constructor stub
        this.ID=ID;
        this.category=category;
        this.rootAct=rootAct;
    }
	
	
	@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		startTime = SystemClock.uptimeMillis();
		//------------------------------------
		String [] words;
        DataLayer dataLayer = new DataLayer();
        String response="false";
        MethodsList currentCategory = MethodsList.values()[category];
        switch (currentCategory) {
		case user:
        	response = dataLayer.deleteUser(ID);
			break;
		case animal:
		case vehicle:
		case person:
		case other:
			String name=currentCategory.name()+"s";
			if(name.equalsIgnoreCase("person"))
				name="people";
        	response = dataLayer.deleteObject(ID, name);
        	publishProgress(response);
        	break;
		case tracker:
        	response = dataLayer.deleteTracker(ID);
        	break;
		case customReport:
			response = dataLayer.deleteCustomReport(ID);
			break;
		default:
			break;
		}
        words=response.split(",");
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
	        		 "Deletion Successful!", 
	                 Toast.LENGTH_LONG).show();
		}
		else
		{
	        Toast.makeText(rootAct, 
	        		 "Deletion Failed!", 
	                 Toast.LENGTH_LONG).show();
		}
		
		//------------------------------------
		stopTime = SystemClock.uptimeMillis();
		Long diff =stopTime-startTime;
        Toast.makeText(rootAct, 
        		 "DELETE elasped: "+diff.toString(), 
                 Toast.LENGTH_LONG).show();
	}
}
