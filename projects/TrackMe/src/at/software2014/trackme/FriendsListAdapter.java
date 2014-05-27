package at.software2014.trackme;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FriendsListAdapter extends ArrayAdapter<FriendsListItem> {
	
	private final LayoutInflater mInflater;
	 
    public FriendsListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
  
    public void setData(List<FriendsListItem> data) {
        clear();
        if (data != null) {
            for (FriendsListItem item : data) {
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
 
        FriendsListItem item = getItem(position);
        ((TextView)view.findViewById(R.id.friend_name)).setText(item.getName());
        ((TextView)view.findViewById(R.id.friend_distance)).setText(item.getDistance());
        ((TextView)view.findViewById(R.id.friend_timestamp)).setText(item.getTimestamp());
 
        return view;
    }
}
