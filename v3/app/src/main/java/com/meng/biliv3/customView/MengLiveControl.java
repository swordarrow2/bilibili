package com.meng.biliv3.customView;

import android.app.*;
import android.content.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.ExpandableListView.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.adapters.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libs.*;

import android.view.View.OnClickListener;

public class MengLiveControl extends LinearLayout {

    private Button btnStart;
    private EditText newName;
    private LinearLayout ll;
    private TextView m1;
    private TextView m2;
    private String partID = null;
	private ExpandableListView mainlistview = null;
	private LivePartSelectAdapter adapter;
	private AlertDialog dialog;
	private UidToLiveRoom sjb;

    public MengLiveControl(final Context context, UidToLiveRoom utlr) {
        super(context);
		sjb = utlr;
        LayoutInflater.from(context).inflate(R.layout.meng_live_control, this);
		mainlistview = new ExpandableListView(context);
		mainlistview.setOnChildClickListener(new OnChildClickListener(){

				@Override
				public boolean onChildClick(ExpandableListView p1, View p2, int p3, int p4, long p5) {
					partID = ((LivePart.GroupData.PartData)adapter.getChild(p3, p4)).id;
					MainActivity.instance.showToast("选择分区:" + ((LivePart.GroupData.PartData)adapter.getChild(p3, p4)).name);
					return false;
				}
			});
		dialog = new AlertDialog.Builder(context).setView(mainlistview).setTitle("选择分区").setPositiveButton("确定",
			new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2) {
					MainActivity.instance.threadPool.execute(new Runnable(){

							@Override
							public void run() {
								long mainUID = MainActivity.instance.sjfSettings.getMainAccount();
								String cookie = MainActivity.instance.getCookie(mainUID);
								String streamJson = Tools.Network.httpGet("https://api.live.bilibili.com/live_stream/v1/StreamList/get_stream_by_roomId?room_id=" + sjb.data.roomid, cookie, "https://link.bilibili.com/p/center/index");
								JsonParser parser = new JsonParser();
								JsonObject rtmp = parser.parse(streamJson).getAsJsonObject().get("data").getAsJsonObject().get("rtmp").getAsJsonObject();
								Tools.BilibiliTool.startLive(sjb.data.roomid, partID, cookie);
								m1 = new TextView(context);
								m2 = new TextView(context);
								m1.setText(rtmp.get("addr").getAsString());
								m2.setText(rtmp.get("code").getAsString());
								m1.setTextAppearance(android.R.style.TextAppearance_Medium);
								m2.setTextAppearance(android.R.style.TextAppearance_Medium);
								m1.setOnClickListener(onclick);
								m2.setOnClickListener(onclick);
								((Activity) context).runOnUiThread(new Runnable() {
										@Override
										public void run() {
											newName.setHint("房间名:" + sjb.data.title);
											ll.addView(m1);
											ll.addView(m2);
											btnStart.setText("关闭直播");
										}
									});
							}
						});
				}
			}).setNegativeButton("返回", null).create();
        btnStart = (Button) findViewById(R.id.btn_start);
        final Button btnRename = (Button) findViewById(R.id.btn_rename);
        newName = (EditText) findViewById(R.id.et_new_name);
		newName.setTextAppearance(android.R.style.TextAppearance_Medium);
        ll = (LinearLayout) findViewById(R.id.linearlayout);
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
		MainActivity.instance.threadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						final LivePart livePartList = new Gson().fromJson(Tools.Network.httpGet("https://api.live.bilibili.com/room/v1/Area/getList"), LivePart.class);
						((Activity) context).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									adapter = new LivePartSelectAdapter(livePartList);
									mainlistview.setAdapter(adapter);
								}
							});         
					} catch (Exception e) {
						MainActivity.instance.showToast(e.toString());
					}
					long mainUID = MainActivity.instance.sjfSettings.getMainAccount();
					if (mainUID != -1) {
						final UidToLiveRoom sjb = new Gson().fromJson(Tools.Network.httpGet("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UidToLiveRoom.class);
						String streamJson = Tools.Network.httpGet("https://api.live.bilibili.com/live_stream/v1/StreamList/get_stream_by_roomId?room_id=" + sjb.data.roomid, MainActivity.instance.getCookie(mainUID), "https://link.bilibili.com/p/center/index");
						JsonParser parser = new JsonParser();
						final JsonObject rtmp = parser.parse(streamJson).getAsJsonObject().get("data").getAsJsonObject().get("rtmp").getAsJsonObject();				
						((Activity) context).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (sjb.data.liveStatus == 0) {
										btnStart.setText("开始直播");
									} else {
										m1 = new TextView(context);
										m1.setText(rtmp.get("addr").getAsString());
										m2 = new TextView(context);
										m2.setText(rtmp.get("code").getAsString());
										m1.setOnClickListener(onclick);
										m2.setOnClickListener(onclick);
										m1.setTextAppearance(android.R.style.TextAppearance_Medium);
										m2.setTextAppearance(android.R.style.TextAppearance_Medium);
										ll.addView(m1);
										ll.addView(m2);
										newName.setHint("房间名:" + sjb.data.title);
										btnStart.setText("关闭直播");                                                     
									}
								}
							});
					}
				}
			});

        btnStart.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (btnStart.getText().toString().equals("开始直播")) {
						dialog.show();
					} else {
						MainActivity.instance.threadPool.execute(new Runnable(){

								@Override
								public void run() {
									long mainUID = MainActivity.instance.sjfSettings.getMainAccount();
									String cookie = MainActivity.instance.getCookie(mainUID);
									UidToLiveRoom sjb = new Gson().fromJson(Tools.Network.httpGet("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UidToLiveRoom.class);
									Tools.BilibiliTool.stopLive(sjb.data.roomid, cookie);
									((Activity) context).runOnUiThread(new Runnable() {
											@Override
											public void run() {
												ll.removeView(m1);
												ll.removeView(m2);
												btnStart.setText("开始直播");
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
								long mainUID = MainActivity.instance.sjfSettings.getMainAccount();
								String cookie = MainActivity.instance.getCookie(mainUID);
								UidToLiveRoom sjb = new Gson().fromJson(Tools.Network.httpGet("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UidToLiveRoom.class);
								Tools.BilibiliTool.renameLive(sjb.data.roomid, name, cookie);
							}
						});
				}
			});
    }

	OnClickListener onclick=new OnClickListener(){

		@Override
		public void onClick(View p1) {
			Tools.AndroidContent.copyToClipboard(((TextView) p1).getText().toString());
		}
	};
}
