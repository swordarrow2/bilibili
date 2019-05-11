package com.meng.bilibilihelper.activity;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.javaBean.BilibiliMyInfo;
import com.meng.bilibilihelper.libAndHelper.DownloadImageRunnable;
import com.meng.bilibilihelper.libAndHelper.HeadType;
import com.meng.bilibilihelper.libAndHelper.MengTextview;

import java.io.*;

public class InfoActivity extends Activity {

    public ProgressBar progressBar;
    public static String mainDic = "";

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
        mainDic = Environment.getExternalStorageDirectory() + "/Pictures/grzx/";

        setContentView(R.layout.info_list);
        context = this;
        progressBar = (ProgressBar) findViewById(R.id.info_listProgressBar1);
        l1 = (LinearLayout) findViewById(R.id.info_listLinearLayout_MengNetworkTextview);
        final ImageView im = new ImageView(this);
        im.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                File imf = new File(mainDic + "bilibili/" + intent.getStringExtra("bid") + ".jpg");
                imf.delete();
                MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(InfoActivity.this, im, intent.getStringExtra("bid"), HeadType.BilibiliUser));
            }
        });

        File imf = new File(mainDic + "bilibili/" + intent.getStringExtra("bid") + ".jpg");
        if (imf.exists()) {
            im.setImageBitmap(BitmapFactory.decodeFile(imf.getAbsolutePath()));
        } else {
            MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(InfoActivity.this, im, intent.getStringExtra("bid"), HeadType.BilibiliUser));
        }
        l1.addView(im);
        getBilibiliUserInfo(intent.getStringExtra("bid"), MainActivity.instence.loginInfo.loginInfoPeople.get(intent.getIntExtra("pos", 0)).cookie);
    }

    private void getBilibiliUserInfo(final String uid, final String cookie) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Gson gson = new Gson();
                try {
                    final BilibiliMyInfo info = gson.fromJson(MainActivity.instence.getSourceCode("http://api.bilibili.com/x/space/myinfo?jsonp=jsonp", cookie), BilibiliMyInfo.class);
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
                            l1.addView(new MengTextview(context, "注册时间", info.data.jointime));
                            l1.addView(new MengTextview(context, "节操", info.data.moral));
                            l1.addView(new MengTextview(context, "绑定邮箱", info.data.email_status));
                            l1.addView(new MengTextview(context, "绑定手机", info.data.tel_status));
                            l1.addView(new MengTextview(context, "生日", info.data.birthday));
                            l1.addView(new MengTextview(context, "硬币", info.data.coins));
                            l1.addView(new MengTextview(context, "vip类型", info.data.vip.type));
                            l1.addView(new MengTextview(context, "vip状态", info.data.vip.status));
                            int ii = InfoActivity.this.getIntent().getIntExtra("pos", 0);
                            if (!MainActivity.instence.loginInfo.loginInfoPeople.get(ii).personInfo.data.name.equals(info.data.name)) {
                                MainActivity.instence.loginInfo.loginInfoPeople.get(ii).personInfo.data.name = info.data.name;

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        MainActivity.instence.arrayList.clear();
                                        for (LoginInfoPeople loginInfoPeople : MainActivity.instence.loginInfo.loginInfoPeople) {;
                                            MainActivity.instence.arrayList.add(loginInfoPeople.personInfo.data.name);
                                        }
                                        MainActivity.instence.adapter.notifyDataSetChanged();
                                        MainActivity.instence.saveConfig();
                                    }
                                });
                            }
                        }
                    });
                    final UserSpaceToLive sjb = gson.fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + uid), UserSpaceToLive.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l1.addView(new MengTextview(context, "直播URL", sjb.data.url));
                            l1.addView(new MengTextview(context, "标题", sjb.data.title));
                            l1.addView(new MengTextview(context, "状态", sjb.data.liveStatus == 1 ? "正在直播" : "未直播"));
                            l1.addView(new MengTextview(context, "房间号", sjb.data.roomid));
                        }
                    });
                    final BilibiliUserRelation r = gson.fromJson(MainActivity.instence.getSourceCode("https://api.bilibili.com/x/relation/stat?vmid=" + uid + "&jsonp=jsonp"), BilibiliUserRelation.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l1.addView(new MengTextview(context, "粉丝", r.data.follower));
                            l1.addView(new MengTextview(context, "关注", r.data.following));
                        }
                    });
                    final BilibiliUpStatus u = gson.fromJson(MainActivity.instence.getSourceCode("https://api.bilibili.com/x/space/upstat?mid=" + uid + "&jsonp=jsonp"), BilibiliUpStatus.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l1.addView(new MengTextview(context, "播放量", u.data.archive.view));
                            l1.addView(new MengTextview(context, "阅读量", u.data.article.view));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
