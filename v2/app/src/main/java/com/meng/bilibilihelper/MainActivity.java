package com.meng.bilibilihelper;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;

import com.google.gson.Gson;
import com.meng.bilibilihelper.javaBean.LoginInfo;
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;
import com.meng.bilibilihelper.javaBean.ReturnData;
import com.meng.bilibilihelper.materialDesign.ActionBarDrawerToggle;
import com.meng.bilibilihelper.materialDesign.DrawerArrowDrawable;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {
    public static MainActivity instence;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;


    public static String mainDic = "";
    public MainFrgment mainFrgment;
    public NaiFragment naiFragment;
    public SignFragment signFragment;
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
    public RelativeLayout rt;
    public TextView rightText;

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
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, new String[]{
                "首页(大概)", "奶", "签到", "退出"
        }));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (((TextView) view).getText().toString()) {
                    case "首页(大概)":
                        initMainFragment(true);
                        break;
                    case "奶":
                        initNaiFragment(true);
                        break;
                    case "签到":
                        initSignFragment(true);
                        break;
                    case "退出":
                        if (true) {
                            System.exit(0);
                        } else {
                            finish();
                        }
                        break;
                }
                mDrawerToggle.syncState();
                mDrawerLayout.closeDrawer(mDrawerList);
            }

        });
    }

    private void findViews() {
        rt = (RelativeLayout) findViewById(R.id.right_drawer);
        rightText = (TextView) findViewById(R.id.main_activityTextViewRight);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
    }

    private void initFragment() {
        manager = getFragmentManager();
        initNaiFragment(false);
        initSignFragment(false);
    }

    private void initMainFragment(boolean showNow) {
        FragmentTransaction transactionWelcome = manager.beginTransaction();
        if (mainFrgment == null) {
            mainFrgment = new MainFrgment();
            transactionWelcome.add(R.id.main_activityLinearLayout, mainFrgment);
        }
        hideFragment(transactionWelcome);
        if (showNow) {
            transactionWelcome.show(mainFrgment);
        }
        transactionWelcome.commit();
    }

    private void initNaiFragment(boolean showNow) {
        FragmentTransaction transactionsettings = manager.beginTransaction();
        if (naiFragment == null) {
            naiFragment = new NaiFragment();
            transactionsettings.add(R.id.main_activityLinearLayout, naiFragment);
        }
        hideFragment(transactionsettings);
        if (showNow) {
            transactionsettings.show(naiFragment);
        }
        transactionsettings.commit();
    }

    private void initSignFragment(boolean showNow) {
        FragmentTransaction transactionBusR = manager.beginTransaction();
        if (signFragment == null) {
            signFragment = new SignFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, signFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(signFragment);
        }
        transactionBusR.commit();
    }


    public void hideFragment(FragmentTransaction transaction) {
        Fragment fs[] = {
                mainFrgment,
                naiFragment,
                signFragment
        };
        for (Fragment f : fs) {
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

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}

