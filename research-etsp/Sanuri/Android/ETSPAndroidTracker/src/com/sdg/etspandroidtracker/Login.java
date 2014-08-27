package com.sdg.etspandroidtracker;

import com.sdg.util.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity{

	private EditText editTextPassword;
	private Button buttonSignIn;
	private Button buttonExit;
	private SharedPreferences reader=null;
	private Editor editor=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		editTextPassword=(EditText)findViewById(R.id.editTextPassword);
		buttonSignIn=(Button)findViewById(R.id.buttonSignIn);
		buttonExit=(Button)findViewById(R.id.buttonExit);
		buttonSignIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v.getId()==R.id.buttonSignIn)
				{
					if(Valid())
					{
						editor=reader.edit();
						editor.putBoolean(Preferences.loggedin, true);
						editor.commit();
						Intent mainIntent = new Intent(Login.this, MainPage.class);
						startActivity(mainIntent);	
						finish();
					}
				}
			}

		});
		
		buttonExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	
	private boolean Valid() {
		// TODO Auto-generated method stub
		boolean result=false;
        reader= this.getSharedPreferences(Preferences.MyPREFERENCES, Context.MODE_PRIVATE); 
		String password=reader.getString(Preferences.password, "");
		if(password.equals(editTextPassword.getText().toString()))
		{
			result=true;
		}
		else
		{
			Messages.showError("ETSP Tracker", "Wrong Password!", this);
		}
		return result;
	}
}
