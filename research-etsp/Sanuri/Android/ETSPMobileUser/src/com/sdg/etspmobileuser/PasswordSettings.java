package com.sdg.etspmobileuser;

import com.sdg.util.Preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;

public class PasswordSettings extends FragmentActivity{

	private Switch switchPassword;
	private LinearLayout linearLayoutPassword;
	private SharedPreferences reader=null;
	private Editor editor=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_settings);
        reader= this.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE); 
		
		linearLayoutPassword=(LinearLayout)findViewById(R.id.passwordlayout);
		switchPassword=(Switch)findViewById(R.id.switchPassword);
		
		switchPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				//if user switched on password show form to give new password
				if(isChecked)
				{
					linearLayoutPassword=(LinearLayout)findViewById(R.id.passwordlayout);
					linearLayoutPassword.setVisibility(View.VISIBLE);
					android.support.v4.app.FragmentManager frg=getSupportFragmentManager();
					android.support.v4.app.FragmentTransaction trans=frg.beginTransaction();
					Fragment currentstatus=new NewPassword();
					trans.replace(R.id.passwordlayout, currentstatus);
					trans.commit();
				}
				else//if he switched off, hide everything
				{
					editor = reader.edit();
		            editor.putBoolean(Preferences.passwordOn, false); 
		            editor.putString(Preferences.password, ""); 
		            editor.putBoolean(Preferences.loggedin, false); 
		            editor.commit();
			        LinearLayout summary=(LinearLayout)findViewById(R.id.passwordlayout);
			        summary.setVisibility(View.GONE);
				}
			}
		});
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//if password is already on, give user the option to change password
		Boolean passwordOn=reader.getBoolean(Preferences.passwordOn, false);
		if(passwordOn)
		{
			switchPassword.setChecked(true);
		}
		
		if(switchPassword.isChecked())
		{
			linearLayoutPassword=(LinearLayout)findViewById(R.id.passwordlayout);
			linearLayoutPassword.setVisibility(View.VISIBLE);
			android.support.v4.app.FragmentManager frg=getSupportFragmentManager();
			android.support.v4.app.FragmentTransaction trans=frg.beginTransaction();
			Fragment currentstatus=new ChangePasswordMain();
			trans.replace(R.id.passwordlayout, currentstatus);
			trans.commit();
		}
		else
		{
	        LinearLayout summary=(LinearLayout)findViewById(R.id.passwordlayout);
	        summary.setVisibility(View.GONE);
		}
	}

}
