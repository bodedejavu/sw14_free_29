package at.software2014.trackme;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContactsAdapter extends ArrayAdapter<ContactsItem> {
	private final LayoutInflater mInflater;
	 
    public ContactsAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
  
    public void setData(List<ContactsItem> data) {
        clear();
        if (data != null) {
            for (ContactsItem item : data) {
                add(item);
            }
        }
    }
    
    /**
     * Populate new items in the list.
     */
    @Override 
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
 
        if (convertView == null) {
            view = mInflater.inflate(R.layout.friendslist_item, parent, false);
        } else {
            view = convertView;
        }
 
        ContactsItem item = getItem(position);
        ((TextView)view.findViewById(R.id.contacts_name)).setText(item.getName());
 
        return view;
    }
}
