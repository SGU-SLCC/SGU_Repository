package com.sdg.etspmobileuser;

import com.sdg.util.Messages;
import com.sdg.util.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ChangePassword extends android.support.v4.app.Fragment{

	private SharedPreferences reader=null;
	private Editor editor=null;
	private EditText editTextCurrent;
	private EditText editTextNew;
	private EditText editTextConfirm;
	
	public ChangePassword() {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LinearLayout ChangePassLayout=(LinearLayout)inflater.inflate(R.layout.change_password, container,false);
		reader = this.getActivity().getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE); 
		editor = reader.edit();
		editTextCurrent=(EditText)ChangePassLayout.findViewById(R.id.editTextCurrentPassword);
		editTextNew=(EditText)ChangePassLayout.findViewById(R.id.editTextNewPassword);
		editTextConfirm=(EditText)ChangePassLayout.findViewById(R.id.editTextConfirmPassword);
		Button buttonChangePassword=(Button)ChangePassLayout.findViewById(R.id.buttonChangePassword);
		buttonChangePassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v.getId()==R.id.buttonChangePassword)
				{
					if(Valid())
					{
			            editor.putBoolean(Preferences.passwordOn, true); 
			            editor.putString(Preferences.password,editTextNew.getText().toString()); 
			            editor.commit();
			            
				        LinearLayout summary=(LinearLayout)getActivity().findViewById(R.id.passwordlayout);
				        summary.setVisibility(View.VISIBLE);
						android.support.v4.app.FragmentManager frg=getActivity().getSupportFragmentManager();
						android.support.v4.app.FragmentTransaction trans=frg.beginTransaction();
						Fragment currentstatus=new ChangePasswordMain();
						trans.replace(R.id.passwordlayout, currentstatus);
						trans.commit();
					}
				}
			}

		});
		return ChangePassLayout;
	}
	
	private boolean Valid() {
		// TODO Auto-generated method stub
		String correctPassword=reader.getString(Preferences.password, "");
		String currentPassword=editTextCurrent.getText().toString();
		String newPassword=editTextNew.getText().toString();
		String confirmPassword=editTextConfirm.getText().toString();
		Boolean result=false;
		if(currentPassword.equals("") || newPassword.equals("") || confirmPassword.equals(""))
		{
			Messages.showError("ETSP", "Please Fill All Fields!", getActivity());
		}
		else if(!correctPassword.equals(currentPassword))
		{
			Messages.showError("ETSP", "Current Password Entered is Invalid!", getActivity());
		}
		else if(!newPassword.equals(confirmPassword))
		{
			Messages.showError("ETSP", "New Password does not Match the Confirmation!", getActivity());
		}
		else
		{
			result=true;
		}
		return result;
	}
}
