package com.sdg.etspmobileuser;

import java.util.ArrayList;

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
import com.sdg.models.Other;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.Messages;
import com.sdg.util.MethodsList;
import com.sdg.util.Preferences;

public class NewOther extends Activity implements OnClickListener,OnItemSelectedListener{

	private SharedPreferences sharedPreferences=null;
	private Button buttonSuccess;
	private Button buttonReset;
	private Button buttonAddTracker;
	private DataLayer dataLayer;
	private Boolean mode;
	private Boolean newTracker;
	private TextView textViewHeader;
	private Spinner spinnerTracker;
	private EditText editTextProfileName;
	private EditText editTextObjectName;
	private EditText editTextAge;
	private EditText editTextSpecificSigns;
	private EditText editTextDescription;
	private String objectID;
	private Other other;
	private String[] trackerIMEIList;
	private ArrayAdapter<String> trackers;
	private Editor editor=null;
	private Gson gson;
	private GetResponse asyncRate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_other);
		dataLayer=new DataLayer();
		textViewHeader=(TextView)findViewById(R.id.textViewOtherHeader);
		spinnerTracker=(Spinner)findViewById(R.id.spinnerOtherTracker);
		spinnerTracker.setOnItemSelectedListener(this);
		editTextObjectName=(EditText)findViewById(R.id.editTextOtherObjectName);
		editTextProfileName = (EditText)findViewById(R.id.editTextOtherProfileName);
		editTextAge=(EditText)findViewById(R.id.editTextOtherAge);
		editTextSpecificSigns=(EditText)findViewById(R.id.editTextOtherSpecificSigns);
		editTextDescription=(EditText)findViewById(R.id.editTextOtherDescription);
		buttonSuccess=(Button)findViewById(R.id.buttonCreateOther);
		buttonReset=(Button)findViewById(R.id.buttonResetOther);
		buttonAddTracker=(Button)findViewById(R.id.buttonNewOtherTracker);
		buttonSuccess.setOnClickListener(this);
		buttonReset.setOnClickListener(this);
		buttonAddTracker.setOnClickListener(this);
		sharedPreferences=this.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE); 
		mode = sharedPreferences.getBoolean(Preferences.ISINSERT, false);
		newTracker = sharedPreferences.getBoolean(Preferences.NEWTRACKER, false);
		if(!mode)
		{
			textViewHeader.setText("Edit Other");
			buttonSuccess.setText("Edit");
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mode = sharedPreferences.getBoolean(Preferences.ISINSERT, false);
		newTracker = sharedPreferences.getBoolean(Preferences.NEWTRACKER, false);
		if(!mode || newTracker)//if its an edit or coming from new tracker page
		{
			if(!mode) // coming for editing
			{
				Intent intent=getIntent();
				objectID=intent.getStringExtra("objectID");
				other=(Other)dataLayer.getObjectDetails(objectID,"other");
				editor=sharedPreferences.edit();
				editor.putBoolean(Preferences.ISINSERT, true);
				editor.commit();
			}
			else// coming from new tracker page
			{
				String json = sharedPreferences.getString(Preferences.CURRENTOBJECT, null);
				gson=new Gson();
				other = gson.fromJson(json, Other.class);
				other.setTrackerIMEI(Integer.parseInt(sharedPreferences.getString(Preferences.NEWTRACKERID, "-1")));
				editor=sharedPreferences.edit();
				editor.putBoolean(Preferences.NEWTRACKER, false);
				editor.commit();
			}
			editTextObjectName.setText(other.getObjectName());
			editTextProfileName.setText(other.getProfile());
			editTextAge.setText(String.valueOf(other.getAge()));
			editTextSpecificSigns.setText(other.getSpecificSigns());
			editTextDescription.setText(other.getDescription());
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
		spinnerTracker.setSelection(trackers.getPosition(String.valueOf(other.getTrackerIMEI())));
	}



	private void fillSpinners() {
		// TODO Auto-generated method stub
		//set tracker
		String company=sharedPreferences.getString(Preferences.COMPANY, "");
		asyncRate = new GetResponse(spinnerTracker,this,null,MethodsList.getTrackerIMEI.ordinal(),company,-1,null);
        asyncRate.execute(); 
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonCreateOther:
			if(mode)
			{
				if(valid())
				{
					other=new Other();
					other.setObjectName(editTextObjectName.getText().toString());
					other.setProfile(editTextProfileName.getText().toString());
					other.setTrackerIMEI(Integer.parseInt(spinnerTracker.getSelectedItem().toString()));
					other.setAge(Double.parseDouble(editTextAge.getText().toString()));
					other.setSpecificSigns(editTextSpecificSigns.getText().toString());
					other.setDescription(editTextDescription.getText().toString());
					if(Boolean.parseBoolean(dataLayer.insertOther(other))){
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
					other=new Other();
					other.setObjectID(objectID);
					other.setObjectName(editTextObjectName.getText().toString());
					other.setProfile(editTextProfileName.getText().toString());
					other.setTrackerIMEI(Integer.parseInt(spinnerTracker.getSelectedItem().toString()));
					other.setAge(Double.parseDouble(editTextAge.getText().toString()));
					other.setSpecificSigns(editTextSpecificSigns.getText().toString());
					other.setDescription(editTextDescription.getText().toString());
					if(Boolean.parseBoolean(dataLayer.updateOther(other))){
					finish();
					}
					else{
						Messages.showError("ETSP", "Update failed!", this);
					}
				}
			}
			break;
		case R.id.buttonResetOther:
			reset();
			break;
		case R.id.buttonNewOtherTracker:
			//save current data
			other=new Other();
			other.setObjectName(editTextObjectName.getText().toString());
			other.setProfile(editTextProfileName.getText().toString());
			other.setTrackerIMEI(Integer.parseInt(spinnerTracker.getSelectedItem().toString()));
			other.setAge(Double.parseDouble(editTextAge.getText().toString()));
			other.setSpecificSigns(editTextSpecificSigns.getText().toString());
			other.setDescription(editTextDescription.getText().toString());
			editor=sharedPreferences.edit();
			gson = new Gson();
			editor.putString(Preferences.CURRENTOBJECT, gson.toJson(other));
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
		editTextObjectName.setText("");
		editTextProfileName.setText("");
		editTextAge.setText(String.valueOf("0"));
		editTextSpecificSigns.setText("");
		editTextDescription.setText("");
		fillSpinners();
		spinnerTracker.setSelection(0);
	}
	
	
	private boolean valid() {
		// TODO Auto-generated method stub
		boolean valid=true;
		if(editTextObjectName.getText().toString().equals("") || editTextProfileName.getText().toString().equals("") || editTextAge.getText().toString().equals("") || editTextSpecificSigns.getText().toString().equals("") || editTextDescription.getText().toString().equals(""))
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
