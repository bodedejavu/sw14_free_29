package at.software2014.trackme;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AddContactActivity extends BaseActivity {
	
	private ListView mListView;
	private ContactsAdapter mListAdapter;
	private ContactsItem mSelectedItem;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_add_contact);

		mListAdapter = new ContactsAdapter(this);
		mListView = (ListView) findViewById(R.id.contacts_add_listView);
		mListView.setAdapter(mListAdapter);
		mListView.setSelector(R.drawable.selector_background);
		
		setListData();
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View mListView,
					int position, long id) {
				mSelectedItem = mListAdapter.getItem(position);
			}
		});
	}
	
	private void setListData() {
		List<ContactEntry> contacts = new ArrayList<ContactEntry>();
		ArrayList<ContactsItem> contactsList = new ArrayList<ContactsItem>();
		
		contacts.add(new ContactEntry("Anna Weber", "anna.weber@gmail.com", (long)1401216003*1000, 47.1, 15.4));
		contacts.add(new ContactEntry("Rainer Lankmayr", "rainer.lankmayr@gmail.com", (long)1401215993*1000, 47.0, 15.5));
		contacts.add(new ContactEntry("Benjamin Steinacher", "benjamin.steinacher@gmail.com", (long)1401216000*1000, 47.08, 15.35));
		
    	for (int i=0; i < contacts.size(); i++) {
    		ContactEntry contactEntry = contacts.get(i);    		
    		String eMail = contactEntry.geteMail();
    		String name = contactEntry.getName();
    		contactsList.add(new ContactsItem(eMail, name));
    	}
    	
		mListAdapter.setData(contactsList);
	}
}
