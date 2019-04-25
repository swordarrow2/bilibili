package com.meng.bilibilihelper;

import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.meng.bilibilihelper.javaBean.relation.Relation;
import com.meng.bilibilihelper.javaBean.spaceToLive.SpaceToLiveJavaBean;
import com.meng.bilibilihelper.javaBean.upstat.Upstat;
import com.meng.bilibilihelper.javaBean.user.BilibiliPersonInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView acountList = (ListView) view.findViewById(R.id.list);
        acountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            sendSignData(MainActivity.instence.loginInfoPeopleHashMap.get(parent.getItemAtPosition(position)).cookie, MainActivity.instence.mainFrgment.editText.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    public void sendSignData(String cookie, String roomId) throws IOException {
        URL postUrl = new URL("https://api.live.bilibili.com/sign/doSign");
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
        final String ss = s.toString();
        reader.close();
        connection.disconnect();
        MainActivity.instence.showToast("结果" + ss);
    }
}
