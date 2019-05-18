package com.meng.bilibilihelper.activity;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;

import com.google.gson.*;
import com.meng.bilibilihelper.activity.main.*;
import com.meng.bilibilihelper.adapters.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.fragment.live.*;
import com.meng.bilibilihelper.fragment.main.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.libAndHelper.*;
import com.meng.bilibilihelper.materialDesign.*;

import java.io.*;
import java.util.*;

import org.jsoup.*;

import com.meng.bilibilihelper.R;


public class MainActivity extends Activity {

    public static MainActivity instence;
    private DrawerLayout mDrawerLayout;
    public ListView mDrawerList;
    private RelativeLayout rightDrawer;
    public ListView rightList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

    public MainFrgment mainFrgment;
    public NaiFragment naiFragment;
    public SignFragment signFragment;
    public ManagerFragment managerFragment;
    public GuaJiFragment guaJiFragment;
    public SettingsFragment settingsFragment;
    public PersonInfoFragment personInfoFragment;
    public SendDanmakuFragment sendDanmakuFragment;
    public SendHotStripFragment sendHotStripFragment;
    public GiftFragment giftFragment;
    public ReplyVideoFragment replayVidoeFragment;
    public FollowFragment followFragment;
    public GiveCoinFragment giveCoinFragment;
    public ZanFragment zanFragment;
    public ChoujiangFragment choujiangFragment;

    public FragmentManager fragmentManager;
    public RelativeLayout relativeLayout;
    public TextView rightText;
    public MengInfoHeaderView infoHeaderLeft;
    public MengLiveControl mengLiveControl;
    public MengInfoHeaderView infoHeaderRight;

    public final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";
    public Gson gson = new Gson();
    public LoginInfo loginInfo;

    public MainListAdapter loginInfoPeopleAdapter;
    public ArrayList<String> arrayList;

    public String jsonPath;
    public String mainDic = "";

    public static boolean onWifi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence = this;
        ExceptionCatcher.getInstance().init(getApplicationContext());
        SharedPreferenceHelper.init(getApplicationContext(), "settings");
        DataBaseHelper.init(getBaseContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("权限申请")
                        .setMessage("本软件需要存储权限用于部分数据存储")
                        .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 321);
                            }
                        }).setCancelable(false).show();
            }
        }
        infoHeaderLeft = new MengInfoHeaderView(this);
        infoHeaderRight = new MengInfoHeaderView(this);
        mengLiveControl = new MengLiveControl(this);
        findViews();
        initFragment();
        setActionBar();
        setListener();
        jsonPath = getApplicationContext().getFilesDir() + "/info.json";
        File f = new File(jsonPath);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
            }
            loginInfo = new LoginInfo();
            saveConfig();
        }
        arrayList = new ArrayList<>();
        try {
            loginInfo = gson.fromJson(readFileToString(), LoginInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (loginInfo != null) {
            for (LoginInfoPeople loginInfoPeople : loginInfo.loginInfoPeople) {
                arrayList.add(loginInfoPeople.personInfo.data.name);
            }
        }
        loginInfoPeopleAdapter = new MainListAdapter(this, loginInfo.loginInfoPeople);
        if (SharedPreferenceHelper.getBoolean("opendraw", true)) {
            mDrawerLayout.openDrawer(mDrawerList);
        } else {
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        mainDic = Environment.getExternalStorageDirectory() + "/Pictures/grzx/";
        File ff = new File(mainDic + "group/");
        if (!ff.exists()) {
            ff.mkdirs();
        }
        File f2 = new File(mainDic + "user/");
        if (!f2.exists()) {
            f2.mkdirs();
        }
        File f3 = new File(mainDic + "bilibili/");
        if (!f3.exists()) {
            f3.mkdirs();
        }
        File f4 = new File(mainDic + ".nomedia");
        if (!f4.exists()) {
            try {
                f4.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        onWifi = wifiNetworkInfo.isConnected();
        final String mainUID = SharedPreferenceHelper.getValue("mainAccount", "");
        if (!mainUID.equals("")) {
            mDrawerList.addHeaderView(mengLiveControl);
            mDrawerList.addHeaderView(infoHeaderLeft);
            rightList.addHeaderView(infoHeaderRight);
            File imf = new File(mainDic + "bilibili/" + mainUID + ".jpg");
            if (imf.exists()) {
                Bitmap b = BitmapFactory.decodeFile(imf.getAbsolutePath());
                MainActivity.instence.infoHeaderLeft.setImage(b);
                MainActivity.instence.infoHeaderRight.setImage(b);
            } else {
                MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(this, infoHeaderLeft.getImageView(), mainUID, HeadType.BilibiliUser));
                MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(this, infoHeaderRight.getImageView(), mainUID, HeadType.BilibiliUser));
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final BilibiliUserInfo info = gson.fromJson(getSourceCode("https://api.bilibili.com/x/space/acc/info?mid=" + mainUID + "&jsonp=jsonp"), BilibiliUserInfo.class);
                    UserSpaceToLive sjb = gson.fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + info.data.mid), UserSpaceToLive.class);
                    String json = MainActivity.instence.getSourceCode("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=" + sjb.data.roomid);
                    JsonParser parser = new JsonParser();
                    JsonObject obj = parser.parse(json).getAsJsonObject();
                    final JsonObject obj2 = obj.get("data").getAsJsonObject().get("level").getAsJsonObject().get("master_level").getAsJsonObject();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonArray ja = obj2.get("next").getAsJsonArray();
                            infoHeaderLeft.setTitle("主播 Lv." + obj2.get("level").getAsInt());
                            infoHeaderLeft.setSummry(obj2.get("anchor_score").getAsInt() + "/" + ja.get(1));
                            infoHeaderRight.setTitle(info.data.name);
                            infoHeaderRight.setSummry("主站 Lv." + info.data.level);
                        }
                    });
                }
            }).start();
        }
        new GithubUpdateManager(this, "swordarrow2", "bilibili", "app.apk");
    }

    private void setActionBar() {
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast("缺失权限会使应用工作不正常");
                } else {
                    initNaiFragment(false);
                    initNaiFragment(true);
                }
            }
        }
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
                "首页(大概)", "奶", "挂机", "发送弹幕", "发送礼物", "cj", "签到", "设置", "退出"
        }));
        rightList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, new String[]{
                "信息", "添加账号", "管理账号", "发送视频评论", "赞视频", "视频投币（2个）", "关注其他用户", "设置", "退出"
        }));
        mDrawerList.setOnItemClickListener(itemClickListener);
        rightList.setOnItemClickListener(itemClickListener);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (view instanceof TextView) {
                switch (((TextView) view).getText().toString()) {
                    case "首页(大概)":
                        initMainFragment(true);
                        break;
                    case "添加账号":
                        startActivity(new Intent(MainActivity.this, Login.class));
                        break;
                    case "管理账号":
                        initManagerFragment(true);
                        break;
                    case "发送弹幕":
                        initSendDanmakuFragment(true);
                        break;
                    case "视频投币（2个）":
                        initCoinFragment(true);
                        break;
                    case "cj":
                        initChouJiangFragment(true);
                        break;
                    case "赞视频":
                        initZanFragment(true);
                        break;
                    case "奶":
                        initNaiFragment(true);
                        break;
                    case "发送礼物":
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("选择礼物类型")
                                .setPositiveButton("瓜子买辣条", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface p11, int p2) {
                                        initHotStripFragment(true);
                                    }
                                }).setNegativeButton("包裹中的辣条", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                initGiftFragment(true);
                            }
                        }).show();
                        break;
                    case "信息":
                        initPersionInfoFragment(true);
                        break;
                    case "挂机":
                        initGuajiFragment(true);
                        break;
                    case "发送视频评论":
                        initReplyVideoFragment(true);
                        break;
                    case "关注其他用户":
                        initFollowFragment(true);
                        break;
                    case "签到":
                        initSignFragment(true);
                        break;
                    case "设置":
                        initSettingsFragment(true);
                        break;
                    case "退出":
                        if (SharedPreferenceHelper.getBoolean("exit", false)) {
                            System.exit(0);
                        } else {
                            finish();
                        }
                        break;
                }
            }
            mDrawerToggle.syncState();
            mDrawerLayout.closeDrawer(mDrawerList);
            mDrawerLayout.closeDrawer(rightDrawer);
        }
    };

    private void findViews() {
        relativeLayout = (RelativeLayout) findViewById(R.id.right_drawer);
        rightText = (TextView) findViewById(R.id.main_activityTextViewRight);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        rightDrawer = (RelativeLayout) findViewById(R.id.right_drawer);
        rightList = (ListView) findViewById(R.id.right_list);
    }

    private void initFragment() {
        fragmentManager = getFragmentManager();
        initSignFragment(false);
        initManagerFragment(false);
        //  initGuajiFragment(false);
        //   initSettingsFragment(false);
        initPersionInfoFragment(false);
        //   initSendDanmakuFragment(false);
        initNaiFragment(false);
        //    initHotStripFragment(false);
        //    initGiftFragment(false);
        //    initReplyVideoFragment(false);
        //    initFollowFragment(false);
        //    initChouJiangFragment(false);
        initMainFragment(true);
    }

    public void initMainFragment(boolean showNow) {
        FragmentTransaction transactionWelcome = fragmentManager.beginTransaction();
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
        FragmentTransaction transactionsettings = fragmentManager.beginTransaction();
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
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
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

    private void initManagerFragment(boolean showNow) {
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
        if (managerFragment == null) {
            managerFragment = new ManagerFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, managerFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(managerFragment);
        }
        transactionBusR.commit();
    }

    private void initGuajiFragment(boolean showNow) {
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
        if (guaJiFragment == null) {
            guaJiFragment = new GuaJiFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, guaJiFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(guaJiFragment);
        }
        transactionBusR.commit();
    }

    private void initSettingsFragment(boolean showNow) {
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, settingsFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(settingsFragment);
        }
        transactionBusR.commit();
    }

    private void initPersionInfoFragment(boolean showNow) {
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
        if (personInfoFragment == null) {
            personInfoFragment = new PersonInfoFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, personInfoFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(personInfoFragment);
        }
        transactionBusR.commit();
    }

    private void initSendDanmakuFragment(boolean showNow) {
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
        if (sendDanmakuFragment == null) {
            sendDanmakuFragment = new SendDanmakuFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, sendDanmakuFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(sendDanmakuFragment);
        }
        transactionBusR.commit();
    }

    private void initHotStripFragment(boolean showNow) {
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
        if (sendHotStripFragment == null) {
            sendHotStripFragment = new SendHotStripFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, sendHotStripFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(sendHotStripFragment);
        }
        transactionBusR.commit();
    }

    private void initGiftFragment(boolean showNow) {
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
        if (giftFragment == null) {
            giftFragment = new GiftFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, giftFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(giftFragment);
        }
        transactionBusR.commit();
    }

    private void initReplyVideoFragment(boolean showNow) {
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
        if (replayVidoeFragment == null) {
            replayVidoeFragment = new ReplyVideoFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, replayVidoeFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(replayVidoeFragment);
        }
        transactionBusR.commit();
    }

    private void initFollowFragment(boolean showNow) {
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
        if (followFragment == null) {
            followFragment = new FollowFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, followFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(followFragment);
        }
        transactionBusR.commit();
    }

    private void initCoinFragment(boolean showNow) {
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
        if (giveCoinFragment == null) {
            giveCoinFragment = new GiveCoinFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, giveCoinFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(giveCoinFragment);
        }
        transactionBusR.commit();
    }

    private void initZanFragment(boolean showNow) {
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
        if (zanFragment == null) {
            zanFragment = new ZanFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, zanFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(zanFragment);
        }
        transactionBusR.commit();
    }

    private void initChouJiangFragment(boolean showNow) {
        FragmentTransaction transactionBusR = fragmentManager.beginTransaction();
        if (choujiangFragment == null) {
            choujiangFragment = new ChoujiangFragment();
            transactionBusR.add(R.id.main_activityLinearLayout, choujiangFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(choujiangFragment);
        }
        transactionBusR.commit();
    }

    public void hideFragment(FragmentTransaction transaction) {
        Fragment fs[] = {
                mainFrgment,
                naiFragment,
                signFragment,
                managerFragment,
                guaJiFragment,
                giftFragment,
                settingsFragment,
                personInfoFragment,
                sendDanmakuFragment,
                sendHotStripFragment,
                replayVidoeFragment,
                followFragment,
                giveCoinFragment,
                choujiangFragment,
                zanFragment
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
        return getSourceCode(url, null, null);
    }

    public String getSourceCode(String url, String cookie) {
        return getSourceCode(url, cookie, null);
    }

    public String getSourceCode(String url, String cookie, String refer) {
        Connection.Response response = null;
        Connection connection;
        try {
            connection = Jsoup.connect(url);
            if (cookie != null) {
                connection.cookies(cookieToMap(cookie));
            }
            if (refer != null) {
                connection.referrer(refer);
            }
            connection.userAgent(userAgent);
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

    public String getCookie(long bid) {
        for (LoginInfoPeople l : loginInfo.loginInfoPeople) {
            if (bid == l.personInfo.data.mid) {
                return l.cookie;
            }
        }
        return null;
    }

    public String readFileToString() throws IOException, UnsupportedEncodingException {
        File file = new File(jsonPath);
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
            File file = new File(jsonPath);
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

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}

