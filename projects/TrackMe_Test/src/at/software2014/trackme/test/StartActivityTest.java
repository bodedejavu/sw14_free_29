package at.software2014.trackme.test;

import at.software2014.trackme.*;

import com.robotium.solo.Solo;
import android.test.ActivityInstrumentationTestCase2;

public class StartActivityTest extends ActivityInstrumentationTestCase2<StartActivity> {

	private Solo mSolo;

	public StartActivityTest() {
		super(StartActivity.class);
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
}
