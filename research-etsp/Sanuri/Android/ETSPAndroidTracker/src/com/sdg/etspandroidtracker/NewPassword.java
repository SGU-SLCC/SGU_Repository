package com.sdg.etspandroidtracker;

import com.sdg.util.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class NewPassword extends android.support.v4.app.Fragment{

	private Button buttonApplyPassword;
	private EditText editTextPassword;
	private EditText editTextConfirmPassword;
	private SharedPreferences reader=null;
	private Editor editor=null;
	private LinearLayout linearLayoutPassword;
	
	public NewPassword() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		reader = this.getActivity().getSharedPreferences(Preferences.MyPREFERENCES, Context.MODE_PRIVATE); 
		editor = reader.edit();
		LinearLayout CreatePassLLayout=(LinearLayout)inflater.inflate(R.layout.new_password, container,false);
		editTextPassword=(EditText)CreatePassLLayout.findViewById(R.id.editTextNewPassword);
		editTextConfirmPassword=(EditText)CreatePassLLayout.findViewById(R.id.editTextConfirmPassword);
		buttonApplyPassword=(Button)CreatePassLLayout.findViewById(R.id.buttonApplyPassword);
		buttonApplyPassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v.getId()==R.id.buttonApplyPassword)
				{
					if(valid())
					{
			            editor.putBoolean(Preferences.passwordOn, true); 
			            editor.putString(Preferences.password, editTextPassword.getText().toString()); 
			            editor.putBoolean(Preferences.loggedin, true);
			            editor.commit();
						linearLayoutPassword=(LinearLayout)getActivity().findViewById(R.id.passwordlayout);
						linearLayoutPassword.setVisibility(View.VISIBLE);
						android.support.v4.app.FragmentManager frg=getActivity().getSupportFragmentManager();
						android.support.v4.app.FragmentTransaction trans=frg.beginTransaction();
						Fragment currentstatus=new ChangePasswordMain();
						trans.replace(R.id.passwordlayout, currentstatus);
						trans.commit();
					}
				}
			}

		});
		return CreatePassLLayout;
	}
	
	
	private boolean valid() {
		// TODO Auto-generated method stub
		String password=editTextPassword.getText().toString();
		String confirmPassword=editTextConfirmPassword.getText().toString();
		Boolean result=false;
		if(password.equals("") || confirmPassword.equals(""))
		{
			Messages.showError("ETSP Tracker", "Please Fill Both Fields!", getActivity());
		}
		else if(!password.equals(confirmPassword))
		{
			Messages.showError("ETSP Tracker","New Password does not Match the Confirmation!", getActivity());
		}
		else
		{
			result=true;
		}
		return result;
	}
}
