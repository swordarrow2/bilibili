package com.meng.biliv3.fragment;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libAndHelper.*;
import java.io.*;
import org.jsoup.*;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class AvFragment extends BaseIdFragment {

	private Button send,editPre,preset,zan,coin1,coin2,favorite,findSender;
	private EditText et;
	private TextView info;
	private Spinner selectAccount;
	private VideoInfoBean videoInfo;
	private ImageView ivPreview;
	private Bitmap preview;

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
		findSender.setVisibility(View.GONE);
		zan = (Button) view.findViewById(R.id.av_fragmentButton_zan);
		coin1 = (Button) view.findViewById(R.id.av_fragmentButton_coin1);
		coin2 = (Button) view.findViewById(R.id.av_fragmentButton_coin2);
		favorite = (Button) view.findViewById(R.id.av_fragmentButton_favorite);	

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
					MainActivity.instance.threadPool.execute(new Runnable(){

							@Override
							public void run() {
								try {
									boom();
								} catch (IOException e) {}
								MainActivity.instance.showToast("bomb");
							}
						});
					break;
			}
		}
	};

	private void boom() throws IOException {
		/*	int cid=videoInfo.data.pages.get(0).cid;
		 Connection.Response response = Jsoup.connect("http://comment.bilibili.com/" + cid + ".xml").ignoreContentType(true).execute();
		 FileOutputStream out = (new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + cid + ".xml")));
		 out.write(response.bodyAsBytes());           
		 out.close();
		 try {
		 InputStream is=new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/" + cid + ".xml"));
		 XmlPullParser xp = Xml.newPullParser();
		 ArrayList<String> list=new ArrayList<>();
		 xp.setInput(is, "utf-8");
		 //获取当前节点的事件类型，通过事件类型的判断，我们可以知道当前节点是什么节点，从而确定我们应该做什么操作
		 //解析是一行一行的解析的，
		 int type = xp.getEventType();
		 //	MainActivity.instance.showToast(type + "");
		 while (type != XmlPullParser.END_DOCUMENT) {//文档结束节点
		 switch (type) {  
		 case XmlPullParser.START_DOCUMENT:  
		 //	persons = new ArrayList<Person>();  
		 break;  
		 case XmlPullParser.START_TAG:  
		 if ("d".equals(xp.getName())) {  
		 String id = xp.getAttributeValue(0);  
		 MainActivity.instance.showToast(id);
		 }
		 break;  
		 case XmlPullParser.END_TAG:  
		 if ("person".equals(parser.getName())) {  
		 persons.add(person);  
		 person = null;  
		 }  
		 break;  
		 }  
		 type = xp.next();
		 }



		 } catch (Exception e) {
		 throw new RuntimeException(e.toString());
		 }*/
	}


	/*

	 //1.获取cid
	 //2.http://comment.bilibili.com/[cid].xml
	 //3
	 <d p="1.29800,1,25,16777215,1535026933,0,40e132dc,4133884241903620">帅气的曲风！！！！</d>

	 p标签里内容，介绍

	 1.29800, 为弹幕播放起始时间 （在视频中出现的时间，单位是秒）

	 第二个参数是弹幕的模式1..3 滚动弹幕 4底端弹幕 5顶端弹幕 6.逆向弹幕 7精准定位 8高级弹幕 

	 第三个参数是字号， 12非常小,16特小,18小,25中,36大,45很大,64特别大 
	 第四个参数是字体的颜色以HTML颜色的十进制为准 
	 第五个参数是Unix格式的时间戳。基准时间为 1970-1-1 08:00:00 
	 第六个参数是弹幕池 0普通池 1字幕池 2特殊池【目前特殊池为高级弹幕专用】 
	 第七个参数是发送者的ID，用于“屏蔽此弹幕的发送者”功能 
	 第八个参数是弹幕在弹幕数据库中rowID 用于“历史弹幕”功能。

	 //4 https://space.bilibili.com/ + uid
	 //http://biliquery.typcn.com/api/user/hash/40e132dc
	 //获取结果为：{"error":0,"data":[{"id":30847042}]}

	 CRC32 crc32 = new CRC32();
	 //crc32.update("30847042".getBytes());
	 long v=Long.parseLong("40e132dc", 16);
	 for (int i=10000000;i < 80000000;++i) {
	 crc32.update(String.valueOf(i).getBytes());
	 if (crc32.getValue() == v) {
	 System.out.println("uid:" + i);
	 break;
	 }
	 if (i % 1000000 == 0) {
	 System.out.println(i);
	 }
	 crc32.reset();
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
