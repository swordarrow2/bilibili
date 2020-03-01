package com.meng.biliv3.fragment;

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.adapters.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libAndHelper.*;
import java.io.*;
import java.net.*;
import java.util.*;
import android.os.*;

public class BaseIdFragment extends Fragment {

	public static final String typeUID = "uid";
	public static final String typeAv = "av";
	public static final String typeLive = "lv";

	protected static final int SendDanmaku=0;
	protected static final int Silver=1;
	protected static final int Pack=2;
	protected static final int Sign=3;
	protected static final int SendJudge=4;
	protected static final int Zan=5;
	protected static final int Coin1=6;
	protected static final int Coin2=7;
	protected static final int Favorite=8;

	protected int id;
	protected String type;

	protected static ArrayAdapter<String> sencencesAdapter=null;
	protected static ArrayAdapter<String> spinnerAccountAdapter=null;
	private static ArrayList<String> spList=null;
	private static CustomSentence customSentence;
	private static File customSentenseFile;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		customSentenseFile = new File(Environment.getExternalStorageDirectory() + "/sjf.json");
		if (customSentenseFile.exists() && customSentence != null) {
			customSentence = new Gson().fromJson(Tools.FileTool.readString(customSentenseFile), CustomSentence.class);
		} else {
			customSentence = new CustomSentence();
			String[] strings = new String[]{ "此生无悔入东方,来世愿生幻想乡","红魔地灵夜神雪,永夜风神星莲船","非想天则文花贴,萃梦神灵绯想天","冥界地狱异变起,樱下华胥主谋现","净罪无改渡黄泉,华鸟风月是非辨","境界颠覆入迷途,幻想花开啸风弄","二色花蝶双生缘,前缘未尽今生还","星屑洒落雨霖铃,虹彩彗光银尘耀","无寿迷蝶彼岸归,幻真如画妖如月","永劫夜宵哀伤起,幼社灵中幻似梦","追忆往昔巫女缘,须弥之间冥梦现","仁榀华诞井中天,歌雅风颂心无念" };
			customSentence.sent.addAll(Arrays.asList(strings));
			saveConfig();
		}
		createSpinnerList();
		if (sencencesAdapter == null) {
			sencencesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, customSentence.sent);
		}
	}

	public static void createSpinnerList() {
		if (spList == null) {
			spList = new ArrayList<>();
		} else {
			spList.clear();
		}
		spList.add("每次选择");
		spList.add("主账号");
		for (AccountInfo ai:MainActivity.instance.loginAccounts) {
			spList.add(ai.name);
		}
		if (spinnerAccountAdapter != null) {
			spinnerAccountAdapter.notifyDataSetChanged();
		} else {
			spinnerAccountAdapter = new ArrayAdapter<String>(MainActivity.instance, android.R.layout.simple_list_item_1, spList);
		}
	}

	protected void sendBili(final String sel, final int opValue, final String msg) {
		if (sel.equals("每次选择")) {
			String items[] = new String[MainActivity.instance.loginAccounts.size()];
			for (int i=0;i < items.length;++i) {
				items[i] = MainActivity.instance.loginAccounts.get(i).name;
			}
			final boolean checkedItems[] = new boolean[items.length];
			new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle("选择账号").setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						checkedItems[which] = isChecked;
					}
				}).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (int i = 0; i < checkedItems.length; i++) {
							if (checkedItems[i]) {
								opSwitch(MainActivity.instance.loginAccounts.get(i), opValue, msg);
							}
						}
					}
				}).show();
		} else {
			opSwitch(sel.equals("主账号") ?MainActivity.instance.getAccount(Integer.parseInt(SharedPreferenceHelper.getValue("mainAccount", ""))): MainActivity.instance.getAccount(sel), opValue, msg);
		}
	}

	private void opSwitch(final AccountInfo ai, final int opValue, final String msg) {
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					switch (opValue) {
						case SendDanmaku:
							Tools.BilibiliTool.sendLiveDanmaku(msg, ai.cookie, id);
							break;
						case Silver:
							MainActivity.instance.runOnUiThread(new Runnable(){

									@Override
									public void run() {
										final EditText editText = new EditText(getActivity());
										new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle("输入辣条数").setView(editText).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													MainActivity.instance.threadPool.execute(new Runnable(){

															@Override
															public void run() {
																String content = editText.getText().toString();
																JsonObject liveToMainInfo=null;
																try {
																	liveToMainInfo = new JsonParser().parse(Tools.Network.getSourceCode("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=" + id)).getAsJsonObject().get("data").getAsJsonObject().get("info").getAsJsonObject();
																} catch (Exception e) {
																	return;
																}
																long uid=liveToMainInfo.get("uid").getAsLong();
																Tools.BilibiliTool.sendHotStrip(ai.uid, uid, id, Integer.parseInt(content), ai.cookie);
															}
														});
												}
											}).show();
									}
								});
							break;
						case Pack:
							sendPackDialog(ai);
							break;
						case Sign:
							Tools.BilibiliTool.sendLiveSign(ai.cookie);
							break;
						case SendJudge:
							Tools.BilibiliTool.sendVideoJudge(msg, id, ai.cookie);
							break;
						case Zan:
							Tools.BilibiliTool.sendLike(id, ai.cookie);
							break;
						case Coin1:
							Tools.BilibiliTool.sendCoin(1, id, ai.cookie);
							break;
						case Coin2:
							Tools.BilibiliTool.sendCoin(2, id, ai.cookie);
							break;
						case Favorite:
							MainActivity.instance.showToast("未填坑");
							break;
					}
				}
			});
	}

	private void sendPackDialog(final AccountInfo ai) {
		JsonObject liveToMainInfo = new JsonParser().parse(Tools.Network.getSourceCode("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=" + id)).getAsJsonObject().get("data").getAsJsonObject().get("info").getAsJsonObject();
		final long uid=liveToMainInfo.get("uid").getAsLong();
		final GiftBag liveBag = new Gson().fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/xlive/web-room/v1/gift/bag_list?t=" + System.currentTimeMillis(), ai.cookie), GiftBag.class);
		if (liveBag.data.list.size() == 0) {
			MainActivity.instance.showToast("包裹中什么也没有");
		}
		getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {

					ListView listView=new ListView(getActivity());
					final GiftAdapter giftAdapter = new GiftAdapter(getActivity(), liveBag.data.list);
					listView.setAdapter(giftAdapter);
					int ii=getStripCount(liveBag.data.list);
					MainActivity.instance.showToast("共有" + ii + "辣条");
					listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(final AdapterView<?> parent, View view, final int p, long itemid) {
								final EditText editText = new EditText(getActivity());
								editText.setHint("要赠送的数量");
								new AlertDialog.Builder(getActivity()).setView(editText).setTitle("编辑").setPositiveButton("确定", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface p11, int p2) {
											MainActivity.instance.threadPool.execute(new Runnable() {
													@Override
													public void run() {
														try {
															int num=Integer.parseInt(editText.getText().toString());
															if (num > getStripCount(liveBag.data.list)) {
																MainActivity.instance.showToast("辣条不足");	
																return;
															}
															for (GiftBag.ListItem i:liveBag.data.list) {
																if (i.gift_name.equals("辣条")) {
																	if (num > i.gift_num) {
																		sendHotStrip(ai.uid, uid, id, i.gift_num, ai.cookie, i);
																		num -= i.gift_num;
																		i.gift_num = 0;
																	} else {
																		sendHotStrip(ai.uid, uid, id, num, ai.cookie, i);											
																		i.gift_num -= num;
																		break;	
																	}
																}
															}
															if (getStripCount(liveBag.data.list) == 0) {
																MainActivity.instance.showToast("已送出全部礼物🎁");
															}
															for (int i=0;i < liveBag.data.list.size();++i) {
																if (liveBag.data.list.get(i).gift_name.equals("辣条") && liveBag.data.list.get(i).gift_num == 0) {
																	liveBag.data.list.remove(i);
																}
															}										
															getActivity().runOnUiThread(new Runnable() {
																	@Override
																	public void run() {
																		giftAdapter.notifyDataSetChanged();
																	}
																});
														} catch (IOException e) {
															e.printStackTrace();
														}
													}
												});
										}
									}).setNegativeButton("取消", null).show();
							}
						});
					listView.setOnItemLongClickListener(new OnItemLongClickListener() {

							@Override
							public boolean onItemLongClick(final AdapterView<?> p1, View p2, final int p3, long p4) {
								MainActivity.instance.threadPool.execute(new Runnable() {
										@Override
										public void run() {
											try {
												sendHotStrip(ai.uid, uid, id, liveBag.data.list.get(p3).gift_num, ai.cookie, liveBag.data.list.get(p3));
												liveBag.data.list.remove(p3);
												if (liveBag.data.list.size() == 0) {
													MainActivity.instance.showToast("已送出全部礼物🎁");
												}
												getActivity().runOnUiThread(new Runnable() {
														@Override
														public void run() {
															giftAdapter.notifyDataSetChanged();
														}
													});
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									});
								return true;
							}
						});
				}
			});
	}

	private int getStripCount(ArrayList<GiftBag.ListItem> list) {
		int ii=0;
		for (GiftBag.ListItem i:list) {
			if (i.gift_name.equals("辣条")) {
				ii += i.gift_num;
			}
		}
		return ii;
	}

	protected void sendHotStrip(long uid, long ruid, long roomID, int num, String cookie,  GiftBag.ListItem liveBagDataList) throws IOException {
        URL postUrl = new URL("https://api.live.bilibili.com/gift/v2/live/bag_send");
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
        connection.setRequestProperty("User-Agent", MainActivity.instance.userAgent);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer", "https://live.bilibili.com/" + roomID);
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("cookie", cookie);
        content = "uid=" + uid +
			"&gift_id=" + liveBagDataList.gift_id +
			"&ruid=" + ruid +
			"&gift_num=" + num +
			"&bag_id=" + liveBagDataList.bag_id +
			"&platform=pc" +
			"&biz_code=live" +
			"&biz_id=" + roomID +
			"&rnd=" + (System.currentTimeMillis() / 1000) +
			"&storm_beat_id=0" +
			"&metadata=" +
			"&price=0" +
			"&csrf_token=" + Tools.Network.cookieToMap(cookie).get("bili_jct") +
			"&csrf=" + Tools.Network.cookieToMap(cookie).get("bili_jct") +
			"&visit_id=";
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
        String ss = s.toString();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(ss).getAsJsonObject();
        MainActivity.instance.showToast(obj.get("msg").getAsString());
        reader.close();
        connection.disconnect();
	}

	private void saveConfig() {
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
}
