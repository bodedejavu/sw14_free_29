package at.software2014.trackme;

import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


/**
 * A fragment containing a google map.
 */
public class GMapFragment extends MapFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String ARG_CONTACT_KEY = "contact_key";
	private static final String ARG_NAME = "name";
	private static final float mDefaultZoomLevel = (float)15.0;

    private GoogleMap mGoogleMap = null;
    private HashMap<String, Marker> mMarkers;
    private Marker mMyMarker;
    private Marker mActiveMarker;
    boolean mMyMarkerVisible = false;
    private String mContactKey = "";
    
    private String mName = ""; 

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GMapFragment newInstance(int sectionNumber, String name) {
    	
        GMapFragment fragment = new GMapFragment();
        
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }
    
    public static GMapFragment newInstance(int sectionNumber, String name, String contactKey) {
    	
        GMapFragment fragment = new GMapFragment();
        
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_NAME, name);
        args.putString(ARG_CONTACT_KEY, contactKey);
        fragment.setArguments(args);
        return fragment;
    }

    public GMapFragment() {
    }

    public HashMap<String, Marker> getMarkers() {
		return mMarkers;
	}

    public int getMarkersCount() {
		return mMarkers.size();
	}

    public void clearMarkers() {
    	mGoogleMap.clear();
    	mMarkers.clear();
    	mMyMarker = null;
    	mActiveMarker = null;
    }

    private void refreshInfoWindow(Marker marker) {
		if (mActiveMarker != null && marker.equals(mActiveMarker)) {
			marker.hideInfoWindow();
			marker.showInfoWindow();
		}
    }
    
    private boolean checkMarkersNeedRecreation(List<ContactEntry> contacts) {
    	
    	if (contacts.size() != mMarkers.size() || contacts.size() == 0) {
    		return true;
    	}
    	
    	for (int i=0; i<contacts.size(); i++) {
    		ContactEntry contactEntry = contacts.get(i);
    		
    		if (mMarkers.containsKey(contactEntry.geteMail()) == false) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public void createMarkers() {
    	Location myLocation = ((MainActivity) getActivity()).getMyLocation();
    	List<ContactEntry> contacts = ((MainActivity) getActivity()).getContacts();
    	
    	boolean recreate = checkMarkersNeedRecreation(contacts);
    	if (recreate == true) {
    		clearMarkers();
    	}
    	
    	mMyMarkerVisible = false;
		LatLng myPosition = new LatLng(0.0, 0.0);
		if (myLocation != null) {
			mMyMarkerVisible = true;
			myPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
		}
    	
    	if (recreate == true) {
    		String myTitle = getResources().getString(R.string.information_me);
        	mMyMarker = mGoogleMap.addMarker(new MarkerOptions().position(myPosition).title(myTitle).snippet("(" + mName + ")").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
    	}
    	else {
    		mMyMarker.setPosition(myPosition);
    		refreshInfoWindow(mMyMarker);
    	}
    	mMyMarker.setVisible(mMyMarkerVisible);
    	
    	for (int i=0; i<contacts.size(); i++) {
    		ContactEntry contactEntry = contacts.get(i);
    		
    		String eMail = contactEntry.geteMail();
    		String title = contactEntry.getName();
    		LatLng position = new LatLng(contactEntry.getLatitude(), contactEntry.getLongitude());
    		String timestamp = contactEntry.getTimestampFormatted(getActivity());
    		String distance = contactEntry.getDistanceFormatted(myLocation, getResources().getString(R.string.information_unknown));
    		
        	String snippet = getResources().getString(R.string.information_distance) + ": " + distance + "\n" + getResources().getString(R.string.information_last_update) + ": " + timestamp;
        	
        	Marker marker;
        	
        	if (recreate == true) {
        		marker = mGoogleMap.addMarker(new MarkerOptions().position(position).title(title).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        		mMarkers.put(eMail, marker);
        	}
        	else {
        		marker = mMarkers.get(eMail);
        		marker.setTitle(title);
        		marker.setPosition(position);
        		marker.setSnippet(snippet);

        		refreshInfoWindow(marker);
        	}
    	}
    }
    
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.g_map, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.action_map_refresh:
			Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_LONG).show();
			((MainActivity) getActivity()).loadAllowedUsers();
			return true;
		case R.id.action_zoom_to_friends:
			if (mMarkers.size() == 0) {
				zoomToMe(true);
			}
			else {
				zoomToFriends(true);
			}
			return true;
		case R.id.action_zoom_to_me:
			zoomToMe(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void zoomToMe(boolean animate) {
		if (mMyMarkerVisible == true) {
			CameraUpdate cu = null;
			
			cu = CameraUpdateFactory.newLatLngZoom(new LatLng(mMyMarker.getPosition().latitude, mMyMarker.getPosition().longitude), mDefaultZoomLevel);
			
			changeCameraPosition(cu, animate);
		}
	}
	
	public void zoomToFriend(boolean animate) {
		if (mMarkers.containsKey(mContactKey) == true) {
			CameraUpdate cu = null;
			
			Marker marker = mMarkers.get(mContactKey);
			marker.showInfoWindow();
			mActiveMarker = marker;

			cu = CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), mDefaultZoomLevel);
			
			changeCameraPosition(cu, animate);
		}
	}
	
	public void zoomToFriends(boolean animate) {
		if (mMyMarkerVisible == true || mMarkers.size() > 0) {
			CameraUpdate cu = null;
			
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			
			if (mMyMarkerVisible == true) {
				builder.include(mMyMarker.getPosition());
			}

			for (String key: mMarkers.keySet()) {
				Marker marker = mMarkers.get(key);
				builder.include(marker.getPosition());
			}

			LatLngBounds bounds = builder.build();

			cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
			
			changeCameraPosition(cu, animate);
		}
	}
	
	private void changeCameraPosition(CameraUpdate cu, boolean animate) {
		if (animate == false) {
			mGoogleMap.moveCamera(cu);	
		}
		else {
			mGoogleMap.animateCamera(cu);
		}		
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	setHasOptionsMenu(true);
    	
    	mMarkers = new HashMap<String, Marker>();

    	mGoogleMap = getMap();
    	
    	Bundle bundle = this.getArguments();
    	if (bundle.containsKey(ARG_NAME)) {
    		mName = bundle.getString(ARG_NAME);
    	}
    	if (bundle.containsKey(ARG_CONTACT_KEY)) {
    		mContactKey = bundle.getString(ARG_CONTACT_KEY);
    	}
    	
    	createMarkers();
    	
        mGoogleMap.setInfoWindowAdapter(new GInfoWindowAdapter(getActivity()));

        mGoogleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				mActiveMarker = null;
			}
		});

        mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				mActiveMarker = marker;
				return false;
			}
		});

        mGoogleMap.setOnCameraChangeListener(new OnCameraChangeListener() {

        	@Override
        	public void onCameraChange(CameraPosition arg0) {
        		Log.d("INFORMATION", "Camera changed");

        		if (mMarkers.size() == 0) {
        			zoomToMe(false);
        		}
        		else if (mContactKey != "") {
            		zoomToFriend(false);
            	}
            	else {
           			zoomToFriends(false);
           		}

        		mGoogleMap.setOnCameraChangeListener(null);
			}
		});

    }

    public void refreshLocation() {
    	createMarkers();	
    }

}
