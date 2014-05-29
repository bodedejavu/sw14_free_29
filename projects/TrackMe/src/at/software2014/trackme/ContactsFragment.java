package at.software2014.trackme;

import java.util.ArrayList;

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

		// TODO: load values
		ArrayList<ContactsItem> contactsList = new ArrayList<ContactsItem>();
		contactsList.add(new ContactsItem("Anna Weber"));
		contactsList.add(new ContactsItem("Benjamin Steinacher"));
		contactsList.add(new ContactsItem("Rainer Lankmayr"));

		mListAdapter = new ContactsAdapter(getActivity());
		mListAdapter.setData(contactsList);
		mListView = (ListView) view.findViewById(R.id.contacts_listView);
		mListView.setAdapter(mListAdapter);
		mListView.setSelector(R.drawable.selector_background);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View mListView,
					int position, long id) {

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
			// TODO: add contact
			Toast.makeText(getActivity(),
					R.string.action_contact_add_successful, Toast.LENGTH_LONG)
					.show();
			return true;
		case R.id.action_contact_delete:
			// TODO: delete contact
			Toast.makeText(getActivity(),
					R.string.action_contact_delete_successful,
					Toast.LENGTH_LONG).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
