package com.example.yamba1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper{
	static final String TAG = "DbHelper";
	static final String DB_NAME = "timeline.sqlite"; //database filename
	static final int DB_VERSION = 1; 			// version database
	//Database constants to access from another class
	static final String TABLE = "timeline";
	static final String C_ID = BaseColumns._ID;
	static final String C_CREATED_AT = "created_at";
	static final String C_SOURCE = "source";
	static final String C_TEXT = "txt";
	static final String C_USER = "user";
	Context context;
	
	//Constructor
	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}
	
	// Called only once, first time the DB is created
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table " + TABLE + " (" + C_ID + " int primary key, "
				+ C_CREATED_AT + " int, " + C_SOURCE + " text, " + C_USER + " text, " + C_TEXT + " text)";
		
		db.execSQL(sql);
		Log.d(TAG, "onCreated sql: " + sql);
	}
	
	// Called whenever newVersion != oldVersion
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//Typically do ALTER TABLE statements, but... we're just in development, 
		//so:
		
		db.execSQL("drop table if exists " + TABLE);//drop the old database
		Log.d(TAG, "onUpdated");
		onCreate(db); // run onCreate to get new database
		
	}

}
