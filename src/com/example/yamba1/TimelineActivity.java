package com.example.yamba1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity {

	Cursor cursor;
	SimpleCursorAdapter adapter;
	ListView listTimeline;
	static final String[] FROM = { DbHelper.C_CREATED_AT, DbHelper.C_USER, DbHelper.C_TEXT };
	static final int[] TO = { R.id.textCreateAt, R.id.textUser, R.id.textText };
	static final String SEND_TIMELINE_NOTIFICATIONS = "com.example.yamba1.SEND_TIMELINE_NOTIFICATIONS";
	IntentFilter filter;
	TimelineReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline_basic);
		filter = new IntentFilter("com.example.yamba1.NEW_STATUS");
		
		//find your views
		
		
		listTimeline = (ListView) findViewById(R.id.listTimeline);
		
		receiver = new TimelineReceiver();
		//yamba = new YambaApplication();
		/*if(yamba.getTwitter().getStatus("username") == null){
			startActivity(new Intent(this,PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPreferences, Toast.LENGTH_LONG).show();
		}*/
		registerReceiver(receiver,filter);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	@Override
	public void onPause(){
		super.onPause();
		unregisterReceiver(receiver);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		this.setupList();
		// Register the receiver
	    super.registerReceiver(receiver, filter,
	        SEND_TIMELINE_NOTIFICATIONS, null);
	}
	public void setupList(){
		//GET the data 
		cursor = yamba.getStatusData().getStatusUpdates();
		startManagingCursor(cursor);
		
		
		//setup Adapter
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		listTimeline.setAdapter(adapter);
	}
	//View binder constant to inject business logic that converts a timestamp to relative time
	static final ViewBinder VIEW_BINDER = new ViewBinder(){
		public boolean setViewValue(View view, Cursor cursor, int columnIndex){
			if(view.getId() != R.id.textCreateAt)
					return false;
			
			//Update the created at text to relative time
			long timestamp = cursor.getLong(columnIndex);
			CharSequence relTime = DateUtils.getRelativeTimeSpanString(view.getContext(), timestamp);
			((TextView) view).setText(relTime);
			return true;
		}
	};
	
	class TimelineReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			setupList();
			Log.d("TimelineReceiver","onReceived");
		}
		
	}
}
