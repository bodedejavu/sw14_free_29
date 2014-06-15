package at.software2014.trackme.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import at.software2014.trackme.MainActivity;
import at.software2014.trackme.ServerComm;
import at.software2014.trackme.ServerComm.AsyncCallback;
import at.software2014.trackme.userdataendpoint.model.UserData;


public class ServerCommTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public ServerCommTest() {
		super(MainActivity.class);
	}
	
	final static String TEST_USER = "traudl@trackertown.com"; 
	final static String TEST_USER_NAME = "Traudl";
	
	
	@Test
	public void test1Register() {
		
		final ServerComm comm = new ServerComm(); 
		final StringBuffer responseName = new StringBuffer();
		final CountDownLatch signal = new CountDownLatch(1);
		
		comm.registerOwnUser(TEST_USER, TEST_USER_NAME, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void response) {
				comm.getRegisteredUsers(new AsyncCallback<List<UserData>>() {
					
					@Override
					public void onSuccess(List<UserData> response) {
						responseName.append(findUser(TEST_USER, response).getUserName()); 
						signal.countDown(); 
					}
					
					@Override
					public void onFailure(Exception failure) {
						failure.printStackTrace(); 
						signal.countDown(); 
					}
				});
			}
			
			@Override
			public void onFailure(Exception failure) {
				failure.printStackTrace(); 
				signal.countDown(); 
			}
		}); 
		
		try {
			signal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertEquals(responseName.toString(),TEST_USER_NAME); 
	}
	
	
	private static UserData findUser(String id, List<UserData> users) {
		
		UserData ownData = null; 
		for(UserData ud : users)
		{
			if(ud.getUserEmail().equals(id))
			{
				ownData = ud; 
				break; 
			}
		}
		return ownData; 
	}
	
	
	@Test
	public void test2LocationUpdate()
	{
		final ServerComm comm = new ServerComm(); 
		final CountDownLatch signal = new CountDownLatch(1);
		
		Location testLocation = new Location(LocationManager.GPS_PROVIDER); 
		double testLat = 47 + Math.random(); 
		double testLon = 15 + Math.random(); 
		testLocation.setLatitude(testLat);
		testLocation.setLongitude(testLon); 
		
		final Location responseLocation = new Location(LocationManager.GPS_PROVIDER); 
		
		comm.updateOwnLocation(TEST_USER, testLocation, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void response) {
				comm.getRegisteredUsers(new AsyncCallback<List<UserData>>() {
					
					@Override
					public void onSuccess(List<UserData> response) {
						UserData ownUser = findUser(TEST_USER, response); 
						responseLocation.setLatitude(ownUser.getUserLastLatitude()); 
						responseLocation.setLongitude(ownUser.getUserLastLongitude());
						signal.countDown(); 		
					}
					
					@Override
					public void onFailure(Exception failure) {
						failure.printStackTrace(); 
						signal.countDown(); 
					}
				});
			}
			
			@Override
			public void onFailure(Exception failure) {
				failure.printStackTrace(); 
				signal.countDown(); 				
			}
		});
		
		
		try {
			signal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertEquals(testLocation.getLatitude(), responseLocation.getLatitude(), 0.0001); 
		assertEquals(testLocation.getLongitude(), responseLocation.getLongitude(), 0.0001); 
	}
	
	

}
