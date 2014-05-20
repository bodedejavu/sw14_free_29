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
import android.os.Bundle;


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