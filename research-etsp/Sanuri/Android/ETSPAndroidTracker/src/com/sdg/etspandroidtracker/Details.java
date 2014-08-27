package com.sdg.etspandroidtracker;

import java.util.ArrayList;
import java.util.Arrays;

import com.sdg.util.Preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.content.SharedPreferences;

public class Details extends android.support.v4.app.Fragment{

	private TextView textViewInterval,textViewMode;
	private ListView listViewSensors;
	private String sensors="",interval="5",mode="";
	private ArrayList<String> activeSensors;
	private SharedPreferences reader=null;

	public Details(){
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		reader= this.getActivity().getSharedPreferences(Preferences.MyPREFERENCES, Context.MODE_PRIVATE); //different in a fragment! :O
		GetDetails();
		LinearLayout CreatePassLLayout=(LinearLayout)inflater.inflate(R.layout.details_page, container,false);
		return CreatePassLLayout;
	}
	
	private void GetDetails() {
		// TODO Auto-generated method stub
		sensors = reader.getString(Preferences.Sensors, "");
		interval = reader.getString(Preferences.Interval, "");
		mode = reader.getString(Preferences.Mode, "");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		listViewSensors=(ListView)getView().findViewById(R.id.listViewActiveSensors);
		textViewInterval=(TextView)getView().findViewById(R.id.textViewInterval);
		textViewMode=(TextView)getView().findViewById(R.id.textViewMode);
		GetDetails();
		FillDetails();
	}

	public void FillDetails() {
		// TODO Auto-generated method stub
		textViewInterval.setText("Every "+interval+" Seconds");
		if(!mode.equals("")){
			if(Integer.parseInt(mode)==1){
				textViewMode.setText("GPRS");
			}
			else{
				textViewMode.setText("SMS");
			}
		}
		activeSensors=new ArrayList<String>();
			String[] Words=sensors.split("/");
			activeSensors.addAll(Arrays.asList(Words));
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
		                getActivity(), 
		                R.layout.custom_list_item,
		                activeSensors );
			listViewSensors.setAdapter(arrayAdapter);
	}





	@Override
	public void setMenuVisibility(boolean menuVisible) {
		// TODO Auto-generated method stub
		super.setMenuVisibility(menuVisible);
		if(menuVisible){
			FillDetails();
		}
	}
	
	
}
