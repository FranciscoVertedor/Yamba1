package com.example.yamba1;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class YambaApplication extends Application implements OnSharedPreferenceChangeListener {
	private static final String TAG = YambaApplication.class.getSimpleName();
	public static final String LOCATION_PROVIDER_NONE = "NONE";
	public static final long INTERVAL_NEVER = 0;
	private boolean serviceRunning;
	private StatusData statusData;
	SharedPreferences prefs;
	Twitter twitter;
	
	public long getInterval() {
		// For some reason storing interval as long doesn't work
		return Long.parseLong(prefs.getString("interval", "0"));
		}
	public String getProvider(){
		return prefs.getString("provider", LOCATION_PROVIDER_NONE);
	}
	
	public StatusData getStatusData(){
		if(statusData == null){
			statusData = new StatusData(this);
		}
		return statusData;
	}
	//Connects to the online service and puts the latest statuses into DB
	//Returns the count of new statuses
	public synchronized int fetchStatusUpdates(){
		Log.d(TAG,"Fetching status updates");
		Twitter twitter = this.getTwitter();
		if(twitter == null){
			Log.d(TAG, "Twitter connection info not initialized");
			return 0;
		}
		try {
				List<Status> statusUpdates = twitter.getFriendsTimeline();
				long latestStatusCreatedAtTime = this.getStatusData().getLatestStatusCreateAtTime();
				int count = 0;
				ContentValues values = new ContentValues();
				for (Status status : statusUpdates) {
					values.put(StatusData.C_ID, status.getId());
					long createdAt = status.getCreatedAt().getTime();
					values.put(StatusData.C_CREATED_AT, createdAt);
					values.put(StatusData.C_TEXT, status.getText());
					values.put(StatusData.C_USER, status.getUser().getName());
					Log.d(TAG, "Got update with id " + status.getId() + ". Saving");
					this.getStatusData().insertOrIgnore(values);
					if (latestStatusCreatedAtTime < createdAt) {
						count++;
					}
				}
				Log.d(TAG, count > 0 ? "Got " + count + " status updates" : "No new status updates");
				return count;
			} catch (RuntimeException e) {
				Log.e(TAG, "Failed to fetch status updates", e);
				return 0;
			}
	}
	
	
	/* Called when the activity is first created. */
	@Override
	public void onCreate() {
		super.onCreate();
		//Setup Preferences
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this); //shared preferences by various parts of this application
		this.prefs.registerOnSharedPreferenceChangeListener(this); //method to notify that the old values are stale
		Log.i(TAG,"onCreated");
	}
	@Override
	public void onTerminate(){
		super.onTerminate();
		Log.i(TAG,"onTerminated");
	}
	
	public synchronized Twitter getTwitter(){
		if(twitter == null){
			
			String username,password,apiRoot;
			
		    prefs = PreferenceManager.getDefaultSharedPreferences(this);
		    
			username = prefs.getString("username", "student");
			password = prefs.getString("password", "password");
			apiRoot = prefs.getString("apiRoot","http://yamba.marakana.com/api");
			
			if(!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(apiRoot)){
				//connect to twitter.com
				twitter = new Twitter(username,password);
				twitter.setAPIRootUrl(apiRoot);
			}
		}
		return this.twitter;
	}
	@Override
	public void onSharedPreferenceChanged(//this method is calling whenever preferences change
				SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		this.twitter = null;	
	}
	
	public boolean isServiceRunning(){
		return serviceRunning;
	}
	public void setServiceRunning(boolean serviceRunning){
		this.serviceRunning = serviceRunning;
	}
}