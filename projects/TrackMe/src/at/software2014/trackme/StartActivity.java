package at.software2014.trackme;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StartActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onPause() {
    	super.onPause();

    	FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment != null) {
            fragmentManager.beginTransaction()
            	.remove(fragment)
            	.commit();
            }
    }

    @Override
    public void onResume() {
    	super.onResume();

    	FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment == null) {
        	onNavigationDrawerItemSelected(mPosition);
            }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        if (position == 0) {
	        fragmentManager.beginTransaction()
        		.replace(R.id.container, GMapFragment.newInstance(position + 1))
        		.commit();
	        }
        else {
	        fragmentManager.beginTransaction()
        		.replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
        		.commit();
	        }
        mPosition = position;
    }

    public void onSectionAttached(int number) {
    	Resources res = getResources();
    	String[] navigationMenu = res.getStringArray(R.array.navigation_menu);
    	
    	mTitle = navigationMenu[number - 1];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.start, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * A fragment containing a google map.
     */
    public static class GMapFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private GoogleMap mGoogleMap = null;
        private LatLngBounds mBounds = null;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static GMapFragment newInstance(int sectionNumber) {
            GMapFragment fragment = new GMapFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public GMapFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	View rootView = inflater.inflate(R.layout.fragment_gmap, container, false);
            return rootView;
        }
        
        @Override
        public void onViewCreated(View v, Bundle savedInstanceState) {
        	super.onViewCreated(v, savedInstanceState);
            
        	FragmentManager fragmentManager = getFragmentManager();
        	Fragment fragment = fragmentManager.findFragmentById(R.id.map);
        	
        	if (fragment != null) {
	        	mGoogleMap = ((MapFragment) fragment).getMap();
	            
	        	Marker anna = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(47.1, 15.4)).title("Anna").snippet("Distance: 5km"));
	            Marker rainer = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(47.08, 15.35)).title("Rainer").snippet("Distance: 6km"));
	            Marker benjamin = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(47.0, 15.5)).title("Benjamin").snippet("Distance: 10km"));
	            
	            LatLngBounds.Builder builder = new LatLngBounds.Builder();
	            builder.include(rainer.getPosition());
	            builder.include(anna.getPosition());
	            builder.include(benjamin.getPosition());
	            mBounds = builder.build();
	            
	            mGoogleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
					
					@Override
					public void onCameraChange(CameraPosition arg0) {
						CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(mBounds, 100);
						mGoogleMap.moveCamera(cu);
						mGoogleMap.setOnCameraChangeListener(null);
					}
				});
	            
	            mGoogleMap.setMyLocationEnabled(true);
        	}
        }
        
        @Override
        public void onDestroyView() {
        	super.onDestroyView();
        	
        	FragmentManager fragmentManager = getFragmentManager();
        	Fragment fragment = fragmentManager.findFragmentById(R.id.map);
            if (fragment != null) {
	            fragmentManager.beginTransaction()
	            	.remove(fragment)
	                .commit();
	            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((StartActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_start, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((StartActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
