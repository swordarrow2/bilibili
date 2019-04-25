package com.meng.bilibilihelper;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.google.gson.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.materialDesign.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jsoup.*;

public class MainActivity extends Activity {
    public static MainActivity instence;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

    public ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    public ArrayList<String> fragmentNames = new ArrayList<String>();

    public static String mainDic = "";
    public MainFrgment frag;
    public HashMap<String, LoginInfoPeople> loginInfoPeopleHashMap = new HashMap<>();
    public LoginInfo loginInfo;

    public final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";

    public String[] strings = new String[]{
            "发发发",
            "你稳了",
            "不会糟的",
            "稳的很",
            "今天,也是发气满满的一天",
            "你这把全关稳了",
            "点歌 信仰は儚き人間の為に",
            "点歌 星条旗のピエロ",
            "点歌 春の湊に-上海アリス幻樂団",
            "点歌 the last crusade",
            "点歌 ピュアヒューリーズ~心の在処",
            "点歌 忘れがたき、よすがの緑",
            "点歌 遥か38万キロのボヤージュ",
            "点歌 プレイヤーズスコア"
    };

    public ArrayAdapter<String> adapter;
    public ArrayList<String> arrayList;

    public FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence = this;
        mainDic = Environment.getExternalStorageDirectory() + "/Pictures/grzx/";
        copyFile();
        File f = new File("info.json");
        if (!f.exists()) {
            f.mkdirs();
            loginInfo = new LoginInfo();
            saveConfig();
        }
        arrayList = new ArrayList<>();
        try {
            loginInfo = new Gson().fromJson(readFileToString(), LoginInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (loginInfo != null) {
            for (LoginInfoPeople loginInfoPeople : loginInfo.loginInfoPeople) {
                loginInfoPeopleHashMap.put(loginInfoPeople.personInfo.data.name, loginInfoPeople);
                fragmentNames.add(loginInfoPeople.personInfo.data.name);
                arrayList.add(loginInfoPeople.personInfo.data.name);
            }
            initFragment();
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        findViews();
        initFragment();
        setActionBar();
        setListener();
    }

    private void setActionBar() {
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    private void setListener() {
        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, drawerArrow, R.string.open, R.string.close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FragmentTransaction transactionBusR = manager.beginTransaction();
                Fragment f = fragments.get(position);
                hideFragment(transactionBusR);
                transactionBusR.show(f);
                transactionBusR.commit();

                mDrawerToggle.syncState();
                mDrawerLayout.closeDrawer(mDrawerList);
            }

        });
    }

    private void findViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        TextView v = new TextView(this);
        v.setText("add");
        v.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });
        mDrawerList.addHeaderView(v);
    }

    private void initFragment() {
        manager = getFragmentManager();
        for (LoginInfoPeople loginInfoPeople : loginInfoPeopleHashMap.values()) {
            FragmentTransaction transactionBusR = manager.beginTransaction();
            Fragment f = new InfoFragment(loginInfoPeople.personInfo);
            transactionBusR.add(R.id.main_activityLinearLayout, f);
            fragments.add(f);
            hideFragment(transactionBusR);
            transactionBusR.commit();
        }
        FragmentTransaction transactionBusR = manager.beginTransaction();
        frag = new MainFrgment();
        fragments.add(frag);
        transactionBusR.add(R.id.main_activityLinearLayout, frag);
        hideFragment(transactionBusR);
        transactionBusR.commit();

    }

    public void hideFragment(FragmentTransaction transaction) {
        for (Fragment f : fragments) {
            if (f != null) {
                transaction.hide(f);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void doVibrate(long time) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

    public String getSourceCode(String url) {
        return getSourceCode(url, null);
    }

    public String getSourceCode(String url, String cookie) {
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
                showToast(String.valueOf(response.statusCode()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.body();
    }

    public Map<String, String> cookieToMap(String value) {
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

    public String readFileToString() throws IOException, UnsupportedEncodingException {
        File file = new File(getApplicationContext().getFilesDir() + "/info.json");
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

    public void saveConfig() {
        try {
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            File file = new File("info.json");
            fos = new FileOutputStream(file);
            writer = new OutputStreamWriter(fos, "utf-8");
            writer.write(new Gson().toJson(loginInfo));
            writer.flush();
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDanmakuData(String msg, String cookie, final String roomId) throws IOException {
        URL postUrl = new URL("http://api.live.bilibili.com/msg/send");
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
        connection.setRequestProperty("User-Agent", userAgent);
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
                "&bubble=0" +
                "&csrf_token=" + cookieToMap(cookie).get("bili_jct") +
                "&csrf=" + cookieToMap(cookie).get("bili_jct");
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
            final ReturnData returnData = new Gson().fromJson(ss, ReturnData.class);
            switch (returnData.code) {
                case 0:
                    if (!returnData.message.equals("")) {
                        showToast(returnData.message);
                    } else {
                        showToast(roomId + "已奶");
                    }
                    break;
                case 1990000:
                    if (returnData.message.equals("risk")) {
                        showToast("需要在官方客户端进行账号风险验证");
                    }
                    break;
                default:
                    showToast(ss);
                    break;
            }
        } catch (Exception e) {
            showToast(ss);
        }
    }

    public String encode(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "Issue while encoding" + e.getMessage();
        }
    }

    public boolean copyFile() {
        File f1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/meng/myBilibili/" + "info.json");
        File f2 = new File(getApplicationContext().getFilesDir() + "/info.json");
        if (!f1.exists()) {
            return false;
        }
        if (!f2.exists()) {
            try {
                f2.createNewFile();
            } catch (IOException e) {
                showToast(e.toString());
            }
        } else {
            showToast("f2Exists");
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(f1);
        } catch (FileNotFoundException e) {
            showToast("f1NotFound");
            return false;
        }
        try {
            out = new FileOutputStream(f2);
        } catch (FileNotFoundException e) {
            showToast("f2NotFound");
            return false;
        }
        if (in != null && out != null) {
            int temp;
            try {
                while ((temp = in.read()) != -1) {
                    out.write(temp);
                }
            } catch (IOException e) {
                showToast(e.toString());
                return false;
            }
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                showToast(e.toString());
                return false;
            }
        }
        return true;
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
        connection.setRequestProperty("User-Agent", userAgent);
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
        showToast("结果" + ss);
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
