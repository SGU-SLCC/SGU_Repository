package com.sdg.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DataBaseAccess {
	SQLiteDatabase dataBase;
	FeedReaderDbHelper mDbHelper;

	//Constructor
	public DataBaseAccess(Context c){
		mDbHelper = new FeedReaderDbHelper(c);
		dataBase = mDbHelper.getWritableDatabase();
	}
	
	//Create Table Query
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + DataBaseAccess.FeedEntry.TABLE_NAME + " (" +
	    		DataBaseAccess.FeedEntry.COLUMN_NAME_ALERT_ID + TEXT_TYPE + COMMA_SEP +
	    		
	    		DataBaseAccess.FeedEntry.COLUMN_NAME_OBJECT_NAME + TEXT_TYPE + COMMA_SEP +	
	    		
	    		DataBaseAccess.FeedEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +	
	    
	    		DataBaseAccess.FeedEntry.COLUMN_NAME_DATETIME + TEXT_TYPE + 
	    
	    " )";

	//Drop table query
	private static final String SQL_DELETE_ENTRIES =
	    "DROP TABLE IF EXISTS " + DataBaseAccess.FeedEntry.TABLE_NAME;
	
	//Inserting data
	public void InsertValues(String objectName,String location, String dateTime){		
		ContentValues values = new ContentValues();
		final String SQL_STATEMENTCHECK = "SELECT "+DataBaseAccess.FeedEntry.COLUMN_NAME_ALERT_ID+" FROM "+DataBaseAccess.FeedEntry.TABLE_NAME
				+" WHERE "+DataBaseAccess.FeedEntry.COLUMN_NAME_OBJECT_NAME+" LIKE '%"+objectName+"%' AND "
				+DataBaseAccess.FeedEntry.COLUMN_NAME_LOCATION+" LIKE '%"+location+"%'";
		Cursor c = dataBase.rawQuery(SQL_STATEMENTCHECK, null);
		if(c.getCount()==0){//there are no identical alerts inserted before
			final String SQL_STATEMENT = "SELECT "+DataBaseAccess.FeedEntry.COLUMN_NAME_ALERT_ID+" FROM "+DataBaseAccess.FeedEntry.TABLE_NAME;
			Cursor cursor = dataBase.rawQuery(SQL_STATEMENT, null);
			int lastID=0;
			if(cursor.getCount()!=0){//There are already added alerts, this is not the first
				cursor.moveToLast();
			lastID = cursor.getInt(0);
			}
	
			values.put(DataBaseAccess.FeedEntry.COLUMN_NAME_ALERT_ID, lastID+1);
			values.put(DataBaseAccess.FeedEntry.COLUMN_NAME_OBJECT_NAME, objectName);
			values.put(DataBaseAccess.FeedEntry.COLUMN_NAME_LOCATION, location);
			values.put(DataBaseAccess.FeedEntry.COLUMN_NAME_DATETIME, dateTime);
			// Insert the new row, returning the primary key value of the new row
			long newRowId=dataBase.insert(DataBaseAccess.FeedEntry.TABLE_NAME,null, values);	
		}
	}
	
	//Data required for retrieval
	String[] projection = {
			DataBaseAccess.FeedEntry.COLUMN_NAME_ALERT_ID,
			DataBaseAccess.FeedEntry.COLUMN_NAME_OBJECT_NAME,
			DataBaseAccess.FeedEntry.COLUMN_NAME_LOCATION,
			DataBaseAccess.FeedEntry.COLUMN_NAME_DATETIME
		    };
	
	//Get Data
	public Cursor GetData()	{
		Cursor c = dataBase.query(
			    DataBaseAccess.FeedEntry.TABLE_NAME,  // The table to query
			    projection,                               // The columns to return
			    null,                                // The columns for the WHERE clause
			    null,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    null                                 // The sort order
			    );
		return c;			
	}
	
	 public void deleteAll(){
		 int n=dataBase.delete(DataBaseAccess.FeedEntry.TABLE_NAME, "1", null);
	   }
	 
	 public void deleteAlert(String ID){
		 int n=dataBase.delete(DataBaseAccess.FeedEntry.TABLE_NAME, DataBaseAccess.FeedEntry.COLUMN_NAME_ALERT_ID+"=?", new String[] { ID });
	   }
	
	//Table Columns
	public static abstract class FeedEntry implements BaseColumns {
		public static final String TABLE_NAME = "Alerts";
	    public static final String COLUMN_NAME_ALERT_ID = "ID";
	    public static final String COLUMN_NAME_OBJECT_NAME = "name";
	    public static final String COLUMN_NAME_LOCATION = "location";
	    public static final String COLUMN_NAME_DATETIME = "dateTime";
	}
			 
	public class FeedReaderDbHelper extends SQLiteOpenHelper {
	    // If you change the database schema, you must increment the database version.
	    public static final int DATABASE_VERSION = 1;
	    public static final String DATABASE_NAME = "Alert.db";

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

