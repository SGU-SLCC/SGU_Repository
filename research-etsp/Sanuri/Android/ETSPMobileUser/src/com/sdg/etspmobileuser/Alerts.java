package com.sdg.etspmobileuser;

import java.util.ArrayList;

import com.sdg.models.Alert;
import com.sdg.util.DataBaseAccess;
import com.sdg.util.DataLayer;
import com.sdg.util.MyCustomAdapterAlert;
import com.sdg.util.MyCustomAdapterChecked;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class Alerts extends Activity implements OnClickListener{

	private Button buttonClearAll;
	DataBaseAccess dbAccess;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alerts);
		dbAccess=new DataBaseAccess(this);
		buttonClearAll=(Button)findViewById(R.id.buttonClearAll);
		buttonClearAll.setOnClickListener(this);
		fillAlerts();
	}

	private void fillAlerts() {
		// TODO Auto-generated method stub
		ArrayList<Alert> alertsList=new ArrayList<Alert>();
		Cursor cursor = dbAccess.GetData();
		if (cursor != null ) {
	        if  (cursor.moveToFirst()) {
        		Alert alert=new Alert();
	        	do {
	        		alert.setAlertID(cursor.getString(cursor.getColumnIndex(DataBaseAccess.FeedEntry.COLUMN_NAME_ALERT_ID)));
	        		alert.setObjectName(cursor.getString(cursor.getColumnIndex(DataBaseAccess.FeedEntry.COLUMN_NAME_OBJECT_NAME)));
	        		alert.setLocation(cursor.getString(cursor.getColumnIndex(DataBaseAccess.FeedEntry.COLUMN_NAME_LOCATION)));
	        		alert.setTime(cursor.getString(cursor.getColumnIndex(DataBaseAccess.FeedEntry.COLUMN_NAME_DATETIME)));
	        		alertsList.add(alert);
	        	}while (cursor.moveToNext());
	        }
	    	ListView listView = (ListView) findViewById(R.id.listAlerts);
			MyCustomAdapterAlert dataAdapter = new MyCustomAdapterAlert(this,R.layout.alert_item, alertsList);		
			listView.setAdapter(dataAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this)
		.setTitle("ETSP")
		.setMessage("Clear All?")
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

		    public void onClick(DialogInterface dialog, int whichButton) {
		    	dbAccess.deleteAll();
		    }})
		 .setNegativeButton(android.R.string.no, null).show();
	}

}
