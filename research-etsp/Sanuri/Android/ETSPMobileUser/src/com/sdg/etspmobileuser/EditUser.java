package com.sdg.etspmobileuser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sdg.models.CheckListData;
import com.sdg.models.User;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.HandleInsertion;
import com.sdg.util.HandleUpdates;
import com.sdg.util.Messages;
import com.sdg.util.MethodsList;
import com.sdg.util.MyCustomAdapterChecked;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class EditUser extends Activity implements OnClickListener{
	
	private MyCustomAdapterChecked dataAdapter = null;
	private Button buttonEdit;
	private Button buttonReset;
	private EditText editTextName;
	private EditText editTextUsername;
	private EditText editTextDescription;
	private ArrayList<String> selectedPrivileges=new ArrayList<String>();
	private DataLayer dataLayer;
	private User user=null;
	private String currentUser;
	private GetResponse asyncRate;
	
	private Button buttonFill;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_user);
		dataLayer=new DataLayer();
		buttonEdit=(Button)findViewById(R.id.buttonEdit);
		buttonReset=(Button)findViewById(R.id.buttonDelete);
		buttonEdit.setOnClickListener(this);
		buttonReset.setOnClickListener(this);
		editTextName=(EditText)findViewById(R.id.editTextEditName);
		editTextUsername=(EditText)findViewById(R.id.editTextEditUsername);
		editTextDescription=(EditText)findViewById(R.id.editTextEditDescription);
		
		buttonFill=(Button)findViewById(R.id.buttonFill);
		buttonFill.setOnClickListener(this);
	}

	
	private void fillDetails() {
		// TODO Auto-generated method stub
		Intent intent=getIntent();
		currentUser=intent.getStringExtra("username");
		asyncRate = new GetResponse(null,this,null,MethodsList.getUserDetails.ordinal(),currentUser,-1,null);
		String response;
		try {
			response = asyncRate.execute().get();

			JSONObject obj;
			try {
				obj = new JSONObject(response);
			    //JSONArray users= obj.getJSONArray("rows");
			    //JSONObject jsonas = users.getJSONObject(0);
			    user=new User();
			    user.setId(obj.getString("_id"));
			    user.setUsername(obj.getString("username"));
			    user.setName(obj.getString("name"));
			    user.setDescription(obj.getString("description"));
			    String privileges = obj.getString("privileges");
			    String []array = privileges.subSequence(1, privileges.length()-1).toString().split(","); //remove [,] and split by comma.
			    ArrayList<String> pList=new ArrayList<String>();
			    for(int i=0;i<array.length; i++){
			        pList.add(array[i]);
			    } 
			    user.setPrevileges(pList);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		editTextName.setText(user.getName());
		editTextUsername.setText(user.getUsername());
		editTextUsername.setEnabled(false);
		editTextDescription.setText(user.getDescription());
		selectedPrivileges= user.getPrevileges();
		displayListView();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fillDetails();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonEdit:
			if(valid())
			{
				user.setId(currentUser);
				user.setName(editTextName.getText().toString());
				user.setDescription(editTextDescription.getText().toString());
				user.setPrevileges(selectedPrivileges);
				HandleUpdates asyncRate = new HandleUpdates(this,user,MethodsList.user.ordinal());
				asyncRate.execute(); 
				finish();
			}
			break;
		case R.id.buttonDelete:
			reset();
			break;
		case R.id.buttonFill:
			editTextName.setText("dsdfsfsdfsdfsfd");
			editTextUsername.setText("dsdfsfsdfsdfsfd");
			editTextDescription.setText("dsdfsfsdfsdfsfd");
			break;
		default:
			break;
		}
	}

	private void reset() {
		// TODO Auto-generated method stub
		editTextName.setText("");
		editTextDescription.setText("");
		selectedPrivileges=new ArrayList<String>();
		displayListView();
	}
	
	//Create and display check box list
	private void displayListView() {
		ListView listViewPrivileges = (ListView) findViewById(R.id.listViewEditPrivileges);
		asyncRate = new GetResponse(listViewPrivileges,this,selectedPrivileges,MethodsList.getPrivileges.ordinal(),null,-1,null);
        asyncRate.execute();
	}
	
	private boolean valid() {
		// TODO Auto-generated method stub
		boolean valid=true;
		if(editTextName.getText().toString().equals("") || editTextUsername.getText().toString().equals("") || editTextDescription.getText().toString().equals(""))
		{
			Messages.showError("ETSP", "Please Fill All Fields!", this);
			valid=false;
		}
		return valid;
	}

}

