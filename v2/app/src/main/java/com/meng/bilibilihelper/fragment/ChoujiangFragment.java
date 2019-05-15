package com.meng.bilibilihelper.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;
import com.meng.bilibilihelper.javaBean.Choujiang;
import com.meng.bilibilihelper.javaBean.HourRank;
import com.meng.bilibilihelper.javaBean.UserSpaceToLive;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class ChoujiangFragment extends Fragment {

    public ListView listview;
    public Button btn;
    public EditText et;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_danmaku, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listview = (ListView) view.findViewById(R.id.send_danmaku_listView);
        btn = (Button) view.findViewById(R.id.send_danmaku_button);
        et = (EditText) view.findViewById(R.id.send_danmaku_editText);
        listview.setAdapter(MainActivity.instence.loginInfoPeopleAdapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //       HourRank hourRank = readRank();
                            //       for (HourRank.HourRankDataList c : hourRank.data.list) {
                            //           UserSpaceToLive sjb = new Gson().fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + c.uid), UserSpaceToLive.class);
                            //   Choujiang choujiang = readInfo(((LoginInfoPeople) parent.getItemAtPosition(position)).cookie, sjb.data.roomid);
                            Choujiang choujiang = readInfo(((LoginInfoPeople) parent.getItemAtPosition(position)).cookie, Integer.parseInt(et.getText().toString()));
                            Thread.sleep(500);
                            //        if (choujiang.data.list == null) continue;
                            if (choujiang.data.list == null) return;
                            for (Choujiang.ChouJiangDataList chouJiangDataList : choujiang.data.list) {
                                join(((LoginInfoPeople) parent.getItemAtPosition(position)).cookie, et.getText().toString(), chouJiangDataList.raffleId);
                                Thread.sleep(500);
                            }
                            //       }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.instence.showToast(e.toString());
                        }
                    }
                }).start();
            }
        });
    }

    public HourRank readRank() {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("Accept", "application/json, text/javascript, */*; q=0.01");
            map.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            map.put("Connection", "keep-alive");
            map.put("Origin", "https://live.bilibili.com");
            Connection connection = Jsoup.connect("https://api.live.bilibili.com/rankdb/v1/Rank2018/getTop?type=master_last_hour&type_id=areaid_hour&area_id=0");
            connection.userAgent(MainActivity.instence.userAgent)
                    .headers(map)
                    .ignoreContentType(true)
                    .method(Connection.Method.GET);
            Connection.Response response = connection.execute();
            if (response.statusCode() != 200) {
                MainActivity.instence.showToast(String.valueOf(response.statusCode()));
                return new HourRank();
            }
            MainActivity.instence.showToast(response.body());
            return new Gson().fromJson(response.body(), HourRank.class);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.instence.showToast(e.toString());
        }
        return new HourRank();
    }

    public Choujiang readInfo(String cookie, int roomId) {
        try {
            Connection connection = Jsoup.connect("https://api.live.bilibili.com/xlive/lottery-interface/v3/smalltv/Check?roomid=" + roomId);
            Map<String, String> map = new HashMap<>();
            map.put("Host", "api.live.bilibili.com");
            map.put("Accept", "application/json, text/javascript, */*; q=0.01");
            map.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            map.put("Connection", "keep-alive");
            map.put("Origin", "https://live.bilibili.com");
            connection.userAgent(MainActivity.instence.userAgent)
                    .headers(map)
                    .ignoreContentType(true)
                    .referrer("https://live.bilibili.com/" + roomId)
                    .cookies(MainActivity.instence.cookieToMap(cookie))
                    .method(Connection.Method.GET);
            Connection.Response response = connection.execute();
            if (response.statusCode() != 200) {
                MainActivity.instence.showToast(String.valueOf(response.statusCode()));
                return new Choujiang();
            }
            MainActivity.instence.showToast(response.body());
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(response.body()).getAsJsonObject();
            MainActivity.instence.showToast(obj.get("msg").getAsString());
            return new Gson().fromJson(response.body(), Choujiang.class);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.instence.showToast(e.toString());
        }
        return new Choujiang();
    }
/*
    public void join1(String cookie, String roomId, int raffleId) throws IOException {
        URL postUrl = new URL("https://api.live.bilibili.com/xlive/lottery-interface/v3/smalltv/Join");
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
        connection.setRequestProperty("Connection", "keep-alive");*/
     //   connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
   /*     connection.setRequestProperty("Origin", "https://live.bilibili.com");
        connection.setRequestProperty("User-Agent", MainActivity.instence.userAgent);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer", "https://live.bilibili.com/" + roomId + "?live_lottery_type=1&broadcast_type=0&from=28003&extra_jump_from=28003&visit_id=");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("cookie", cookie);
        content = "roomid=" + roomId +
                "&raffleId=" + raffleId +
                "&type=Gift" +
                "&csrf_token=" + MainActivity.instence.cookieToMap(cookie).get("bili_jct") +
                "&csrf=" + MainActivity.instence.cookieToMap(cookie).get("bili_jct");
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
        reader.close();
        connection.disconnect();
        MainActivity.instence.showToast(ss);
    }

*/


    public void join(String cookie, String roomId, int raffleId) {
        try {
            Connection connection = Jsoup.connect("https://api.live.bilibili.com/xlive/lottery-interface/v3/smalltv/Join");
            String csrf = MainActivity.instence.cookieToMap(cookie).get("bili_jct");
            Map<String, String> map = new HashMap<>();
            map.put("Host", "api.live.bilibili.com");
            map.put("Accept", "application/json, text/javascript, */*; q=0.01");
            map.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            map.put("Connection", "keep-alive");
            map.put("Origin", "https://live.bilibili.com");
            connection.userAgent(MainActivity.instence.userAgent)
                    .headers(map)
                    .ignoreContentType(true)
                    .referrer("https://live.bilibili.com/" + roomId)
                    .cookies(MainActivity.instence.cookieToMap(cookie))
                    .data("roomid", roomId)
                    .data("raffleId", String.valueOf(raffleId))
                    .data("type", "Gift")
                    .data("csrf_token", csrf)
                    .data("csrf", csrf)
                    .method(Connection.Method.POST);
            Connection.Response response = connection.execute();
            if (response.statusCode() != 200) {
                MainActivity.instence.showToast(String.valueOf(response.statusCode()));
            }
   //         JsonParser parser = new JsonParser();
   //         JsonObject obj = parser.parse(response.body()).getAsJsonObject();
    //        MainActivity.instence.showToast(obj.get("message").getAsString());
            MainActivity.instence.showToast(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.instence.showToast(e.toString());
        }
    }
}
