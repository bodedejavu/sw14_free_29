package at.software2014.trackme;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
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
		ArrayList<ContactsItem> contactsList = new ArrayList<ContactsItem>();
		ArrayList<String> userEMail = new ArrayList<String>();
		ArrayList<String> userNames = new ArrayList<String>();
		
		if (getIntent().getExtras() != null) {
			userEMail = getIntent().getStringArrayListExtra("UserEMail");
			userNames = getIntent().getStringArrayListExtra("UserNames");
			
			for (int i = 0; i < userEMail.size(); i++) {
				String eMail = userEMail.get(i);
				String name = userNames.get(i);
				contactsList.add(new ContactsItem(eMail, name));
			}
        }
		
		TextView empty = (TextView) this.findViewById(
				R.id.contacts_add_empty);

		if (contactsList.isEmpty()) {			
			empty.setVisibility(View.VISIBLE);
		} else {
			empty.setVisibility(View.INVISIBLE);
		}

		mListAdapter.setData(contactsList);
	}

	@Override
	public void finish() {
		if (mSelectedItem != null) {
			Intent intent = new Intent();
			intent.putExtra("eMail", mSelectedItem.getKey());
			setResult(RESULT_OK, intent);
		} else {
			setResult(RESULT_CANCELED);
		}
		super.finish();
	}
}
