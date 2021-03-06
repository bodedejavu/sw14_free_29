package at.software2014.trackme.test;

import java.util.ArrayList;
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

	final static String TEST_USER_2;
	final static String TEST_USER_NAME_2 = "traudl";
	final static String TEST_USER;
	final static String TEST_USER_NAME = "thomas";

	static {
		TEST_USER = (int)(Math.random() * 100) + "." + "thomas@gmail.com";
		TEST_USER_2 = (int)(Math.random() * 100) + "." +  "traudl@gmail.com";	
	}

	@Test
	public void test1Register() {

		final ServerComm comm = new ServerComm(); 
		final StringBuffer responseName = new StringBuffer();
		final CountDownLatch signal = new CountDownLatch(1);

		comm.registerOwnUser(TEST_USER, TEST_USER_NAME, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void response) {

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				comm.getRegisteredUsers(TEST_USER, new AsyncCallback<List<UserData>>() {

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


	@Test
	public void test2Register() {

		final ServerComm comm = new ServerComm(); 
		final StringBuffer responseName = new StringBuffer();
		final CountDownLatch signal = new CountDownLatch(1);

		comm.registerOwnUser(TEST_USER_2, TEST_USER_NAME_2, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void response) {

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				comm.getRegisteredUsers(TEST_USER_2, new AsyncCallback<List<UserData>>() {

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
	public void test3LocationUpdate()
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
				comm.getRegisteredUsers(TEST_USER_2, new AsyncCallback<List<UserData>>() {

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
	public void test4AddAllowedUser() {

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

		assertEquals(TEST_USER, responseName.toString());
	}


	@Test
	public void test5RemoveAllowedUser() {

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

	
	@Test
	public void test6UnregisterUser() {

		final ServerComm comm = new ServerComm(); 
		final CountDownLatch signal = new CountDownLatch(1);
		final List<UserData> existingUsers = new ArrayList<UserData>();

		comm.unregisterOwnUser(TEST_USER_2, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void response) {

				comm.getRegisteredUsers(TEST_USER_2, new AsyncCallback<List<UserData>>() {

					@Override
					public void onSuccess(List<UserData> response) {

						if(response != null) {
							existingUsers.addAll(response);						
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

		assertTrue(!existingUsers.contains(TEST_USER_2));
	}
	
	
	@Test
	public void test7UnregisterUser() {

		final ServerComm comm = new ServerComm(); 
		final CountDownLatch signal = new CountDownLatch(1);
		final List<UserData> existingUsers = new ArrayList<UserData>();

		comm.unregisterOwnUser(TEST_USER, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void response) {

				comm.getRegisteredUsers(TEST_USER, new AsyncCallback<List<UserData>>() {

					@Override
					public void onSuccess(List<UserData> response) {

						if(response != null) {
							existingUsers.addAll(response);						
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

		assertTrue(!existingUsers.contains(TEST_USER));
	}


}
