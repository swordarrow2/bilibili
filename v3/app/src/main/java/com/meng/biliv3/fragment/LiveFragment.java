package com.meng.biliv3.fragment;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.media.*;
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
import com.meng.biliv3.libs.*;
import com.universalvideoview.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import org.java_websocket.exceptions.*;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class LiveFragment extends BaseIdFragment implements View.OnClickListener, UniversalVideoView.VideoViewCallback {

	private Button send,editPre,preset,silver,pack,download,milk;
	private EditText et;
	private TextView info;
	private Spinner selectAccount;

	private Bitmap preview;
	private ImageView img;
	private Uri uri;

    View mVideoLayout;
	View mBottomLayout;
	private int cachedHeight;

	//private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
	private int mSeekPosition;
	private UniversalVideoView mVideoView;
    private UniversalMediaController mMediaController;

	private boolean isFullscreen;
	private JsonObject liveInfo;
	private TabHost tab;

	public ArrayList<String> recieved = new ArrayList<>();
	public ArrayAdapter<String> adapter;
	private ListView danmakuList;

	private DanmakuListener danmakuListener;

	public LiveFragment(String type, long liveId) {
		this.type = type;
		id = liveId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		tab = (TabHost) view.findViewById(android.R.id.tabhost);
		tab.setup();
        LayoutInflater layoutInflater=LayoutInflater.from(getActivity()); 
		layoutInflater.inflate(R.layout.live_fragment, tab.getTabContentView()); 
		layoutInflater.inflate(R.layout.live_fragment2, tab.getTabContentView());
		tab.addTab(tab.newTabSpec("tab1").setIndicator("直播" , null).setContent(R.id.live_fragmentLinearLayout_main));
        tab.addTab(tab.newTabSpec("tab2").setIndicator("弹幕" , null).setContent(R.id.live_fragment2RelativeLayout));


		send = (Button) view.findViewById(R.id.live_fragment2Button_send);
		silver = (Button) view.findViewById(R.id.live_fragment2Button_silver);
		pack = (Button) view.findViewById(R.id.live_fragment2Button_pack);
		//editPre = (Button) view.findViewById(R.id.live_fragmentButton_edit_pre);
		preset = (Button) view.findViewById(R.id.live_fragment2Button_preset);
		img = (ImageView) view.findViewById(R.id.live_fragmentImageView);  
		et = (EditText) view.findViewById(R.id.live_fragment2EditText_danmaku);
		info = (TextView) view.findViewById(R.id.live_fragmentTextView_info);
		selectAccount = (Spinner) view.findViewById(R.id.live_fragment2Spinner);
		milk = (Button) view.findViewById(R.id.livefragmentButtonSerialMilk);
		download = (Button) view.findViewById(R.id.livefragmentButtonDownload);
		mVideoLayout = view.findViewById(R.id.videoLayout);
        mBottomLayout = view.findViewById(R.id.live_fragmentLinearLayout_b);
		mVideoView = (UniversalVideoView) view.findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) view.findViewById(R.id.media_controller);
		danmakuList = (ListView) view.findViewById(R.id.livefragmentListView_danmaku);

		adapter = new ArrayAdapter<String>(MainActivity.instance, android.R.layout.simple_list_item_1, recieved);
		danmakuList.setAdapter(adapter);
		try {
			danmakuListener = new DanmakuListener(this, id);
			danmakuListener.connect();
		} catch (URISyntaxException e) {
			MainActivity.instance.showToast("弹幕服务器连接失败:" + e.toString());
		}
		mVideoView.setMediaController(mMediaController);
        setVideoAreaSize();
        mVideoView.setVideoViewCallback(this);
		download.setOnClickListener(this);
		preset.setOnClickListener(this);
		send.setOnClickListener(this);
		silver.setOnClickListener(this);
		pack.setOnClickListener(this);
		//editPre.setOnClickListener(onclick);
		milk.setOnClickListener(this);
		selectAccount.setAdapter(spinnerAccountAdapter);
		img.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1) {
					if (mSeekPosition > 0) {
						mVideoView.seekTo(mSeekPosition);
					}
					mVideoLayout.setVisibility(View.VISIBLE);
					img.setVisibility(View.GONE);
					mVideoView.start();
					mMediaController.setTitle("发发发");
				}
			});
		mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					MainActivity.instance.showToast("播放完成");
					mVideoLayout.setVisibility(View.GONE);
					img.setVisibility(View.VISIBLE);
				}
			});
		img.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1) {
					try {
						saveBitmap(type + id, preview);
						MainActivity.instance.showToast("图片已保存至" + MainActivity.instance.mainDic + type + id + ".png");
					} catch (Exception e) {}
					return true;
				}
			});

		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					JsonParser parser = new JsonParser();
					liveInfo = parser.parse(Tools.Network.httpGet("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + id + "&quality=4&platform=web")).getAsJsonObject();
					if (liveInfo.get("code").getAsInt() == 19002003) {
						MainActivity.instance.showToast("不存在的房间");
						return;
					}
					final JsonArray ja = liveInfo.get("data").getAsJsonObject().get("durl").getAsJsonArray();
					JsonObject liveToMainInfo=null;
					try {
						liveToMainInfo = new JsonParser().parse(Tools.Network.httpGet("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=" + id)).getAsJsonObject().get("data").getAsJsonObject().get("info").getAsJsonObject();
					} catch (Exception e) {
						return;
					}
					long uid=liveToMainInfo.get("uid").getAsLong();
					final String uname=liveToMainInfo.get("uname").getAsString();
					final UidToLiveRoom sjb = new Gson().fromJson(Tools.Network.httpGet("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + uid), UidToLiveRoom.class);
					final byte[] imgbs = PictureCacher.getNetPicture(MainActivity.instance.gson.fromJson(Tools.BilibiliTool.getLiveRoomInfo(uid), UidToLiveRoom.class).data.cover);
					preview = BitmapFactory.decodeByteArray(imgbs, 0, imgbs.length);
					getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								img.setImageBitmap(preview);
								if (sjb.data.liveStatus != 1) {
									info.setText("房间号:" + id + "\n主播:" + uname + "\n未直播");
									mMediaController.setTitle("未直播");
									MainActivity.instance.renameFragment(type + id, uname + "的直播间");
								} else {
									uri = Uri.parse(ja.get(0).getAsJsonObject().get("url").getAsString());
									mVideoView.setVideoURI(uri);
									mVideoView.requestFocus();
									mMediaController.setTitle(sjb.data.title);
									info.setText("房间号:" + id + "\n主播:" + uname + "\n标题:" + sjb.data.title);
									MainActivity.instance.renameFragment(type + id, uname + "的直播间");
									MainActivity.instance.showToast("uri:" + uri);
								}
							}
						});

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

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (!isVisibleToUser && mVideoView != null && mVideoView.isPlaying()) {
			mSeekPosition = mVideoView.getCurrentPosition();
			mVideoView.pause();
		}
		if (isVisibleToUser) {
			try {
				danmakuListener.send(danmakuListener.encode(2, "").data);
			} catch (WebsocketNotConnectedException e) {
				MainActivity.instance.showToast("连接中断,重新连接....");
				danmakuListener.reconnect();
			}
		}
		super.setUserVisibleHint(isVisibleToUser);
	}
	/**
     * 置视频区域大小
     */
    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
				@Override
				public void run() {
					int width = mVideoLayout.getWidth();
					cachedHeight = (int) (width * 405f / 720f);
//                cachedHeight = (int) (width * 3f / 4f);
//                cachedHeight = (int) (width * 9f / 16f);
					ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
					videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
					videoLayoutParams.height = cachedHeight;
					mVideoLayout.setLayoutParams(videoLayoutParams);
					mVideoView.requestFocus();
				}
			});
    }

	@Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);
            mBottomLayout.setVisibility(View.GONE);
        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.cachedHeight;
            mVideoLayout.setLayoutParams(layoutParams);
            mBottomLayout.setVisibility(View.VISIBLE);
        }
		switchTitleBar(!isFullscreen);
    }

	private void switchTitleBar(boolean show) {
        ActionBar supportActionBar = getActivity().getActionBar();
        if (supportActionBar != null) {
            if (show) {
                supportActionBar.show();
            } else {
                supportActionBar.hide();
            }
        }
    }


	@Override
	public void onClick(final View p1) {
		switch (p1.getId()) {
			case R.id.live_fragment2Button_preset:
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
			case R.id.live_fragment2Button_send:
				sendBili((String) selectAccount.getSelectedItem(), SendDanmaku, et.getText().toString());
				break;
			case R.id.live_fragment2Button_pack:
				sendBili((String) selectAccount.getSelectedItem(), Pack, "");
				break;
			case R.id.live_fragment2Button_silver:
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

	@Override
	public void onPause(MediaPlayer mediaPlayer) {
		// TODO: Implement this method
	}

	@Override
	public void onStart(MediaPlayer mediaPlayer) {
		// TODO: Implement this method
	}

	@Override
	public void onBufferingStart(MediaPlayer mediaPlayer) {
		// TODO: Implement this method
	}

	@Override
	public void onBufferingEnd(MediaPlayer mediaPlayer) {
		// TODO: Implement this method
	}
}
