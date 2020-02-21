package com.meng.bilibilihelper.fragment.main;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.adapters.ListWithImageSwitchAdapter;
import com.meng.bilibilihelper.fragment.BaseFrgment;
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

public class ZanFragment extends BaseFrgment {
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
        et.setHint("AID");
		et.setText(String.valueOf(new Random().nextInt()));
        listview.setAdapter(new ListWithImageSwitchAdapter(MainActivity.instence, MainActivity.instence.loginInfo.loginInfoPeople));
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        ListWithImageSwitchAdapter cda = (ListWithImageSwitchAdapter) listview.getAdapter();
                        for (int i = 0; i < cda.getCount(); ++i) {
                            if (cda.getChecked(i)) {
                                try {
                                    sendLike(((LoginInfoPeople) cda.getItem(i)).cookie, String.valueOf(Integer.parseInt(et.getText().toString())));
                                    Thread.sleep(1000);
                                } catch (Exception e) {
                                    MainActivity.instence.showToast(e.toString());
                                }
                            }
                        }
                    }
                }).start();
            }
        });
    }

    public void sendLike(String cookie, String AID) throws IOException {
        Connection connection = Jsoup.connect("https://api.bilibili.com/x/web-interface/archive/like");
        connection.userAgent(MainActivity.instence.userAgent)
                .headers(mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/video/av" + AID)
                .cookies(MainActivity.instence.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("aid", AID)
                .data("like", "1")
                .data("csrf", MainActivity.instence.cookieToMap(cookie).get("bili_jct"));
        Connection.Response response = connection.execute();
        if (response.statusCode() != 200) {
            MainActivity.instence.showToast(String.valueOf(response.statusCode()));
        }
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(response.body()).getAsJsonObject();
        MainActivity.instence.showToast(obj.get("message").getAsString());
    }
}
