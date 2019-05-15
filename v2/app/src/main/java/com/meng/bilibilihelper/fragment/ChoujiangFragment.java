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
import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;
import com.meng.bilibilihelper.javaBean.Choujiang;
import com.meng.bilibilihelper.javaBean.HourRank;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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
                            HourRank chouJiangInfo = readRank();
                            for (HourRank.HourRankDataList c : chouJiangInfo.data.list) {
                                Choujiang choujiang = readInfo(((LoginInfoPeople) parent.getItemAtPosition(position)).cookie, c.id);
                                Thread.sleep(500);
                                for (Choujiang.ChouJiangDataList chouJiangDataList : choujiang.data.list) {
                                    join(((LoginInfoPeople) parent.getItemAtPosition(position)).cookie, et.getText().toString(), chouJiangDataList.raffleId);
                                    Thread.sleep(500);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    public HourRank readRank() {
        try {
            Connection connection = Jsoup.connect("https://api.live.bilibili.com/rankdb/v1/Rank2018/getTop?type=master_last_hour&type_id=areaid_hour&area_id=0");
            connection.userAgent(MainActivity.instence.userAgent)
                    .ignoreContentType(false)
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
            Connection connection = Jsoup.connect("https://api.live.bilibili.com/rankdb/v1/Rank2018/getTop?type=master_last_hour&type_id=areaid_hour&area_id=0");
            Map<String, String> map = new HashMap<>();
            map.put("Host", "api.live.bilibili.com");
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
            return new Gson().fromJson(response.body(), Choujiang.class);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.instence.showToast(e.toString());
        }
        return new Choujiang();
    }

    public void join(String cookie, String roomId, int raffleId) {
        try {
            Connection connection = Jsoup.connect("https://api.live.bilibili.com/xlive/lottery-interface/v3/smalltv/Join");
            String csrf = MainActivity.instence.cookieToMap(cookie).get("bili_jct");
            Map<String, String> map = new HashMap<>();
            map.put("Host", "api.live.bilibili.com");
            map.put("Connection", "keep-alive");
            map.put("Origin", "https://live.bilibili.com");
            map.put("Referer", "https://live.bilibili.com/" + roomId + "?live_lottery_type=1&broadcast_type=0&from=28003&extra_jump_from=28003&visit_id=459wg8aatmu0");
            connection.userAgent(MainActivity.instence.userAgent)
                    .headers(map)
                    .ignoreContentType(true)
                    .referrer("https://live.bilibili.com/" + roomId)
                    .cookies(MainActivity.instence.cookieToMap(cookie))
                    .method(Connection.Method.POST)
                    .data("roomid=" + roomId +
                            "&raffleId=" + raffleId +
                            "&type=Gift" +
                            "&csrf_token=" + csrf +
                            "&csrf=" + csrf);
            Connection.Response response = connection.execute();
            if (response.statusCode() != 200) {
                MainActivity.instence.showToast(String.valueOf(response.statusCode()));
            }
            MainActivity.instence.showToast(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.instence.showToast(e.toString());
        }
    }
}
