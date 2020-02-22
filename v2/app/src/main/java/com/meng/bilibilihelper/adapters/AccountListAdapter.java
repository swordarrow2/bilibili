package com.meng.bilibilihelper.adapters;

import android.graphics.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.libAndHelper.*;
import java.io.*;
import java.util.*;

public class AccountListAdapter extends BaseAdapter {
    private MainActivity activity;
    private ArrayList<Boolean> checked = new ArrayList<>();

    public AccountListAdapter(MainActivity context) {
        this.activity = context;
        for (int i=0,j=activity.loginAccounts.size();i < j;++i) {
            checked.add(false);
        }
    }

    public int getCount() {
        return activity.loginAccounts.size();
    }

    public boolean getChecked(int position) {
        return checked.get(position);
    }

    public Object getItem(int position) {
        return activity.loginAccounts.get(position);
    }

    public long getItemId(int position) {
        return activity.loginAccounts.get(position).hashCode();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.list_item_image_text_switch, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.group_reply_list_itemTextView);
            holder.aSwitch = (Switch) convertView.findViewById(R.id.group_reply_list_itemSwitch);
            holder.ivHeader = (ImageView) convertView.findViewById(R.id.group_reply_list_itemImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AccountInfo account = activity.loginAccounts.get(position);
        holder.tvName.setText(account.name);
        holder.aSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton p1, boolean p2) {
					checked.set(position, p2);
				}
			});
        holder.aSwitch.setChecked(checked.get(position));
        File bilibiliImageFile = new File(MainActivity.instance.mainDic + "bilibili/" + account.uid + ".jpg");
        if (bilibiliImageFile.exists()) {
            holder.ivHeader.setImageBitmap(BitmapFactory.decodeFile(bilibiliImageFile.getAbsolutePath()));
        } else {
            if (MainActivity.onWifi) {
                MainActivity.instance.getFragment("人员信息", PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(activity, holder.ivHeader, String.valueOf(account.uid), DownloadImageRunnable.BilibiliUser));
            } else {
                holder.ivHeader.setImageResource(R.drawable.stat_sys_download_anim0);
                holder.ivHeader.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							MainActivity.instance.getFragment("人员信息", PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(activity, holder.ivHeader, String.valueOf(account.uid), DownloadImageRunnable.BilibiliUser));
						}
					});
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView ivHeader;
        private TextView tvName;
        private Switch aSwitch;
    }
}



