package at.software2014.trackme;

public class ContactsItem implements Comparable<ContactsItem> {
	private String mKey;
	private String mName;

	public ContactsItem(String key, String name) {
		this.mKey = key;
		this.mName = name;
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

	@Override
	public int compareTo(ContactsItem contactsItem) {
		return this.mName.compareTo(contactsItem.getName());
	}
}
