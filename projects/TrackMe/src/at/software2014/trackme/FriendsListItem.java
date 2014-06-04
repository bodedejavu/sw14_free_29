package at.software2014.trackme;

public class FriendsListItem {
	private String mKey;
	private String mName;
	private String mDistance;
	private String mTimestamp;

	public FriendsListItem(String key, String name, String distance,
			String timestamp) {
		this.mKey = key;
		this.mName = name;
		this.mDistance = distance;
		this.mTimestamp = timestamp;
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

	public String getDistance() {
		return mDistance;
	}

	public void setDistance(String distance) {
		this.mDistance = distance;
	}

	public String getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(String timestamp) {
		this.mTimestamp = timestamp;
	}
}
