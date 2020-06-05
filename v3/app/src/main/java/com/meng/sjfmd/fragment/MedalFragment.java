package com.meng.sjfmd.fragment;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.github.clans.fab.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.sjfmd.adapters.*;
import com.meng.sjfmd.enums.*;
import com.meng.sjfmd.javabean.*;
import com.meng.sjfmd.libs.*;
import com.meng.sjfmd.result.*;

import android.view.View.OnClickListener;

public class MedalFragment extends BaseIdFragment {

	private ListView medalsList;
	private MedalsAdapter medalsAdapter;
	private Medals medals;

	private FloatingActionMenu menuGroup;
	private FloatingActionButton fabRefresh;
    private FloatingActionButton fabAdd;
	private FloatingActionButton fabMulti;

	public MedalFragment(IDType type, long id) {
		super(type, id);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MainActivity.instance.renameFragment(IDType.Medal.toString() + id, MainActivity.instance.getAccount(id).name + "的头衔");
		return inflater.inflate(R.layout.medals_view, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
		medalsList = (ListView) view.findViewById(R.id.medals_view_ListView);
		menuGroup = (FloatingActionMenu) view.findViewById(R.id.medals_view_FloatingActionMenu);
		fabRefresh = (FloatingActionButton) view.findViewById(R.id.medals_view_fab_refresh);
		fabAdd = (FloatingActionButton) view.findViewById(R.id.medals_view_fab_add);
		fabMulti = (FloatingActionButton) view.findViewById(R.id.medals_view_fab_multi);
		fabRefresh.setOnClickListener(onClick);
		fabAdd.setOnClickListener(onClick);
		fabMulti.setOnClickListener(onClick);
		fabAdd.setEnabled(false);
		menuGroup.setClosedOnTouchOutside(true);
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					medals = Bilibili.getMedal(MainActivity.instance.getAccount(id).cookie);
					MainActivity.instance.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								medalsList.setAdapter(medalsAdapter = new MedalsAdapter(medals));
							}
						});
				}
			});

		medalsList.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, final int p3, long p4) {
					final AccountInfo ai = MainActivity.instance.getAccount(id);
					new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle("选择辣条(" + ai.name + ")")
						.setNegativeButton("包裹", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2) {
								MainActivity.instance.threadPool.execute(new Runnable(){

										@Override
										public void run() {
											sendPackDialog(ai, medalsAdapter.getItem(p3).target_id);
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
																	MainActivity.instance.showToast(new JsonParser().parse(Bilibili.sendHotStrip(ai.uid, medalsAdapter.getItem(p3).target_id, id, Integer.parseInt(editText.getText().toString()), ai.cookie)).getAsJsonObject().get("message").getAsString());
																	medals.data.fansMedalList.clear();
																	medals.data.fansMedalList.addAll(Bilibili.getMedal(MainActivity.instance.getAccount(id).cookie).data.fansMedalList);
																	MainActivity.instance.runOnUiThread(new Runnable(){

																			@Override
																			public void run() {
																				medalsAdapter.notifyDataSetChanged();
																			}
																		});
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
		medalsList.setOnItemLongClickListener(new OnItemLongClickListener(){

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
											GiftBag liveBag = Bilibili.getGiftBag(ai.cookie);
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

	OnClickListener onClick=new OnClickListener() {
        @Override
        public void onClick(View v) {
			menuGroup.close(true);
            switch (v.getId()) {
				case R.id.medals_view_fab_multi:
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
											int count=0;
											for (int i = 0; i < checkedItems.length; i++) {
												if (checkedItems[i]) {
													int send=0;
													Medals.FansMedal mfm = medals.data.fansMedalList.get(i);
													int need = mfm.day_limit - mfm.today_feed;
													if (need == 0) {
														sb.append("赠送").append(mfm.medal_name).append(send).append("辣条\n");
														continue;
													}
													GiftBag liveBag = Bilibili.getGiftBag(ai.cookie);
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
													count += send;
													sb.append("赠送").append(mfm.medal_name).append(send).append("辣条\n");
													try {
														Thread.sleep(100);
													} catch (InterruptedException e) {}
												}
											}
											MainActivity.instance.showToast("共送出" + count + "辣条", sb.toString());
										}
									});
							}
						}).show();
					break;
                case R.id.medals_view_fab_add:

					break;
                case R.id.medals_view_fab_refresh:
                    MainActivity.instance.threadPool.execute(new Runnable(){

							@Override
							public void run() {
								medals.data.fansMedalList.clear();
								medals.data.fansMedalList.addAll(Bilibili.getMedal(MainActivity.instance.getAccount(id).cookie).data.fansMedalList);
								MainActivity.instance.runOnUiThread(new Runnable(){

										@Override
										public void run() {
											medalsAdapter.notifyDataSetChanged();
										}
									});
							}
						});
					break;
            }
        }
    };
}
