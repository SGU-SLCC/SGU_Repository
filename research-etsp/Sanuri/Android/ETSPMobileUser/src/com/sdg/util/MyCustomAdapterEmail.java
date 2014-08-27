package com.sdg.util;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdg.etspmobileuser.EditUser;
import com.sdg.etspmobileuser.ManageObjects;
import com.sdg.etspmobileuser.ManageTrackers;
import com.sdg.etspmobileuser.ManageUsers;
import com.sdg.etspmobileuser.R;
import com.sdg.etspmobileuser.Statistics;
import com.sdg.models.CheckListData;

public class MyCustomAdapterEmail  extends ArrayAdapter<CheckListData> {
	  
	  private ArrayList<CheckListData> itemList;
	  private Context context;
	  public ArrayList<Integer> selectedList;
	  private GetResponse asyncRate;
	  private HandleDeletion handleDeletion;
	  private String parentReport;
		
	  //Constructor
	  public MyCustomAdapterEmail(Context context, int textViewResourceId,ArrayList<CheckListData> list,String parentReport) {
		super(context, textViewResourceId, list);
		this.context=context;
		itemList = new ArrayList<CheckListData>();
		itemList.addAll(list);
		this.parentReport=parentReport;
	  }
	  
	  //Holds Item information
	  private class ViewHolder {
	   TextView itemName;
	   ImageView deleteImage;
	   ImageView emailImage;
	  }
	  
	  //Gets view
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	  
	   ViewHolder holder = null;
	  
	   if (convertView == null) {
		  
		   LayoutInflater vi = ((Activity) context).getLayoutInflater();
		   convertView = vi.inflate(R.layout.custom_reports_list_item, null);
	  
	   holder = new ViewHolder();
	   holder.itemName = (TextView) convertView.findViewById(R.id.textViewItemName);
	   holder.deleteImage = (ImageView) convertView.findViewById(R.id.imageViewDelete);
	   holder.emailImage = (ImageView) convertView.findViewById(R.id.imageViewEmail);
	   holder.itemName.setTextColor(Color.WHITE);	
	   holder.deleteImage.setImageResource(R.drawable.cancel);
	   holder.emailImage.setImageResource(R.drawable.email);
	   
	   convertView.setTag(holder);
	  
		    holder.itemName.setOnClickListener( new View.OnClickListener() { 
		     
		    	public void onClick(View v) { 
					TextView textViewItem = (TextView) v ; 
					final CheckListData item = (CheckListData) textViewItem.getTag(); 
					asyncRate = new GetResponse(null,(Statistics)context,null,MethodsList.openReport.ordinal(),item.getName(),-1,parentReport);
					asyncRate.execute();
		    	}
		    });
		    
		    holder.deleteImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					ImageView imageViewDelete = (ImageView) v ; 
					final CheckListData item = (CheckListData) imageViewDelete.getTag(); 
					
					new AlertDialog.Builder(context)
					.setTitle("ETSP")
					.setMessage("Delete Item Permenantly?")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

					    public void onClick(DialogInterface dialog, int whichButton) {
					    	handleDeletion = new HandleDeletion((Statistics)context, item.getName(), MethodsList.customReport.ordinal());
					    	handleDeletion.execute(); 
					    	((Statistics)context).fillCustomReports(parentReport);
					    }})
					 .setNegativeButton(android.R.string.no, null).show();
				}
			});
		    
		    
		   holder.emailImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// get prompts.xml view
				LayoutInflater layoutInflater = LayoutInflater.from(context);

				View promptView = layoutInflater.inflate(R.layout.prompt, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set prompts.xml to be the layout file of the alertdialog builder
				alertDialogBuilder.setView(promptView);

				final EditText input = (EditText) promptView.findViewById(R.id.editTextEmailAddress);
				ImageView imageViewEmail = (ImageView) v ; 
				final CheckListData selectedItem = (CheckListData) imageViewEmail.getTag(); 
				
				// setup a dialog window
				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										// get user input and set it to result
										asyncRate = new GetResponse(null,(Statistics)context,null,MethodsList.emailReport.ordinal(),selectedItem.getName(),input.getText().toString(),-1);
										asyncRate.execute();
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int id) {
										dialog.cancel();
									}
								});

				// create an alert dialog
				AlertDialog alertD = alertDialogBuilder.create();

				alertD.show();
			}
		});
	   }
	   else {
	    holder = (ViewHolder) convertView.getTag();
	   }
	   
	   //Set text of textview part of list item and attach the tag
	   CheckListData item = itemList.get(position);
	   holder.itemName.setText(item.getName());
	   holder.itemName.setTag(item);
	   holder.deleteImage.setTag(item);
	   holder.emailImage.setTag(item);
	   return convertView;	  
	  }  
}