package com.meng.bilibilihelper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.gson.Gson;
import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.adapters.GiftAdapter;
import com.meng.bilibilihelper.javaBean.liveBag.LiveBag;
 
public class GiftActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int posotion = getIntent().getIntExtra("position", -1);
        if (posotion == -1) {
            finish();
        }
        setContentView(R.layout.list_fragment);
        ListView listView = (ListView) findViewById(R.id.normal_listview);
        LiveBag liveBag = new Gson().fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/xlive/web-room/v1/gift/bag_list?t=" + System.currentTimeMillis(), MainActivity.instence.loginInfo.loginInfoPeople.get(posotion).cookie), LiveBag.class);
        GiftAdapter giftAdapter = new GiftAdapter(this, liveBag.data.list);
        listView.setAdapter(giftAdapter);
    }
}
