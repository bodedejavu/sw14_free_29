package at.software2014.trackme;

import java.util.HashMap;
import java.util.List;
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
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import at.software2014.trackme.R.string;


/**
 * A fragment containing a google map.
 */
public class GMapFragment extends MapFragment {
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
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	mGoogleMap = getMap();

    	LocationManager locationManager = (LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);
    	Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	
    	HashMap<String, ContactEntry> contacts = ((MainActivity)getActivity()).getContacts();
    	HashMap<String, List<HistoryEntry>> history = ((MainActivity)getActivity()).getHistory();
    	
    	LatLngBounds.Builder builder = new LatLngBounds.Builder();
    	
    	for (String key: contacts.keySet()) {
    		ContactEntry contactEntry = contacts.get(key);
    		HistoryEntry historyEntry = history.get(key).get(history.get(key).size() - 1);
    		
    		String title = contactEntry.getFirstName() + " " + contactEntry.getSecondName();
    		
    		LatLng position = historyEntry.getLatLng();
    		
    		String timestamp = historyEntry.getTimestamp().toLocaleString();
    		String distance = getResources().getString(R.string.information_unknown);
        	if (lastLocation != null) {
        		LatLng myPosition = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        		distance = historyEntry.getDistanceFormatted(myPosition);
        	}
        	String snippet = getResources().getString(R.string.information_distance) + ": " + distance + "\n" + getResources().getString(R.string.information_last_update) + ": " + timestamp;
        	
    		Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(position).title(title).snippet(snippet));
			
    		builder.include(marker.getPosition());
    	}
    	
        mBounds = builder.build();
        
        mGoogleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			
			@Override
			public void onCameraChange(CameraPosition arg0) {
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(mBounds, 100);
				mGoogleMap.moveCamera(cu);
				mGoogleMap.setOnCameraChangeListener(null);
			}
		});

        mGoogleMap.setInfoWindowAdapter(new GInfoWindowAdapter(getActivity()));
        mGoogleMap.setMyLocationEnabled(true);

    }

}
