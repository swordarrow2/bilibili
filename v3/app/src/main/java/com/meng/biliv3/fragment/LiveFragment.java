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
import com.meng.biliv3.customView.*;
import com.meng.biliv3.result.*;
import com.meng.biliv3.libs.*;
import com.universalvideoview.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import org.java_websocket.client.*;
import org.java_websocket.exceptions.*;
import org.java_websocket.handshake.*;

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
		danmakuList.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
					Tools.AndroidContent.copyToClipboard(recieved.get(p3));
				}
			});
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
					final UidToRoom sjb = Tools.BilibiliTool.getRoomByUid(uid);
					final byte[] imgbs = NetworkCacher.getNetPicture(Tools.BilibiliTool.getRoomByUid(uid).data.cover);
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
	
	private class DanmakuListener extends WebSocketClient {

		private long roomId;

		private static final int customHeartBeat=2;
		private static final int hot=3;
		private static final int command=5;
		private static final int initJoin=7;
		private static final int serverHeartBeat=8;

		private static final String DANMU_MSG="DANMU_MSG";//弹幕
		private static final String SEND_GIFT="SEND_GIFT";//有人送礼
		private static final String WELCOME="WELCOME";//欢迎加入房间
		private static final String WELCOME_GUARD="WELCOME_GUARD";//欢迎房管加入房间
		private static final String SYS_MSG="SYS_MSG";//系统消息
		private static final String PREPARING="PREPARING";//主播准备中
		private static final String LIVE="LIVE";//直播开始
		private static final String ROOM_BLOCK_MSG="ROOM_BLOCK_MSG";//禁言通知

		private LiveFragment liveFragment;

		public DanmakuListener(LiveFragment lvf, long roomId) throws URISyntaxException {
			super(new URI("wss://broadcastlv.chat.bilibili.com:2245/sub"));
			this.roomId = roomId;
			liveFragment = lvf;
		}

		@Override
		public void onMessage(String p1) {
			// TODO: Implement this method
		}

		@Override
		public void onOpen(ServerHandshake serverHandshake) {
			send(encode(initJoin, String.format(Tools.AndroidContent.readAssetsString("bliveInit"), roomId)).data);
			MainActivity.instance.threadPool.execute(new Runnable(){

					@Override
					public void run() {
						while (true) {
							try {
								send(encode(customHeartBeat, "").data);
								Thread.sleep(30000);
							} catch (InterruptedException e) {

							} catch (Exception e) {

							}
						}
					}
				});
		}

		@Override
		public void onMessage(ByteBuffer bs) {
			byte[] bytes=bs.array();
			int offset=0;
			do{
				DataPackage dp=decode(bytes, offset);
				offset += dp.length;
				switch (dp.op) {
					case hot:
						break;
					case command:
						onCommand(dp);
						break;
					case serverHeartBeat:
						break;
				}
			}while(offset < bytes.length - 1);
		}

		private void onCommand(DataPackage dp) {
			try {
				JsonObject jobj=new JsonParser().parse(dp.body).getAsJsonObject();
				switch (jobj.get("cmd").getAsString()) {
					case DANMU_MSG:
//					{
//						"cmd": "DANMU_MSG",
//						"info": [
//						[ 0, 1, 25, 16777215, 1587168682304, 61431705, 0, "0c0beae3", 0, 0, 0 ],
//						"a",
//						[ 64483321, "妖怪之山的厄神", 0, 0, 0, 10000, 1, "" ],
//						[ 17, "台混", "散落的烛光", 2409909, 16752445, "", 0 ],
//						[ 23, 0, 5805790, ">50000" ],
//						[ "title-220-1", "title-220-1" ],
//						0,
//						0,
//						null,
//						{ "ts": 1587168682, "ct": "28CDA054" },
//						0,
//						0,
//						null,
//						null,
//						0
//						]
//					}
						JsonArray jaar=jobj.get("info").getAsJsonArray();
						String danmakuText=jaar.get(1).getAsString();
						JsonArray jaar2=jaar.get(2).getAsJsonArray();
						String speakerName=jaar2.get(1).getAsString();
						long speakerUid=jaar2.get(0).getAsLong();
						liveFragment.recieved.add(speakerName + ":" + danmakuText);
						break;
					case SEND_GIFT:
						JsonObject giftData=jobj.get("data").getAsJsonObject();
						liveFragment.recieved.add(String.format("%s(%d)赠送了%d个%s", giftData.get("uname").getAsString(), giftData.get("uid").getAsLong(), giftData.get("num").getAsInt(), giftData.get("giftName").getAsString()));
						break;
					case ROOM_BLOCK_MSG:
//					{
//						"cmd": "ROOM_BLOCK_MSG",
//						"uid": "424494698",
//						"uname": "八云橙的幻想",
//						"data": {
//							"uid": 424494698,
//							"uname": "八云橙的幻想",
//							"operator": 2
//						}
//					}
						liveFragment.recieved.add("用户 " + jobj.get("uname").getAsString() + " 已被禁言");
						break;
					case WELCOME:

						break;
					case WELCOME_GUARD:

						break;
					case LIVE:
//					LiveStatues lsl=MainActivity.instance.gson.fromJson(dp.body,LiveStatues.class);
						liveFragment.recieved.add("开始直播");
						break;
					case PREPARING:
//					LiveStatues lsp=MainActivity.instance.gson.fromJson(dp.body,LiveStatues.class);
						liveFragment.recieved.add("直播结束");
						break;
					case SYS_MSG:
						liveFragment.recieved.add(dp.body);
						break;
				}
				MainActivity.instance.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							liveFragment.adapter.notifyDataSetChanged();
						}
					});
			} catch (JsonSyntaxException je) {
				MainActivity.instance.showToast(dp.body);
			}
		}

		@Override
		public void onClose(int i, String s, boolean b) {

		}

		@Override
		public void onError(Exception e) {
			MainActivity.instance.showToast(e.toString());
		}

		private class LiveStatues {

			public String cmd;
			public String roomid;

			public boolean isStartLive() {
				if (!cmd.equals(LIVE) && !cmd.equals(PREPARING)) {
					throw new RuntimeException("unkown cmd");
				}
				return cmd.equals(LIVE);
			}
		}




		public DataPackage encode(int op, String body) {
			return new DataPackage(op, body);
		}

		public DataPackage decode(byte[] pack, int pos) {
			return new DataPackage(pack, pos);
		}

		private class DataPackage {
			public byte[] data;
			private int pos=0;

			public int length;
			public short headLen;
			public short version;
			public int op;
			public int seq;
			public String body="";

			public DataPackage(int opCode, String jsonStr) {
				byte[] jsonByte=jsonStr.getBytes();
				data = new byte[16 + jsonByte.length];
				write(getBytes(length = data.length));
				write(getBytes(headLen = (short)16));
				write(getBytes(version = (short)1));
				write(getBytes(op = opCode));
				write(getBytes(seq = 1));
				write(jsonByte);
			}   

			public DataPackage(byte[] pack, int offset) {
				data = pack;
				pos = offset;
				length = readInt();
				headLen = readShort();
				version = readShort();
				op = readInt();
				seq = readInt();
				body = new String(data, offset + 16, length - 16);
				data = null;
			}

			private void write(byte[] bs) {
				for (int i=0;i < bs.length;++i) {
					data[pos++] = bs[i];
				}
			}

			private byte[] getBytes(int i) {
				byte[] bs=new byte[4];
				bs[0] = (byte) ((i >> 24) & 0xff);
				bs[1] = (byte) ((i >> 16) & 0xff);
				bs[2] = (byte) ((i >> 8) & 0xff);
				bs[3] = (byte) (i & 0xff);
				return bs;	
			}

			private byte[] getBytes(short s) {
				byte[] bs=new byte[2];
				bs[0] = (byte) ((s >> 8) & 0xff);
				bs[1] = (byte) (s & 0xff) ;
				return bs;	
			}
			/*大端模式*/
			public short readShort() {
				return (short) ((data[pos++] & 0xff) << 8 | (data[pos++] & 0xff) << 0);
			}

			public int readInt() {
				return (data[pos++] & 0xff) << 24 | (data[pos++] & 0xff) << 16 | (data[pos++] & 0xff) << 8 | (data[pos++] & 0xff) << 0;
			}
		}
	}
}
