package com.meng.grzxv2.javaBean.bilibili.user;

import java.util.ArrayList;

public class bilibiliUserInfoData {
    public long mid = 0;
    public String name = "";
    public String sex = "";
    public String face = "";
    public String sign = "";
    public int rank = 0;
    public int level = 0;
    public int jointime = 0;
    public int moral = 0;
    public int silence = 0;
    public String birthday = "";
    public float coins = 0f;
    public Official official = new Official();
    public Vip vip = new Vip();
    public boolean is_followed = false;
    public String top_photo = "";
    public Theme theme = new Theme();

    public ArrayList<String > toArrayList() {
        ArrayList<String>list=new ArrayList<>();
        list.add("-----data-----");
        list.add("mid:"+mid);
        list.add("name:"+name);
        list.add("sex:"+sex);
        list.add("face"+face);
        list.add("sign:"+sign);
        list.add("rank:"+rank);
        list.add("level:"+level);
        list.add("jointime:"+jointime);
        list.add("moral:"+moral);
        list.add("silence:"+silence);
        list.add("birthday:"+birthday);
        list.add("coins:"+coins);
        list.add("is_followed:"+is_followed);
        list.add("top_photo:"+top_photo);
        list.addAll(official.toArrayList());
        list.addAll(vip.toArrayList());
        list.addAll(theme.toArrayList());
        return list;
    }
}
