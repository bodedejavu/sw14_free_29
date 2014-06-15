package at.software2014.trackme;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final int REQUEST_CODE_CONTACT_PICKER = 1001;
	private static final int REQUEST_CODE_ADD = 1002;
	private MainActivity mMainActivity;
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
		
		mMainActivity = (MainActivity) getActivity();
		setListData();
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
		List<ContactEntry> contacts = mMainActivity.getContacts();
		ArrayList<ContactsItem> contactsList = new ArrayList<ContactsItem>();
		
		TextView empty = (TextView) this.getView().findViewById(
				R.id.contacts_empty);

		if (contacts.isEmpty()) {			
			empty.setVisibility(View.VISIBLE);
		} else {
			empty.setVisibility(View.INVISIBLE);

			for (int i = 0; i < contacts.size(); i++) {
				ContactEntry contactEntry = contacts.get(i);
				String eMail = contactEntry.geteMail();
				String name = contactEntry.getName();
				contactsList.add(new ContactsItem(eMail, name));
			}
		}

		mListAdapter.setData(contactsList);
	}

	private void inviteContact() {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, REQUEST_CODE_CONTACT_PICKER);
	}
	
	private void openInvitationMail(String mailAddress) {
		String aEmailList[] = { mailAddress };
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
		emailIntent
				.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources()
						.getString(R.string.action_contact_invite_suject));
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources()
				.getString(R.string.action_contact_invite_text));
		startActivity(emailIntent);
	}

	private void addContact() {
		mMainActivity.loadRegisteredUsers();
		List<ContactEntry> registeredUsers = mMainActivity.getRegisteredUsers();
		List<ContactEntry> contacts = mMainActivity.getContacts();
		List<ContactEntry> relevantUsers = new ArrayList<ContactEntry>();
		String myEMail = mMainActivity.getEmailAddress();
		
		// relevantUsers = RegisteredUsers - contacts - myEMail
		for (ContactEntry registeredUser : registeredUsers) {
			if (!registeredUser.geteMail().equals(myEMail)) {
				boolean isContained = false;
				for (ContactEntry contact : contacts) {
					if(registeredUser.geteMail().equals(contact.geteMail())) {
						isContained = true;
					}
				}
				if(!isContained) {
					relevantUsers.add(registeredUser);
				}
			}
		}
		
		ArrayList<String> userEMail = new ArrayList<String>();
		ArrayList<String> userNames = new ArrayList<String>();
		
		if (!relevantUsers.isEmpty()) {			
			for (ContactEntry contactEntry : relevantUsers) {
				userEMail.add(contactEntry.geteMail());
				userNames.add(contactEntry.getName());
			}
		}
		
		Intent intent = new Intent(getActivity(), AddContactActivity.class);
		intent.putStringArrayListExtra("UserEMail", (ArrayList<String>) userEMail);
		intent.putStringArrayListExtra("UserNames", (ArrayList<String>) userNames);
		startActivityForResult(intent, REQUEST_CODE_ADD);
	}

	private void deleteContact() {
		if (mSelectedItem != null) {
			String key = mSelectedItem.getKey();
			mMainActivity.deleteContact(key);
		} else {
			Toast.makeText(getActivity(),
					R.string.toast_contact_delete_no_selection,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CONTACT_PICKER:
			if (resultCode == Activity.RESULT_OK) {
				String email = "";
				Uri result = data.getData();
				String id = result.getLastPathSegment();

				Cursor cursor = getActivity().getContentResolver().query(
						Email.CONTENT_URI, null, Email.CONTACT_ID + "=?",
						new String[] { id }, null);

				if (cursor.moveToFirst()) {
					int emailIdx = cursor.getColumnIndex(Email.DATA);
					email = cursor.getString(emailIdx);
				}

				if (email.length() != 0) {
					openInvitationMail(email);
				} else {
					Toast.makeText(getActivity(),
							R.string.toast_contact_invite_no_email,
							Toast.LENGTH_LONG).show();
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// do nothing
			} else {
				Toast.makeText(getActivity(),
						R.string.toast_contact_invite_cp_fail,
						Toast.LENGTH_LONG).show();
			}
			break;

		case REQUEST_CODE_ADD:
			if (resultCode == Activity.RESULT_OK) {
				String eMail = data.getExtras().getString("eMail");
				mMainActivity.addContact(eMail);
				Toast.makeText(getActivity(), getResources()
						.getString(R.string.toast_contact_add_in_progress) + 
						" ...\n" + eMail, Toast.LENGTH_LONG).show();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// do nothing
			} else {
				// do nothing
			}
			break;
		}
	}
	
	public void refreshContactList() {
		setListData();
		//mListAdapter.notifyDataSetChanged();
		mSelectedItem = null;
		mListView.clearChoices();
	}
}
