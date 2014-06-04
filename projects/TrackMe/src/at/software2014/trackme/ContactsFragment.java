package at.software2014.trackme;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
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
