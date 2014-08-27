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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sdg.etspmobileuser.EditUser;
import com.sdg.etspmobileuser.ManageObjects;
import com.sdg.etspmobileuser.ManageTrackers;
import com.sdg.etspmobileuser.ManageUsers;
import com.sdg.etspmobileuser.R;
import com.sdg.etspmobileuser.Statistics;
import com.sdg.models.CheckListData;

public class MyCustomAdapterDelete extends ArrayAdapter<CheckListData> {
	  
	  private ArrayList<CheckListData> itemList;
	  private Context context;
	  public ArrayList<Integer> selectedList;
	  private SharedPreferences sharedPreferences=null;
	  private Editor editor=null;
	  private int category;
	  private DataLayer dataLayer;
	  private HandleDeletion handleDeletion;
	  
	  //Constructor
	  public MyCustomAdapterDelete(Context context, int textViewResourceId,ArrayList<CheckListData> list,int category) {
		super(context, textViewResourceId, list);
		this.context=context;
		this.category=category;
		itemList = new ArrayList<CheckListData>();
		itemList.addAll(list);
		sharedPreferences=context.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE); 
		dataLayer=new DataLayer();
	  }
	  
	  //Holds Item information
	  private class ViewHolder {
	   TextView itemName;
	   ImageView deleteImage;
	  }
	  
	  //Gets view
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	  
	   ViewHolder holder = null;
	  
	   if (convertView == null) {
		  
		   LayoutInflater vi = ((Activity) context).getLayoutInflater();
		   convertView = vi.inflate(R.layout.list_item_with_delete, null);
	  
	   holder = new ViewHolder();
	   holder.itemName = (TextView) convertView.findViewById(R.id.textViewItemName);
	   holder.deleteImage = (ImageView) convertView.findViewById(R.id.imageViewDelete);
	   holder.itemName.setTextColor(Color.WHITE);	
	   holder.deleteImage.setImageResource(R.drawable.cancel);
	   
	   convertView.setTag(holder);
	  
		    holder.itemName.setOnClickListener( new View.OnClickListener() { 
		     
		    	public void onClick(View v) { 
		      TextView textViewItem = (TextView) v ; 
		      CheckListData item = (CheckListData)textViewItem.getTag();
				
				Intent objectEdit;
				editor=sharedPreferences.edit();
				editor.putBoolean(Preferences.ISINSERT, false);
				editor.commit();
				MethodsList currentCategory = MethodsList.values()[category];
				switch (currentCategory) {
				case animal:
					objectEdit=new Intent("com.sdg.etspmobileuser.CREATEWILDLIFE");
					objectEdit.putExtra("objectID", item.getId());
					break;
				case vehicle:
					objectEdit=new Intent("com.sdg.etspmobileuser.CREATEVEHICLE");
					objectEdit.putExtra("objectID", item.getId());
					break;
				case person:
					objectEdit=new Intent("com.sdg.etspmobileuser.CREATEPERSON");	
					objectEdit.putExtra("objectID", item.getId());
					break;
				case other:
					objectEdit=new Intent("com.sdg.etspmobileuser.CREATEOTHER");
					objectEdit.putExtra("objectID", item.getId());
					break;
				case tracker:
		    		editor=sharedPreferences.edit();
		    		editor.putBoolean(Preferences.ISINSERT, false);
		    		editor.putString(Preferences.TRACKERID, item.getId());
		    		editor.commit();
		    		objectEdit=new Intent("com.sdg.etspmobileuser.NEWTRACKER");
		    		break;
				default:
		    		objectEdit = new Intent(context, EditUser.class); 
		    		objectEdit.putExtra("username", item.getId());
					break;
				}
				context.startActivity(objectEdit);
		    	}
		    });
		    
		    holder.deleteImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					ImageView imageViewDelete = (ImageView) v ; 
					final CheckListData item = (CheckListData) imageViewDelete.getTag(); 
					
					final int cat=category;
					new AlertDialog.Builder(context)
					.setTitle("ETSP")
					.setMessage("Delete Item Permenantly?")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

					    public void onClick(DialogInterface dialog, int whichButton) {
					    	
					    	MethodsList currentCategory = MethodsList.values()[cat];
					    	switch (currentCategory) {
							case tracker:
						    	handleDeletion = new HandleDeletion((ManageTrackers)context, item.getId(), MethodsList.tracker.ordinal());
						    	handleDeletion.execute(); 
					    		((ManageTrackers)context).fillTrackers();
								break;
							case user:
						    	handleDeletion = new HandleDeletion((ManageUsers)context, item.getId(), MethodsList.user.ordinal());
						    	handleDeletion.execute(); 
					    		((ManageUsers)context).fillUsers();
								break;
							case animal:
							case vehicle:
							case person:
							case other:
						    	handleDeletion = new HandleDeletion((ManageObjects)context, item.getId(), cat);
						    	handleDeletion.execute(); 
					    		((ManageObjects)context).fillObjects();
								break;
							default:
								break;
							}

					    }})
					 .setNegativeButton(android.R.string.no, null).show();
				}
			});
	   }
	   else {
	    holder = (ViewHolder) convertView.getTag();
	   }
	   
	   //Set text of textview part of list item and attach the tag
	   MethodsList currentCategory = MethodsList.values()[category];
	   CheckListData item = itemList.get(position);
	   switch (currentCategory) {
	case tracker:
		holder.itemName.setText(item.getName());
		break;
	case user:
		holder.itemName.setText(item.getName());
		break;
	default:
		holder.itemName.setText(item.getName());
		break;
	}
	   holder.itemName.setTag(item);
	   holder.deleteImage.setTag(item);
	   return convertView;	  
	  }  
}