package com.sdg.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.GsonBuilder;
import com.sdg.models.GeoFence;
import com.sdg.models.Other;
import com.sdg.models.Person;
import com.sdg.models.Tracker;
import com.sdg.models.User;
import com.sdg.models.Vehicle;
import com.sdg.models.WildLife;

public class DataLayer {

	//----------------------------------------------------AUTHENTICATION N SECURITY---------------------------------------------------------------------------

	public String authenticate(String username, String password) {
		// TODO Auto-generated method stub
		String info = HttpRequest.get("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/user/authenticate.php",true,"username",username,"password",password).body();
		return info;
	}
	
	public Boolean sendPasswordRequest(String username) {
		// TODO Auto-generated method stub
		HttpRequest.post("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/user/sendPasswordRequest.php",true,"username",username).body();
		return true;
	}
//-------------------------------------------------------------USER---------------------------------------------------------------------------
	
	public String insertUser(User user) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("name", user.getName());
		obj.put("username", user.getUsername());
		obj.put("password", user.getPassword());
		obj.put("description", user.getDescription());
		obj.put("privileges", user.getPrevileges().toString());
		insertEntity("users",obj);
		return "true";
	}

	public String getPrivileges() {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/privileges/find/_id&privilegeName").body();
		return response;
	}

	//Cz its better to do the heavy processing at server, we dont have much memory here!
	public String checkUsernameExists(String username) {
			// TODO Auto-generated method stub
			//HttpRequest request=HttpRequest.post("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/user/checkUsernameExists.php",true,"username",username);
			//String response = request.body();
			return "false";
	}

	public String getUsers() {
			// TODO Auto-generated method stub
			String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/users/find/_id&username").body();
			return response;
	}

	public String getUserDetails(String id) {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/users/"+id).body();
		return response;
	}

	public String deleteUser(String id) {
		// TODO Auto-generated method stub
		deleteEntity("users", id);
		return "true";
	}

	public String updatetUser(User user) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("name", user.getName());
		obj.put("username", user.getUsername());
		obj.put("description", user.getDescription());
		obj.put("privileges", user.getPrevileges().toString());
		updateEntity("users",user.getId(), obj);
		return "true";
	}
	

	//-------------------------------------------------------------TRACKER---------------------------------------------------------------------------

	public String getTrackerTypes() {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/trackerTypes/find/name").body();
		return response;
	}

	public String getSensorDataTypes() {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/sensorDataTypes/find/_id&name").body();
		return response;
	}

	public String insertTracker(Tracker t) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("imei", t.getImeiNumber());
		obj.put("contact", t.getTrackerContactNumber());
		obj.put("type", t.getTrackerType());
		obj.put("mode", t.getMode().toString());
		obj.put("data", t.getData().toString());
		insertEntity("trackers", obj);
		return "true";
	}

	public String updateTracker(Tracker tracker) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("imei", tracker.getImeiNumber());
		obj.put("contact", tracker.getTrackerContactNumber());
		obj.put("type", tracker.getTrackerType());
		obj.put("mode", tracker.getMode().toString());
		obj.put("data", tracker.getData().toString());
		updateEntity("trackers", tracker.getId(), obj);
		return "true";
	}
	
	public String getTrackers() {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/trackers/find/_id&imei").body();
		return response;
	}
	
	public String getTrackerDetails(String id) {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/trackers/"+id).body();
		return response;
	}
	
	public String deleteTracker(String id) {
		// TODO Auto-generated method stub
		deleteEntity("trackers", id);
		return "true";
	}

	//Get commands associated with the given specific tracker, so far
	public String getCommands(String imei) {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/search/commands?imei="+imei).body();
		return response;
	}

	//Insert into commands collection
	public Boolean sendConfigurationRequest(String imeiNumber, String command) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("imei", imeiNumber);
		obj.put("command", command);
		insertEntity("commands", obj);
		return true;
	}
	
	//-------------------------------------------------------------Registration requests---------------------------------------------------------------------------

	public String getPendingRequests(String company) {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/user/getPendingRequests.php",true,"company",company).body();
		return response;
	}

	public Boolean rejectRequests(ArrayList<String> selectedRequests) {
		// TODO Auto-generated method stub
		HttpRequest.post("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/user/rejectRequests.php",true,"requests",selectedRequests).body();
		return true;
	}

	public Boolean acceptRequests(ArrayList<String> selectedRequests) {
		// TODO Auto-generated method stub
		HttpRequest.post("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/user/acceptRequests.php",true,"requests",selectedRequests).body();
		return true;
	}
	
	
	//-------------------------------------------------------------MAPPING---------------------------------------------------------------------------

	public String getObjectsWithDetails(String filter,String company) {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/map/getObjectsWithDetails.php",true,"filter",filter,"company",company).body();
		return response;
	}

	public String getLocationData(String filter, String company) {
		// TODO Auto-generated method stub
		/*String data="";
		for(int i=0;i<4;i++)
		{
			data=data+i+" "+(i*5+n)+" "+(i*5+n)+":";
			
		}
		data=data+4+" 5.0 80.0:";
		return data;*/
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/map/getObjectsWithDetails.php",true,"filter",filter,"company",company).body();
		return response;
	}

	public String getObjectsList(String company) {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/map/getObjectsList.php",true,"company",company).body();
		return response;
	}
	
//-----------------------------------------------REPORTS---------------------------------------------------------------
	public String getPredefinedReports() {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/standardReports/find/name").body();
		return response;
	}

	//Get report names with ids for the company and belongs to te standard report type
	public String getCustomReports(String company,String predefiedReport) {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/report/getCustomReports.php",true,"predefinedReport",predefiedReport).body();
		return response;
	}

	public String sendReportByEmail(String reportToEmail,String emails) {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/report/sendReportByEmail.php",true,"report",reportToEmail,"emails",emails).body();
		return response;
	}

	public String openReport(String reportToDownload) {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/report/sendReportByEmail.php",true,"report",reportToDownload).body();
		return response;
	}
	
	public String deleteCustomReport(String reportToDelete) {
		// TODO Auto-generated method stub
		deleteEntity("customrReports", reportToDelete);
		return "true";
	}
	
	//-----------------------------------------------------------OBJECTS-------------------------------------------------------------------------------------
	
	public String getWildLife() {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/animals/find/_id&name").body();
		return response;
	}

	public String getVehicles() {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/vehicles/find/_id&registration").body();
		return response;
	}

	public String getPeople() {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/people/find/_id&name").body();
		return response;
	}

	public String getOther() {
		// TODO Auto-generated method stub
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/others/find/_id&name").body();
		return response;
	}

	//Directly accessed from presentation layer, cz small info and, those fields should be filled immediately
	public java.lang.Object getObjectDetails(String objectID, String objectType) {
		// TODO Auto-generated method stub
		String collection=objectType+"s";
		if(objectType.equalsIgnoreCase("person"))
			collection="people";
		String response = HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/"+collection+"/"+objectID).body();
		java.lang.Object object=new java.lang.Object();
		try {
			JSONObject obj = new JSONObject(response);
			if(objectType.equalsIgnoreCase("animal"))
			{
				WildLife wildLife=new WildLife();
				wildLife.setObjectID(objectID);
				wildLife.setObjectName(obj.getString("name"));
				wildLife.setAge(obj.getDouble("age"));
				wildLife.setCategory(obj.getString("category"));
				wildLife.setDescription(obj.getString("description"));
				wildLife.setProfile(objectType);
				wildLife.setSpecificSigns(obj.getString("specificSigns"));
				wildLife.setTrackerIMEI(obj.getInt("imei"));
				return wildLife;
			}
			else if(objectType.equalsIgnoreCase("vehicle"))
			{
				Vehicle vehicle=new Vehicle();
				vehicle.setRegisterationNumber(objectID);
				vehicle.setCategory(obj.getString("category"));
				vehicle.setColor(obj.getString("color"));
				vehicle.setCost(obj.getDouble("cost"));
				vehicle.setDescription(obj.getString("description"));
				vehicle.setDriver(obj.getString("driver"));
				vehicle.setObjectID(objectID);
				vehicle.setProfile(objectType);
				vehicle.setTrackerIMEI(obj.getInt("imei"));
				return vehicle;
			}
			else if(objectType.equalsIgnoreCase("person"))
			{
				Person person=new Person();
				person.setObjectID(objectID);
				person.setObjectName(obj.getString("name"));
				person.setAge(obj.getInt("age"));
				person.setTrackerIMEI(obj.getInt("imei"));
				person.setLocation(obj.getString("location"));
				person.setNIC(obj.getString("NIC"));
				person.setProfile(objectType);
				person.setDescription(obj.getString("remarks"));
				return person;
			}
			else
			{
				Other other=new Other();
				other.setTrackerIMEI(obj.getInt("imei"));
				other.setProfile(objectType);
				other.setObjectID(objectID);
				other.setDescription(obj.getString("description"));
				other.setObjectName(obj.getString("name"));
				return other;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return object;
	}
	
	public String insertWildLife(WildLife wildLife) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("name", wildLife.getObjectName());
		obj.put("age", wildLife.getAge().toString());
		obj.put("category", wildLife.getCategory());
		obj.put("description", wildLife.getDescription());
		obj.put("profile", wildLife.getProfile());
		obj.put("specificSigns", wildLife.getSpecificSigns());
		obj.put("tracker", wildLife.getTrackerIMEI().toString());
		insertEntity("animals", obj);
		return "true";
	}

	public String updateWildLife(WildLife wildLife) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("id", wildLife.getObjectID());
		obj.put("name", wildLife.getObjectName());
		obj.put("age", wildLife.getAge().toString());
		obj.put("category", wildLife.getCategory());
		obj.put("description", wildLife.getDescription());
		obj.put("profile", wildLife.getProfile());
		obj.put("specificSigns", wildLife.getSpecificSigns());
		obj.put("tracker", wildLife.getTrackerIMEI().toString());
		updateEntity("animals", wildLife.getObjectID(), obj);
		return "true";
	}
	
	public String insertVehicle(Vehicle vehicle) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("color", vehicle.getColor());
		obj.put("registration", vehicle.getRegisterationNumber());
		obj.put("category", vehicle.getCategory());
		obj.put("description", vehicle.getDescription());
		obj.put("profile", vehicle.getProfile());
		obj.put("cost", vehicle.getCost().toString());
		obj.put("driver", vehicle.getDriver());
		obj.put("tracker", vehicle.getTrackerIMEI().toString());
		insertEntity("vehicles", obj);
		return "true";
	}

	public String updateVehicle(Vehicle vehicle) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("id", vehicle.getObjectID());
		obj.put("color", vehicle.getColor());
		obj.put("registration", vehicle.getRegisterationNumber());
		obj.put("category", vehicle.getCategory());
		obj.put("description", vehicle.getDescription());
		obj.put("profile", vehicle.getProfile());
		obj.put("cost", vehicle.getCost().toString());
		obj.put("driver", vehicle.getDriver());
		obj.put("tracker", vehicle.getTrackerIMEI().toString());
		updateEntity("vehicles", vehicle.getObjectID(), obj);
		return "true";
	}

	public String insertPerson(Person person) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("name", person.getObjectName());
		obj.put("age", person.getAge().toString());
		obj.put("location", person.getLocation());
		obj.put("remarks", person.getDescription());
		obj.put("profile", person.getProfile());
		obj.put("NIC", person.getNIC());
		obj.put("tracker", person.getTrackerIMEI().toString());
		insertEntity("people", obj);
		return "true";
	}

	public String updatePerson(Person person) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("id", person.getObjectID());
		obj.put("name", person.getObjectName());
		obj.put("age", person.getAge().toString());
		obj.put("location", person.getLocation());
		obj.put("remarks", person.getDescription());
		obj.put("profile", person.getProfile());
		obj.put("NIC", person.getNIC());
		obj.put("tracker", person.getTrackerIMEI().toString());
		updateEntity("people", person.getObjectID(), obj);
		return "true";
	}
	
	public String insertOther(Other other) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("name", other.getObjectName());
		obj.put("description", other.getDescription());
		obj.put("profile", other.getProfile());
		obj.put("tracker", other.getTrackerIMEI().toString());
		insertEntity("others", obj);
		return "true";
	}

	public String updateOther(Other other) {
		// TODO Auto-generated method stub
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("id", other.getObjectID());
		obj.put("name", other.getObjectName());
		obj.put("description", other.getDescription());
		obj.put("profile", other.getProfile());
		obj.put("tracker", other.getTrackerIMEI().toString());
		updateEntity("öthers", other.getObjectID(), obj);
		return "true";
	}

	public String deleteObject(String ID, String category) {
		// TODO Auto-generated method stub
		deleteEntity(category, ID);
		return "true";
	}

	public String getTrackerIMEI(String company) {
		// TODO Auto-generated method stub
		String response=HttpRequest.get("http://"+GetIPAddress.getIP()+"/collections/search/trackers?company="+company).body();
		return response;
	}

	public String getCategory(String profile) {
		// TODO Auto-generated method stub
		String response=HttpRequest.get("http://"+GetIPAddress.getIP()+"/ETSP_MongoAPI/v1/object/getCategory.php",true,"profile",profile).body();
		return response;
	}

	//-----------------------------------------------------------GEOFENCES-------------------------------------------------------------------------------------
	
	//get the set of geofences associated with current object and company
	public List<GeoFence> getGeoFences(String filter, String company,int object) {
		// TODO Auto-generated method stub
		
		if(filter.equalsIgnoreCase("")){
			//get geofences of a specific object
		}
		else{
			//get all geofences according to the filter
		}
		
		List<GeoFence> geofences=new ArrayList<GeoFence>();
		GeoFence fence=new GeoFence();
		LatLng point=new LatLng(0, 0); // start
		fence.getGeoFence().add(point);
		point=new LatLng(100, 100);
		fence.getGeoFence().add(point);
		point=new LatLng(50, 150);
		fence.getGeoFence().add(point);
		point=new LatLng(0, 100);
		fence.getGeoFence().add(point);
		point=new LatLng(0, 50);
		fence.getGeoFence().add(point);
		point=new LatLng(0, 0);//end
		fence.getGeoFence().add(point);
		geofences.add(fence);
		return geofences;
	}

	//-----------------------------------------------------------ALERTS-------------------------------------------------------------------------------------

	public void deleteAlert(String alertID) {
		// TODO Auto-generated method stub
		
	}
	
	public LatLng getCurrentPosition(String objectID, String company) {
		// TODO Auto-generated method stub
		LatLng point=new LatLng(6, 80);
		return point;
	}	
	
//--------------------------------Reusable methods for rest calls- Insert, Update and Delete-----------------------------
	public String insertEntity(String collection,Map<String,String> object)
	{
		String json = new GsonBuilder().create().toJson(object, Map.class);		
		try {
	        HttpPost httpPost = new HttpPost("http://"+GetIPAddress.getIP()+"/collections/"+collection);
	        httpPost.setEntity(new StringEntity(json));
	        httpPost.setHeader("Accept", "application/json");
	        httpPost.setHeader("Content-type", "application/json");
	        return new DefaultHttpClient().execute(httpPost).toString();
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return "false";
	}
	
	public String updateEntity(String collection,String objectID,Map<String,String> object)
	{
		String json = new GsonBuilder().create().toJson(object, Map.class);		
		try {
	        HttpPut httpPut = new HttpPut("http://"+GetIPAddress.getIP()+"/collections/"+collection+"/"+objectID);
	        httpPut.setEntity(new StringEntity(json));
	        httpPut.setHeader("Accept", "application/json");
	        httpPut.setHeader("Content-type", "application/json");
	        return new DefaultHttpClient().execute(httpPut).toString();
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return "false";
	}
	
	public String deleteEntity(String collection,String objectID)
	{		
		try {
	        HttpDelete httpDelete = new HttpDelete("http://"+GetIPAddress.getIP()+"/collections/"+collection+"/"+objectID);
	        httpDelete.setHeader("Accept", "application/json");
	        httpDelete.setHeader("Content-type", "application/json");
	        return new DefaultHttpClient().execute(httpDelete).toString();
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return "false";
	}
}
