package com.meng.bilibili;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.meng.bilibili.javaBean.LoginInfo;
import com.meng.bilibili.javaBean.LoginInfoPeople;

public class MainActivity extends Activity {
    public static MainActivity instence;
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
        et=(EditText) findViewById(R.id.et);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,LiveActivity.class);
                String s= (String) parent.getItemAtPosition(position);
                LoginInfoPeople loginInfoPeople=hashMap.get(s);
                intent.putExtra("cookie",loginInfoPeople.cookie);
                intent.putExtra("url",et.getText().toString());
                startActivity(intent);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Login.class);
                intent.putExtra("id",Long.parseLong(et.getText().toString()));
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
}
