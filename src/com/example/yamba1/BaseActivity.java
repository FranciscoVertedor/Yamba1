package com.example.yamba1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


/**
 * 
 * @author Francis
 * The base activity with common features shared by TimelineActivity and StatusActivity
 */
public class BaseActivity extends Activity{
	YambaApplication yamba;
	
	/*When is called this activity get the application object*/
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		yamba = (YambaApplication) getApplication();//return the application that owns YambaApplication
	}
	
	//called only the first time is clicked
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	//called every time user clicks on a menu item
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.itemPrefs:
				startActivity(new Intent(this,PrefsActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
			case R.id.itemRefresh:
				startService(new Intent(this,UpdaterService.class));
			break;
			case R.id.itemPurge:
				/*((YambaApplication)getApplication).getStatusData().delete();
				Toast.makeText(this, R.string.msgAllDataPurged, Toast.LENGTH_LONG).show();*/
			break;
			case R.id.itemTimeLine:
				startActivity(new Intent(this,TimelineActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
			case R.id.itemStatus:
				startActivity(new Intent(this, StatusActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		}
		return true;
	}
}
