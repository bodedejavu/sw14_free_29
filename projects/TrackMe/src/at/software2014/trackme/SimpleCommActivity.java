package at.software2014.trackme;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import at.software2014.trackme.ServerComm.AsyncCallback;
import at.software2014.trackme.userdataendpoint.model.UserData;

public class SimpleCommActivity extends Activity {

	
	private ServerComm mServerInterface;
	private Location mLocation;
	private Location mLocation2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.server_comm_test);
		
		mServerInterface = new ServerComm();
		mLocation = new Location("fused");
		mLocation.setLatitude(47.067684);
		mLocation.setLongitude(15.438403);
		mLocation.setTime(new Date().getTime());
		mLocation2 = new Location("fused");
		mLocation2.setLatitude(47.060000);
		mLocation2.setLongitude(15.430000);
		mLocation2.setTime(new Date().getTime());
		
		Button register = (Button) findViewById(R.id.btn1);
		Button register2 = (Button) findViewById(R.id.btn12);
		Button sendLocation = (Button) findViewById(R.id.btn2);
		Button sendLocation2 = (Button) findViewById(R.id.btn22);
		Button getRegisteredUsers = (Button) findViewById(R.id.btn3);
		Button addAllowedUser = (Button) findViewById(R.id.btn4);
		Button getAllowedUsers = (Button) findViewById(R.id.btn5);


		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				mServerInterface.registerOwnUser("trude@trackertown.com", "Trude", new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void response) {
						Toast.makeText(SimpleCommActivity.this, "Registration successful", Toast.LENGTH_SHORT).show(); 
						
					}
					
					@Override
					public void onFailure(Exception failure) {
						Toast.makeText(SimpleCommActivity.this, "Registration failed " + failure.getMessage(), Toast.LENGTH_SHORT).show(); 
						
					}
				}); 
			}
		});
		
		register2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				mServerInterface.registerOwnUser("jakob@trackertown.com", "Jakob", new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void response) {
						Toast.makeText(SimpleCommActivity.this, "Registration successful", Toast.LENGTH_SHORT).show(); 
						
					}
					
					@Override
					public void onFailure(Exception failure) {
						Toast.makeText(SimpleCommActivity.this, "Registration failed " + failure.getMessage(), Toast.LENGTH_SHORT).show(); 
						
					}
				}); 
			}
		});
		
		
		sendLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				mServerInterface.updateOwnLocation("trude@trackertown.com", mLocation, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void response) {
						Toast.makeText(SimpleCommActivity.this, "Location successful", Toast.LENGTH_SHORT).show(); 
						
					}
					
					@Override
					public void onFailure(Exception failure) {
						Toast.makeText(SimpleCommActivity.this, "Location failed " + failure.getMessage(), Toast.LENGTH_SHORT).show(); 
						
					}
				}); 
			}
		});
		
		sendLocation2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				mServerInterface.updateOwnLocation("jakob@trackertown.com", mLocation2, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void response) {
						Toast.makeText(SimpleCommActivity.this, "Location successful", Toast.LENGTH_SHORT).show(); 
						
					}
					
					@Override
					public void onFailure(Exception failure) {
						Toast.makeText(SimpleCommActivity.this, "Location failed " + failure.getMessage(), Toast.LENGTH_SHORT).show(); 
						
					}
				}); 
			}
		});

		getRegisteredUsers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				mServerInterface.getRegisteredUsers(new AsyncCallback<List<UserData>>() {
					
					@Override
					public void onSuccess(List<UserData> response) {
						
						for(UserData data : response) {
							Log.d("Registered User", data.getUserName() + " " + data.getUserEmail() + " " + data.getUserLastLatitude() + " " + data.getUserLastLongitude());
						}
						
						Toast.makeText(SimpleCommActivity.this, "Users successful", Toast.LENGTH_SHORT).show(); 
						
					}
					
					@Override
					public void onFailure(Exception failure) {
						Toast.makeText(SimpleCommActivity.this, "Users failed " + failure.getMessage(), Toast.LENGTH_SHORT).show(); 
						
					}
				}); 
			}
		});
		
		addAllowedUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				mServerInterface.addAllowedUser("trude@trackertown.com", "jakob@trackertown.com", new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void response) {
						Toast.makeText(SimpleCommActivity.this, "Allowed user successful", Toast.LENGTH_SHORT).show(); 
						
					}
					
					@Override
					public void onFailure(Exception failure) {
						Toast.makeText(SimpleCommActivity.this, "Allowed user failed " + failure.getMessage(), Toast.LENGTH_SHORT).show(); 
						
					}
				}); 
			}
		});
		
		
		getAllowedUsers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				mServerInterface.getAllowedUsers("trude@trackertown.com", new AsyncCallback<List<UserData>>() {
					
					@Override
					public void onSuccess(List<UserData> response) {
						
						for(UserData data : response) {
							Log.d("Allowed User", data.getUserName() + " " + data.getUserEmail() + " " + data.getUserLastLatitude() + " " + data.getUserLastLongitude());
						}
						
						Toast.makeText(SimpleCommActivity.this, "Successful", Toast.LENGTH_SHORT).show(); 
						
					}
					
					@Override
					public void onFailure(Exception failure) {
						Toast.makeText(SimpleCommActivity.this, "Failed " + failure.getMessage(), Toast.LENGTH_SHORT).show(); 
						
					}
				}); 
			}
		});
		
	}



}
