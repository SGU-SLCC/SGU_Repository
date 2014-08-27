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
import com.sdg.models.Person;
import com.sdg.models.WildLife;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.Messages;
import com.sdg.util.MethodsList;
import com.sdg.util.Preferences;

public class NewPerson  extends Activity  implements OnClickListener,OnItemSelectedListener{

	private SharedPreferences sharedPreferences=null;
	private Button buttonSuccess;
	private Button buttonReset;
	private Button buttonNewTracker;
	private DataLayer dataLayer;
	private Boolean mode;
	private Boolean newTracker;
	private TextView textViewHeader;
	private Spinner spinnerTracker;
	private EditText editTextName;
	private Spinner spinnerGender;
	private EditText editTextNIC;
	private EditText editTextLocation;
	private EditText editTextAge;
	private EditText editTextRemarks;
	private String personID;
	private Person person;
	private String[] trackerIMEIList;
	private String[] categoryList={"Male","Female"};
	private ArrayAdapter<String> trackers;
	private ArrayAdapter<String> genders;
	private Editor editor=null;
	private Gson gson;
	private GetResponse asyncRate;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_person);
		dataLayer=new DataLayer();
		textViewHeader=(TextView)findViewById(R.id.textViewPersonHeader);
		spinnerTracker=(Spinner)findViewById(R.id.spinnerVehicleTracker);
		spinnerTracker.setOnItemSelectedListener(this);
		editTextName=(EditText)findViewById(R.id.editTextPersonName);
		spinnerGender=(Spinner)findViewById(R.id.spinnerPersonGender);
		spinnerGender.setOnItemSelectedListener(this);
		editTextAge=(EditText)findViewById(R.id.editTextPersonAge);
		editTextLocation=(EditText)findViewById(R.id.editTextPersonLocation);
		editTextRemarks=(EditText)findViewById(R.id.editTextPersonRemarks);
		buttonSuccess=(Button)findViewById(R.id.buttonCreatePerson);
		buttonReset=(Button)findViewById(R.id.buttonResetPerson);
		buttonNewTracker=(Button)findViewById(R.id.buttonNewTrackerPerson);
		editTextNIC=(EditText)findViewById(R.id.editTextPersonNIC);
		buttonSuccess.setOnClickListener(this);
		buttonReset.setOnClickListener(this);
		buttonNewTracker.setOnClickListener(this);
		sharedPreferences=this.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE); 
		mode = sharedPreferences.getBoolean(Preferences.ISINSERT, false);
		newTracker = sharedPreferences.getBoolean(Preferences.NEWTRACKER, false);
		if(!mode)
		{
			textViewHeader.setText("Edit Person");
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
			if(!mode)
			{
				Intent intent=getIntent();
				personID=intent.getStringExtra("objectID");
				person=(Person)dataLayer.getObjectDetails(personID,"person");
				editor=sharedPreferences.edit();
				editor.putBoolean(Preferences.ISINSERT, true);
				editor.commit();
			}
			else
			{
				String json = sharedPreferences.getString(Preferences.CURRENTOBJECT, null);
				gson=new Gson();
				person = gson.fromJson(json, Person.class);
				person.setTrackerIMEI(Integer.parseInt(sharedPreferences.getString(Preferences.NEWTRACKERID, "-1")));
				editor=sharedPreferences.edit();
				editor.putBoolean(Preferences.NEWTRACKER, false);
				editor.commit();
			}
			editTextAge.setText(String.valueOf(person.getAge()));
			editTextLocation.setText(person.getLocation());
			editTextName.setText(person.getObjectName());
			editTextNIC.setText(person.getNIC());
			editTextRemarks.setText(person.getDescription());
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
		spinnerTracker.setSelection(trackers.getPosition(String.valueOf(person.getTrackerIMEI())));
        //set category
		spinnerGender.setSelection(genders.getPosition(person.getGender()));
	}



	private void fillSpinners() {
		// TODO Auto-generated method stub
		//set tracker
		String company=sharedPreferences.getString(Preferences.COMPANY, "");
		asyncRate = new GetResponse(spinnerTracker,this,null,MethodsList.getTrackerIMEI.ordinal(),company,-1,null);
        asyncRate.execute(); 
        
        //set category
        genders = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, categoryList);      
        spinnerGender.setAdapter(genders);
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonCreatePerson:
			if(mode)
			{
				if(valid())
				{
					person=new Person();
					person.setObjectName(editTextName.getText().toString());
					person.setProfile("person");
					person.setAge(Integer.parseInt(editTextAge.getText().toString()));
					person.setNIC(editTextNIC.getText().toString());
					person.setLocation(editTextLocation.getText().toString());
					person.setDescription(editTextRemarks.getText().toString());
					person.setGender(spinnerGender.getSelectedItem().toString());
					person.setTrackerIMEI(Integer.parseInt(spinnerTracker.getSelectedItem().toString()));
					if(Boolean.parseBoolean(dataLayer.insertPerson(person))){
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
					person=new Person();
					person.setObjectID(personID);
					person.setObjectName(editTextName.getText().toString());
					person.setNIC(editTextNIC.getText().toString());
					person.setProfile("person");
					person.setAge(Integer.parseInt(editTextAge.getText().toString()));
					person.setLocation(editTextLocation.getText().toString());
					person.setDescription(editTextRemarks.getText().toString());
					person.setGender(spinnerGender.getSelectedItem().toString());
					person.setTrackerIMEI(Integer.parseInt(spinnerTracker.getSelectedItem().toString()));
					if(Boolean.parseBoolean(dataLayer.updatePerson(person))){
					finish();
					}
					else{
						Messages.showError("ETSP", "Update failed!", this);
					}
				}
			}
			break;
		case R.id.buttonResetPerson:
			reset();
			break;
		case R.id.buttonNewTrackerPerson:
			person=new Person();
			person.setObjectName(editTextName.getText().toString());
			person.setProfile("person");
			person.setNIC(editTextNIC.getText().toString());
			person.setAge(Integer.parseInt(editTextAge.getText().toString()));
			person.setLocation(editTextLocation.getText().toString());
			person.setDescription(editTextRemarks.getText().toString());
			person.setGender(spinnerGender.getSelectedItem().toString());
			person.setTrackerIMEI(Integer.parseInt(spinnerTracker.getSelectedItem().toString()));
			dataLayer.insertPerson(person);
			editor=sharedPreferences.edit();
			gson = new Gson();
			editor.putString(Preferences.CURRENTOBJECT, gson.toJson(person));
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
		editTextAge.setText("0");
		editTextLocation.setText("");
		editTextName.setText("");
		editTextNIC.setText("");
		editTextRemarks.setText("");
		fillSpinners();
		spinnerGender.setSelection(0);
		spinnerTracker.setSelection(0);
	}
	
	
	private boolean valid() {
		// TODO Auto-generated method stub
		boolean valid=true;
		if(editTextAge.getText().toString().equals("") || editTextLocation.getText().toString().equals("") || editTextName.getText().toString().equals("") || editTextNIC.getText().toString().equals("") || editTextRemarks.getText().toString().equals(""))
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
