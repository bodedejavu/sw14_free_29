package at.software2014.trackme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import at.software2014.trackme.ServerComm.AsyncCallback;
import at.software2014.trackme.userdataendpoint.Userdataendpoint;

public class SimpleCommActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Button sendButton = new Button(this);

		setContentView(sendButton);
		sendButton.setText("Send");
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new ServerComm().registerOwnUser("korl@trackertown.com", "Korl", new AsyncCallback<Void>() {
					
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

	}



}
