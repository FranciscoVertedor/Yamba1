package com.example.yamba1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;


//Listen if the network is available or not
public class NetworkReceiver extends BroadcastReceiver{
	public static final String TAG = "NetworkReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		boolean isNetworkDown = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);
		
		if(isNetworkDown){
			Log.d(TAG,"onReceive: NOT connected,  stopping UpdaterService");
			context.stopService(new Intent(context, UpdaterService.class));
		}else{
			Log.d(TAG,"onReceive:connected, starting UpdaterService");
			context.startService(new Intent(context, UpdaterService.class));
		}
	}

	
	

}
