package at.software2014.trackme.test;

import at.software2014.trackme.*;

import com.robotium.solo.Solo;
import android.test.ActivityInstrumentationTestCase2;

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
//		final String expected = "Friends";
//		final String actual = mSolo.getView(TextView.class, 0).getText().toString();
//		assertEquals(expected, actual);
	}
	
	public void testNavigationDrawer_Contacts() {
		mSolo.setNavigationDrawer(Solo.OPENED);
		mSolo.clickInList(3);
		mSolo.clickOnText("Your Contacts");
//		final String expected = "Your Contacts";
//		final String actual = mSolo.getView(TextView.class, 4).getText().toString();
//		assertEquals(expected, actual);
	}
}
