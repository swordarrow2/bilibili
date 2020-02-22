package com.meng.bilibilihelper.adapters;

import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.libAndHelper.*;
import java.io.*;

public class MainListAdapter extends BaseAdapter {
    private MainActivity activity;

    public MainListAdapter(MainActivity context) {
        activity = context;
    }

    public int getCount() {
        return activity.loginAccounts.size();
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
            holder.aSwitch.setVisibility(View.GONE);
            holder.ivHead = (ImageView) convertView.findViewById(R.id.group_reply_list_itemImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AccountInfo accountInfo = activity.loginAccounts.get(position);
        holder.tvName.setText(accountInfo.name);
        File bilibiliImageFile = new File(MainActivity.instance.mainDic + "bilibili/" + accountInfo.uid + ".jpg");
        if (bilibiliImageFile.exists()) {
            holder.ivHead.setImageBitmap(BitmapFactory.decodeFile(bilibiliImageFile.getAbsolutePath()));
        } else {
            if (MainActivity.onWifi) {
                MainActivity.instance.getFragment("人员信息", PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(activity, holder.ivHead, String.valueOf(accountInfo.uid), DownloadImageRunnable.BilibiliUser));
            } else {
                holder.ivHead.setImageResource(R.drawable.stat_sys_download_anim0);
                holder.ivHead.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							MainActivity.instance.getFragment("人员信息", PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(activity, holder.ivHead, String.valueOf(accountInfo.uid), DownloadImageRunnable.BilibiliUser));
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



