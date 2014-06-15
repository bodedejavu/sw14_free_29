package at.software2014.trackme;

import java.io.IOException;
import java.util.List;

import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import at.software2014.trackme.userdataendpoint.Userdataendpoint;
import at.software2014.trackme.userdataendpoint.model.UserData;
import at.software2014.trackme.userdataendpoint.model.UserDataCollection;

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


	public void registerOwnUser(final String email, final String name) {
		registerOwnUser(email, name, null);
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
					if(onRegisterCompleteCallback != null)	{
						onRegisterCompleteCallback.onSuccess(result); 
					}
				}
				else {
					if(onRegisterCompleteCallback != null)	{
						onRegisterCompleteCallback.onFailure(mException); 
					}
					else
					{
						mException.printStackTrace(); 
					}
				}
			}

		}.execute((Void)null); 
	}


	private void registerOwnUserSync(String email, String name) throws IOException {	
		isValidEmail(email);
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
		isValidEmail(ownEmail);
		mUserEndpoint.updateLocation(ownEmail, location.getLatitude(), location.getLongitude(), location.getTime()).execute();
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
		isValidEmail(ownEmail);
		mUserEndpoint.addAllowedUser(ownEmail, userEmail).execute();
	}

	public void removeAllowedUser(final String ownEmail, final String userEmail, final AsyncCallback<Void> onDeleteAllowedUserCallback) {

		new AsyncTask <Void, Void, Void>() {
			Exception mException;

			@Override
			protected Void doInBackground(Void... params) {

				try {
					removeAllowedUserSync(ownEmail, userEmail);
				} catch (IOException e){
					mException = e;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if(mException == null) {
					onDeleteAllowedUserCallback.onSuccess(result); 
				}
				else {
					onDeleteAllowedUserCallback.onFailure(mException); 
				}
			}
		}.execute((Void)null);
	}


	private void removeAllowedUserSync(String ownEmail, String userEmail) throws IOException {	
		isValidEmail(ownEmail);
		mUserEndpoint.removeAllowedUser(ownEmail, userEmail).execute();
	}


	public void unregisterOwnUser(final String ownEmail, final AsyncCallback<Void> onDeleteOwnUserCompleteCallback) {

		new AsyncTask<Void,Void,Void>() {
			Exception mException; 

			@Override
			protected Void doInBackground(Void... params) {
				try {
					unregisterOwnUserSync(ownEmail);
				} catch (IOException e) {
					mException = e; 
				} 
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if(mException == null) {
					onDeleteOwnUserCompleteCallback.onSuccess(result); 
				}
				else {
					onDeleteOwnUserCompleteCallback.onFailure(mException); 
				}
			}

		}.execute((Void)null);
	}


	private void unregisterOwnUserSync(String ownEmail) throws IOException {	
		isValidEmail(ownEmail);
		mUserEndpoint.removeUserData(ownEmail).execute();
	}


	public void getAllowedUsers(final String ownEmail, final AsyncCallback<List<UserData>> onGetAllowedUsersCompleteCallback) {

		new AsyncTask<Void,Void,List<UserData>>() {
			Exception mException; 

			@Override
			protected List<UserData> doInBackground(Void... params) {
				try {
					return getAllowedUsersSync(ownEmail);
				} catch (IOException e) {
					mException = e; 
				} 
				return null;
			}

			@Override
			protected void onPostExecute(List<UserData> result) {
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
		isValidEmail(ownEmail);
		UserDataCollection users = mUserEndpoint.getAllowedUsers(ownEmail).execute();
		return users.getItems();
	}


	public void getRegisteredUsers(final AsyncCallback<List<UserData>> onGetRegisteredUsersCompleteCallback) {

		new AsyncTask<Void,Void,List<UserData>>() {
			Exception mException; 

			@Override
			protected List<UserData> doInBackground(Void... params) {
				try {
					return getAllRegisteredUsersSync();
				} catch (IOException e) {
					mException = e; 
				} 
				return null;
			}

			@Override
			protected void onPostExecute(List<UserData> result) {
				if(mException == null) {
					onGetRegisteredUsersCompleteCallback.onSuccess(result); 
				}
				else {
					onGetRegisteredUsersCompleteCallback.onFailure(mException); 
				}
			}

		}.execute((Void)null);
	}


	private List<UserData> getAllRegisteredUsersSync() throws IOException {
		List<UserData> registeredUsers = mUserEndpoint.getRegisteredUsers().execute().getItems();
		return registeredUsers; 
	}
	

	public final static void isValidEmail(String address) throws IOException {
		boolean valid = !TextUtils.isEmpty(address) && android.util.Patterns.EMAIL_ADDRESS.matcher(address).matches();
		
		if(!valid) {
			throw new IOException("'" + address + "'"  + " is not a valid email address."); 
		}
	}


}
