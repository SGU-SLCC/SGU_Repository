package com.sdg.etspandroidtracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.sdg.util.Preferences;

import android.database.Cursor;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

public class SetUp extends Activity{

	private MyCustomAdapter dataAdapter = null;
	private String sensors="",interval="5",mode="";
	private EditText editTextInterval;
	private RadioButton radioButtonSMS,radioButtonGPRS;
	private ArrayList<String> selectedSensors=new ArrayList<String>();
    private SharedPreferences reader = null;
    private Editor editor = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		
		//Prepare shared preferences
		reader = this.getSharedPreferences(Preferences.MyPREFERENCES, Context.MODE_PRIVATE); 
		editor = reader.edit();
		
		
        final boolean first = reader.getBoolean(Preferences.firstTime, true);
        if(first){
            editor.putBoolean(Preferences.firstTime, false); 
            editor.commit();
        }
		
		editTextInterval=(EditText)findViewById(R.id.editTextTimeInterval);
		radioButtonSMS=(RadioButton)findViewById(R.id.radioButtonGSM);
		radioButtonGPRS=(RadioButton)findViewById(R.id.radioButtonGPRS);
	}

	
	
	private void saveData() {
		// TODO Auto-generated method stub
	      editor.putString(Preferences.Sensors, sensors);
	      editor.putString(Preferences.Interval, interval.toString());
	      editor.putString(Preferences.Mode, mode.toString());
	      editor.commit(); 
	}

	private void getData() {
		// TODO Auto-generated method stub
		sensors="";
		if(selectedSensors!=null && selectedSensors.size()!=0){
			for (String mySensor : selectedSensors) {
				if(mySensor!=null){
					sensors+=mySensor+"/";
				}
			}	
			
			sensors=sensors.substring(0, sensors.length()-1);	
		}		
		interval=editTextInterval.getText().toString();	
		if(radioButtonSMS.isChecked()){
			mode="0";
		}
		else if(radioButtonGPRS.isChecked()){
			mode="1";
		}
	}

	@Override
    public void onBackPressed() {
		getData();
        if(mode.equals("")){
        	Messages.showError("ETSP Tracker", "Please Select the Mode!",this);
        }
        else if(sensors.equals("")){
        	Messages.showError("ETSP Tracker", "Please Select Data to Send!",this);
        }
        else if(interval.equals("") || Integer.parseInt(interval)==0){
        	Messages.showError("ETSP Tracker", "Please Enter the Time Interval!",this);
        }
        else if(Integer.parseInt(interval) < 10){
        	Messages.showError("ETSP Tracker", "Minimum Interval Allowed is 10 Seconds!",this);
        }
        else{
        	if(mode.equalsIgnoreCase("0")){
        		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		    		saveData();
        					Intent setupIntent=new Intent("com.sdg.etspandroidtracker.START");
        					startActivity(setupIntent);	
        					finish();
        		            break;
        		        case DialogInterface.BUTTON_NEGATIVE:
        		            break;
        		        }
        		    }
        		};

        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		builder.setMessage("Are you sure you want to use SMS mode? This will cost money!").setPositiveButton("Yes", dialogClickListener)
        		    .setNegativeButton("No", dialogClickListener).show();

        	}
        	else{
	    		saveData();
				Intent setupIntent=new Intent("com.sdg.etspandroidtracker.START");
				startActivity(setupIntent);	
				finish();
        	}
        
        }
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		restoreData();
	}

	private void restoreData() {
		// TODO Auto-generated method stub
		

		sensors = reader.getString(Preferences.Sensors, "");
		interval = reader.getString(Preferences.Interval, "10");
		mode = reader.getString(Preferences.Mode, "1");
		
		if(!interval.equals("")){
			editTextInterval.setText(interval);
		}
		
		radioButtonSMS.setChecked(false);
		radioButtonGPRS.setChecked(false);
		if(mode.equals("1")){
			radioButtonGPRS.setChecked(true);
		}
		else if(mode.equals("0")){
			radioButtonSMS.setChecked(true);
		}
		//resumeSensors=new ArrayList<String>();
		selectedSensors.clear();
		String[] Words=sensors.split("/");
		selectedSensors.addAll(Arrays.asList(Words));

		displayListView();	
	}

	//Get the list of available sensors on device
	private List<Sensor> getSensors() {
		
		// TODO Auto-generated method stub
		SensorManager mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<Sensor> sensorList=mgr.getSensorList(Sensor.TYPE_ALL);
	    return sensorList;
	}
	
	//Create and display check box list
	private void displayListView() {
		//get available sensors list
		List<Sensor> sensors=getSensors();
		//Get retrievable info
		ArrayList<MySensor> infoTypes=getInformationTypes(sensors);
		//create an ArrayAdaptar from the String Array
		dataAdapter = new MyCustomAdapter(this,R.layout.sensor_info, infoTypes);
		ListView listViewSensors = (ListView) findViewById(R.id.listViewSensors);
		// Assign adapter to ListView
		listViewSensors.setAdapter(dataAdapter);
	}
	
	private ArrayList<MySensor> getInformationTypes(List<Sensor> sensors) {
		// TODO Auto-generated method stub
		ArrayList<MySensor> infoTypes=new ArrayList<MySensor>();
		MySensor s=new MySensor("Location", false);
		infoTypes.add(s);
		for (Sensor sensor : sensors) {
			if(sensor.getType()==Sensor.TYPE_TEMPERATURE)
			{
		        s=new MySensor("Temperature", false);
		        infoTypes.add(s);
			}
			else if(sensor.getType()==Sensor.TYPE_ACCELEROMETER)
			{
		        s=new MySensor("Acceleration", false);
		        infoTypes.add(s);
		        s=new MySensor("Distance", false);
		        infoTypes.add(s);
		        s=new MySensor("Speed", false);
		        infoTypes.add(s);
			}
			else if(sensor.getType()==Sensor.TYPE_PRESSURE)
			{
		        s=new MySensor("Pressure", false);
		        infoTypes.add(s);
			}
			else if(sensor.getType()==Sensor.TYPE_RELATIVE_HUMIDITY)
			{
		        s=new MySensor("Humidity", false);
		        infoTypes.add(s);
			}
			else if(sensor.getType()==Sensor.TYPE_ORIENTATION)
			{
		        s=new MySensor("Orientation", false);
		        infoTypes.add(s);
			}
			else if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
		        s=new MySensor("Step Count", false);
		        infoTypes.add(s);
			}
			else if (sensor.getType() == Sensor.TYPE_GRAVITY) {
		        s=new MySensor("Gravity", false);
		        infoTypes.add(s);
			}
		}
		return infoTypes;
	}

	//----------------------------------------------------------------------------------------------

	private class MyCustomAdapter extends ArrayAdapter<MySensor> {
		  
		  private ArrayList<MySensor> sensorList;
		  
		  //Constructor
		  public MyCustomAdapter(Context context, int textViewResourceId,ArrayList<MySensor> sensors) {
			super(context, textViewResourceId, sensors);
		  	sensorList = new ArrayList<MySensor>();
		  	sensorList.addAll(sensors);
		  }
		  
		  //Holds Item information
		  private class ViewHolder {
		   CheckBox name;
		  }
		  
		  //Gets view
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		  
		   ViewHolder holder = null;
		  
		   if (convertView == null) {
		   LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		   convertView = vi.inflate(R.layout.sensor_info, null);
		  
		   holder = new ViewHolder();
		   holder.name = (CheckBox) convertView.findViewById(R.id.checkBoxSelection);
		   holder.name.setTextColor(Color.WHITE);	   
		   
		   
		   convertView.setTag(holder);
		  
			    holder.name.setOnClickListener( new View.OnClickListener() { 
			     
			    	public void onClick(View v) { 
			      CheckBox cb = (CheckBox) v ; 
			      MySensor mySensor = (MySensor) cb.getTag(); 
			      mySensor.setSelected(cb.isChecked());
			      
			      for (int i = 0; i < selectedSensors.size(); i++) {
					if(selectedSensors.get(i).equalsIgnoreCase(mySensor.name)){
						selectedSensors.remove(i);
					}
				}
			    
			      if(mySensor.selected){
			    	  selectedSensors.add(mySensor.name);
			     } 
			    	}
			    });
		   }
		   else {
		    holder = (ViewHolder) convertView.getTag();
		   }
		  
		   MySensor sensor = sensorList.get(position);
		   
		   if(selectedSensors!=null){
			   for (String selectedSensor : selectedSensors) {
				if(selectedSensor.equals(sensor.getName())){
					sensor.selected=true;
				}
			   }
		   }
		  
		   holder.name.setText(sensor.getName());
		   holder.name.setChecked(sensor.isSelected());
		   holder.name.setTag(sensor);		  
		   return convertView;
		  
		  }	  
	}
	
}
	
