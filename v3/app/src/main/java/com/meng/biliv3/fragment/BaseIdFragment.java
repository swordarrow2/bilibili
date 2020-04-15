package com.meng.biliv3.fragment;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.adapters.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libs.*;
import java.io.*;
import java.util.*;
import org.jsoup.*;

public class BaseIdFragment extends Fragment {

	public static final String typeUID = "uid";
	public static final String typeAv = "av";
	public static final String typeLive = "lv";
	public static final String typeCv = "cv";

	protected static final int SendDanmaku=0;
	protected static final int Silver=1;
	protected static final int Pack=2;
	protected static final int Sign=3;
	protected static final int SendVideoJudge=4;
	protected static final int LikeVideo=5;
	protected static final int VideoCoin1=6;
	protected static final int VideoCoin2=7;
	protected static final int Favorite=8;
	protected static final int SendCvJudge=9;
	protected static final int CvCoin1=10;
	protected static final int CvCoin2=11;
	protected static final int LikeArtical=12;

	protected long id;
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

	protected void saveBitmap(String bitName, Bitmap mBitmap) throws Exception {
		File f = new File(Environment.getExternalStorageDirectory() + "/pictures/" + bitName + ".png");
		f.createNewFile();
		FileOutputStream fOut = null;
		fOut = new FileOutputStream(f);
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		fOut.flush();
		fOut.close();
		getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
	}
	
	public static void createSpinnerList() {
		if (spList == null) {
			spList = new ArrayList<>();
		} else {
			spList.clear();
		}
		spList.add("每次选择");
		spList.add("主账号");
		spList.add("全部");
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
		} else if (sel.equals("全部")) {
			MainActivity.instance.threadPool.execute(new Runnable(){

					@Override
					public void run() {
						for (AccountInfo ac:MainActivity.instance.loginAccounts) {
							opSwitch(ac, opValue, msg);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {}
						}
					}
				});
		} else {
			opSwitch(sel.equals("主账号") ?MainActivity.instance.getAccount(MainActivity.instance.sjfSettings.getMainAccount()): MainActivity.instance.getAccount(sel), opValue, msg);
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
										new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle("输入辣条数(" + ai.name + ")").setView(editText).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
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
						case SendVideoJudge:
							Tools.BilibiliTool.sendVideoJudge(msg, id, ai.cookie);
							break;
						case LikeVideo:
							Tools.BilibiliTool.sendAvLike(id, ai.cookie);
							break;
						case VideoCoin1:
							Tools.BilibiliTool.sendAvCoin(1, id, ai.cookie);
							break;
						case VideoCoin2:
							Tools.BilibiliTool.sendAvCoin(2, id, ai.cookie);
							break;
						case Favorite:
							MainActivity.instance.showToast("未填坑");
							break;
						case SendCvJudge:
							Tools.BilibiliTool.sendArticalJudge(id, msg, ai.cookie);
							break;
						case CvCoin1:
							Tools.BilibiliTool.sendCvCoin(1, id, ai.cookie);
							break;
						case CvCoin2:
							Tools.BilibiliTool.sendCvCoin(2, id, ai.cookie);
							break;
						case LikeArtical:
							Tools.BilibiliTool.sendCvLike(id, ai.cookie);
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
			//	MainActivity.instance.showToast("包裹中什么也没有");
		}
		getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ListView listView=new ListView(getActivity());
					new AlertDialog.Builder(getActivity()).setView(listView).setTitle("选择(" + ai.name + ")").show();
					final GiftAdapter giftAdapter = new GiftAdapter(getActivity(), liveBag.data.list);
					listView.setAdapter(giftAdapter);
					int ii=getStripCount(liveBag.data.list);
					//	MainActivity.instance.showToast("共有" + ii + "辣条");
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

	public static void sendHotStrip(long uid, long ruid, long roomID, int num, String cookie,  GiftBag.ListItem liveBagDataList) {
		Connection connection = Jsoup.connect("https://api.live.bilibili.com/gift/v2/live/bag_send");
		String csrf = Tools.Network.cookieToMap(cookie).get("bili_jct");
		connection.userAgent(MainActivity.instance.userAgent)
			.headers(Tools.liveHead)
			.ignoreContentType(true)
			.referrer("https://live.bilibili.com/" + roomID)
			.cookies(Tools.Network.cookieToMap(cookie))
			.method(Connection.Method.POST)
			.data("uid", String.valueOf(uid))
			.data("gift_id", String.valueOf(liveBagDataList.gift_id))
			.data("ruid", String.valueOf(ruid))
			.data("gift_num", String.valueOf(num))
			.data("bag_id", String.valueOf(liveBagDataList.bag_id))
			.data("platform", "pc")
			.data("biz_code", "live")
			.data("biz_id", String.valueOf(roomID))
			.data("rnd", String.valueOf(System.currentTimeMillis() / 1000))
			.data("storm_beat_id", "0")
			.data("metadata", "")
			.data("price", "0")
			.data("csrf_token", csrf)
			.data("csrf", csrf)
			.data("visit_id", "");	
		Connection.Response response=null;
		try {
			response = connection.execute();
		} catch (IOException e) {
			MainActivity.instance.showToast("连接出错");
			return;
		}
		if (response.statusCode() != 200) {
			MainActivity.instance.showToast(String.valueOf(response.statusCode()));
		}
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(response.body()).getAsJsonObject();
		MainActivity.instance.showToast(obj.get("message").getAsString());
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
