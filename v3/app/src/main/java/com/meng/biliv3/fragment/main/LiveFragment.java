package com.meng.biliv3.fragment.main;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
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

import android.view.View.OnClickListener;

public class LiveFragment extends Fragment {

	private Uri uri;
	private VideoView videoView;
	private Button send,editPre,preset,silver,pack,sign;
	private EditText et;
	private TextView info;
	private Spinner selectAccount;
	private int id;

	private CustomSentence customSentence;
	private File customSentenseFile;

	private static ArrayAdapter<String> sencencesAdapter=null;
	private static ArrayList<String> spList=null;
	public LiveFragment(int liveId) {
		id = liveId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.live_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		customSentenseFile = new File(Environment.getExternalStorageDirectory() + "/sjf.json");
		if (customSentenseFile.exists()) {
			customSentence = new Gson().fromJson(Tools.FileTool.readString(customSentenseFile), CustomSentence.class);
		} else {
			customSentence = new CustomSentence();
			String[] strings = new String[]{ "此生无悔入东方,来世愿生幻想乡","红魔地灵夜神雪,永夜风神星莲船","非想天则文花贴,萃梦神灵绯想天","冥界地狱异变起,樱下华胥主谋现","净罪无改渡黄泉,华鸟风月是非辨","境界颠覆入迷途,幻想花开啸风弄","二色花蝶双生缘,前缘未尽今生还","星屑洒落雨霖铃,虹彩彗光银尘耀","无寿迷蝶彼岸归,幻真如画妖如月","永劫夜宵哀伤起,幼社灵中幻似梦","追忆往昔巫女缘,须弥之间冥梦现","仁榀华诞井中天,歌雅风颂心无念" };
			customSentence.sent.addAll(Arrays.asList(strings));
			saveConfig();
		}
		send = (Button) view.findViewById(R.id.live_fragmentButton_send);
		silver = (Button) view.findViewById(R.id.live_fragmentButton_silver);
		pack = (Button) view.findViewById(R.id.live_fragmentButton_pack);
		sign = (Button) view.findViewById(R.id.livefragmentButton_sign);
		//editPre = (Button) view.findViewById(R.id.live_fragmentButton_edit_pre);
		preset = (Button) view.findViewById(R.id.live_fragmentButton_preset);
		et = (EditText) view.findViewById(R.id.live_fragmentEditText_danmaku);
		videoView = (VideoView) view.findViewById(R.id.live_fragmentVideoView);  
		info = (TextView) view.findViewById(R.id.live_fragmentTextView_info);
		selectAccount = (Spinner) view.findViewById(R.id.live_fragmentSpinner);

		videoView.setMediaController(new MediaController(getActivity()));
		if (sencencesAdapter == null) {
			sencencesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, customSentence.sent);
		}
		preset.setOnClickListener(onclick);
		send.setOnClickListener(onclick);
		silver.setOnClickListener(onclick);
		pack.setOnClickListener(onclick);
		sign.setOnClickListener(onclick);
		//editPre.setOnClickListener(onclick);
		if (spList == null) {
			spList = new ArrayList<>();
			spList.add("每次选择");
			spList.add("主账号");
			for (AccountInfo ai:MainActivity.instance.loginAccounts) {
				spList.add(ai.name);
			}
		}
		selectAccount.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spList));
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					JsonParser parser = new JsonParser();
					JsonObject obj = parser.parse(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + id + "&quality=4&platform=web")).getAsJsonObject();
					if (obj.get("code").getAsInt() == 19002003) {
						MainActivity.instance.showToast("不存在的房间");
						return;
					}
					final JsonArray ja = obj.get("data").getAsJsonObject().get("durl").getAsJsonArray();
					getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								uri = Uri.parse(ja.get(0).getAsJsonObject().get("url").getAsString());
								videoView.setVideoURI(uri);  
								//videoView.start();  
								videoView.requestFocus();
							}
						});
					JsonObject liveToMainInfo=null;
					try {
						liveToMainInfo = new JsonParser().parse(Tools.Network.getSourceCode("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=" + id)).getAsJsonObject().get("data").getAsJsonObject().get("info").getAsJsonObject();
					} catch (Exception e) {
						return;
					}
					long uid=liveToMainInfo.get("uid").getAsLong();
					final String uname=liveToMainInfo.get("uname").getAsString();
					final SpaceToLiveJavaBean sjb = new Gson().fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + uid), SpaceToLiveJavaBean.class);
                    if (sjb.data.liveStatus != 1) {
						getActivity().runOnUiThread(new Runnable(){

								@Override
								public void run() {
									info.setText("房间号:" + id + "\n主播:" + uname + "\n未直播");
								}
							});
						return;
					} else {
						getActivity().runOnUiThread(new Runnable(){

								@Override
								public void run() {
									info.setText("房间号:" + id + "\n主播:" + uname + "\n标题:" + sjb.data.title);
								}
							});
					}
					/*	String html = Tools.Network.getSourceCode("https://live.bilibili.com/" + id);
					 String jsonInHtml = html.substring(html.indexOf("{\"roomInitRes\":"), html.lastIndexOf("}") + 1);
					 final JsonObject data = new JsonParser().parse(jsonInHtml).getAsJsonObject().get("baseInfoRes").getAsJsonObject().get("data").getAsJsonObject();
					 getActivity().runOnUiThread(new Runnable(){

					 @Override
					 public void run() {
					 info.setText("房间号:" + id + "\n主播:" + uname + "\n房间标题:" + data.get("title").getAsString() +
					 "\n分区:" + data.get("parent_area_name").getAsString() + "-" + data.get("area_name").getAsString() +
					 "\n标签:" + data.get("tags").getAsString());
					 }
					 });	*/
				}
			});
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

	private OnClickListener onclick=new OnClickListener(){

		@Override
		public void onClick(View p1) {
			switch (p1.getId()) {
				case R.id.live_fragmentButton_preset:
					ListView naiSentenseListview = new ListView(getActivity());
					naiSentenseListview.setAdapter(sencencesAdapter);
					naiSentenseListview.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
								String msg=(String)p1.getAdapter().getItem(p3);
								sendDanmaku(msg);
							}
						});
					new AlertDialog.Builder(getActivity())
						.setView(naiSentenseListview)
						.setTitle("选择预设语句")
						.setNegativeButton("返回", null).show();
					break;
				case R.id.live_fragmentButton_send:
					sendDanmaku(et.getText().toString());
					break;
				case R.id.live_fragmentButton_silver:
					final EditText editText = new EditText(getActivity());
					new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle("输入辣条数").setView(editText).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String content = editText.getText().toString();
								sendHotStrip(Integer.parseInt(content));
							}
						}).show();
					break;
				case R.id.live_fragmentButton_pack:
					sendPackGift();
					break;
				case R.id.livefragmentButton_sign:
					sendSign();
					break;
			}
		}
	};

	private void sendDanmaku(final String msg) {
		final String sel=(String) selectAccount.getSelectedItem();
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
								final AccountInfo ai=MainActivity.instance.loginAccounts.get(i);
								MainActivity.instance.threadPool.execute(new Runnable(){

										@Override
										public void run() {
											Tools.BilibiliTool.sendLiveDanmaku(msg, ai.cookie, id);
										}
									});
							}
						}
					}
				}).show();
		} else if (sel.equals("主账号")) {
			MainActivity.instance.threadPool.execute(new Runnable(){

					@Override
					public void run() {
						Tools.BilibiliTool.sendLiveDanmaku(msg, MainActivity.instance.getAccount(Integer.parseInt(SharedPreferenceHelper.getValue("mainAccount", ""))).cookie, id);
					}
				});
		} else {
			MainActivity.instance.threadPool.execute(new Runnable(){

					@Override
					public void run() {
						Tools.BilibiliTool.sendLiveDanmaku(msg, MainActivity.instance.getAccount(sel).cookie, id);
					}
				});
		}
	}

	private void sendSign() {
		final String sel=(String) selectAccount.getSelectedItem();
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
								final AccountInfo ai=MainActivity.instance.loginAccounts.get(i);
								MainActivity.instance.threadPool.execute(new Runnable(){

										@Override
										public void run() {
											Tools.BilibiliTool.sendLiveSign(ai.cookie);
										}
									});
							}
						}
					}
				}).show();
		} else if (sel.equals("主账号")) {
			MainActivity.instance.threadPool.execute(new Runnable(){

					@Override
					public void run() {
						AccountInfo ai=MainActivity.instance.getAccount(Integer.parseInt(SharedPreferenceHelper.getValue("mainAccount", "")));
						Tools.BilibiliTool.sendLiveSign(ai.cookie);
					}
				});
		} else {
			MainActivity.instance.threadPool.execute(new Runnable(){

					@Override
					public void run() {
						AccountInfo ai=MainActivity.instance.getAccount(sel);
						Tools.BilibiliTool.sendLiveSign(ai.cookie);
					}
				});
		}
	}

	private void sendHotStrip(final int count) {
		final String sel=(String) selectAccount.getSelectedItem();
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
								final AccountInfo ai=MainActivity.instance.loginAccounts.get(i);
								MainActivity.instance.threadPool.execute(new Runnable(){

										@Override
										public void run() {
											JsonObject liveToMainInfo=null;
											try {
												liveToMainInfo = new JsonParser().parse(Tools.Network.getSourceCode("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=" + id)).getAsJsonObject().get("data").getAsJsonObject().get("info").getAsJsonObject();
											} catch (Exception e) {
												return;
											}
											long uid=liveToMainInfo.get("uid").getAsLong();
											Tools.BilibiliTool.sendHotStrip(ai.uid, uid, id, count, ai.cookie);
										}
									});
							}
						}
					}
				}).show();
		} else if (sel.equals("主账号")) {
			MainActivity.instance.threadPool.execute(new Runnable(){

					@Override
					public void run() {
						JsonObject liveToMainInfo=null;
						try {
							liveToMainInfo = new JsonParser().parse(Tools.Network.getSourceCode("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=" + id)).getAsJsonObject().get("data").getAsJsonObject().get("info").getAsJsonObject();
						} catch (Exception e) {
							return;
						}
						long uid=liveToMainInfo.get("uid").getAsLong();
						AccountInfo ai=MainActivity.instance.getAccount(Integer.parseInt(SharedPreferenceHelper.getValue("mainAccount", "")));
						Tools.BilibiliTool.sendHotStrip(ai.uid, uid, id, count, ai.cookie);
					}
				});
		} else {
			MainActivity.instance.threadPool.execute(new Runnable(){

					@Override
					public void run() {
						JsonObject liveToMainInfo=null;
						try {
							liveToMainInfo = new JsonParser().parse(Tools.Network.getSourceCode("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=" + id)).getAsJsonObject().get("data").getAsJsonObject().get("info").getAsJsonObject();
						} catch (Exception e) {
							return;
						}
						long uid=liveToMainInfo.get("uid").getAsLong();
						AccountInfo ai=MainActivity.instance.getAccount(sel);
						Tools.BilibiliTool.sendHotStrip(ai.uid, uid, id, count, ai.cookie);
					}
				});
		}
	}

	private void sendPackGift() {
		final String sel=(String) selectAccount.getSelectedItem();
		if (sel.equals("每次选择")) {
			String items[] = new String[MainActivity.instance.loginAccounts.size()];
			for (int i=0;i < items.length;++i) {
				items[i] = MainActivity.instance.loginAccounts.get(i).name;
			}
			ListView lvAccount = new ListView(getActivity());
			lvAccount.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items));
			lvAccount.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> p1, View p2, final int p3, long p4) {
						MainActivity.instance.threadPool.execute(new Runnable(){

								@Override
								public void run() {
									AccountInfo ai=MainActivity.instance.loginAccounts.get(p3);
									sendPackDialog(ai);
								}
							});
					}
				});
			new AlertDialog.Builder(getActivity()).setView(lvAccount).setTitle("选择账号").setNegativeButton("返回", null).show();
		} else if (sel.equals("主账号")) {
			MainActivity.instance.threadPool.execute(new Runnable(){

					@Override
					public void run() {
						AccountInfo ai=MainActivity.instance.loginAccounts.get(Integer.parseInt(SharedPreferenceHelper.getValue("mainAccount", "")));
						sendPackDialog(ai);
					}
				});
		} else {
			MainActivity.instance.threadPool.execute(new Runnable(){

					@Override
					public void run() {
						AccountInfo ai=MainActivity.instance.getAccount(sel);
						sendPackDialog(ai);
					}
				});
		}
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
					int ii=0;
					for (GiftBag.ListItem i:liveBag.data.list) {
						if (i.gift_name.equals("辣条")) {
							ii += i.gift_num;
						}
					}
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

	public void sendHotStrip(long uid, long ruid, long roomID, int num, String cookie,  GiftBag.ListItem liveBagDataList) throws IOException {
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
}
