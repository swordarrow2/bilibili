package com.meng.biliv3.fragment.main;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libAndHelper.*;
import java.io.*;
import java.util.*;
import org.jsoup.*;

import android.view.View.OnClickListener;
import android.graphics.*;

public class AvFragment extends Fragment {


	private Button send,editPre,preset,zan,coin1,coin2,favorite;
	private EditText et;
	private TextView info;
	private Spinner selectAccount;
	private int id;

	private CustomSentence customSentence;
	private File customSentenseFile;

	private static ArrayAdapter<String> sencencesAdapter=null;
	private static ArrayList<String> spList=null;

	private ImageView ivPreview;

	public AvFragment(int liveId) {
		id = liveId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.av_fragment, container, false);
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
		send = (Button) view.findViewById(R.id.av_fragmentButton_send);
		//editPre = (Button) view.findViewById(R.id.live_fragmentButton_edit_pre);
		preset = (Button) view.findViewById(R.id.av_fragmentButton_preset);

		zan = (Button) view.findViewById(R.id.av_fragmentButton_zan);
		coin1 = (Button) view.findViewById(R.id.av_fragmentButton_coin1);
		coin2 = (Button) view.findViewById(R.id.av_fragmentButton_coin2);
		favorite = (Button) view.findViewById(R.id.av_fragmentButton_favorite);	

		et = (EditText) view.findViewById(R.id.av_fragmentEditText_msg);
		ivPreview = (ImageView) view.findViewById(R.id.av_fragmentImageView);  
		info = (TextView) view.findViewById(R.id.av_fragmentTextView_info);
		selectAccount = (Spinner) view.findViewById(R.id.av_fragmentSpinner);
		if (sencencesAdapter == null) {
			sencencesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, customSentence.sent);
		}
		preset.setOnClickListener(onclick);
		zan.setOnClickListener(onclick);
		coin1.setOnClickListener(onclick);
		coin2.setOnClickListener(onclick);
		favorite.setOnClickListener(onclick);
		send.setOnClickListener(onclick);
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
					final VideoInfoBean infoBean=MainActivity.instance.gson.fromJson(Tools.Network.getSourceCode("http://api.bilibili.com/x/web-interface/view?aid=" + id), VideoInfoBean.class);	
					if (infoBean.code != 0) {
						MainActivity.instance.showToast(infoBean.message);
						return;
					}
					getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								info.setText(infoBean.toString());
							}
						});
					try {
						Connection.Response response = Jsoup.connect(infoBean.data.pic).ignoreContentType(true).execute();
						byte[] img = response.bodyAsBytes();
						final Bitmap bmp=BitmapFactory.decodeByteArray(img, 0, img.length);
						getActivity().runOnUiThread(new Runnable(){

								@Override
								public void run() {
									ivPreview.setImageBitmap(bmp);
								}

							});
					} catch (IOException e) {
						throw new RuntimeException(e.toString());
					}
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
		public void onClick(final View p1) {
			if (p1.getId() == R.id.av_fragmentButton_preset) {
				ListView naiSentenseListview = new ListView(getActivity());
				naiSentenseListview.setAdapter(sencencesAdapter);
				naiSentenseListview.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
							String msg=(String)p1.getAdapter().getItem(p3);
							sendMsg(msg);
						}
					});
				new AlertDialog.Builder(getActivity()).setView(naiSentenseListview).setTitle("选择预设语句").setNegativeButton("返回", null).show();
				return;
			}

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
									final AccountInfo ai = MainActivity.instance.loginAccounts.get(i);
									MainActivity.instance.threadPool.execute(new Runnable(){

											@Override
											public void run() {
												switch (p1.getId()) {
													case R.id.av_fragmentButton_send:
														Tools.BilibiliTool.sendVideoJudge(et.getText().toString(), id, ai.cookie);
														break;
													case R.id.av_fragmentButton_zan:
														Tools.BilibiliTool.sendLike(id, ai.cookie);
														break;
													case R.id.av_fragmentButton_coin1:
														Tools.BilibiliTool.sendCoin(1, id, ai.cookie);
														break;
													case R.id.av_fragmentButton_coin2:
														Tools.BilibiliTool.sendCoin(2, id, ai.cookie);
														break;
													case R.id.av_fragmentButton_favorite:
														MainActivity.instance.showToast("未填坑");
														break;
												}
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
							AccountInfo ai = MainActivity.instance.getAccount(Integer.parseInt(SharedPreferenceHelper.getValue("mainAccount", "")));
							switch (p1.getId()) {
								case R.id.av_fragmentButton_send:
									Tools.BilibiliTool.sendVideoJudge(et.getText().toString(), id, ai.cookie);
									break;
								case R.id.av_fragmentButton_zan:
									Tools.BilibiliTool.sendLike(id, ai.cookie);
									break;
								case R.id.av_fragmentButton_coin1:
									Tools.BilibiliTool.sendCoin(1, id, ai.cookie);
									break;
								case R.id.av_fragmentButton_coin2:
									Tools.BilibiliTool.sendCoin(2, id, ai.cookie);
									break;
								case R.id.av_fragmentButton_favorite:
									MainActivity.instance.showToast("未填坑");
									break;
							}
						}
					});
			} else {
				MainActivity.instance.threadPool.execute(new Runnable(){

						@Override
						public void run() {
							AccountInfo ai = MainActivity.instance.getAccount(sel);
							switch (p1.getId()) {
								case R.id.av_fragmentButton_send:
									Tools.BilibiliTool.sendVideoJudge(et.getText().toString(), id, ai.cookie);
									break;
								case R.id.av_fragmentButton_zan:
									Tools.BilibiliTool.sendLike(id, ai.cookie);
									break;
								case R.id.av_fragmentButton_coin1:
									Tools.BilibiliTool.sendCoin(1, id, ai.cookie);
									break;
								case R.id.av_fragmentButton_coin2:
									Tools.BilibiliTool.sendCoin(2, id, ai.cookie);
									break;
								case R.id.av_fragmentButton_favorite:
									MainActivity.instance.showToast("未填坑");
									break;
							}
						}
					});
			}
		}
	};

	private void sendMsg(final String msg) {
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
				})
				.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (int i = 0; i < checkedItems.length; i++) {
							if (checkedItems[i]) {
								final AccountInfo ai=MainActivity.instance.loginAccounts.get(i);
								MainActivity.instance.threadPool.execute(new Runnable(){

										@Override
										public void run() {
											Tools.BilibiliTool.sendVideoJudge(msg, id, ai.cookie);
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
						Tools.BilibiliTool.sendVideoJudge(msg, id, MainActivity.instance.getAccount(Integer.parseInt(SharedPreferenceHelper.getValue("mainAccount", ""))).cookie);
					}
				});
		} else {
			MainActivity.instance.threadPool.execute(new Runnable(){

					@Override
					public void run() {
						Tools.BilibiliTool.sendVideoJudge(msg, id, MainActivity.instance.getAccount(sel).cookie);
					}
				});
		}
	}

//	private void sendFavorite() {
//		final String sel=(String) selectAccount.getSelectedItem();
//		if (sel.equals("每次选择")) {
//			String items[] = new String[MainActivity.instance.loginAccounts.size()];
//			for (int i=0;i < items.length;++i) {
//				items[i] = MainActivity.instance.loginAccounts.get(i).name;
//			}
//			final boolean checkedItems[] = new boolean[items.length];
//			new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle("选择账号").setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//						checkedItems[which] = isChecked;
//					}
//				})
//				.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						for (int i = 0; i < checkedItems.length; i++) {
//							if (checkedItems[i]) {
//								final AccountInfo ai=MainActivity.instance.loginAccounts.get(i);
//								MainActivity.instance.threadPool.execute(new Runnable(){
//
//										@Override
//										public void run() {
//											Tools.BilibiliTool.send(ai.cookie, id);
//										}
//									});
//							}
//						}
//					}
//				}).show();
//		} else if (sel.equals("主账号")) {
//			MainActivity.instance.threadPool.execute(new Runnable(){
//
//					@Override
//					public void run() {
//						Tools.BilibiliTool.sendLike(MainActivity.instance.getAccount(Integer.parseInt(SharedPreferenceHelper.getValue("mainAccount", ""))).cookie, id);
//					}
//				});
//		} else {
//			MainActivity.instance.threadPool.execute(new Runnable(){
//
//					@Override
//					public void run() {
//						Tools.BilibiliTool.sendLike(MainActivity.instance.getAccount(sel).cookie, id);
//					}
//				});
//		}
//	}
}
