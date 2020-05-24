package com.meng.biliv3.adapters;

import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.meng.biliv3.javabean.*;
import com.meng.biliv3.libs.*;
import com.meng.sjfmd.*;
import java.io.*;

public class AccountAdapter extends BaseAdapter {
    private MainActivity activity;

    public AccountAdapter(MainActivity context) {
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
            convertView = activity.getLayoutInflater().inflate(R.layout.account_adapter, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.account_adapterTextView_name);
			holder.ivHead = (ImageView) convertView.findViewById(R.id.account_adapterImageView_head);
			convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AccountInfo accountInfo = activity.loginAccounts.get(position);
		holder.tvName.setText(accountInfo.name);
		holder.tvName.setTextColor(accountInfo.isCookieExceed() ?Color.RED: MainActivity.instance.colorManager.getColorText());
		File bilibiliImageFile = new File(MainActivity.instance.mainDic + "bilibili/" + accountInfo.uid + ".jpg");
        if (bilibiliImageFile.exists()) {
            holder.ivHead.setImageBitmap(BitmapFactory.decodeFile(bilibiliImageFile.getAbsolutePath()));
        } else {
            if (MainActivity.onWifi) {
                MainActivity.instance.threadPool.execute(new DownloadImageRunnable(holder.ivHead, accountInfo.uid, DownloadImageRunnable.BilibiliUser));
            } else {
                holder.ivHead.setImageResource(R.drawable.stat_sys_download_anim0);
                holder.ivHead.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							MainActivity.instance.threadPool.execute(new DownloadImageRunnable(holder.ivHead, accountInfo.uid, DownloadImageRunnable.BilibiliUser));
						}
					});
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView ivHead;
        private TextView tvName;
    }
}



