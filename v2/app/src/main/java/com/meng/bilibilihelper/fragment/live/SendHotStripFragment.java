package com.meng.bilibilihelper.fragment.live;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.adapters.ListWithImageSwitchAdapter;
import com.meng.bilibilihelper.fragment.BaseFrgment;
import com.meng.bilibilihelper.javaBean.BilibiliMyInfo;
import com.meng.bilibilihelper.javaBean.UserSpaceToLive;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SendHotStripFragment extends BaseFrgment {
    ListView listview;
    Button btnSend;
    EditText etUID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_danmaku, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listview = (ListView) view.findViewById(R.id.send_danmaku_listView);
        btnSend = (Button) view.findViewById(R.id.send_danmaku_button);
        etUID = (EditText) view.findViewById(R.id.send_danmaku_editText);
        etUID.setVisibility(View.GONE);
        listview.setAdapter(new ListWithImageSwitchAdapter(MainActivity.instence, MainActivity.instence.loginInfo.loginInfoPeople));
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        final String uid = MainActivity.instence.mainFrgment.mengEditText.getUId();
                        if (uid.equals("")) {
                            Toast.makeText(getActivity(), "请在主页中输入用户ID而不是直播间ID", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ListWithImageSwitchAdapter cda = (ListWithImageSwitchAdapter) listview.getAdapter();
                        for (int i = 0; i < cda.getCount(); ++i) {
                            if (cda.getChecked(i)) {
                                try {
                                    Gson gson = new Gson();
                                    String cookie = MainActivity.instence.loginInfo.loginInfoPeople.get(i).cookie;
                                    BilibiliMyInfo info = gson.fromJson(MainActivity.instence.getSourceCode("http://api.bilibili.com/x/space/myinfo?jsonp=jsonp", cookie), BilibiliMyInfo.class);
                                    UserSpaceToLive userSpaceToLive = gson.fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + uid), UserSpaceToLive.class);
                                    sendHotStrip(info.data.mid, uid, userSpaceToLive.data.roomid, cookie);
                                    Thread.sleep(50);
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                }).start();
            }
        });
    }

    public void sendHotStrip(long uid, String ruid, int roomID, String cookie) throws IOException {
        Connection connection = Jsoup.connect("http://api.live.bilibili.com/gift/v2/gift/send");
        String csrf = MainActivity.instence.cookieToMap(cookie).get("bili_jct");
        connection.userAgent(MainActivity.instence.userAgent)
                .headers(liveHead)
                .ignoreContentType(true)
                .referrer("https://live.bilibili.com/" + roomID)
                .cookies(MainActivity.instence.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("uid", String.valueOf(uid))
                .data("gift_id", "1")
                .data("ruid", ruid)
                .data("gift_num", "1")
                .data("coin_type", "silver")
                .data("bag_id", "0")
                .data("platform", "pc")
                .data("biz_code", "live")
                .data("biz_id", String.valueOf(roomID))
                .data("rnd", String.valueOf(System.currentTimeMillis() / 1000))
                .data("metadata", "")
                .data("price", "0")
                .data("csrf_token", csrf)
                .data("csrf", csrf)
                .data("visit_id", "");
        Connection.Response response = connection.execute();
        if (response.statusCode() != 200) {
            MainActivity.instence.showToast(String.valueOf(response.statusCode()));
        }
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(response.body()).getAsJsonObject();
        MainActivity.instence.showToast(obj.get("message").getAsString());
    }
}
