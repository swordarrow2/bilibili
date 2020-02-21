package com.meng.bilibilihelper.adapters;

import android.app.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.javaBean.personInfo.*;
import com.meng.bilibilihelper.libAndHelper.*;
import java.io.*;
import java.util.*;

public class NameAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private ArrayList<PersonInfo> personInfos;
    private ArrayList<PersonInfo> data=new ArrayList<>();

    public NameAdapter(Activity context, ArrayList<PersonInfo> personInfo) {
        this.activity = context;
        this.personInfos = personInfo;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<PersonInfo> willShow = new ArrayList<>();
                if (constraint != null) {
                    for (PersonInfo listItem : personInfos) {
                        if (listItem.name.contains(constraint)) {
                            willShow.add(listItem);
                        }
                    }
                }
                results.values = willShow;
                results.count = willShow.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (ArrayList) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position).bid;
    }

    public long getItemId(int position) {
        return data.get(position).hashCode();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.list_item_image_text_switch, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.group_reply_list_itemTextView);
            holder.aSwitch = (Switch) convertView.findViewById(R.id.group_reply_list_itemSwitch);
            holder.aSwitch.setVisibility(View.GONE);
            holder.ivHead = (ImageView) convertView.findViewById(R.id.group_reply_list_itemImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final PersonInfo loginInfoPeople = data.get(position);
        holder.tvName.setText(loginInfoPeople.name);
        File bilibiliImageFile = new File(MainActivity.instence.mainDic + "bilibili/" + loginInfoPeople.bid + ".jpg");
        if (bilibiliImageFile.exists()) {
            holder.ivHead.setImageBitmap(BitmapFactory.decodeFile(bilibiliImageFile.getAbsolutePath()));
        } else {
            if (MainActivity.onWifi) {
                MainActivity.instence.getFragment("人员信息",PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(activity, holder.ivHead, String.valueOf(loginInfoPeople.bid), DownloadImageRunnable.BilibiliUser));
            } else {
                holder.ivHead.setImageResource(R.drawable.stat_sys_download_anim0);
                holder.ivHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.instence.getFragment("人员信息",PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(activity, holder.ivHead, String.valueOf(loginInfoPeople.bid), DownloadImageRunnable.BilibiliUser));
                    }
                });
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView ivHead;
        private TextView tvName;
        private Switch aSwitch;
    }
}



