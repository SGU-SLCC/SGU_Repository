package com.sdg.util;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sdg.etspmobileuser.R;
import com.sdg.models.Alert;


public class MyCustomAdapterAlert  extends ArrayAdapter<Alert> {
	  
	  private ArrayList<Alert> alertsList;
	  private Context context;
	  
	  //Constructor
	  public MyCustomAdapterAlert(Context context, int textViewResourceId,ArrayList<Alert> alerts) {
		super(context, textViewResourceId,alerts);
		this.context=context;
		alertsList = new ArrayList<Alert>();
		alertsList.addAll(alerts);
	  }
	  
	  //Holds Item information
	  private class ViewHolder {
	   TextView objectName;
	   TextView location;
	   TextView time;
	   ImageView delete;
	  }
	  
	  //Gets view
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	  
	   ViewHolder holder = null;
	  
	   if (convertView == null) {
	   LayoutInflater vi = ((Activity) context).getLayoutInflater();
	   convertView = vi.inflate(R.layout.alert_item, null);
	  
	   holder = new ViewHolder();
	   holder.objectName = (TextView) convertView.findViewById(R.id.textViewObjectName);
	   holder.location = (TextView) convertView.findViewById(R.id.textViewLocation);
	   holder.time= (TextView) convertView.findViewById(R.id.textViewTime);
	   holder.delete = (ImageView) convertView.findViewById(R.id.imageViewDeleteAlert);
	   holder.objectName.setTextColor(Color.WHITE);	 
	   holder.location.setTextColor(Color.WHITE);	  
	   holder.time.setTextColor(Color.WHITE);	  
	   
	   convertView.setTag(holder);
	   
	   holder.delete.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ImageView imageViewDelete = (ImageView) v ; 
			final Alert obj = (Alert) imageViewDelete.getTag(); 
			
			new AlertDialog.Builder(context)
			.setTitle("ETSP")
			.setMessage("Clear Alert?")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			    public void onClick(DialogInterface dialog, int whichButton) {
					DataLayer dataLayer=new DataLayer();
					dataLayer.deleteAlert(obj.getAlertID());
			    }})
			 .setNegativeButton(android.R.string.no, null).show();
		}
	});
	   
	   }
	   else {
	    holder = (ViewHolder) convertView.getTag();
	   }
	  
	   Alert obj = alertsList.get(position);
	  
	   holder.objectName.setText(obj.getObjectName());
	   holder.objectName.setTextColor(Color.WHITE);
	   holder.objectName.setTag(obj);	
	   holder.location.setText(obj.getLocation());
	   holder.location.setTextColor(Color.WHITE);
	   holder.location.setTag(obj);
	   holder.time.setText(obj.getTime());
	   holder.time.setTextColor(Color.WHITE);
	   holder.time.setTag(obj);
	   holder.delete.setTag(obj);
	   return convertView;	  
	  }	  
}