package com.meng.grzxv2.javaBean.bilibili.user;

import java.util.ArrayList;

public class BilibiliPersonInfo {
    public int code = 0;
    public String message = "";
    public bilibiliUserInfoData data = new bilibiliUserInfoData();

    public ArrayList<String> toArrayList(){
        ArrayList<String > list=new ArrayList<>();
        list.add("code:"+code);
        list.add("message:"+message);
        list.addAll(data.toArrayList());
        return list;
    }
}
