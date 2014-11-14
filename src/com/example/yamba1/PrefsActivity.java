package com.example.yamba1;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class PrefsActivity extends PreferenceActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater(); //get the menu from the context
		inflater.inflate(R.menu.menu, menu);	//use the object inflater to inflate the menu from XML
		return true; // to show the menu should return true
	}
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
