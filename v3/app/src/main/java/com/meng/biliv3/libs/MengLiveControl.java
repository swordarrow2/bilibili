package com.meng.biliv3.libs;

import android.app.*;
import android.content.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.adapters.*;
import com.meng.biliv3.javaBean.*;
import java.io.*;
import java.util.*;
import org.jsoup.*;

public class MengLiveControl extends LinearLayout {

    private Button btnStart;
    private EditText newName;
    private LinearLayout ll;
    private TextView m1;
    private TextView m2;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayList<LivePartList.ListItemInListItem> itemInListItem;

    private LivePartList livePartList;

    public MengLiveControl(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.meng_live_control, this);
        btnStart = (Button) findViewById(R.id.btn_start);
        final Button btnRename = (Button) findViewById(R.id.btn_rename);
        newName = (EditText) findViewById(R.id.et_new_name);
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
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.part);
        MainActivity.instance.threadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						livePartList = new Gson().fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Area/getList"), LivePartList.class);
						itemInListItem = livePartList.getPartInfo();
						((Activity) context).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									autoCompleteTextView.setAdapter(new PartListAdapter((Activity) context, itemInListItem));
								}
							});         
					} catch (Exception e) {
						MainActivity.instance.showToast(e.toString());
					}
					final String mainUID = SharedPreferenceHelper.getValue("mainAccount", "");
					if (!mainUID.equals("")) {
						final UserSpaceToLive sjb = new Gson().fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UserSpaceToLive.class);
						String streamJson = Tools.Network.getSourceCode("https://api.live.bilibili.com/live_stream/v1/StreamList/get_stream_by_roomId?room_id=" + sjb.data.roomid, MainActivity.instance.getCookie(Long.parseLong(mainUID)), "https://link.bilibili.com/p/center/index");
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
										ll.addView(m1);
										ll.addView(m2);
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
					MainActivity.instance.threadPool.execute(new Runnable() {
							@Override
							public void run() {
								String mainUID = SharedPreferenceHelper.getValue("mainAccount", "");
								String cookie = MainActivity.instance.getCookie(Long.parseLong(mainUID));
								if (btnStart.getText().toString().equals("开始直播")) {
									UserSpaceToLive sjb = new Gson().fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UserSpaceToLive.class);
									String streamJson = Tools.Network.getSourceCode("https://api.live.bilibili.com/live_stream/v1/StreamList/get_stream_by_roomId?room_id=" + sjb.data.roomid, cookie, "https://link.bilibili.com/p/center/index");
									JsonParser parser = new JsonParser();
									JsonObject rtmp = parser.parse(streamJson).getAsJsonObject().get("data").getAsJsonObject().get("rtmp").getAsJsonObject();
									try {
										Tools.BilibiliTool.startLive(sjb.data.roomid, getIdByName(autoCompleteTextView.getText().toString()), cookie);
									} catch (IOException e) {
										e.printStackTrace();
									}
									(m1 = new TextView(context)).setText(rtmp.get("addr").getAsString());
									(m2 = new TextView(context)).setText(rtmp.get("code").getAsString());
									m1.setOnClickListener(onclick);
									m2.setOnClickListener(onclick);
									((Activity) context).runOnUiThread(new Runnable() {
											@Override
											public void run() {
												ll.addView(m1);
												ll.addView(m2);
												btnStart.setText("关闭直播");
											}
										});
								} else {
									UserSpaceToLive sjb = new Gson().fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UserSpaceToLive.class);
									try {
										Tools.BilibiliTool.stopLive(sjb.data.roomid, cookie);
									} catch (IOException e) {
										e.printStackTrace();
									}
									((Activity) context).runOnUiThread(new Runnable() {
											@Override
											public void run() {
												ll.removeView(m1);
												ll.removeView(m2);
												btnStart.setText("开始直播");
											}
										});
								}	
							}
						});
				}
			});

		btnRename.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					final String name = newName.getText().toString();
					if (name.equals("")) {
						MainActivity.instance.showToast("房间名不能为空");
						return;
					}
					MainActivity.instance.threadPool.execute(new Runnable() {
							@Override
							public void run() {
								String mainUID = SharedPreferenceHelper.getValue("mainAccount", "");
								String cookie = MainActivity.instance.getCookie(Long.parseLong(mainUID));
								UserSpaceToLive sjb = new Gson().fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + mainUID), UserSpaceToLive.class);
								try {
									Tools.BilibiliTool.renameLive(sjb.data.roomid, name, cookie);
								} catch (IOException e) {
									MainActivity.instance.showToast(e.toString());
								}
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

    public String getIdByName(String name) {
        for (LivePartList.ListItemInListItem item : itemInListItem) {
            if (name.equals(item.name)) {
                return item.id;
            }
        }
        return null;
    }
}
