package com.meng.biliv3.adapters;

import android.content.pm.*;
import android.graphics.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libs.*;
import com.meng.biliv3.update.*;
import java.io.*;
import org.java_websocket.client.*;

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
            convertView = activity.getLayoutInflater().inflate(R.layout.account_adapter, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.account_adapterTextView_name);
			holder.ivHead = (ImageView) convertView.findViewById(R.id.account_adapterImageView_head);
            holder.btnUpload = (Button) convertView.findViewById(R.id.account_adapterButton_upload);
			convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AccountInfo accountInfo = activity.loginAccounts.get(position);
		holder.btnUpload.setVisibility(isShowUpload(accountInfo.uid) ?View.VISIBLE: View.GONE);
		holder.btnUpload.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1) {
					MainActivity.instance.threadPool.execute(new Runnable(){

							@Override
							public void run() {
								try {
									RanConnect rc=RanConnect.getRanconnect();
									if (!rc.isOpen()) {
										rc.addOnOpenAction(new WebSocketOnOpenAction(){

												@Override
												public int useTimes() {
													return 1;
												}

												@Override
												public void action(WebSocketClient wsc) {
													BotDataPack bdp=BotDataPack.encode(BotDataPack.cookie);
													bdp.write((int)accountInfo.uid).write(accountInfo.cookie);
													wsc.send(bdp.getData());
												}
											});
										rc.connect();
									} else {
										BotDataPack bdp=BotDataPack.encode(BotDataPack.cookie);
										bdp.write((int)accountInfo.uid).write(accountInfo.cookie);
										rc.send(bdp.getData());	
									}
								} catch (Exception e) {
									MainActivity.instance.showToast(e.toString());
								}
							}
						});
					MainActivity.instance.showToast(accountInfo.name + "的cookie已上传");
				}
			});
		holder.tvName.setText(accountInfo.name);
		holder.tvName.setTextColor(accountInfo.isCookieExceed() ?Color.RED: Color.BLACK);
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

	private boolean isShowUpload(long uid) {
		long[] uids={

		};
		for (long l:uids) {
			if (l == uid) {
				return true;
			}
		}
		return true;
	}

    private class ViewHolder {
        private ImageView ivHead;
        private TextView tvName;
		private Button btnUpload;
		/*	private Button up;
		 private Button down;
		 private Button refresh;
		 private Button delete;*/
    }
}



