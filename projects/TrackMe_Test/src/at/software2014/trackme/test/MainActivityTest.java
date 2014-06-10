package at.software2014.trackme.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import at.software2014.trackme.*;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.robotium.solo.Solo;

import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private Solo mSolo;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mSolo = new Solo(getInstrumentation(), getActivity());
		
		MainActivity mainActivity = (MainActivity)mSolo.getCurrentActivity();
		
		HashMap<String, ContactEntry> mContacts = new HashMap<String, ContactEntry>();
		HashMap<String, List<HistoryEntry>> mHistory = new HashMap<String, List<HistoryEntry>>();
		
		mContacts.put("anna_weber", new ContactEntry("Anna", "Weber"));
		mContacts.put("benjamin_steinacher", new ContactEntry("Benjamin", "Steinacher"));
		mContacts.put("rainer_lankmayr", new ContactEntry("Rainer", "Lankmayr"));

		List<HistoryEntry> historyList1 = new ArrayList<HistoryEntry>();
		historyList1.add(new HistoryEntry(new Date(0), mainActivity.createLocation(46.1, 15.4), 0, ""));
		historyList1.add(new HistoryEntry(new Date((long)1401216003*1000), mainActivity.createLocation(47.1, 15.4), 0, ""));
		mHistory.put("anna_weber", historyList1);
		List<HistoryEntry> historyList2 = new ArrayList<HistoryEntry>();
		historyList2.add(new HistoryEntry(new Date(0), mainActivity.createLocation(46.08, 15.35), 0, ""));
		historyList2.add(new HistoryEntry(new Date((long)1401216000*1000), mainActivity.createLocation(47.08, 15.35), 0, ""));
		mHistory.put("benjamin_steinacher", historyList2);
		List<HistoryEntry> historyList3 = new ArrayList<HistoryEntry>();
		historyList3.add(new HistoryEntry(new Date(0), mainActivity.createLocation(46.0, 15.5), 0, ""));
		historyList3.add(new HistoryEntry(new Date((long)1401215993*1000), mainActivity.createLocation(47.0, 15.5), 0, ""));
		mHistory.put("rainer_lankmayr", historyList3);
		
		mainActivity.setContacts(mContacts);
		mainActivity.setHistory(mHistory);
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
	public void testData() {
		MainActivity mainActivity = (MainActivity)mSolo.getCurrentActivity();
		
		HashMap<String, ContactEntry> mContacts = new HashMap<String, ContactEntry>();
		mContacts = mainActivity.getContacts();
		ContactEntry contactEntry = mContacts.get("rainer_lankmayr");
		
		assertEquals(contactEntry.getFirstName(), "Rainer");
		assertEquals(contactEntry.getSecondName(), "Lankmayr");
	}

	public void testData2() {
		MainActivity mainActivity = (MainActivity)mSolo.getCurrentActivity();
		
		HashMap<String, ContactEntry> mContacts = new HashMap<String, ContactEntry>();
		mContacts = mainActivity.getContacts();
		ContactEntry contactEntry = mContacts.get("anna_weber");
		
		assertEquals(contactEntry.getFirstName(), "Anna");
		assertEquals(contactEntry.getSecondName(), "Weber");
		
	}
	
	public void testActionBar() {
		mSolo.clickOnView(mSolo.getView("action_refresh"));
	}

	public void testNavigationDrawer() {
		mSolo.setNavigationDrawer(Solo.OPENED);
	}

	public void testNavigationDrawer_Friends() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(2);
		mSolo.clickOnText("Friends");
		// alternative testing, fails occasionally
		// final String expected = "Friends";
		// final String actual = mSolo.getView(TextView.class,
		// 0).getText().toString();
		// assertEquals(expected, actual);
	}

	public void testNavigationDrawer_Contacts() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickOnText("Your Contacts");
		// alternative testing, fails occasionally
		// final String expected = "Your Contacts";
		// final String actual = mSolo.getView(TextView.class,
		// 4).getText().toString();
		// assertEquals(expected, actual);
	}

	public void testFriends_ListView() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(2);
		ListView lv = (ListView) mSolo.getView(
				at.software2014.trackme.R.id.friendslist).findViewById(
				at.software2014.trackme.R.id.friendslist_listView);
		assertNotNull("List view not found", lv);
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

	public void testContacts_ListView() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		ListView lv = (ListView) mSolo.getView(
				at.software2014.trackme.R.id.contacts).findViewById(
				at.software2014.trackme.R.id.contacts_listView);
		assertNotNull("List view not found", lv);
		View view;
		TextView name;
		view = lv.getChildAt(0);
		name = (TextView) view
				.findViewById(at.software2014.trackme.R.id.contacts_name);
		assertEquals("Name not found", "Rainer Lankmayr", name.getText()
				.toString());
		view = lv.getChildAt(1);
		name = (TextView) view
				.findViewById(at.software2014.trackme.R.id.contacts_name);
		assertEquals("Name not found", "Benjamin Steinacher", name.getText()
				.toString());
		view = lv.getChildAt(2);
		name = (TextView) view
				.findViewById(at.software2014.trackme.R.id.contacts_name);
		assertEquals("Name not found", "Anna Weber", name.getText().toString());
	}

	public void testContacts_ActionAddContactButton() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickOnView(mSolo.getView("action_contact_add"));
	}

	public void testContacts_ActionDeleteContactButton() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickOnView(mSolo.getView("action_contact_delete"));
	}

	public void testContacts_ActionDeleteContactNoSelection() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickOnView(mSolo.getView("action_contact_delete"));
		assertTrue(this.mSolo.searchText("No Contact selected"));
	}

	public void testContacts_ActionDeleteContactSuccess() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickInList(0);
		mSolo.clickOnView(mSolo.getView("action_contact_delete"));
		assertTrue(this.mSolo.searchText("Contact deleted"));
		ListView lv = (ListView) mSolo.getView(
				at.software2014.trackme.R.id.contacts).findViewById(
				at.software2014.trackme.R.id.contacts_listView);
		View view = lv.getChildAt(0);
		TextView name = (TextView) view
				.findViewById(at.software2014.trackme.R.id.contacts_name);
		assertEquals("Name not found", "Benjamin Steinacher", name.getText()
				.toString());
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

				assertEquals(true, googleMap.isMyLocationEnabled());
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
				assertEquals(3, gMapFragment.getMarkersCount());
				gMapFragment.clearMarkers();
				assertEquals(0, gMapFragment.getMarkersCount());
				gMapFragment.createMarkers(true);
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
				HashMap<String, Marker> markers = gMapFragment.getMarkers();
				Marker marker;

				marker = markers.get("rainer_lankmayr");
				assertEquals("Rainer Lankmayr", marker.getTitle());
				marker = markers.get("anna_weber");
				assertEquals("Anna Weber", marker.getTitle());
				marker = markers.get("benjamin_steinacher");
				assertEquals("Benjamin Steinacher", marker.getTitle());
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

		String[] keys = {"rainer_lankmayr", "anna_weber", "benjamin_steinacher"};

		for (int i=0; i<keys.length; i++) {

			handler.post(new Runnable() {
				private GMapFragment gMapFragment;
				private String keyName;

				@Override
				public void run() {
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
}
