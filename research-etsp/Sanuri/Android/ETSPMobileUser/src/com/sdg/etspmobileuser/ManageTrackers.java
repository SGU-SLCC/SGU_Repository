package com.sdg.etspmobileuser;

import com.sdg.util.GetResponse;
import com.sdg.util.MethodsList;
import com.sdg.util.Preferences;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Button;

public class ManageTrackers extends Activity implements OnClickListener{//,OnItemClickListener{
	
	private Button buttonCreateNew;
	private Button buttonRegistrationRequests;
	private Button buttonConfigure;
	private Intent clicked;
	private ListView listViewTracker;
	private Editor editor=null;
	private SharedPreferences sharedPreferences;
	private GetResponse asyncRate;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_trackers);
		sharedPreferences= getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE);
		buttonCreateNew=(Button)findViewById(R.id.buttonAddNewTracker);
		buttonRegistrationRequests=(Button)findViewById(R.id.buttonRegistrationRequests);
		buttonConfigure=(Button)findViewById(R.id.buttonConfigureTrackers);
		buttonCreateNew.setOnClickListener(this);
		buttonRegistrationRequests.setOnClickListener(this);
		buttonConfigure.setOnClickListener(this);
		listViewTracker=(ListView)findViewById(R.id.listViewManageTrackers);
		//listViewTracker.setOnItemClickListener(this);
	}

	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fillTrackers();
		editor=sharedPreferences.edit();
		editor.putBoolean(Preferences.NEWTRACKER, false);
		editor.commit();
	}



	public void fillTrackers() {
		// TODO Auto-generated method stub
		asyncRate = new GetResponse(listViewTracker,this,null,MethodsList.getTrackers.ordinal(),null,MethodsList.tracker.ordinal(),null);
        asyncRate.execute(); 
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonAddNewTracker:
			editor=sharedPreferences.edit();
			editor.putBoolean(Preferences.ISINSERT, true);
			editor.commit();
			clicked = new Intent("com.sdg.etspmobileuser.NEWTRACKER");
			startActivity(clicked);
			break;
		case R.id.buttonRegistrationRequests:
			clicked = new Intent("com.sdg.etspmobileuser.REGISTRATIONREQUESTS");
			startActivity(clicked);
			break;
		case R.id.buttonConfigureTrackers:
			clicked = new Intent("com.sdg.etspmobileuser.CONFIGURETRACKERS");
			startActivity(clicked);
			break;
		default:
			break;
		}	
	}


/*
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if(view.getId()==R.id.imageViewDelete) // if delete is pressed
		{
			final int itemPosition=position;
			new AlertDialog.Builder(this)
			.setTitle("ETSP")
			.setMessage("Delete Item Permenantly?")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			    public void onClick(DialogInterface dialog, int whichButton) {
			    	handleDeletion = new HandleDeletion(getParent(), listViewTracker.getItemAtPosition(itemPosition).toString(), "tracker");
			    	handleDeletion.execute(); 
			    	fillTrackers();
			    }})
			 .setNegativeButton(android.R.string.no, null).show();
		}
		else // if item pressed
		{
			Object item=listViewTracker.getItemAtPosition(position);
			editor=sharedPreferences.edit();
			editor.putBoolean(Preferences.ISINSERT, false);
			editor.putString(Preferences.TRACKERID, ((CheckListData)item).getName());
			editor.commit();
			Intent trackerEdit=new Intent("com.sdg.etspmobileuser.NEWTRACKER");
			startActivity(trackerEdit);
		}
	}*/

}
