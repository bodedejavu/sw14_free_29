package at.software2014.trackme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
		friendslist = new ArrayList<FriendsListItem>();
		setListData();
		mListView = (ListView) view.findViewById(R.id.friendslist_listView);
		mListView.setAdapter(mListAdapter);

		// ListView Item Click Listener
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View mListView,
					int position, long id) {
				String key = friendslist.get(position).getKey();

				// TODO: call activity to fix drawer menu and action bar
				Fragment fragment = GMapFragment.newInstance(position, key);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, fragment).commit();
			}
		});
		return view;
	}

	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		super.onViewCreated(v, savedInstanceState);

		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.friendslist);

		if (fragment != null) {
			// TODO: load values?
		}
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

	private void setListData() {
		HashMap<String, ContactEntry> contacts = ((MainActivity) getActivity())
				.getContacts();
		HashMap<String, List<HistoryEntry>> history = ((MainActivity) getActivity())
				.getHistory();

		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		Location myLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		friendslist.clear();

		for (String key : contacts.keySet()) {
			ContactEntry contactEntry = contacts.get(key);
			HistoryEntry historyEntry = history.get(key).get(
					history.get(key).size() - 1);

			String name = contactEntry.getFirstName() + " "
					+ contactEntry.getSecondName();
			String date = DateFormat.getDateFormat(getActivity()).format(
					historyEntry.getTimestamp());
			String time = DateFormat.getTimeFormat(getActivity()).format(
					historyEntry.getTimestamp());
			String timestamp = date + ", " + time;
			String distance = getResources().getString(
					R.string.information_unknown);
			if (myLocation != null) {
				LatLng myPosition = new LatLng(myLocation.getLatitude(),
						myLocation.getLongitude());
				distance = historyEntry.getDistanceFormatted(myPosition);
			}
			friendslist
					.add(new FriendsListItem(key, name, distance, timestamp));
		}
		mListAdapter = new FriendsListAdapter(getActivity());
		mListAdapter.setData(friendslist);
	}
}
