package com.meng.grzxv2.javaBean.bilibili.user;

import java.util.ArrayList;
import java.util.Collection;

public class Vip {
    public int type = 0;
    public int status = 0;
    public int theme_type = 0;

    public ArrayList<String> toArrayList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("-----vip-----");
        list.add("type:" + type);
        list.add("status:" + status);
        list.add("theme_type" + theme_type);
        return list;
    }
}
