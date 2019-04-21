package com.meng.bilibili;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    public static MainActivity instence;

    private final String exDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    public final String mainDic = exDir + "/meng/myBilibili/";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instence = this;
        startActivity(new Intent(MainActivity.this, MainActivity2.class).putExtra("setTheme", getIntent().getBooleanExtra("setTheme", false)));
        finish();
        overridePendingTransition(0, 0);
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
}
