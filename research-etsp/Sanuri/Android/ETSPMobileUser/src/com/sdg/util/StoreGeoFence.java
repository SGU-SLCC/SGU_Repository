package com.sdg.util;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.Toast;

public class StoreGeoFence {
	SQLiteDatabase dataBase;
	FeedReaderDbHelper mDbHelper;
	private Intent serviceIntent;
	private Context baseContext;

	//Constructor
	public StoreGeoFence(Context c){
		mDbHelper = new FeedReaderDbHelper(c);
		dataBase = mDbHelper.getWritableDatabase();
		baseContext=c;
	}
	
	//Create Table Query
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + StoreGeoFence.FeedEntry.TABLE_NAME + " (" +
	    		StoreGeoFence.FeedEntry.COLUMN_NAME_FENCE_ID + TEXT_TYPE + COMMA_SEP +
	    		
	    		StoreGeoFence.FeedEntry.COLUMN_NAME_PATH + TEXT_TYPE + COMMA_SEP +	
	    		
	    		StoreGeoFence.FeedEntry.COLUMN_NAME_ISINSIDE + TEXT_TYPE + COMMA_SEP +	
	    
	    		StoreGeoFence.FeedEntry.COLUMN_NAME_OBJECTS + TEXT_TYPE + 
	    
	    " )";

	//Drop table query
	private static final String SQL_DELETE_ENTRIES =
	    "DROP TABLE IF EXISTS " + StoreGeoFence.FeedEntry.TABLE_NAME;
	
	//Inserting data
	public void InsertValues(String path,Boolean isInside, String objects){		
		ContentValues values = new ContentValues();
		
		final String SQL_STATEMENT = "SELECT "+StoreGeoFence.FeedEntry.COLUMN_NAME_FENCE_ID+" FROM "+StoreGeoFence.FeedEntry.TABLE_NAME;
		Cursor c = dataBase.rawQuery(SQL_STATEMENT, null);
		int lastID=0;
		if(c.getCount()!=0){
		c.moveToLast();
		lastID = c.getInt(0);
		}

		values.put(StoreGeoFence.FeedEntry.COLUMN_NAME_FENCE_ID, lastID+1);
		values.put(StoreGeoFence.FeedEntry.COLUMN_NAME_PATH, path);
		values.put(StoreGeoFence.FeedEntry.COLUMN_NAME_ISINSIDE, isInside);
		values.put(StoreGeoFence.FeedEntry.COLUMN_NAME_OBJECTS, objects);
		// Insert the new row, returning the primary key value of the new row
		long newRowId=dataBase.insert(StoreGeoFence.FeedEntry.TABLE_NAME,null, values);	
	}


	//Data required for retrieval
	String[] projection = {
			StoreGeoFence.FeedEntry.COLUMN_NAME_FENCE_ID,
			StoreGeoFence.FeedEntry.COLUMN_NAME_PATH,
			StoreGeoFence.FeedEntry.COLUMN_NAME_ISINSIDE,
			StoreGeoFence.FeedEntry.COLUMN_NAME_OBJECTS
		    };
	
	//Get Data
	public Cursor GetData()	{
		Cursor c = dataBase.query(
			    StoreGeoFence.FeedEntry.TABLE_NAME,  // The table to query
			    projection,                               // The columns to return
			    null,                                // The columns for the WHERE clause
			    null,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    null                                 // The sort order
			    );
		return c;			
	}
	
	public Cursor GetGeofencesOfObject(String objectID)	{
		final String SQL_STATEMENT = "SELECT * FROM "+StoreGeoFence.FeedEntry.TABLE_NAME+ " WHERE "+StoreGeoFence.FeedEntry.COLUMN_NAME_OBJECTS+" LIKE '%"+objectID+"%'";
		Cursor c = dataBase.rawQuery(SQL_STATEMENT, null);
		return c;			
	}
	
	 public void deleteAll(){
		 int n=dataBase.delete(StoreGeoFence.FeedEntry.TABLE_NAME, "1", null);
	   }

	public void deleteGeoFence(String ID){
		int n=dataBase.delete(StoreGeoFence.FeedEntry.TABLE_NAME, StoreGeoFence.FeedEntry.COLUMN_NAME_FENCE_ID+"=?", new String[] { ID });
		final String SQL_STATEMENT = "SELECT "+StoreGeoFence.FeedEntry.COLUMN_NAME_FENCE_ID+" FROM "+StoreGeoFence.FeedEntry.TABLE_NAME;
		Cursor c = dataBase.rawQuery(SQL_STATEMENT, null);
		if(c.getCount()==0){
		}
	   }
	
	//Table Columns
	public static abstract class FeedEntry implements BaseColumns {
		public static final String TABLE_NAME = "GeoFences";
	    public static final String COLUMN_NAME_FENCE_ID = "ID";
	    public static final String COLUMN_NAME_PATH = "path";
	    public static final String COLUMN_NAME_ISINSIDE = "isInside";
	    public static final String COLUMN_NAME_OBJECTS = "objects";
	}
			 
	public class FeedReaderDbHelper extends SQLiteOpenHelper {
	    // If you change the database schema, you must increment the database version.
	    public static final int DATABASE_VERSION = 1;
	    public static final String DATABASE_NAME = "GeoFence.db";

	    //Constructor
	    public FeedReaderDbHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	   

		//Create table when an object of this class is created
	    public void onCreate(SQLiteDatabase db) {
	    	db.execSQL(SQL_CREATE_ENTRIES);
	    }
	    
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // This database is only a cache for online data, so its upgrade policy is
	        // to simply to discard the data and start over
	        db.execSQL(SQL_DELETE_ENTRIES);
	        onCreate(db);
	    }
	    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        onUpgrade(db, oldVersion, newVersion);
	    }
	}
	
	//Close database operations
	public void closeDataBase()	{
		mDbHelper.close();
	}
	
	
}
