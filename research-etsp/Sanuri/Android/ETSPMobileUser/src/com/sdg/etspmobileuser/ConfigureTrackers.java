package com.sdg.etspmobileuser;

import java.util.concurrent.ExecutionException;

import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.Messages;
import com.sdg.util.MethodsList;
import com.sdg.util.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ConfigureTrackers extends Activity implements OnClickListener,OnItemSelectedListener{

	private Spinner spinnerTracker;
	private AutoCompleteTextView multiTextViewCommand;
	private Button buttonSend;
	private Button buttonReset;
	private DataLayer dataLayer;
	private String[] trackerIMEIList;
	private ArrayAdapter<String> trackers;
	private GetResponse asyncRate;
	private SharedPreferences sharedPreferences=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_trackers);
		sharedPreferences=this.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE); 
		spinnerTracker=(Spinner)findViewById(R.id.spinnerTrackersConfig);
		spinnerTracker.setOnItemSelectedListener(this);
		multiTextViewCommand=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextViewCommand);
		buttonSend=(Button)findViewById(R.id.buttonSendCommand);
		buttonReset=(Button)findViewById(R.id.buttonResetTrackerConfig);
		buttonSend.setOnClickListener(this);
		buttonReset.setOnClickListener(this);
		dataLayer=new DataLayer();
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fillTrackers();
		if(spinnerTracker.getCount()!=0)
			prepareAutoFill();
	}

	private void prepareAutoFill() {
		// TODO Auto-generated method stub
		asyncRate = new GetResponse(multiTextViewCommand,this,null,MethodsList.getCommands.ordinal(),spinnerTracker.getSelectedItem().toString(),null,-1);
        asyncRate.execute(); 
	}



	private void fillTrackers() {
		// TODO Auto-generated method stub
		String company=sharedPreferences.getString(Preferences.COMPANY, "");
		asyncRate = new GetResponse(spinnerTracker,this,null,MethodsList.getTrackerIMEI.ordinal(),company,-1,null);
        asyncRate.execute(); 
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.buttonSendCommand:
			if(valid())
			{
				asyncRate = new GetResponse(spinnerTracker.getSelectedItem().toString(),multiTextViewCommand.getText().toString(),MethodsList.sendConfigurationRequest.ordinal());
		        String result;
				try {
					result = asyncRate.execute().get();
					if(Boolean.parseBoolean(result))
					{
						Messages.showError("ETSP", "Tracker Successfully Configured!", this);
						reset();
					}
					else
					{
						Messages.showError("ETSP", "Configuration Request Failed!", this);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case R.id.buttonResetTrackerConfig:
			reset();
			break;
		default:
			break;
		}
	}



	private void reset() {
		// TODO Auto-generated method stub
		multiTextViewCommand.setText("");
		prepareAutoFill();
		fillTrackers();
		spinnerTracker.setSelection(0);
	}



	private boolean valid() {
		// TODO Auto-generated method stub
		boolean valid=true;
		if(multiTextViewCommand.getText().toString().equals(""))
		{
			Messages.showError("ETSP", "Please Fill the Command!", this);
			valid=false;
		}
		return valid;
	}



	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,	long id) {
		// TODO Auto-generated method stub
		((TextView) parent.getChildAt(0)).setBackgroundColor(Color.WHITE);
	}



	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
