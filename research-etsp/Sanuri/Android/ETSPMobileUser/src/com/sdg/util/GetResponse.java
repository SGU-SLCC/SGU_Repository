package com.sdg.util;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.sdg.etspmobileuser.R;
import com.sdg.models.CheckListData;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class GetResponse extends AsyncTask<Void, String, String> {
	
	
    private  View rootView;
    private Activity rootAct;
    private ArrayList<String> selectedList=new ArrayList<String>();
    private int method;
    private String ID;
    private String emails;
    private int category;
    private String stringFieldOne;
    private String stringFieldTwo;
    private String selectedReportType;
    Long startTime,stopTime;
    
    public GetResponse(View view,Activity act,ArrayList<String> selectedPrivileges,int method,String ID,int category,String selectedReportType) {
        // TODO Auto-generated constructor stub
        rootView = view;
        rootAct = act;
        this.selectedList=selectedPrivileges;
        this.method=method;
        this.ID=ID;
        this.category=category;
        this.selectedReportType=selectedReportType;
    }


	public GetResponse(View view,Activity act,ArrayList<String> selectedPrivileges,int method,String ID,String emails,int category) {
		// TODO Auto-generated constructor stub
        rootView = view;
        rootAct = act;
        this.selectedList=selectedPrivileges;
        this.method=method;
        this.ID=ID;
        this.emails=emails;
        this.category=category;
	}


	public GetResponse(String stringFieldOne,String stringFieldTwo,int method) {
		// TODO Auto-generated constructor stub
		this.stringFieldOne=stringFieldOne;
		this.stringFieldTwo=stringFieldTwo;
		this.method=method;
	}


	@Override
    protected String doInBackground(Void... params) {
		startTime = SystemClock.uptimeMillis();
		//------------------------------------
		String response ="";
		String words[];
        DataLayer dataLayer = new DataLayer();
        MethodsList currentMethod = MethodsList.values()[method];
        switch (currentMethod) {
		case getPrivileges:
	        response = dataLayer.getPrivileges();
	        publishProgress(response);
			break;
		case checkUsernameExists:
        	response = dataLayer.checkUsernameExists(ID);
        	words=response.split(",");
        	response=words[0];
        	break;
		case getUsers:
			//dataLayer.testInsert();
			//dataLayer.testUpdate();
			//dataLayer.testDelete();
	        response = dataLayer.getUsers();
	        publishProgress(response);
	        break;
		case getUserDetails:
			response = dataLayer.getUserDetails(ID);
			break;
		case getTrackerTypes:
			response = dataLayer.getTrackerTypes();
			break;
		case getSensorDataTypes:
        	response= dataLayer.getSensorDataTypes();
        	publishProgress(response);
        	break;
		case getTrackers:
        	response= dataLayer.getTrackers();
        	publishProgress(response);
        	break;
		case getTrackerDetails:
        	response= dataLayer.getTrackerDetails(ID);
        	publishProgress(response);
        	break;
		case getWildLife:
        	response= dataLayer.getWildLife();
        	publishProgress(response);
        	break;
		case getVehicles:
        	response= dataLayer.getVehicles();
        	publishProgress(response);
        	break;
		case getPeople:
        	response= dataLayer.getPeople();
        	publishProgress(response);
        	break;
		case getOther:
        	response= dataLayer.getOther();
        	publishProgress(response);
        	break;
		case getPredefinedReports:
        	response= dataLayer.getPredefinedReports();
        	break;
		case getCustomReports:
			SharedPreferences sharedPreferences= rootAct.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE);
        	response=dataLayer.getCustomReports(sharedPreferences.getString(Preferences.COMPANY, ""),ID);
        	publishProgress(response);
        	break;
		case emailReport:
        	response=dataLayer.sendReportByEmail(ID,emails);
        	publishProgress(response);
        	break;
		case openReport:
        	response=dataLayer.openReport(ID);
        	publishProgress(response);
        	break;
		case sendPasswordRequest:
        	response=dataLayer.sendPasswordRequest(stringFieldOne).toString();
        	break;
		case authenticate:
        	response = dataLayer.authenticate(stringFieldOne, stringFieldTwo);
        	words=response.split(",");
        	response=words[0];
        	break;
		case getCommands:
			response = dataLayer.getCommands(ID);
			publishProgress(response);
			break;
		case sendConfigurationRequest:
			response=dataLayer.sendConfigurationRequest(stringFieldOne,stringFieldTwo).toString();
			break;
		case getPendingRequests:
			response=dataLayer.getPendingRequests(ID);
			publishProgress(response);
			break;
		case acceptRequests:
			dataLayer.acceptRequests(selectedList);
			break;
		case rejectRequests:
			dataLayer.rejectRequests(selectedList);
			break;
		case getObjectsWithDetails:
			response=dataLayer.getObjectsWithDetails(stringFieldOne,stringFieldTwo);
			break;
		case getObjectsList:
			response=dataLayer.getObjectsList(stringFieldOne);
			publishProgress(response);
			break;
		case getTrackerIMEI:
			response=dataLayer.getTrackerIMEI(ID);
			publishProgress(response);
			break;
		case getCategory:
			response=dataLayer.getCategory(ID);
			publishProgress(response);
		default:
			break;
		}
        
        return  response;
    }

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		MethodsList currentMethod = MethodsList.values()[method];
		ListView listView;
		switch (currentMethod) {
		//MyCustomAdapterChecked----------------------------------------------------------------
		case getPrivileges:
		case getSensorDataTypes:
		case getPendingRequests:
		case getObjectsList:
			listView = (ListView) rootView;
			MyCustomAdapterChecked dataAdapter = new MyCustomAdapterChecked(rootAct,R.layout.sensor_info, prepareList(values[0]),selectedList);		
			listView.setAdapter(dataAdapter);
			break;
		//MyCustomAdapterDelete----------------------------------------------------------------
		case getUsers:
		case getTrackers:
		case getWildLife:
		case getVehicles:
		case getPeople:
		case getOther:
		    listView = (ListView) rootView;	
			listView.setAdapter(new MyCustomAdapterDelete(rootAct, R.layout.list_item_with_delete,prepareList(values[0]),category));
			break;
			//MyCustomAdapterEmail----------------------------------------------------------------
		case getCustomReports:
			String parentReport=selectedReportType;
		    listView = (ListView) rootView;	
			listView.setAdapter(new MyCustomAdapterEmail(rootAct, R.layout.custom_reports_list_item,prepareList(values[0]),parentReport));
			break;
		case emailReport:
	        Toast.makeText(rootAct, 
	        		 "Report Emailed Successfully!", 
	                 Toast.LENGTH_LONG).show();
			break;
		case openReport:
        	//open downloaded report
            Toast.makeText(rootAct, 
           		 "Here comes the report!", 
                    Toast.LENGTH_LONG).show();
            break;
		case getCommands:
			prepareStringList(values[0]);
			ArrayAdapter<String> commandsListAdapter = new ArrayAdapter<String>(rootAct,android.R.layout.simple_spinner_dropdown_item,prepareStringList(values[0]));
			((AutoCompleteTextView)rootView).setAdapter(commandsListAdapter);
			break;
		case getTrackerIMEI:
			ArrayAdapter<String> trackers = new ArrayAdapter<String>(rootAct,android.R.layout.simple_spinner_dropdown_item, prepareStringList(values[0]));      
			((Spinner)rootView).setAdapter(trackers);
			break;
		case getCategory:
			ArrayAdapter<String> categories = new ArrayAdapter<String>(rootAct,android.R.layout.simple_spinner_dropdown_item, prepareStringList(values[0]));      
	        ((Spinner)rootView).setAdapter(categories);
			break;
		default:
			break;
		}
		//------------------------------------
		stopTime = SystemClock.uptimeMillis();
		Long diff =stopTime-startTime;
        Toast.makeText(rootAct, 
        		 "GET elapsed: "+diff.toString(), 
                 Toast.LENGTH_LONG).show();
	}


	private String[] prepareStringList(String value) {
		// TODO Auto-generated method stub
		String[] itemsList = null;
		try {
			JSONObject obj=new JSONObject(value);
		    JSONArray items= obj.getJSONArray("rows");
		    itemsList=new String[items.length()];
	    for(int i=0;i<items.length(); i++){
	        JSONObject jsonas = items.getJSONObject(i);
	        String item;
	        item = jsonas.getString("c0");
			itemsList[i]=item;
	    }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemsList;
	}


	private ArrayList<CheckListData> prepareList(String value) {
		// TODO Auto-generated method stub
		JSONObject obj;
	    ArrayList<CheckListData> itemsList=new ArrayList<CheckListData>();
		try {
			obj = new JSONObject(value);
			JSONArray jsonArray = obj.getJSONArray("rows");		
		    for(int i=0;i<jsonArray.length(); i++){
		        JSONObject jsonas = jsonArray.getJSONObject(i);
		        CheckListData data=new CheckListData(jsonas.getString("c0"), jsonas.getString("c1"), false);
		        itemsList.add(data);
		    }
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemsList;
	}


    
    
}