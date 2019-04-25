package com.meng.grzxv2.javaBean.bilibili.user;

import java.util.ArrayList;
import java.util.Collection;

public class Official {
    public int mid = 0;
    public String title = "";
    public String desc = "";

    public ArrayList<String> toArrayList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("-----official-----");
        list.add("mid:" + mid);
        list.add("title:" + title);
        list.add("desc" + desc);
        return list;
    }
}
