package com.meng.bilibilihelper.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.adapters.GiftAdapter;
import com.meng.bilibilihelper.javaBean.BilibiliMyInfo;
import com.meng.bilibilihelper.javaBean.UserSpaceToLive;
import com.meng.bilibilihelper.javaBean.liveBag.LiveBag;
import com.meng.bilibilihelper.javaBean.liveBag.LiveBagDataList;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GiftActivity extends Activity {
    private BilibiliMyInfo info;
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
        uid = MainActivity.instence.naiFragment.getUId();
        if (uid.equals("")) {
            Toast.makeText(getApplicationContext(), "请在主页中输入用户ID而不是直播间ID", Toast.LENGTH_SHORT).show();
            finish();
        }
        setContentView(R.layout.list_fragment);
        final ListView listView = (ListView) findViewById(R.id.normal_listview);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                String cookie = MainActivity.instence.loginInfo.loginInfoPeople.get(position).cookie;
                info = gson.fromJson(MainActivity.instence.getSourceCode("http://api.bilibili.com/x/space/myinfo?jsonp=jsonp", cookie), BilibiliMyInfo.class);
                userSpaceToLive = gson.fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + uid), UserSpaceToLive.class);
                liveBag = new Gson().fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/xlive/web-room/v1/gift/bag_list?t=" + System.currentTimeMillis(), MainActivity.instence.loginInfo.loginInfoPeople.get(position).cookie), LiveBag.class);
                giftAdapter = new GiftAdapter(GiftActivity.this, liveBag.data.list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (liveBag.data.list.size() == 0) {
                            Toast.makeText(getApplicationContext(), "包裹中什么也没有", Toast.LENGTH_SHORT).show();
                            GiftActivity.this.finish();
                        }
                        listView.setAdapter(giftAdapter);
                    }
                });
            }
        }).start();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int p, long id) {

                final EditText editText = new EditText(getApplicationContext());
                editText.setHint("要赠送的数量");
                new AlertDialog.Builder(getApplicationContext())
                        .setView(editText)
                        .setTitle("编辑")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface p11, int p2) {
                                if (liveBag.data.list.get(p).gift_num > 0) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String cookie = MainActivity.instence.loginInfo.loginInfoPeople.get(position).cookie;
                                                sendHotStrip(info.data.mid, uid, userSpaceToLive.data.roomid, editText.getText().toString(), cookie, (LiveBagDataList) parent.getItemAtPosition(p));
                                                liveBag.data.list.get(p).gift_num -= Integer.parseInt(editText.getText().toString());
                                                giftAdapter.notifyDataSetChanged();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                } else {
                                    Toast.makeText(getApplicationContext(), "物品数量为0", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
    }

    public void sendHotStrip(long uid, String ruid, int roomID, String cookie, String num, LiveBagDataList liveBagDataList) throws IOException {
        URL postUrl = new URL("http://api.live.bilibili.com/gift/v2/gift/send");
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
        connection.setRequestProperty("User-Agent", MainActivity.instence.userAgent);
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
                "&csrf_token=" + MainActivity.instence.cookieToMap(cookie).get("bili_jct") +
                "&csrf=" + MainActivity.instence.cookieToMap(cookie).get("bili_jct") +
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
        final String ss = s.toString();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GiftActivity.this, ss, Toast.LENGTH_SHORT).show();
            }
        });
        reader.close();
        connection.disconnect();
    }
}
