package com.sdg.etspandroidtracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class ChangePasswordMain extends android.support.v4.app.Fragment{

	public ChangePasswordMain() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LinearLayout ChangePassMainLayout=(LinearLayout)inflater.inflate(R.layout.change_password_main, container,false);
		Button buttonChangePassword=(Button)ChangePassMainLayout.findViewById(R.id.buttonChangePasswordMain);
		buttonChangePassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v.getId()==R.id.buttonChangePasswordMain)
				{
			        LinearLayout summary=(LinearLayout)getActivity().findViewById(R.id.passwordlayout);
			        summary.setVisibility(View.VISIBLE);
					android.support.v4.app.FragmentManager frg=getActivity().getSupportFragmentManager();
					android.support.v4.app.FragmentTransaction trans=frg.beginTransaction();
					Fragment currentstatus=new ChangePassword();
					trans.replace(R.id.passwordlayout, currentstatus);
					trans.commit();
				}
			}
		});
		return ChangePassMainLayout;
	}

}
