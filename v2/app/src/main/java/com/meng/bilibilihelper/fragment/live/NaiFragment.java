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
		customSentenseFile = new File(Environment.getExternalStorageDirectory() + "/sjf.json");
		if (customSentenseFile.exists()) {
			customSentence = new Gson().fromJson(MainActivity.instence.methodsManager.readFileToString(customSentenseFile), CustomSentence.class);
		  } else {
			try {
				customSentenseFile.createNewFile();
			  } catch (IOException e) {}
			customSentence = new CustomSentence();
			String[] strings = new String[]{
				"此生无悔入东方,来世愿生幻想乡",
				"红魔地灵夜神雪,永夜风神星莲船",
				"非想天则文花贴,萃梦神灵绯想天",
				"冥界地狱异变起,樱下华胥主谋现",
				"净罪无改渡黄泉,华鸟风月是非辨",
				"境界颠覆入迷途,幻想花开啸风弄",
				"二色花蝶双生缘,前缘未尽今生还",
				"星屑洒落雨霖铃,虹彩彗光银尘耀",
				"无寿迷蝶彼岸归,幻真如画妖如月",
				"永劫夜宵哀伤起,幼社灵中幻似梦",
				"追忆往昔巫女缘,须弥之间冥梦现",
				"仁榀华诞井中天,歌雅风颂心无念"
			  };
			customSentence.sent.addAll(Arrays.asList(strings));
			saveConfig();
		  }
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
			FileOutputStream fos = new FileOutputStream(customSentenseFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "utf-8");
            writer.write(new Gson().toJson(customSentence));
            writer.flush();
            fos.close();
		  } catch (IOException e) {
            throw new RuntimeException(customSentenseFile.getAbsolutePath() + " not found");
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
