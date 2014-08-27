package com.sdg.etspmobileuser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.sdg.models.Tracker;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.HandleInsertion;
import com.sdg.util.HandleUpdates;
import com.sdg.util.Messages;
import com.sdg.util.MethodsList;
import com.sdg.util.MyCustomAdapterChecked;
import com.sdg.util.Preferences;

public class NewTracker extends Activity implements OnClickListener,OnItemSelectedListener,OnFocusChangeListener{

	private EditText editTextImeiNumber;
	private EditText editTextContactNumber;
	private EditText editTextOther;
	private Spinner spinnerTrackerType; 
	private ListView listViewData; 
	private RadioButton radioButtonSMS;
	private RadioButton radioButtonGPRS;
	private Button buttonCreate;
	private Button buttonCancel;
	private SharedPreferences sharedPreferences=null;
	private Editor editor=null;
	private String selectedType;
	private ArrayList<String> selectedData=new ArrayList<String>();
	private Tracker tracker;
	private boolean trackerTypeMarked=false;
	private TextView textViewHeader;
	private Boolean mode=false;
	public ArrayAdapter<String> typesList;
	private static final Pattern telPattern = Pattern.compile("^\\+[0-9]{11}$");
	private Boolean telEntered=false;
	private GetResponse asyncRate;
	private String trackerID="";
	
	public NewTracker(){
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_tracker);
		sharedPreferences= getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE);
		textViewHeader=(TextView)findViewById(R.id.textViewTrackerHeader);
		editTextImeiNumber=(EditText)findViewById(R.id.editTextImeiNumber);
		editTextContactNumber=(EditText)findViewById(R.id.editTextContactNumber);
		editTextContactNumber.setOnFocusChangeListener(this);
		editTextOther=(EditText)findViewById(R.id.editTextOther);
		spinnerTrackerType=(Spinner)findViewById(R.id.spinnerTrackerType);
		listViewData=(ListView)findViewById(R.id.listViewData);
		radioButtonSMS=(RadioButton)findViewById(R.id.radioButtonGSM);
		radioButtonGPRS=(RadioButton)findViewById(R.id.radioButtonGPRS);
		buttonCreate=(Button)findViewById(R.id.buttonCreateNewTracker);
		buttonCancel=(Button)findViewById(R.id.buttonCancelNewTracker);
		buttonCreate.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
		spinnerTrackerType.setOnItemSelectedListener(this);
		selectedData=new ArrayList<String>();
		mode = sharedPreferences.getBoolean(Preferences.ISINSERT, false);
		if(!mode)
		{
			textViewHeader.setText("Edit Tracker");
			buttonCreate.setText("Edit");
			editTextImeiNumber.setEnabled(false);
		}
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mode = sharedPreferences.getBoolean(Preferences.ISINSERT, false);
		if(!mode)//edit
		{
			String ImeiNumber=sharedPreferences.getString(Preferences.TRACKERID, "");
			asyncRate = new GetResponse(null,this,null,MethodsList.getTrackerDetails.ordinal(),ImeiNumber,-1,null);
			String response;
			try {
				response = asyncRate.execute().get();

				JSONObject obj;
				try {
					obj = new JSONObject(response);
				    trackerID = obj.getString("_id");
				    tracker=new Tracker();
				    tracker.setId(trackerID);
				    tracker.setImeiNumber(obj.getString("imei"));
				    tracker.setMode(obj.getInt("mode"));
				    tracker.setTrackerContactNumber(obj.getString("contact"));
				    tracker.setTrackerType(obj.getString("type"));
				    String data = obj.getString("data");
				    String []array = data.subSequence(1, data.length()-2).toString().split(","); //remove [,] and split by comma.
				    ArrayList<String> dList=new ArrayList<String>();
				    for(int i=0;i<array.length; i++){
				        String datum = array[i].substring(1, array[i].length()-2); //remove """on either side of the id.  
				        dList.add(datum);
				    }
				    tracker.setData(dList);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			editTextImeiNumber.setText(tracker.getImeiNumber());
			editTextContactNumber.setText(tracker.getTrackerContactNumber());
			fillTrackerTypes();
			if(typesList.getPosition(tracker.getTrackerType())!=-1)
			{
				spinnerTrackerType.setSelection(typesList.getPosition(tracker.getTrackerType()));
				trackerTypeMarked=true;
			}
			if(!trackerTypeMarked)
			{
				editTextOther.setText(tracker.getTrackerType());
			}
			trackerTypeMarked=false;
			if(tracker.getMode()==0)
			{
				radioButtonSMS.setChecked(true);
			}
			else
			{
				radioButtonGPRS.setChecked(true);
			}		
			selectedData=tracker.getData();
			displayListView();
		}
		else//insert
		{
			reset();
		}
	}

	private void fillTrackerTypes() {
		// TODO Auto-generated method stub
		asyncRate = new GetResponse(spinnerTrackerType,this,null,MethodsList.getTrackerTypes.ordinal(),null,-1,null);
        try {
			String response=asyncRate.execute().get();
			try {
				JSONObject obj=new JSONObject(response);
			    JSONArray trackerTypes= obj.getJSONArray("rows");
			    String[] types=new String[trackerTypes.length()];	
			    for(int i=0;i<trackerTypes.length(); i++){
			        JSONObject jsonas = trackerTypes.getJSONObject(i);
			        String type = jsonas.getString("c0");
			        types[i]=type;
			    }
			    
		        ArrayAdapter<String> trackerTypesList= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, types);      
		        spinnerTrackerType.setAdapter(trackerTypesList);	
		        typesList=trackerTypesList;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.buttonCreateNewTracker:
			if(valid())
			{
				Tracker t=new Tracker();
				t.setImeiNumber(editTextImeiNumber.getText().toString());
				if(telEntered)
				{
					t.setTrackerContactNumber(editTextContactNumber.getText().toString());
				}
				else
				{
					t.setTrackerContactNumber("");
				}
				if(editTextOther.getText().toString().equalsIgnoreCase(""))
				{
					t.setTrackerType(selectedType);
				}
				else
				{
					t.setTrackerType(editTextOther.getText().toString());
				}
				
				if(radioButtonGPRS.isChecked())
				{
					t.setMode(1);
				}
				else
				{
					t.setMode(0);
				}
				
				t.setData(selectedData);
				
				if(mode)
				{
					HandleInsertion asyncRate = new HandleInsertion(this,t,MethodsList.tracker.ordinal());
					asyncRate.execute();
					editor=sharedPreferences.edit();
					editor.putBoolean(Preferences.NEWTRACKER, true);
					editor.putString(Preferences.NEWTRACKERID, t.getImeiNumber());
					editor.commit();
				}
				else
				{
					t.setId(trackerID);
					HandleUpdates asyncRate = new HandleUpdates(this,t,MethodsList.tracker.ordinal());
					asyncRate.execute(); 
				}
				finish();
			}
			break;
		case R.id.buttonCancelNewTracker:
			reset();
			break;
		default:break;
		}
	}

	private void reset() {
		// TODO Auto-generated method stub
		editTextImeiNumber.setText("");
		editTextOther.setText("");
		editTextContactNumber.setText("+94711111111");
		editTextContactNumber.setTextColor(Color.GRAY);
		spinnerTrackerType.setSelection(0);
		radioButtonGPRS.setChecked(false);
		radioButtonSMS.setChecked(false);
		fillTrackerTypes();
		selectedData=new ArrayList<String>();
		displayListView();
	}

	private boolean valid() {
		// TODO Auto-generated method stub
		boolean valid=true;
		if(editTextImeiNumber.getText().toString().equals("") || (!radioButtonGPRS.isChecked() && !radioButtonSMS.isChecked()) || selectedData.isEmpty())
		{
			Messages.showError("ETSP Tracker", "Please Fill All Fields!", this);
			valid=false;
		}
		else if(!editTextContactNumber.getText().toString().equals("") && !telPattern.matcher(editTextContactNumber.getText().toString()).matches())
		{
			Messages.showError("ETSP Tracker", "Invalid Contact Number!", this);
			valid=false;			
		}
		return valid;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		((TextView) parent.getChildAt(0)).setBackgroundColor(Color.WHITE);
		selectedType = parent.getItemAtPosition(position).toString();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
	}

	//Create and display check box list
	private void displayListView() {
		asyncRate = new GetResponse(listViewData,this,selectedData,MethodsList.getSensorDataTypes.ordinal(),null,-1,null);
        asyncRate.execute(); 
	}

	//implemet contact number place holder
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if(hasFocus)
		{
			if(editTextContactNumber.getText().toString().equalsIgnoreCase("+94711111111"))
			{
				editTextContactNumber.setText("");
				telEntered=false;
			}
			editTextContactNumber.setTextColor(Color.BLACK);
		}
		else
		{
			telEntered=true;
			if(editTextContactNumber.getText().toString().equalsIgnoreCase(""))
			{
				editTextContactNumber.setText("+94711111111");
				editTextContactNumber.setTextColor(Color.GRAY);
				telEntered=false;
			}
		}
	}
	
}
