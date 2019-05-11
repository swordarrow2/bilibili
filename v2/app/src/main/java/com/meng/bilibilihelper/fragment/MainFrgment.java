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
import com.meng.bilibilihelper.adapters.PersonInfoAdapter;
import com.meng.bilibilihelper.javaBean.*;

import java.io.*;
import java.net.*;
import java.util.*;

import com.meng.bilibilihelper.libAndHelper.MengTextview;

public class MainFrgment extends Fragment {

    public AutoCompleteTextView autoCompleteTextView;
    public PlanePlayerList planePlayerList = null;
    public LinearLayout l1;
    public RadioButton radioButtonUID;
    public RadioButton radioButtonLiveID;

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
        radioButtonUID = (RadioButton) view.findViewById(R.id.rbBid);
        radioButtonLiveID = (RadioButton) view.findViewById(R.id.rbLiveId);
        l1 = (LinearLayout) view.findViewById(R.id.info_listLinearLayout_MengNetworkTextview);
        btn.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
		planePlayerList=new Gson().fromJson(getFromAssets("list.json"),PlanePlayerList.class);
		PersonInfoAdapter personInfoAdapter=new PersonInfoAdapter(getActivity(), MainActivity.instence.mainFrgment.planePlayerList.planePlayers);
		MainActivity.instence.personInfoFragment.listview.setAdapter(personInfoAdapter);
		ArrayList<String> list = new ArrayList<>();
		for (PlanePlayerList.PlanePlayer planePlayer : planePlayerList.planePlayers) {
		  if(planePlayer.bliveRoom==0)continue;
			list.add(planePlayer.name);
			list.add(String.valueOf(planePlayer.bliveRoom));
		  }
		autoCompleteTextView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, list));
		
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
							PersonInfoAdapter personInfoAdapter=new PersonInfoAdapter(getActivity(), MainActivity.instence.mainFrgment.planePlayerList.planePlayers);
							MainActivity.instence.personInfoFragment.listview.setAdapter(personInfoAdapter);
                            ArrayList<String> list = new ArrayList<>();
                            for (PlanePlayerList.PlanePlayer planePlayer : planePlayerList.planePlayers) {
                                if(planePlayer.bliveRoom==0)continue;
                                if(planePlayer.bid==0)continue;
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
	
	public String getFromAssets(String fileName){
		try {
			InputStreamReader inputReader = new InputStreamReader( getResources().getAssets().open(fileName) );
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line="";
			StringBuilder Result= new StringBuilder();
			while((line = bufReader.readLine()) != null)
			  Result.append(line);
			return Result.toString();
		  } catch (Exception e) {
			e.printStackTrace();
		  }
		return "";
	  }
    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.naiAll:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            for (LoginInfoPeople loginInfoPeople : MainActivity.instence.loginInfo.loginInfoPeople) {
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
                            for (LoginInfoPeople loginInfoPeople : MainActivity.instence.loginInfo.loginInfoPeople) {
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
        public void afterTextChanged(final Editable s) {
            final Gson gson = new Gson();
            if (planePlayerList != null) {
                for (final PlanePlayerList.PlanePlayer planePlayer : planePlayerList.planePlayers) {
                    if (s.toString().equals(planePlayer.name)) {
                        autoCompleteTextView.setText(String.valueOf(planePlayer.bid));
                        break;
                    }
                }
            }
            new Thread(new Runnable(){

				  @Override
				  public void run() {
					try{
						if(s.toString().equals("0")){
							return;
						  }
						if (radioButtonUID.isChecked()) {
							final BilibiliUserInfo person = gson.fromJson(readStringFromNetwork("https://api.bilibili.com/x/space/acc/info?mid=" + s.toString() + "&jsonp=jsonp"), BilibiliUserInfo.class);
							final UserSpaceToLive sjb = gson.fromJson(readStringFromNetwork("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + s.toString()), UserSpaceToLive.class);
							final LiveAddress la = gson.fromJson(readStringFromNetwork("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + sjb.data.roomid + "&quality=4&platform=web"), LiveAddress.class);
							getActivity().runOnUiThread(new Runnable() {
								  @Override
								  public void run() {
									  try {
										  l1.removeAllViews();
										  l1.addView(new MengTextview(getActivity(), "屑站ID", person.data.mid));
										  l1.addView(new MengTextview(getActivity(), "用户名", person.data.name));
										  l1.addView(new MengTextview(getActivity(),"性别",person.data.sex));
										  l1.addView(new MengTextview(getActivity(),"签名",person.data.sign));
										  l1.addView(new MengTextview(getActivity(),"等级",person.data.level));
										  l1.addView(new MengTextview(getActivity(),"生日",person.data.birthday));
										  l1.addView(new MengTextview(getActivity(),"硬币",person.data.coins));
										  l1.addView(new MengTextview(getActivity(),"vip类型",person.data.vip.type));
										  l1.addView(new MengTextview(getActivity(),"vip状态",person.data.vip.status));
										  l1.addView(new MengTextview(getActivity(), "直播URL", sjb.data.url));
										  l1.addView(new MengTextview(getActivity(), "标题", sjb.data.title));
										  l1.addView(new MengTextview(getActivity(), "状态", sjb.data.liveStatus == 1 ? "正在直播" : "未直播"));
										  l1.addView(new MengTextview(getActivity(), "视频地址1", la.data.durl.get(0).url));
										  l1.addView(new MengTextview(getActivity(), "视频地址2", la.data.durl.get(1).url));
										  l1.addView(new MengTextview(getActivity(), "视频地址3", la.data.durl.get(2).url));
										  l1.addView(new MengTextview(getActivity(), "视频地址4", la.data.durl.get(3).url));
										} catch (Exception e) {

										}
									}
								});
						  } else if (radioButtonLiveID.isChecked()) {
							final LiveAddress la = gson.fromJson(readStringFromNetwork("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + s.toString() + "&quality=4&platform=web"), LiveAddress.class);
							getActivity().runOnUiThread(new Runnable() {
								  @Override
								  public void run() {
									  try {
										  l1.removeAllViews();
										  l1.addView(new MengTextview(getActivity(), "视频地址1", la.data.durl.get(0).url));
										  l1.addView(new MengTextview(getActivity(), "视频地址2", la.data.durl.get(1).url));
										  l1.addView(new MengTextview(getActivity(), "视频地址3", la.data.durl.get(2).url));
										  l1.addView(new MengTextview(getActivity(), "视频地址4", la.data.durl.get(3).url));
										} catch (Exception e) {

										}
									}
								});

						  } 
					}catch(Exception e){
					  e.printStackTrace();
					}
					}
				}).start();
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
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getActivity(), url + " " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            return "{\"personInfo\":[]}";
        }
    }

}
