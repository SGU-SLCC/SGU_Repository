package com.sdg.etspandroidtracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class Messages {

	//-------------------------------------------------Error message----------------------------------------------
	
	public static void showError(String title, String msg,Context context) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(msg);

		// Setting Icon to Dialog
		alertDialog.setIcon(R.drawable.alert);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
}
