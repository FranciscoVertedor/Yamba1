package com.example.yamba1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


public class StatusData {
	private static final String TAG = StatusData.class.getSimpleName();
	
	static final int VERSION = 1;
	static final String DATABASE = "timeline.sqlite";
	static final String TABLE = "timeline";
	static final String C_ID = BaseColumns._ID;
	static final String C_CREATED_AT = "created_at";
	static final String C_SOURCE = "source";
	static final String C_TEXT = "txt";
	static final String C_USER = "user";
	
	private static final String GET_ALL_ORDER_BY = C_CREATED_AT + " DESC";
	private static final String[] MAX_CREATED_AT_COLUMNS = {"max("
		+ StatusData.C_CREATED_AT + ")"};
	private static final String[] DB_TEXT_COLUMNS = { C_TEXT };
	
	//Constructor
	public StatusData(Context context){ //3
		this.dbHelper = new DbHelper(context);
		Log.i(TAG,"Initialized data");
	}	
	public void close(){//4
		this.dbHelper.close();
	}
	public void insertOrIgnore(ContentValues values){//5
		Log.d(TAG, "insertOrIgnore on " + values);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();//6
		try{
			db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);//7
		}finally{
			db.close();//8
		}
	}
	
	/**
	 * 
	 *  @return Cursor where the columns are _id, created_at, user, txt
	 */
	
	public Cursor getStatusUpdates(){//9
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(TABLE,null,null,null,null,null, GET_ALL_ORDER_BY);
	}
	/**
	 * 
	 *  @return Timestamp of the latest status we have it the database
	 */
	public long getLatestStatusCreateAtTime(){//10
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try{
			Cursor cursor = db.query(TABLE, MAX_CREATED_AT_COLUMNS, null,null,null,null,null);
			try{
				return cursor.moveToNext() ? cursor.getLong(0) : Long.MIN_VALUE;
			}finally{
				cursor.close();
			}
		}finally{
			db.close();
		}
	}
	/**
	 * 
	 *  @param id of the status we are looking for
	 *  @return text of the status
	 */
	public String getStatusTextById(Long id){//11
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try{
			Cursor cursor = db.query(TABLE, DB_TEXT_COLUMNS, C_ID + "=" + id, null, null, null, null);
			try{
				return cursor.moveToNext() ? cursor.getString(0) : null;
			}finally{
				cursor.close();
			}
		}finally{
			db.close();
		}
	}
		class DbHelper extends SQLiteOpenHelper{
			public DbHelper(Context context) {
				super(context,DATABASE, null, VERSION);
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
	
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				// TODO Auto-generated method stub
				db.execSQL("drop talbe " + TABLE);
				this.onCreate(db);
			}
		}
		final DbHelper dbHelper; // only is created once
		
		
}
