package com.meng.biliv3.fragment;

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
import com.meng.biliv3.adapters.*;
import com.meng.biliv3.enums.*;
import com.meng.biliv3.javabean.*;
import com.meng.biliv3.libs.*;
import com.meng.biliv3.result.*;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class MedalFragment extends BaseIdFragment {

	private ListView selected;
	private Button refresh;
	private MedalsAdapter medalsAdapter;
	private Medals medals;

	public MedalFragment(IDType type, long id) {
		super(type, id);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MainActivity.instance.renameFragment(IDType.Medal.toString() + id, MainActivity.instance.getAccount(id).name + "的头衔");
		RelativeLayout rl=(RelativeLayout) inflater.inflate(R.layout.account_manager, null);
		selected = (ListView) rl.findViewById(R.id.account_managerListView);
		refresh = (Button) rl.findViewById(R.id.account_managerButton);
		refresh.setText("刷新");
		refresh.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1) {
					MainActivity.instance.threadPool.execute(new Runnable(){

							@Override
							public void run() {
								medals.data.fansMedalList.clear();
								medals.data.fansMedalList.addAll(getAllMedals().data.fansMedalList);
								refreshAdapter(medalsAdapter);
								//MainActivity.instance.showToast("刷新");
							}
						});
				}
			});
		refresh.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1) {
					final AccountInfo ai = MainActivity.instance.getAccount(id);
					String items[] = new String[medals.data.fansMedalList.size()];
					for (int i=0;i < items.length;++i) {
						items[i] = medals.data.fansMedalList.get(i).medal_name;
					}
					final boolean checkedItems[] = new boolean[items.length];
					new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle("选择要送满的头衔").setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
								checkedItems[which] = isChecked;
							}
						}).setNegativeButton("取消", null).setPositiveButton("确定送满", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								MainActivity.instance.threadPool.execute(new Runnable() {
										@Override
										public void run() {
											StringBuilder sb = new StringBuilder();
											for (int i = 0; i < checkedItems.length; i++) {
												if (checkedItems[i]) {
													int send=0;
													Medals.FansMedal mfm = medals.data.fansMedalList.get(i);
													int need = mfm.day_limit - mfm.today_feed;
													if (need == 0) {
														sb.append("赠送").append(mfm.medal_name).append(send).append("辣条\n");
														continue;
													}
													GiftBag liveBag = Tools.BilibiliTool.getGiftBag(ai.cookie);
													if (need > liveBag.getStripCount()) {
														MainActivity.instance.showToast("辣条不足");	
														continue;
													}
													for (GiftBag.ListItem gli:liveBag.data.list) {
														if (gli.gift_name.equals("辣条")) {
															if (need > gli.gift_num) {
																sendHotStrip(ai.uid, mfm.target_id, id, gli.gift_num, ai.cookie, gli);
																need -= gli.gift_num;
																send += gli.gift_num;
																gli.gift_num = 0;
															} else {
																sendHotStrip(ai.uid, mfm.target_id, id, need, ai.cookie, gli);
																send += need;
																gli.gift_num -= need;
																break;	
															}
														}
													}
													if (liveBag.getStripCount() == 0) {
														MainActivity.instance.showToast("已送出全部辣条🎁");
													}
													sb.append("赠送").append(mfm.medal_name).append(send).append("辣条\n");
													try {
														Thread.sleep(100);
													} catch (InterruptedException e) {}
												}
											}
											MainActivity.instance.showToast(sb.toString());
										}
									});
							}
						}).show();
					return true;
				}
			});
		return rl;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					medals = getAllMedals();
					MainActivity.instance.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								selected.setAdapter(medalsAdapter = new MedalsAdapter(medals));
							}
						});
				}
			});

		selected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(final AdapterView<?> parent, View view, final int position, long itemId) {
					final AccountInfo ai = MainActivity.instance.getAccount(id);
					new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle("选择辣条(" + ai.name + ")")
						.setNegativeButton("包裹", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2) {
								MainActivity.instance.threadPool.execute(new Runnable(){

										@Override
										public void run() {
											sendPackDialog(ai, medalsAdapter.getItem(position).target_id);
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
																	MainActivity.instance.showToast(new JsonParser().parse(Tools.BilibiliTool.sendHotStrip(ai.uid, medalsAdapter.getItem(position).target_id, id, Integer.parseInt(editText.getText().toString()), ai.cookie)).getAsJsonObject().get("message").getAsString());
																	medals.data.fansMedalList.clear();
																	medals.data.fansMedalList.addAll(getAllMedals().data.fansMedalList);
																	refreshAdapter(medalsAdapter);
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
		selected.setOnItemLongClickListener(new OnItemLongClickListener(){

				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4) {
					new AlertDialog.Builder(getActivity()).setTitle("确定送满吗").setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface p1, int p2) {
								MainActivity.instance.threadPool.execute(new Runnable() {
										@Override
										public void run() {
											AccountInfo ai = MainActivity.instance.getAccount(id);
											int send = 0;
											Medals.FansMedal mfm = medals.data.fansMedalList.get(p3);
											int need = mfm.day_limit - mfm.today_feed;
											if (need == 0) {
												MainActivity.instance.showToast("今日亲密度已满");
												return;
											}
											GiftBag liveBag = Tools.BilibiliTool.getGiftBag(ai.cookie);
											if (need > liveBag.getStripCount()) {
												MainActivity.instance.showToast("辣条不足");	
												return;
											}
											for (GiftBag.ListItem gli:liveBag.data.list) {
												if (gli.gift_name.equals("辣条")) {
													if (need > gli.gift_num) {
														sendHotStrip(ai.uid, mfm.target_id, id, gli.gift_num, ai.cookie, gli);
														need -= gli.gift_num;
														send += gli.gift_num;
														gli.gift_num = 0;
													} else {
														sendHotStrip(ai.uid, mfm.target_id, id, need, ai.cookie, gli);
														send += need;
														gli.gift_num -= need;
														break;	
													}
												}
											}
											if (liveBag.getStripCount() == 0) {
												MainActivity.instance.showToast("已送出全部辣条🎁");
											}
											MainActivity.instance.showToast(String.format("赠送%s%d辣条", mfm.medal_name, send));
										}
									});
							}
						}).setNegativeButton("取消", null).show();
					return true;
				}
			});
	}

	private Medals getAllMedals() {
		String cookie = MainActivity.instance.getAccount(id).cookie;
		Medals mb = Tools.BilibiliTool.getMedal(cookie);
		/*for (int i=mb.data.pageinfo.curPage;i < mb.data.pageinfo.totalpages;++i) {
		 Medals tm=Tools.BilibiliTool.getMedal(cookie, i, 10);
		 mb.data.fansMedalList.addAll(tm.data.fansMedalList);
		 }*/
		return mb;
	}
}
