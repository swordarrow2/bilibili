package com.meng.biliv3.customView;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javabean.*;
import com.meng.biliv3.libs.*;
import com.meng.biliv3.result.*;
import java.io.*;

public class UserInfoHeaderView extends LinearLayout {

	private ImageView ivHead;
    private TextView tvName;
    private TextView tvBMain;
	private TextView tvBLive;

    public UserInfoHeaderView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.main_account_list_header, this);
		ivHead = (ImageView) findViewById(R.id.main_account_list_headerImageView_header);
        tvName = (TextView) findViewById(R.id.main_account_list_headerTextView_name);
        tvBMain = (TextView) findViewById(R.id.main_account_list_headerTextView_bmain);
		tvBLive = (TextView) findViewById(R.id.main_account_list_headerTextView_blive);

		setBackgroundColor(MainActivity.instance.colorManager.getColorDrawerHeader());
		long mainUID = MainActivity.instance.sjfSettings.getMainAccount();
		if (mainUID == -1) {
			tvName.setVisibility(View.VISIBLE);
			tvName.setText("点击SJF设置主账号");
			ivHead.setImageResource(R.drawable.ic_launcher);
		} else {
			getInfo(MainActivity.instance.getAccount(mainUID));
        }
		ivHead.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1) {
					String items[] = new String[MainActivity.instance.loginAccounts.size()];
					final int[] wi=new int[1];
					for (int i=0,j=MainActivity.instance.loginAccounts.size();i < j;++i) {
						items[i] = MainActivity.instance.loginAccounts.get(i).name;
					}
					new AlertDialog.Builder(MainActivity.instance).setIcon(R.drawable.ic_launcher).setTitle("选择账号").setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								wi[0] = which;
							}
						}).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								AccountInfo ai=MainActivity.instance.loginAccounts.get(wi[0]);
								if (ai == null) {
									return;
								}
								getInfo(ai);
								MainActivity.instance.sjfSettings.setMainAccount(ai.uid);
							}
						}).show();
				}
			});
    }

	private void getInfo(final AccountInfo ai) {
		File imf = new File(MainActivity.instance.mainDic + "bilibili/" + ai.uid + ".jpg");
		if (imf.exists()) {
			Bitmap b = BitmapFactory.decodeFile(imf.getAbsolutePath());
			ivHead.setImageBitmap(b);
		} else {
			MainActivity.instance.threadPool.execute(new DownloadImageRunnable(ivHead, ai.uid, DownloadImageRunnable.BilibiliUser));
		}
		MainActivity.instance.threadPool.execute(new Runnable() {
				@Override
				public void run() {
					final UserInfo info = Tools.BilibiliTool.getUserInfo(ai.uid);
					if (info.code != 0) {
						MainActivity.instance.showToast("cookie过期");
						return;
					}
					final UidToRoom sjb = Tools.BilibiliTool.getRoomByUid(info.data.mid);
					MainActivity.instance.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								tvName.setText(info.data.name);
								tvBMain.setText("主站 Lv." + info.data.level);
							}
						});

					final RoomToUid rtu=Tools.BilibiliTool.getUidByRoom(sjb.data.roomid);
					MainActivity.instance.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (rtu != null) {
									tvBLive.setText("主播 Lv." + rtu.data.level.master_level.level + "(" + rtu.data.level.master_level.anchor_score + "/" + rtu.data.level.master_level.next.get(1) + ")");
								} else {
									tvBLive.setVisibility(View.VISIBLE);
									tvBLive.setText("未开通直播间");
								}
							}
						});
				}
			});
	}
}
