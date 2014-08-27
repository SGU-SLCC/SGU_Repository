package com.sdg.etspmobileuser;

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

public class ObjectCategories extends Activity implements OnClickListener{

	private Button buttonWildLife;
	private Button buttonVehicle;
	private Button buttonPerson;
	private Button buttonOther;
	private SharedPreferences sharedPreferences=null;
	private Editor editor=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.object_categories);
		sharedPreferences=this.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE); 
		buttonWildLife=(Button)findViewById(R.id.buttonWildLife);
		buttonVehicle=(Button)findViewById(R.id.buttonVehicle);
		buttonPerson=(Button)findViewById(R.id.buttonPerson);
		buttonOther=(Button)findViewById(R.id.buttonOther);
		buttonWildLife.setOnClickListener(this);
		buttonVehicle.setOnClickListener(this);
		buttonPerson.setOnClickListener(this);
		buttonOther.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		editor=sharedPreferences.edit();
		switch(v.getId())
		{
		case R.id.buttonWildLife:
			editor.putString(Preferences.OBJECTCATEGORY, "animal");
			break;
		case R.id.buttonVehicle:
			editor.putString(Preferences.OBJECTCATEGORY, "vehicle");
			break;
		case R.id.buttonPerson:
			editor.putString(Preferences.OBJECTCATEGORY, "person");
			break;
		case R.id.buttonOther:
			editor.putString(Preferences.OBJECTCATEGORY, "other");
			break;
		}
		editor.commit();
		Intent clicked = new Intent("com.sdg.etspmobileuser.MANAGEOBJECTS");
		startActivity(clicked);
	}

}
