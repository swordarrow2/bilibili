package com.meng.bilibilihelper.fragment.main;
import android.app.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.libAndHelper.*;
import java.io.*;
import java.util.*;

import android.view.View.OnClickListener;

public class LiveFragment extends Fragment {

	private Uri uri;
	private MvideoView videoView;
	private Button preset;
	private EditText et;

	private CustomSentence customSentence;
	private File customSentenseFile;
	private AlertDialog alertDialog;

	private ArrayAdapter<String> adapter;

	public LiveFragment(final int liveId) {
		MainActivity.instence.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					JsonParser parser = new JsonParser();
					JsonObject obj = parser.parse(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + liveId + "&quality=4&platform=web")).getAsJsonObject();
					final JsonArray ja = obj.get("data").getAsJsonObject().get("durl").getAsJsonArray();
					getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								uri = Uri.parse(ja.get(0).getAsJsonObject().get("url").getAsString());
								videoView.setVideoURI(uri);  
								videoView.start();  
								videoView.requestFocus(); 
							}
						});
				}
			});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.live_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		customSentenseFile = new File(Environment.getExternalStorageDirectory() + "/sjf.json");
		if (customSentenseFile.exists()) {
			customSentence = new Gson().fromJson(Tools.FileTool.readString(customSentenseFile), CustomSentence.class);
		} else {
			try {
				customSentenseFile.createNewFile();
			} catch (IOException e) {}
			customSentence = new CustomSentence();
			String[] strings = new String[]{
				"此生无悔入东方,来世愿生幻想乡",
				"红魔地灵夜神雪,永夜风神星莲船",
				"非想天则文花贴,萃梦神灵绯想天",
				"冥界地狱异变起,樱下华胥主谋现",
				"净罪无改渡黄泉,华鸟风月是非辨",
				"境界颠覆入迷途,幻想花开啸风弄",
				"二色花蝶双生缘,前缘未尽今生还",
				"星屑洒落雨霖铃,虹彩彗光银尘耀",
				"无寿迷蝶彼岸归,幻真如画妖如月",
				"永劫夜宵哀伤起,幼社灵中幻似梦",
				"追忆往昔巫女缘,须弥之间冥梦现",
				"仁榀华诞井中天,歌雅风颂心无念"
			};
			customSentence.sent.addAll(Arrays.asList(strings));
			saveConfig();
		}
		preset = (Button) view.findViewById(R.id.live_fragmentButton_preset);
		et = (EditText) view.findViewById(R.id.live_fragmentEditText_danmaku);
		//	uri = Uri.parse("https://txy.live-play.acgvideo.com/live-txy/600467/live_130324472_3085508.flv?wsSecret=177505333bfeb3dce31e18d644b1c164&wsTime=1582289846&trid=9af5d39aefc74ba0ba554f05b66e0201&pt=web&oi=3549062321&order=1&sig=no");
		videoView = (MvideoView) view.findViewById(R.id.live_fragmentVideoView);  
		//videoView.setMediaController(new MediaController(getActivity()));
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, customSentence.sent);
	preset.setOnClickListener(onclick);
		}
	public void saveConfig() {
        try {
			FileOutputStream fos = new FileOutputStream(customSentenseFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "utf-8");
            writer.write(new Gson().toJson(customSentence));
            writer.flush();
            fos.close();
		} catch (IOException e) {
            throw new RuntimeException(customSentenseFile.getAbsolutePath() + " not found");
		}
	}
	OnClickListener onclick=new OnClickListener(){

		@Override
		public void onClick(View p1) {
			switch (p1.getId()) {
				case R.id.live_fragmentButton_preset:
					ListView naiSentenseListview = new ListView(getActivity());
					naiSentenseListview.setAdapter(adapter);
					naiSentenseListview.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
								et.setText((String)p1.getAdapter().getItem(p3));
								alertDialog.dismiss();
							}
						});
					alertDialog = new AlertDialog.Builder(getActivity())
						.setView(naiSentenseListview)
						.setTitle("选择预设语句")
						.setNegativeButton("返回", null).show();
					break;
			}
		}
	};
}
