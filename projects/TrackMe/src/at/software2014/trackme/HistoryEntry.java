package at.software2014.trackme;

import java.util.Date;

import android.location.Location;

public class HistoryEntry {

	private Date timestamp = null;
	private Location location = null;
	private Integer statusFlag = 0;
	private String statusText = "";
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
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
	
	public HistoryEntry(Date timestamp, Location location, Integer statusFlag,
			String statusText) {
		super();
		this.timestamp = timestamp;
		this.location = location;
		this.statusFlag = statusFlag;
		this.statusText = statusText;
	}

	public String getDistanceFormatted(Location locationTo) {
		String distanceFormatted;
		
		float distance = location.distanceTo(locationTo);
		
		if (distance < 1000.0) {
			distanceFormatted = String.format("%.0f", distance) + " m";
		}
		else {
			distanceFormatted = String.format("%.2f", distance / 1000.0) + " km";
		}
		
		return distanceFormatted;
	}
	
}
