/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.software2014.trackme;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * This code is based on the android navigation drawer example
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class MainActivity extends BaseActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	private static final String FIRST_LAUNCH = "first_launch";
	private static final String TRACK_ME_PREFERENCES = "TrackMePreferences";
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mTitle;
	private String[] mMenuTitles;
	private int mCurrentPosition = 0;

	private List<ContactEntry> mContacts;

	LocationClient mLocationClient;
	LocationRequest mLocationRequest;
	Location mMyLocation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		registerUserAtFirstLaunch();

		mContacts = new ArrayList<ContactEntry>();

		loadDummyData();

		mTitle = getTitle();
		mMenuTitles = getResources().getStringArray(R.array.navigation_menu);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_close  /* "close drawer" description for accessibility */
				) {
 			public void onDrawerClosed(View view) {
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
 			}

 			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
 			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			if(isGooglePlayServicesConnected()){				
				selectItem(0, "");
			}
		}

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		mLocationRequest.setInterval(5000);
		mLocationRequest.setFastestInterval(1000);

		mLocationClient = new LocationClient(this, this, this);

	}

	private String getEmailAddress() {
		String emailAddress = "";
		
		Pattern emailPattern = Patterns.EMAIL_ADDRESS;
		Account[] accounts = AccountManager.get(getBaseContext()).getAccounts();
		
		for (Account account: accounts) {
			if (emailPattern.matcher(account.name).matches() && account.name.endsWith("gmail.com")) {
				emailAddress = account.name;
				break;
			}
		}
		
		return emailAddress;
	}

	private String extractNameFromeMail(String eMail) {
		String name = "";

		String[] parts = eMail.split("@");
		if (parts.length > 0) {
			name = parts[0];
		}
		
		return name;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext())) {			
			selectItem(mCurrentPosition, "");
		}
	}

	private void registerUserAtFirstLaunch() {

		if(isFirstAppStart() || true) {
			SharedPreferences prefs = getSharedPreferences(TRACK_ME_PREFERENCES, MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putBoolean(FIRST_LAUNCH, false);

			// get user info and save it to shared prefs
			String eMail = getEmailAddress();
			String name  = extractNameFromeMail(eMail);

			Log.d("INFORMATION", eMail);
			Log.d("INFORMATION", name);

			editor.putString("email", eMail);
			editor.putString("first_name", name);

			editor.commit();
		}

	}

	private boolean isFirstAppStart() {
		SharedPreferences sharedPreferences = getSharedPreferences(TRACK_ME_PREFERENCES, MODE_PRIVATE);
		return sharedPreferences.getBoolean(FIRST_LAUNCH, true);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.start, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		int[] menuItemIds = {R.id.action_refresh, R.id.action_contact_delete, R.id.action_contact_add,
				R.id.action_contact_invite, R.id.action_zoom_to_friends, R.id.action_zoom_to_me};

		for (int i=0; i<menuItemIds.length; i++) {
			MenuItem menuItem = menu.findItem(menuItemIds[i]);
			if (menuItem != null) {
				menuItem.setVisible(!drawerOpen);
			}
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch(item.getItemId()) {		
		case R.id.action_refresh:
			Toast.makeText(this, "Refresh", Toast.LENGTH_LONG).show();
			return true;
		case R.id.action_settings:
			startActivity(new Intent(MainActivity.this, RegisterActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void loadDummyData() {
		mContacts.clear();
		
		mContacts.add(new ContactEntry("Anna Weber", "anna.weber@gmail.com", (long)1401216003*1000, 47.1, 15.4));
		mContacts.add(new ContactEntry("Rainer Lankmayr", "rainer.lankmayr@gmail.com", (long)1401215993*1000, 47.0, 15.5));
		mContacts.add(new ContactEntry("Benjamin Steinacher", "benjamin.steinacher@gmail.com", (long)1401216000*1000, 47.08, 15.35));
	}

	public ContactEntry getContactByEMail(String eMail) {
		ContactEntry contactEntry = null;
		
		for (int i=0; i<mContacts.size(); i++) {
			if (mContacts.get(i).geteMail() == eMail) {
				contactEntry = mContacts.get(i);
				break;
			}
		}
		
		return contactEntry;
	}
	
	public List<ContactEntry> getContacts() {
		return mContacts;
	}

	public void setContacts(List<ContactEntry> mContacts) {
		this.mContacts = mContacts;
	}

	public boolean deleteContact(String eMail) {
		ContactEntry contactEntry = getContactByEMail(eMail);
		if(contactEntry != null) {
			mContacts.remove(eMail);
			return true;
		}
		return false;
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position, "");
		}
	}

	public void selectItem(int position, String argument) {

		Fragment fragment = new Fragment();

		mCurrentPosition = position;

		switch(position) {
		case 0:
			if (argument == "") {
				fragment = GMapFragment.newInstance(position);
			}
			else {
				fragment = GMapFragment.newInstance(position, argument);
			}
			break;
		case 1:
			fragment = FriendsListFragment.newInstance(position);
			break;
		case 2:
			fragment = ContactsFragment.newInstance(position);
			break;
		case 3:
			closeConnection();
			System.exit(0);
		}

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mMenuTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);

	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	protected void onStop() {
		closeConnection();
		super.onStop();
	}

	public void closeConnection() {
		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
		mLocationClient.disconnect();		
	}

	@Override
	public void onConnected(Bundle dataBundle) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d("INFORMATION", "Location changed");
		mMyLocation = location;
		refreshLocation();
	}
	
	public Location getMyLocation() {
		return mMyLocation;
	}

	public void refreshLocation() {
		Fragment fragment = getFragmentManager().findFragmentById(R.id.content_frame);

		switch(mCurrentPosition) {
		case 0:
			((GMapFragment) fragment).refreshLocation();
			break;
		case 1:
			((FriendsListFragment) fragment).refreshLocation();
			break;
		}		
	}
}