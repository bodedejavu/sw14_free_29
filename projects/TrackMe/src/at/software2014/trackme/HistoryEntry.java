package at.software2014.trackme;

import java.util.Date;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class HistoryEntry {

	private Date timestamp = null;
	private LatLng latLng = null;
	private Integer statusFlag = 0;
	private String statusText = "";
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public LatLng getLatLng() {
		return latLng;
	}
	
	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}
	
	public Integer getStatusFlag() {
		return statusFlag;
	}
	
	public void setStatusFlag(Integer statusFlag) {
		this.statusFlag = statusFlag;
	}
	
	public String getStatusText() {
		return statusText;
	}
	
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
	
	public HistoryEntry(Date timestamp, LatLng latLng, Integer statusFlag,
			String statusText) {
		super();
		this.timestamp = timestamp;
		this.latLng = latLng;
		this.statusFlag = statusFlag;
		this.statusText = statusText;
	}
	
	public float getDistance(LatLng latLngTo) {
		float distance;
		
		Location locationA = new Location("a");
		locationA.setLatitude(this.latLng.latitude);
		locationA.setLongitude(this.latLng.longitude);
		
		Location locationB = new Location("b");
		locationB.setLatitude(latLngTo.latitude);
		locationB.setLongitude(latLngTo.longitude);
		
		distance = locationA.distanceTo(locationB);
		
		return distance; 
	}
	
}
