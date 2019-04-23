package com.meng.bilibili;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.meng.bilibili.javaBean.LoginInfo;
import com.meng.bilibili.javaBean.LoginInfoPeople;

public class MainActivity extends Activity {
    public static MainActivity instence;
    public final String POST_URL = "http://api.live.bilibili.com/msg/send";
    public static final String UA = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";
    public static Gson gson = new Gson();
    private static final String exDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String mainDic = exDir + "/meng/myBilibili/";
    public HashMap<String, LoginInfoPeople> hashMap = new HashMap<>();
    public static LoginInfo loginInfo;
    private Button btn;
    private ListView listView;
    private EditText et;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence = this;
        File f = new File(mainDic);
        if (!f.exists()) {
            f.mkdirs();
            loginInfo = new LoginInfo();
            saveConfig();
        }
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            loginInfo = gson.fromJson(readFileToString(), LoginInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (loginInfo != null) {
            for (LoginInfoPeople loginInfoPeople : loginInfo.loginInfoPeople) {
                hashMap.put(loginInfoPeople.name, loginInfoPeople);
                arrayList.add(loginInfoPeople.name);
            }
        }
        btn = (Button) findViewById(R.id.btn);
        listView = (ListView) findViewById(R.id.lv);
        et = (EditText) findViewById(R.id.et);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    sendDanmakuData("发发发", hashMap.get(parent.getItemAtPosition(position)).cookie, Long.parseLong(et.getText().toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });


    }

    public void doVibrate(long time) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

    public static String getSourceCode(String url) {
        return getSourceCode(url, null);
    }

    public static String getSourceCode(String url, String cookie) {
        Connection.Response response = null;
        Connection connection = null;
        try {
            connection = Jsoup.connect(url);
            if (cookie != null) {
                connection.cookies(cookieToMap(cookie));
            }
            connection.ignoreContentType(true).method(Connection.Method.GET);
            response = connection.execute();
            if (response.statusCode() != 200) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.body();
    }

    public static Map<String, String> cookieToMap(String value) {
        Map<String, String> map = new HashMap<String, String>();
        String values[] = value.split("; ");
        for (String val : values) {
            String vals[] = val.split("=");
            if (vals.length == 2) {
                map.put(vals[0], vals[1]);
            } else if (vals.length == 1) {
                map.put(vals[0], "");
            }
        }
        return map;
    }

    public static String readFileToString() throws IOException, UnsupportedEncodingException {
        File file = new File(mainDic + "info.json");
        if (!file.exists()) {
            file.createNewFile();
        }
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(file);
        in.read(filecontent);
        in.close();
        return new String(filecontent, "UTF-8");
    }

    public static void saveConfig() {
        try {
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            File file = new File(mainDic + "info.json");
            fos = new FileOutputStream(file);
            writer = new OutputStreamWriter(fos, "utf-8");
            writer.write(gson.toJson(loginInfo));
            writer.flush();
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDanmakuData(String msg, String cookie, final long roomId) throws IOException {
        URL postUrl = new URL(POST_URL);
        String content = "";//要发出的数据
        // 打开连接
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        // 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        //	 Post 请求不能使用缓存
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        //	发送弹幕貌似要检查User-Agent和cookie Referer貌似并没有检查但发送的数据中包含这一项于是就加上了
        connection.setRequestProperty("Host", "api.live.bilibili.com");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Origin", "https://live.bilibili.com");
        connection.setRequestProperty("User-Agent", UA);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer", "https://live.bilibili.com/" + roomId);
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("cookie", cookie);
        content = "color=16777215" +
                "&fontsize=25" +
                "&mode=1" +
                "&msg=" + encode(msg) +
                "&rnd=" + (System.currentTimeMillis() / 1000) +
                "&roomid=" + roomId +
                "&csrf_token=" + cookieToMap(cookie).get("bili_jct");
        connection.setRequestProperty("Content-Length", String.valueOf(content.length()));

        // 连接，从postUrl.openConnection()至此的配置必须要在 connect之前完成，
        // 要注意的是connection.getOutputStream会隐含的进行 connect。
        connection.connect();
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(content);
        out.flush();
        out.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        System.out.println(" Contents of post request ");
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println(" Contents of post request ends ");
        reader.close();
        connection.disconnect();

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MainActivity.this, roomId + "已奶", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String encode(String url) {
        try {
            String encodeURL = URLEncoder.encode(url, "UTF-8");
            return encodeURL;
        } catch (UnsupportedEncodingException e) {
            return "Issue while encoding" + e.getMessage();
        }
    }


}
