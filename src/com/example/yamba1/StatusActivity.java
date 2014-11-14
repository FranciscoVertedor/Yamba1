package com.example.yamba1;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends BaseActivity implements OnClickListener, TextWatcher, LocationListener {

	private static final String TAG = "StatusActivity";
	private static final long LOCATION_MIN_TIME=3600000;
	private static final float LOCATION_MIN_DISTANCE= 1000;
	EditText editText;
	Button updateButton;
	Twitter twitter;
	TextView textCount;
	SharedPreferences prefs;
	LocationManager locationManager;
	Location location;
	String provider;
	YambaApplication yamba;
	
	//Called first time user clicks on the menu button
		@Override
		public boolean onCreateOptionsMenu(Menu menu){
			MenuInflater inflater = getMenuInflater(); //get the menu from the context
			inflater.inflate(R.menu.menu, menu);	//use the object inflater to inflate the menu from XML
			return true; // to show the menu should return true
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item){
			switch(item.getItemId()){
				/*case R.id.itemServiceStart:
					startService(new Intent(this,UpdaterService.class));
				break;
				case R.id.itemServiceStop:
					stopService(new Intent(this,UpdaterService.class));
				break;*/
				case R.id.itemPrefs:
					startActivity(new Intent(this,PrefsActivity.class));
				break;
				case R.id.itemTimeLine:
					startActivity(new Intent(this,TimelineActivity.class));
				break;
			}
			return true;
		}
	/* Called when the activity is first created*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status); //load template layout/status.xml
		
		//Find Views
		editText = (EditText)findViewById(R.id.editText);
		updateButton = (Button)findViewById(R.id.buttonUpdate);
		updateButton.setOnClickListener(this);
		
		textCount = (TextView) findViewById(R.id.textCount);
		textCount.setText(Integer.toString(140));
		textCount.setTextColor(Color.GREEN);

		editText.addTextChangedListener(this);
		yamba = (YambaApplication) getApplication();
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		// Setup location information
		provider = yamba.getProvider();
		if(!YambaApplication.LOCATION_PROVIDER_NONE.equals(provider)){
			locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
		}else{
			locationManager = null;
			location = null;
		}
		if(locationManager != null){
			location = locationManager.getLastKnownLocation(provider);
			locationManager.requestLocationUpdates(provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if(locationManager != null){
			locationManager.removeUpdates(this);
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String status = editText.getText().toString();
		new PostToTwitter().execute(status);
		
		Log.d(TAG, "onClicked");
	}
	
	// Asynchronously posts to twitter
			class PostToTwitter extends AsyncTask<String, Integer, String>{
				
				//Called to initiate the background activity
				@Override
				protected String doInBackground(String... statuses) {
					// TODO Auto-generated method stub
					try{
						if(location != null){
							double latlong[] = {location.getLatitude(), location.getLongitude()};
							yamba.getTwitter().setMyLocation(latlong);
						}
						YambaApplication yamba = (YambaApplication) getApplication();
						Twitter.Status status = yamba.getTwitter().updateStatus(statuses[0]);
						return status.text;
					}catch(TwitterException e){
						Log.e(TAG, e.toString());
						e.printStackTrace();
						return "Failed to post";
					}
				}
				//Called when there's a status to be updated
				@Override
				protected void onProgressUpdate(Integer... values){
					super.onProgressUpdate(values);
					// Not used in this case
				}
				
				//Called once the background activity has completed
				@Override
				protected void onPostExecute(String result){
					Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void afterTextChanged(Editable statusText) {
				// TODO Auto-generated method stub
				int count = 140 - statusText.length();
				textCount.setText(Integer.toString(count));
				textCount.setTextColor(Color.GREEN);
				if(count < 10){
					textCount.setTextColor(Color.YELLOW);
				}
				if(count<0){
					textCount.setTextColor(Color.RED);
				}
			}
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				this.location = location;
			}
			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				if(this.provider.equals(provider)){
					locationManager.requestLocationUpdates(this.provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
				}
			}
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				if(this.provider.equals(provider)){
					locationManager.removeUpdates(this);
				}
			}
}
