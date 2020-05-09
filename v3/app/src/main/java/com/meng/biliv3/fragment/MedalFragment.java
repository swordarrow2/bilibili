package com.meng.biliv3.fragment;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.adapters.*;
import com.meng.biliv3.javabean.*;
import com.meng.biliv3.libs.*;
import com.meng.biliv3.result.*;

public class MedalFragment extends BaseIdFragment {

	private ListView selected;
	private MedalsAdapter medalsAdapter;
	public MedalFragment(String type, long uid) {
		this.type = type;
		id = uid;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		selected = new ListView(getActivity());
		MainActivity.instance.renameFragment(typeMedal + id, MainActivity.instance.getAccount(id).name + "的头衔");
		return selected;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					final Medals mds=Tools.BilibiliTool.getMedal(MainActivity.instance.loginAccounts.get(0).cookie, 1, 20);
					MainActivity.instance.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								selected.setAdapter(medalsAdapter = new MedalsAdapter(mds));
							}
						});
				}
			});

		selected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(final AdapterView<?> parent, View view, final int position, long itemId) {
					final AccountInfo ai=MainActivity.instance.getAccount(id);
					new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle("选择辣条(" + ai.name + ")")
						.setNegativeButton("包裹", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2) {
								MainActivity.instance.threadPool.execute(new Runnable(){

										@Override
										public void run() {

											final GiftBag liveBag = GSON.fromJson(Tools.Network.httpGet("https://api.live.bilibili.com/xlive/web-room/v1/gift/bag_list?t=" + System.currentTimeMillis(), ai.cookie), GiftBag.class);
											getActivity().runOnUiThread(new Runnable() {
													@Override
													public void run() {
														ListView listView=new ListView(getActivity());
														new AlertDialog.Builder(getActivity()).setView(listView).setTitle("选择(" + ai.name + ")").show();
														final GiftAdapter giftAdapter = new GiftAdapter(getActivity(), liveBag.data.list);
														listView.setAdapter(giftAdapter);
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
																									Medals.FansMedal mfm=(Medals.FansMedal) medalsAdapter.getItem(position);
																									if (num > i.gift_num) {
																										sendHotStrip(ai.uid, mfm.target_id, id, i.gift_num, ai.cookie, i);
																										num -= i.gift_num;
																										i.gift_num = 0;
																									} else {
																										sendHotStrip(ai.uid, mfm.target_id, id, num, ai.cookie, i);											
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
																							notifyDataSetChange();
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
																				sendHotStrip(ai.uid, ((Medals.FansMedal) medalsAdapter.getItem(position)).target_id, id, liveBag.data.list.get(p3).gift_num, ai.cookie, liveBag.data.list.get(p3));
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
																				notifyDataSetChange();
																			}
																		});
																	return true;
																}
															});
													}
												});
										}
									});
							}
						})
						.setPositiveButton("瓜子", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
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
																	MainActivity.instance.showToast(new JsonParser().parse(Tools.BilibiliTool.sendHotStrip(ai.uid, ((Medals.FansMedal)medalsAdapter.getItem(position)).target_id, id, Integer.parseInt(content), ai.cookie)).getAsJsonObject().get("message").getAsString());
																	notifyDataSetChange();
																}
															});
													}
												}).show();
										}
									});
							}
						}).show();
				}
			});
	}
	
	private void notifyDataSetChange() {
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					final Medals m=Tools.BilibiliTool.getMedal(MainActivity.instance.getAccount(id).cookie, 1, 20);
					MainActivity.instance.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								medalsAdapter.notifyDataSetChanged(m);
							}
						});
				}
			});
	}
}
