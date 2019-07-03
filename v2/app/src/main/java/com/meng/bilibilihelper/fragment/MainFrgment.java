package com.meng.bilibilihelper.fragment;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.adapters.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.javaBean.personInfo.*;
import com.meng.bilibilihelper.libAndHelper.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainFrgment extends Fragment {

    public ConfigJavaBean planePlayerList = null;
    public LinearLayout l1;
    public NameAdapter nameAdapter;
    public MengEditText mengEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mengEditText = (MengEditText) view.findViewById(R.id.meng_edittext);
        Button btn = (Button) view.findViewById(R.id.naiAll);
        Button btn2 = (Button) view.findViewById(R.id.signAll);
        l1 = (LinearLayout) view.findViewById(R.id.info_listLinearLayout_MengNetworkTextview);
        btn.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        planePlayerList = new Gson().fromJson(MainActivity.instence.methodsManager.getFromAssets("list.json"), ConfigJavaBean.class);
        PersonInfoAdapter personInfoAdapter = new PersonInfoAdapter(getActivity(), MainActivity.instence.mainFrgment.planePlayerList.personInfo);
        MainActivity.instence.personInfoFragment.listview.setAdapter(personInfoAdapter);
        nameAdapter = new NameAdapter(getActivity(), planePlayerList.personInfo);
        mengEditText.setAdapter(nameAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket client = new Socket("123.207.65.93", Integer.parseInt(SharedPreferenceHelper.getValue("port", "9603")));
                    OutputStream out = client.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(out);
                    dos.writeUTF("getFull");
                    InputStream in = client.getInputStream();
                    DataInputStream dis = new DataInputStream(in);
                    String result = dis.readUTF();
                    planePlayerList = new Gson().fromJson(result, ConfigJavaBean.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (planePlayerList.personInfo.size() == 0) {
                                Toast.makeText(getActivity(), "飞机佬信息服务器连接失败", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "飞机佬信息服务器连接成功", Toast.LENGTH_SHORT).show();
                                PersonInfoAdapter personInfoAdapter = new PersonInfoAdapter(getActivity(), MainActivity.instence.mainFrgment.planePlayerList.personInfo);
                                MainActivity.instence.personInfoFragment.listview.setAdapter(personInfoAdapter);
                                nameAdapter = new NameAdapter(getActivity(), planePlayerList.personInfo);
                            }
                        }
                    });
                } catch (Exception e) {

                }
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
                            for (LoginInfoPeople loginInfoPeople : MainActivity.instence.loginInfo.loginInfoPeople) {
                                try {
                                    Thread.sleep(1000);
                                    MainActivity.instence.naiFragment.sendDanmakuData(MainActivity.instence.naiFragment.getRandomSentense(), loginInfoPeople.cookie, MainActivity.instence.mainFrgment.mengEditText.getLiveId());
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
                            for (LoginInfoPeople loginInfoPeople : MainActivity.instence.loginInfo.loginInfoPeople) {
                                try {
                                    Thread.sleep(1000);
                                    MainActivity.instence.signFragment.sendSignData(loginInfoPeople.cookie);
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

}
