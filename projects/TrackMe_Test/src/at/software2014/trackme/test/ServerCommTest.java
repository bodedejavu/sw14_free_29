package at.software2014.trackme.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.api.client.googleapis.auth.clientlogin.ClientLogin.Response;

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

	final static String TEST_USER_2 = "traudl@gmail.com"; 
	final static String TEST_USER_NAME_2 = "Traudl";
	final static String TEST_USER = "jakob@gmail.com";
	final static String TEST_USER_NAME = "Jakob";

	@BeforeClass 
	public static void setUpClass() {

		ServerComm comm = new ServerComm();
		final CountDownLatch signal = new CountDownLatch(1);


		comm.registerOwnUser(TEST_USER, TEST_USER_NAME, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void response) {
				signal.countDown();				
			}

			@Override
			public void onFailure(Exception failure) {
				signal.countDown();
			}
		});


		try {
			signal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}


	@Test
	public void test1Register() {

		final ServerComm comm = new ServerComm(); 
		final StringBuffer responseName = new StringBuffer();
		final CountDownLatch signal = new CountDownLatch(1);

		comm.registerOwnUser(TEST_USER_2, TEST_USER_NAME_2, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void response) {
				comm.getRegisteredUsers(new AsyncCallback<List<UserData>>() {

					@Override
					public void onSuccess(List<UserData> response) {
						responseName.append(findUser(TEST_USER_2, response).getUserName()); 
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

		assertEquals(responseName.toString(),TEST_USER_NAME_2); 
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

		comm.updateOwnLocation(TEST_USER_2, testLocation, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void response) {
				comm.getRegisteredUsers(new AsyncCallback<List<UserData>>() {

					@Override
					public void onSuccess(List<UserData> response) {
						UserData ownUser = findUser(TEST_USER_2, response); 
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


	@Test
	public void test3AddAllowedUser() {

		final ServerComm comm = new ServerComm(); 
		final CountDownLatch signal = new CountDownLatch(1);
		final StringBuffer responseName = new StringBuffer();

		comm.addAllowedUser(TEST_USER_2, TEST_USER, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void response) {

				comm.getAllowedUsers(TEST_USER_2, new AsyncCallback<List<UserData>>() {

					@Override
					public void onSuccess(List<UserData> response) {

						UserData allowedUser = findUser(TEST_USER ,response);
						responseName.append(allowedUser.getUserEmail());
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

		assertEquals(TEST_USER,responseName.toString());
	}


	@Test
	public void test4RemoveAllowedUser() {

		final ServerComm comm = new ServerComm(); 
		final CountDownLatch signal = new CountDownLatch(1);
		final List<UserData> allowedUsers = new ArrayList<UserData>();

		comm.removeAllowedUser(TEST_USER_2, TEST_USER, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void response) {

				comm.getAllowedUsers(TEST_USER_2, new AsyncCallback<List<UserData>>() {

					@Override
					public void onSuccess(List<UserData> response) {
						
						if(response != null) {
							allowedUsers.addAll(response);							
						}
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
		
		assertTrue(!allowedUsers.contains(TEST_USER));
	}



}
