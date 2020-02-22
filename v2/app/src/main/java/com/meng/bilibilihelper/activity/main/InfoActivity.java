package com.meng.bilibilihelper.activity.main;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.libAndHelper.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class InfoActivity extends Activity {

    public ProgressBar progressBar;

    private LinearLayout l1;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        if (intent.getStringExtra("bid") == null) {
            finish();
        }
        if (intent.getStringExtra("cookie") == null) {
            finish();
        }

        setContentView(R.layout.info_list);
        context = this;
        progressBar = (ProgressBar) findViewById(R.id.info_listProgressBar1);
        l1 = (LinearLayout) findViewById(R.id.info_listLinearLayout_MengNetworkTextview);
        final ImageView im = new ImageView(this);
        im.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                File imf = new File(MainActivity.instance.mainDic + "bilibili/" + intent.getStringExtra("bid") + ".jpg");
                imf.delete();
                MainActivity.instance.getFragment("人员信息",PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(InfoActivity.this, im, intent.getStringExtra("bid"), DownloadImageRunnable.BilibiliUser));
            }
        });

        File imf = new File(MainActivity.instance.mainDic + "bilibili/" + intent.getStringExtra("bid") + ".jpg");
        if (imf.exists()) {
            im.setImageBitmap(BitmapFactory.decodeFile(imf.getAbsolutePath()));
        } else {
            MainActivity.instance.getFragment("人员信息",PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(InfoActivity.this, im, intent.getStringExtra("bid"), DownloadImageRunnable.BilibiliUser));
        }
        l1.addView(im);
        getBilibiliUserInfo(intent.getStringExtra("bid"), MainActivity.instance.loginAccounts.get(intent.getIntExtra("pos", 0)).cookie);
    }

    private void getBilibiliUserInfo(final String uid, final String cookie) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Gson gson = new Gson();
                try {
                    final BilibiliMyInfo info = gson.fromJson(Tools.Network.getSourceCode("http://api.bilibili.com/x/space/myinfo?jsonp=jsonp", cookie), BilibiliMyInfo.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            l1.addView(new MengTextview(context, "ID", info.data.mid));
                            l1.addView(new MengTextview(context, "用户名", info.data.name));
                            l1.addView(new MengTextview(context, "性别", info.data.sex));
                            l1.addView(new MengTextview(context, "签名", info.data.sign));
                            l1.addView(new MengTextview(context, "等级", info.data.level));
                            l1.addView(new MengTextview(context, "经验", info.data.level_exp.current_exp + "/" + info.data.level_exp.next_exp));
                            l1.addView(new MengTextview(context, "注册时间", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(info.data.jointime * 1000))));
                            l1.addView(new MengTextview(context, "节操", info.data.moral));
                            l1.addView(new MengTextview(context, "绑定邮箱", info.data.email_status == 1 ? "是" : "否"));
                            l1.addView(new MengTextview(context, "绑定手机", info.data.tel_status == 1 ? "是" : "否"));
                            l1.addView(new MengTextview(context, "硬币", info.data.coins));
                            l1.addView(new MengTextview(context, "vip类型", info.data.vip.type));
                            l1.addView(new MengTextview(context, "vip状态", info.data.vip.status));
                            int ii = InfoActivity.this.getIntent().getIntExtra("pos", 0);
                            if (!MainActivity.instance.loginAccounts.get(ii).name.equals(info.data.name)) {
                                MainActivity.instance.loginAccounts.get(ii).name = info.data.name;
                                MainActivity.instance.mainAccountAdapter.notifyDataSetChanged();
                                MainActivity.instance.saveConfig();
                            }
                        }
                    });
                    final UserSpaceToLive sjb = gson.fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + uid), UserSpaceToLive.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l1.addView(new MengTextview(context, "直播URL", sjb.data.url));
                            l1.addView(new MengTextview(context, "标题", sjb.data.title));
                            l1.addView(new MengTextview(context, "状态", sjb.data.liveStatus == 1 ? "正在直播" : "未直播"));
                            l1.addView(new MengTextview(context, "房间号", sjb.data.roomid));
                        }
                    });
                    JsonParser parser = new JsonParser();
                    JsonObject upObj = parser.parse(Tools.Network.getSourceCode("https://api.bilibili.com/x/relation/stat?vmid=" + uid + "&jsonp=jsonp")).getAsJsonObject();
                    final JsonObject upData = upObj.get("data").getAsJsonObject();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l1.addView(new MengTextview(context, "粉丝", upData.get("follower").getAsInt()));
                            l1.addView(new MengTextview(context, "关注", upData.get("following").getAsInt()));
                        }
                    });
                    JsonObject upInfoObj = parser.parse(Tools.Network.getSourceCode("https://api.bilibili.com/x/space/upstat?mid=" + uid + "&jsonp=jsonp")).getAsJsonObject();
                    final JsonObject upInfo = upInfoObj.get("data").getAsJsonObject();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l1.addView(new MengTextview(context, "播放量", upInfo.get("archive").getAsJsonObject().get("view").getAsInt()));
                            l1.addView(new MengTextview(context, "阅读量", upInfo.get("article").getAsJsonObject().get("view").getAsInt()));
                            l1.addView(new MengTextview(context, "cookie", getIntent().getStringExtra("cookie")));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
