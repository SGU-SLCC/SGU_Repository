package com.sdg.etspmobileuser;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.sdg.models.CheckListData;
import com.sdg.models.GeoFence;
import com.sdg.models.Object;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.Messages;
import com.sdg.util.MethodsList;
import com.sdg.util.MyCustomAdapterChecked;
import com.sdg.util.Preferences;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AutoCompleteTextView.Validator;
import android.widget.RadioButton;

public class AddGeoFence extends Dialog implements android.view.View.OnClickListener, OnItemSelectedListener{

	private ListView listViewObjects;
	private Button buttonCreate;
	private Button buttonReset;
	private RadioButton radioButtonInside;
	private RadioButton radioButtonOutside;
	private ArrayList<CheckListData> objectsList;
	private DataLayer dataLayer;
	private SharedPreferences sharedPreferences=null;
	private ArrayList<String> selectedObjects=new ArrayList<String>();
	private Editor editor=null;
	private Gson gson;
	private Context context;
	private Activity activity;
	private GetResponse asyncRate;
	
	public AddGeoFence(Context context,Activity activity) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.activity=activity;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_geofence);
		sharedPreferences= context.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE);
		listViewObjects=(ListView)findViewById(R.id.listViewObjects);
		buttonCreate=(Button)findViewById(R.id.buttonCreateGeofence);
		buttonReset=(Button)findViewById(R.id.buttonResetGeofence);
		radioButtonInside=(RadioButton)findViewById(R.id.radioButtonIn);
		radioButtonOutside=(RadioButton)findViewById(R.id.radioButtonOut);
		buttonCreate.setOnClickListener(this);
		buttonReset.setOnClickListener(this);
		fillObjects();
	}
	
	private void fillObjects() {
		// TODO Auto-generated method stub
		dataLayer=new DataLayer();
		String company=sharedPreferences.getString(Preferences.COMPANY, "");
		asyncRate = new GetResponse(listViewObjects,activity,null,MethodsList.getObjectsList.ordinal(),company,null,-1);
        asyncRate.execute(); 
		MyCustomAdapterChecked dataAdapter = new MyCustomAdapterChecked(context,R.layout.sensor_info, objectsList,selectedObjects);		
		listViewObjects.setAdapter(dataAdapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.buttonCreateGeofence:
			if(valid()){
				GeoFence  geofence=new GeoFence();
				geofence.setInside(radioButtonInside.isChecked());
				geofence.setObjects(selectedObjects);
				editor=sharedPreferences.edit();
				gson = new Gson();
				editor.putString(Preferences.CURRENTGEOFENCE, gson.toJson(geofence));
				editor.putBoolean(Preferences.DRAWGEOFENCE, true);
				editor.commit();
				((MainTrackerInterface)context).getActionBar().hide();
				((TrackObjects)((MainTrackerInterface)context).currentstatus).buttonAddGeofence.setEnabled(false);
				Messages.showError("Instructions", "1.Go to the desired location. \n2.Press and hold to start drawing. \n3.Draw the geo-fence. \n4.Save.", context);
				this.cancel();
			}
			break;
		case R.id.buttonResetGeofence:
			reset();
			break;
		default:break;
		}
	}

	private boolean valid() {
		// TODO Auto-generated method stub
		boolean result=true;
		if(selectedObjects.size()==0 || (!radioButtonInside.isChecked() && !radioButtonOutside.isChecked()))
		{
			Messages.showError("ETSP", "Please Fill All Fields!", getContext());
			result=false;
		}
		return result;
	}

	private void reset() {
		// TODO Auto-generated method stub
		radioButtonInside.setChecked(false);
		radioButtonOutside.setChecked(false);
		selectedObjects.clear();
		fillObjects();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long Id) {
		// TODO Auto-generated method stub
		((TextView) parent.getChildAt(0)).setBackgroundColor(Color.WHITE);
		if(((TextView) parent.getChildAt(position)).getText().equals("Custom shape")){
			
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
