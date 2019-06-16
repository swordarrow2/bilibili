package com.meng.bilibilihelper.libAndHelper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.adapters.PartListAdapter;
import com.meng.bilibilihelper.javaBean.LivePartList;
import com.meng.bilibilihelper.javaBean.UserSpaceToLive;
import com.meng.bilibilihelper.javaBean.personInfo.ConfigJavaBean;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MengLiveControl extends LinearLayout {

    private Button btnStart;
    private EditText newName;
    private LinearLayout ll;
    private MengTextview m1;
    private MengTextview m2;

    public MengLiveControl(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.meng_live_control, this);
        btnStart = (Button) findViewById(R.id.btn_start);
        Button btnRename = (Button) findViewById(R.id.btn_rename);
        newName = (EditText) findViewById(R.id.et_new_name);
        ll = (LinearLayout) findViewById(R.id.linearlayout);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.part);
        autoCompleteTextView.setAdapter(new PartListAdapter((Activity) context, new Gson().fromJson(MainActivity.instence.mainFrgment.getFromAssets("partlist.json"), LivePartList.class).getPartInfo()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String mainUID = SharedPreferenceHelper.getValue("mainAccount", "");
                if (!mainUID.equals("")) {
                    final UserSpaceToLive sjb = new Gson().fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UserSpaceToLive.class);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnStart.setText(sjb.data.liveStatus == 0 ? "开始直播" : "关闭直播");
                        }
                    });
                }
            }
        }).start();
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnStart.getText().toString().equals("开始直播")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String mainUID = SharedPreferenceHelper.getValue("mainAccount", "");
                            if (!mainUID.equals("")) {
                                String cookie = MainActivity.instence.getCookie(Long.parseLong(mainUID));
                                UserSpaceToLive sjb = new Gson().fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UserSpaceToLive.class);
                                String streamJson = MainActivity.instence.getSourceCode("https://api.live.bilibili.com/live_stream/v1/StreamList/get_stream_by_roomId?room_id=" + sjb.data.roomid, cookie, "https://link.bilibili.com/p/center/index");
                                JsonParser parser = new JsonParser();
                                JsonObject rtmp = parser.parse(streamJson).getAsJsonObject().get("data").getAsJsonObject().get("rtmp").getAsJsonObject();
                                try {
                                    start(sjb.data.roomid, cookie);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                m1 = new MengTextview(context, "", rtmp.get("addr").getAsString());
                                m2 = new MengTextview(context, "", rtmp.get("code").getAsString());
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ll.addView(m1);
                                        ll.addView(m2);
                                        btnStart.setText("关闭直播");
                                    }
                                });
                            }
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String mainUID = SharedPreferenceHelper.getValue("mainAccount", "");
                            UserSpaceToLive sjb = new Gson().fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UserSpaceToLive.class);
                            try {
                                stop(sjb.data.roomid, MainActivity.instence.getCookie(Long.parseLong(mainUID)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll.removeView(m1);
                                    ll.removeView(m2);
                                    btnStart.setText("开始直播");
                                }
                            });
                        }
                    }).start();
                }
            }
        });
        btnRename.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = newName.getText().toString();
                if (name.equals("")) {
                    MainActivity.instence.showToast("房间名不能为空");
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String mainUID = SharedPreferenceHelper.getValue("mainAccount", "");
                        if (!mainUID.equals("")) {
                            String cookie = MainActivity.instence.getCookie(Long.parseLong(mainUID));
                            UserSpaceToLive sjb = new Gson().fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UserSpaceToLive.class);
                            try {
                                rename(sjb.data.roomid, cookie, name);
                            } catch (IOException e) {
                                MainActivity.instence.showToast(e.toString());
                            }
                        }
                    }
                }).start();
            }
        });
    }

    public void start(int roomID, String cookie) throws IOException {
        Connection connection = Jsoup.connect("https://api.live.bilibili.com/room/v1/Room/startLive");
        String csrf = MainActivity.instence.cookieToMap(cookie).get("bili_jct");
        Map<String, String> liveHead = new HashMap<>();
        liveHead.put("Host", "api.live.bilibili.com");
        liveHead.put("Accept", "application/json, text/javascript, */*; q=0.01");
        liveHead.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        liveHead.put("Connection", "keep-alive");
        liveHead.put("Origin", "https://link.bilibili.com");
        connection.userAgent(MainActivity.instence.userAgent)
                .headers(liveHead)
                .ignoreContentType(true)
                .referrer("https://link.bilibili.com/p/center/index")
                .cookies(MainActivity.instence.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("room_id", String.valueOf(roomID))
                .data("platform", "pc")
                .data("area_v2", "235")
                .data("csrf_token", csrf)
                .data("csrf", csrf);
        Connection.Response response = connection.execute();
        if (response.statusCode() != 200) {
            MainActivity.instence.showToast(String.valueOf(response.statusCode()));
        }
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(response.body()).getAsJsonObject();
        MainActivity.instence.showToast(obj.get("message").getAsString());
    }


    public void stop(int roomID, String cookie) throws IOException {
        Connection connection = Jsoup.connect("https://api.live.bilibili.com/room/v1/Room/stopLive");
        String csrf = MainActivity.instence.cookieToMap(cookie).get("bili_jct");
        Map<String, String> liveHead = new HashMap<>();
        liveHead.put("Host", "api.live.bilibili.com");
        liveHead.put("Accept", "application/json, text/javascript, */*; q=0.01");
        liveHead.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        liveHead.put("Connection", "keep-alive");
        liveHead.put("Origin", "https://link.bilibili.com");
        connection.userAgent(MainActivity.instence.userAgent)
                .headers(liveHead)
                .ignoreContentType(true)
                .referrer("https://link.bilibili.com/p/center/index")
                .cookies(MainActivity.instence.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("room_id", String.valueOf(roomID))
                .data("csrf_token", csrf)
                .data("csrf", csrf);
        Connection.Response response = connection.execute();
        if (response.statusCode() != 200) {
            MainActivity.instence.showToast(String.valueOf(response.statusCode()));
        }
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(response.body()).getAsJsonObject();
        MainActivity.instence.showToast(obj.get("message").getAsString());
    }

    public void rename(int roomID, String cookie, String newName) throws IOException {
        Connection connection = Jsoup.connect("https://api.live.bilibili.com/room/v1/Room/update");
        String csrf = MainActivity.instence.cookieToMap(cookie).get("bili_jct");
        Map<String, String> liveHead = new HashMap<>();
        liveHead.put("Host", "api.live.bilibili.com");
        liveHead.put("Accept", "application/json, text/javascript, */*; q=0.01");
        liveHead.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        liveHead.put("Connection", "keep-alive");
        liveHead.put("Origin", "https://link.bilibili.com");
        connection.userAgent(MainActivity.instence.userAgent)
                .headers(liveHead)
                .ignoreContentType(true)
                .referrer("https://link.bilibili.com/p/center/index")
                .cookies(MainActivity.instence.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("room_id", String.valueOf(roomID))
                .data("title", newName)
                .data("csrf_token", csrf)
                .data("csrf", csrf);
        Connection.Response response = connection.execute();
        if (response.statusCode() != 200) {
            MainActivity.instence.showToast(String.valueOf(response.statusCode()));
        }
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(response.body()).getAsJsonObject();
        MainActivity.instence.showToast(obj.get("message").getAsString());
    }
}
