package com.sdg.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sdg.etspmobileuser.R;

public class MyCustomAdapter extends ArrayAdapter<String> {
	  
	  private ArrayList<String> objectList;
	  private Context context;
	  
	  //Constructor
	  public MyCustomAdapter(Context context, int textViewResourceId,ArrayList<String> privileges) {
		super(context, textViewResourceId, privileges);
		this.context=context;
		objectList = new ArrayList<String>();
		objectList.addAll(privileges);
	  }
	  
	  //Holds Item information
	  private class ViewHolder {
	   TextView name;
	  }
	  
	  //Gets view
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	  
	   ViewHolder holder = null;
	  
	   if (convertView == null) {
	   LayoutInflater vi = ((Activity) context).getLayoutInflater();
	   convertView = vi.inflate(R.layout.plain_list_view_item, null);
	  
	   holder = new ViewHolder();
	   holder.name = (TextView) convertView.findViewById(R.id.textViewItem);
	   holder.name.setTextColor(Color.WHITE);	   
	     
	   convertView.setTag(holder);
	   }
	   else {
	    holder = (ViewHolder) convertView.getTag();
	   }
	  
	   String obj = objectList.get(position);

	  
	   holder.name.setText(obj);
	   holder.name.setTag(obj);		  
	   return convertView;	  
	  }	  
}