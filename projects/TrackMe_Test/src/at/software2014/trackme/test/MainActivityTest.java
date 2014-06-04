package at.software2014.trackme.test;

import at.software2014.trackme.*;

import com.robotium.solo.Solo;
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
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
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
				.findViewById(at.software2014.trackme.R.id.friend_distance);
		assertEquals("Distance not found", "Unknown", name.getText().toString());
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
	}
}
