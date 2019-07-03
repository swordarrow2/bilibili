package com.meng.bilibilihelper.adapters;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.javaBean.LivePartList;
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;
import com.meng.bilibilihelper.javaBean.personInfo.PersonInfo;
import com.meng.bilibilihelper.libAndHelper.DownloadImageRunnable;
import com.meng.bilibilihelper.libAndHelper.HeadType;

import java.io.File;
import java.util.ArrayList;

public class NameAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private ArrayList<PersonInfo> personInfos;
    private ArrayList<PersonInfo> data;

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
                ArrayList<String> willShow = new ArrayList<>();
                if (constraint != null) {
                    for (PersonInfo listItem : personInfos) {
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
        return personInfos.size();
    }

    public Object getItem(int position) {
        return personInfos.get(position);
    }

    public long getItemId(int position) {
        return personInfos.get(position).hashCode();
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
        final PersonInfo loginInfoPeople = personInfos.get(position);
        holder.tvName.setText(loginInfoPeople.name);
        File bilibiliImageFile = new File(MainActivity.instence.mainDic + "bilibili/" + loginInfoPeople.bid + ".jpg");
        if (bilibiliImageFile.exists()) {
            holder.ivHead.setImageBitmap(BitmapFactory.decodeFile(bilibiliImageFile.getAbsolutePath()));
        } else {
            if (MainActivity.onWifi) {
                MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(activity, holder.ivHead, String.valueOf(loginInfoPeople.bid), HeadType.BilibiliUser));
            } else {
                holder.ivHead.setImageResource(R.drawable.stat_sys_download_anim0);
                holder.ivHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(activity, holder.ivHead, String.valueOf(loginInfoPeople.bid), HeadType.BilibiliUser));
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



