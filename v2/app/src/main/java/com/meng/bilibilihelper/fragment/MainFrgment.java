package com.meng.bilibilihelper.fragment;

import android.app.*;
import android.os.*;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.google.gson.Gson;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.javaBean.persionInfo.PlanePlayer;
import com.meng.bilibilihelper.javaBean.persionInfo.PlanePlayerList;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainFrgment extends Fragment {

    public AutoCompleteTextView autoCompleteTextView;
    public PlanePlayerList planePlayerList = null;

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
        btn.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                planePlayerList = new Gson().fromJson(readStringFromNetwork("https://github.com/swordarrow2/swordarrow2.github.io/blob/master/configV2.json"), PlanePlayerList.class);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<String> list = new ArrayList<>();
                        for (PlanePlayer planePlayer : planePlayerList.planePlayers) {
                            list.add(planePlayer.name);
                            list.add(String.valueOf(planePlayer.bliveRoom));
                        }
                        autoCompleteTextView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, list));
                        Toast.makeText(getActivity(), "飞机佬信息服务器连接成功", Toast.LENGTH_SHORT).show();
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
            if (planePlayerList != null) {
                for (PlanePlayer planePlayer : planePlayerList.planePlayers) {
                    if (s.toString().equals(planePlayer.name)) {
                        s = new SpannableStringBuilder(String.valueOf(planePlayer.bliveRoom));
                    }
                }
            }
        }
    };

    public String readStringFromNetwork(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            InputStream in = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            return "{\"personInfo\":[]}";
        }
    }

}
