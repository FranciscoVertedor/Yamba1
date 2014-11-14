package com.example.yamba1;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter {
	
	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	SimpleCursorAdapter adapter;
	static final String[] FROM = { DbHelper.C_CREATED_AT, DbHelper.C_USER, DbHelper.C_TEXT };
	static final int[] TO = { R.id.textCreateAt, R.id.textUser, R.id.textText };
	TextView textTimeLine;
	
	public TimelineAdapter(Context context, Cursor c) {
		super(context, R.layout.row, c, FROM, TO);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void bindView(View row, Context context, Cursor cursor){
		super.bindView(row, context, cursor);
		
		//Manually bind created at timestamp to its view
		long timestamp = cursor.getLong(cursor.getColumnIndex(DbHelper.C_CREATED_AT));
		TextView textCreatedAt = (TextView)row.findViewById(R.id.textCreateAt);
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp)); 
	}
	
}
