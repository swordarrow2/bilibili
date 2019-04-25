package com.meng.bilibilihelper;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import com.meng.bilibilihelper.javaBean.*;

import java.util.*;

import android.view.View.OnClickListener;

public class MainFrgment extends Fragment {

    public final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";

    public EditText editText;

    public MainFrgment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Button btnNaiAll = (Button) view.findViewById(R.id.naiAll);
        Button btnSignAll = (Button) view.findViewById(R.id.signAll);

        editText = (EditText) view.findViewById(R.id.et);

        btnNaiAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        for (LoginInfoPeople l : MainActivity.instence.loginInfoPeopleHashMap.values()) {
                            try {
                                Thread.sleep(1000);
                                MainActivity.instence.sendDanmakuData(MainActivity.instence.strings[new Random().nextInt(MainActivity.instence.strings.length)], l.cookie, editText.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
        btnSignAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        for (LoginInfoPeople l : MainActivity.instence.loginInfoPeopleHashMap.values()) {
                            try {
                                Thread.sleep(1000);
                                MainActivity.instence.sendSignData(l.cookie, editText.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }


}
