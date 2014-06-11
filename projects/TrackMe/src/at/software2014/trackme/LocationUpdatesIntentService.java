package at.software2014.trackme;

import com.google.android.gms.location.LocationClient;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import at.software2014.trackme.ServerComm.AsyncCallback;

public class LocationUpdatesIntentService extends IntentService {
	
	public LocationUpdatesIntentService() {
		super("LocationUpdatesService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		if (intent.hasExtra(LocationClient.KEY_LOCATION_CHANGED)) {
			
            Bundle bundle = intent.getExtras();
            String eMail = bundle.getString("eMail");
            
            if (eMail != "") {
                Location currentLocation = (Location) bundle.get(LocationClient.KEY_LOCATION_CHANGED);
            	
                ServerComm servercomm = new ServerComm();
                
                servercomm.updateOwnLocation(eMail, currentLocation, new AsyncCallback<Void>() {
    				
    				@Override
    				public void onSuccess(Void response) {
    					Log.d("SERVICE", "updateOwnLocation successfully");
    				}
    				
    				@Override
    				public void onFailure(Exception failure) {
    					Log.d("SERVICE", "updateOwnLocation not successfully");
    				}
    			});
            }
        }

	}

}
