package com.sdg.etspmobileuser;

import java.util.ArrayList;

import com.sdg.models.CheckListData;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.MethodsList;
import com.sdg.util.MyCustomAdapterChecked;
import com.sdg.util.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;

public class RegistrationRequests extends Activity implements OnClickListener,OnCheckedChangeListener{

	private Button buttonAccept;
	private Button buttonReject;
	private ListView listViewRequests;
	private DataLayer dataLayer;
	private MyCustomAdapterChecked dataAdapter;
	private ArrayList<String> selectedRequests;
	private ImageView imageViewRefresh;
	private CheckBox checkBoxAll;
	private ArrayList<CheckListData> requests;
	private GetResponse asyncRate;
	private SharedPreferences sharedPreferences=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_requests);
		sharedPreferences=this.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE); 
		buttonAccept=(Button)findViewById(R.id.buttonAccept);
		buttonReject=(Button)findViewById(R.id.buttonReject);
		listViewRequests=(ListView)findViewById(R.id.listViewRequests);
		imageViewRefresh=(ImageView)findViewById(R.id.imageViewRefresh);
		checkBoxAll=(CheckBox)findViewById(R.id.checkBoxAll);
		buttonAccept.setOnClickListener(this);
		buttonReject.setOnClickListener(this);
		imageViewRefresh.setOnClickListener(this);
		checkBoxAll.setOnCheckedChangeListener(this);
		dataLayer=new DataLayer();
		selectedRequests=new ArrayList<String>();
		displayListView();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonAccept:
			asyncRate = new GetResponse(null,null,selectedRequests,MethodsList.rejectRequests.ordinal(),null,null,-1);
	        asyncRate.execute(); 
			selectedRequests=new ArrayList<String>();
			displayListView();
			break;
		case R.id.buttonReject:
			asyncRate = new GetResponse(null,null,selectedRequests,MethodsList.rejectRequests.ordinal(),null,null,-1);
	        asyncRate.execute(); 
			selectedRequests=new ArrayList<String>();
			displayListView();
			break;
		case R.id.imageViewRefresh:
			selectedRequests=dataAdapter.selectedList;
			displayListView();
			break;
		default:
			break;
		}
	}

	private void displayListView() {
		asyncRate = new GetResponse(listViewRequests,this,null,MethodsList.getPendingRequests.ordinal(),sharedPreferences.getString(Preferences.COMPANY, ""),-1,null);
        asyncRate.execute(); 
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if(isChecked)
		{
			for (CheckListData data : requests) {
				selectedRequests.add(data.getId());
			}
		}
		else
		{
			selectedRequests.clear();
		}
		displayListView();
	}	
}
