package com.meng.biliv3.libs;

import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import org.java_websocket.client.*;
import org.java_websocket.handshake.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.fragment.*;
import java.nio.channels.*;

public class DanmakuListener extends WebSocketClient {

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

						} catch (NotYetConnectedException e) {
							MainActivity.instance.showToast("连接中断,重新连接....");
							reconnect();
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
			body = new String(data, 16, length - 16);
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
