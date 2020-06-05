package com.meng.sjfmd.customView;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import android.widget.ExpandableListView.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.sjfmd.adapters.*;
import com.meng.sjfmd.customView.*;
import com.meng.sjfmd.enums.*;
import com.meng.sjfmd.javabean.*;
import com.meng.sjfmd.libs.*;
import com.meng.sjfmd.result.*;
import java.io.*;
import android.support.design.widget.*;


public class UserInfoHeaderView extends LinearLayout implements View.OnClickListener {

	private ImageView ivHead;
    private TextView tvName;
    private TextView tvBMain;
	private TextView tvBLive;
	private TextInputLayout til;
	
	private Button btnStart;
	private Button btnCopy;
	private Button btnRename;
    private EditText newName;
    private ExpandableListView mainlistview = null;
	private LivePartSelectAdapter adapter;
	private AlertDialog dialog;
	private UidToRoom utr;
	private StartLive liveStart;
	private LiveStream liveStream;

//	public UserInfoHeaderView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		initView(context);
//	}
//
//	public UserInfoHeaderView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        initView(context);
//    }

    public UserInfoHeaderView(Context context) {
        super(context);
        initView(context);
    }

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.main_account_list_header, this);
		til = (TextInputLayout) findViewById(R.id.main_account_list_header_TextInputLayout);
		ivHead = (ImageView) findViewById(R.id.main_account_list_headerImageView_header);
        tvName = (TextView) findViewById(R.id.main_account_list_headerTextView_name);
        tvBMain = (TextView) findViewById(R.id.main_account_list_headerTextView_bmain);
		tvBLive = (TextView) findViewById(R.id.main_account_list_headerTextView_blive);
		MainActivity.instance.colorManager.addView(findViewById(R.id.main_account_list_headerLinearLayout_main),ColorType.DrawerHeader);
		btnStart = (Button) findViewById(R.id.main_account_list_headerButton_start);
		btnRename = (Button) findViewById(R.id.main_account_list_headerButton_rename);
		newName = (EditText) findViewById(R.id.main_account_list_headerEditText_new_name);
		btnCopy = (Button) findViewById(R.id.main_account_list_headerButton_copy);
		long mainUID = MainActivity.instance.sjfSettings.getMainAccount();
		if (mainUID == -1) {
			tvName.setVisibility(View.VISIBLE);
			tvName.setText("点击SJF设置主账号");
			ivHead.setImageResource(R.drawable.ic_launcher);
		} else {
			getInfo(MainActivity.instance.getAccount(mainUID));
        }
		ivHead.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1) {
					String items[] = new String[MainActivity.instance.loginAccounts.size()];
					final int[] wi=new int[1];
					for (int i=0,j=MainActivity.instance.loginAccounts.size();i < j;++i) {
						items[i] = MainActivity.instance.loginAccounts.get(i).name;
					}
					new AlertDialog.Builder(MainActivity.instance).setIcon(R.drawable.ic_launcher).setTitle("选择账号").setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								wi[0] = which;
							}
						}).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								AccountInfo ai=MainActivity.instance.loginAccounts.get(wi[0]);
								if (ai == null) {
									return;
								}
								MainActivity.instance.sjfSettings.setMainAccount(ai.uid);
								getInfo(ai);
							}
						}).show();
				}
			});
		initCtrl();
	}

	private void initCtrl() {
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					long mainUid = MainActivity.instance.sjfSettings.getMainAccount();
					if (mainUid == -1) {
						return;
					}
					utr = Bilibili.getUidToRoom(mainUid);
					liveStream = Bilibili.getLiveStream(utr.data.roomid, MainActivity.instance.getCookie(MainActivity.instance.sjfSettings.getMainAccount()));
					final LivePart livePartList = GSON.fromJson(AndroidContent.readAssetsString("livePart.json"), LivePart.class);
					if (utr == null | liveStream == null || livePartList == null) {
						MainActivity.instance.showToast("直播间连接失败");
						return;
					}
					MainActivity.instance.runOnUiThread(new Runnable(){

							@Override
							public void run() {
										if (liveStream != null) {
									btnCopy.setEnabled(true);
									btnCopy.setText("复制推流码");
								}
								til.setHint("房间名:" + utr.data.title);
								mainlistview = new ExpandableListView(MainActivity.instance);
								mainlistview.setOnChildClickListener(new OnChildClickListener(){

										@Override
										public boolean onChildClick(ExpandableListView p1, View p2, int p3, int p4, long p5) {
											final LivePart.PartData child = adapter.getChild(p3, p4);
											new AlertDialog.Builder(MainActivity.instance).setIcon(R.drawable.ic_launcher).setTitle("确定在" + child.name + "开播吗").setPositiveButton("确定", new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface p1, int p2) {
														MainActivity.instance.threadPool.execute(new Runnable(){

																@Override
																public void run() {
																	liveStart = Bilibili.startLive(utr.data.roomid, child.id, MainActivity.instance.getCookie(MainActivity.instance.sjfSettings.getMainAccount()));
																	if (liveStart.code != 0) {
																		return;
																	}
																	MainActivity.instance.showToast("开播成功");
																	MainActivity.instance.runOnUiThread(new Runnable() {
																			@Override
																			public void run() {
																				btnCopy.setEnabled(true);
																				btnCopy.setText("复制推流码");
																				newName.setHint("房间名:" + utr.data.title);
																				btnStart.setText("关闭直播间");
																			}
																		});
																}
															});
													}
												}).setNegativeButton("取消", null).show();
											dialog.dismiss();
											return true;
										}
									});
								dialog = new AlertDialog.Builder(MainActivity.instance).setView(mainlistview).setTitle("选择分区")
									.setPositiveButton("其它单机", new DialogInterface.OnClickListener(){

										@Override
										public void onClick(DialogInterface p1, int p2) {
											MainActivity.instance.threadPool.execute(new Runnable(){

													@Override
													public void run() {
														liveStart = Bilibili.startLive(utr.data.roomid, "235", MainActivity.instance.getCookie(MainActivity.instance.sjfSettings.getMainAccount()));
														if (liveStart.code != 0) {
															return;
														}
														MainActivity.instance.showToast("开播成功");
														MainActivity.instance.runOnUiThread(new Runnable() {
																@Override
																public void run() {
																	btnCopy.setEnabled(true);
																	btnCopy.setText("复制推流码");
																	til.setHint("房间名:" + utr.data.title);
																	btnStart.setText("关闭直播间");
																}
															});
													}
												});
										}
									}).create();
								btnStart.setOnClickListener(UserInfoHeaderView.this);
								btnRename.setOnClickListener(UserInfoHeaderView.this);
								btnCopy.setOnClickListener(UserInfoHeaderView.this);
								newName.setTextAppearance(android.R.style.TextAppearance_Medium);
								newName.addTextChangedListener(new TextWatcher() {
										@Override
										public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

										}

										@Override
										public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

										}

										@Override
										public void afterTextChanged(Editable editable) {
											btnRename.setVisibility(editable.toString().equals("") ? GONE : VISIBLE);
										}
									});

								if (livePartList == null) {
									MainActivity.instance.showToast("获取直播分区列表失败");
									return;
								}
								mainlistview.setAdapter(adapter = new LivePartSelectAdapter(livePartList));
								if (MainActivity.instance.sjfSettings.getMainAccount() != -1) {
									if (utr.data.liveStatus == 0) {
										btnStart.setText("打开直播间");
									} else {
										til.setHint("房间名:" + utr.data.title);
										btnStart.setText("关闭直播间");                                                     
									}
								}
							}
						});
				}
			});
	}

	private void getInfo(final AccountInfo ai) {
		File imf = new File(MainActivity.instance.mainDic + "bilibili/" + ai.uid + ".jpg");
		if (imf.exists()) {
			ivHead.setImageBitmap(BitmapFactory.decodeFile(imf.getAbsolutePath()));
		} else {
			MainActivity.instance.threadPool.execute(new DownloadImageRunnable(ivHead, ai.uid, DownloadImageRunnable.BilibiliUser));
		}
		MainActivity.instance.threadPool.execute(new Runnable() {
				@Override
				public void run() {
					final UserInfo info = Bilibili.getUserInfo(ai.uid);
					if (info.code != 0) {
						MainActivity.instance.showToast("cookie过期");
						return;
					}
					final UidToRoom sjb = Bilibili.getUidToRoom(info.data.mid);
					MainActivity.instance.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								tvName.setText(info.data.name);
								tvBMain.setText("主站 Lv." + info.data.level);
							}
						});

					final RoomToUid rtu=Bilibili.getRoomToUid(sjb.data.roomid);
					MainActivity.instance.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (rtu != null) {
									tvBLive.setText("主播 Lv." + rtu.data.level.master_level.level + "(" + rtu.data.level.master_level.anchor_score + "/" + rtu.data.level.master_level.next.get(1) + ")");
									btnStart.setEnabled(true);
									btnCopy.setEnabled(true);
									newName.setVisibility(View.VISIBLE);
									initCtrl();
								} else {
									tvBLive.setText("未开通直播间");
									btnStart.setEnabled(false);
									btnCopy.setEnabled(false);
									newName.setVisibility(View.GONE);
								}
							}
						});
				}
			});
	}
	@Override
	public void onClick(View p1) {
		switch (p1.getId()) {
			case R.id.main_account_list_headerButton_start:
				if (btnStart.getText().toString().equals("打开直播间")) {
					dialog.show();
				} else {
					new AlertDialog.Builder(MainActivity.instance).setTitle("确定关播吗").setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface p1, int p2) {
								MainActivity.instance.threadPool.execute(new Runnable(){

										@Override
										public void run() {
											if (Bilibili.stopLive(utr.data.roomid, MainActivity.instance.getCookie(MainActivity.instance.sjfSettings.getMainAccount())).code != 0) {
												return;
											}
											MainActivity.instance.showToast("关播成功");
											MainActivity.instance.runOnUiThread(new Runnable() {
													@Override
													public void run() {
														btnStart.setText("打开直播间");
													}
												});
										}
									});
							}
						}).setNegativeButton("取消", null).show();
				}	
				break;
			case R.id.main_account_list_headerButton_rename:
				p1.setVisibility(View.GONE);
				final String name = newName.getText().toString();
				if (name.equals("")) {
					MainActivity.instance.showToast("房间名不能为空");
					return;
				}
				MainActivity.instance.threadPool.execute(new Runnable() {
						@Override
						public void run() {
							Bilibili.renameLive(utr.data.roomid, name, MainActivity.instance.getCookie(MainActivity.instance.sjfSettings.getMainAccount()));
						}
					});
				break;
			case R.id.main_account_list_headerButton_copy:
				if (liveStart != null) {
					AndroidContent.copyToClipboard("服务器:\n" + liveStart.data.rtmp.addr + "\n推流码:\n" + liveStart.data.rtmp.code);
					MainActivity.instance.showToast("已复制推流码到剪贴板");
				} else if (liveStream != null) {
					AndroidContent.copyToClipboard("服务器:\n" + liveStream.data.rtmp.addr + "\n推流码:\n" + liveStream.data.rtmp.code);
					MainActivity.instance.showToast("已复制推流码到剪贴板");
				} else {
					MainActivity.instance.showToast("未知错误");
				}
				break;
		}
	}
}
