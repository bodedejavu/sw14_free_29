package at.software2014.trackme;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class GInfoWindowAdapter implements InfoWindowAdapter {
	private Activity mActivity = null;
	
	public GInfoWindowAdapter(Activity activity) {
		this.mActivity = activity;
	}
	
	@Override
	public View getInfoWindow(Marker marker) {
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.infowindow_gmap, null);
		
		if (marker != null) {
			TextView textView1 = (TextView) v.findViewById(R.id.textView1);
			textView1.setText(marker.getTitle());
			
			TextView textView2 = (TextView) v.findViewById(R.id.textView2);
			textView2.setText(marker.getSnippet());
			
			ImageView imageView1 = (ImageView) v.findViewById(R.id.imageView1);
			try {
				InputStream ims = mActivity.getAssets().open("ic_action_person.png");
				Drawable d = Drawable.createFromStream(ims,  null);
				imageView1.setImageDrawable(d);
			}
			catch(IOException ex) {
				return null;
			}
			
		}
		
		return v;
	}
	
	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}
}