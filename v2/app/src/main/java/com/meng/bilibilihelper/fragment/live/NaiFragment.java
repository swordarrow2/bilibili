package com.meng.bilibilihelper.fragment.live;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.activity.live.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.javaBean.personInfo.*;
import com.meng.bilibilihelper.libAndHelper.*;

import java.io.*;
import java.util.*;

import org.jsoup.*;

public class NaiFragment extends BaseFrgment {

    public CustomSentence customSentence;
    File customSentenseFile;
    AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        customSentenseFile = MainActivity.instence.methodsManager.newFile(Environment.getExternalStorageDirectory() + "/sjf.json",
                new Runnable() {
                    @Override
                    public void run() {
                        customSentence = new Gson().fromJson(MainActivity.instence.methodsManager.readFileToString(customSentenseFile), CustomSentence.class);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        customSentence = new CustomSentence();
                        String[] strings = new String[]{
                                "发发发", "你稳了", "不会糟的", "稳的很",
                                "今天,也是发气满满的一天",
                                "你这把全关稳了",
                                "点歌 信仰は儚き人間の為に",
                                "点歌 星条旗のピエロ",
                                "点歌 春の湊に-上海アリス幻樂団",
                                "点歌 the last crusade",
                                "点歌 ピュアヒューリーズ~心の在処",
                                "点歌 忘れがたき、よすがの緑",
                                "点歌 遥か38万キロのボヤージュ",
                                "点歌 プレイヤーズスコア"};
                        customSentence.sent.addAll(Arrays.asList(strings));
                        saveConfig();
                    }
                });
        ListView mainListview = (ListView) view.findViewById(R.id.normal_listview);
        mainListview.setAdapter(MainActivity.instence.loginInfoPeopleAdapter);
        mainListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                ListView naiSentenseListview = new ListView(getActivity());
                naiSentenseListview.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, customSentence.sent));
                naiSentenseListview.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(final AdapterView<?> p1, View p2, final int p3, long p4) {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                sendDanmakuData((String) p1.getItemAtPosition(p3), ((LoginInfoPeople) parent.getItemAtPosition(position)).cookie, MainActivity.instence.mainFrgment.mengEditText.getLiveId());
                            }
                        }).start();
                        alertDialog.dismiss();
                    }
                });
                alertDialog = new AlertDialog.Builder(getActivity())
                        .setView(naiSentenseListview)
                        .setTitle("奶")
                        .setNegativeButton("我好了", null).show();
            }
        });
    }

    public void saveConfig() {
        try {
            FileOutputStream fos;
            OutputStreamWriter writer;
            fos = new FileOutputStream(customSentenseFile);
            writer = new OutputStreamWriter(fos, "utf-8");
            writer.write(new Gson().toJson(customSentence));
            writer.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRandomSentense() {
        return customSentence.sent.get(new Random().nextInt(customSentence.sent.size()));
    }

    public void sendDanmakuData(String msg, String cookie, final String roomId) {
        Connection.Response response = null;
        try {
            Connection connection = Jsoup.connect("http://api.live.bilibili.com/msg/send");
            String csrf = MainActivity.instence.cookieToMap(cookie).get("bili_jct");
            connection.userAgent(MainActivity.instence.userAgent)
                    .headers(liveHead)
                    .ignoreContentType(true)
                    .referrer("https://live.bilibili.com/" + roomId)
                    .cookies(MainActivity.instence.cookieToMap(cookie))
                    .method(Connection.Method.POST)
                    .data("color", "16777215")
                    .data("fontsize", "25")
                    .data("msg", msg)
                    .data("rnd", String.valueOf(System.currentTimeMillis() / 1000))
                    .data("roomid", roomId)
                    .data("bubble", "0")
                    .data("csrf_token", csrf)
                    .data("csrf", csrf);
            response = connection.execute();
            if (response.statusCode() != 200) {
                MainActivity.instence.showToast(String.valueOf(response.statusCode()));
            }
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(response.body()).getAsJsonObject();
            switch (obj.get("code").getAsInt()) {
                case 0:
                    if (!obj.get("message").getAsString().equals("")) {
                        MainActivity.instence.showToast(obj.getAsJsonObject("message").getAsString());
                    } else {
                        MainActivity.instence.showToast(roomId + "已奶");
                    }
                    break;
                case 1990000:
                    if (obj.get("message").getAsString().equals("risk")) {
                        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        if (wifiNetworkInfo.isConnected()) {
                            Intent intent = new Intent(getActivity(), LiveWebActivity.class);
                            intent.putExtra("cookie", cookie);
                            intent.putExtra("url", "https://live.bilibili.com/" + roomId);
                            getActivity().startActivity(intent);
                        } else {
                            MainActivity.instence.showToast("需要在官方客户端进行账号风险验证");
                        }
                    }
                    break;
                default:
                    MainActivity.instence.showToast(response.body());
                    break;
            }
        } catch (Exception e) {
            if (response != null) {
                MainActivity.instence.showToast(response.body());
            }
        }
    }
}
