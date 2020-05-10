package com.meng.biliv3.customView;

import android.app.*;
import android.content.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import android.widget.ExpandableListView.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.adapters.*;
import com.meng.biliv3.libs.*;
import com.meng.biliv3.result.*;

public class MengLiveControl extends LinearLayout {

    private Button btnStart;
	private Button btnCopy;
	private Button btnRename;
    private EditText newName;
    private String partID = null;
	private ExpandableListView mainlistview = null;
	private LivePartSelectAdapter adapter;
	private AlertDialog dialog;
	private UidToRoom utr;
	private StartLive liveStart;
	private LiveStream liveStream;

    public MengLiveControl(final Context context) {
        super(context);
		LayoutInflater.from(context).inflate(R.layout.meng_live_control, this);

		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					utr = Tools.BilibiliTool.getRoomByUid(MainActivity.instance.sjfSettings.getMainAccount());
					liveStream = Tools.BilibiliTool.getLiveStream(utr.data.roomid, MainActivity.instance.getCookie(MainActivity.instance.sjfSettings.getMainAccount()));
					final LivePart livePartList = Tools.BilibiliTool.getLivePart();
					if (utr == null | liveStream == null || livePartList == null) {
						MainActivity.instance.showToast("直播间连接失败");
						return;
					}
					MainActivity.instance.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								btnStart = (Button) findViewById(R.id.meng_live_controlButton_start);
								btnRename = (Button) findViewById(R.id.meng_live_controlButton_rename);
								newName = (EditText) findViewById(R.id.meng_live_controlEditText_new_name);
								btnCopy = (Button) findViewById(R.id.meng_live_controlButton_copy);
								if (liveStream != null) {
									btnCopy.setEnabled(true);
									btnCopy.setText("复制推流码");
								}
								mainlistview = new ExpandableListView(context);
								mainlistview.setOnChildClickListener(new OnChildClickListener(){

										@Override
										public boolean onChildClick(ExpandableListView p1, View p2, int p3, int p4, long p5) {
											partID = ((LivePart.GroupData.PartData)adapter.getChild(p3, p4)).id;
											MainActivity.instance.showToast("选择分区:" + ((LivePart.GroupData.PartData)adapter.getChild(p3, p4)).name);
											MainActivity.instance.threadPool.execute(new Runnable(){

													@Override
													public void run() {
														long mainUID = MainActivity.instance.sjfSettings.getMainAccount();
														String cookie = MainActivity.instance.getCookie(mainUID);
														liveStart = Tools.BilibiliTool.startLive(utr.data.roomid, partID, cookie);
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
											dialog.dismiss();
											return true;
										}
									});
								btnCopy.setOnClickListener(new OnClickListener(){

										@Override
										public void onClick(View p1) {
											if (liveStart != null) {
												Tools.AndroidContent.copyToClipboard("服务器:\n" + liveStart.data.rtmp.addr + "\n推流码:\n" + liveStart.data.rtmp.code);
												MainActivity.instance.showToast("已复制推流码到剪贴板");
											} else if (liveStream != null) {
												Tools.AndroidContent.copyToClipboard("服务器:\n" + liveStream.data.rtmp.addr + "\n推流码:\n" + liveStream.data.rtmp.code);
												MainActivity.instance.showToast("已复制推流码到剪贴板");
											} else {
												MainActivity.instance.showToast("未知错误");
											}	
										}
									});
								dialog = new AlertDialog.Builder(context).setView(mainlistview).setTitle("选择分区").setPositiveButton("确定", null).setNegativeButton("返回", null).create();
								btnStart.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											if (btnStart.getText().toString().equals("打开直播间")) {
												dialog.show();
											} else {
												MainActivity.instance.threadPool.execute(new Runnable(){

														@Override
														public void run() {
															long mainUID = MainActivity.instance.sjfSettings.getMainAccount();
															Tools.BilibiliTool.stopLive(utr.data.roomid, MainActivity.instance.getCookie(mainUID));
															MainActivity.instance.runOnUiThread(new Runnable() {
																	@Override
																	public void run() {
																		btnStart.setText("打开直播间");
																	}
																});
														}
													});
											}	
										}
									});
								btnRename.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View view) {
											view.setVisibility(View.GONE);
											final String name = newName.getText().toString();
											if (name.equals("")) {
												MainActivity.instance.showToast("房间名不能为空");
												return;
											}
											MainActivity.instance.threadPool.execute(new Runnable() {
													@Override
													public void run() {
														Tools.BilibiliTool.renameLive(utr.data.roomid, name, MainActivity.instance.getCookie(MainActivity.instance.sjfSettings.getMainAccount()));
													}
												});
										}
									});
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
								adapter = new LivePartSelectAdapter(livePartList);
								mainlistview.setAdapter(adapter);       
								long mainUID = MainActivity.instance.sjfSettings.getMainAccount();
								if (mainUID != -1) {
									if (utr.data.liveStatus == 0) {
										btnStart.setText("打开直播间");
									} else {
										newName.setHint("房间名:" + utr.data.title);
										btnStart.setText("关闭直播间");                                                     
									}
								}
							}
						});
				}
			});
    }
}
