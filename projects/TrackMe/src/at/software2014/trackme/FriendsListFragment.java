package at.software2014.trackme;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FriendsListFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
	private ListView mListView;
	private FriendsListAdapter mListAdapter;
	private ArrayList<FriendsListItem> friendslist;

	public static Fragment newInstance(int position) {
		FriendsListFragment fragment = new FriendsListFragment();

		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, position);
		fragment.setArguments(args);
		return fragment;
	}

	public FriendsListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friendslist, container,
				false);
		setHasOptionsMenu(true);

		mListAdapter = new FriendsListAdapter(getActivity());
		friendslist = new ArrayList<FriendsListItem>();
		mListView = (ListView) view.findViewById(R.id.friendslist_listView);
		mListView.setAdapter(mListAdapter);

		// ListView Item Click Listener
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View mListView,
					int position, long id) {
				String key = friendslist.get(position).getKey();
				((MainActivity) getActivity()).selectItem(0, key);
			}
		});
		return view;
	}

	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		super.onViewCreated(v, savedInstanceState);
		setListData();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.friendslist);
		if (fragment != null) {
			fragmentManager.beginTransaction().remove(fragment).commit();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.friends, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.action_friends_refresh:
			// TODO refresh data
			Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_LONG).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setListData() {
		friendslist.clear();
		Location myLocation = ((MainActivity) getActivity()).getMyLocation();
		List<ContactEntry> contacts = ((MainActivity) getActivity())
				.getContacts();
		
		TextView empty = (TextView) this.getView().findViewById(
				R.id.friendslist_empty);

		if (contacts.isEmpty()) {			
			empty.setVisibility(View.VISIBLE);
		} else {
			empty.setVisibility(View.INVISIBLE);
			
			for (int i = 0; i < contacts.size(); i++) {
				ContactEntry contactEntry = contacts.get(i);
	
				String eMail = contactEntry.geteMail();
				String name = contactEntry.getName();
				String timestamp = contactEntry
						.getTimestampFormatted(getActivity());
				String distance = contactEntry.getDistanceFormatted(myLocation,
						getResources().getString(R.string.information_unknown));
	
				friendslist.add(new FriendsListItem(eMail, name, distance,
						timestamp));
			}
		}
		
		mListAdapter.setData(friendslist);
		// mListAdapter.notifyDataSetChanged();
	}

	public void refreshLocation() {
		setListData();
	}
}
