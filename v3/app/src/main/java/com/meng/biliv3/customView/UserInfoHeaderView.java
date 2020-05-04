package com.meng.biliv3.customView;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.customView.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libs.*;
import java.io.*;

public class UserInfoHeaderView extends LinearLayout {

    private ImageView ivHead;
    private TextView tvName;
    private TextView tvBMain;
	private TextView tvBLive;

    public UserInfoHeaderView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.main_account_list_header, this);
        ivHead = (ImageView) findViewById(R.id.imageView);
        tvName = (TextView) findViewById(R.id.textView1);
        tvBMain = (TextView) findViewById(R.id.textView2);
		tvBLive = (TextView) findViewById(R.id.textView3);
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
		tvName.setVisibility(View.GONE);
		tvBMain.setVisibility(View.GONE);
		tvBLive.setVisibility(View.GONE);
		View v=MainActivity.instance.mDrawerList.getChildAt(1);
		if (v instanceof MengLiveControl) {
			MainActivity.instance.mDrawerList.removeHeaderView(v);
		}
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
					final BilibiliUserInfo info = MainActivity.instance.gson.fromJson(Tools.Network.httpGet("https://api.bilibili.com/x/space/acc/info?mid=" + ai.uid + "&jsonp=jsonp"), BilibiliUserInfo.class);
					if (info.code != 0) {
						MainActivity.instance.showToast("cookie过期");
						return;
					}
					UidToLiveRoom sjb = MainActivity.instance.gson.fromJson(Tools.Network.httpGet("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + info.data.mid), UidToLiveRoom.class);
					MainActivity.instance.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								tvName.setVisibility(View.VISIBLE);
								tvBMain.setVisibility(View.VISIBLE);
								tvName.setText(info.data.name);
								tvBMain.setText("主站 Lv." + info.data.level);
							}
						});
					try {
						String json = Tools.Network.httpGet("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=" + sjb.data.roomid);
						JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
						final JsonObject obj2=obj.get("data").getAsJsonObject().get("level").getAsJsonObject().get("master_level").getAsJsonObject();
						MainActivity.instance.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									JsonArray ja = obj2.get("next").getAsJsonArray();
									tvBLive.setVisibility(View.VISIBLE);
									tvBLive.setText("主播 Lv." + obj2.get("level").getAsInt() + "(" + obj2.get("anchor_score").getAsInt() + "/" + ja.get(1).getAsInt() + ")");
									if (MainActivity.instance.mDrawerList.getHeaderViewsCount() == 1) {
										MainActivity.instance.mDrawerList.addHeaderView(new MengLiveControl(MainActivity.instance));
									}
								}
							});
					} catch (Exception e) {
						MainActivity.instance.runOnUiThread(new Runnable(){

								@Override
								public void run() {
									tvBLive.setVisibility(View.VISIBLE);
									tvBLive.setText("未开通直播间");
								}
							});
					}

				}
			});
	}

}
