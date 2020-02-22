package com.meng.bilibilihelper.activity.live;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.adapters.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.libAndHelper.*;
import java.io.*;
import java.net.*;

public class GiftActivity extends Activity {
    private BilibiliMyInfo myInfo;
    private UserSpaceToLive userSpaceToLive;
    private String uid;
    private LiveBag liveBag;
    private GiftAdapter giftAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int position = getIntent().getIntExtra("position", -1);
        if (position == -1) {
            finish();
		}
        uid = MainActivity.instance.getFragment("Main", MainFragment.class).mengEditText.getUId();
        if (uid.equals("")) {
            Toast.makeText(getApplicationContext(), "请在主页中输入用户ID而不是直播间ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
		}
        setContentView(R.layout.list_fragment);
        final ListView listView = (ListView) findViewById(R.id.normal_listview);
        MainActivity.instance.threadPool.execute(new Runnable() {
				@Override
				public void run() {
					Gson gson = new Gson();
					String cookie = MainActivity.instance.loginAccounts.get(position).cookie;
					myInfo = gson.fromJson(Tools.Network.getSourceCode("http://api.bilibili.com/x/space/myinfo?jsonp=jsonp", cookie), BilibiliMyInfo.class);
					userSpaceToLive = gson.fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + uid), UserSpaceToLive.class);
					liveBag = new Gson().fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/xlive/web-room/v1/gift/bag_list?t=" + System.currentTimeMillis(), MainActivity.instance.loginAccounts.get(position).cookie), LiveBag.class);
					giftAdapter = new GiftAdapter(GiftActivity.this, liveBag.data.list);
					runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (liveBag.data.list.size() == 0) {
									MainActivity.instance.showToast("包裹中什么也没有");
									GiftActivity.this.finish();
								}
								listView.setAdapter(giftAdapter);
								int ii=0;
								for (LiveBag.LiveBagDataList i:liveBag.data.list) {
									if (i.gift_name.equals("辣条")) {
										ii += i.gift_num;
									}
								}
								MainActivity.instance.showToast("共有" + ii + "辣条");
							}
						});
				}
			});
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(final AdapterView<?> parent, View view, final int p, long id) {

					final EditText editText = new EditText(GiftActivity.this);
					editText.setHint("要赠送的数量");
					new AlertDialog.Builder(GiftActivity.this)
						.setView(editText)
						.setTitle("编辑")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface p11, int p2) {
								MainActivity.instance.threadPool.execute(new Runnable() {
										@Override
										public void run() {
											try {
												String cookie = MainActivity.instance.loginAccounts.get(position).cookie;
												int num=Integer.parseInt(editText.getText().toString());
												if (num > getStripCount(liveBag.data)) {
													MainActivity.instance.showToast("辣条不足");	
													return;
												}
												for (LiveBag.LiveBagDataList i:liveBag.data.list) {
													if (i.gift_name.equals("辣条")) {
														if (num > i.gift_num) {
															sendHotStrip(myInfo.data.mid, uid, userSpaceToLive.data.roomid, cookie, i.gift_num, i);
															num -= i.gift_num;
															i.gift_num = 0;
														} else {
															sendHotStrip(myInfo.data.mid, uid, userSpaceToLive.data.roomid, cookie, num, i);											
															i.gift_num -= num;
															break;	
														}
													}
												}
												if (getStripCount(liveBag.data) == 0) {
													MainActivity.instance.showToast("已送出全部礼物🎁");
													finish();
												}
												for (int i=0;i < liveBag.data.list.size();++i) {
													if (liveBag.data.list.get(i).gift_name.equals("辣条") && liveBag.data.list.get(i) .gift_num == 0) {
														liveBag.data.list.remove(i);
													}
												}										

												runOnUiThread(new Runnable() {
														@Override
														public void run() {
															giftAdapter.notifyDataSetChanged();
														}
													});
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									});
							}
						}).setNegativeButton("取消", null).show();
				}
			});

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(final AdapterView<?> p1, View p2, final int p3, long p4) {
					MainActivity.instance.threadPool.execute(new Runnable() {
							@Override
							public void run() {
								try {
									String cookie = MainActivity.instance.loginAccounts.get(position).cookie;
									sendHotStrip(myInfo.data.mid, uid, userSpaceToLive.data.roomid, cookie, liveBag.data.list.get(p3).gift_num, (LiveBag.LiveBagDataList) p1.getItemAtPosition(p3));
									liveBag.data.list.remove(p3);
									if (liveBag.data.list.size() == 0) {
										MainActivity.instance.showToast("已送出全部礼物🎁");
										finish();
									}
									runOnUiThread(new Runnable() {
											@Override
											public void run() {
												giftAdapter.notifyDataSetChanged();
											}
										});
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					return true;
				}
			});
	}
	private int getStripCount(LiveBag.LiveBagData data) {
		int ii=0;
		for (LiveBag.LiveBagDataList i:liveBag.data.list) {
			if (i.gift_name.equals("辣条")) {
				ii += i.gift_num;
			}
		}
		return ii;
	}
    public void sendHotStrip(long uid, String ruid, int roomID, String cookie, int num, LiveBag.LiveBagDataList liveBagDataList) throws IOException {
        URL postUrl = new URL("https://api.live.bilibili.com/gift/v2/live/bag_send");
        String content = "";//要发出的数据
        // 打开连接
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        // 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        //	 Post请求不能使用缓存
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Host", "api.live.bilibili.com");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Origin", "https://live.bilibili.com");
        connection.setRequestProperty("User-Agent", MainActivity.instance.userAgent);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer", "https://live.bilibili.com/" + roomID);
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("cookie", cookie);
        content = "uid=" + uid +
			"&gift_id=" + liveBagDataList.gift_id +
			"&ruid=" + ruid +
			"&gift_num=" + num +
			"&bag_id=" + liveBagDataList.bag_id +
			"&platform=pc" +
			"&biz_code=live" +
			"&biz_id=" + roomID +
			"&rnd=" + (System.currentTimeMillis() / 1000) +
			"&storm_beat_id=0" +
			"&metadata=" +
			"&price=0" +
			"&csrf_token=" + Tools.Network.cookieToMap(cookie).get("bili_jct") +
			"&csrf=" + Tools.Network.cookieToMap(cookie).get("bili_jct") +
			"&visit_id=";
        connection.setRequestProperty("Content-Length", String.valueOf(content.length()));
        // 连接,从postUrl.openConnection()至此的配置必须要在 connect之前完成
        // 要注意的是connection.getOutputStream会隐含的进行 connect
        connection.connect();
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(content);
        out.flush();
        out.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder s = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            s.append(line);
		}
        String ss = s.toString();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(ss).getAsJsonObject();
        MainActivity.instance.showToast(obj.get("msg").getAsString());
        reader.close();
        connection.disconnect();
	}
}
