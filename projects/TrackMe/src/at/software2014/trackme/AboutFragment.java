package at.software2014.trackme;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";

	public static Fragment newInstance(int position) {
		AboutFragment fragment = new AboutFragment();

		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, position);
		fragment.setArguments(args);
		return fragment;
	}

	public AboutFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_about, container,
				false);

		TextView textViewTitle = (TextView) view.findViewById(R.id.about_title);
		textViewTitle.setText(getResources().getString(R.string.app_name));
		
		TextView textViewText = (TextView) view.findViewById(R.id.about_text);
		textViewText.setText(Html.fromHtml(getResources().getString(R.string.app_description)));
		textViewText.setMovementMethod(LinkMovementMethod.getInstance());
		
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.about);
		if (fragment != null) {
			fragmentManager.beginTransaction().remove(fragment).commit();
		}
	}

}
