package com.meng.bilibilihelper.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.meng.bilibilihelper.javaBean.LivePartList;

import java.util.ArrayList;

public class PartListAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private ArrayList<LivePartList.ListItemInListItem> items;
    private ArrayList<String> data = new ArrayList<>();

    public PartListAdapter(Activity context, ArrayList<LivePartList.ListItemInListItem> items) {
        this.activity = context;
        this.items = items;
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(android.R.layout.simple_dropdown_item_1line, null);
        }
        ((TextView) convertView).setText(data.get(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<String> willShow = new ArrayList<>();
                if (constraint != null) {
                    for (LivePartList.ListItemInListItem listItem : items) {
                        if (listItem.name.contains(constraint)) {
                            willShow.add(listItem.name);
                        }
                    }
                }
                results.values = willShow;
                results.count = willShow.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

