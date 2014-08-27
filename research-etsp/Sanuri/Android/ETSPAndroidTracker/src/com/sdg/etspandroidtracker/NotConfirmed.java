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

public class NotConfirmed extends Activity implements OnClickListener{

	private Button buttonSendAgain;
	private SharedPreferences reader=null;
	private Editor editor=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.not_confirmed);
		buttonSendAgain=(Button)findViewById(R.id.buttonSendAgain);
		buttonSendAgain.setOnClickListener(this);
		reader= this.getSharedPreferences(Preferences.MyPREFERENCES, Context.MODE_PRIVATE); 
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		editor=reader.edit();
        editor.putBoolean(Preferences.registrationRequestSent, false); 
        editor.commit();
		Intent setupIntent=new Intent(this,Registration.class);
		startActivity(setupIntent);	
		finish();
	}

	
}
