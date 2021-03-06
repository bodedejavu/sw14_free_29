package at.software2014.trackme;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserData {
	
	public UserData() {
		
		allowedUsersForQuerying = new ArrayList<String>();
	}
	
	@Id
	private String userEmail;	
	
	private String userName;
	private List<String> allowedUsersForQuerying; 
	//private UserLocation location; 
	
	private double userLastLatitude; 
	private double userLastLongitude; 
	private long timestamp;
	

	public double getUserLastLatitude() {
		return userLastLatitude;
	}

	public void setUserLastLatitude(double userLastLatitude) {
		this.userLastLatitude = userLastLatitude;
	}

	public double getUserLastLongitude() {
		return userLastLongitude;
	}

	public void setUserLastLongitude(double userLastLongitude) {
		this.userLastLongitude = userLastLongitude;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public List<String> getAllowedUsersForQuerying() {
		return allowedUsersForQuerying;
	}

	public void setAllowedUsersForQuerying(List<String> allowedUsersForQuerying) {
		this.allowedUsersForQuerying = allowedUsersForQuerying;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
//	public void setLastLocation(UserLocation location)
//	{
//		this.location = location; 
//	}
//	
//	public UserLocation getLastLocation()
//	{
//		return this.location; 
//	}
	
	
}
