package at.software2014.trackme;

public class FriendsListItem implements Comparable<FriendsListItem> {
	private String mKey;
	private String mName;
	private String mDistanceFormatted;
	private String mTimestamp;
	private Float mDistance;

	public FriendsListItem(String key, String name, String distanceFormatted,
			String timestamp, Float distance) {
		this.mKey = key;
		this.mName = name;
		this.mDistanceFormatted = distanceFormatted;
		this.mTimestamp = timestamp;
		this.mDistance = distance;
	}

	public String getKey() {
		return mKey;
	}

	public void setKey(String key) {
		mKey = key;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getDistanceFormatted() {
		return mDistanceFormatted;
	}

	public void setDistanceFormatted(String distanceFormatted) {
		this.mDistanceFormatted = distanceFormatted;
	}
	
	public Float getDistance() {
		return mDistance;
	}

	public void setDistance(Float distance) {
		this.mDistance = distance;
	}

	public String getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(String timestamp) {
		this.mTimestamp = timestamp;
	}

	@Override
	public int compareTo(FriendsListItem friendsListItem) {
		return this.mDistance.compareTo(friendsListItem.getDistance());
	}
}
