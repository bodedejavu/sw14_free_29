package at.software2014.trackme;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
	private ListView mListView;
	private ArrayAdapter<String> mListAdapter;

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
		
		// TODO: load values
		String[] values =  new String[] {
				"Anna Weber", 
				"Benjamin Steinacher", 
				"Rainer Lankmayr"};
		ArrayList<String> friendsList = new ArrayList<String>();  
		friendsList.addAll(Arrays.asList(values)); 

		mListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, friendsList);
		mListView = (ListView) view.findViewById(R.id.contacts_listView);
		mListView.setAdapter(mListAdapter);
		
		// ListView Item Click Listener
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View mListView,
			  	int position, long id) {

	  		}
		});

		return view;
	}
}
