package com.sdg.etspmobileuser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.sdg.models.CheckListData;
import com.sdg.models.User;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.HandleInsertion;
import com.sdg.util.Messages;
import com.sdg.util.MethodsList;
import com.sdg.util.MyCustomAdapterChecked;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CreateUser extends Activity implements OnClickListener{
	
	private Button buttonCreate;
	private Button buttonReset;
	private EditText editTextName;
	private EditText editTextUsername;
	private EditText editTextPassword;
	private EditText editTextDescription;
	private EditText editTextConfirmPassword;
	private ArrayList<String> selectedPrivileges=new ArrayList<String>();
	public static ArrayList<CheckListData> privileges=new ArrayList<CheckListData>();
	private GetResponse asyncRate;
	
	private Button buttonFill;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_user);
		buttonCreate=(Button)findViewById(R.id.buttonCreate);
		buttonReset=(Button)findViewById(R.id.buttonReset);
		buttonCreate.setOnClickListener(this);
		buttonReset.setOnClickListener(this);
		editTextName=(EditText)findViewById(R.id.editTextName);
		editTextUsername=(EditText)findViewById(R.id.editTextUsername);
		editTextPassword=(EditText)findViewById(R.id.editTextPassword);
		editTextConfirmPassword=(EditText)findViewById(R.id.editTextConfirmPassword);
		editTextDescription=(EditText)findViewById(R.id.editTextDescription);
		
		buttonFill=(Button)findViewById(R.id.buttonFill);
		buttonFill.setOnClickListener(this);
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		reset();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonCreate:
			if(valid())
			{
				User user=new User();
				user.setName(editTextName.getText().toString());
				user.setUsername(editTextUsername.getText().toString());
				user.setPassword(editTextPassword.getText().toString());
				user.setDescription(editTextDescription.getText().toString());
				user.setPrevileges(selectedPrivileges);
				HandleInsertion asyncRate = new HandleInsertion(this,user,MethodsList.user.ordinal());
				asyncRate.execute(); 
			}
			break;
		case R.id.buttonReset:
			reset();
			break;
		case R.id.buttonFill:
			editTextName.setText("dsdfsfsdfsdfsfd");
			editTextUsername.setText("dsdfsfsdfsdfsfd");
			editTextPassword.setText("dsdfsfsdfsdfsfd");
			editTextConfirmPassword.setText("dsdfsfsdfsdfsfd");
			editTextDescription.setText("dsdfsfsdfsdfsfd");
			break;
		default:
			break;
		}
	}
	
	private void reset() {
		// TODO Auto-generated method stub
		editTextUsername.setText("");
		editTextName.setText("");
		editTextPassword.setText("");
		editTextConfirmPassword.setText("");
		editTextDescription.setText("");
		selectedPrivileges.clear();
		displayListView();	
	}

	//Create and display check box list
	private void displayListView() {
		ListView listViewPrivileges = (ListView) findViewById(R.id.listViewPrivileges);
		asyncRate = new GetResponse(listViewPrivileges,this,selectedPrivileges,MethodsList.getPrivileges.ordinal(),null,-1,null);
        asyncRate.execute(); 
	}
	
	private boolean valid() {
		// TODO Auto-generated method stub
		boolean valid=true;
		if(editTextName.getText().toString().equals("") || editTextUsername.getText().toString().equals("") || editTextPassword.getText().toString().equals("") || editTextConfirmPassword.getText().toString().equals("") || editTextDescription.getText().toString().equals(""))
		{
			Messages.showError("ETSP", "Please Fill All Fields!", this);
			valid=false;
		}
		else if(!editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString()))
		{
			Messages.showError("ETSP", "New Password does not Match the Confirmation!", this);
			valid=false;
		}
		else
		{
			asyncRate = new GetResponse(null,this,null,MethodsList.checkUsernameExists.ordinal(),editTextUsername.getText().toString(),-1,null);
			String response="true";
			try {
				response = asyncRate.execute().get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(Boolean.parseBoolean(response))
			{
				Messages.showError("ETSP", "Username Already Exists!", this);
				valid=false;
			}
		}
		return valid;
	}
}
