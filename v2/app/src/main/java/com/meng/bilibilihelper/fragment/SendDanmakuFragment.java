package com.meng.bilibilihelper.fragment;

import android.app.*;
import android.os.*;
import android.view.*;

import com.meng.bilibilihelper.*;

import android.widget.*;

import com.meng.bilibilihelper.activity.*;

import android.view.View.*;

import com.meng.bilibilihelper.adapters.ListWithImageSwitchAdapter;
import com.meng.bilibilihelper.javaBean.*;

import java.io.*;

public class SendDanmakuFragment extends Fragment {
    public ListView listview;
    public Button btn;
    public EditText et;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_danmaku, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listview = (ListView) view.findViewById(R.id.send_danmaku_listView);
        btn = (Button) view.findViewById(R.id.send_danmaku_button);
        et = (EditText) view.findViewById(R.id.send_danmaku_editText);

        listview.setAdapter(new ListWithImageSwitchAdapter(MainActivity.instence, MainActivity.instence.loginInfo.loginInfoPeople));
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        ListWithImageSwitchAdapter cda = (ListWithImageSwitchAdapter) listview.getAdapter();

                        for (int i = 0; i < cda.getCount(); ++i) {
                            if (cda.getChecked(i)) {
                                try {
                                    MainActivity.instence.naiFragment.sendDanmakuData(et.getText().toString(), ((LoginInfoPeople) cda.getItem(i)).cookie, MainActivity.instence.naiFragment.getLiveId());
                                } catch (IOException e) {

                                }
                            }
                        }
                    }
                }).start();
            }
        });
    }

}
