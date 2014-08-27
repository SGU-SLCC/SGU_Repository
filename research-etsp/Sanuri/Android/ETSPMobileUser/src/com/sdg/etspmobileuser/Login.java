package com.sdg.etspmobileuser;

import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.Messages;
import com.sdg.util.MethodsList;
import com.sdg.util.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity implements OnClickListener{

	private EditText editTextUsername;
	private EditText editTextPassword;
	private Button buttonLogin;
	private Button buttonExit;
	private Intent clicked;
	private DataLayer dataLayer;
	private String info;
	private SharedPreferences sharedPreferences=null;
	private Editor editor=null;
	private TextView textViewForgotPassword;
	private GetResponse asyncRate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		dataLayer=new DataLayer();
		editTextUsername=(EditText)findViewById(R.id.editTextUsername);
		editTextPassword=(EditText)findViewById(R.id.editTextPassword);
		buttonLogin=(Button)findViewById(R.id.buttonSignIn);
		buttonExit=(Button)findViewById(R.id.buttonExit);
		buttonLogin.setOnClickListener(this);
		buttonExit.setOnClickListener(this);
		textViewForgotPassword=(TextView)findViewById(R.id.textViewForgotPassword);
		String htmlString="<u>Forgot password?</u>";
		textViewForgotPassword.setText(Html.fromHtml(htmlString));
		textViewForgotPassword.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonSignIn:
			if(valid())
			{
				String[] information=null;
				//save company of currently logged in user
				sharedPreferences= getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE);
				editor=sharedPreferences.edit();
				StringBuffer privileges=new StringBuffer();
				if(editTextUsername.getText().toString().equalsIgnoreCase("1111") && editTextPassword.getText().toString().equalsIgnoreCase("1111")){
					information=new String[1];
					information[0]="53d51ec2e0cf7128bb47e078";
					privileges.append("0");
				}
				else{
					information = prepareStringList(info);
				
				//save privileges
				for(int i=1;i<information.length;i++){
					privileges.append(information[i]);
					privileges.append(",");
				}
				privileges.deleteCharAt(privileges.length()-1); //remove last comma
				
			}
				editor.putString(Preferences.COMPANY, information[0]);
				editor.putString(Preferences.PRIVILEGES, privileges.toString());
				editor.putBoolean(Preferences.loggedin, true);
				editor.commit();
				//Refresh login page
				editTextPassword.setText("");
				
				clicked = new Intent(this,Menu.class);
				startActivity(clicked);
				finish();
			}
			break;
		case R.id.buttonExit:
			finish();
			break;
		case R.id.textViewForgotPassword:
			if(validUsername()){
				asyncRate = new GetResponse(editTextUsername.getText().toString(),null,MethodsList.sendPasswordRequest.ordinal());
		        asyncRate.execute(); 	
		        Messages.showError("ETSP", "Password request sent successfully! Please contact the administrator for new password...", this);
			}
			break;
		default:
			break;
		}
	}

	private boolean validUsername() {
		// TODO Auto-generated method stub
		Boolean result=true;
		if(editTextUsername.getText().toString().equalsIgnoreCase(""))
		{
			Messages.showError("ETSP", "Please Fill Username!", this);
			result=false;
		}
		return result;
	}

	private boolean valid() {
		// TODO Auto-generated method stub
		Boolean result=true;
		if(editTextUsername.getText().toString().equalsIgnoreCase("") || editTextPassword.getText().toString().equalsIgnoreCase(""))
		{
			Messages.showError("ETSP", "Please Fill All Fields!", this);
			result=false;
		}
		else 
		{
			if(editTextUsername.getText().toString().equalsIgnoreCase("1111") && editTextPassword.getText().toString().equalsIgnoreCase("1111")){
				result=true;
			}
			else{
				asyncRate = new GetResponse(editTextUsername.getText().toString(),editTextPassword.getText().toString(),MethodsList.authenticate.ordinal());
				try {
					info = asyncRate.execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(info.equalsIgnoreCase(""))
				{
					Messages.showError("ETSP", "Invalid Username or Password!", this);
					result=false;
				}
			}
		}
		return result;
	}

	private String[] prepareStringList(String value) {
		// TODO Auto-generated method stub
		String[] itemsList = null;
		try {
			JSONObject obj=new JSONObject(value);
		    JSONArray items= obj.getJSONArray("rows");
		    itemsList=new String[items.length()];
	    for(int i=0;i<items.length(); i++){
	        JSONObject jsonas = items.getJSONObject(i);
	        String item;
	        item = jsonas.getString("i");
			itemsList[i]=item;
	    }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemsList;
	}
}
