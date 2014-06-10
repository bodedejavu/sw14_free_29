package at.software2014.trackme;

import android.content.Context;
import android.location.Location;
import android.text.format.DateFormat;

public class ContactEntry {

	private String name = "";
	private String eMail = "";
	private long timestamp = 0;
	private double latitude = 0.0;
	private double longitude = 0.0;
	
	public ContactEntry(String name, String eMail, long timestamp,
			double latitude, double longitude) {
		super();
		this.name = name;
		this.eMail = eMail;
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public String getTimestampFormatted(Context context) {
		//String timestamp = historyEntry.getTimestamp().toLocaleString();
		String date = DateFormat.getDateFormat(context).format(timestamp);
		String time = DateFormat.getTimeFormat(context).format(timestamp);
		String timestampFormatted = date + ", " + time;
		return timestampFormatted;
	}
	
	public String getDistanceFormmated(Location locationTo, String textUnknown) {
		String distanceFormatted;
		
		Location location = new Location("dummy");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		
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
