package at.software2014.trackme.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.software2014.trackme.*;
import at.software2014.trackme.userdataendpoint.model.UserData;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.robotium.solo.Solo;

import android.app.Fragment;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	
	private Solo mSolo;
	private MainActivity mMainActivity;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mSolo = new Solo(getInstrumentation(), getActivity());

		mMainActivity = (MainActivity)mSolo.getCurrentActivity();
		
		List<ContactEntry> contacts = new ArrayList<ContactEntry>();
		List<ContactEntry> registeredUsers = new ArrayList<ContactEntry>();

		contacts.add(new ContactEntry("Rainer Lankmayr", "rainer.lankmayr@gmail.com", (long)1401215993*1000, 47.1, 15.4));
		contacts.add(new ContactEntry("Anna Weber", "anna.weber@gmail.com", (long)1401216003*1000, 47.03, 15.37));
		contacts.add(new ContactEntry("Paul Bodenbenner", "paul.bodenbenner@gmail.com", (long)1401216003*1000, 47.0, 15.5));
		
		mMainActivity.setContacts(contacts);
		
		registeredUsers.add(contacts.get(0));
		registeredUsers.add(contacts.get(1));
		registeredUsers.add(contacts.get(2));
		registeredUsers.add(new ContactEntry("Benjamin Steinacher", "benjamin.steinacher@gmail.com", (long)1401216000*1000, 47.08, 15.35));
		
		mMainActivity.setRegisteredUsers(registeredUsers);
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		mSolo.finishOpenedActivities();
		super.tearDown();
	}
	
	public void test0001_SetPreferences() {
		mMainActivity.setDisableServerComm(true);
		mSolo.sleep(10000);
	}
	
	public void testZZZZ_SetPreferences() {
		mMainActivity.setDisableServerComm(false);
	}
	
	public void testContactsDataContacts01() {
		ContactEntry contactEntry = mMainActivity.getContactByEMail(
				"rainer.lankmayr@gmail.com");		
		assertEquals("Rainer Lankmayr", contactEntry.getName());
	}

	public void testContactsDataContacts02() {
		ContactEntry contactEntry = mMainActivity.getContactByEMail(
				"anna.weber@gmail.com");		
		assertEquals("Anna Weber", contactEntry.getName());
	}

	public void testContactsDataContacts03() {
		ContactEntry contactEntry = mMainActivity.getContactByEMail(
				"max.mustermann@gmail.com");		
		assertEquals(null, contactEntry);
	}

	public void testContactsDataRegisteredUser01() {
		ContactEntry contactEntry = mMainActivity.getRegisteredUserByEMail(
				"rainer.lankmayr@gmail.com");		
		assertEquals("Rainer Lankmayr", contactEntry.getName());
	}

	public void testContactsDataRegisteredUser02() {
		ContactEntry contactEntry = mMainActivity.getRegisteredUserByEMail(
				"anna.weber@gmail.com");		
		assertEquals("Anna Weber", contactEntry.getName());
	}

	public void testContactsDataRegisteredUser03() {
		ContactEntry contactEntry = mMainActivity.getRegisteredUserByEMail(
				"max.mustermann@gmail.com");		
		assertEquals(null, contactEntry);
	}

	public void testActionBar() {
		mSolo.clickOnView(mSolo.getView("action_map_refresh"));
	}

	public void testNavigationDrawer() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.setNavigationDrawer(Solo.CLOSED);
	}
	
	public void testNavigationDrawer_Content() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		View view = mSolo.getView(at.software2014.trackme.R.id.drawer_layout);
		ListView lv = (ListView) view.findViewById(at.software2014.trackme.R.id.left_drawer);
		assertNotNull("List view not found", lv);
		assertEquals(5, lv.getCount());
		
		TextView item;
		item = (TextView) lv.getChildAt(0);
		assertEquals("Map", item.getText());
		item = (TextView) lv.getChildAt(1);
		assertEquals("Friends", item.getText());
		item = (TextView) lv.getChildAt(2);
		assertEquals("Contacts", item.getText());
		item = (TextView) lv.getChildAt(3);
		assertEquals("About", item.getText());
		item = (TextView) lv.getChildAt(4);
		assertEquals("Quit", item.getText());
	}

	public void testNavigationDrawer_Friends() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(2);
		assertTrue(mSolo.waitForText("Friends"));
	}

	public void testNavigationDrawer_Contacts() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		assertTrue(mSolo.waitForText("Your Contacts"));
	}

	public void testNavigationDrawer_About() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(4);
		assertTrue(mSolo.waitForText("About"));
	}

	public void testAbout_Layout() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(4);
		assertNotNull(mSolo.getView("about_title"));
		assertNotNull(mSolo.getView("about_text"));
	}

	public void testFriends_ListView() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(2);
		ListView lv = (ListView) mSolo.getView(
				at.software2014.trackme.R.id.friendslist).findViewById(
				at.software2014.trackme.R.id.friendslist_listView);
		assertNotNull("List view not found", lv);
		assertEquals(3, lv.getCount());
		View view;
		TextView name;
		view = lv.getChildAt(0);
		name = (TextView) view
				.findViewById(at.software2014.trackme.R.id.friend_name);
		assertEquals("Name not found", "Rainer Lankmayr", name.getText()
				.toString());
		view = lv.getChildAt(0);
		name = (TextView) view
				.findViewById(at.software2014.trackme.R.id.friend_timestamp);
		assertEquals("Timestamp not found", "27.05.2014, 20:39", name.getText()
				.toString());
	}
	
	public void testFriends_ListViewEmpty() {
		ArrayList<ContactEntry> contacts = new ArrayList<ContactEntry>();
		mMainActivity.setContacts(contacts);
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(2);
		View view = mSolo.getView(
				at.software2014.trackme.R.id.friendslist);
		ListView lv = (ListView) view.findViewById(
				at.software2014.trackme.R.id.friendslist_listView);
		assertEquals(0, lv.getCount());
		
		TextView empty = (TextView) view.findViewById(
				at.software2014.trackme.R.id.friendslist_empty);
		assertEquals("no entries", empty.getText());
        assertEquals(View.VISIBLE, empty.getVisibility());
	}
	
	public void testFriends_Layout() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(2);
		assertNotNull(mSolo.getView("action_friends_refresh"));
		assertNotNull(mSolo.getView("friendslist_title"));
		assertNotNull(mSolo.getView("friendslist_listView"));
	}
	
	public void testFriends_ActionRefresh() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(2);
		mSolo.clickOnView(mSolo.getView("action_friends_refresh"));
		assertTrue(this.mSolo.waitForText("Refreshing ..."));
	}

	public void testContacts_ListView() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		ListView lv = (ListView) mSolo.getView(
				at.software2014.trackme.R.id.contacts).findViewById(
				at.software2014.trackme.R.id.contacts_listView);
		assertNotNull("List view not found", lv);
		assertEquals(3, lv.getCount());
		View view;
		TextView name;
		view = lv.getChildAt(0);
		name = (TextView) view
				.findViewById(at.software2014.trackme.R.id.contacts_name);
		assertEquals("Name not found", "Anna Weber", name.getText()
				.toString());
		view = lv.getChildAt(1);
		name = (TextView) view
				.findViewById(at.software2014.trackme.R.id.contacts_name);
		assertEquals("Name not found", "Paul Bodenbenner", name.getText()
				.toString());
		view = lv.getChildAt(2);
		name = (TextView) view
				.findViewById(at.software2014.trackme.R.id.contacts_name);
		assertEquals("Name not found", "Rainer Lankmayr", 
				name.getText().toString());
	}
	
	public void testContacts_ListViewEmpty() {
		ArrayList<ContactEntry> contacts = new ArrayList<ContactEntry>();
		mMainActivity.setContacts(contacts);
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		View view = mSolo.getView(
				at.software2014.trackme.R.id.contacts);
		ListView lv = (ListView) view.findViewById(
				at.software2014.trackme.R.id.contacts_listView);
		assertEquals(0, lv.getCount());
		
		TextView empty = (TextView) view.findViewById(
				at.software2014.trackme.R.id.contacts_empty);
		assertEquals("no entries", empty.getText());
        assertEquals(View.VISIBLE, empty.getVisibility());
	}
	
	public void testContacts_Layout() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
	    assertNotNull(mSolo.getView("action_contact_invite"));
	    assertNotNull(mSolo.getView("action_contact_add"));
	    assertNotNull(mSolo.getView("action_contact_delete"));
	    assertNotNull(mSolo.getView("contacts_title"));
	    assertNotNull(mSolo.getView("contacts_listView"));
	}
	
	public void testContacts_AddContactActivity() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickOnView(mSolo.getView("action_contact_add"));
		assertNotNull(mSolo.getView("contacts_add_title"));
		assertNotNull(mSolo.getView("contacts_add_listView"));
	}
	
	public void testContacts_AddContactActivityList() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickOnView(mSolo.getView("action_contact_add"));
		ListView lv = (ListView) mSolo.getView(
				at.software2014.trackme.R.id.contacts_add_listView);
		assertNotNull("List view not found", lv);
		assertEquals(1, lv.getCount());
		View view = lv.getChildAt(0);
		TextView name = (TextView) view
				.findViewById(at.software2014.trackme.R.id.contacts_name);
		assertEquals("Benjamin Steinacher", name.getText().toString());
	}
	
	public void testContacts_AddContactActivityListEmpty() {
		ArrayList<ContactEntry> contacts = new ArrayList<ContactEntry>();
		mMainActivity.setRegisteredUsers(contacts);
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickOnView(mSolo.getView("action_contact_add"));
		View view = mSolo.getView(
				at.software2014.trackme.R.id.contacts_add);
		ListView lv = (ListView) view.findViewById(
				at.software2014.trackme.R.id.contacts_add_listView);
		assertEquals(0, lv.getCount());
		
		TextView empty = (TextView) view.findViewById(
				at.software2014.trackme.R.id.contacts_add_empty);
		assertEquals("no entries", empty.getText());
        assertEquals(View.VISIBLE, empty.getVisibility());
	}
	
	public void testContacts_AddContactActivityConfirmationDialog() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickOnView(mSolo.getView("action_contact_add"));
		mSolo.clickInList(0);
		assertTrue(mSolo.waitForText("Confirmation"));
		assertTrue(mSolo.waitForText("By clicking 'Confirm' you agree"));
		assertTrue(mSolo.searchButton("Confirm"));
		assertTrue(mSolo.searchButton("Cancel"));
	}
	
	public void testContacts_AddContactActivitySuccess() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickOnView(mSolo.getView("action_contact_add"));
		mSolo.clickInList(0);
		mSolo.clickOnButton("Confirm");
		assertTrue(mSolo.waitForText("Adding new contact"));
	}
	
	public void testContacts_AddContactActivityCancel() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickOnView(mSolo.getView("action_contact_add"));
		mSolo.clickInList(0);
		mSolo.clickOnButton("Cancel");
		assertNotNull(mSolo.getView("contacts_add_title"));
	}

	public void testContacts_DeleteContactNoSelection() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickOnView(mSolo.getView("action_contact_delete"));
		assertTrue(this.mSolo.waitForText("No Contact selected"));
		ListView lv = (ListView) mSolo.getView(
				at.software2014.trackme.R.id.contacts).findViewById(
				at.software2014.trackme.R.id.contacts_listView);
		assertEquals(3, lv.getCount());
	}

	public void testContacts_DeleteContactSuccess() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickInList(0);
		mSolo.clickOnView(mSolo.getView("action_contact_delete"));
		assertTrue(this.mSolo.waitForText("Contact deleted"));
		ListView lv = (ListView) mSolo.getView(
				at.software2014.trackme.R.id.contacts).findViewById(
				at.software2014.trackme.R.id.contacts_listView);
		assertEquals(2, lv.getCount());
		View view = lv.getChildAt(0);
		TextView name = (TextView) view
				.findViewById(at.software2014.trackme.R.id.contacts_name);
		assertEquals("Name not found", "Paul Bodenbenner", name.getText()
				.toString());
	}
	
	public void testContacts_DeleteContactAll() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		
		View view = mSolo.getView(
				at.software2014.trackme.R.id.contacts);
		ListView lv = (ListView) view.findViewById(
				at.software2014.trackme.R.id.contacts_listView);
		TextView empty = (TextView) view.findViewById(
				at.software2014.trackme.R.id.contacts_empty);
		assertEquals(3, lv.getCount());
		assertEquals(View.INVISIBLE, empty.getVisibility());
		
		mSolo.clickInList(3);
		mSolo.clickOnView(mSolo.getView("action_contact_delete"));
		mSolo.clickInList(2);
		mSolo.clickOnView(mSolo.getView("action_contact_delete"));
		mSolo.clickInList(1);
		mSolo.clickOnView(mSolo.getView("action_contact_delete"));
		mSolo.waitForText("Contact deleted");
		
		assertEquals(0, lv.getCount());
        assertEquals(View.VISIBLE, empty.getVisibility());
	}

	public void testGoogleMaps_Layout() {
		 assertNotNull(mSolo.getView("action_map_refresh"));
		 assertNotNull(mSolo.getView("action_zoom_to_friends"));
		 assertNotNull(mSolo.getView("action_zoom_to_me"));
	}
	
	public void testGoogleMapsCreated() {
		Fragment fragment = mSolo.getCurrentActivity().getFragmentManager().findFragmentById(at.software2014.trackme.R.id.content_frame);
		GMapFragment gMapFragment = (GMapFragment)fragment;

		GoogleMap googleMap = gMapFragment.getMap();

		assertNotNull(googleMap);
	}

	public void testGoogleMapsIsMyLocationEnabled() {
		Fragment fragment = mSolo.getCurrentActivity().getFragmentManager().findFragmentById(at.software2014.trackme.R.id.content_frame);
		GMapFragment gMapFragment = (GMapFragment)fragment;

		Handler handler = new Handler(Looper.getMainLooper());

		handler.post(new Runnable() {
			private GMapFragment gMapFragment;

			@Override
			public void run() {
				GoogleMap googleMap = gMapFragment.getMap();

				assertEquals(false, googleMap.isMyLocationEnabled());
			}

			private Runnable init(GMapFragment gMapFragment) {
				this.gMapFragment = gMapFragment;
				return this;
			}

		}.init(gMapFragment));

	}

	public void testGoogleMapsMarkersCreateDelete() {
		Fragment fragment = mSolo.getCurrentActivity().getFragmentManager().findFragmentById(at.software2014.trackme.R.id.content_frame);
		GMapFragment gMapFragment = (GMapFragment)fragment;

		Handler handler = new Handler(Looper.getMainLooper());

		handler.post(new Runnable() {
			private GMapFragment gMapFragment;

			@Override
			public void run() {
				mMainActivity.refreshCurrentFragment();

				assertEquals(3, gMapFragment.getMarkersCount());
				gMapFragment.clearMarkers();
				assertEquals(0, gMapFragment.getMarkersCount());
				gMapFragment.createMarkers();
				assertEquals(3, gMapFragment.getMarkersCount());
			}

			private Runnable init(GMapFragment gMapFragment) {
				this.gMapFragment = gMapFragment;
				return this;
			}

		}.init(gMapFragment));

	}

	public void testGoogleMapsMarkersTitle() {
		Fragment fragment = mSolo.getCurrentActivity().getFragmentManager().findFragmentById(at.software2014.trackme.R.id.content_frame);
		GMapFragment gMapFragment = (GMapFragment)fragment;

		Handler handler = new Handler(Looper.getMainLooper());

		handler.post(new Runnable() {
			private GMapFragment gMapFragment;

			@Override
			public void run() {
				mMainActivity.refreshCurrentFragment();

				HashMap<String, Marker> markers = gMapFragment.getMarkers();
				Marker marker;

				marker = markers.get("rainer.lankmayr@gmail.com");
				assertEquals("Rainer Lankmayr", marker.getTitle());
				marker = markers.get("anna.weber@gmail.com");
				assertEquals("Anna Weber", marker.getTitle());
				marker = markers.get("paul.bodenbenner@gmail.com");
				assertEquals("Paul Bodenbenner", marker.getTitle());
			}

			private Runnable init(GMapFragment gMapFragment) {
				this.gMapFragment = gMapFragment;
				return this;
			}

		}.init(gMapFragment));

	}

	public void testGoogleMapsMarkersShowInfoWindow() {
		Fragment fragment = mSolo.getCurrentActivity().getFragmentManager().findFragmentById(at.software2014.trackme.R.id.content_frame);
		GMapFragment gMapFragment = (GMapFragment)fragment;

		Handler handler = new Handler(Looper.getMainLooper());

		String[] keys = {"rainer.lankmayr@gmail.com", "anna.weber@gmail.com", "paul.bodenbenner@gmail.com"};

		for (int i=0; i<keys.length; i++) {

			handler.post(new Runnable() {
				private GMapFragment gMapFragment;
				private String keyName;

				@Override
				public void run() {
					mMainActivity.refreshCurrentFragment();

					HashMap<String, Marker> markers = gMapFragment.getMarkers();
					Marker marker = markers.get(keyName);
					marker.showInfoWindow();
				}

				private Runnable init(GMapFragment gMapFragment, String keyName) {
					this.gMapFragment = gMapFragment;
					this.keyName = keyName;
					return this;
				}

			}.init(gMapFragment, keys[i]));

			mSolo.sleep(1000);
		}
	}

	public void testGoogleMapsZoomButtons() {
		mSolo.clickOnView(mSolo.getView("action_zoom_to_me"));
		mSolo.sleep(2000);
		mSolo.clickOnView(mSolo.getView("action_zoom_to_friends"));
		mSolo.sleep(2000);
	}

	public void testGoogleMapsMyMarkerShowInfoWindow() {
		Fragment fragment = mSolo.getCurrentActivity().getFragmentManager().findFragmentById(at.software2014.trackme.R.id.content_frame);
		GMapFragment gMapFragment = (GMapFragment)fragment;

		Handler handler = new Handler(Looper.getMainLooper());

		handler.post(new Runnable() {
			private GMapFragment gMapFragment;

			@Override
			public void run() {
				mMainActivity.refreshCurrentFragment();

				Marker myMarker = gMapFragment.getMyMarker();
				myMarker.showInfoWindow();
			}

			private Runnable init(GMapFragment gMapFragment) {
				this.gMapFragment = gMapFragment;
				return this;
			}

		}.init(gMapFragment));

		mSolo.sleep(1000);
	}

	public void testLocation01_Disable() {
		mMainActivity.setDisableLocationServices(true);
	}

	public void testLocation99_Enable() {
		mMainActivity.setDisableLocationServices(false);
	}

	public void testLocation02_DistanceKnown() {
		Location myLocation = new Location("dummy");
		myLocation.setLatitude(10.0);
		myLocation.setLongitude(20.0);

		MainActivity mainActivity = (MainActivity)mSolo.getCurrentActivity();

		mainActivity.setMyLocation(myLocation);

		Fragment fragment = mSolo.getCurrentActivity().getFragmentManager().findFragmentById(at.software2014.trackme.R.id.content_frame);
		GMapFragment gMapFragment = (GMapFragment)fragment;

		Handler handler = new Handler(Looper.getMainLooper());

		handler.post(new Runnable() {
			private GMapFragment gMapFragment;

			@Override
			public void run() {
				mMainActivity.refreshCurrentFragment();

				HashMap<String, Marker> markers = gMapFragment.getMarkers();
				Marker marker;

				marker = markers.get("rainer.lankmayr@gmail.com");
				assertEquals("Distance: 4135,42 km\nLast Update: 27.05.2014, 20:39", marker.getSnippet());
			}

			private Runnable init(GMapFragment gMapFragment) {
				this.gMapFragment = gMapFragment;
				return this;
			}

		}.init(gMapFragment));

	}

	public void testLocation03_DistanceUnknown() {
		Fragment fragment = mSolo.getCurrentActivity().getFragmentManager().findFragmentById(at.software2014.trackme.R.id.content_frame);
		GMapFragment gMapFragment = (GMapFragment)fragment;

		Handler handler = new Handler(Looper.getMainLooper());

		handler.post(new Runnable() {
			private GMapFragment gMapFragment;

			@Override
			public void run() {
				mMainActivity.refreshCurrentFragment();

				HashMap<String, Marker> markers = gMapFragment.getMarkers();
				Marker marker;

				marker = markers.get("rainer.lankmayr@gmail.com");
				assertEquals("Distance: Unknown\nLast Update: 27.05.2014, 20:39", marker.getSnippet());
			}

			private Runnable init(GMapFragment gMapFragment) {
				this.gMapFragment = gMapFragment;
				return this;
			}

		}.init(gMapFragment));

	}

	public void testCustomDataTypes01() {
		UserData userData = new UserData();
		userData.setUserName("Max Mustermann");
		userData.setUserEmail("max.mustermann@gmail.com");
		userData.setTimestamp((long)1401216003*1000);
		userData.setUserLastLatitude(10.0);
		userData.setUserLastLongitude(20.0);

		ContactEntry contactEntry = new ContactEntry(userData);

		assertEquals("Max Mustermann", contactEntry.getName());
		assertEquals("max.mustermann@gmail.com", contactEntry.geteMail());
		assertEquals((long)1401216003*1000, contactEntry.getTimestamp());
		assertEquals(10.0, contactEntry.getLatitude());
		assertEquals(20.0, contactEntry.getLongitude());
	}

	public void testCustomDataTypes02() {
		ContactEntry contactEntry = new ContactEntry();
		contactEntry.setTimestamp((long)1401216003*1000);
		
		assertEquals("27.05.2014, 20:40", contactEntry.getTimestampFormatted(getActivity()));
	}

	public void testCustomDataTypes03() {
		ContactEntry contactEntry = new ContactEntry();
		contactEntry.setLatitude(10.0);
		contactEntry.setLongitude(12.0);

		Location myLocation = new Location("dummy");
		myLocation.setLatitude(10.0);
		myLocation.setLongitude(15.0);

		assertEquals("328,92 km", contactEntry.getDistanceFormatted(myLocation, "Unknown"));
	}

	public void testCustomDataTypes04() {
		ContactEntry contactEntry = new ContactEntry();
		contactEntry.setLatitude(10.0);
		contactEntry.setLongitude(12.0);

		assertEquals("Unknown", contactEntry.getDistanceFormatted(null, "Unknown"));
	}

}
