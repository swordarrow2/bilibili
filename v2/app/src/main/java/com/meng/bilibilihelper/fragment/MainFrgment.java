package com.meng.bilibilihelper.fragment;

import android.app.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.javaBean.persionInfo.*;
import com.meng.bilibilihelper.javaBean.spaceToLive.*;
import com.meng.bilibilihelper.javaBean.user.*;
import java.io.*;
import java.net.*;
import java.util.*;
import com.meng.bilibilihelper.javaBean.liveAddress.*;

public class MainFrgment extends Fragment {

    public AutoCompleteTextView autoCompleteTextView;
    public PlanePlayerList planePlayerList = null;
	public LinearLayout l1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
	  }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextview);
        autoCompleteTextView.addTextChangedListener(textWatcher);
        Button btn = (Button) view.findViewById(R.id.naiAll);
        Button btn2 = (Button) view.findViewById(R.id.signAll);
		l1 = (LinearLayout)view.findViewById(R.id.info_listLinearLayout_MengNetworkTextview);
        btn.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        new Thread(new Runnable() {
			  @Override
			  public void run() {
				  planePlayerList = new Gson().fromJson(readStringFromNetwork("https://swordarrow2.github.io/configV2.json"), PlanePlayerList.class);

				  getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (planePlayerList.planePlayers.size() == 0) {				  
								Toast.makeText(getActivity(), "飞机佬信息服务器连接失败", Toast.LENGTH_SHORT).show();
							  } else {
								Toast.makeText(getActivity(), "飞机佬信息服务器连接成功", Toast.LENGTH_SHORT).show();
								ArrayList<String> list = new ArrayList<>();
								for (PlanePlayer planePlayer : planePlayerList.planePlayers) {
									list.add(planePlayer.name);
									list.add(String.valueOf(planePlayer.bliveRoom));
								  }
								autoCompleteTextView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, list));   
							  }
						  }
					  });
				}
			}).start();
	  }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.naiAll:
				  new Thread(new Runnable() {

                        @Override
                        public void run() {
                            for (LoginInfoPeople loginInfoPeople : MainActivity.instence.loginInfoPeopleHashMap.values()) {
                                try {
                                    Thread.sleep(1000);
                                    String room = autoCompleteTextView.getText().toString().equals("") ? autoCompleteTextView.getHint().toString() : autoCompleteTextView.getText().toString();
                                    MainActivity.instence.naiFragment.sendDanmakuData(MainActivity.instence.naiFragment.getRandomSentense(), loginInfoPeople.cookie, room);
								  } catch (Exception e) {
                                    e.printStackTrace();
								  }
							  }
						  }
					  }).start();
				  break;
                case R.id.signAll:
				  new Thread(new Runnable() {

                        @Override
                        public void run() {
                            String room = autoCompleteTextView.getText().toString().equals("") ? autoCompleteTextView.getHint().toString() : autoCompleteTextView.getText().toString();
                            for (LoginInfoPeople loginInfoPeople : MainActivity.instence.loginInfoPeopleHashMap.values()) {
                                try {
                                    Thread.sleep(1000);
                                    MainActivity.instence.signFragment.sendSignData(loginInfoPeople.cookie, room);
								  } catch (Exception e) {
                                    e.printStackTrace();
								  }
							  }
						  }
					  }).start();
				  break;
			  }
		  }
	  };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		  }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

		  }

        @Override
        public void afterTextChanged(Editable s) {
			final PlanePlayer p=new PlanePlayer();
			try {
				p.bliveRoom = Integer.parseInt(s.toString()); 
			  } catch (Exception e) {

			  }
            if (planePlayerList != null) {
                for (final PlanePlayer planePlayer : planePlayerList.planePlayers) {
                    if (s.toString().equals(planePlayer.name)) {
						p.bid = planePlayer.bid;
						p.bliveRoom = planePlayer.bliveRoom;
						autoCompleteTextView.setText(String.valueOf(planePlayer.bliveRoom));	
						break; 				
					  }
				  }
				new Thread(new Runnable(){

					  @Override
					  public void run() {
						  try {
							if(p.bid!=0){
							  final BilibiliPersonInfo person = new Gson().fromJson(readStringFromNetwork("https://api.bilibili.com/x/space/acc/info?mid=" + p.bid + "&jsonp=jsonp"), BilibiliPersonInfo.class);
							  getActivity().runOnUiThread(new Runnable(){
									@Override
									public void run() {
										l1.removeAllViews();
										l1.addView(new MengTextview(getActivity(), "屑站ID", person.data.mid));
										l1.addView(new MengTextview(getActivity(), "用户名", person.data.name));
									  }
								  });
								  }
							} catch (Exception e) {

							}  
						  try {if(p.bid!=0){
							  final SpaceToLiveJavaBean sjb = new Gson().fromJson(readStringFromNetwork("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + p.bid), SpaceToLiveJavaBean.class);	
							  getActivity().runOnUiThread(new Runnable(){
									@Override
									public void run() {
										l1.addView(new MengTextview(getActivity(), "直播URL", sjb.data.url));
										l1.addView(new MengTextview(getActivity(), "标题", sjb.data.title));
										l1.addView(new MengTextview(getActivity(), "状态", sjb.data.liveStatus == 1 ?"正在直播": "未直播"));		
									  }
								  });	
								  }
							} catch (Exception e) {

							}	
						  try {
							  final LiveAddress la=new Gson().fromJson(readStringFromNetwork("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + p.bliveRoom + "&quality=4&platform=web"), LiveAddress.class);
							  getActivity().runOnUiThread(new Runnable(){
									@Override
									public void run() {
									  try{
										l1.addView(new MengTextview(getActivity(), "视频地址1", la.data.durl.get(0).url));															
										l1.addView(new MengTextview(getActivity(), "视频地址2", la.data.durl.get(1).url));																	
										l1.addView(new MengTextview(getActivity(), "视频地址3", la.data.durl.get(2).url));																	
										l1.addView(new MengTextview(getActivity(), "视频地址4", la.data.durl.get(3).url));																							
								}catch(Exception e){
								  
								}	  }
								  });		
							} catch (Exception e) {

							}
						}
					}).start();
			  }
		  }
	  };

    public String readStringFromNetwork(final String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            InputStream in = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
			  }	
            return sb.toString();
		  } catch (final Exception e) {
			getActivity().runOnUiThread(new Runnable(){

				  @Override
				  public void run() {
					  Toast.makeText(getActivity(), url + " " + e.toString(), Toast.LENGTH_SHORT).show();
					}
				});
            return "{\"personInfo\":[]}";
		  }
	  }

  }
