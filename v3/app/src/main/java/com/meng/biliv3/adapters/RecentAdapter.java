package com.meng.biliv3.adapters;

import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import java.util.*;

public class RecentAdapter extends BaseAdapter {
    private ArrayList<String> names = new ArrayList<>();

	public void add(String s) {
		names.add(0, s);
		notifyDataSetChanged();
	}

	public void remove(String id) {
//		MainActivity.instance.hideFragment();
//		MainActivity.instance.removeFragment(id);
		for (int i=0;i < names.size();++i) {
			if (names.get(i).equals(id)) {
				names.remove(i);
				break;
			}
		}
		notifyDataSetChanged();
	}

    public int getCount() {
        return names.size();
    }

    public Object getItem(int position) {
        return names.get(position);
    }

    public long getItemId(int position) {
        return names.get(position).hashCode();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = MainActivity.instance.getLayoutInflater().inflate(R.layout.recent_list_item, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.recent_list_itemTextView);
            holder.close = (ImageButton) convertView.findViewById(R.id.recent_list_itemImageButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String s = names.get(position);
        holder.tvName.setText(s);
		holder.tvName.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1) {
					MainActivity.instance.showFragment(holder.tvName.getText().toString());
				}
			});
		holder.close.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1) {
					remove(s);
				}
			});
        return convertView;
    }

    private class ViewHolder {
        private TextView tvName;
        private ImageButton close;
    }
}



