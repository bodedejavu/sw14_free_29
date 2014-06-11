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
	private static final float mDefaultZoomLevel = (float)15.0;

    private GoogleMap mGoogleMap = null;
    private HashMap<String, Marker> mMarkers;
    private Marker mActiveMarker;
    private String mContactKey = "";

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
    
    public static GMapFragment newInstance(int sectionNumber, String contactKey) {
    	
        GMapFragment fragment = new GMapFragment();
        
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
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
    	mMarkers.clear();
    	mGoogleMap.clear();
    }

    public void createMarkers(Boolean initial) {
    	if (initial == true) {
    		clearMarkers();
    	}
    	
    	Location myLocation = ((MainActivity) getActivity()).getMyLocation();
    	List<ContactEntry> contacts = ((MainActivity)getActivity()).getContacts();
    	
    	for (int i=0; i<contacts.size(); i++) {
    		ContactEntry contactEntry = contacts.get(i);
    		
    		String eMail = contactEntry.geteMail();
    		String title = contactEntry.getName();
    		LatLng position = new LatLng(contactEntry.getLatitude(), contactEntry.getLongitude());
    		String timestamp = contactEntry.getTimestampFormatted(getActivity());
    		String distance = contactEntry.getDistanceFormatted(myLocation, getResources().getString(R.string.information_unknown));
    		
        	String snippet = getResources().getString(R.string.information_distance) + ": " + distance + "\n" + getResources().getString(R.string.information_last_update) + ": " + timestamp;
        	
        	Marker marker;
        	
        	if (initial == true) {
        		marker = mGoogleMap.addMarker(new MarkerOptions().position(position).title(title).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        		mMarkers.put(eMail, marker);
        	}
        	else {
        		marker = mMarkers.get(eMail);
        		marker.setTitle(title);
        		marker.setPosition(position);
        		marker.setSnippet(snippet);

        		if (mActiveMarker != null && marker.equals(mActiveMarker)) {
        			marker.hideInfoWindow();
        			marker.showInfoWindow();
        		}
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
			//TODO refresh data
			Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_LONG).show();
			return true;
		case R.id.action_zoom_to_friends:
			zoomToFriends(true);
			return true;
		case R.id.action_zoom_to_me:
			zoomToMe(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void zoomToMe(boolean animate) {
		CameraUpdate cu = null;
		
		Location myLocation = ((MainActivity) getActivity()).getMyLocation();
		if (myLocation != null) {
			cu = CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), mDefaultZoomLevel);
		}
		
		changeCameraPosition(cu, animate);
	}
	
	public void zoomToFriend(boolean animate) {
		CameraUpdate cu = null;
		
		Marker marker = mMarkers.get(mContactKey);
		marker.showInfoWindow();
		mActiveMarker = marker;

		cu = CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), mDefaultZoomLevel);
		
		changeCameraPosition(cu, animate);
	}
	
	public void zoomToFriends(boolean animate) {
		CameraUpdate cu = null;
		
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		Location myLocation = ((MainActivity) getActivity()).getMyLocation();
		if (myLocation != null) {
			builder.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
		}

		for (String key: mMarkers.keySet()) {
			Marker marker = mMarkers.get(key);
			builder.include(marker.getPosition());
		}

		LatLngBounds bounds = builder.build();

		cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
		
		changeCameraPosition(cu, animate);
	}
	
	private void changeCameraPosition(CameraUpdate cu, boolean animate) {
		if (cu != null) {
			if (animate == false) {
				mGoogleMap.moveCamera(cu);	
			}
			else {
				mGoogleMap.animateCamera(cu);
			}
		}		
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	setHasOptionsMenu(true);
    	
    	mMarkers = new HashMap<String, Marker>();

    	mGoogleMap = getMap();
    	
    	Bundle bundle = this.getArguments();
    	if (bundle.containsKey(ARG_CONTACT_KEY)) {
    		mContactKey = bundle.getString(ARG_CONTACT_KEY);
    	}
    	
    	createMarkers(true);
    	
        mGoogleMap.setInfoWindowAdapter(new GInfoWindowAdapter(getActivity()));
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

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
    	createMarkers(false);	
    }

}
