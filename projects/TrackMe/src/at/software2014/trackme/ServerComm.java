package at.software2014.trackme;

import java.io.IOException;
import java.util.List;

import android.location.Location;
import android.os.AsyncTask;
import at.software2014.trackme.userdataendpoint.Userdataendpoint;
import at.software2014.trackme.userdataendpoint.Userdataendpoint.GetUserData;
import at.software2014.trackme.userdataendpoint.model.UserData;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;

public class ServerComm {

	private Userdataendpoint mUserEndpoint;

	public ServerComm()
	{
		Userdataendpoint.Builder endpointBuilder = new Userdataendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				new HttpRequestInitializer() {
					public void initialize(HttpRequest httpRequest) {
					}
				});

		mUserEndpoint = CloudEndpointUtils.updateBuilder(endpointBuilder)
				.build();
	}


	public interface AsyncCallback<T>
	{
		void onSuccess(T response);
		void onFailure(Exception failure); 
	}


	public void registerOwnUser(final String email, final String name, final AsyncCallback<Void> onRegisterCompleteCallback) {
		new AsyncTask<Void,Void,Void>() {
			Exception mException; 

			@Override
			protected Void doInBackground(Void... params) {
				try {
					registerOwnUserSync(email, name);
				} catch (IOException e) {
					mException = e; 
				} 
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if(mException == null) {
					onRegisterCompleteCallback.onSuccess(result); 
				}
				else {
					onRegisterCompleteCallback.onFailure(mException); 
				}
			}

		}.execute((Void)null); 
	}

	
	private void registerOwnUserSync(String email, String name) throws IOException {	
		mUserEndpoint.register(email, name).execute();		
	}

	
	public void updateOwnLocation(final String ownEmail, final Location location, final AsyncCallback<Void> onLocationUpdateCompleteCallback) {

		new AsyncTask<Void,Void,Void>() {
			Exception mException; 

			@Override
			protected Void doInBackground(Void... params) {
				try {
					updateOwnLocationSync(ownEmail, location);
				} catch (IOException e) {
					mException = e; 
				} 
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if(mException == null) {
					onLocationUpdateCompleteCallback.onSuccess(result); 
				}
				else {
					onLocationUpdateCompleteCallback.onFailure(mException); 
				}
			}

		}.execute((Void)null);
	}
	

	private void updateOwnLocationSync(String ownEmail, Location location) throws IOException {
		mUserEndpoint.updateLocation(ownEmail, location.getLatitude(), location.getLongitude(), location.getTime());
	}

	
	public void addAllowedUser(final String ownEmail, final String userEmail, final AsyncCallback<Void> onAddAllowedUserCompleteCallback) {

		new AsyncTask<Void,Void,Void>() {
			Exception mException; 

			@Override
			protected Void doInBackground(Void... params) {
				try {
					addAllowedUserSync(ownEmail, userEmail);
				} catch (IOException e) {
					mException = e; 
				} 
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if(mException == null) {
					onAddAllowedUserCompleteCallback.onSuccess(result); 
				}
				else {
					onAddAllowedUserCompleteCallback.onFailure(mException); 
				}
			}

		}.execute((Void)null);
	}
	
	
	private void addAllowedUserSync(String ownEmail, String userEmail) throws IOException {
		mUserEndpoint.addAllowedUser(ownEmail, userEmail);
	}
	
	
	public void deleteAllowedUser(final String ownEmail, final String userEmail, final AsyncCallback<Void> onDeleteAllowedUserCompleteCallback) {

		new AsyncTask<Void,Void,Void>() {
			Exception mException; 

			@Override
			protected Void doInBackground(Void... params) {
				try {
					deleteAllowedUserSync(ownEmail, userEmail);
				} catch (IOException e) {
					mException = e; 
				} 
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if(mException == null) {
					onDeleteAllowedUserCompleteCallback.onSuccess(result); 
				}
				else {
					onDeleteAllowedUserCompleteCallback.onFailure(mException); 
				}
			}

		}.execute((Void)null);
	}
	
	
	private void deleteAllowedUserSync(String ownEmail, String userEmail) throws IOException {
		
		UserData ud = mUserEndpoint.getUserData(ownEmail).execute();
		List<String> users = ud.getAllowedUsersForQuerying();
		
		if(users.contains(userEmail)) {			
			mUserEndpoint.removeUserData(userEmail);
		}
	}

	public void getAllowedUsers(final String ownEmail, final AsyncCallback<Void> onGetAllowedUsersCompleteCallback) {

		new AsyncTask<Void,Void,Void>() {
			Exception mException; 

			@Override
			protected Void doInBackground(Void... params) {
				try {
					getAllowedUsersSync(ownEmail);
				} catch (IOException e) {
					mException = e; 
				} 
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if(mException == null) {
					onGetAllowedUsersCompleteCallback.onSuccess(result); 
				}
				else {
					onGetAllowedUsersCompleteCallback.onFailure(mException); 
				}
			}

		}.execute((Void)null);
	}
	
	
	private List<UserData> getAllowedUsersSync(String ownEmail) throws IOException {

		UserData ud = mUserEndpoint.getUserData(ownEmail).execute();
		List<String> allowedUsers = ud.getAllowedUsersForQuerying();

		// TODO: fix problem with list
		// List<UserData> users = mUserEndpoint.getUserDataList(allowedUsers).execute();
		//return users;
		return null;
	}

	
	// TODO: implement query
	private void getAllRegisteredUsersSync() {
		
	}
	
	
	// TODO: needed?
	private void updateAllowedUsersLocation(List<String> userEmails) {

	}
	
	
	// TODO: needed?
	private void updateUserLocation(String userEmail) {
		
	}
}
