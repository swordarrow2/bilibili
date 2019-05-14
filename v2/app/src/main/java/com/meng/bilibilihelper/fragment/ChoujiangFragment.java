package com.meng.bilibilihelper.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.meng.bilibilihelper.activity.LiveWebActivity;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;
import com.meng.bilibilihelper.javaBean.SendDanmakuReturnedData;
import com.meng.bilibilihelper.javaBean.chouJiang.ChouJiangDataList;
import com.meng.bilibilihelper.javaBean.chouJiang.Choujiang;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
                            Choujiang choujiang = readInfo(((LoginInfoPeople) parent.getItemAtPosition(position)).cookie, et.getText().toString());
                            for (ChouJiangDataList chouJiangDataList : choujiang.data.list) {
                                join(((LoginInfoPeople) parent.getItemAtPosition(position)).cookie, et.getText().toString(), chouJiangDataList.raffleId);
                                Thread.sleep(500);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }

    public Choujiang readInfo(String cookie, String roomId) throws IOException {
        URL postUrl = new URL("https://api.live.bilibili.com/xlive/lottery-interface/v3/smalltv/Check?roomid=" + roomId);
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        connection.setDoOutput(false);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Host", "api.live.bilibili.com");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Origin", "https://live.bilibili.com");
        connection.setRequestProperty("User-Agent", MainActivity.instence.userAgent);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer", "https://live.bilibili.com/" + roomId);
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("cookie", cookie);
        connection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder s = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            s.append(line);
        }
        String ss = s.toString();
        Choujiang choujiang = new Gson().fromJson(ss, Choujiang.class);
        reader.close();
        connection.disconnect();
        MainActivity.instence.showToast(ss);
        return choujiang;
    }

    public void join(String cookie, String roomId, int raffleId) throws IOException {
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
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Origin", "https://live.bilibili.com");
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
        try {
            final SendDanmakuReturnedData returnData = new Gson().fromJson(ss, SendDanmakuReturnedData.class);
            switch (returnData.code) {
                case 0:
                    if (!returnData.message.equals("")) {
                        MainActivity.instence.showToast(returnData.message);
                    } else {
                        MainActivity.instence.showToast(roomId + "已奶");
                    }
                    break;
                case 1990000:
                    if (returnData.message.equals("risk")) {
                        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        if (wifiNetworkInfo.isConnected()) {
                            Intent intent = new Intent(getActivity(), LiveWebActivity.class);
                            intent.putExtra("cookie", cookie);
                            intent.putExtra("url", "https://live.bilibili.com/" + roomId);
                            getActivity().startActivity(intent);
                        } else {
                            MainActivity.instence.showToast("需要在官方客户端进行账号风险验证");
                        }
                    }
                    break;
                default:
                    MainActivity.instence.showToast(ss);
                    break;
            }
        } catch (Exception e) {
            MainActivity.instence.showToast(ss);
        }
    }

    public String encode(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "Issue while encoding" + e.getMessage();
        }
    }

}
