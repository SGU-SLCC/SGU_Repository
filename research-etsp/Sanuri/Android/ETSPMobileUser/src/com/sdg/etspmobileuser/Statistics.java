package com.sdg.etspmobileuser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sdg.models.CheckListData;
import com.sdg.util.DataLayer;
import com.sdg.util.GetResponse;
import com.sdg.util.HandleDeletion;
import com.sdg.util.MethodsList;
import com.sdg.util.MyCustomAdapterDelete;
import com.sdg.util.Preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Statistics extends Activity implements OnItemClickListener{

	private ListView listViewPredefined;
	private ListView listViewCustom;
	private DataLayer dataLayer;
	private int selection=0;
	private GetResponse asyncRate;
	private HandleDeletion handleDeletion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics_reports);
		dataLayer=new DataLayer();
		listViewPredefined=(ListView)findViewById(R.id.listPredefinedReports);
		listViewCustom=(ListView)findViewById(R.id.listCustomReports);
		listViewPredefined.setOnItemClickListener(this);
		listViewCustom.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fillPredefinedReports();
	}



	private void fillPredefinedReports() {
		// TODO Auto-generated method stub
		//asyncRate = new GetResponse(listViewPredefined,this,null,MethodsList.getPredefinedReports.ordinal(),null,"preDefinedReports");
		asyncRate = new GetResponse(listViewPredefined,this,null,MethodsList.getPredefinedReports.ordinal(),null,-1,"preDefinedReports");
		String response;
		try {
			response = asyncRate.execute().get();
			try {
				JSONObject obj=new JSONObject(response);
			    JSONArray users= obj.getJSONArray("rows");
			    ArrayList<String> usersList=new ArrayList<String>();		
			    for(int i=0;i<users.length(); i++){
			        JSONObject jsonas = users.getJSONObject(i);
			        usersList.add(jsonas.getString("u"));
			    }
			    
				listViewPredefined.setAdapter(new ArrayAdapter<String>(this,R.layout.plain_list_view_item, R.id.textViewItem, usersList));
				listViewPredefined.setSelection(selection);
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
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		for(int i=0;i<parent.getChildCount();i++)
		{
			parent.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.list_background));
		}
		view.setBackgroundColor(getResources().getColor(R.color.list_background_pressed));
		String selectedReportType="";
		switch (parent.getId()) {
		case R.id.listPredefinedReports:
			selectedReportType=((TextView)view.findViewById(R.id.textViewItem)).getText().toString();
			fillCustomReports(selectedReportType);
			break;
		default:
			break;
		}
	}

	public void fillCustomReports(String selectedReportType) {
		// TODO Auto-generated method stub
		asyncRate = new GetResponse(listViewCustom,this,null,MethodsList.getCustomReports.ordinal(),selectedReportType,MethodsList.customReport.ordinal(),selectedReportType);
        asyncRate.execute(); 
	}
	
}
