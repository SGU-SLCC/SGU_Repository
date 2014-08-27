package com.sdg.etspmobileuser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.gson.Gson;
import com.sdg.models.Vehicle;
import com.sdg.models.WildLife;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.Messages;
import com.sdg.util.MethodsList;
import com.sdg.util.Preferences;

public class NewVehicle extends Activity  implements OnClickListener,OnItemSelectedListener{

	private SharedPreferences sharedPreferences=null;
	private Button buttonSuccess;
	private Button buttonReset;
	private Button buttonNewTracker;
	private DataLayer dataLayer;
	private Boolean mode;
	private Boolean newTracker;
	private TextView textViewHeader;
	private Spinner spinnerTracker;
	private EditText editTextRegistrationNumber;
	private Spinner spinnerCategory;
	private EditText editTextColor;
	private EditText editTextDriver;
	private EditText editTextCost;
	private EditText editTextDescription;
	private Vehicle vehicle;
	private String[] trackerIMEIList;
	private String[] categoryList;
	private ArrayAdapter<String> trackers;
	private ArrayAdapter<String> categories;
	private String registrationNumber;
	private Editor editor=null;
	private Gson gson;
	private GetResponse asyncRate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_vehicle);
		dataLayer=new DataLayer();
		textViewHeader=(TextView)findViewById(R.id.textViewVehicleHeader);
		spinnerTracker=(Spinner)findViewById(R.id.spinnerVehicleTracker);
		spinnerTracker.setOnItemSelectedListener(this);
		editTextRegistrationNumber=(EditText)findViewById(R.id.editTextVehicleRegistrationNumber);
		spinnerCategory=(Spinner)findViewById(R.id.spinnerVehicleCategory);
		spinnerCategory.setOnItemSelectedListener(this);
		editTextColor=(EditText)findViewById(R.id.editTextColor);
		editTextDriver=(EditText)findViewById(R.id.editTextDriver);
		editTextCost=(EditText)findViewById(R.id.editTextCost);
		editTextDescription=(EditText)findViewById(R.id.editTextVehicleDescription);
		buttonSuccess=(Button)findViewById(R.id.buttonCreateVehicle);
		buttonReset=(Button)findViewById(R.id.buttonResetVehicle);
		buttonNewTracker=(Button)findViewById(R.id.buttonNewTrackerVehicle);
		buttonSuccess.setOnClickListener(this);
		buttonReset.setOnClickListener(this);
		buttonNewTracker.setOnClickListener(this);
		sharedPreferences=this.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE); 
		mode = sharedPreferences.getBoolean(Preferences.ISINSERT, false);
		newTracker = sharedPreferences.getBoolean(Preferences.NEWTRACKER, false);
		if(!mode)
		{
			textViewHeader.setText("Edit Vehicle");
			buttonSuccess.setText("Edit");
		}
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mode = sharedPreferences.getBoolean(Preferences.ISINSERT, false);
		newTracker = sharedPreferences.getBoolean(Preferences.NEWTRACKER, false);
		if(!mode || newTracker)//if its an edit
		{
			if(!mode) // coming for editing
			{
				Intent intent=getIntent();
				registrationNumber=intent.getStringExtra("objectID");
				vehicle=(Vehicle)dataLayer.getObjectDetails(registrationNumber,"vehicle");
				editor=sharedPreferences.edit();
				editor.putBoolean(Preferences.ISINSERT, true);
				editor.commit();
			}
			else
			{
				String json = sharedPreferences.getString(Preferences.CURRENTOBJECT, null);
				gson=new Gson();
				vehicle = gson.fromJson(json, Vehicle.class);
				vehicle.setTrackerIMEI(Integer.parseInt(sharedPreferences.getString(Preferences.NEWTRACKERID, "-1")));
				editor=sharedPreferences.edit();
				editor.putBoolean(Preferences.NEWTRACKER, false);
				editor.commit();
			}
			editTextRegistrationNumber.setText(vehicle.getRegisterationNumber());
			editTextColor.setText(String.valueOf(vehicle.getColor()));
			editTextCost.setText(String.valueOf(vehicle.getCost()));
			editTextDescription.setText(vehicle.getDescription());
			editTextDriver.setText(vehicle.getDriver());
			fillSpinners();
			setSpinnerSelectedValues();
			spinnerTracker.setSelection(trackers.getCount()-1); // select new tracker
		}
		else//if create new
		{
			reset();
		}
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		editor=sharedPreferences.edit();
		editor.putBoolean(Preferences.ISINSERT, true);
		editor.putBoolean(Preferences.NEWTRACKER, false);
		editor.commit();
		super.onDestroy();
	}



	private void setSpinnerSelectedValues() {
		// TODO Auto-generated method stub
		//set tracker
		spinnerTracker.setSelection(trackers.getPosition(String.valueOf(vehicle.getTrackerIMEI())));
        //set category
		spinnerCategory.setSelection(categories.getPosition(vehicle.getCategory()));
	}



	private void fillSpinners() {
		// TODO Auto-generated method stub
		//set tracker
		String company=sharedPreferences.getString(Preferences.COMPANY, "");
		asyncRate = new GetResponse(spinnerTracker,this,null,MethodsList.getTrackerIMEI.ordinal(),company,-1,null);
        asyncRate.execute(); 
        
        //set category
		asyncRate = new GetResponse(spinnerCategory,this,null,MethodsList.getCategory.ordinal(),company,-1,null);
        asyncRate.execute(); 

	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonCreateVehicle:
			if(mode)
			{
				if(valid())
				{
					vehicle=new Vehicle();
					vehicle.setRegisterationNumber(editTextRegistrationNumber.getText().toString());
					vehicle.setProfile("vehicle");
					vehicle.setTrackerIMEI(Integer.parseInt(spinnerTracker.getSelectedItem().toString()));
					vehicle.setCost(Double.parseDouble(editTextCost.getText().toString()));
					vehicle.setColor(editTextColor.getText().toString());
					vehicle.setCategory(spinnerCategory.getSelectedItem().toString());
					vehicle.setDescription(editTextDescription.getText().toString());
					vehicle.setDriver(editTextDriver.getText().toString());
					if(Boolean.parseBoolean(dataLayer.insertVehicle(vehicle))){
					finish();
					}
					else{
						Messages.showError("ETSP", "Insert failed!", this);
					}
				}
			}
			else
			{
				if(valid())
				{
					Vehicle vehicle1=new Vehicle();
					vehicle1.setObjectID(vehicle.getObjectID());
					vehicle1.setRegisterationNumber(editTextRegistrationNumber.getText().toString());
					vehicle1.setProfile("vehicle");
					vehicle1.setTrackerIMEI(Integer.parseInt(spinnerTracker.getSelectedItem().toString()));
					vehicle1.setCost(Double.parseDouble(editTextCost.getText().toString()));
					vehicle1.setColor(editTextColor.getText().toString());
					vehicle1.setCategory(spinnerCategory.getSelectedItem().toString());
					vehicle1.setDescription(editTextDescription.getText().toString());
					vehicle1.setDriver(editTextDriver.getText().toString());
					if(Boolean.parseBoolean(dataLayer.updateVehicle(vehicle))){
					finish();
					}
					else{
						Messages.showError("ETSP", "Update failed!", this);
					}
				}
			}
			break;
		case R.id.buttonResetVehicle:
			reset();
			break;
		case R.id.buttonNewTrackerVehicle:
			//save current data
			Vehicle vehicle1=new Vehicle();
			vehicle1.setRegisterationNumber(editTextRegistrationNumber.getText().toString());
			vehicle1.setProfile("vehicle");
			vehicle1.setTrackerIMEI(Integer.parseInt(spinnerTracker.getSelectedItem().toString()));
			vehicle1.setCost(Double.parseDouble(editTextCost.getText().toString()));
			vehicle1.setColor(editTextColor.getText().toString());
			vehicle1.setCategory(spinnerCategory.getSelectedItem().toString());
			vehicle1.setDescription(editTextDescription.getText().toString());
			vehicle1.setDriver(editTextDriver.getText().toString());
			editor=sharedPreferences.edit();
			gson = new Gson();
			editor.putString(Preferences.CURRENTOBJECT, gson.toJson(vehicle1));
			editor.putBoolean(Preferences.NEWTRACKER, true);
			editor.commit();
			//Go to new tracker interface
			Intent clicked = new Intent("com.sdg.etspmobileuser.NEWTRACKER");
			startActivity(clicked);
			break;
		default:
			break;
		}
	}
	
	private void reset() {
		// TODO Auto-generated method stub
		editTextColor.setText("");
		editTextCost.setText(String.valueOf("0"));
		editTextDescription.setText("");
		editTextDriver.setText("");
		editTextRegistrationNumber.setText("");
		fillSpinners();
		spinnerCategory.setSelection(0);
		spinnerTracker.setSelection(0);
	}
	
	
	private boolean valid() {
		// TODO Auto-generated method stub
		boolean valid=true;
		if(editTextColor.getText().toString().equals("") || editTextCost.getText().toString().equals("") || editTextDriver.getText().toString().equals("") || editTextDescription.getText().toString().equals("") || editTextRegistrationNumber.getText().toString().equals(""))
		{
			Messages.showError("ETSP", "Please Fill All Fields!", this);
			valid=false;
		}
		return valid;
	}



	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		((TextView) parent.getChildAt(0)).setBackgroundColor(Color.WHITE);
	}



	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
