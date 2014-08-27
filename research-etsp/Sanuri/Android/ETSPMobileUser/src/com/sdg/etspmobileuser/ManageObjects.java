package com.sdg.etspmobileuser;

import java.util.ArrayList;

import com.sdg.models.CheckListData;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.MethodsList;
import com.sdg.util.MyCustomAdapter;
import com.sdg.util.MyCustomAdapterChecked;
import com.sdg.util.MyCustomAdapterDelete;
import com.sdg.util.Preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class ManageObjects extends Activity implements OnClickListener{//,OnItemClickListener{

	private Button buttonCreateNew;
	private TextView textViewHeader;
	private ListView listViewWildLife;
	private DataLayer dataLayer;
	private Intent clicked;
	private SharedPreferences sharedPreferences=null;
	private Editor editor=null;
	private String category="";
	private ArrayAdapter<CheckListData> listViewData;
	private GetResponse asyncRate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_wildlife);
		dataLayer=new DataLayer();
		sharedPreferences=this.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE); 
		category=sharedPreferences.getString(Preferences.OBJECTCATEGORY, "");
		buttonCreateNew=(Button)findViewById(R.id.buttonAddNewWildLife);
		textViewHeader=(TextView)findViewById(R.id.textViewManageHeader);
		listViewWildLife=(ListView)findViewById(R.id.listViewManageWildLife);
		buttonCreateNew.setOnClickListener(this);
		
		if(category.equalsIgnoreCase("animal"))
		{
			buttonCreateNew.setText("Create New Animal");
			textViewHeader.setText("Manage Wild Life");
		}
		else if(category.equalsIgnoreCase("vehicle"))
		{
			buttonCreateNew.setText("Create New Vehicle");
			textViewHeader.setText("Manage Vehicles");
		}
		else if(category.equalsIgnoreCase("person"))
		{
			buttonCreateNew.setText("Create New Person");
			textViewHeader.setText("Manage People");
		}
		else
		{
			buttonCreateNew.setText("Create New Other Profile");	
			textViewHeader.setText("Manage Other Profiles");
		}
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fillObjects();
	}


	public void fillObjects() {
		// TODO Auto-generated method stub	
		if(category.equalsIgnoreCase("animal"))
		{
			asyncRate = new GetResponse(listViewWildLife,this,null,MethodsList.getWildLife.ordinal(),null,MethodsList.animal.ordinal(),null);
	        asyncRate.execute(); 	
		}
		else if(category.equalsIgnoreCase("vehicle"))
		{
			asyncRate = new GetResponse(listViewWildLife,this,null,MethodsList.getVehicles.ordinal(),null,MethodsList.vehicle.ordinal(),null);
	        asyncRate.execute(); 
		}
		else if(category.equalsIgnoreCase("person"))
		{
			asyncRate = new GetResponse(listViewWildLife,this,null,MethodsList.getPeople.ordinal(),null,MethodsList.person.ordinal(),null);
	        asyncRate.execute(); 
		}
		else
		{
			asyncRate = new GetResponse(listViewWildLife,this,null,MethodsList.getOther.ordinal(),null,MethodsList.other.ordinal(),null);
	        asyncRate.execute(); 
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		editor=sharedPreferences.edit();
		editor.putBoolean(Preferences.ISINSERT, true);
		editor.commit();
		switch (v.getId()) {
		case R.id.buttonAddNewWildLife:
			if(category.equalsIgnoreCase("animal"))
			{
				clicked = new Intent("com.sdg.etspmobileuser.CREATEWILDLIFE");
				startActivity(clicked);
			}
			else if(category.equalsIgnoreCase("vehicle"))
			{
				clicked = new Intent("com.sdg.etspmobileuser.CREATEVEHICLE");
				startActivity(clicked);
			}
			else if(category.equalsIgnoreCase("person"))
			{
				clicked = new Intent("com.sdg.etspmobileuser.CREATEPERSON");
				startActivity(clicked);		
			}
			else
			{
				clicked = new Intent("com.sdg.etspmobileuser.CREATEOTHER");
				startActivity(clicked);	
			}
			break;
		default:
			break;
		}
		
	}
	
	
	/*@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if(view.getId()==R.id.imageViewDelete) // if delete is pressed
		{
			final View itemView=parent.findViewById(R.id.textViewItem);
			final String cat=category;
			new AlertDialog.Builder(this)
			.setTitle("ETSP")
			.setMessage("Delete Item Permenantly?")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			    public void onClick(DialogInterface dialog, int whichButton) {
			    	String [] Words=((TextView) itemView).getText().toString().split(":");
			    	dataLayer.deleteObject(Words[0],cat);
			    	fillObjects();
			    }})
			 .setNegativeButton(android.R.string.no, null).show();
		}
		else // if item pressed
		{
			Object item=listViewWildLife.getItemAtPosition(position);
	
			Intent objectEdit;
			editor=sharedPreferences.edit();
			editor.putBoolean(Preferences.ISINSERT, false);
			editor.commit();
			if(category.equalsIgnoreCase("animal"))
			{
				String []words=item.toString().split(":");
				objectEdit=new Intent("com.sdg.etspmobileuser.CREATEWILDLIFE");
				objectEdit.putExtra("objectID", words[0]);
			}
			else if(category.equalsIgnoreCase("vehicle"))
			{
				String []words=item.toString().split(":");
				objectEdit=new Intent("com.sdg.etspmobileuser.CREATEVEHICLE");
				objectEdit.putExtra("objectID", words[0]);
			}
			else if(category.equalsIgnoreCase("person"))
			{
				String []words=item.toString().split(":");
				objectEdit=new Intent("com.sdg.etspmobileuser.CREATEPERSON");	
				objectEdit.putExtra("objectID", words[0]);
			}
			else
			{
				String []words=item.toString().split(":");
				objectEdit=new Intent("com.sdg.etspmobileuser.CREATEOTHER");
				objectEdit.putExtra("objectID", words[0]);
			}
			startActivity(objectEdit);
		}
	}*/

}
