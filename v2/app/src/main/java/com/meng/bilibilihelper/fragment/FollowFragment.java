package com.meng.bilibilihelper.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.adapters.ListWithImageSwitchAdapter;
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class FollowFragment extends Fragment {
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
                                    sendFollowDataStep1(((LoginInfoPeople) cda.getItem(i)).cookie, String.valueOf(Integer.parseInt(et.getText().toString())));
                                    sendFollowDataStep2(((LoginInfoPeople) cda.getItem(i)).cookie, String.valueOf(Integer.parseInt(et.getText().toString())));
                                } catch (IOException e) {
                                    MainActivity.instence.showToast(e.toString());
                                }
                            }
                        }
                    }
                }).start();
            }
        });
    }

    public void sendFollowDataStep1(String cookie, String UID) throws IOException {
        String content = "";
        URL postUrl = null;
        postUrl = new URL("https://api.bilibili.com/x/relation/modify?cross_domain=true");
        content = "fid=" + UID +
                "&act=1" +
                "&re_src=122" +
                "&csrf=" + MainActivity.instence.cookieToMap(cookie).get("bili_jct");
        // 打开连接
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        // 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        //	 Post请求不能使用缓存
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Host", "api.bilibili.com");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Origin", "https://www.bilibili.com");
        connection.setRequestProperty("User-Agent", MainActivity.instence.userAgent);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer", "https://www.bilibili.com/video/av" + new Random().nextInt() % 47957369);
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("cookie", cookie);

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
        MainActivity.instence.showToast(ss);
        reader.close();
        connection.disconnect();
    }

    public void sendFollowDataStep2(String cookie, String UID) throws IOException {
        String content = "";
        URL postUrl = null;
        //   if (clickFollow) {
        postUrl = new URL("https://api.bilibili.com/x/relation/tags/addUsers?cross_domain=true");
        content = "fids=" + UID +
                "&tagids=0" +
                "&csrf=" + MainActivity.instence.cookieToMap(cookie).get("bili_jct");
        //    } else {
        //       postUrl = new URL("https://api.bilibili.com/x/relation/modify?cross_domain=true");
        //       content = "fid=" + UID +
        //               "&act=2" +
        //               "&re_src=122" +
        //               "&csrf=" + MainActivity.instence.cookieToMap(cookie).get("bili_jct");
        //    }
        // 打开连接
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        // 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        //	 Post请求不能使用缓存
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Host", "api.bilibili.com");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Origin", "https://www.bilibili.com");
        connection.setRequestProperty("User-Agent", MainActivity.instence.userAgent);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer", "https://www.bilibili.com/video/av" + new Random().nextInt() % 47957369);
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("cookie", cookie);

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
        MainActivity.instence.showToast(ss);
        reader.close();
        connection.disconnect();
    }
}
