package at.software2014.trackme;

import java.io.IOException;

import android.os.AsyncTask;
import at.software2014.trackme.userdataendpoint.Userdataendpoint;
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
	
	
	public void registerOwnUser(final String email, final String name, final AsyncCallback<Void> onRegisterCompleteCallback)
	{
		new AsyncTask<Void,Void,Void>()
		{
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
				if(mException == null)
				{
					onRegisterCompleteCallback.onSuccess(result); 
				}
				else
				{
					onRegisterCompleteCallback.onFailure(mException); 
				}
			}
			
		}.execute((Void)null); 
		
		
	}
	
	private void registerOwnUserSync(String email, String name) throws IOException
	{	

			mUserEndpoint.register(email, name).execute();		
			
			// DEMO code for querying own user data and updating location
//			UserData ud1 = mUserEndpoint.getUserData(email).execute();
//			 
//			System.out.println(ud1);
//
//			mUserEndpoint.updateLocation(email,
//					47.13243 * Math.random(), 15.3434 * Math.random())
//					.execute();
//			UserData ud2 = mUserEndpoint.getUserData(email).execute();
//
//			System.out.println(ud2);

	}
	
	

}
