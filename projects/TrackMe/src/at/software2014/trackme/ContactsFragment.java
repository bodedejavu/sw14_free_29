package at.software2014.trackme;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final int CONTACT_PICKER_RESULT = 1001;
	private ListView mListView;
	private ContactsAdapter mListAdapter;
	private ContactsItem mSelectedItem;

	public static Fragment newInstance(int position) {
		ContactsFragment fragment = new ContactsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, position);
		fragment.setArguments(args);
		return fragment;
	}

	public ContactsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contacts, container,
				false);
		setHasOptionsMenu(true);

		mListAdapter = new ContactsAdapter(getActivity());
		setListData();
		// mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mListView = (ListView) view.findViewById(R.id.contacts_listView);
		mListView.setAdapter(mListAdapter);
		mListView.setSelector(R.drawable.selector_background);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View mListView,
					int position, long id) {
				mSelectedItem = mListAdapter.getItem(position);
			}
		});
		return view;
	}

	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		super.onViewCreated(v, savedInstanceState);

		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.contacts);

		if (fragment != null) {
			// TODO: load values?
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.contacts);
		if (fragment != null) {
			fragmentManager.beginTransaction().remove(fragment).commit();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (menu != null) {
			menu.findItem(R.id.action_refresh).setVisible(false);
		}
		inflater.inflate(R.menu.contacts, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.action_contact_invite:
			inviteContact();
			return true;
		case R.id.action_contact_add:
			addContact();
			return true;
		case R.id.action_contact_delete:
			deleteContact();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setListData() {
		HashMap<String, ContactEntry> contacts = ((MainActivity) getActivity())
				.getContacts();
		ArrayList<ContactsItem> contactsList = new ArrayList<ContactsItem>();

		for (String key : contacts.keySet()) {
			ContactEntry contactEntry = contacts.get(key);
			String name = contactEntry.getFirstName() + " "
					+ contactEntry.getSecondName();
			contactsList.add(new ContactsItem(key, name));
		}

		mListAdapter.setData(contactsList);
	}

	private void inviteContact() {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case CONTACT_PICKER_RESULT:
				String email = "";
				Uri result = data.getData();
				String id = result.getLastPathSegment();

				Cursor cursor = getActivity().getContentResolver().query(
						Email.CONTENT_URI, null, Email.CONTACT_ID + "=?",
						new String[] { id }, null);
				// cursor.moveToFirst();

				if (cursor.moveToFirst()) {
					int emailIdx = cursor.getColumnIndex(Email.DATA);
					email = cursor.getString(emailIdx);
				}

				if (email.length() == 0) {
					Toast.makeText(getActivity(),
							R.string.action_contact_invite_no_email,
							Toast.LENGTH_LONG).show();
				} else {
					openInvitationMail(email);
				}
				break;
			}
		} else {
			Toast.makeText(getActivity(),
					R.string.action_contact_invite_cp_fail, Toast.LENGTH_LONG)
					.show();
		}
	}

	private void openInvitationMail(String mailAddress) {
		String aEmailList[] = { mailAddress };
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"TrackMe Invitation");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
				"Hello,\n i am inviting you to download and test the APP TrackMe.\n link");
		startActivity(emailIntent);
	}

	private void addContact() {
		// TODO: implement addContact
		Toast.makeText(getActivity(), R.string.action_contact_add_successful,
				Toast.LENGTH_LONG).show();
	}

	private void deleteContact() {

		if (mSelectedItem != null) {
			String key = mSelectedItem.getKey();
			if (((MainActivity) getActivity()).deleteContact(key)) {
				setListData();
				mListAdapter.notifyDataSetChanged();

				Toast.makeText(getActivity(),
						R.string.action_contact_delete_successful,
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getActivity(),
						R.string.action_contact_delete_failed,
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getActivity(),
					R.string.action_contact_delete_no_selection,
					Toast.LENGTH_LONG).show();
		}
	}
}
