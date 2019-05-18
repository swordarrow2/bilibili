package com.meng.bilibilihelper.libAndHelper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.javaBean.UserSpaceToLive;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MengLiveControl extends LinearLayout {

    private Button btn;
    private LinearLayout ll;

    public MengLiveControl(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.meng_live_control, this);
        btn = (Button) findViewById(R.id.btn);
        ll = (LinearLayout) findViewById(R.id.linearlayout);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String mainUID = SharedPreferenceHelper.getValue("mainAccount", "");
                if (!mainUID.equals("")) {
                    final UserSpaceToLive sjb = new Gson().fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UserSpaceToLive.class);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn.setText(sjb.data.liveStatus == 0 ? "开始直播" : "关闭直播");
                        }
                    });
                }
            }
        }).start();
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn.getText().toString().equals("开始直播")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String mainUID = SharedPreferenceHelper.getValue("mainAccount", "");
                            if (!mainUID.equals("")) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String cookie = MainActivity.instence.getCookie(Long.parseLong(mainUID));
                                        UserSpaceToLive sjb = new Gson().fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UserSpaceToLive.class);
                                        String streamJson = MainActivity.instence.getSourceCode("https://api.live.bilibili.com/live_stream/v1/StreamList/get_stream_by_roomId?room_id=" + sjb.data.roomid, cookie, "https://link.bilibili.com/p/center/index");
                                        JsonParser parser = new JsonParser();
                                        JsonObject rtmp = parser.parse(streamJson).getAsJsonObject().get("data").getAsJsonObject().get("rtmp").getAsJsonObject();
                                        MainActivity.instence.showToast(rtmp.get("message").getAsString());
                                        try {
                                            start(sjb.data.roomid, cookie);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        final MengTextview m1 = new MengTextview(context, "rtmp地址", rtmp.get("addr").getAsString());
                                        final MengTextview m2 = new MengTextview(context, "直播码", rtmp.get("code").getAsString());
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ll.addView(m1);
                                                ll.addView(m2);
                                                btn.setText("关闭直播");
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }

                    });
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
                                    ll.removeViewAt(1);
                                    ll.removeViewAt(2);
                                    btn.setText("开始直播");
                                }
                            });
                        }
                    }).start();
                }
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
}
