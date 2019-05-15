package com.meng.bilibilihelper.fragment;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView l = (ListView) view.findViewById(R.id.normal_listview);
        l.setAdapter(MainActivity.instence.loginInfoPeopleAdapter);
        l.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> p1, View p2, final int p3, long p4) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            sendSignData(((LoginInfoPeople) (p1.getItemAtPosition(p3))).cookie);
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    public void sendSignData(String cookie) throws IOException {
        Connection connection = Jsoup.connect("https://api.live.bilibili.com/sign/doSign");
        Map<String, String> map = new HashMap<>();
        map.put("Host", "api.live.bilibili.com");
        map.put("Accept", "application/json, text/javascript, */*; q=0.01");
        map.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        map.put("Connection", "keep-alive");
        map.put("Origin", "https://live.bilibili.com");
        connection.userAgent(MainActivity.instence.userAgent)
                .headers(map)
                .ignoreContentType(true)
                .referrer("https://live.bilibili.com/" + new Random().nextInt() % 9721949)
                .cookies(MainActivity.instence.cookieToMap(cookie))
                .method(Connection.Method.GET);
        Connection.Response response = connection.execute();
        if (response.statusCode() != 200) {
            MainActivity.instence.showToast(String.valueOf(response.statusCode()));
        }
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(response.body()).getAsJsonObject();
        MainActivity.instence.showToast(obj.get("message").getAsString());
    }
}
