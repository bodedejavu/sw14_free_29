package at.software2014.trackme;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FriendsListFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";

	public static Fragment newInstance(int position) {

		FriendsListFragment fragment = new FriendsListFragment();

		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, position);
		fragment.setArguments(args);
		return fragment;
	}


	private ListView mListView;
	private ArrayAdapter<String> mListAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Context context = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
		LayoutInflater localInflater = inflater.cloneInContext(context);
		View view = localInflater.inflate(R.layout.fragment_frindslist, container, false);

		// TODO: load values
		String[] values =  new String[] {
				"Anna Weber                    200m   10:30", 
				"Benjamin Steinacher     250m   10:15", 
		"Rainer Lankmayr              2km   10:30"};
		ArrayList<String> friendsList = new ArrayList<String>();  
		friendsList.addAll(Arrays.asList(values)); 

		mListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, friendsList);
		mListView = (ListView) view.findViewById(R.id.friendslist_listView);
		mListView.setAdapter(mListAdapter);

		return view;
	}

	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		super.onViewCreated(v, savedInstanceState);

		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.friendslist);

		if (fragment != null) {
			// TODO: load values
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.friendslist);
		if (fragment != null) {
			fragmentManager.beginTransaction()
			.remove(fragment)
			.commit();
		}
	}

}
