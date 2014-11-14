package com.example.yamba1;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends IntentService{
	private static final String TAG = "UpdaterService";
	
	static final int DELAY = 6000; // a minute
	private boolean runflag = false;
	private Updater updater;
	private YambaApplication yamba;
	DbHelper dbHelper;
	SQLiteDatabase db;
	public static final String NEW_STATUS_INTENT = "com.example.yamba1.NEW_STATUS";
	public static final String NEW_STATUS_EXTRA_COUNT = "NEW_STATUS_EXTRA_COUNT";
	static final String RECEIVE_TIMELINE_NOTIFICATIONS = "com.example.yamba1.RECEIVE_TIMELINE_NOTIFICATIONS";
	private Notification notification;
	private NotificationManager notificationManager;
	
	public UpdaterService(){
		super(TAG);
		Log.d(TAG,"UpdaterService constructed");
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate(){//Only is called when the service is initially created
		super.onCreate();
		this.yamba = (YambaApplication) getApplication();
		this.updater = new Updater();
		
		dbHelper = new DbHelper(this);
		
		Log.d(TAG,"onCreated");
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){//is called each time the service recives a startService() intent
		super.onStartCommand(intent, flags, startId);
		if(!runflag){
			this.runflag = true;
			this.updater.start();
			this.yamba.setServiceRunning(true);
			Log.d(TAG,"onStarted");
		}
		return START_STICKY;//is used as a flag to indicate this service is started and stopped explicitly
	}
	@Override
	public void onDestroy(){//is called just before the service is destroyed by the stopService() request
		super.onDestroy();
		this.runflag = false;
		this.updater.interrupt();
		this.updater = null;
		this.yamba.setServiceRunning(false);
		Log.d(TAG,"onDestroyed");
	}
	
	/**
	 * Thread that performs the actual update from the online service
	 */
	private class Updater extends Thread{
		
		List<Twitter.Status> timeline;
		Intent intent;
		public Updater(){
			super("UpdateService-Updater");
		}
		@Override
		public void run(){
			UpdaterService updaterService = UpdaterService.this;
			while (updaterService.runflag){
				Log.d(TAG,"Runing background thread");
				try{
						YambaApplication yamba = (YambaApplication) updaterService.getApplication(); //
						int newUpdates = yamba.fetchStatusUpdates(); //
						if (newUpdates > 0) { //
							Log.d(TAG, "We have a new status");
							intent = new Intent(NEW_STATUS_INTENT);
							intent.putExtra(NEW_STATUS_EXTRA_COUNT, newUpdates);
							updaterService.sendBroadcast(intent, RECEIVE_TIMELINE_NOTIFICATIONS);
						}
						Thread.sleep(DELAY);
				}catch(InterruptedException e){
					updaterService.runflag = false;
				}
			}
		}
	}//Updater

	@Override
	protected void onHandleIntent(Intent inIntent) {
		// TODO Auto-generated method stub
		Intent intent;
		Log.d(TAG,"onHandleIntent'ing");
		YambaApplication yamba = (YambaApplication) getApplication(); //
		int newUpdates = yamba.fetchStatusUpdates(); //
		if (newUpdates > 0) { //
			Log.d(TAG, "We have a new status");
			intent = new Intent(NEW_STATUS_INTENT);
			intent.putExtra(NEW_STATUS_EXTRA_COUNT, newUpdates);
			sendBroadcast(intent, RECEIVE_TIMELINE_NOTIFICATIONS);
			sendTimelineNotification(newUpdates);
		}
		
	}
	/**
	* Creates a notification in the notification bar telling user there are new
	* messages
	*
	* @param timelineUpdateCount
	* Number of new statuses
	*/
	private void sendTimelineNotification(int timelineUpdateCount) {
		Log.d(TAG, "sendTimelineNotification'ing");
		PendingIntent pendingIntent = PendingIntent.getActivity(this, -1,
		new Intent(this, TimelineActivity.class),
		PendingIntent.FLAG_UPDATE_CURRENT); //
		this.notification.when = System.currentTimeMillis(); //
		this.notification.flags |= Notification.FLAG_AUTO_CANCEL; //
		CharSequence notificationTitle = this.getText(R.string.msgNotificationTitle); //
		CharSequence notificationSummary = this.getString(R.string.msgNotificationMessage, timelineUpdateCount);
		this.notification.setLatestEventInfo(this, notificationTitle,notificationSummary, pendingIntent); //
		this.notificationManager.notify(0, this.notification);
		Log.d(TAG, "sendTimelineNotificationed");
	}

}
