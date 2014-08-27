package com.sdg.etspmobileuser;

import com.sdg.util.GetResponse;
import com.sdg.util.HandleDeletion;
import com.sdg.util.MethodsList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ManageUsers extends Activity implements OnClickListener{//,OnItemClickListener{
	
	private Button buttonCreateNew;
	private Intent clicked;
	private ListView listViewUsers;
	private GetResponse asyncRate;
	private HandleDeletion handleDeletion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_users);
		buttonCreateNew=(Button)findViewById(R.id.ButtonCreateNewUser);
		buttonCreateNew.setOnClickListener(this);
		listViewUsers = (ListView) findViewById(R.id.listViewUsers);
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fillUsers();
	}



	public void fillUsers() {
		// TODO Auto-generated method stub
		asyncRate = new GetResponse(listViewUsers,this,null,MethodsList.getUsers.ordinal(),null,MethodsList.user.ordinal(),null);
        asyncRate.execute(); 
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ButtonCreateNewUser:
			clicked = new Intent("com.sdg.etspmobileuser.CREATEUSER");
			startActivity(clicked);
			break;
		default:
			break;
		}
		
	}
	
}