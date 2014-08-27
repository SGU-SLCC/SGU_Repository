package com.sdg.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.sdg.etspmobileuser.R;
import com.sdg.models.CheckListData;

	public class MyCustomAdapterChecked extends ArrayAdapter<CheckListData> {
		  
		  private ArrayList<CheckListData> itemList;
		  private Context context;
		  public ArrayList<String> selectedList;
		  
		  //Constructor
		  public MyCustomAdapterChecked(Context context, int textViewResourceId,ArrayList<CheckListData> list,ArrayList<String> selectedList) {
			super(context, textViewResourceId, list);
			this.context=context;
			this.selectedList=selectedList;
			itemList = new ArrayList<CheckListData>();
			itemList.addAll(list);
		  }
		  
		  //Holds Item information
		  private class ViewHolder {
		   CheckBox name;
		  }
		  
		  //Gets view
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		  
		   ViewHolder holder = null;
		  
		   if (convertView == null) {
			  
	 		   LayoutInflater vi = ((Activity) context).getLayoutInflater();
			   convertView = vi.inflate(R.layout.sensor_info, null);
			   
		   //LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		   //convertView = vi.inflate(R.layout.sensor_info, null);
		  
		   holder = new ViewHolder();
		   holder.name = (CheckBox) convertView.findViewById(R.id.checkBoxSelection);
		   holder.name.setTextColor(Color.WHITE);	   
		   
		   
		   convertView.setTag(holder);
		  
			    holder.name.setOnClickListener( new View.OnClickListener() { 
			     
			    	public void onClick(View v) { 
			      CheckBox cb = (CheckBox) v ; 
			      CheckListData item = (CheckListData) cb.getTag(); 
			      item.setSelected(cb.isChecked());
			      
			      ArrayList<String> t=selectedList;
			      for (int i = 0; i < selectedList.size(); i++) {
			    	String s=selectedList.get(i).trim();
					if(selectedList.get(i).trim().equalsIgnoreCase(item.getId())){
						selectedList.remove(i);
					}
				}
			    
			      if(item.isSelected()){
			    	  selectedList.add(item.getId());
			     } 
			    	}
			    });
		   }
		   else {
		    holder = (ViewHolder) convertView.getTag();
		   }
		  
		   CheckListData item = itemList.get(position);
		   
		   if(selectedList!=null){
			   for (String selectedItem : selectedList) {
				   selectedItem=selectedItem.trim();
				if(selectedItem.equalsIgnoreCase(item.getId().trim())){
					item.setSelected(true);
				}
			   }
		   }
		  
		   holder.name.setText(item.getName());
		   holder.name.setChecked(item.isSelected());
		   holder.name.setTag(item);		  
		   return convertView;	  
		  }	  
	}
