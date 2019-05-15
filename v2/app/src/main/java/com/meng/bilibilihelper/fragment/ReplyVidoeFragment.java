package com.meng.bilibilihelper.fragment;

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
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;

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

public class ReplyVidoeFragment extends Fragment {
    public ListView listview;
    public Button btn;
    public EditText etAv;
    public EditText etContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_reply, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listview = (ListView) view.findViewById(R.id.send_reply_listView);
        btn = (Button) view.findViewById(R.id.send_reply_button);
        etAv = (EditText) view.findViewById(R.id.send_reply_editText_av);
        etContent = (EditText) view.findViewById(R.id.send_reply_editText_content);

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
                                    sendReplyData(etContent.getText().toString(), ((LoginInfoPeople) cda.getItem(i)).cookie, String.valueOf(Integer.parseInt(etAv.getText().toString())));
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

    public void sendReplyData(String msg, String cookie, String AID) throws IOException {

        Connection connection = Jsoup.connect("https://api.bilibili.com/x/v2/reply/add");
        String csrf = MainActivity.instence.cookieToMap(cookie).get("bili_jct");
        Map<String, String> map = new HashMap<>();
        map.put("Host", "api.bilibili.com");
        map.put("Accept", "application/json, text/javascript, */*; q=0.01");
        map.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        map.put("Connection", "keep-alive");
        map.put("Origin", "https://www.bilibili.com");
        connection.userAgent(MainActivity.instence.userAgent)
                .headers(map)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/video/av" + AID)
                .cookies(MainActivity.instence.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("oid=" + AID +
                        "&type=1" +
                        "&message=" + encode(msg) +
                        "&jsonp=jsonp" +
                        "&csrf=" + MainActivity.instence.cookieToMap(cookie).get("bili_jct"));
        Connection.Response response = connection.execute();
        if (response.statusCode() != 200) {
            MainActivity.instence.showToast(String.valueOf(response.statusCode()));
        }
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(response.body()).getAsJsonObject();
        MainActivity.instence.showToast(obj.get("message").getAsString());
        /*
        URL postUrl = new URL("https://api.bilibili.com/x/v2/reply/add");
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
        connection.setRequestProperty("Connection", "keep-alive");*/
        //   connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
      /*  connection.setRequestProperty("Origin", "https://www.bilibili.com");
        connection.setRequestProperty("User-Agent", MainActivity.instence.userAgent);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer", "https://www.bilibili.com/video/av" + AID);
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("cookie", cookie);
        String content = "oid=" + AID +
                "&type=1" +
                "&message=" + encode(msg) +
                "&jsonp=jsonp" +
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
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(ss).getAsJsonObject();
        MainActivity.instence.showToast(obj.get("message").getAsString());
        reader.close();
        connection.disconnect();
*/
    }

    public String encode(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "Issue while encoding" + e.getMessage();
        }
    }
}
