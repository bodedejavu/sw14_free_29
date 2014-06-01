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

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateFormat;


/**
 * A fragment containing a google map.
 */
public class GMapFragment extends MapFragment implements android.location.LocationListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private GoogleMap mGoogleMap = null;
    private HashMap<String, Marker> mMarkers;
    private Marker mActiveMarker;
    private String mMoveToContactKey = "";

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
    
    public static GMapFragment newInstance(int sectionNumber, String moveToContactKey) {
    	
        GMapFragment fragment = new GMapFragment();
        
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("MOVE_TO_CONTACT_KEY", moveToContactKey);
        fragment.setArguments(args);
        return fragment;
    }

    public GMapFragment() {
    }
    
    public void createMarkers(Location myLocation, Boolean initial) {
    	if (initial == true) {
    		mGoogleMap.clear();
        	mMarkers = new HashMap<String, Marker>();
    	}
    	
    	HashMap<String, ContactEntry> contacts = ((MainActivity)getActivity()).getContacts();
    	HashMap<String, List<HistoryEntry>> history = ((MainActivity)getActivity()).getHistory();
    	
    	for (String key: contacts.keySet()) {
    		ContactEntry contactEntry = contacts.get(key);
    		HistoryEntry historyEntry = history.get(key).get(history.get(key).size() - 1);
    		
    		String title = contactEntry.getFirstName() + " " + contactEntry.getSecondName();
    		
    		LatLng position = historyEntry.getLatLng();
    		
    		//String timestamp = historyEntry.getTimestamp().toLocaleString();
    		String date = DateFormat.getDateFormat(getActivity()).format(historyEntry.getTimestamp());
    		String time = DateFormat.getTimeFormat(getActivity()).format(historyEntry.getTimestamp());
    		String timestamp = date + ", " + time;
    		
    		String distance = getResources().getString(R.string.information_unknown);
        	if (myLocation != null) {
        		LatLng myPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        		distance = historyEntry.getDistanceFormatted(myPosition);
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
    	if (bundle.containsKey("MOVE_TO_CONTACT_KEY")) {
    		mMoveToContactKey = bundle.getString("MOVE_TO_CONTACT_KEY");
    	}
    	
		LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
    	Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	
    	createMarkers(myLocation, true);
    	
        mGoogleMap.setInfoWindowAdapter(new GInfoWindowAdapter(getActivity()));
        mGoogleMap.setMyLocationEnabled(true);
    	
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
		
        mGoogleMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				mActiveMarker = null;
			}
		});
        
        mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				mActiveMarker = marker;
				return false;
			}
		});
        
        mGoogleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
        	
        	@Override
        	public void onCameraChange(CameraPosition arg0) {
        		
        		LatLngBounds.Builder builder = new LatLngBounds.Builder();
        		
        		if (mMoveToContactKey != "") {
        			Marker marker = mMarkers.get(mMoveToContactKey);
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
		});

    }
    
    @Override
    public void onDestroyView() {
    	super.onDestroyView();
    	
		LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
    	locationManager.removeUpdates(this);
    }
    
    @Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		createMarkers(location, false);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}
