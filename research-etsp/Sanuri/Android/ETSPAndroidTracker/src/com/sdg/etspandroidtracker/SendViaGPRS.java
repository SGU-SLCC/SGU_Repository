package com.sdg.etspandroidtracker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.GsonBuilder;
import com.sdg.util.GetIPAddress;
import com.sdg.util.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SendViaGPRS{
    
    public SendViaGPRS(){
    	super();
    }
    
    public boolean sendGPRSData(String message) {
    	Map<String, String> obj = new HashMap<String, String>();
		obj.put("message", message);
		insertEntity("commands", obj);
		return true;
    }
    
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
}
          

