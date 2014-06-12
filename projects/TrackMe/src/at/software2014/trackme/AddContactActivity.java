package at.software2014.trackme;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

		setListData();

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View mListView,
					int position, long id) {
				mSelectedItem = mListAdapter.getItem(position);
				showAffirmationDialog();
			}
		});
	}

	private void showAffirmationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.action_contact_add_dialog_title);
		builder.setMessage(R.string.action_contact_add_dialog_text);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mSelectedItem = null;
						mListView.clearChoices();
						mListView.requestLayout();
						dialog.cancel();
					}
				});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void setListData() {
		List<ContactEntry> contacts = new ArrayList<ContactEntry>();
		ArrayList<ContactsItem> contactsList = new ArrayList<ContactsItem>();

		contacts.add(new ContactEntry("Anna Weber", "anna.weber@gmail.com",
				(long) 1401216003 * 1000, 47.1, 15.4));
		contacts.add(new ContactEntry("Rainer Lankmayr",
				"rainer.lankmayr@gmail.com", (long) 1401215993 * 1000, 47.0,
				15.5));
		contacts.add(new ContactEntry("Benjamin Steinacher",
				"benjamin.steinacher@gmail.com", (long) 1401216000 * 1000,
				47.08, 15.35));

		for (int i = 0; i < contacts.size(); i++) {
			ContactEntry contactEntry = contacts.get(i);
			String eMail = contactEntry.geteMail();
			String name = contactEntry.getName();
			contactsList.add(new ContactsItem(eMail, name));
		}

		mListAdapter.setData(contactsList);
	}

	@Override
	public void finish() {
		if (mSelectedItem != null) {
			Intent data = new Intent();
			data.putExtra("eMail", mSelectedItem.getKey());
			setResult(RESULT_OK, data);
		} else {
			setResult(RESULT_CANCELED);
		}
		super.finish();
	}
}
