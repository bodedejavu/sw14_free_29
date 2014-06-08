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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;


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
    
    private void createMarkers(Boolean initial) {
    	if (initial == true) {
    		mGoogleMap.clear();
        	mMarkers = new HashMap<String, Marker>();
    	}
    	Location myLocation = ((MainActivity) getActivity()).getMyLocation();
    	HashMap<String, ContactEntry> contacts = ((MainActivity)getActivity()).getContacts();
    	HashMap<String, List<HistoryEntry>> history = ((MainActivity)getActivity()).getHistory();
    	
    	for (String key: contacts.keySet()) {
    		ContactEntry contactEntry = contacts.get(key);
    		HistoryEntry historyEntry = history.get(key).get(history.get(key).size() - 1);
    		
    		String title = contactEntry.getFirstName() + " " + contactEntry.getSecondName();
    		
    		LatLng position = new LatLng(historyEntry.getLocation().getLatitude(), historyEntry.getLocation().getLongitude());
    		
    		//String timestamp = historyEntry.getTimestamp().toLocaleString();
    		String date = DateFormat.getDateFormat(getActivity()).format(historyEntry.getTimestamp());
    		String time = DateFormat.getTimeFormat(getActivity()).format(historyEntry.getTimestamp());
    		String timestamp = date + ", " + time;
    		
    		String distance = getResources().getString(R.string.information_unknown);
        	if (myLocation != null) {
        		distance = historyEntry.getDistanceFormatted(myLocation);
        	}
        	
        	String snippet = getResources().getString(R.string.information_distance) + ": " + distance + "\n" + getResources().getString(R.string.information_last_update) + ": " + timestamp;
        	
        	Marker marker;
        	
        	if (initial == true) {
        		marker = mGoogleMap.addMarker(new MarkerOptions().position(position).title(title).snippet(snippet));
        		mMarkers.put(key, marker);
        	}
        	else {
        		marker = mMarkers.get(key);
        		marker.setTitle(title);
        		marker.setPosition(position);
        		marker.setSnippet(snippet);
        		
        		if (mActiveMarker != null && marker.getTitle().equals(mActiveMarker.getTitle())) {
        			marker.hideInfoWindow();
        			marker.showInfoWindow();
        		}
        	}
    	}
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	mGoogleMap = getMap();
    	
    	Bundle bundle = this.getArguments();
    	if (bundle.containsKey(ARG_CONTACT_KEY)) {
    		mContactKey = bundle.getString(ARG_CONTACT_KEY);
    	}
    	
    	createMarkers(true);
    	
        mGoogleMap.setInfoWindowAdapter(new GInfoWindowAdapter(getActivity()));
        mGoogleMap.setMyLocationEnabled(true);
    	
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
        		
        		if (mMarkers.size() > 0) {
        			LatLngBounds.Builder builder = new LatLngBounds.Builder();
            		
            		if (mContactKey != "") {
            			Marker marker = mMarkers.get(mContactKey);
            			builder.include(marker.getPosition());
    					marker.showInfoWindow();
        				mActiveMarker = marker;
            		}
            		else {
            			for (String key: mMarkers.keySet()) {
            				Marker marker = mMarkers.get(key);
            				builder.include(marker.getPosition());
                		}
            		}
            		
            		LatLngBounds bounds = builder.build();
            		
            		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
    				
    				mGoogleMap.moveCamera(cu);
    				mGoogleMap.setOnCameraChangeListener(null);
        		}
			}
		});

    }
    
    public void refreshLocation() {
    	createMarkers(false);	
    }

}
