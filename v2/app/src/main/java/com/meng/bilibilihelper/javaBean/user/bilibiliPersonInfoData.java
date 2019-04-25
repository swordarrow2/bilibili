package com.meng.bilibilihelper.javaBean.user;

import java.util.ArrayList;

public class bilibiliPersonInfoData {
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
}
