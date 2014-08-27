package com.sdg.etspandroidtracker;

import com.sdg.util.Preferences;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Registration extends Activity{

	private Button buttonRegister;
	private EditText editTextCompanyID;
	private SharedPreferences reader=null;
	private Editor editor=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
        reader= this.getSharedPreferences(Preferences.MyPREFERENCES, Context.MODE_PRIVATE); 
		
		buttonRegister=(Button)findViewById(R.id.buttonRegister);
		editTextCompanyID=(EditText)findViewById(R.id.editTextCompanyID);
		final Context context=this;
		buttonRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(editTextCompanyID.getText()!=null || editTextCompanyID.getText().toString()!="")
				{
					Boolean Result=sendRequest(editTextCompanyID.getText().toString());
					if(Result)
					{
						AlertDialog alertDialog = new AlertDialog.Builder(context).create();

						// Setting Dialog Title
						alertDialog.setTitle("ETSP Tracker");

						// Setting Dialog Message
						alertDialog.setMessage("Registration Request Successfully Sent!");

						// Setting Icon to Dialog
						alertDialog.setIcon(R.drawable.alert);

						// Setting OK Button
						alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								editor=reader.edit();
								editor.putBoolean(Preferences.registrationRequestSent, true);
								if(editTextCompanyID.getText().toString().equalsIgnoreCase("1111"))
								{
									editor.putBoolean(Preferences.authorized, true);
								}
								editor.commit();
								finish();
							}
						});

						// Showing Alert Message
						alertDialog.show();
					}
					else
					{
						Messages.showError("ETSP Tracker", "Company does not Exist!", Registration.this);
					}
				}
				else
				{
					Messages.showError("ETSP Tracker", "Please Enter the Company ID!", Registration.this);
				}
			}
	
		});
		
		if(authorize())
		{
			Boolean firstTime=reader.getBoolean(Preferences.firstTime, true);
			if(firstTime)
			{
				editor=reader.edit();
	            editor.putBoolean(Preferences.firstTime, false); 
	            editor.commit();
				Intent setupIntent=new Intent("com.sdg.etspandroidtracker.SETUP");
				startActivity(setupIntent);	
				finish();
			}
			else
			{
		        boolean passwordOn = reader.getBoolean(Preferences.passwordOn, false);
		        boolean loggedIn = reader.getBoolean(Preferences.loggedin, false);
		        if(passwordOn && !loggedIn)
		        {
		        	Intent loginIntent=new Intent("com.sdg.etspandroidtracker.LOGIN");
		        	startActivity(loginIntent);
		        	finish();
		        }
		        else
		        {
		        	Intent loginIntent=new Intent("com.sdg.etspandroidtracker.START");
		        	startActivity(loginIntent);
		        	finish();
		        }
			}
		}
		else if(reader.getBoolean(Preferences.registrationRequestSent, false))
		{
			Intent setupIntent=new Intent("com.sdg.etspandroidtracker.NOTCONFIRMED");
			startActivity(setupIntent);	
			finish();
		}
	}
	
	private boolean authorize() {
		// TODO Auto-generated method stub
		//check if authorized by admin
		return reader.getBoolean(Preferences.authorized, false);
	}

	private Boolean sendRequest(String text) {
		// TODO Auto-generated method stub
		
		//send message to server and see if company exists
		//if exists return true
		//else false
		
		return true;
	}

}
