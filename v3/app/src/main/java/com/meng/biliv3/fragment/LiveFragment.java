package com.meng.biliv3.fragment;

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
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libAndHelper.*;
import java.lang.reflect.*;

import android.view.View.OnClickListener;

public class LiveFragment extends BaseIdFragment {

	private Uri uri;
	private VideoView videoView;
	private Button send,editPre,preset,silver,pack,download,milk;
	private EditText et;
	private TextView info;
	private Spinner selectAccount;

	private JsonObject liveInfo;
	public LiveFragment(String type, long liveId) {
		this.type = type;
		id = liveId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.live_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		send = (Button) view.findViewById(R.id.live_fragmentButton_send);
		silver = (Button) view.findViewById(R.id.live_fragmentButton_silver);
		pack = (Button) view.findViewById(R.id.live_fragmentButton_pack);
		//editPre = (Button) view.findViewById(R.id.live_fragmentButton_edit_pre);
		preset = (Button) view.findViewById(R.id.live_fragmentButton_preset);
		et = (EditText) view.findViewById(R.id.live_fragmentEditText_danmaku);
		videoView = (VideoView) view.findViewById(R.id.live_fragmentVideoView);  
		info = (TextView) view.findViewById(R.id.live_fragmentTextView_info);
		selectAccount = (Spinner) view.findViewById(R.id.live_fragmentSpinner);
		milk = (Button) view.findViewById(R.id.livefragmentButtonSerialMilk);
		download = (Button) view.findViewById(R.id.livefragmentButtonDownload);
		download.setOnClickListener(onclick);
		videoView.setMediaController(new MediaController(getActivity()));
		preset.setOnClickListener(onclick);
		send.setOnClickListener(onclick);
		silver.setOnClickListener(onclick);
		pack.setOnClickListener(onclick);
		//editPre.setOnClickListener(onclick);
		milk.setOnClickListener(onclick);
		selectAccount.setAdapter(spinnerAccountAdapter);
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					JsonParser parser = new JsonParser();
					liveInfo = parser.parse(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + id + "&quality=4&platform=web")).getAsJsonObject();
					if (liveInfo.get("code").getAsInt() == 19002003) {
						MainActivity.instance.showToast("不存在的房间");
						return;
					}
					final JsonArray ja = liveInfo.get("data").getAsJsonObject().get("durl").getAsJsonArray();
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
									MainActivity.instance.renameFragment(typeLive + id, uname + "的直播间");
								}
							});
						return;
					} else {
						getActivity().runOnUiThread(new Runnable(){

								@Override
								public void run() {
									uri = Uri.parse(ja.get(0).getAsJsonObject().get("url").getAsString());
									videoView.setVideoURI(uri);  
									//videoView.start();  
									videoView.requestFocus();
									info.setText("房间号:" + id + "\n主播:" + uname + "\n标题:" + sjb.data.title);
									MainActivity.instance.renameFragment(typeLive + id, uname + "的直播间");
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

	private OnClickListener onclick=new OnClickListener(){

		@Override
		public void onClick(final View p1) {
			switch (p1.getId()) {
				case R.id.live_fragmentButton_preset:
					ListView naiSentenseListview = new ListView(getActivity());
					naiSentenseListview.setAdapter(sencencesAdapter);
					naiSentenseListview.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
								sendBili((String) selectAccount.getSelectedItem(), SendDanmaku, (String)p1.getAdapter().getItem(p3));
							}
						});
					new AlertDialog.Builder(getActivity()).setView(naiSentenseListview).setTitle("选择预设语句").setNegativeButton("返回", null).show();
					break;
				case R.id.live_fragmentButton_send:
					sendBili((String) selectAccount.getSelectedItem(), SendDanmaku, et.getText().toString());
					break;
				case R.id.live_fragmentButton_pack:
					sendBili((String) selectAccount.getSelectedItem(), Pack, "");
					break;
				case R.id.live_fragmentButton_silver:
					sendBili((String) selectAccount.getSelectedItem(), Silver, "");
					break;
				case R.id.livefragmentButtonSerialMilk:
					final SelectMilk sm=new SelectMilk(getActivity(), id);
					new AlertDialog.Builder(getActivity()).setView(sm).setTitle("选择").setPositiveButton("发送",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//MainActivity.instance.showToast(sm.toString());
								MainActivity.instance.threadPool.execute(sm.getSendTask());
								try {  
									Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
									field.setAccessible(true);
									field.set(dialog, true);  
								} catch (Exception e) {  
								}
							}
						}).setNeutralButton("添加",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								sm.add();
								try {  
									Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
									field.setAccessible(true);
									field.set(dialog, false);  
								} catch (Exception e) {  
								}  
							}
						}).setNegativeButton("清空", 
						new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int witch) {
								sm.clear();
								try {  
									Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
									field.setAccessible(true);
									field.set(dialog, false);  
								} catch (Exception e) {  
								}
							}
						}).show();
					break;
					/*	case R.id.livefragmentButtonDownload:
					 // 本地存储路径
					 final JsonArray ja = liveInfo.get("data").getAsJsonObject().get("durl").getAsJsonArray();
					 Uri uri = Uri.parse(ja.get(0).getAsJsonObject().get("url").getAsString());
					 DownloadManager downloadManager=(DownloadManager)getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
					 DownloadManager.Request request=new DownloadManager.Request(uri);
					 long downloadId=downloadManager.enqueue(request);
					 break;*/
			}
		}
	};
}
