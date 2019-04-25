package com.meng.bilibilihelper;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import com.google.gson.*;
import com.meng.bilibilihelper.javaBean.relation.*;
import com.meng.bilibilihelper.javaBean.spaceToLive.*;
import com.meng.bilibilihelper.javaBean.upstat.*;
import com.meng.bilibilihelper.javaBean.user.*;

import java.io.*;
import java.net.*;

import android.view.View.OnClickListener;

public class InfoFragment extends Fragment {

    public ProgressBar progressBar;
    private LinearLayout l1;
    private BilibiliPersonInfo bilibiliPersonInfo;

    public InfoFragment(BilibiliPersonInfo bilibiliPersonInfo) {
        super();
        this.bilibiliPersonInfo = bilibiliPersonInfo;
    }

    public InfoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.info_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) view.findViewById(R.id.info_listProgressBar1);
        l1 = (LinearLayout) view.findViewById(R.id.info_listLinearLayout_MengNetworkTextview);
        ImageView im = new ImageView(getActivity());
        im.setImageBitmap(BitmapFactory.decodeFile(MainActivity.mainDic + "bilibili/" + bilibiliPersonInfo.data.mid + ".jpg"));
        l1.addView(im);

        Button btnNai = (Button) view.findViewById(R.id.nai);
        Button btnSign = (Button) view.findViewById(R.id.sign);
        btnNai.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        ListView l = new ListView(getActivity());
                        l.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, MainActivity.instence.strings));
                        l.setOnItemClickListener(new OnItemClickListener() {

                            @Override
                            public void onItemClick(final AdapterView<?> p1, View p2, final int p3, long p4) {
                                try {
                                    MainActivity.instence.sendDanmakuData((String) p1.getItemAtPosition(p3), MainActivity.instence.loginInfoPeopleHashMap.get(bilibiliPersonInfo.data.mid).cookie, MainActivity.instence.frag.editText.getText().toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        new AlertDialog.Builder(getActivity())
                                .setView(l)
                                .setTitle("奶")
                                .setNegativeButton("我好了", null).show();
                    }
                }).start();
            }
        });
        btnSign.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            MainActivity.instence.sendSignData(MainActivity.instence.loginInfoPeopleHashMap.get(bilibiliPersonInfo.data.mid).cookie, MainActivity.instence.frag.editText.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


        //	getBilibiliUserInfo(uid);
    }

    private String readHttpString(String urlstr) throws MalformedURLException, IOException {
        URL url = new URL(urlstr);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    private void getBilibiliUserInfo(final String uid) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Gson gson = new Gson();
                try {
                    final BilibiliPersonInfo person = gson.fromJson(readHttpString("https://api.bilibili.com/x/space/acc/info?mid=" + uid + "&jsonp=jsonp"), BilibiliPersonInfo.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            l1.addView(new MengNetworkTextview(getActivity(), "ID", person.data.mid));
                            l1.addView(new MengNetworkTextview(getActivity(), "用户名", person.data.name));
                            l1.addView(new MengNetworkTextview(getActivity(), "性别", person.data.sex));
                            l1.addView(new MengNetworkTextview(getActivity(), "签名", person.data.sign));
                            l1.addView(new MengNetworkTextview(getActivity(), "等级", person.data.level));
                            l1.addView(new MengNetworkTextview(getActivity(), "生日", person.data.birthday));
                            l1.addView(new MengNetworkTextview(getActivity(), "硬币", person.data.coins));
                            l1.addView(new MengNetworkTextview(getActivity(), "vip类型", person.data.vip.type));
                            l1.addView(new MengNetworkTextview(getActivity(), "vip状态", person.data.vip.status));
                        }
                    });
                    final SpaceToLiveJavaBean sjb = gson.fromJson(readHttpString("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + uid), SpaceToLiveJavaBean.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l1.addView(new MengNetworkTextview(getActivity(), "直播URL", sjb.data.url));
                            l1.addView(new MengNetworkTextview(getActivity(), "标题", sjb.data.title));
                            l1.addView(new MengNetworkTextview(getActivity(), "状态", sjb.data.liveStatus == 1 ? "正在直播" : "未直播"));
                            l1.addView(new MengNetworkTextview(getActivity(), "房间号", sjb.data.roomid));
                        }
                    });
                    final Relation r = gson.fromJson(readHttpString("https://api.bilibili.com/x/relation/stat?vmid=" + uid + "&jsonp=jsonp"), Relation.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l1.addView(new MengNetworkTextview(getActivity(), "粉丝", r.data.follower));
                            l1.addView(new MengNetworkTextview(getActivity(), "关注", r.data.following));
                        }
                    });
                    final Upstat u = gson.fromJson(readHttpString("https://api.bilibili.com/x/space/upstat?mid=" + uid + "&jsonp=jsonp"), Upstat.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l1.addView(new MengNetworkTextview(getActivity(), "播放量", u.data.archive.view));
                            l1.addView(new MengNetworkTextview(getActivity(), "阅读量", u.data.article.view));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
