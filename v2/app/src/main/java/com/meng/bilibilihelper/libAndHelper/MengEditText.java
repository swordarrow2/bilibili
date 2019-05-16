package com.meng.bilibilihelper.libAndHelper;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.javaBean.BilibiliUserInfo;
import com.meng.bilibilihelper.javaBean.UserSpaceToLive;
import com.meng.bilibilihelper.javaBean.personInfo.PersonInfo;

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

    public void setAdapter(ArrayAdapter<String> adapter) {
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
                for (PersonInfo pp : MainActivity.instence.mainFrgment.planePlayerList.personInfo) {
                    if (uid.equals(String.valueOf(pp.bid))) {
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
            for (PersonInfo pp : MainActivity.instence.mainFrgment.planePlayerList.personInfo) {
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
            if (MainActivity.instence.mainFrgment.planePlayerList != null) {
                for (final PersonInfo planePlayer : MainActivity.instence.mainFrgment.planePlayerList.personInfo) {
                    if (s.toString().equals(planePlayer.name)) {
                        autoCompleteTextView.setText(String.valueOf(planePlayer.bid));
                        break;
                    }
                }
            }
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (s.toString().equals("0")) {
                            return;
                        }
                        if (radioButtonUID.isChecked()) {
                            final BilibiliUserInfo person = gson.fromJson(MainActivity.instence.getSourceCode("https://api.bilibili.com/x/space/acc/info?mid=" + s.toString() + "&jsonp=jsonp"), BilibiliUserInfo.class);
                            final UserSpaceToLive sjb = gson.fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + s.toString()), UserSpaceToLive.class);
                            JsonParser parser = new JsonParser();
                            JsonObject obj = parser.parse(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + sjb.data.roomid + "&quality=4&platform=web")).getAsJsonObject();
                            final JsonArray ja = obj.get("data").getAsJsonObject().get("durl").getAsJsonArray();
                            ((MainActivity) activity).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MainActivity) activity).mainFrgment.l1.removeAllViews();
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "屑站ID", person.data.mid));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "用户名", person.data.name));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "性别", person.data.sex));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "签名", person.data.sign));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "等级", person.data.level));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "生日", person.data.birthday));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "vip类型", person.data.vip.type));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "vip状态", person.data.vip.status));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "直播URL", sjb.data.url));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "标题", sjb.data.title));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "状态", sjb.data.liveStatus == 1 ? "正在直播" : "未直播"));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "视频地址1", ja.get(0).getAsJsonObject().get("url").getAsString()));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "视频地址2", ja.get(1).getAsJsonObject().get("url").getAsString()));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "视频地址3", ja.get(2).getAsJsonObject().get("url").getAsString()));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "视频地址4", ja.get(3).getAsJsonObject().get("url").getAsString()));
                                }
                            });
                        } else if (radioButtonLiveID.isChecked()) {
                            JsonParser parser = new JsonParser();
                            JsonObject obj = parser.parse(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + s.toString() + "&quality=4&platform=web")).getAsJsonObject();
                            final JsonArray ja = obj.get("data").getAsJsonObject().get("durl").getAsJsonArray();
                            ((MainActivity) activity).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MainActivity) activity).mainFrgment.l1.removeAllViews();
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "视频地址1", ja.get(0).getAsJsonObject().get("url").getAsString()));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "视频地址2", ja.get(1).getAsJsonObject().get("url").getAsString()));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "视频地址3", ja.get(2).getAsJsonObject().get("url").getAsString()));
                                    ((MainActivity) activity).mainFrgment.l1.addView(new MengTextview(activity, "视频地址4", ja.get(3).getAsJsonObject().get("url").getAsString()));
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
