package com.meng.bilibilihelper.libAndHelper;

import android.content.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.adapters.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.javaBean.personInfo.*;

public class MengEditText extends LinearLayout {
    private Context activity;

    public AutoCompleteTextView autoCompleteTextView;
    public RadioButton radioButtonUID;
    public RadioButton radioButtonLiveID;

    public MengEditText(Context activity, String title, long summry) {
        this(activity, title, String.valueOf(summry));
    }

    public MengEditText(Context activity, String title, float summry) {
        this(activity, title, String.valueOf(summry));
    }

    public MengEditText(Context activity, String title, String summry) {
        super(activity);
        this.activity = activity;
        LayoutInflater.from(activity).inflate(R.layout.meng_edittext, this);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextview);
        autoCompleteTextView.addTextChangedListener(textWatcher);
        radioButtonUID = (RadioButton) findViewById(R.id.rbBid);
        radioButtonLiveID = (RadioButton) findViewById(R.id.rbLiveId);

    }

    public MengEditText(Context activity, AttributeSet attributeSet) {
        super(activity, attributeSet);
        this.activity = activity;
        LayoutInflater.from(activity).inflate(R.layout.meng_edittext, this);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextview);
        autoCompleteTextView.addTextChangedListener(textWatcher);
        radioButtonUID = (RadioButton) findViewById(R.id.rbBid);
        radioButtonLiveID = (RadioButton) findViewById(R.id.rbLiveId);
    }

    public void setAdapter(NameAdapter adapter) {
        autoCompleteTextView.setAdapter(adapter);
    }

    public void checkUidButton() {
        radioButtonUID.setChecked(true);
    }

    public void setLIDRadioButtomCheck() {
        radioButtonLiveID.setChecked(true);
    }

    public String getLiveId() {
        if (radioButtonLiveID.isChecked()) {
            return autoCompleteTextView.getText().toString();
        } else if (radioButtonUID.isChecked()) {
            if (autoCompleteTextView.getText().toString().equals("")) {
                String uid = SharedPreferenceHelper.getValue("mainAccount", "");
                for (PersonInfo pp : MainActivity.instance.getFragment("Main", MainFragment.class).planePlayerList.personInfo) {
                    if (uid.equals(String.valueOf(pp.bid))) {
                        return String.valueOf(pp.bliveRoom);
                    }
                }
            } else {
                for (PersonInfo pp : MainActivity.instance.getFragment("Main", MainFragment.class).planePlayerList.personInfo) {
                    if (autoCompleteTextView.getText().toString().equals(String.valueOf(pp.bid))) {
                        return String.valueOf(pp.bliveRoom);
                    }
                }
            }
        }
        return "";
    }

    public String getUId() {
        if (radioButtonUID.isChecked()) {
            if (autoCompleteTextView.getText().toString().equals("")) {
                return SharedPreferenceHelper.getValue("mainAccount", "");
            }
            return autoCompleteTextView.getText().toString();
        } else if (radioButtonLiveID.isChecked()) {
            String lid = autoCompleteTextView.getText().toString();
            if (lid.equals("")) {
                return "";
            }
            for (PersonInfo pp : MainActivity.instance.getFragment("Main", MainFragment.class).planePlayerList.personInfo) {
                if (lid.equals(String.valueOf(pp.bliveRoom))) {
                    return String.valueOf(pp.bid);
                }
            }
        }
        return "";
    }

    public void setText(String text) {
        autoCompleteTextView.setText(text);
    }

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
            new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							if (s.toString().equals("0")) {
								return;
							}
							try {
								int bid=Integer.parseInt(s.toString());
							} catch (Exception e) {
								return; 
							}
							if (radioButtonUID.isChecked()) {
								final BilibiliUserInfo person = gson.fromJson(Tools.Network.getSourceCode("https://api.bilibili.com/x/space/acc/info?mid=" + s.toString() + "&jsonp=jsonp"), BilibiliUserInfo.class);
								final UserSpaceToLive sjb = gson.fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + s.toString()), UserSpaceToLive.class);
								JsonParser parser = new JsonParser();
								JsonObject obj = parser.parse(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + sjb.data.roomid + "&quality=4&platform=web")).getAsJsonObject();
								final JsonArray ja = obj.get("data").getAsJsonObject().get("durl").getAsJsonArray();
								((MainActivity) activity).runOnUiThread(new Runnable() {
										@Override
										public void run() {
											MainFragment mf=((MainActivity) activity).getFragment("Main", MainFragment.class);
											mf.l1.removeAllViews();
											mf.l1.addView(new MengTextview(activity, "屑站ID", person.data.mid));
											mf.l1.addView(new MengTextview(activity, "用户名", person.data.name));
											mf.l1.addView(new MengTextview(activity, "性别", person.data.sex));
											mf.l1.addView(new MengTextview(activity, "签名", person.data.sign));
											mf.l1.addView(new MengTextview(activity, "等级", person.data.level));
											mf.l1.addView(new MengTextview(activity, "生日", person.data.birthday));
											mf.l1.addView(new MengTextview(activity, "vip类型", person.data.vip.type));
											mf.l1.addView(new MengTextview(activity, "vip状态", person.data.vip.status));
											mf.l1.addView(new MengTextview(activity, "直播URL", sjb.data.url));
											mf.l1.addView(new MengTextview(activity, "标题", sjb.data.title));
											mf.l1.addView(new MengTextview(activity, "状态", sjb.data.liveStatus == 1 ? "正在直播" : "未直播"));
											mf.l1.addView(new MengTextview(activity, "视频地址1", ja.get(0).getAsJsonObject().get("url").getAsString()));
											mf.l1.addView(new MengTextview(activity, "视频地址2", ja.get(1).getAsJsonObject().get("url").getAsString()));
											mf.l1.addView(new MengTextview(activity, "视频地址3", ja.get(2).getAsJsonObject().get("url").getAsString()));
											mf.l1.addView(new MengTextview(activity, "视频地址4", ja.get(3).getAsJsonObject().get("url").getAsString()));
										}
									});
							} else if (radioButtonLiveID.isChecked()) {
								JsonParser parser = new JsonParser();
								JsonObject obj = parser.parse(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + s.toString() + "&quality=4&platform=web")).getAsJsonObject();
								final JsonArray ja = obj.get("data").getAsJsonObject().get("durl").getAsJsonArray();
								((MainActivity) activity).runOnUiThread(new Runnable() {
										@Override
										public void run() {
											MainFragment mf=((MainActivity) activity).getFragment("Main", MainFragment.class);
											mf.l1.removeAllViews();
											mf.l1.addView(new MengTextview(activity, "视频地址1", ja.get(0).getAsJsonObject().get("url").getAsString()));
											mf.l1.addView(new MengTextview(activity, "视频地址2", ja.get(1).getAsJsonObject().get("url").getAsString()));
											mf.l1.addView(new MengTextview(activity, "视频地址3", ja.get(2).getAsJsonObject().get("url").getAsString()));
											mf.l1.addView(new MengTextview(activity, "视频地址4", ja.get(3).getAsJsonObject().get("url").getAsString()));
										}
									});
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
        }
    };
}
