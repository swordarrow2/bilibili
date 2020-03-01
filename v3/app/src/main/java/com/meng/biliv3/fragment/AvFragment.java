package com.meng.biliv3.fragment;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libAndHelper.*;
import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.xmlpull.v1.*;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import java.util.zip.*;

public class AvFragment extends BaseIdFragment {

	private Button send,editPre,preset,zan,coin1,coin2,favorite,findSender;
	private EditText et;
	private TextView info,danmakuSender;
	private Spinner selectAccount;
	private VideoInfoBean videoInfo;
	private ImageView ivPreview;
	private Bitmap preview;
	private ArrayList<DanmakuBean> danmakuList=null;
	public class DanmakuBean {
		/*	1.29800, 为弹幕播放起始时间 （在视频中出现的时间，单位是秒）

		 第二个参数是弹幕的模式1..3 滚动弹幕 4底端弹幕 5顶端弹幕 6.逆向弹幕 7精准定位 8高级弹幕 

		 第三个参数是字号， 12非常小,16特小,18小,25中,36大,45很大,64特别大 
		 第四个参数是字体的颜色以HTML颜色的十进制为准 
		 第五个参数是Unix格式的时间戳。基准时间为 1970-1-1 08:00:00 
		 第六个参数是弹幕池 0普通池 1字幕池 2特殊池【目前特殊池为高级弹幕专用】 
		 第七个参数是发送者的ID，用于“屏蔽此弹幕的发送者”功能 
		 第八个参数是弹幕在弹幕数据库中rowID 用于“历史弹幕”功能。
		 */
		public int uid=-1;
		public float time;
		public int mode;
		public int fontSize;
		public int color;
		public long timeStamp;
		public int danmakuPool;
		public long userHash;
		public long databaseId;
		public String msg;
	}

	public AvFragment(String type, int liveId) {
		this.type = type;
		id = liveId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.av_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		send = (Button) view.findViewById(R.id.av_fragmentButton_send);
		//editPre = (Button) view.findViewById(R.id.live_fragmentButton_edit_pre);
		preset = (Button) view.findViewById(R.id.av_fragmentButton_preset);
		findSender = (Button) view.findViewById(R.id.avfragmentButtonGetDanmakuSender);
		//findSender.setVisibility(View.GONE);
		zan = (Button) view.findViewById(R.id.av_fragmentButton_zan);
		coin1 = (Button) view.findViewById(R.id.av_fragmentButton_coin1);
		coin2 = (Button) view.findViewById(R.id.av_fragmentButton_coin2);
		favorite = (Button) view.findViewById(R.id.av_fragmentButton_favorite);
		danmakuSender = (TextView) view.findViewById(R.id.av_fragmentTextView_sender);
		et = (EditText) view.findViewById(R.id.av_fragmentEditText_msg);
		ivPreview = (ImageView) view.findViewById(R.id.av_fragmentImageView);  
		info = (TextView) view.findViewById(R.id.av_fragmentTextView_info);
		selectAccount = (Spinner) view.findViewById(R.id.av_fragmentSpinner);
		preset.setOnClickListener(onclick);
		zan.setOnClickListener(onclick);
		coin1.setOnClickListener(onclick);
		coin2.setOnClickListener(onclick);
		favorite.setOnClickListener(onclick);
		send.setOnClickListener(onclick);
		//editPre.setOnClickListener(onclick);
		findSender.setOnClickListener(onclick);
		selectAccount.setAdapter(spinnerAccountAdapter);
		ivPreview.setOnLongClickListener(new OnLongClickListener(){

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
					videoInfo = MainActivity.instance.gson.fromJson(Tools.Network.getSourceCode("http://api.bilibili.com/x/web-interface/view?aid=" + id), VideoInfoBean.class);	
					if (videoInfo.code != 0) {
						MainActivity.instance.showToast(videoInfo.message);
						return;
					}
					getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								info.setText(videoInfo.toString());
								MainActivity.instance.renameFragment(typeAv + id, videoInfo.data.title);
							}
						});
					try {
						Connection.Response response = Jsoup.connect(videoInfo.data.pic).ignoreContentType(true).execute();
						byte[] img = response.bodyAsBytes();
						preview = BitmapFactory.decodeByteArray(img, 0, img.length);
						getActivity().runOnUiThread(new Runnable(){

								@Override
								public void run() {
									ivPreview.setImageBitmap(preview);
								}

							});
					} catch (IOException e) {
						throw new RuntimeException(e.toString());
					}
				}
			});
	}

	private void saveBitmap(String bitName, Bitmap mBitmap) throws Exception {
		File f = new File(Environment.getExternalStorageDirectory() + "/pictures/" + bitName + ".png");
		f.createNewFile();
		FileOutputStream fOut = null;
		fOut = new FileOutputStream(f);
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		fOut.flush();
		fOut.close();
		getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
	}

	private OnClickListener onclick=new OnClickListener(){

		@Override
		public void onClick(final View p1) {
			switch (p1.getId()) {
				case R.id.av_fragmentButton_preset:
					ListView naiSentenseListview = new ListView(getActivity());
					naiSentenseListview.setAdapter(sencencesAdapter);
					naiSentenseListview.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
								sendBili((String) selectAccount.getSelectedItem(), SendJudge, (String)p1.getAdapter().getItem(p3));
							}
						});
					new AlertDialog.Builder(getActivity()).setView(naiSentenseListview).setTitle("选择预设语句").setNegativeButton("返回", null).show();
					break;
				case R.id.av_fragmentButton_send:
					sendBili((String) selectAccount.getSelectedItem(), SendJudge, et.getText().toString());
					break;
				case R.id.av_fragmentButton_zan:
					sendBili((String) selectAccount.getSelectedItem(), Zan, "");
					break;
				case R.id.av_fragmentButton_coin1:
					sendBili((String) selectAccount.getSelectedItem(), Coin1, "");
					break;
				case R.id.av_fragmentButton_coin2:
					sendBili((String) selectAccount.getSelectedItem(), Coin2, "");
					break;
				case R.id.av_fragmentButton_favorite:
					sendBili((String) selectAccount.getSelectedItem(), Favorite, "");
					break;
				case R.id.avfragmentButtonGetDanmakuSender:
					p1.setVisibility(View.GONE);
					MainActivity.instance.showToast("开始连接");
					MainActivity.instance.threadPool.execute(new Runnable(){

							@Override
							public void run() {
								boom2();
							}
						});
					break;
			}
		}
	};

	private void boom2() {
		try {
			danmakuList = new ArrayList<>();
			int cid=videoInfo.data.pages.get(0).cid;
			Connection.Response response = Jsoup.connect("http://comment.bilibili.com/" + cid + ".xml").ignoreContentType(true).execute();
			FileOutputStream out = (new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + cid + ".xml")));
			out.write(response.bodyAsBytes());           
			out.close();
			HashSet<Long> danmakuIdSet=new HashSet<>();
			InputStream is=new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/" + cid + ".xml"));
			XmlPullParser xp = Xml.newPullParser();
			xp.setInput(is, "utf-8");
			int type = xp.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				if (type == XmlPullParser.START_TAG) {
					if ("d".equals(xp.getName())) {  
						long id = Long.parseLong(xp.getAttributeValue(0).split(",")[6], 16);  
						danmakuIdSet.add(id);				
						String[] d=xp.getAttributeValue(0).split(",");
						DanmakuBean db=new DanmakuBean();
						db.time = Float.parseFloat(d[0]);
						db.mode = Integer.parseInt(d[1]);
						db.fontSize = Integer.parseInt(d[2]);
						db.color = Integer.parseInt(d[3]);
						db.timeStamp = Long.parseLong(d[4]);
						db.danmakuPool = Integer.parseInt(d[5]);
						db.userHash = Long.parseLong(d[6], 16);
						db.databaseId = Long.parseLong(d[7]);
						db.msg = xp.nextText();
						danmakuList.add(db);
					}
				}
				type = xp.next();
			}
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}

		for (final DanmakuBean db:danmakuList) {
			if (MainActivity.instance.sanaeConnect.isClosed()) {
				MainActivity.instance.sanaeConnect.reconnect();
			}
			if (db.uid != -1) {
				final int uid=db.uid;
				final String hashS=Long.toHexString(db.userHash);
				final String dan=db.msg;
				MainActivity.instance.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							danmakuSender.setText(danmakuSender.getText() + "\n用户id:" + uid + "  hash:" + hashS + " 弹幕:" + dan);
						}
					});
				continue;
			}
			MainActivity.instance.sanaeConnect.sendHash(db);
			while (db.uid == -1) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
			for (DanmakuBean da:danmakuList) {
				if (da.userHash == db.userHash) {
					da.uid = db.uid;
				}
			}
			final int uid=db.uid;
			final String hashS=Long.toHexString(db.userHash);
			final String dan=db.msg;
			MainActivity.instance.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						danmakuSender.setText(danmakuSender.getText() + "\n用户id:" + uid + "  hash:" + hashS + " 弹幕:" + dan);
					}
				});
		}
		MainActivity.instance.showToast("完成");
	}
	/*
	 private void boom() throws IOException {
	 //AV44286340
	 danmakuList = new ArrayList<>();
	 int cid=videoInfo.data.pages.get(0).cid;
	 Connection.Response response = Jsoup.connect("http://comment.bilibili.com/" + cid + ".xml").ignoreContentType(true).execute();
	 FileOutputStream out = (new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + cid + ".xml")));
	 out.write(response.bodyAsBytes());           
	 out.close();
	 final HashSet<Long> danmakuIdSet=new HashSet<>();
	 try {
	 InputStream is=new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/" + cid + ".xml"));
	 XmlPullParser xp = Xml.newPullParser();
	 xp.setInput(is, "utf-8");
	 int type = xp.getEventType();
	 while (type != XmlPullParser.END_DOCUMENT) {
	 if (type == XmlPullParser.START_TAG) {
	 if ("d".equals(xp.getName())) {  
	 long id = Long.parseLong(xp.getAttributeValue(0).split(",")[6], 16);  
	 danmakuIdSet.add(id);				
	 String[] d=xp.getAttributeValue(0).split(",");
	 DanmakuBean db=new DanmakuBean();
	 db.time = Float.parseFloat(d[0]);
	 db.mode = Integer.parseInt(d[1]);
	 db.fontSize = Integer.parseInt(d[2]);
	 db.color = Integer.parseInt(d[3]);
	 db.timeStamp = Long.parseLong(d[4]);
	 db.danmakuPool = Integer.parseInt(d[5]);
	 db.userHash = Long.parseLong(d[6], 16);
	 db.databaseId = Long.parseLong(d[7]);
	 db.msg = xp.nextText();
	 danmakuList.add(db);
	 }
	 }
	 type = xp.next();
	 }
	 } catch (Exception e) {
	 throw new RuntimeException(e.toString());
	 }
	 //MainActivity.instance.showToast(danmakuIdSet.toString());
	 final CRC32 crc32=new CRC32();
	 final int[] to={0,0};
	 while (danmakuIdSet.size() > 0) {
	 to[1] += 4;
	 if (to[0] % 1000000 == 0) {
	 MainActivity.instance.runOnUiThread(new Runnable(){

	 @Override
	 public void run() {
	 progressbar.setProgress((int)(((float)to[0] * 100) / 400000000));
	 progress.setText("当前位置:" + to[0] + " 剩余:" + danmakuIdSet.size());
	 }
	 });

	 }
	 crc32.update(String.valueOf(to[0]).getBytes());
	 final long value = crc32.getValue();
	 if (danmakuIdSet.contains(value)) {
	 for (final DanmakuBean db:danmakuList) {
	 if (db.userHash == value) {
	 danmakuIdSet.remove(value);
	 db.uid = to[0];
	 MainActivity.instance.runOnUiThread(new Runnable(){

	 @Override
	 public void run() {
	 danmakuSender.setText(danmakuSender.getText() + "\n用户id:" + db.uid + "  hash:" + Long.toHexString(value) + " 弹幕:" + db.msg);
	 }
	 });
	 }
	 }
	 }
	 crc32.reset();
	 ++to[0];
	 }
	 MainActivity.instance.runOnUiThread(new Runnable(){

	 @Override
	 public void run() {
	 danmakuSender.setText(danmakuSender.getText() + "\nsize:" + to[1]);
	 }
	 });
	 }
	 */


//	private void sendFavorite() {
//		final String sel=(String) selectAccount.getSelectedItem();
//		if (sel.equals("每次选择")) {
//			String items[] = new String[MainActivity.instance.loginAccounts.size()];
//			for (int i=0;i < items.length;++i) {
//				items[i] = MainActivity.instance.loginAccounts.get(i).name;
//			}
//			final boolean checkedItems[] = new boolean[items.length];
//			new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle("选择账号").setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//						checkedItems[which] = isChecked;
//					}
//				})
//				.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						for (int i = 0; i < checkedItems.length; i++) {
//							if (checkedItems[i]) {
//								final AccountInfo ai=MainActivity.instance.loginAccounts.get(i);
//								MainActivity.instance.threadPool.execute(new Runnable(){
//
//										@Override
//										public void run() {
//											Tools.BilibiliTool.send(ai.cookie, id);
//										}
//									});
//							}
//						}
//					}
//				}).show();
//		} else if (sel.equals("主账号")) {
//			MainActivity.instance.threadPool.execute(new Runnable(){
//
//					@Override
//					public void run() {
//						Tools.BilibiliTool.sendLike(MainActivity.instance.getAccount(Integer.parseInt(SharedPreferenceHelper.getValue("mainAccount", ""))).cookie, id);
//					}
//				});
//		} else {
//			MainActivity.instance.threadPool.execute(new Runnable(){
//
//					@Override
//					public void run() {
//						Tools.BilibiliTool.sendLike(MainActivity.instance.getAccount(sel).cookie, id);
//					}
//				});
//		}
//	}
}
