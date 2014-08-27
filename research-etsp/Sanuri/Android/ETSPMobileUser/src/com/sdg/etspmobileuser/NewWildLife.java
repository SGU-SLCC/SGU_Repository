package com.sdg.etspmobileuser;

import com.google.gson.Gson;
import com.sdg.models.WildLife;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.Messages;
import com.sdg.util.MethodsList;
import com.sdg.util.Preferences;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class NewWildLife extends Activity implements OnClickListener,OnItemSelectedListener{

	private SharedPreferences sharedPreferences=null;
	private Button buttonSuccess;
	private Button buttonReset;
	private Button buttonAddTracker;
	private DataLayer dataLayer;
	private Boolean mode;
	private Boolean newTracker;
	private TextView textViewHeader;
	private Spinner spinnerTracker;
	private EditText editTextName;
	private Spinner spinnerCategory;
	private EditText editTextAge;
	private EditText editTextSpecificSigns;
	private EditText editTextDescription;
	private String objectID;
	private WildLife wildLife;
	private String[] trackerIMEIList;
	private String[] categoryList;
	private ArrayAdapter<String> trackers;
	private ArrayAdapter<String> categories;
	private Editor editor=null;
	private Gson gson;
	private GetResponse asyncRate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_wildlife);
		dataLayer=new DataLayer();
		textViewHeader=(TextView)findViewById(R.id.textViewAnimalHeader);
		spinnerTracker=(Spinner)findViewById(R.id.spinnerWildLifeTracker);
		spinnerTracker.setOnItemSelectedListener(this);
		editTextName=(EditText)findViewById(R.id.editTextAnimalName);
		spinnerCategory=(Spinner)findViewById(R.id.spinnerWildLifeCategory);
		spinnerCategory.setOnItemSelectedListener(this);
		editTextAge=(EditText)findViewById(R.id.editTextWildLifeAge);
		editTextSpecificSigns=(EditText)findViewById(R.id.editTextWildLifeSpecificSigns);
		editTextDescription=(EditText)findViewById(R.id.editTextWildLifeDescription);
		buttonSuccess=(Button)findViewById(R.id.buttonCreateAnimal);
		buttonReset=(Button)findViewById(R.id.buttonResetAnimal);
		buttonAddTracker=(Button)findViewById(R.id.buttonNewWildLifeTracker);
		buttonSuccess.setOnClickListener(this);
		buttonReset.setOnClickListener(this);
		buttonAddTracker.setOnClickListener(this);
		sharedPreferences=this.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE); 
		mode = sharedPreferences.getBoolean(Preferences.ISINSERT, false);
		newTracker = sharedPreferences.getBoolean(Preferences.NEWTRACKER, false);
		if(!mode)
		{
			textViewHeader.setText("Edit Animal");
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
				wildLife=(WildLife)dataLayer.getObjectDetails(objectID,"animal");
				editor=sharedPreferences.edit();
				editor.putBoolean(Preferences.ISINSERT, true);
				editor.commit();
			}
			else// coming from new tracker page
			{
				String json = sharedPreferences.getString(Preferences.CURRENTOBJECT, null);
				gson=new Gson();
				wildLife = gson.fromJson(json, WildLife.class);
				wildLife.setTrackerIMEI(Integer.parseInt(sharedPreferences.getString(Preferences.NEWTRACKERID, "-1")));
				editor=sharedPreferences.edit();
				editor.putBoolean(Preferences.NEWTRACKER, false);
				editor.commit();
			}
			editTextName.setText(wildLife.getObjectName());
			editTextAge.setText(String.valueOf(wildLife.getAge()));
			editTextSpecificSigns.setText(wildLife.getSpecificSigns());
			editTextDescription.setText(wildLife.getDescription());
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
		spinnerTracker.setSelection(trackers.getPosition(String.valueOf(wildLife.getTrackerIMEI())));
        //set category
		spinnerCategory.setSelection(categories.getPosition(wildLife.getCategory()));
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
		case R.id.buttonCreateAnimal:
			if(mode)
			{
				if(valid())
				{
					wildLife=new WildLife();
					wildLife.setObjectName(editTextName.getText().toString());
					wildLife.setProfile("animal");
					wildLife.setTrackerIMEI(Integer.parseInt(spinnerTracker.getSelectedItem().toString()));
					wildLife.setAge(Double.parseDouble(editTextAge.getText().toString()));
					wildLife.setSpecificSigns(editTextSpecificSigns.getText().toString());
					wildLife.setCategory(spinnerCategory.getSelectedItem().toString());
					wildLife.setDescription(editTextDescription.getText().toString());
					if(Boolean.parseBoolean(dataLayer.insertWildLife(wildLife))){
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
					wildLife=new WildLife();
					wildLife.setObjectID(objectID);
					wildLife.setObjectName(editTextName.getText().toString());
					wildLife.setProfile("animal");
					wildLife.setTrackerIMEI(Integer.parseInt(spinnerTracker.getSelectedItem().toString()));
					wildLife.setAge(Double.parseDouble(editTextAge.getText().toString()));
					wildLife.setSpecificSigns(editTextSpecificSigns.getText().toString());
					wildLife.setCategory(spinnerCategory.getSelectedItem().toString());
					wildLife.setDescription(editTextDescription.getText().toString());
					if(Boolean.parseBoolean(dataLayer.updateWildLife(wildLife))){
					finish();
					}
					else{
						Messages.showError("ETSP", "Update failed!", this);
					}
				}
			}
			break;
		case R.id.buttonResetAnimal:
			reset();
			break;
		case R.id.buttonNewWildLifeTracker:
			//save current data
			wildLife=new WildLife();
			wildLife.setObjectName(editTextName.getText().toString());
			wildLife.setProfile("animal");
			wildLife.setTrackerIMEI(Integer.parseInt(spinnerTracker.getSelectedItem().toString()));
			wildLife.setAge(Double.parseDouble(editTextAge.getText().toString()));
			wildLife.setSpecificSigns(editTextSpecificSigns.getText().toString());
			wildLife.setCategory(spinnerCategory.getSelectedItem().toString());
			wildLife.setDescription(editTextDescription.getText().toString());
			editor=sharedPreferences.edit();
			gson = new Gson();
			editor.putString(Preferences.CURRENTOBJECT, gson.toJson(wildLife));
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
		editTextName.setText("");
		editTextAge.setText(String.valueOf("0"));
		editTextSpecificSigns.setText("");
		editTextDescription.setText("");
		fillSpinners();
		spinnerCategory.setSelection(0);
		spinnerTracker.setSelection(0);
	}
	
	
	private boolean valid() {
		// TODO Auto-generated method stub
		boolean valid=true;
		if(editTextName.getText().toString().equals("") || editTextAge.getText().toString().equals("") || editTextSpecificSigns.getText().toString().equals("") || editTextDescription.getText().toString().equals(""))
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
